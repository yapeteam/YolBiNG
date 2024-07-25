package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventEntityAction;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "WTap", category = ModuleCategory.COMBAT)
public class WTap extends Module {

    private boolean taped, stoppedLastTick;

    private final ModeValue<String> mode = new ModeValue<>("Mode", "Legit", "Legit", "Spoof");

    private KillAura killauraModule;

    private EntityLivingBase lastCursorTarget;

    private int cursorTargetTicks;

    public WTap() {
        this.addValues(mode);
    }

    @Override
    public void onEnable() {
        taped = false;
        stoppedLastTick = false;
    }

    @Override
    public void onClientStarted() {
        killauraModule = YolBi.instance.getModuleManager().getModule(KillAura.class);
    }

    @Listener(Priority.LOW)
    public void onTick(EventTick event) {
        if (mode.is("Legit")) {
            if (stoppedLastTick) {
                mc.gameSettings.keyBindForward.setPressed(Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode()));
                stoppedLastTick = false;
                return;
            }

            EntityLivingBase target = getCurrentTarget();

            if (target != null) {
                if (target.hurtTime >= 2 && mc.thePlayer.onGround && mc.thePlayer.isSprinting()) {
                    if (!taped) {
                        mc.gameSettings.keyBindSprint.setPressed(false);
                        mc.gameSettings.keyBindForward.setPressed(false);
                        stoppedLastTick = true;
                        taped = true;
                    }
                } else {
                    taped = false;
                }
            } else {
                taped = false;
            }
        }
    }

    @Listener(Priority.LOW)
    public void onEntityAction(EventEntityAction event) {
        if (mode.is("Spoof")) {
            EntityLivingBase target = getCurrentTarget();

            if (target != null) {
                if (target.hurtTime >= 2 && mc.thePlayer.onGround && mc.thePlayer.isSprinting()) {
                    if (!taped) {
                        event.setSprinting(false);
                        taped = true;
                    }
                } else {
                    taped = false;
                }
            } else {
                taped = false;
            }
        }
    }

    public EntityLivingBase getCurrentTarget() {
        if (killauraModule == null) {
            killauraModule = YolBi.instance.getModuleManager().getModule(KillAura.class);
        }

        if (killauraModule.isEnabled() && killauraModule.getTarget() != null) {
            return killauraModule.getTarget();
        } else if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && mc.objectMouseOver.entityHit instanceof EntityLivingBase) {
            lastCursorTarget = (EntityLivingBase) mc.objectMouseOver.entityHit;

            return (EntityLivingBase) mc.objectMouseOver.entityHit;
        } else if (lastCursorTarget != null) {
            if (++cursorTargetTicks > 10) {
                lastCursorTarget = null;
            } else {
                return lastCursorTarget;
            }
        }

        return null;
    }

}
