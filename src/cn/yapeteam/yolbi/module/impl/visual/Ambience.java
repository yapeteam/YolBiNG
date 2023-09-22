package cn.yapeteam.yolbi.module.impl.visual;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.event.impl.render.Render3DEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ColorValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;

import java.awt.*;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.visual
 * don't mind
 * @date 2023/8/22 22:03
 */

@Getter
@Setter
@ModuleInfo(name = "Ambience", category = ModuleCategory.VISUAL)
public class Ambience extends Module {
    private static Ambience ambience;

    public static Ambience getInstance() {
        return ambience;
    }

    private final NumberValue<Integer> time = new NumberValue("Time", 0, 0, 22999, 1);
    private final NumberValue<Integer> speed = new NumberValue("Time Speed", 0, 0, 20, 1);

    private final ModeValue<String> weather = new ModeValue<>("Weather", "Unchanged",
            "Unchanged", "Clear", "Rain", "Heavy Snow", "Light Snow", "Nether Particles");

    private final BooleanValue NetherFog = new BooleanValue("NetherFog", () -> weather.is("Nether Particles"), true);
    private final ColorValue NetherFogColor = new ColorValue("NetherFogColor", () -> (NetherFog.getValue() && weather.is("Nether Particles")), new Color(255, 255, 255).getRGB());

    private final ColorValue snowColor = new ColorValue("snowColor", () -> (weather.is("Heavy Snow") || weather.is("Light Snow")), new Color(255, 255, 255).getRGB());

    public Ambience() {
        this.addValues(time, speed, weather, snowColor, NetherFog, NetherFogColor);
        ambience = this;
    }

    @Override
    protected void onDisable() {
        mc.theWorld.setRainStrength(0);
        mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
        mc.theWorld.getWorldInfo().setRainTime(0);
        mc.theWorld.getWorldInfo().setThunderTime(0);
        mc.theWorld.getWorldInfo().setRaining(false);
        mc.theWorld.getWorldInfo().setThundering(false);
    }

    @Listener
    protected void onRender3D(Render3DEvent event) {
        mc.theWorld.setWorldTime((time.getValue().intValue() + (System.currentTimeMillis() * speed.getValue().intValue())));
    }

    @Listener
    protected void onPreMotionEvent(MotionEvent event) {
        if (mc.thePlayer.ticksExisted % 20 == 0) {

            switch (this.weather.getValue()) {
                case "Clear": {
                    mc.theWorld.setRainStrength(0);
                    mc.theWorld.getWorldInfo().setCleanWeatherTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setRainTime(0);
                    mc.theWorld.getWorldInfo().setThunderTime(0);
                    mc.theWorld.getWorldInfo().setRaining(false);
                    mc.theWorld.getWorldInfo().setThundering(false);
                    break;
                }
                case "Nether Particles":
                case "Light Snow":
                case "Heavy Snow":
                case "Rain": {
                    mc.theWorld.setRainStrength(1);
                    mc.theWorld.getWorldInfo().setCleanWeatherTime(0);
                    mc.theWorld.getWorldInfo().setRainTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setThunderTime(Integer.MAX_VALUE);
                    mc.theWorld.getWorldInfo().setRaining(true);
                    mc.theWorld.getWorldInfo().setThundering(false);
                }
            }
        }
    }

    @Listener
    protected void onPacketReceiveEvent(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        } else if (event.getPacket() instanceof S2BPacketChangeGameState && !this.weather.is("Unchanged")) {
            S2BPacketChangeGameState s2b = event.getPacket();

            if (s2b.getGameState() == 1 || s2b.getGameState() == 2) {
                event.setCancelled(true);
            }
        }
    }

    public float getFloatTemperature(BlockPos blockPos, BiomeGenBase biomeGenBase) {
        if (this.isEnabled()) {
            switch (this.weather.getValue()) {
                case "Nether Particles":
                case "Light Snow":
                case "Heavy Snow":
                    return 0.1F;
                case "Rain":
                    return 0.2F;
            }
        }

        return biomeGenBase.getFloatTemperature(blockPos);
    }

    public boolean skipRainParticles() {
        final String name = this.weather.getValue();
        return this.isEnabled() && name.equals("Light Snow") || name.equals("Heavy Snow") || name.equals("Nether Particles");
    }


}
