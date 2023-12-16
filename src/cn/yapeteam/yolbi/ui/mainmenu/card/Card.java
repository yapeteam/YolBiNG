package cn.yapeteam.yolbi.ui.mainmenu.card;


import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.Stencil;
import cn.yapeteam.yolbi.util.render.animation.Animation;
import cn.yapeteam.yolbi.util.render.animation.Easing;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.ui.mainmenu.card
 * don't mind
 * @date 2023/11/25 21:46
 */

@Getter
public class Card {
    private String name;
    private int xPostion;
    private int yPostion;
    private boolean isRender;
    private Runnable action;
    private final int heigh=25;
    private final int width=270;
    private Animation animation = new Animation(Easing.EASE_OUT_EXPO,2000);

    public Card(String name, int xPostion, int yPostion, boolean isRender, Runnable action) {
        this.name = name;
        this.xPostion = xPostion;
        this.yPostion = yPostion;
        this.isRender = isRender;
        this.action = action;
        animation.setValue(200);
    }


    public void drawCard(int mouseX,int mouseY){
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        animation.run(checkIsHovever(mouseX,mouseY)?200:1);
        //if (!checkIsHovever(mouseX,mouseY)) return;
        RenderUtil.drawFastRoundedRect(xPostion,yPostion,xPostion+width,yPostion+heigh,5,new Color(255, 255, 255,(int)animation.getValue()/2).getRGB());

        Stencil.write(false);
       //DrawUtil.drawImage(new ResourceLocation("yolbi/BackGround/BackGround.png"),0,0,this.width,this.height);
        RenderUtil.drawCircle(mouseX,mouseY,animation.getValue()*4,new Color(255,255,255, (int) animation.getValue()).getRGB());
        Stencil.erase(true);
        DrawUtil.drawImage(new ResourceLocation("yolbi/BackGround/"+name+".png"),0,0,sr.getScaledWidth(),sr.getScaledHeight());
        //RenderUtil.drawRect(0,0,sr.getScaledWidth(),sr.getScaledHeight(),new Color(26, 26, 26, 113).getRGB());
        RenderUtil.drawFastRoundedRect(xPostion,yPostion,xPostion+width,yPostion+heigh,5,new Color(255, 255, 255,(int)animation.getValue()/2).getRGB());

        Stencil.dispose();


        if (checkIsHovever(mouseX,mouseY)) YolBi.instance.getFontManager().getPingFang72().drawCenteredString(name,sr.getScaledWidth()/2,sr.getScaledHeight()/2,new Color(255,255,255,(int)animation.getValue()));
    }

    public boolean checkIsHovever(int x,int y){
        return xPostion<=x&&x<=xPostion+width&&yPostion<=y&&y<=yPostion+heigh;
    }


}
