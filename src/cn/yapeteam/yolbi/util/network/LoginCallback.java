package cn.yapeteam.yolbi.util.network;

public interface LoginCallback {
    void run(String username, String uuid, String access_token, boolean success);
}