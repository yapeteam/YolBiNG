package cn.yapeteam.yolbi.ui.mainmenu;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.ui.mainmenu.impl.ButtonPanel;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author TIMER_err
 */
public class ImplScreen extends GuiScreen {
    @Getter
    private final ArrayList<AbstractComponent> components = new ArrayList<>();
    private boolean init = false;

    @Override
    public void initGui() {
        if (!init) {
            components.add(new ButtonPanel(this));
            components.forEach(AbstractComponent::init);
            init = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        RenderUtil.drawRect2(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(31, 31, 31).getRGB());
        AbstractFontRenderer font = YolBi.instance.getFontManager().getLightBeach72();
        String text = "YolBi";
        font.drawString(text, (sr.getScaledWidth() - font.getStringWidth(text)) / 2f, (sr.getScaledHeight() / 2f - font.getHeight()) / 2f, new Color(5, 134, 105));
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        AbstractFontRenderer font2 = YolBi.instance.getFontManager().getPingFang18();
        font2.drawString(time, (sr.getScaledWidth() - font2.getStringWidth(text)) / 2f, (sr.getScaledHeight() / 2f - font.getHeight()) / 2f + 30, new Color(158, 158, 158));
        components.forEach(c -> c.drawComponent(mouseX, mouseY, partialTicks));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        components.forEach(m -> m.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        components.forEach(m -> m.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}
