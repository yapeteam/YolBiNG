package cn.yapeteam.yolbi.module.impl.combat;

import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.impl.RenderEvent;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.player.FixedRotations;
import cn.yapeteam.yolbi.util.player.RotationsUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

import java.util.ArrayList;
import java.util.Comparator;

public class AimAssist extends Module {

    private Antibot antibotModule;
    private Teams teamsModule;

    private final ModeValue<String> filter = new ModeValue<>("Filter", "Range", "Range", "Health");
    private final NumberValue<Double> range = new NumberValue<>("Range", 4.5, 3.0, 8.0, 0.1);

    private final NumberValue<Integer> speed = new NumberValue<>("Speed", 10, 1, 40, 1);

    private final TimerUtil timer = new TimerUtil();

    private EntityPlayer target;

    private FixedRotations rotations;

    public AimAssist() {
        super("AimAssist", ModuleCategory.COMBAT);
        this.addValues(filter, range, speed);
    }

    @Override
    public void onEnable() {
        rotations = new FixedRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);
    }

    @Override
    public void onClientStarted() {
        antibotModule = Vestige.instance.getModuleManager().getModule(Antibot.class);
        teamsModule = Vestige.instance.getModuleManager().getModule(Teams.class);
    }

    @Listener
    public void onRender(RenderEvent event) {
        target = findTarget();

        if(target != null && Mouse.isButtonDown(0) && mc.currentScreen == null) {
            float rots[] = RotationsUtil.getRotationsToEntity(target, false);

            float yaw = rots[0];
            float currentYaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);

            float diff = Math.abs(currentYaw - yaw);

            if (diff >= 4 && diff <= 356) {
                float aa;

                if (diff <= speed.getValue()) {
                    aa = diff * 0.9F;
                } else {
                    aa = (float) (speed.getValue() - Math.random() * 0.5F);
                }

                float finalSpeed = aa * Math.max(timer.getTimeElapsed(), 1) * 0.01F;

                if (diff <= 180) {
                    if (currentYaw > yaw) {
                        mc.thePlayer.rotationYaw -= finalSpeed;
                    } else {
                        mc.thePlayer.rotationYaw += finalSpeed;
                    }
                } else {
                    if (currentYaw > yaw) {
                        mc.thePlayer.rotationYaw += finalSpeed;
                    } else {
                        mc.thePlayer.rotationYaw -= finalSpeed;
                    }
                }
            }
        }

        rotations.updateRotations(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch);

        mc.thePlayer.rotationYaw = rotations.getYaw();
        mc.thePlayer.rotationPitch = rotations.getPitch();

        timer.reset();
    }

    public EntityPlayer findTarget() {
        ArrayList<EntityPlayer> entities = new ArrayList<>();
        for(Entity entity : mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityPlayer && entity != mc.thePlayer) {
                EntityPlayer player = (EntityPlayer) entity;

                if(canAttackEntity(player)) {
                    entities.add(player);
                }
            }
        }

        if(entities != null && entities.size() > 0) {
            switch (filter.getValue()) {
                case "Range":
                    entities.sort(Comparator.comparingDouble(entity -> entity.getDistanceToEntity(mc.thePlayer)));
                    break;
                case "Health":
                    entities.sort(Comparator.comparingDouble(entity -> entity.getHealth()));
                    break;
            }

            return entities.get(0);
        }

        return null;
    }

    private boolean canAttackEntity(EntityPlayer player) {
        if (!player.isDead) {
            if (mc.thePlayer.getDistanceToEntity(player) < range.getValue()) {
                if ((!player.isInvisible() && !player.isInvisibleToPlayer(mc.thePlayer))) {
                    if(!teamsModule.canAttack(player)) {
                        return false;
                    }

                    return antibotModule.canAttack(player, this);
                }
            }
        }

        return false;
    }

}
