package cn.yapeteam.yolbi.ui.menu;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.menu.components.Button;
import cn.yapeteam.yolbi.util.misc.AudioUtil;
import cn.yapeteam.yolbi.util.network.MicrosoftExternalLogin;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class AltLoginScreen extends GuiScreen {

    private GuiTextField email;
    private GuiPasswordField password;

    private final Button[] buttons = {
            new Button("Login"),
            new Button("Import email:pass"),
            new Button("Copy session"),
            new Button("Login from browser"),
            new Button("Back")
    };

    @Getter
    @Setter
    private String status;

    private AbstractFontRenderer font;

    private final int textColor = new Color(220, 220, 220).getRGB();

    @Override
    public void initGui() {
        ScaledResolution sr = new ScaledResolution(mc);

        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        int y = Math.max(sr.getScaledHeight() / 2 - totalHeight / 2 - 50, 75);

        email = new GuiTextField(0, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y, 160, 20);
        password = new GuiPasswordField(1, mc.fontRendererObj, sr.getScaledWidth() / 2 - 80, y + 30, 160, 20);

        font = YolBi.instance.getFontManager().getProductSans();

        for(Button button : buttons) {
            button.updateState(false);
            button.setAnimationDone(true);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        ScaledResolution sr = new ScaledResolution(mc);

        DrawUtil.renderMainMenuBackground(this, sr);

        email.drawTextBox();
        password.drawTextBox();

        // y 240

        int buttonWidth = 120;
        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        double y = Math.max(sr.getScaledHeight() / 2f - totalHeight * 0.2, 140);

        double titleY = Math.max(sr.getScaledHeight() / 2 - totalHeight / 2 - 110, 20);

        String altLogin = "Alt login";

        font.drawStringWithShadow(altLogin, (float) (sr.getScaledWidth() / 2 - font.getStringWidth(altLogin) / 2), (float) titleY, -1);
        font.drawStringWithShadow(status, (float) (sr.getScaledWidth() / 2 - font.getStringWidth(status) / 2), (float) (titleY + 25), -1);

        int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
        int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

        for(Button button : buttons) {
            Gui.drawRect(startX, y, endX, y + buttonHeight, 0x50000000);

            button.updateState(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight);

            if(button.isHovered() || !button.isAnimationDone()) {
                double scale = button.getMult();

                Gui.drawRect(startX, y, startX + buttonWidth * scale, y + buttonHeight, ColorUtil.buttonHoveredColor);
            }

            String buttonName = button.getName();

            font.drawStringWithShadow(buttonName, (float) (sr.getScaledWidth() / 2 - font.getStringWidth(buttonName) / 2), (float) (y + 6), textColor);

            y += buttonHeight;
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        try {
            super.keyTyped(typedChar, keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        email.textboxKeyTyped(typedChar, keyCode);
        password.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        try {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        } catch (IOException e) {
            e.printStackTrace();
        }

        email.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);

        ScaledResolution sr = new ScaledResolution(mc);

        int buttonWidth = 120;
        int buttonHeight = 20;

        int totalHeight = buttonHeight * buttons.length;

        double y = Math.max(sr.getScaledHeight() / 2 - totalHeight * 0.2, 140);

        int startX = sr.getScaledWidth() / 2 - buttonWidth / 2;
        int endX = sr.getScaledWidth() / 2 + buttonWidth / 2;

        for(Button button : buttons) {
            if(mouseX > startX && mouseX < endX && mouseY > y && mouseY < y + buttonHeight) {
                switch (button.getName()) {
                    case "Login":
                        break;
                    case "Import email:pass":
                        try {
                            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                            if (data.contains(":")) {
                                String[] credentials = data.split(":");
                                email.setText(credentials[0]);
                                password.setText(credentials[1]);
                            }
                        } catch (UnsupportedFlavorException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "Copy session":
                        String session = mc.getSession().getUsername() + ":" + mc.getSession().getPlayerID() + ":" + mc.getSession().getToken();

                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(new StringSelection(session), null);

                        status = "Copied session";
                        break;
                    case "Login from browser":
                        new Thread(() -> {
                            try {
                                new MicrosoftExternalLogin(this).start();
                            } catch (Exception e) {
                                e.printStackTrace();

                                status = "Login failed !";
                            }
                        }).start();
                        break;
                    case "Back":
                        mc.displayGuiScreen(YolBi.instance.getMainMenu());
                        break;
                }

                AudioUtil.buttonClick();
            }

            y += buttonHeight;
        }
    }

}
