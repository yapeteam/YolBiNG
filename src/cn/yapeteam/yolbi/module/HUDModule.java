package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiChat;
import cn.yapeteam.yolbi.event.Listener;

public abstract class HUDModule extends Module {

    public final NumberValue posX;
    public final NumberValue posY;

    @Getter
    protected int width, height;

    @Getter
    @Setter
    private boolean holdingMouse;

    @Getter
    protected AlignType alignType;

    public HUDModule(String name, ModuleCategory category, double defaultX, double defaultY, int width, int height, AlignType alignType) {
        super(name, category);

        posX = new NumberValue("Pos X", () -> false, defaultX, 0, 1000, 0.5);
        posY = new NumberValue("Pos Y", () -> false, defaultY, 0, 1000, 0.5);

        this.width = width;
        this.height = height;

        this.alignType = alignType;

        this.listenType = EventListenType.MANUAL;
        this.startListening();

        this.addValues(posX, posY);
    }

    @Listener
    public final void onRender(RenderEvent event) {
        boolean inChat = mc.currentScreen instanceof GuiChat;

        if(this.isEnabled() || inChat) {
            renderModule(inChat);
        }
    }

    protected abstract void renderModule(boolean inChat);
}
