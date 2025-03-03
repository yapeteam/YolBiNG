package cn.yapeteam.yolbi.ui.menu;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.guiMultiplayer.GuiMultiplayer;
import cn.yapeteam.yolbi.ui.menu.components.Button;
import cn.yapeteam.yolbi.util.misc.AudioUtil;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import net.minecraft.client.gui.*;

import java.awt.*;
import java.io.IOException;

@Deprecated
public class VestigeMainMenu extends GuiScreen {

    private final Button[] buttons = {new Button("Singleplayer"), new Button("Multiplayer"), new Button("Alt login"), new Button("Settings"), new Button("Quit")};

    private final int textColor = new Color(220, 220, 220).getRGB();

    @Override
    public void initGui() {
        for (Button button : buttons) {
            button.updateState(false);
            button.setAnimationDone(true);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        try {
            super.actionPerformed(button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        AbstractFontRenderer bigFont = YolBi.instance.getFontManager().getProductSans23();

        ScaledResolution sr = new ScaledResolution(mc);

        DrawUtil.renderMainMenuBackground(this, sr);

        int buttonWidth = 80;
        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        double y = sr.getScaledHeight() / 2f - totalHeight * 0.3;

        int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
        int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

        for (Button button : buttons) {
            Gui.drawRect(startX, y, endX, y + buttonHeight, 0x50000000);

            button.updateState(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight);

            if (button.isHovered() || !button.isAnimationDone()) {
                double scale = button.getMult();

                Gui.drawRect(startX, y + buttonHeight - 2, startX + buttonWidth * scale, y + buttonHeight, ColorUtil.buttonHoveredColor);
            }

            String buttonName = button.getName();

            bigFont.drawStringWithShadow(buttonName, sr.getScaledWidth() / 2f - bigFont.getStringWidth(buttonName) / 2, (float) (y + 5), textColor);

            y += buttonHeight;
        }

        //String clientName = Vestige.instance.name;

        //titleFont.drawStringWithShadow(clientName, sr.getScaledWidth() / 2 - titleFont.getStringWidth(clientName) / 2, clientNameY, textColor);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (mouseButton == 0) {
            int buttonWidth = 80;
            int buttonHeight = 20;

            int totalHeight = buttonHeight * buttons.length;

            ScaledResolution sr = new ScaledResolution(mc);

            double y = sr.getScaledHeight() / 2f - totalHeight * 0.3;

            int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
            int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

            for (Button button : buttons) {
                if (mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight) {
                    switch (button.getName()) {
                        case "Singleplayer":
                            mc.displayGuiScreen(new GuiSelectWorld(this));
                            break;
                        case "Multiplayer":
                            mc.displayGuiScreen(new GuiMultiplayer(this));
                            break;
                        case "Alt login":
                            mc.displayGuiScreen(new AltLoginScreen());
                            break;
                        case "Settings":
                            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                            break;
                        case "Quit":
                            mc.shutdown();
                            break;
                    }

                    AudioUtil.buttonClick();
                }

                y += buttonHeight;
            }
        }
    }

}
