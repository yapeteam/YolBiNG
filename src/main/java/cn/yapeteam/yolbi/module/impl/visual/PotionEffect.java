package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.module.*;
import cn.yapeteam.yolbi.util.render.FontUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.animation.Animation;
import cn.yapeteam.yolbi.util.render.animation.Easing;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/10/1 13:18
 */

@ModuleInfo(name = "PotionEffect", category = ModuleCategory.VISUAL)
public class PotionEffect extends HUDModule {

    private int numcount,lastNum =0;
    private Animation animation = new Animation(Easing.EASE_IN_OUT_ELASTIC,1000);
    private boolean inflag,outflag=false;
    public PotionEffect(){
        super(0, 0, 140, 50, AlignType.LEFT);
        this.width = 100;
//        ScaledResolution sr = new ScaledResolution(mc);
//        this.posX.setValue(sr.getScaledWidth() / 2 - width / 2);
//        this.posY.setValue(sr.getScaledHeight() / 2 + 20);
    }

//    @Override
//    public void onClientStarted() {
//        theme = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
//    }


    @Override
    protected void renderModule(boolean inChat) {
        if (this.isEnabled()){
            onRender();
        }

    }
    @Override
    public void onDisable(){
        numcount = 0;
        lastNum = 0;
    }

    protected void onRender(){
        if (mc.thePlayer.getActivePotionEffects().isEmpty()) return;
        //RenderUtil.drawRect(this.posX.getValue(),this.posY.getValue(),
        //        this.posX.getValue()+this.width,this.posY.getValue()+this.height,new Color(255, 255, 255,123).getRGB());
        int forPosY = 0;
        numcount = 0;
        for (final net.minecraft.potion.PotionEffect potionEffect:mc.thePlayer.getActivePotionEffects()){
            numcount += 1;
            final Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
             String name = I18n.format(potion.getName());

            if (potionEffect.getAmplifier() == 1) {
                name = name + " " + I18n.format("enchantment.level.2");
            } else if (potionEffect.getAmplifier() == 2) {
                name = name + " " + I18n.format("enchantment.level.3");
            } else if (potionEffect.getAmplifier() == 3) {
                name = name + " " + I18n.format("enchantment.level.4");
            }
            if (inflag && numcount==lastNum-1){
                System.out.println("Testtttt");
                animation.run(200);
                RenderUtil.drawBloomShadow((float) (this.posX.getValue().intValue()+animation.getValue()),this.posY.getValue().intValue()+forPosY,
                        100f,18f,5,5,new Color(0,0,0,120));
                drawPotionIcon(potion, (int) (this.posX.getValue().intValue()+animation.getValue()),this.posY.getValue().intValue()+forPosY-2,0.5f);
                YolBi.instance.getFontManager().getPingFangBold16().drawString(
                        name,
                        (float) (this.posX.getValue()+20+animation.getValue()),
                        (float) (this.posY.getValue()+forPosY+5),
                        -1);
                if (potionEffect.getDuration()<=100){ //时间小于5秒
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70+animation.getValue()),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(255, 80, 80));
                }else if (potionEffect.getDuration()<=300){ //时间小于15秒
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70+animation.getValue()),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(255, 213, 67));
                }else {
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70+animation.getValue()),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(132, 255, 150));
                }
                //if (animation.isFinished()) inflag = false;
                forPosY+=20;

            }else if (outflag){

            }else {
                RenderUtil.drawBloomShadow(this.posX.getValue().intValue(),this.posY.getValue().intValue()+forPosY,
                        100f,18f,5,5,new Color(0,0,0,120));
                drawPotionIcon(potion,this.posX.getValue().intValue(),this.posY.getValue().intValue()+forPosY-2,0.5f);
                YolBi.instance.getFontManager().getPingFangBold16().drawString(
                        name,
                        (float) (this.posX.getValue()+20),
                        (float) (this.posY.getValue()+forPosY+5),
                        -1);
                if (potionEffect.getDuration()<=100){ //时间小于5秒
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(255, 80, 80));
                }else if (potionEffect.getDuration()<=300){ //时间小于15秒
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(255, 213, 67));
                }else {
                    YolBi.instance.getFontManager().getPingFangBold16().drawString(
                            Potion.getDurationString(potionEffect),
                            (float) (this.posX.getValue()+70),
                            (float) (this.posY.getValue()+forPosY+5),
                            new Color(132, 255, 150));
                }

                forPosY+=20;
            }
        }
        if (numcount != 0 && lastNum != numcount){
            if (lastNum<numcount && !inflag){
                //lastNum = numcount;
                inflag = true;
            }
            if (lastNum>numcount && !outflag){
                //lastNum = numcount;
                outflag = true;
            }
        }
        this.height=forPosY;
        //for ()
    }




    private void drawPotionIcon(Potion potion,int x, int y, float size){
        if (!potion.hasStatusIcon()) return;
        int index = potion.getStatusIconIndex();

        GlStateManager.pushMatrix();
        GlStateManager.scale(size,size,size);
        GlStateManager.enableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
        mc.ingameGUI.drawTexturedModalRect((x + 6)/size, (y + 7)/size,
                index % 8 * 18, 198 + index / 8 * 18,
                18, 18);
        GlStateManager.popMatrix();
    }



}
