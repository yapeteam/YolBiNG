package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/12/15 22:12
 */

//@ModuleInfo(name = "ShoulderSurfing", category = ModuleCategory.VISUAL)
public class ShoulderSurfing extends Module {
//    public static final String NAME = "Shoulder Surfing";
//    public static final String MODID = "shouldersurfing";
//    public static final String MC_VERSION = "1.8.9";
//    public static final String VERSION = "2.2.19";
//    public static final String DEVELOPERS = "Joshua Powers, Exopandora (for 1.8+)";
//    public static final String CERTIFICATE = "d6261bb645f41db84c74f98e512c2bb43f188af2";
    private static final Predicate<Entity> ENTITY_IS_PICKABLE = Predicates.and(EntitySelectors.NOT_SPECTATING, entity -> entity != null && entity.canBeCollidedWith());

    private static final Vec3 ZERO = new Vec3(0, 0, 0);
    private final NumberValue<Integer> PointOfView = new NumberValue<>("PointOfView",1,0,10,1);
    private final NumberValue<Double> OffsetX = new NumberValue<>("OffsetX",0d,0d,360d,0.1d);
    private final NumberValue<Double> OffsetY = new NumberValue<>("OffsetY",0d,0d,360d,0.1d);
    private final NumberValue<Double> OffsetZ = new NumberValue<>("OffsetZ",0d,0d,360d,0.1d);
    private final BooleanValue limitPlayerReach = new BooleanValue("limitPlayerReach",false);



    public ShoulderSurfing() {
        this.addValues(PointOfView,OffsetX,OffsetY,OffsetZ,limitPlayerReach);
    }

    @Listener
    public void onRender3D(EventRender3D eventRender3D){

    }




}
