package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.network.PacketSendEvent;
import cn.yapeteam.yolbi.event.impl.player.PostMotionEvent;
import cn.yapeteam.yolbi.event.impl.player.SlowdownEvent;
import cn.yapeteam.yolbi.event.impl.player.UpdateEvent;
import cn.yapeteam.yolbi.event.impl.render.ItemRenderEvent;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.impl.combat.Killaura;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.util.misc.LogUtil;
import cn.yapeteam.yolbi.util.network.PacketUtil;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import org.lwjgl.input.Mouse;

public class Noslow extends Module {

    private final ModeValue<String> swordMethod = new ModeValue<>("Sword method", "Vanilla", "Vanilla", "NCP", "AAC4", "AAC5", "Spoof", "Spoof2", "Blink", "None");
    private final ModeValue<String> consumableMethod = new ModeValue<>("Comsumable method", "Vanilla", "Vanilla", "Hypixel", "AAC4", "AAC5", "None");

    private final NumberValue<Double> forward = new NumberValue<>("Forward", 1.0, 0.2, 1.0, 0.05);
    private final NumberValue<Double> strafe = new NumberValue<>("Strafe", 1.0, 0.2, 1.0, 0.05);

    private final NumberValue<Integer> blinkTicks = new NumberValue<>("Blink ticks", () -> swordMethod.is("Blink"), 5, 2, 10, 1);

    public final BooleanValue allowSprinting = new BooleanValue("Allow sprinting", true);

    private Killaura killauraModule;

    private boolean lastUsingItem;
    private int ticks;

    private int lastSlot;

    private boolean wasEating;

    public Noslow() {
        super("Noslow", ModuleCategory.MOVEMENT);
        this.addValues(swordMethod, consumableMethod, forward, strafe, blinkTicks, allowSprinting);
    }

    @Override
    public void onEnable() {
        lastUsingItem = wasEating = false;
        lastSlot = mc.thePlayer.inventory.currentItem;

        ticks = 0;
    }

    @Override
    public void onDisable() {
        YolBi.instance.getPacketBlinkHandler().stopAll();
    }

    @Override
    public void onClientStarted() {
        killauraModule = YolBi.instance.getModuleManager().getModule(Killaura.class);
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
    public void onItemRender(ItemRenderEvent event) {
        if (isHoldingSword() && pressingUseItem() && swordMethod.is("Blink")) {
            event.setRenderBlocking(true);
        }

        if (consumableMethod.is("Hypixel") && ticks > 1) {
            event.setRenderBlocking(true);
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