package cn.yapeteam.yolbi.ui.noti;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender2D;
import cn.yapeteam.yolbi.font.AbstractFontRenderer;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.impl.visual.Notifications;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.optifine.util.LinkedList;

import java.awt.*;
import java.util.ArrayList;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.ui.noti
 * don't mind
 * @date 2023/12/16 21:41
 */
public class InfoMannager implements IMinecraft {
    private final Module notificationModule;
    private ScaledResolution scaledResolution;

    private final ArrayList<NotificationINFO>  notificationINFOLinkedList;
    private final AbstractFontRenderer font;

    public InfoMannager() {
        YolBi.instance.getEventManager().register(this);
        notificationModule = YolBi.instance.getModuleManager().getModule(Notifications.class);
        notificationINFOLinkedList = new ArrayList<>();
        font = YolBi.instance.getFontManager().getPingFang18();
    }


    public void post(final NotificationINFO notificationINFO){
        notificationINFOLinkedList.add(notificationINFO);
    }

    public void delete(){
        delete(0);
    }

    public void delete(int index){
        if (notificationINFOLinkedList.isEmpty()) return;
        notificationINFOLinkedList.remove(index);
    }

    @Listener
    private void onRender(final EventRender2D event){
        if (!notificationModule.isEnabled()) return;
        if (notificationINFOLinkedList.isEmpty()) return;
        scaledResolution = new ScaledResolution(mc);

        for (int index=0;index<notificationINFOLinkedList.size();index++){
            int baseHeight = index * 30;
            NotificationINFO info = notificationINFOLinkedList.get(index);
            if (info.getAnimation().getValue()>=1){
                delete(index);
                continue;
            }

            String text =  info.getText();
            switch (info.getType()){
                case INFO: {
                    text = "Info : " + text;
                    break;
                }case WARN:{
                    text = "Warning : " + text;
                    break;
                }case SUCCESS:{
                    text = "Success : " + text;
                    break;
                }
            }
            RenderUtil.drawBloomShadow(
                    (float) (scaledResolution.getScaledWidth()/2-font.getStringWidth(text)*0.75),
                    scaledResolution.getScaledHeight()/2-font.getHeight()/2-baseHeight,
                    (float) (font.getStringWidth(text)*1.5),
                    font.getHeight()*2,50,5,new Color(0, 0, 0, 187)
                    );
            font.drawCenteredString(text,scaledResolution.getScaledWidth()/2,
                    scaledResolution.getScaledHeight()/2-baseHeight,-1);



        }
    }


}
