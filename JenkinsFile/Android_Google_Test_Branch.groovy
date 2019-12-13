pipeline{
  agent none
  triggers {
    pollSCM('H/5 * * * *')
  }
  stages{
    stage('Build'){
      agent{node{label('ios')}}
      steps{
        lock('buildResource')
        {
          script{
            try{
              git(branch: 'test', credentialsId: 'lukai@sunmi.com', url: 'https://code.sunmi.com/wbu-app/sunmi-assistant-android.git', poll: true)
              dir('apmanager'){
                git(branch: 'test', credentialsId: 'lukai@sunmi.com', url: 'https://code.sunmi.com/wbu-app/sunmi-assistant-android-ap-manager.git', poll: true)
              }
              sh('''
                export PATH="/usr/local/bin/:$PATH"
                export LC_ALL=en_US.UTF-8
                export LANG=en_US.UTF-8
                export ANDROID_HOME=/Users/admin/Library/Android/sdk
                export ANDROID_NDK_HOME=/Users/admin/Library/Android/sdk/ndk-bundle
                curl http://api.fir.im/apps/latest/5d0067f3ca87a86608c23f57?api_token=01c4d7223e96d2504d4009515f0595de > info.json
                mkdir -p build
                fastlane googleTestEnv
                ''')
              archiveArtifacts(artifacts: 'app/build/outputs/apk/**/app-google-universal-debug.apk', onlyIfSuccessful: true)
              stash(includes: 'app/build/outputs/apk/**/app-google-universal-debug.apk', name: 'apk')
            }catch(e){
              def stageName = 'build'
              if(currentBuild.currentResult == "FAILURE"){
                NotifyBuild(currentBuild.result, stageName)
              }
              currentBuild.result = "FAILURE"
              throw e
            }
          }
        }
      }
    }
    stage('Upload') {
      agent{node{label('ios')}}
      when{
        not{equals(expected:"FAILURE", actual:currentBuild.result)}
      }
      steps{
        script{
          try{
            milestone("${env.BUILD_NUMBER}".toInteger())
            unstash(name: 'apk')
            sh('''
              export PATH="/usr/local/bin/:$PATH"
              export LC_ALL=en_US.UTF-8
              export LANG=en_US.UTF-8
              fir login 01c4d7223e96d2504d4009515f0595de
              fir publish app/build/outputs/apk/google/smtest/app-google-universal-*.apk
              ''')
          }catch(e){
            def stageName = 'release'
            if(currentBuild.currentResult == "FAILURE"){
              NotifyBuild(currentBuild.result, stageName)
            }
            currentBuild.result = "FAILURE"
            throw e
          }
        }
      }
      post{
        success {
          echo "R ${currentBuild.result} C ${currentBuild.currentResult}"
          script{
            def recipient_list = 'lukai@sunmi.com,xiaoxinwu@sunmi.com,hanruifeng@sunmi.com,ningrulin@sunmi.com,yangshijie@sunmi.com,yangjibin@sunmi.com,simayujing@sunmi.com,linianhan@sunmi.com,liuxiaoliang@sunmi.com,chaoheng.nong@sunmi.com,lixuanzhen@sunmi.com,yangzhen@sunmi.com,zhaiyongqing@sunmi.com'
            def changeString = getChangeString()
            emailext(attachLog: false, body: '''Download url:	https://fir.im/61d5<br/>更新内容：<br/>''' + changeString, mimeType: 'text/html', subject: 'Android Google Test Build Ready', to: recipient_list)
          }
        } 
      }
    }
  }
}

@NonCPS
def getChangeString() {
  MAX_MSG_LEN = 100
  def changeString = ""

  echo "Gathering SCM changes"
  def changeLogSets = currentBuild.changeSets
  for (int i = 0; i < changeLogSets.size(); i++) {
    def entries = changeLogSets[i].items
    for (int j = 0; j < entries.length; j++) {
      def entry = entries[j]
      truncated_msg = entry.msg.take(MAX_MSG_LEN)
      changeString += " - ${truncated_msg} [${entry.author}]\n<br/>"
    }
  }

  if (!changeString) {
    changeString = " - No new changes"
  }
  return changeString
}

def NotifyBuild(String buildStatus = 'STARTED', String stage){
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESS'
 
  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at "<a href="${env.BUILD_URL}/console">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>"""
 
  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }

  def recipient_list = 'lukai@sunmi.com,xiaoxinwu@sunmi.com,yangshijie@sunmi.com,yangjibin@sunmi.com,lvsiwen@sunmi.com,ningrulin@sunmi.com,lixuanzhen@sunmi.com,yangzhen@sunmi.com'

  switch(stage){
    case 'build':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android Google Test Branch 构建出错', to: recipient_list)
      break

    case 'deploy':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android Google Test Branch 部署出错', to: recipient_list)
      break

    case 'test':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android Google Test Branch 测试步骤出错', to: recipient_list)
      break

    case 'release':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android Google Test Branch 上传步骤出错', to: recipient_list)
      break
  }
}