package cn.yapeteam.yolbi.util.network.http.apache;

import cn.yapeteam.yolbi.util.network.http.HttpEngine;
import cn.yapeteam.yolbi.util.network.http.HttpResponse;
import cn.yapeteam.yolbi.util.network.http.utils.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ApacheHttpEngine extends HttpEngine {
    private final CloseableHttpClient client = HttpClientBuilder.create().build();

    @Override
    protected HttpResponse makeRequest(String method, String url, byte[] body, Map<String, String> headers) throws IOException {
        assert !HttpEngine.requiresRequestBody(method) || body != null : "HTTP Method" + method + " requires a body.";

        RequestBuilder builder = RequestBuilder.create(method);
        if (body != null) {

            builder.setEntity(new ByteArrayEntity(body));
        }
        builder.setUri(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }

        try (CloseableHttpResponse resp = client.execute(builder.build())) {
            return getHttpResponse(resp);
        }
    }

    private HttpResponse getHttpResponse(CloseableHttpResponse resp) throws IOException {
        int code = resp.getStatusLine().getStatusCode();
        String message = resp.getStatusLine().getReasonPhrase();
        HttpEntity entity = resp.getEntity();
        byte[] respBody = null;
        if (entity != null) {
            try (InputStream is = entity.getContent()) {
                respBody = IOUtils.toBytes(is);
            }
        }
        return new HttpResponse(code, message, respBody);
    }

    @Override
    public void shutdown() throws IOException {
        client.close();
    }
}
