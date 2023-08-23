package cn.yapeteam.yolbi.ui.mainmenu.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.mainmenu.AbstractComponent;
import cn.yapeteam.yolbi.ui.mainmenu.utils.Circle;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.Stencil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * @author TIMER_err
 */
public class Button extends AbstractComponent {
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private String icon;

    @Getter
    @Setter
    private AbstractFontRenderer font;

    @Getter
    private final Runnable action;

    public Button(AbstractComponent parent, Runnable action) {
        super(parent);
        this.action = action;
        YolBi.instance.getEventManager().register(this);
    }

    @Override
    public void init() {
        Label label = new Label(this, text, new Color(0));
        label.setX(getX());
        label.setY(getY());
        label.setWidth(getWidth());
        label.setHeight(10);
        label.setFont(YolBi.instance.getFontManager().getPingFang14());
        getChildComponents().add(label);
    }

    private float COff = 0;

    private Boolean lastHovered = false;

    private Circle circle = null;

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        getChildComponents().forEach(c -> {
            if (c instanceof Label) {
                Label label = (Label) c;
                label.setX(getX());
                label.setY(getY() + COff + getHeight());
            }
        });

        float TOff = isHovering(mouseX, mouseY) ? -4 : 0;
        COff += (TOff - COff) / 15f;

        if (isHovering(mouseX, mouseY) && !lastHovered) {
            lastHovered = true;
            circle = new Circle(getWidth(), 40, () -> lastHovered);
        } else if (!isHovering(mouseX, mouseY))
            lastHovered = false;
        RenderUtil.drawBloomShadow(getX(), getY() + COff, getWidth(), getHeight(), 10, new Color(0));
        RenderUtil.drawFastRoundedRect2(getX(), getY() + COff, getWidth(), getHeight(), 5, new Color(216, 216, 216).getRGB());
        font.drawString(icon, getX() + (getWidth() - font.getStringWidth(icon)) / 2f, getY() + COff + (getHeight() - font.getHeight()) / 2f + 5.5f, 0);
        Stencil.write(false);
        RenderUtil.drawFastRoundedRect2(getX(), getY() + COff, getWidth(), getHeight(), 5, new Color(216, 216, 216).getRGB());
        Stencil.erase(true);
        if (circle != null) {
            circle.runCircle();
            circle.drawCircle(getX() + getWidth() / 2f, getY() + getHeight() / 2f + COff);
            if (circle.isComplete()) circle = null;
        }
        Stencil.dispose();
        if (isHovering(mouseX, mouseY))
            super.drawComponent(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (isHovering(mouseX, mouseY)) action.run();
    }
}
