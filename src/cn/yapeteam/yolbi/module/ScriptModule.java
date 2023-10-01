package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.script.Script;
import lombok.Getter;

@Getter
public class ScriptModule extends Module {
    private final Script script;

    public ScriptModule(String source) {
        try {
            this.script = new Script(source);
        } catch (NoSuchFieldException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
