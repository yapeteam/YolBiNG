package cn.yapeteam.yolbi.module.impl.player;

import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.EventPacketReceive;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.InventoryUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;

import java.util.Random;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.module.impl.player
 * don't mind
 * @date 2023/8/23 10:38
 */
@ModuleInfo(name = "AutoGapple", category = ModuleCategory.PLAYER)
public class AutoGapple extends Module {
    private final ModeValue<String> modeValue = new ModeValue<>("Mode", "Auto", "Auto", "LegitAuto", "Legit", "Head");
    private final NumberValue<Float> percent = new NumberValue<>("HealthPercent", 75.0f, 1.0f, 100f, 1f);
    private final NumberValue<Integer> min = new NumberValue<>("MinDelay", 75, 1, 5000, 1);
    private final NumberValue<Integer> max = new NumberValue<>("MaxDelay", 125, 1, 5000, 1);
    private final NumberValue<Float> regenSec = new NumberValue<>("MinRegenSec", 4.6f, 0.0f, 10f, 0.1f);
    private final BooleanValue groundCheck = new BooleanValue("OnlyOnGround", false);
    private final BooleanValue waitRegen = new BooleanValue("WaitRegen", false);
    private final BooleanValue invCheck = new BooleanValue("InvCheck", false);
    private final BooleanValue absorpCheck = new BooleanValue("NoAbsorption", false);

    private final TimerUtil timer = new TimerUtil();
    private int eating = -1;
    private long delay = 0;
    private boolean isDisable = false;
    private boolean tryHeal = false;
    private int prevSlot = -1;
    private boolean switchBack = false;

    public AutoGapple() {
        min.setCallback((oldV, newV) -> newV > max.getValue() ? oldV : newV);
        max.setCallback((oldV, newV) -> newV < min.getValue() ? oldV : newV);
        this.addValues(modeValue, percent, min, max, regenSec, groundCheck, waitRegen, invCheck, absorpCheck);
    }

    @Override
    protected void onEnable() {
        eating = -1;
        prevSlot = -1;
        switchBack = false;
        timer.reset();
        isDisable = false;
        tryHeal = false;
        delay = MathHelper.getRandomIntegerInRange(new Random(), min.getValue(), max.getValue());
    }

    @Listener
    protected void onPacket(EventPacketReceive event) {
        final Packet packet = event.getPacket();
        if (eating != -1 && packet instanceof C03PacketPlayer) {
            eating++;
        } else if (packet instanceof C03PacketPlayer || packet instanceof C09PacketHeldItemChange) {
            eating = -1;
        }
    }

    @Listener
    protected void onUpdate(EventUpdate event) {
        if (tryHeal) {
            int gappleInHotbar;
            try {
                gappleInHotbar = InventoryUtil.findItem(36, 45, Items.golden_apple);
            } catch (IndexOutOfBoundsException exception) {
                gappleInHotbar = -1;
            }
            if (gappleInHotbar == -1) {
                tryHeal = false;
                return;
            }
            switch (modeValue.getValue().toLowerCase()) {
                case "auto": {
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(gappleInHotbar - 36));
                    PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                    for (int i = 1; i <= 35; i++) {
                        //发送C03达到快速吃苹果的目的
                        PacketUtil.sendPacketNoEvent(new C03PacketPlayer(mc.thePlayer.onGround));
                    }
                    PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                    //alert("Gapple eaten");

                    tryHeal = false;
                    timer.reset();
                    delay = MathHelper.getRandomIntegerInRange(new Random(), min.getValue(), max.getValue());
                    break;
                }
                case "legitauto": {
                    if (eating == -1) {
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(gappleInHotbar - 36));
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        eating = 0;
                    } else if (eating > 35) {
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        timer.reset();
                        tryHeal = false;
                        delay = MathHelper.getRandomIntegerInRange(new Random(), min.getValue(), max.getValue());
                    }
                    break;
                }
                case "legit": {
                    if (eating == -1) {
                        if (prevSlot == -1)
                            prevSlot = mc.thePlayer.inventory.currentItem;

                        mc.thePlayer.inventory.currentItem = gappleInHotbar - 36;
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        eating = 0;
                    } else if (eating > 35) {
                        timer.reset();
                        tryHeal = false;
                        delay = MathHelper.getRandomIntegerInRange(new Random(), min.getValue(), max.getValue());
                    }
                    break;
                }
                case "head": {
                    int headInHotbar = InventoryUtil.findItem(36, 45, Items.skull);
                    if (headInHotbar != -1) {
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(headInHotbar - 36));
                        PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        timer.reset();
                        tryHeal = false;
                        delay = MathHelper.getRandomIntegerInRange(new Random(), min.getValue(), max.getValue());
                    } else {
                        tryHeal = false;
                    }
                    break;
                }
            }


        }
        if (mc.thePlayer.ticksExisted <= 10 && isDisable) {
            isDisable = false;
        }

        int absorp = MathHelper.ceiling_double_int(mc.thePlayer.getAbsorptionAmount());


        if (!tryHeal && prevSlot != -1) {
            if (!switchBack) {
                switchBack = true;
                return;
            }
            mc.thePlayer.inventory.currentItem = prevSlot;
            prevSlot = -1;
            switchBack = false;
        }

        if ((groundCheck.getValue() && !mc.thePlayer.onGround) || (invCheck.getValue() && mc.currentScreen instanceof GuiContainer) || (absorp > 0 && absorpCheck.getValue()))
            return;
        if (waitRegen.getValue() && mc.thePlayer.isPotionActive(Potion.regeneration) && mc.thePlayer.getActivePotionEffect(Potion.regeneration).getDuration() > regenSec.getValue() * 20.0f)
            return;
        if (!isDisable && (mc.thePlayer.getHealth() <= (percent.getValue() / 100.0f) * mc.thePlayer.getMaxHealth()) && timer.delay(delay)) {
            if (tryHeal)
                return;
            tryHeal = true;
        }
    }


}
