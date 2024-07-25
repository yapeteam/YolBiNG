package cn.yapeteam.yolbi.ui.menu;

import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.ui.menu
 * don't mind
 * @date 2023/8/26 21:24
 */
public class InfoGui extends GuiScreen {
    private ArrayList<String> list = new ArrayList<>();

    @Override
    public void initGui() {
        list.clear();
        add("0 0 Velocity in KKCraft");
        add("Bhop in KKCraft");
        add("BowBomb ,but it's can't bypass any anti cheat lol");
        add("ViaMCP");
        add("PacketFix for 1.12.2 or higher");
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution res = new ScaledResolution(mc);

        RenderUtil.drawBloomShadow(0, 0, res.getScaledWidth(), res.getScaledHeight(), 100, new Color(14, 14, 14, 126));
        int posy = 10;
        mc.fontRendererObj.drawString("[+] for add, [-] for delete, [*] for fix:", 10, 5, -1);

        for (String str : list) {
            posy = posy + 10;
            mc.fontRendererObj.drawString(str, 20, posy, -1);
        }
    }

    private void add(String addContain) {
        this.list.add("[+]" + addContain);
    }

    private void del(String delContain) {
        this.list.add("[-]" + delContain);
    }

    private void fix(String fixContain) {
        this.list.add("[*]" + fixContain);
    }

    private void fix(String fixContain, String helper) {
        this.list.add("[*]" + fixContain + " supported " + helper);
    }
}
