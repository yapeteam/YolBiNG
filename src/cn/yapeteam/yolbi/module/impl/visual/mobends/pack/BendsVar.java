package cn.yapeteam.yolbi.module.impl.visual.mobends.pack;


import cn.yapeteam.yolbi.module.impl.visual.mobends.data.EntityData;

public class BendsVar {
	public static EntityData tempData;
	
	public static float getGlobalVar(String name){
		if(name.equalsIgnoreCase("ticks")){
			if(tempData == null)
				return 0;
			return tempData.ticks;
		} else if (name.equalsIgnoreCase("ticksAfterPunch")){
			if(tempData == null)
				return 0;
			return tempData.ticksAfterPunch;
		}
		return Float.POSITIVE_INFINITY;
	}
}
