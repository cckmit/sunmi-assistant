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
              git(branch: 'onl', credentialsId: 'lukai@sunmi.com', url: 'https://code.sunmi.com/wbu-app/sunmi-assistant-android.git', poll: true)
              dir('apmanager'){
                git(branch: 'onl', credentialsId: 'lukai@sunmi.com', url: 'https://code.sunmi.com/wbu-app/sunmi-assistant-android-ap-manager.git', poll: true)
              }
              sh('''
                export PATH="/usr/local/bin/:$PATH"
                export LC_ALL=en_US.UTF-8
                export LANG=en_US.UTF-8
                curl http://api.fir.im/apps/latest/5c048efcca87a826b0c07ece?api_token=8abeee66a3604b68f707d9c2753f7fb4 > info.json
                export ANDROID_HOME=/Users/admin/Library/Android/sdk
                export ANDROID_NDK_HOME=/Users/admin/Library/Android/sdk/ndk-bundle
                echo $ANDROID_HOME
                mkdir -p build
                rm -rf apmanager/build/outputs/*
                fastlane googleEnv
                ''') 
              stash(includes: 'app/build/outputs/apk/**/app-google-universal-*.apk', name: 'apk')
            }catch(e){
              def stageName = 'build'
              echo "R ${currentBuild.result} C ${currentBuild.currentResult}"
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
    stage('GooglePlay') {
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
              export ANDROID_HOME=/Users/admin/Library/Android/sdk
              export apk_path=app/build/outputs/apk/google/release/
              mkdir -p googleplay
              rm -rf googleplay/*
              cd $apk_path
              apk=`ls *universal*`
              cd $WORKSPACE
              version=`$ANDROID_HOME/build-tools/28.0.3/aapt dump badging $apk_path$apk | grep versionName | awk '{print $4}' | sed s/versionName=//g | sed s/\\'//g`
              name=`$ANDROID_HOME/build-tools/28.0.3/aapt dump badging $apk_path$apk | grep application: | awk '{print $2}' | sed s/label=//g | sed s/\\'//g`
              icon=`$ANDROID_HOME/build-tools/28.0.3/aapt dump badging $apk_path$apk | grep application: | awk '{print $3}' | sed s/icon=//g | sed s/\\'//g`
              echo name=$name > version.txt
              echo version=$version >> version.txt
              cp version.txt googleplay
			  cp $apk_path$apk googleplay
              rm -rf latest
              ''')
            archiveArtifacts(artifacts: 'googleplay/*', onlyIfSuccessful: true)
          }catch(e){
            def stageName = 'googleplay'
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
            def recipient_list = 'lukai@sunmi.com,xiaoxinwu@sunmi.com,yangshijie@sunmi.com,yangjibin@sunmi.com,lvsiwen@sunmi.com,ningrulin@sunmi.com,hanruifeng@sunmi.com,simayujing@sunmi.com,linianhan@sunmi.com,liuxiaoliang@sunmi.com'
            def changeString = getChangeString()
            def details = """<p>请从以下URL下载： "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p><br/>更新内容：<br/>""" 
            emailext(attachLog: false, body: details + changeString, mimeType: 'text/html', subject: 'Android GooglePlay Build 已加固完成', to: recipient_list)
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

  def recipient_list = 'lukai@sunmi.com,xiaoxinwu@sunmi.com,yangshijie@sunmi.com,yangjibin@sunmi.com,ningrulin@sunmi.com'

  switch(stage){
    case 'build':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android GooglePlay Branch 构建出错', to: recipient_list)
      break

    case 'deploy':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android GooglePlay Branch 部署出错', to: recipient_list)
      break

    case 'test':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android GooglePlay Branch 测试步骤出错', to: recipient_list)
      break

    case 'release':
      emailext(attachLog: false, body: details, mimeType: 'text/html', subject: 'Android GooglePlay Branch 加固步骤出错', to: recipient_list)
      break
  }
}
