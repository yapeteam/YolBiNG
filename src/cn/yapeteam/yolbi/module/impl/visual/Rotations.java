package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.event.impl.render.RotationsRenderEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class Rotations extends Module {

    private float yaw, pitch;
    private float lastYaw, lastPitch;

    private boolean customRender;

    private final BooleanValue smooth = new BooleanValue("Smooth", true);

    public Rotations() {
        super("Rotations", ModuleCategory.VISUAL);
        this.addValues(smooth);
    }

    @Listener(Priority.LOWER)
    public void onMotion(MotionEvent event) {
        customRender = mc.thePlayer.rotationYaw != event.getYaw() || mc.thePlayer.rotationPitch != event.getPitch();

        this.lastYaw = yaw;
        this.lastPitch = pitch;

        yaw = event.getYaw();
        pitch = event.getPitch();
    }

    @Listener(Priority.LOWER)
    public void onRotsRender(RotationsRenderEvent event) {
        if (customRender) {
            float partialTicks = event.getPartialTicks();

            event.setYaw(smooth.getValue() ? interpolateRotation(lastYaw, yaw, partialTicks) : yaw);
            event.setBodyYaw(smooth.getValue() ? interpolateRotation(lastYaw, yaw, partialTicks) : yaw);
            event.setPitch(smooth.getValue() ? lastPitch + (pitch - lastPitch) * partialTicks : pitch);
        }
    }

    protected float interpolateRotation(float par1, float par2, float par3) {
        float f;

        for (f = par2 - par1; f < -180.0F; f += 360.0F) {
            ;
        }

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return par1 + par3 * f;
    }

}
