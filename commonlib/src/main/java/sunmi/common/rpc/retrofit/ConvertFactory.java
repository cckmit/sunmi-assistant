package sunmi.common.rpc.retrofit;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Description: 请求和响应的转换器
 * Created by bruce on 2019/2/27.
 */
public class ConvertFactory extends Converter.Factory {

    private final Gson gson;

    public static ConvertFactory create() {
        return create(new Gson());
    }

    public static ConvertFactory create(Gson gson) {
        return new ConvertFactory(gson);
    }

    private ConvertFactory(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new RespBodyConverter<>(adapter);
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new ReqBodyConverter<>(gson, adapter);
    }

}