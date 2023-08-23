package cn.yapeteam.yolbi.module.impl.visual.mobends.animation.player;



import cn.yapeteam.yolbi.module.impl.visual.mobends.animation.Animation;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayer;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Player;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.EntityData;
import cn.yapeteam.yolbi.module.impl.visual.mobends.pack.BendsPack;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class Animation_Attack extends Animation {
	public String getName() {
		return "attack";
	}

	@Override
	public void animate(EntityLivingBase argEntity, ModelBase argModel, EntityData argData) {
		ModelBendsPlayer model = (ModelBendsPlayer) argModel;
		Data_Player data = (Data_Player) argData;
		EntityPlayer player = (EntityPlayer) argEntity;

		if (player.getCurrentEquippedItem() != null) {
			if (data.ticksAfterPunch < 10) {
				if (data.currentAttack == 1) {
					Animation_Attack_Combo0.animate((EntityPlayer) argEntity,model,data);
					BendsPack.animate(model,"player","attack");
					BendsPack.animate(model,"player","attack_0");
				} else if (data.currentAttack == 2) {
					Animation_Attack_Combo1.animate((EntityPlayer) argEntity,model,data);
					BendsPack.animate(model,"player","attack");
					BendsPack.animate(model,"player","attack_1");
				} else if (data.currentAttack == 3) {
					Animation_Attack_Combo2.animate((EntityPlayer) argEntity,model,data);
					BendsPack.animate(model,"player","attack");
					BendsPack.animate(model,"player","attack_2");
				}
			} else if (data.ticksAfterPunch < 60) {
				Animation_Attack_Stance.animate((EntityPlayer) argEntity,model,data);
				BendsPack.animate(model,"player","attack_stance");
			}
		} else {
			if (data.ticksAfterPunch < 10) {
				Animation_Attack_Punch.animate((EntityPlayer) argEntity,model,data);
				BendsPack.animate(model,"player","attack");
				BendsPack.animate(model,"player","punch");
			} else if (data.ticksAfterPunch < 60) {
				Animation_Attack_PunchStance.animate((EntityPlayer) argEntity,model,data);
				BendsPack.animate(model,"player","punch_stance");
			}
		}
	}
}
