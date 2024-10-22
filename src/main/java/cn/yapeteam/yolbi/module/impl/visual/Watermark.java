package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.AlignType;
import cn.yapeteam.yolbi.module.HUDModule;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.network.ServerUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@ModuleInfo(name = "Watermark", category = ModuleCategory.VISUAL)
public class Watermark extends HUDModule {

    private AbstractFontRenderer productSans;

    private ClientTheme theme;

    private boolean initialised;

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Simple", "Simple", "New", "Outline");

    public Watermark() {
        super(4, 4, 100, 100, AlignType.LEFT);
        this.addValues(mode);
        this.setEnabledSilently(true);
    }

    private void initialise() {
        productSans = YolBi.instance.getFontManager().getProductSans();
        theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
    }

    @Override
    protected void renderModule(boolean inChat) {
        if (!initialised) {
            initialise();
            initialised = true;
        }

        if (mc.gameSettings.showDebugInfo) return;

        switch (mode.getValue()) {
            case "New":
                renderNew();
                break;
            case "Outline":
                renderOutline();
                break;
            case "Simple":
                renderSimple();
                break;
        }
    }

    private void renderNew() {
        String clientName = YolBi.instance.name;
        String formattedClientName = String.valueOf(clientName.charAt(0)) + ChatFormatting.WHITE + clientName.substring(1);

        String watermark = formattedClientName + " " + YolBi.instance.version + " | " + Minecraft.getDebugFPS() + "FPS | " + ServerUtil.getCurrentServer();

        double watermarkWidth = getStringWidth(watermark);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        Gui.drawRect(x, y + 2, x + 2 + (int) watermarkWidth, y + 14.5F, 0x60000000);

        for (float i = x; i < x + 1 + watermarkWidth; i++) {
            Gui.drawRect(i, y, i + 1, y + 2, theme.getColor((int) (i * 10)));
        }

        drawStringWithShadow(watermark, x + 1, y + 4.5F, theme.getColor(0));

        width = (int) (watermarkWidth + 3);
        height = 15;
    }

    private void renderOutline() {
        String clientName = YolBi.instance.name;
        String formattedClientName = String.valueOf(clientName.charAt(0)) + ChatFormatting.WHITE + clientName.substring(1);

        String watermark = formattedClientName + " " + YolBi.instance.version + " | " + Minecraft.getDebugFPS() + "FPS | " + ServerUtil.getCurrentServer();

        double watermarkWidth = getStringWidth(watermark);

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        for (float i = x; i < x + 2 + watermarkWidth; i++) {
            int color = theme.getColor((int) (i * 18));

            Gui.drawRect(i, y, i + 1, y + 2.5F, color);
        }

        DrawUtil.drawGradientSideRect(x - 2, y + 1, x, y + 14.5F, 0x15000000, 0x50000000);
        DrawUtil.drawGradientSideRect(x + 3 + (int) watermarkWidth, y + 1, x + 5 + (int) watermarkWidth, y + 14.5F, 0x50000000, 0x15000000);

        DrawUtil.drawGradientVerticalRect(x, y + 14.5F, x + 3 + (int) watermarkWidth, y + 16.5F, 0x50000000, 0x15000000);

        drawStringWithShadow(watermark, x + 1, y + 5, theme.getColor(0));

        width = (int) (watermarkWidth + 3);
        height = 17;
    }

    private void renderSimple() {
        String clientName = YolBi.instance.name;
        //String formattedClientName = String.valueOf(clientName.charAt(0)) + ChatFormatting.WHITE + clientName.substring(1, clientName.length());

        String watermark = clientName + " " + ChatFormatting.WHITE + YolBi.instance.version;

        float x = posX.getValue().floatValue();
        float y = posY.getValue().floatValue();

        drawStringWithShadow(watermark, x, y, theme.getColor(0));

        width = (int) (getStringWidth(watermark) + 2);
        height = 12;
    }

    private void drawStringWithShadow(String text, float x, float y, int color) {
        switch (mode.getValue()) {
            case "Simple":
                mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
                break;
            case "New":
            case "Outline":
                productSans.drawStringWithShadow(text, x, y, color);
                break;
        }
    }

    private double getStringWidth(String s) {
        switch (mode.getValue()) {
            case "Simple":
                return mc.fontRendererObj.getStringWidth(s);
            case "New":
            case "Outline":
                return productSans.getStringWidth(s);
        }

        return mc.fontRendererObj.getStringWidth(s);
    }

}
