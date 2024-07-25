package cn.yapeteam.yolbi.util.network.http;

import cn.yapeteam.yolbi.util.network.http.utils.JsonUtils;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;

public class HttpResponse {
    public final int code;
    public final String message;
    public final byte[] body;

    public HttpResponse(int code, String message, byte[] body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public <T> T fromJson(Gson gson, Type type) throws IOException {
        assert body != null : "Response has no body?";
        return JsonUtils.parse(gson, new ByteArrayInputStream(body), type);
    }
}
