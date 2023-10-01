package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.render.RenderUtil;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.player
 * don't mind
 * @date 2023/9/29 22:58
 */

@ModuleInfo(name = "PrePos", category = ModuleCategory.PLAYER)
public class PrePos extends Module {
    private double actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ;
    private float ftick=0;
    public PrePos(){

    }
    @Listener
    public void onMotionEvent(EventMotion event){
        lastActualX = event.getX()+mc.thePlayer.motionX*ftick/10*3;
        lastActualY = event.getY()+mc.thePlayer.motionY;
        lastActualZ = event.getZ()+mc.thePlayer.motionZ*ftick/10*3;
        actualX = event.getX()+mc.thePlayer.motionX*ftick/10*3;
        actualZ = event.getZ()+mc.thePlayer.motionZ*ftick/10*3;
        actualY = event.getY()+mc.thePlayer.motionY;

    }
    @Listener
    public void onTick(EventTick event){
        ftick=ftick+0.1f;
        if (mc.thePlayer.motionX==0&&mc.thePlayer.motionY==0&&mc.thePlayer.motionZ==0){
            ftick=1;
        }
        if (ftick>=20) ftick=20;
    }


    @Listener
    public void onRender3D(EventRender3D event){
        RenderUtil.prepareBoxRender(3.25F, 1F, 1F, 1F, 0.8F);
        RenderUtil.renderEntityBox(mc.getRenderManager(),event.getPartialTicks(),mc.thePlayer);
        RenderUtil.prepareBoxRender(3.25F, 1F, 1F, 1F, 0.2F);
        for (float i=0.5f;i<=10;i=i+0.05f){
            RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), event.getPartialTicks(), actualX+mc.thePlayer.motionX*i, actualY, actualZ+mc.thePlayer.motionZ*i,
                    lastActualX+mc.thePlayer.motionX*i, lastActualY, lastActualZ+mc.thePlayer.motionZ*i);
        }
        RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), event.getPartialTicks(), actualX+mc.thePlayer.motionX*2, actualY, actualZ+mc.thePlayer.motionZ*2,
                lastActualX+mc.thePlayer.motionX*2, lastActualY, lastActualZ+mc.thePlayer.motionZ*2);
        RenderUtil.prepareBoxRender(3.25F, 1F, 1F, 1F, 0.4F);
        RenderUtil.renderCustomPlayerBox(mc.getRenderManager(), event.getPartialTicks(), actualX, actualY, actualZ, lastActualX, lastActualY, lastActualZ);
        RenderUtil.stopBoxRender();

    }
}
