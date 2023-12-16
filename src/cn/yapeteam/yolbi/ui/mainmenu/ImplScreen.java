package cn.yapeteam.yolbi.ui.mainmenu;


import cn.yapeteam.yolbi.ui.guiMultiplayer.GuiMultiplayer;
import cn.yapeteam.yolbi.ui.mainmenu.card.Card;
import cn.yapeteam.yolbi.ui.mainmenu.impl.ButtonPanel;
import cn.yapeteam.yolbi.ui.menu.AltLoginScreen;
import cn.yapeteam.yolbi.ui.menu.InfoGui;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author TIMER_err
 */
public class ImplScreen extends GuiScreen {



    private final LinkedList<Card> cardList = new LinkedList<>();

    @Override
    public void initGui() {
        //trhjuyy
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        Card SinglePlayerCard = new Card("SinglePlayer",width/2-135,scaledResolution.getScaledHeight()/2-75,true,
                () -> Minecraft.getMinecraft().displayGuiScreen(new GuiSelectWorld(this)));
        Card Multiplayerard = new Card("Multiplayer",width/2-135,scaledResolution.getScaledHeight()/2-45,true,
                () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMultiplayer(this)));
        Card AltsCard = new Card("Alts",width/2-135,scaledResolution.getScaledHeight()/2-15,true,
                () -> Minecraft.getMinecraft().displayGuiScreen(new AltLoginScreen()));
        Card OptionsCard = new Card("Options",width/2-135,scaledResolution.getScaledHeight()/2+15,true,
                () -> Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(this, Minecraft.getMinecraft().gameSettings)));
        Card InfoCard = new Card("Info",width/2-135,scaledResolution.getScaledHeight()/2+45,true,
                () -> Minecraft.getMinecraft().displayGuiScreen(new InfoGui()));
        Card ExitCard = new Card("Exit",width/2-135,scaledResolution.getScaledHeight()/2+75,true,
                () -> Minecraft.getMinecraft().shutdown());
        cardList.clear();
        cardList.add(SinglePlayerCard);
        cardList.add(Multiplayerard);
        cardList.add(AltsCard);
        cardList.add(OptionsCard);
        cardList.add(InfoCard);
        cardList.add(ExitCard);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        DrawUtil.drawImage(new ResourceLocation("yolbi/BackGround/BackGround.png"),0,0,this.width,this.height);
        RenderUtil.drawRect(0,0,width,height,new Color(0x28FFFFFF, true).getRGB());
        //标准线
        //RenderUtil.drawRect(0,height/2-5,width,height/2+5,new Color(10,10,10,70).getRGB());
        cardList.forEach(card -> {
            card.drawCard(mouseX,mouseY);
        });




    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        cardList.forEach(card -> {
            if (card.checkIsHovever(mouseX,mouseY)){
                card.getAction().run();
            }
        });
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    }
}
