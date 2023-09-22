package cn.yapeteam.yolbi.module;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleInfo {
    String name();

    ModuleCategory category();

    int key() default Keyboard.KEY_NONE;
}
