package cn.yapeteam.yolbi.util.player;

import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.world.WorldUtil;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import java.util.List;

public class RotationsUtil implements IMinecraft {

    public static float[] getRotationsToPosition(double x, double y, double z) {
        double deltaX = x - mc.thePlayer.posX;
        double deltaY = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double deltaZ = z - mc.thePlayer.posZ;

        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(-Math.atan2(deltaX, deltaZ));
        float pitch = (float) Math.toDegrees(-Math.atan2(deltaY, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToBlockPos(BlockPos pos) {
        return getRotationsToPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static float[] getRotationsToPosition(double x, double y, double z, double targetX, double targetY, double targetZ) {
        double dx = targetX - x;
        double dy = targetY - y;
        double dz = targetZ - z;

        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

        float yaw = (float) Math.toDegrees(-Math.atan2(dx, dz));
        float pitch = (float) Math.toDegrees(-Math.atan2(dy, horizontalDistance));

        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToEntity(EntityLivingBase entity, boolean usePartialTicks) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToEntity(double x, double y, double z, EntityLivingBase entity, boolean usePartialTicks) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks : entity.posX;
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks : entity.posY;
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks : entity.posZ;

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? y + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(x, y + mc.thePlayer.getEyeHeight(), z, entityX, finalEntityY, entityZ);
    }

    public static float[] getRotationsToEntityRandomised(EntityLivingBase entity, boolean usePartialTicks, double randomAmount) {
        float partialTicks = mc.timer.renderPartialTicks;

        double entityX = usePartialTicks ? entity.lastTickPosX + (entity.posX + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosX) * partialTicks : entity.posX + (Math.random() * randomAmount - randomAmount * 0.5);
        double entityY = usePartialTicks ? entity.lastTickPosY + (entity.posY + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosY) * partialTicks : entity.posY + (Math.random() * randomAmount - randomAmount * 0.9);
        double entityZ = usePartialTicks ? entity.lastTickPosZ + (entity.posZ + (Math.random() * randomAmount - randomAmount * 0.5) - entity.lastTickPosZ) * partialTicks : entity.posZ + (Math.random() * randomAmount - randomAmount * 0.5);

        double yDiff = mc.thePlayer.posY - entityY;

        double finalEntityY = yDiff >= 0 ? entityY + entity.getEyeHeight() : -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entityY;

        return getRotationsToPosition(entityX, finalEntityY, entityZ);
    }

    public static boolean raycastEntity(EntityLivingBase target, float yaw, float pitch, float lastYaw, float lastPitch, double reach) {
        Entity entity = mc.getRenderViewEntity();

        Entity pointedEntity = null;

        if (entity != null && mc.theWorld != null) {
            float partialTicks = mc.timer.renderPartialTicks;

            double d0 = mc.playerController.getBlockReachDistance();
            mc.objectMouseOver = WorldUtil.raytraceLegit(yaw, pitch, lastYaw, lastPitch);
            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;

            if (mc.playerController.extendedReach()) {
                d0 = 6.0D;
                d1 = 6.0D;
            } else if (d0 > reach) {
                flag = true;
            }

            if (mc.objectMouseOver != null) {
                d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
            }

            float aaaa = lastPitch + (pitch - lastPitch) * partialTicks;
            float bbbb = lastYaw + (yaw - lastYaw) * partialTicks;
            Vec3 vec31 = mc.thePlayer.getVectorForRotation(aaaa, bbbb);

            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Vec3 vec33 = null;
            float f = 1.0F;
            List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                public boolean apply(Entity p_apply_1_) {
                    return p_apply_1_.canBeCollidedWith();
                }
            }));
            double d2 = d1;

            for (Entity value : list) {
                float f1 = value.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = value.getEntityBoundingBox().expand(f1, f1, f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = value;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        boolean flag1 = false;

                        if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                            flag1 = Reflector.callBoolean(value, Reflector.ForgeEntity_canRiderInteract);
                        }

                        if (!flag1 && value == entity.ridingEntity) {
                            if (d2 == 0.0D) {
                                pointedEntity = value;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            pointedEntity = value;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            }

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > reach) {
                pointedEntity = null;
                mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }
        }

        return pointedEntity != null && pointedEntity == target;
    }

    public static float getGCD() {
        return (float) (Math.pow(mc.gameSettings.mouseSensitivity * 0.6 + 0.2, 3) * 1.2);
    }

}