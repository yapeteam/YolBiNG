package cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity;



import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayer;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.layers.LayerBendsCape;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.layers.LayerBendsCustomHead;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.layers.LayerBendsPlayerArmor;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Player;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderBendsPlayer extends RenderPlayer {
    private final boolean smallArms;

    public RenderBendsPlayer(RenderManager renderManager) {
        super(renderManager, false);
        this.smallArms = false;
        this.mainModel = new ModelBendsPlayer(0.0F, false);
        this.layerRenderers.clear();
        this.addLayer(new LayerBendsPlayerArmor(this));
        this.addLayer(new LayerHeldItem(this));
        //this.addLayer(new LayerArrow(this));
        //this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerBendsCape(this));
        this.addLayer(new LayerBendsCustomHead((ModelBendsPlayer) this.getMainModel()));
    }

    public RenderBendsPlayer(RenderManager renderManager, boolean useSmallArms) {
        super(renderManager, useSmallArms);
        this.smallArms = useSmallArms;
        this.mainModel = new ModelBendsPlayer(0.0F, useSmallArms);
        this.layerRenderers.clear();
        this.addLayer(new LayerBendsPlayerArmor(this));
        this.addLayer(new LayerHeldItem(this));
        //this.addLayer(new LayerArrow(this));
        //this.addLayer(new LayerDeadmau5Head(this));
        this.addLayer(new LayerBendsCape(this));
        this.addLayer(new LayerBendsCustomHead((ModelBendsPlayer) this.getMainModel()));
    }

    @Override
    public ModelPlayer getMainModel() {
        if (!(this.mainModel instanceof ModelBendsPlayer)) {
            this.mainModel = new ModelBendsPlayer(0.0F, this.smallArms);
        }
        return (ModelBendsPlayer) this.mainModel;
    }

    private void setModelVisibilities(AbstractClientPlayer clientPlayer) {
        ModelBendsPlayer modelplayer = (ModelBendsPlayer) this.getMainModel();

        if (clientPlayer.isSpectator()) {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        } else {
            ItemStack itemstack = clientPlayer.inventory.getCurrentItem();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.heldItemLeft = 0;
            modelplayer.aimedBow = false;
            modelplayer.isSneak = clientPlayer.isSneaking();

            if (itemstack == null) {
                modelplayer.heldItemRight = 0;
            } else {
                modelplayer.heldItemRight = 1;

                if (clientPlayer.getItemInUseCount() > 0) {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK) {
                        modelplayer.heldItemRight = 3;
                    } else if (enumaction == EnumAction.BOW) {
                        modelplayer.aimedBow = true;
                    }
                }
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        return entity.getLocationSkin();
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * clientPlayer, partialTicks
     */
    @Override
    protected void preRenderCallback(AbstractClientPlayer clientPlayer, float partialTicks) {
        float f1 = 0.9375F;
        GlStateManager.scale(f1, f1, f1);

        ((ModelBendsPlayer) this.getMainModel()).updateWithEntityData(clientPlayer);
        ((ModelBendsPlayer) this.mainModel).postRenderTranslate(0.0625f);

        Data_Player data = Data_Player.get(clientPlayer.getEntityId());

        GL11.glPushMatrix();
        float f5 = 0.0625F;
        GL11.glScalef(-f5, -f5, f5);
        data.swordTrail.render((ModelBendsPlayer) this.getMainModel());
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glPopMatrix();


        ((ModelBendsPlayer) this.getMainModel()).postRenderRotate(0.0625f);
    }

    @Override
    public void renderRightArm(AbstractClientPlayer clientPlayer) {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.swingProgress = 0.0F;
        modelplayer.isSneak = false;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.renderRightArm();
    }

    @Override
    public void renderLeftArm(AbstractClientPlayer clientPlayer) {
        float f = 1.0F;
        GlStateManager.color(f, f, f);
        ModelPlayer modelplayer = this.getMainModel();
        this.setModelVisibilities(clientPlayer);
        modelplayer.isSneak = false;
        modelplayer.swingProgress = 0.0F;
        modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
        modelplayer.renderLeftArm();
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    @Override
    protected void renderLivingAt(AbstractClientPlayer p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_) {
        super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doe
     */
}
