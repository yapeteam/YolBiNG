package cn.yapeteam.yolbi.ui.menu;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.ui.menu
 * don't mind
 * @date 2023/8/22 08:56
 */
public class ConfigMenu extends GuiScreen {
    public static boolean FastRender = true;
    private boolean hoverOnFastRender =false;
    private boolean hoverOnLanguage=false;

    private Color unhoverColor = new Color(255, 255, 255,90);
    private Color hoverColor = new Color(255, 255, 255, 160);
    private Color disableunHoverColor = new Color(0,0,0, 176);
    private Color disableHoverColor = new Color(0,0,0,90);

    @Override
    public void initGui() {
        YolBi.instance.haveGotTheConfig = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        DrawUtil.renderMainMenuBackground(this, sr);
        YolBi.instance.getFontManager().getPingFang72().drawCenteredStringWithShadow("Config",sr.getScaledWidth()/2,50,-1);
        hoverOnFastRender =     50<=mouseX&&mouseX<=sr.getScaledWidth() - 50&&
                sr.getScaledHeight()/3-15<=mouseY&&mouseY<=sr.getScaledHeight()/3+15;

        if (hoverOnFastRender) {
            RenderUtil.drawFastRoundedRect(50,
                    sr.getScaledHeight() / 3 - 15,
                    sr.getScaledWidth() - 50,
                    sr.getScaledHeight() / 3 + 15,
                    4,
                    FastRender?hoverColor.getRGB():disableHoverColor.getRGB()
                    );
        }else {
            RenderUtil.drawFastRoundedRect(50,
                    sr.getScaledHeight() / 3 - 15,
                    sr.getScaledWidth() - 50,
                    sr.getScaledHeight() / 3 + 15,
                    4,
                    FastRender?unhoverColor.getRGB():disableunHoverColor.getRGB()

            );
        }
        YolBi.instance.getFontManager().getPingFang18().drawString(FastRender?"EnableFastRender":"DisableFastRender",60,sr.getScaledHeight()/3-3,
                FastRender?new Color(126, 255, 127):
                        new Color(253, 78, 78));


        hoverOnLanguage =     50<=mouseX&&mouseX<=sr.getScaledWidth() - 50&&
                sr.getScaledHeight()/3-15+40<=mouseY&&mouseY<=sr.getScaledHeight()/3+15+40;


        RenderUtil.drawFastRoundedRect(50,
                sr.getScaledHeight()/3-15 + 40,
                sr.getScaledWidth() - 50,
                sr.getScaledHeight()/3+15 +40,
                4,
                hoverOnLanguage?
                new Color(253, 253, 253, 120).getRGB():
                new Color(253, 253, 253, 87).getRGB());
        YolBi.instance.getFontManager().getPingFang18().drawString("Language Setting",60,sr.getScaledHeight()/3-3+40,-1);





    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hoverOnFastRender){
            FastRender = !FastRender;
        }

    }

}
