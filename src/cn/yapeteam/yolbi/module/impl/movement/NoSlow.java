package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketReceiveEvent;
import cn.yapeteam.yolbi.event.impl.network.PacketSendEvent;
import cn.yapeteam.yolbi.event.impl.player.MotionEvent;
import cn.yapeteam.yolbi.event.impl.player.PostMotionEvent;
import cn.yapeteam.yolbi.event.impl.player.SlowdownEvent;
import cn.yapeteam.yolbi.event.impl.player.UpdateEvent;
import cn.yapeteam.yolbi.event.impl.render.ItemRenderEvent;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.module.impl.combat.KillAura;
import cn.yapeteam.yolbi.util.misc.LogUtil;
import cn.yapeteam.yolbi.util.misc.TimerUtil;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import cn.yapeteam.yolbi.util.player.MovementUtil;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Mouse;

import java.util.LinkedList;

@ModuleInfo(name = "NoSlow", category = ModuleCategory.MOVEMENT)
public class NoSlow extends Module {
    private final ModeValue<String> swordMethod = new ModeValue<>("Sword method", "Vanilla", "Vanilla", "NCP", "AAC4", "AAC5", "Spoof", "Spoof2", "Blink", "GrimAC", "GrimACSwitch", "Intave", "None");
    private final ModeValue<String> consumableMethod = new ModeValue<>("Comsumable method", "Vanilla", "Vanilla", "Hypixel", "AAC4", "AAC5", "GrimAC", "GrimACSwitch", "Intave", "None");

    private final NumberValue<Double> forward = new NumberValue<>("Forward", 1.0, 0.2, 1.0, 0.05);
    private final NumberValue<Double> strafe = new NumberValue<>("Strafe", 1.0, 0.2, 1.0, 0.05);

    private final NumberValue<Integer> blinkTicks = new NumberValue<>("Blink ticks", () -> swordMethod.is("Blink"), 5, 2, 10, 1);

    public final BooleanValue allowSprinting = new BooleanValue("Allow sprinting", true);

    private KillAura killauraModule;

    private boolean lastUsingItem;
    private TimerUtil msTimer = new TimerUtil();
    private boolean nextTemp = false;
    private LinkedList packetBuf = new LinkedList<Packet<INetHandlerPlayServer>>();

    private int ticks;

    private int lastSlot;

    private boolean wasEating;
    private boolean lastBlockingStat = false;
    private long delay = 100L; // for intave mode
    private boolean funnyBoolean = false; // for intave mode


    public NoSlow() {
        this.addValues(swordMethod, consumableMethod, forward, strafe, blinkTicks, allowSprinting);
    }

    @Override
    public void onEnable() {
        lastUsingItem = wasEating = false;
        nextTemp = false;
        lastSlot = mc.thePlayer.inventory.currentItem;
        msTimer.reset();
        ticks = 0;
    }

    @Override
    public void onDisable() {
        nextTemp = false;
        YolBi.instance.getPacketBlinkHandler().stopAll();
    }

    @Override
    public void onClientStarted() {
        killauraModule = YolBi.instance.getModuleManager().getModule(KillAura.class);
    }

    @Listener
    public void onUpdate(UpdateEvent event) {
        if (isUsingItem()) {
            if (isBlocking()) {
                switch (swordMethod.getValue()) {
                    case "NCP":
                        PacketUtil.releaseUseItem(true);
                        break;
                    case "AAC4":
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            PacketUtil.releaseUseItem(true);
                        }
                        break;
                    case "AAC5":
                        if (lastUsingItem) {
                            PacketUtil.sendBlocking(true, false);
                        }
                        break;
                    case "Spoof":
                        int slot = mc.thePlayer.inventory.currentItem;

                        PacketUtil.sendPacket(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
                        PacketUtil.sendPacket(new C09PacketHeldItemChange(slot));

                        if (lastUsingItem) {
                            PacketUtil.sendBlocking(true, false);
                        }
                        break;
                    case "Spoof2":
                        PacketUtil.sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        break;
                    case "GrimAC":
                        if (msTimer.getTimeElapsed() >= 230l && nextTemp) {
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            if (!packetBuf.isEmpty()) {
                                boolean canAttack = false;
                                for (Object packet : packetBuf) {
                                    if ((Packet) packet instanceof C03PacketPlayer) {
                                        canAttack = true;
                                    }
                                    if (!(((Packet) packet instanceof C02PacketUseEntity || (Packet) packet instanceof C0APacketAnimation) && !canAttack)) {
                                        PacketUtil.sendPacketNoEvent((Packet) packet);
                                    }
                                }
                                packetBuf.clear();
                            }
                        }
                        if (!nextTemp) {
                            lastBlockingStat = mc.thePlayer.isBlocking();
                            if (!mc.thePlayer.isBlocking()) {
                                return;
                            }
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                            nextTemp = true;
                            msTimer.reset();
                        }

                        break;
                }
            } else {
                switch (consumableMethod.getValue()) {
                    case "AAC4":
                        if (lastUsingItem) {
                            int slot = mc.thePlayer.inventory.currentItem;

                            PacketUtil.sendPacket(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
                            PacketUtil.sendPacket(new C09PacketHeldItemChange(slot));
                        }
                        break;
                    case "AAC5":
                        if (lastUsingItem) {
                            PacketUtil.sendBlocking(true, false);
                        }
                        break;
                    case "GrimAC":
                        if (msTimer.getTimeElapsed() >= 230l && nextTemp) {
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                            PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                            if (!packetBuf.isEmpty()) {
                                boolean canAttack = false;
                                for (Object packet : packetBuf) {
                                    if ((Packet) packet instanceof C03PacketPlayer) {
                                        canAttack = true;
                                    }
                                    if (!(((Packet) packet instanceof C02PacketUseEntity || (Packet) packet instanceof C0APacketAnimation) && !canAttack)) {
                                        PacketUtil.sendPacketNoEvent((Packet) packet);
                                    }
                                }
                                packetBuf.clear();
                            }
                        }
                        if (!nextTemp) {
                            lastBlockingStat = mc.thePlayer.isUsingItem();
                            if (!mc.thePlayer.isBlocking()) {
                                return;
                            }
                            PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                            nextTemp = true;
                            msTimer.reset();
                        }

                        break;
                }
            }
        }

        if (swordMethod.is("Blink")) {
            if (isHoldingSword() && pressingUseItem()) {
                if (ticks == 1) {
                    YolBi.instance.getPacketBlinkHandler().releaseAll();
                    YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
                }

                if (ticks > 0 && ticks < blinkTicks.getValue()) {
                    mc.gameSettings.keyBindUseItem.setPressed(false);
                }

                if (ticks == blinkTicks.getValue()) {
                    YolBi.instance.getPacketBlinkHandler().stopAll();

                    mc.gameSettings.keyBindUseItem.setPressed(true);

                    ticks = 0;
                }

                ticks++;
            } else {
                YolBi.instance.getPacketBlinkHandler().stopAll();

                ticks = 0;
            }
        }

        if (consumableMethod.is("Hypixel")) {
            if (wasEating && mc.thePlayer.isUsingItem()) {
                YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
                mc.gameSettings.keyBindUseItem.setPressed(false);
                ticks = 32;

                lastSlot = mc.thePlayer.inventory.currentItem;

                mc.thePlayer.inventory.currentItem = (lastSlot + 1) % 8;
                YolBi.instance.getSlotSpoofHandler().startSpoofing(lastSlot);
            }

            if (ticks == 31) {
                mc.thePlayer.inventory.currentItem = lastSlot;
                YolBi.instance.getSlotSpoofHandler().stopSpoofing();
            }

            if (ticks > 1) {
                mc.gameSettings.keyBindUseItem.setPressed(false);

                if (!Mouse.isButtonDown(1)) {
                    ticks = 2;
                }
            } else if (ticks == 1) {
                YolBi.instance.getPacketBlinkHandler().stopAll();
                LogUtil.addChatMessage("Stopped eating");
            }

            ticks--;
        }
    }

    @Listener
    public void onPostMotion(PostMotionEvent event) {
        boolean usingItem = mc.thePlayer.isUsingItem();
        if (usingItem) {
            if (isBlocking()) {
                switch (swordMethod.getValue()) {
                    case "NCP":
                        if (isBlocking()) {
                            PacketUtil.sendBlocking(true, false);
                        }
                        break;
                    case "AAC4":
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            PacketUtil.sendBlocking(true, false);
                        }
                        break;
                    case "Intave":

                        if (msTimer.delay(delay)) {
                            delay = 200L;
                            if (funnyBoolean) {
                                delay = 100L;
                                funnyBoolean = false;
                            } else
                                funnyBoolean = true;
                            msTimer.reset();
                        }


//                        }
                        break;
                }
            } else {
                switch (consumableMethod.getValue()) {
                    case "Intave":

                        if (msTimer.delay(delay)) {
                            mc.playerController.syncCurrentPlayItem();
                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                    BlockPos.ORIGIN,
                                    EnumFacing.DOWN));
                            delay = 200L;
                            if (funnyBoolean) {
                                delay = 100L;
                                funnyBoolean = false;
                            } else
                                funnyBoolean = true;
                            msTimer.reset();
                        }

                        break;
                }

            }
        }


        lastUsingItem = usingItem;

        wasEating = usingItem && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.getHeldItem().getItem() instanceof ItemFood || mc.thePlayer.getHeldItem().getItem() instanceof ItemPotion);
    }

    @Listener
    public void onSlowdown(SlowdownEvent event) {
        if (!((isBlocking() && swordMethod.is("None")) || (!isBlocking() && consumableMethod.is("None")))) {
            event.setForward(forward.getValue().floatValue());
            event.setStrafe(strafe.getValue().floatValue());
            event.setAllowedSprinting(allowSprinting.getValue());
        }
    }

    @Listener
    public void onSend(PacketSendEvent event) {
        if (event.getPacket() instanceof C07PacketPlayerDigging) {
            C07PacketPlayerDigging packet = event.getPacket();

            if (packet.getStatus() == C07PacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                if (isHoldingSword() && swordMethod.is("Spoof")) {
                    event.setCancelled(true);

                    int slot = mc.thePlayer.inventory.currentItem;

                    PacketUtil.sendPacketFinal(new C09PacketHeldItemChange(slot < 8 ? slot + 1 : 0));
                    PacketUtil.sendPacketFinal(new C09PacketHeldItemChange(slot));
                }
            }
        }
    }

    @Listener
    public void onReceive(PacketReceiveEvent event) {
        Packet packet = event.getPacket();
        if (swordMethod.is("GrimAC") || consumableMethod.is("GrimAC")) {
            if (nextTemp) {
                if ((packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) && mc.thePlayer.getHeldItem() != null && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Listener
    public void onItemRender(ItemRenderEvent event) {
        if (isHoldingSword() && pressingUseItem() && swordMethod.is("Blink")) {
            event.setRenderBlocking(true);
        }

        if (consumableMethod.is("Hypixel") && ticks > 1) {
            event.setRenderBlocking(true);
        }
    }

    @Listener
    public void onMotion(MotionEvent event) {
        if (isUsingItem() && MovementUtil.isMoving()) {
            if (isBlocking()) {
                switch (swordMethod.getValue()) {
                    case "GrimACSwitch":
                        int cItem = mc.thePlayer.inventory.currentItem;
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange((cItem + 1) % 9));
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(cItem));
                        break;
                    case "Intave":
                        if (msTimer.delay(delay)) {
                            //mc.playerController.syncCurrentPlayItem();
                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                    BlockPos.ORIGIN,
                                    EnumFacing.DOWN));
                        }
                        break;
                }
            } else {
                switch (consumableMethod.getValue()) {
                    case "GrimACSwitch":
                        int cItem = mc.thePlayer.inventory.currentItem;
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange((cItem + 1) % 9));
                        PacketUtil.sendPacketNoEvent(new C09PacketHeldItemChange(cItem));
                        break;
                    case "Intave":
                        if (msTimer.delay(delay)) {
                            mc.playerController.syncCurrentPlayItem();
                            PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                                    BlockPos.ORIGIN,
                                    EnumFacing.DOWN));
                        }
                        break;
                }

            }
        }
    }


    public boolean isBlocking() {
        return mc.thePlayer.isUsingItem() && isHoldingSword();
    }

    public boolean isUsingItem() {
        return mc.thePlayer.isUsingItem() && !(killauraModule.isEnabled() && killauraModule.getTarget() != null && !killauraModule.autoblock.is("None"));
    }

    public boolean isHoldingSword() {
        return mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public boolean pressingUseItem() {
        return !(mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest) && Mouse.isButtonDown(1);
    }

}