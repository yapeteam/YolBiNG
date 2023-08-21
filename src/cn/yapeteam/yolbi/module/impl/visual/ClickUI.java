package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

@Getter
public class ClickUI extends Module {

    private final BooleanValue pauseGame = new BooleanValue("PauseGame", true);
    private final BooleanValue blur = new BooleanValue("Blur background", true);
    private final BooleanValue rainbow = new BooleanValue("RainBow", false);
    private final NumberValue<Integer> blurRadius = new NumberValue<>("blurRadius", blur::getValue, 3, 0, 50, 1);

    public ClickUI() {
        super("ClickUI", ModuleCategory.VISUAL);
        setKey(Keyboard.KEY_RSHIFT);
        addValues(pauseGame, blur, rainbow, blurRadius);
    }

    @Getter
    private final ImplScreen screen = new ImplScreen();

    @Override
    protected void onEnable() {
        setEnabled(false);
        mc.displayGuiScreen(screen);
    }
}
