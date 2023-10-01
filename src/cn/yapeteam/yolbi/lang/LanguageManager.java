package cn.yapeteam.yolbi.lang;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.misc.StringUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class LanguageManager implements IMinecraft {
    private final File data = new File(YolBi.instance.getFileSystem().getYolbiDir(), "lang.json");
    private final ArrayList<Lang> languages = new ArrayList<>();
    private Lang current;
    private final Gson gson = new Gson();

    public LanguageManager() {
        try {
            for (JsonElement jsonElement : gson.fromJson(readFromResource("list.json"), JsonArray.class))
                languages.add(new Lang(jsonElement.getAsString().replace(".json", ""), gson.fromJson(readFromResource(jsonElement.getAsString()), JsonObject.class)));
            if (data.exists())
                load();
            else save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCurrent(String name) {
        current = languages.stream().filter(l -> l.getName().equals(name)).findFirst().orElse(current);
    }

    public String translate(String src) {
        String result = current.getMap().get(src);
        return result != null ? result : src;
    }

    public void load() throws IOException {
        setCurrent(gson.fromJson(StringUtil.readString(Files.newInputStream(data.toPath())), JsonObject.class).get("current").getAsString());
    }

    public void save() throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("current", current != null ? current.getName() : "English");
        Files.write(data.toPath(), jsonObject.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String readFromResource(String name) throws IOException {
        return StringUtil.readString(getClass().getResourceAsStream("/assets/minecraft/yolbi/lang/" + name));
    }

    @Getter
    public static class Lang {
        private final String name;
        private final Map<String, String> map;

        public Lang(String name, JsonObject src) {
            this.name = name;
            map = new HashMap<>();
            for (JsonElement map : src.getAsJsonArray("map")) {
                String[] translate = StringUtil.split(map.toString().replace("{", "").replace("}", "").replace("\"", ""), ":");
                this.map.put(translate[0], translate[1]);
            }
        }
    }
}