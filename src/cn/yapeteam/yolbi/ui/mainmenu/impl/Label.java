package cn.yapeteam.yolbi.ui.mainmenu.impl;

import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.mainmenu.AbstractComponent;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * @author TIMER_err
 */
public class Label extends AbstractComponent {
    @Getter
    @Setter
    private AbstractFontRenderer font;
    @Getter
    @Setter
    private String text;
    @Getter
    @Setter
    private Color color;

    public Label(AbstractComponent parent, String text, Color color) {
        super(parent);
        this.text = text;
        this.color = color;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        font.drawString(text, getX() + (getWidth() - font.getStringWidth(text)) / 2f, getY() + (getHeight() - font.getHeight()) / 2f, color.getRGB());
    }
}
