package cn.yapeteam.yolbi.ui.listedclickui.component.impl;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.ui.Theme;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.ui.listedclickui.component.AbstractComponent;
import cn.yapeteam.yolbi.ui.listedclickui.component.Limitation;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @author TIMER_err
 */
public class KeyBindingButton extends AbstractComponent {
    private final Module module;
    private boolean keyBinding = false;

    public KeyBindingButton(AbstractComponent parent, Module module) {
        super(parent);
        this.module = module;
    }

    @SuppressWarnings("DuplicatedCode")
    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation) {
        AbstractFontRenderer font = Vestige.instance.getFontManager().getPingFang14();
        RenderUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), Theme.MainTheme[1].darker().getRGB());
        int index = 0, all = 0;
        for (AbstractComponent component : getParent().getParent().getChildComponents()) {
            if (component instanceof ModuleButton) {
                ModuleButton moduleButton = (ModuleButton) component;
                boolean should = getParent().getParent().getChildComponents().indexOf(getParent()) > getParent().getParent().getChildComponents().indexOf(moduleButton);
                all++;
                if (should)
                    index++;
                if (moduleButton.isExtended())
                    for (AbstractComponent childComponent : moduleButton.getChildComponents())
                        if (childComponent instanceof ValueButton) {
                            all++;
                            if (should)
                                index++;
                            else if (moduleButton == getParent() && getParent().getChildComponents().indexOf(this) >= getParent().getChildComponents().indexOf(childComponent))
                                index++;
                        }
            }
        }
        Color rainbow = ColorUtil.rainbow(10, (all - 1 - index) * 10, 1, 1, 1);
        String text = keyBinding ? "Listening..." : EnumChatFormatting.WHITE + "Bind: " + EnumChatFormatting.RESET + Keyboard.getKeyName(module.getKey());
        font.drawString(text, getX() + (getWidth() - font.getStringWidth(text)) / 2f, getY() + (getHeight() - font.getStringHeight(text)) / 2f, ImplScreen.rainbow ? rainbow.getRGB() : -1);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        keyBinding = isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) keyBinding = false;
        if (keyBinding) {
            module.setKey(keyCode == 211 ? 0 : keyCode);
            keyBinding = false;
        }
    }
}
