package cn.yapeteam.yolbi.util.network.http;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class HttpEngine {
    public static final String FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON = "application/json";

    public final HttpResponse getJson(String url, Map<String, String> headers) throws IOException {
        headers = new HashMap<>(headers);
        headers.put("Content-Type", APPLICATION_JSON);
        headers.put("Accept", APPLICATION_JSON);

        return makeRequest("GET", url, null, headers);
    }

    public final HttpResponse postJson(String url, Gson gson, Map<String, Object> body) throws IOException {
        return postJson(url, gson, body, new HashMap<>());
    }

    public final HttpResponse postJson(String url, Gson gson, Map<String, Object> body, Map<String, String> headers) throws IOException {
        headers = new HashMap<>(headers);
        headers.put("Content-Type", APPLICATION_JSON);
        headers.put("Accept", APPLICATION_JSON);

        return makeRequest("POST", url, gson.toJson(body).getBytes(UTF_8), headers);
    }

    public HttpResponse putJson(String url, Gson gson, Map<String, Object> body) throws IOException {
        return putJson(url, gson, body, new HashMap<>());
    }

    public HttpResponse putJson(String url, Gson gson, Map<String, Object> body, Map<String, String> headers) throws IOException {
        headers = new HashMap<>(headers);
        headers.put("Content-Type", APPLICATION_JSON);

        return makeRequest("PUT", url, gson.toJson(body).getBytes(UTF_8), headers);
    }

    public final HttpResponse postForm(String url, Map<String, String> body) throws IOException {
        return postForm(url, body, new HashMap<>());
    }

    public final HttpResponse postForm(String url, Map<String, String> body, Map<String, String> headers) throws IOException {
        headers = new HashMap<>(headers);
        headers.put("Content-Type", FORM_URL_ENCODED);

        return makeRequest("POST", url, formEncode(body).getBytes(UTF_8), headers);
    }

    protected abstract HttpResponse makeRequest(String method, String url, byte[] body, Map<String, String> headers) throws IOException;

    public abstract void shutdown() throws IOException;

    public static String formEncode(Map<String, String> query) {
        try {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (builder.length() > 0) {
                    builder.append("&");
                }
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                builder.append("=");
                builder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Wot?", e);
        }
    }

    public static boolean requiresRequestBody(String method) {
        return method.equalsIgnoreCase("POST")
               || method.equalsIgnoreCase("PUT")
               || method.equalsIgnoreCase("PATCH")
               || method.equalsIgnoreCase("PROPPATCH")
               || method.equalsIgnoreCase("REPORT");
    }
}
