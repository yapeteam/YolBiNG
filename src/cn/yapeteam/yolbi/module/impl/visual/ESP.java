package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.Render3DEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.impl.combat.Antibot;

import java.awt.*;

public class ESP extends Module {

    private final NumberValue lineWidth = new NumberValue("Line width", 3.25, 0.5, 4, 0.25);
    private final NumberValue alpha = new NumberValue("Alpha", 0.8, 0.2, 1, 0.05);

    private final BooleanValue renderInvisibles = new BooleanValue("Render invisibles", false);

    private ClientTheme theme;
    private Antibot antibotModule;

    public ESP() {
        super("ESP", ModuleCategory.VISUAL);
        this.addValues(lineWidth, alpha, renderInvisibles);
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (theme == null) {
            theme = Vestige.instance.getModuleManager().getModule(ClientTheme.class);
        }

        if (antibotModule == null) {
            antibotModule = Vestige.instance.getModuleManager().getModule(Antibot.class);
        }

        Color color = new Color(theme.getColor(100));

        RenderUtil.prepareBoxRender((float) lineWidth.getValue(), color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, alpha.getValue());

        RenderManager rm = mc.getRenderManager();
        float partialTicks = event.getPartialTicks();

        mc.theWorld.getLoadedEntityList().stream().filter(entity ->
                entity != mc.thePlayer &&
                (!(entity.isInvisible() || entity.isInvisibleToPlayer(mc.thePlayer)) || renderInvisibles.getValue()) &&
                entity instanceof EntityPlayer && antibotModule.canAttack((EntityPlayer) entity, this)
        ).forEach(entity -> RenderUtil.renderEntityBox(rm, partialTicks, entity));

        RenderUtil.stopBoxRender();
    }

}
