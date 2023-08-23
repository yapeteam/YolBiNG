package cn.yapeteam.yolbi.module.impl.visual.mobends.client.renderer.entity.layers;

import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayer;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class LayerBendsCustomHead implements LayerRenderer<EntityLivingBase>
{
    private final ModelBendsPlayer model;
    
    public LayerBendsCustomHead(ModelBendsPlayer p_i46120_1_) {
        this.model = p_i46120_1_;
    }

    public void doRenderLayer(EntityLivingBase livingBase, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        ItemStack itemstack = livingBase.getCurrentArmor(3);

        if (itemstack != null && itemstack.getItem() != null) {
            Item item = itemstack.getItem();
            Minecraft minecraft = Minecraft.getMinecraft();
            GlStateManager.pushMatrix();

            if (livingBase.isSneaking()) {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean flag = livingBase instanceof EntityVillager || livingBase instanceof EntityZombie && ((EntityZombie)livingBase).isVillager();
            float f7;

            if (!flag && livingBase.isChild())
            {
                f7 = 2.0F;
                float f8 = 1.4F;
                GlStateManager.scale(f8 / f7, f8 / f7, f8 / f7);
                GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            }

            this.model.bipedBody.postRender(0.0625F);
            this.model.bipedHead.postRender(0.0625F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            if (item instanceof ItemBlock)
            {
                f7 = 0.625F;
                GlStateManager.translate(0.0F, -0.25F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.scale(f7, -f7, -f7);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                }

                minecraft.getItemRenderer().renderItem(livingBase, itemstack, ItemCameraTransforms.TransformType.HEAD);
            }
            else if (item == Items.skull)
            {
                f7 = 1.1875F;
                GlStateManager.scale(f7, -f7, -f7);

                if (flag)
                {
                    GlStateManager.translate(0.0F, 0.0625F, 0.0F);
                }

                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.readGameProfileFromNBT(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8))
                    {
                        gameprofile = TileEntitySkull.updateGameprofile(new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner")));
                        nbttagcompound.setTag("SkullOwner", NBTUtil.writeGameProfile(new NBTTagCompound(), gameprofile));
                    }
                }

                TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, -1);
            }

            GlStateManager.popMatrix();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}