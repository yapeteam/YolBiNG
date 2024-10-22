package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiChat;

public abstract class HUDModule extends Module {

    public final NumberValue<Double> posX;
    public final NumberValue<Double> posY;

    @Getter
    protected int width, height;

    @Getter
    @Setter
    private boolean holdingMouse;

    @Getter
    protected AlignType alignType;

    public HUDModule(double defaultX, double defaultY, int width, int height, AlignType alignType) {
        posX = new NumberValue<>("Pos X", () -> false, defaultX, 0.0, 1000.0, 0.5);
        posY = new NumberValue<>("Pos Y", () -> false, defaultY, 0.0, 1000.0, 0.5);

        this.width = width;
        this.height = height;

        this.alignType = alignType;

        this.listenType = EventListenType.MANUAL;
        this.startListening();

        this.addValues(posX, posY);
    }

    @Listener
    private void onRender(EventRender2D event) {
        boolean inChat = mc.currentScreen instanceof GuiChat;

        if (this.isEnabled()) {
            renderModule(inChat);
        }
    }

    protected abstract void renderModule(boolean inChat);
}
