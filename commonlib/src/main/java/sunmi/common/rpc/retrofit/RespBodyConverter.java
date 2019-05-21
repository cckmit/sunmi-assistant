package sunmi.common.rpc.retrofit;

import android.support.annotation.NonNull;

import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import sunmi.common.utils.log.LogCat;

/**
 * Description:response拦截处理
 * Created by bruce on 2019/2/27.
 */
public class RespBodyConverter<T> implements Converter<ResponseBody, T> {

    private final TypeAdapter<T> adapter;

    public RespBodyConverter(TypeAdapter<T> adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        byte[] bytes = value.bytes();
        LogCat.e("RespBodyConverter", new String(bytes, "UTF-8"));
        //解密字符串
        return bytes == null ? null : adapter.fromJson(new String(bytes, "UTF-8"));
    }

}