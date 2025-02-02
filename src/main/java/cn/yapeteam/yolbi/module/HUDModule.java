package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.animation.Animation;
import cn.yapeteam.yolbi.util.render.animation.Easing;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiChat;

import java.awt.*;

public abstract class HUDModule extends Module {

    public final NumberValue<Double> posX;
    public final NumberValue<Double> posY;
    private final Animation animation = new Animation(Easing.EASE_OUT_CIRC,500);

    @Getter
    protected int width, height;

    @Getter
    @Setter
    private boolean holdingMouse;

    @Getter
    @Setter
    private boolean hoverMouse;

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
            if (!inChat){
                animation.isNotAndRun(0);
            }else if (this.hoverMouse){
                animation.isNotAndRun(255);
            }else {
                animation.isNotAndRun(0);
            }

            if (alignType == AlignType.LEFT) {
                RenderUtil.drawCornerBox(posX.getValue(), posY.getValue(), posX.getValue() + width, posY.getValue() + height, 2.0, new Color(224, 224, 224, (int) animation.getValue()));
            } else if (alignType == AlignType.RIGHT) {
                RenderUtil.drawCornerBox(
                        event.getScaledresolution().getScaledWidth() - posX.getValue() - width,
                        posY.getValue(),
                        event.getScaledresolution().getScaledWidth() - posX.getValue(),
                        posY.getValue() + height, 2.0, new Color(224, 224, 224, (int) animation.getValue()));

            }

        }
    }

    protected abstract void renderModule(boolean inChat);
}
