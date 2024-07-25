package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

public class LayerBipedArmor extends LayerArmorBase<ModelBiped>
{
    public LayerBipedArmor(RendererLivingEntity<?> rendererIn)
    {
        super(rendererIn);
    }

    protected void initArmor()
    {
        this.modelLeggings = new ModelBiped(0.5F);
        this.modelArmor = new ModelBiped(1.0F);
    }

    protected void setModelPartVisible(ModelBiped p_177179_1_, int p_177179_2_)
    {
        this.func_177194_a(p_177179_1_);

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

    protected void func_177194_a(ModelBiped p_177194_1_)
    {
        p_177194_1_.setInvisible(false);
    }
}
