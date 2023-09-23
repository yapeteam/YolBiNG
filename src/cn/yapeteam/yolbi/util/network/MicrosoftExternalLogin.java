package cn.yapeteam.yolbi.util.network;

import cn.yapeteam.yolbi.ui.menu.AltLoginScreen;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.network.http.utils.ColUtils;
import cn.yapeteam.yolbi.util.network.http.utils.HttpUtils;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import com.viaversion.viaversion.libs.gson.JsonParser;
import net.minecraft.util.Session;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MicrosoftExternalLogin implements IMinecraft {

    private final AltLoginScreen screen;

    public MicrosoftExternalLogin(AltLoginScreen screen) {
        this.screen = screen;
    }

    public static final String CLIENT_ID;

    private static String toStr(char[] data) {
        StringBuilder str = new StringBuilder();
        for (char c : data) {
            str.append(c);
        }
        return str.toString();
    }

    static {
        char[] data2 = new char[]{'b', 'a', 'e', 'b', '6', '3', '4', '4', '-', '8', '1', '2', '9', '-', '4', '3', '4', '0', '-', 'b', '9', 'a', '7', '-', 'd', '7', '2', 'd', '4', 'e', '8', 'd', '3', '6', 'd', '3'};
        CLIENT_ID = toStr(data2);
    }

    public void login(LoginCallback callback) {
        try {
            String authorize = HttpUtils.buildUrl("https://login.live.com/oauth20_authorize.srf", ColUtils.mapOf(
                    "client_id", CLIENT_ID,
                    "response_type", "code",
                    "redirect_url", "http://127.0.0.1:30828",
                    "scope", "XboxLive.signin offline_access"
            ));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(authorize), null);
            screen.setStatus("Login URL copied to your clipboard.");

            //Let's get the Code
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(30828), 0);
            httpServer.createContext("/", httpExchange -> {
                String message = "Currently received  authcode, you can close this window.";

                httpExchange.sendResponseHeaders(200, message.length());
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.write(message.getBytes(StandardCharsets.UTF_8));
                responseBody.close();

                screen.setStatus("Logging in...");

                String oauth_access_token = JsonParser.parseString( //Parse the Response
                        HttpUtils.readResponse(
                                //Let's get the Token
                                HttpUtils.getEngine().postForm(
                                        "https://login.live.com/oauth20_token.srf", ColUtils.mapOf(
                                                "client_id", CLIENT_ID,
                                                "code", httpExchange.getRequestURI().toString().
                                                        substring(
                                                                httpExchange.getRequestURI().toString().
                                                                        lastIndexOf('=') + 1
                                                        ),
                                                "grant_type", "authorization_code",
                                                "redirect_url", "http://127.0.0.1:30828"
                                        )
                                )
                        )
                ).getAsJsonObject().get("access_token").getAsString();

                System.out.println("OAuthToken");

                //The XBL
                //noinspection HttpUrlsUsage
                JsonObject xbl = HttpUtils.gson().fromJson(HttpUtils.readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://user.auth.xboxlive.com/user/authenticate",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "Properties", ColUtils.mapOf(
                                                        "AuthMethod", "RPS",
                                                        "SiteName", "user.auth.xboxlive.com",
                                                        "RpsTicket", "d=" + oauth_access_token
                                                ),
                                                "RelyingParty", "http://auth.xboxlive.com",
                                                "TokenType", "JWT"
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("XBOX Live");

                String xbl_token = xbl.get("Token").getAsString();

                //The XSTS
                JsonObject xsts = HttpUtils.gson().fromJson(HttpUtils.readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://xsts.auth.xboxlive.com/xsts/authorize",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "Properties", ColUtils.mapOf(
                                                        "SandboxId", "RETAIL",
                                                        "UserTokens", new String[]{xbl_token}
                                                ),
                                                "RelyingParty", "rp://api.minecraftservices.com/",
                                                "TokenType", "JWT"
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("XSTS");

                String xsts_token = xsts.get("Token").getAsString();
                String xsts_uhs = xsts.get("DisplayClaims").getAsJsonObject().get("xui").getAsJsonArray().get(0).getAsJsonObject().get("uhs").getAsString();

                //Login with XBOX
                JsonObject xbox = HttpUtils.gson().fromJson(HttpUtils.readResponse(
                                HttpUtils.getEngine().postJson(
                                        "https://api.minecraftservices.com/authentication/login_with_xbox",
                                        HttpUtils.gson(), ColUtils.mapOf(
                                                "identityToken", String.format("XBL3.0 x=%s;%s", xsts_uhs, xsts_token)
                                        )
                                )
                        ), JsonObject.class
                );

                System.out.println("Login XBOX");

                //Get Minecraft profile!
                JsonObject profile = HttpUtils.gson().fromJson(HttpUtils.readResponse(
                                HttpUtils.getEngine().getJson(
                                        "https://api.minecraftservices.com/minecraft/profile", ColUtils.mapOf(
                                                "Authorization", "Bearer " + xbox.get("access_token").getAsString()
                                        )
                                )
                        ), JsonObject.class
                );
                callback.run(
                        profile.get("name").getAsString(),
                        profile.get("id").getAsString().replace("-", ""),
                        xbox.get("access_token").getAsString(), true
                );
                httpServer.stop(0);
            });
            httpServer.setExecutor(null);
            httpServer.start();
        } catch (Exception e) {
            callback.run(null, null, null, false);
        }
    }

    public void start() throws Exception {
        login((username, uuid, access_token, success) -> {
            if (success) {
                mc.setSession(new Session(username, uuid, access_token, "ms"));
                screen.setStatus("Successfully logged as " + mc.getSession().getUsername());
            } else screen.setStatus("Login failed !");
        });
    }
}