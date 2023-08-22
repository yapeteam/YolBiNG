package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.RenderEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.util.render.FontUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.client.gui.ScaledResolution;

public class IngameInfo extends Module {

    private final ModeValue<String> font = FontUtil.getFontSetting();
    private final BooleanValue bps = new BooleanValue("BPS", true);
    private final BooleanValue balance = new BooleanValue("Balance", true);

    public IngameInfo() {
        super("Ingame Info", ModuleCategory.VISUAL);
        this.addValues(font, bps, balance);
    }

    @Listener
    public void onRender(RenderEvent event) {
        ScaledResolution sr = new ScaledResolution(mc);

        if (mc.gameSettings.showDebugInfo) return;

        float x = 4;
        float y = sr.getScaledHeight() - 13;

        if (balance.getValue()) {
            FontUtil.drawStringWithShadow(font.getValue(), "Balance : " + YolBi.instance.getBalanceHandler().getBalanceInMS(), x, y, -1);

            y -= 10;
        }

        if (bps.getValue()) {
            double bpt = Math.hypot(mc.thePlayer.posX - mc.thePlayer.lastTickPosX, mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.timerSpeed;
            double bps = bpt * 20;

            double roundedBPS = Math.round(bps * 100) / 100.0;

            FontUtil.drawStringWithShadow(font.getValue(), "BPS : " + roundedBPS, x, y, -1);
        }
    }
}
