package cn.yapeteam.yolbi.ui.listedclickui.component.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.ui.Theme;
import cn.yapeteam.yolbi.ui.listedclickui.ImplScreen;
import cn.yapeteam.yolbi.ui.listedclickui.component.AbstractComponent;
import cn.yapeteam.yolbi.ui.listedclickui.component.Limitation;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.values.Value;
import lombok.Getter;
import net.minecraft.client.renderer.GlStateManager;

import java.util.stream.Collectors;

/**
 * @author TIMER_err
 */
public class ModuleButton extends AbstractComponent {
    @Getter
    private final Module module;
    @Getter
    private float realY;

    public ModuleButton(AbstractComponent parent, Module module) {
        super(parent);
        this.module = module;
    }

    @Getter
    private boolean extended = false;
    @Getter
    private float extend = 0;

    @Override
    public void init() {
        getChildComponents().clear();
        float y = getY() + getHeight();
        KeyBindingButton keyBindingButton = new KeyBindingButton(this, module);
        keyBindingButton.setX(getX());
        keyBindingButton.setY(y);
        keyBindingButton.setWidth(getWidth());
        keyBindingButton.setHeight(ImplScreen.keyBindHeight);
        getChildComponents().add(keyBindingButton);
        y += ImplScreen.keyBindHeight + ImplScreen.valueSpacing;
        for (Value<?> value : module.getValues()) {
            ValueButton valueButton = new ValueButton(this, value);
            valueButton.setX(getX());
            valueButton.setY(y);
            valueButton.setWidth(getWidth());
            valueButton.setHeight();
            getChildComponents().add(valueButton);
            y += valueButton.getHeight() + ImplScreen.valueSpacing;
        }
        super.init();
    }

    private float lastExpand;

    @Override
    public void update() {
        extend = 0;
        float y = getY() + getHeight();
        for (AbstractComponent component : getChildComponents()) {
            component.setX(getX());
            component.setY(y);
            if (!(component instanceof ValueButton && !((ValueButton) component).getValue().getVisibility().get()))
                y += component.getHeight() + ImplScreen.valueSpacing;
            if (extended && !(component instanceof ValueButton && !((ValueButton) component).getValue().getVisibility().get()))
                extend += component.getHeight() + ImplScreen.valueSpacing;
        }

        int extend = 0;
        for (AbstractComponent component : getChildComponents())
            if (!(component instanceof ValueButton && !((ValueButton) component).getValue().getVisibility().get()))
                extend += component.getHeight() + ImplScreen.valueSpacing;
        if (lastExpand != extend) {
            if (extended)
                getParent().setHeight(getParent().getHeight() + (extend - lastExpand));//更新展开长度
            lastExpand = extend;
        }
        super.update();
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation) {
        if (!(
                getX() + getWidth() < limitation.getX() ||
                getX() > limitation.getX() + limitation.getWidth() ||
                getY() + getHeight() < limitation.getY() ||
                getY() > limitation.getY() + limitation.getHeight()
        )) {
            int index = 0, all = 0;
            for (AbstractComponent component : getParent().getChildComponents())
                if (component instanceof ModuleButton) {
                    ModuleButton moduleButton = (ModuleButton) component;
                    if (getParent().getChildComponents().indexOf(this) > getParent().getChildComponents().indexOf(moduleButton)) {
                        index++;
                        if (moduleButton.isExtended())
                            index += moduleButton.getChildComponents().size();
                    }
                    if (moduleButton.isExtended())
                        all += moduleButton.getChildComponents().size();
                    all++;
                }
            GlStateManager.color(1, 1, 1, 1);
            RenderUtil.drawRect(getX(), getY(), getX() + getWidth(), getY() + getHeight(), module.isEnabled() ? (ImplScreen.rainbow ? ColorUtil.rainbow(10, (all - 1 - index) * 10, 1, 1, 1).getRGB() : Theme.MainTheme[2].getRGB()) : (isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY) && (!((Panel) getParent()).getScreenIn().getCurrentPanel().isHovering(mouseX, mouseY) || ((Panel) getParent()).isCurrent()) ? Theme.MainTheme[0].getRGB() : Theme.MainTheme[1].getRGB()));
            AbstractFontRenderer font = YolBi.instance.getFontManager().getPingFang14();
            AbstractFontRenderer icon = YolBi.instance.getFontManager().getFLUXICON14();
            font.drawString(module.getName(), getX() + 5, getY() + (getHeight() - font.getStringHeight(module.getName())) / 2f + 1, ImplScreen.rainbow && module.isEnabled() ? Theme.MainTheme[4].getRGB() : -1);
            if (getChildComponents().size() > 1) {
                GlStateManager.pushMatrix();
                float x = getX() + getWidth() - icon.getStringWidth("g") - 3;
                float tx = x + icon.getStringWidth("g") / 2f, ty = getY() + (getHeight() - icon.getHeight()) / 2f + 2;
                GlStateManager.translate(tx, ty, 0.0f);
                GlStateManager.rotate(90, 0.0f, 0.0f, 1.0f);
                GlStateManager.translate(-tx, -ty, 0.0f);
                icon.drawString("g", x, getY() + (getHeight() - icon.getHeight()) / 2f, ImplScreen.rainbow && module.isEnabled() ? Theme.MainTheme[4].getRGB() : -1);
                GlStateManager.popMatrix();
            }
        }
        if (extended)
            getChildComponents().stream().filter(c -> !(c instanceof ValueButton && !((ValueButton) c).getValue().getVisibility().get())).collect(Collectors.toList()).forEach(c -> c.drawComponent(mouseX, mouseY, partialTicks, limitation));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (isHovering(getParent().getX(), getParent().getY() + ImplScreen.panelTopHeight, getParent().getWidth(), getParent().getHeight() - ImplScreen.panelTopHeight, mouseX, mouseY))
            if (isHovering(getX(), getY(), getWidth(), getHeight(), mouseX, mouseY)) {
                if (mouseButton == 0) module.setEnabled(!module.isEnabled());
                if (mouseButton == 1 && getChildComponents().size() >= 1) {
                    int extend = 0;
                    for (AbstractComponent component : getChildComponents())
                        if (!(component instanceof ValueButton && !((ValueButton) component).getValue().getVisibility().get()))
                            extend += component.getHeight() + ImplScreen.valueSpacing;
                    extended = !extended;
                    getParent().setHeight(getParent().getHeight() + (extended ? 1 : -1) * Math.min(extend, 100));
                }
            }
        if (extended)
            getChildComponents().stream().filter(c -> !(c instanceof ValueButton && !((ValueButton) c).getValue().getVisibility().get())).collect(Collectors.toList()).forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        if (extended)
            getChildComponents().stream().filter(c -> !(c instanceof ValueButton && !((ValueButton) c).getValue().getVisibility().get())).collect(Collectors.toList()).forEach(c -> c.mouseReleased(mouseX, mouseY, state));
    }

    public void setRealY(float realY) {
        this.realY = realY;
    }
}
