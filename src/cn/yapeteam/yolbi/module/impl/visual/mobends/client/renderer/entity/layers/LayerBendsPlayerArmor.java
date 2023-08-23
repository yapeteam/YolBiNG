package cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.layers;

import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayerArmor;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;

public class LayerBendsPlayerArmor extends LayerArmorBase<ModelBiped>
{
    public LayerBendsPlayerArmor(RendererLivingEntity<?> rendererIn) {
        super(rendererIn);
    }

    @Override
    protected void initArmor() {
        this.modelLeggings = new ModelBendsPlayerArmor(0.5F);
        this.modelArmor = new ModelBendsPlayerArmor(1.0F);
    }

    @Override
    protected void setModelPartVisible(ModelBiped p_177179_1_, int p_177179_2_)
    {
        this.setModelVisible(p_177179_1_);

        switch (p_177179_2_)
        {
            case 1:
                p_177179_1_.bipedRightLeg.showModel = true;
                p_177179_1_.bipedLeftLeg.showModel = true;
                break;
            case 2:
                p_177179_1_.bipedBody.showModel = true;
                p_177179_1_.bipedRightLeg.showModel = true;
                p_177179_1_.bipedLeftLeg.showModel = true;
                break;
            case 3:
                p_177179_1_.bipedBody.showModel = true;
                p_177179_1_.bipedRightArm.showModel = true;
                p_177179_1_.bipedLeftArm.showModel = true;
                break;
            case 4:
                p_177179_1_.bipedHead.showModel = true;
                p_177179_1_.bipedHeadwear.showModel = true;
        }
    }

    protected void setModelVisible(ModelBiped p_177194_1_)
    {
        p_177194_1_.setInvisible(false);
    }
}