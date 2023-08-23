package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.TickEvent;
import cn.yapeteam.yolbi.event.impl.render.Render3DEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.visual.mobends.AnimatedEntity;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.RenderBendsPlayer;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.RenderBendsSpider;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.RenderBendsZombie;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Player;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Spider;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Zombie;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/8/23 17:10
 */
public class Mobends extends Module {
    private static Mobends mobends;
    public static Mobends getInstance(){
        return mobends;
    }

    private final BooleanValue zombieAnimation = new BooleanValue("Zombie Animation", true);
    private final BooleanValue spiderAnimation = new BooleanValue("Spider Animation", true);
    public final BooleanValue swordTrail = new BooleanValue("Sword Trail", true);
    public final BooleanValue spinAttack = new BooleanValue("Spin attack", true);
    public final BooleanValue usecolor = new BooleanValue("Use color",true);
    public Mobends() {
        super("Mobends",ModuleCategory.VISUAL);
        this.addValues(zombieAnimation, spiderAnimation, swordTrail, spinAttack,usecolor);
        AnimatedEntity.register();
        mobends = this;
    }

    public float partialTicks = 0.0f;
    public float ticks = 0.0f;
    public float ticksPerFrame = 0.0f;
    public float count=0;
    public ResourceLocation texture_NULL = new ResourceLocation("mobends/textures/white.png");

    @Listener
    protected void onRender3D(Render3DEvent event){
        if (mc.theWorld == null) {
            return;
        }

        float partialTicks = event.getPartialTicks();

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player.dataList.get(i).update(partialTicks);
        }

        for (int i = 0; i < Data_Zombie.dataList.size(); i++) {
            Data_Zombie.dataList.get(i).update(partialTicks);
        }

        for (int i = 0; i < Data_Spider.dataList.size(); i++) {
            Data_Spider.dataList.get(i).update(partialTicks);
        }
        if (mc.thePlayer != null) {
            float newTicks = mc.thePlayer.ticksExisted + partialTicks;
            if (!(mc.theWorld.isRemote && mc.isGamePaused())) {
                ticksPerFrame = Math.min(Math.max(0F, newTicks - ticks), 1F);
                ticks = newTicks;
            } else {
                ticksPerFrame = 0F;
            }
        }
    }

    @Listener
    protected void onTick(TickEvent event){
        if (mc.theWorld == null) {
            return;
        }
        count = (float) (count + 0.1);
        if (count >=18) count=1;

        for (int i = 0; i < Data_Player.dataList.size(); i++) {
            Data_Player data = Data_Player.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getCommandSenderName())) {
                    Data_Player.dataList.remove(data);
                    Data_Player.add(new Data_Player(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Player.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0; i < Data_Zombie.dataList.size(); i++) {
            Data_Zombie data = Data_Zombie.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getCommandSenderName())) {
                    Data_Zombie.dataList.remove(data);
                    Data_Zombie.add(new Data_Zombie(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Zombie.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

        for (int i = 0; i < Data_Spider.dataList.size(); i++) {
            Data_Spider data = Data_Spider.dataList.get(i);
            Entity entity = mc.theWorld.getEntityByID(data.entityID);
            if (entity != null) {
                if (!data.entityType.equalsIgnoreCase(entity.getCommandSenderName())) {
                    Data_Spider.dataList.remove(data);
                    Data_Spider.add(new Data_Spider(entity.getEntityId()));
                    //BendsLogger.log("Reset entity",BendsLogger.DEBUG);
                } else {

                    data.motion_prev.set(data.motion);

                    data.motion.x = (float) entity.posX - data.position.x;
                    data.motion.y = (float) entity.posY - data.position.y;
                    data.motion.z = (float) entity.posZ - data.position.z;

                    data.position = new Vector3f((float) entity.posX, (float) entity.posY, (float) entity.posZ);
                }
            } else {
                Data_Spider.dataList.remove(data);
                //BendsLogger.log("No entity",BendsLogger.DEBUG);
            }
        }

    }


    public boolean onRenderLivingEvent(RendererLivingEntity renderer, EntityLivingBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (!this.isEnabled() || renderer instanceof RenderBendsPlayer || renderer instanceof RenderBendsZombie || renderer instanceof RenderBendsSpider) {
            return false;
        }

        AnimatedEntity animatedEntity = AnimatedEntity.getByEntity(entity);
        //System.out.println(animatedEntity);
        if (animatedEntity != null && (entity instanceof EntityPlayer || (entity instanceof EntityZombie && zombieAnimation.getValue()) || (entity instanceof EntitySpider && spiderAnimation.getValue()))) {
            if (entity instanceof EntityPlayer) {
                AbstractClientPlayer player = (AbstractClientPlayer) entity;
                AnimatedEntity.getPlayerRenderer(player).doRender(player, x, y, z, entityYaw, partialTicks);
            } else if (entity instanceof EntityZombie) {
                EntityZombie zombie = (EntityZombie) entity;
                AnimatedEntity.zombieRenderer.doRender(zombie, x, y, z, entityYaw, partialTicks);
            } else {
                EntitySpider spider = (EntitySpider) entity;
                AnimatedEntity.spiderRenderer.doRender(spider, x, y, z, entityYaw, partialTicks);
            }
            return true;
        }
        return false;
    }

}
