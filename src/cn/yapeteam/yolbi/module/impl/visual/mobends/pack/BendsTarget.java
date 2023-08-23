package cn.yapeteam.yolbi.module.impl.visual.mobends.pack;





import cn.yapeteam.yolbi.module.impl.visual.mobends.client.model.ModelRendererBends;
import cn.yapeteam.yolbi.module.impl.visual.mobends.util.EnumAxis;
import cn.yapeteam.yolbi.module.impl.visual.mobends.util.SmoothVector3f;

import java.util.ArrayList;
import java.util.List;

public class BendsTarget {
	public String mob;
	public List<BendsAction> actions = new ArrayList<>();
	public float visual_DeletePopUp;
	
	public BendsTarget(String argMob) {
		this.mob = argMob;
		this.visual_DeletePopUp = 0;
	}
	
	public void applyToModel(ModelRendererBends box, String anim, String model) {
		for (int i = 0;i < actions.size();i++) {
			if ((actions.get(i).anim.equalsIgnoreCase(anim) | actions.get(i).anim.equalsIgnoreCase("all")) &
			   actions.get(i).model.equalsIgnoreCase(model)) {
				if (actions.get(i).prop == BendsAction.EnumBoxProperty.ROT) {
					box.rotation.setSmooth(actions.get(i).axis,actions.get(i).getNumber((actions.get(i).axis == EnumAxis.X ? box.rotation.vFinal.x : actions.get(i).axis == EnumAxis.Y ? box.rotation.vFinal.y : box.rotation.vFinal.z)),actions.get(i).smooth);
				} else if (actions.get(i).prop == BendsAction.EnumBoxProperty.PREROT) {
					box.pre_rotation.setSmooth(actions.get(i).axis,actions.get(i).getNumber((actions.get(i).axis == EnumAxis.X ? box.pre_rotation.vFinal.x : actions.get(i).axis == EnumAxis.Y ? box.pre_rotation.vFinal.y : box.pre_rotation.vFinal.z)),actions.get(i).smooth);
				} else if (actions.get(i).prop == BendsAction.EnumBoxProperty.SCALE) {
					if (actions.get(i).axis == null | actions.get(i).axis == EnumAxis.X) box.scaleX = actions.get(i).getNumber(box.scaleX);
					if (actions.get(i).axis == null | actions.get(i).axis == EnumAxis.Y) box.scaleY = actions.get(i).getNumber(box.scaleY);
					if (actions.get(i).axis == null | actions.get(i).axis == EnumAxis.Z) box.scaleZ = actions.get(i).getNumber(box.scaleZ);
				}
			}
		}
	}
	
	public void applyToModel(SmoothVector3f box, String anim, String model) {
		for (int i = 0;i < actions.size();i++) {
			if ((actions.get(i).anim.equalsIgnoreCase(anim) | actions.get(i).anim.equalsIgnoreCase("all")) &
			   actions.get(i).model.equalsIgnoreCase(model)) {
				if (actions.get(i).prop == BendsAction.EnumBoxProperty.ROT) {
					box.setSmooth(actions.get(i).axis,actions.get(i).getNumber((actions.get(i).axis == EnumAxis.X ? box.vFinal.x : actions.get(i).axis == EnumAxis.Y ? box.vFinal.y : box.vFinal.z)),actions.get(i).smooth);
				}
			}
		}
	}
}
