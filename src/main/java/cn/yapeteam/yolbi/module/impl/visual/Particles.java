package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.player.EventAttack;
import cn.yapeteam.yolbi.event.impl.player.EventMotion;
import cn.yapeteam.yolbi.event.impl.render.EventRender3D;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.particles.EvictingList;
import cn.yapeteam.yolbi.util.particles.Particle;
import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;

import java.util.List;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/8/22 16:51
 */
// skid from FDPClient
@ModuleInfo(name = "Particles", category = ModuleCategory.VISUAL)
public class Particles extends Module {
    private final NumberValue<Integer> amount = new NumberValue<>("Amount", 10, 1, 50, 1);
    private final BooleanValue physics = new BooleanValue("Physics", true);

    private final List<Particle> particles = new EvictingList<>(100);
    private final TimerUtil timer = new TimerUtil();
    private EntityLivingBase target;

    public Particles() {
        this.addValues(amount, physics);
    }

    @Listener
    public void onAttack(final EventAttack event) {
        if (event.getTargetEntity() instanceof EntityLivingBase) {
            target = (EntityLivingBase) event.getTargetEntity();
        }
    }

    @Listener
    public void onMotion(final EventMotion event) {
        if (target != null && target.hurtTime >= 9 && mc.thePlayer.getDistance(target.posX, target.posY, target.posZ) < 10) {
            for (int i = 0; i <= amount.getValue(); i++)
                particles.add(new Particle(new Vec3(target.posX + (Math.random() - 0.5) * 0.5, target.posY + Math.random() * 1 + 0.5, target.posZ + (Math.random() - 0.5) * 0.5)));
            target = null;
        }
    }

    @Listener
    public void onRender3D(final EventRender3D event) {
        if (particles.isEmpty())
            return;

        for (int i = 0; i <= timer.getTimeElapsed() / 1E+11; i++) {
            if (physics.getValue())
                particles.forEach(Particle::update);
            else
                particles.forEach(Particle::updateWithoutPhysics);
        }

        particles.removeIf(particle -> mc.thePlayer.getDistanceSq(particle.position.xCoord, particle.position.yCoord, particle.position.zCoord) > 50 * 10);

        timer.reset();

        RenderUtil.renderParticles(particles);
    }
}
