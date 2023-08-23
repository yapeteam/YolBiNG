package cn.yapeteam.yolbi.module.impl.visual.mobends.animation.player;



import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.ModelRendererBends;
import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.entity.ModelBendsPlayer;
import cn.yapeteam.yolbi.module.impl.visual.mobends.data.Data_Player;
import cn.yapeteam.yolbi.module.impl.visual.mobends.util.GUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;

import org.lwjgl.util.vector.Vector3f;

public class Animation_Attack_Combo1 {
	public static void animate(EntityPlayer player, ModelBendsPlayer model, Data_Player data){
		if(data.ticksAfterPunch < 0.5f){
			model.swordTrail.reset();
		}
		
		if(player.getCurrentEquippedItem() != null){
			if(player.getCurrentEquippedItem().getItem() instanceof ItemSword){
				model.swordTrail.add(model);
			}
		}
		
		float attackState = data.ticksAfterPunch/10.0f;
		float armSwing = attackState*3.0f;
		armSwing = GUtil.max(armSwing, 1.0f);
		
//		if(!KeepRiding.getKeepRiding().isToggled() && !player.isRiding()){
//			model.renderRotation.setSmoothY(30,0.7f);
//		}
		
		Vector3f bodyRot = new Vector3f(0,0,0);
		
		bodyRot.x = 20.0f-attackState*20.0f;
		bodyRot.y = -40.0f*attackState+50*attackState;
		
		((ModelRendererBends)model.bipedBody).rotation.setSmooth(bodyRot,0.9f);
		((ModelRendererBends)model.bipedHead).rotation.setY(model.headRotationY-30);
		((ModelRendererBends)model.bipedHead).rotation.setX(model.headRotationX);
		((ModelRendererBends)model.bipedHead).pre_rotation.setSmoothX(-bodyRot.x,0.9f);
		((ModelRendererBends)model.bipedHead).pre_rotation.setSmoothY(-bodyRot.y,0.9f);
		
		
		
		((ModelRendererBends)model.bipedRightArm).pre_rotation.setSmoothZ(60.0f,0.3f);
		((ModelRendererBends)model.bipedRightArm).rotation.setSmoothX(-20+armSwing*100,3.0f);
		((ModelRendererBends)model.bipedRightArm).rotation.setSmoothY(0.0f,0.3f);
		((ModelRendererBends)model.bipedRightArm).rotation.setSmoothZ(0.0f,0.9f);
		((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothZ(20,0.3f);
		((ModelRendererBends)model.bipedLeftArm).pre_rotation.setSmoothZ(-80,0.3f);
		((ModelRendererBends)model.bipedLeftArm).rotation.setSmoothY(0.0f,0.3f);
		
		((ModelRendererBends)model.bipedRightForeArm).rotation.setSmoothX(-20,0.3f);
		((ModelRendererBends)model.bipedLeftForeArm).rotation.setSmoothX(-60,0.3f);
		
		if(data.motion.x == 0 & data.motion.z == 0){
			((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothX(-30,0.3f);
			((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothX(-30,0.3f);
			((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothY(-25,0.3f);
			((ModelRendererBends)model.bipedRightLeg).rotation.setSmoothZ(10);
			((ModelRendererBends)model.bipedLeftLeg).rotation.setSmoothZ(-10);
			
			((ModelRendererBends)model.bipedRightForeLeg).rotation.setSmoothX(30,0.3f);
			((ModelRendererBends)model.bipedLeftForeLeg).rotation.setSmoothX(30,0.3f);
			
//			if(!KeepRiding.getKeepRiding().isToggled() && !player.isRiding()){
//				model.renderOffset.setSmoothY(-2.0f);
//			}
		}else{
			
		}
		
		model.renderItemRotation.setSmoothX(90, 0.9f);
	}
}
