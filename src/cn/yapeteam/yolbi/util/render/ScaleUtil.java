package cn.yapeteam.yolbi.util.render;

import cn.yapeteam.yolbi.util.IMinecraft;
import net.minecraft.client.Minecraft;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.render
 * don't mind
 * @date 2023/8/23 17:44
 */
public class ScaleUtil implements IMinecraft {
    public static double[] getScaledMouseCoordinates(double mouseX, double mouseY){
        double x = mouseX;
        double y = mouseY;
        switch (mc.gameSettings.guiScale){
            case 0:
                x*=2;
                y*=2;
                break;
            case 1:
                x*=0.5;
                y*=0.5;
                break;
            case 3:
                x*=1.4999999999999999998;
                y*=1.4999999999999999998;
        }
        return new double[]{x,y};
    }
}
