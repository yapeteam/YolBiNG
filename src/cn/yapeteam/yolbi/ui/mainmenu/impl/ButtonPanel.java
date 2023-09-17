package cn.yapeteam.yolbi.ui.mainmenu.impl;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.ui.guiMultiplayer.GuiMultiplayer;
import cn.yapeteam.yolbi.ui.mainmenu.AbstractComponent;
import cn.yapeteam.yolbi.ui.mainmenu.ImplScreen;
import cn.yapeteam.yolbi.ui.menu.AltLoginScreen;
import cn.yapeteam.yolbi.ui.menu.InfoGui;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.Stencil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

import java.awt.*;

/**
 * @author TIMER_err
 */
public class ButtonPanel extends AbstractComponent {
    @Getter
    private final ImplScreen parentScreen;
    @Getter
    private final float lrSpacing = 12, buttonWidth = 32, buttonSpacing = 9, buttonNumber = 6;

    public ButtonPanel(GuiScreen parent) {
        super(null);
        this.parentScreen = (ImplScreen) parent;
    }

    @Override
    public void init() {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setWidth(lrSpacing + buttonNumber * (buttonWidth + buttonSpacing) - buttonSpacing + lrSpacing);
        setHeight(50);
        setX((sr.getScaledWidth() - getWidth()) / 2f);
        setY((sr.getScaledHeight() - getHeight()) / 2f + 20);
        addButton("h", "SinglePlayer", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiSelectWorld(parentScreen)));
        addButton("i", "Multiplayer", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayer(parentScreen)));
        addButton("j", "Alts", () -> Minecraft.getMinecraft().displayGuiScreen(new AltLoginScreen()));
        addButton("k", "Options", () -> Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(parentScreen, Minecraft.getMinecraft().gameSettings)));
        addButton("?", "Info", () -> { Minecraft.getMinecraft().displayGuiScreen(new InfoGui());
        });
        addButton("l", "Exit", () -> Minecraft.getMinecraft().shutdown());
        super.init();
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        setX((sr.getScaledWidth() - getWidth()) / 2f);
        setY((sr.getScaledHeight() - getHeight()) / 2f + 20);
        getChildComponents().forEach(c -> {
            if (c instanceof Button) {
                Button button = (Button) c;
                button.setX(getX() + lrSpacing + getChildComponents().indexOf(button) * (buttonWidth + buttonSpacing));
                button.setY(getY() + (getHeight() - buttonWidth) / 2f);
            }
        });

        RenderUtil.drawBloomShadow(getX(), getY(), getWidth(), getHeight(), 5, new Color(0));
        RenderUtil.drawFastRoundedRect2(getX(), getY(), getWidth(), getHeight(), 3, -1);
        Stencil.write(false);
        RenderUtil.drawFastRoundedRect2(getX(), getY(), getWidth(), getHeight(), 3, -1);
        Stencil.erase(true);
        super.drawComponent(mouseX, mouseY, partialTicks);
        Stencil.dispose();
    }

    private void addButton(String icon, String text, Runnable action) {
        Button button = new Button(this, action);
        button.setFont(YolBi.instance.getFontManager().getMainIcon36());
        if (!getChildComponents().isEmpty()) {
            AbstractComponent last = getChildComponents().get(getChildComponents().size() - 1);
            button.setX(last.getX() + last.getWidth() + buttonSpacing);
        } else button.setX(getX() + lrSpacing);
        button.setY(getY() + 5);
        button.setWidth(32);
        button.setHeight(32);
        button.setIcon(icon);
        button.setText(text);
        getChildComponents().add(button);
    }
}
