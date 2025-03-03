package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.event.impl.game.EventTick;
import cn.yapeteam.yolbi.event.impl.player.EventEntityAction;
import cn.yapeteam.yolbi.event.impl.player.EventUpdate;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.ModuleInfo;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

@ModuleInfo(name = "Inventory Move", category = ModuleCategory.MOVEMENT)
public class InventoryMove extends Module {

    private final ModeValue<String> noSprint = new ModeValue<>("No sprint", "Disabled", "Disabled", "Enabled", "Spoof");
    private final BooleanValue blink = new BooleanValue("Blink", false);

    private boolean hadInventoryOpened;

    private boolean blinking;

    public InventoryMove() {
        this.addValues(noSprint, blink);
    }

    @Override
    public void onDisable() {
        if (blinking) {
            YolBi.instance.getPacketBlinkHandler().stopAll();
            blinking = false;
        }
    }

    @Listener(Priority.LOW)
    public void onTick(EventTick event) {
        if (isInventoryOpened()) {
            allowMove();

            if (noSprint.is("Enabled")) {
                mc.gameSettings.keyBindSprint.setPressed(false);
                mc.thePlayer.setSprinting(false);
            }

            if (blink.getValue()) {
                YolBi.instance.getPacketBlinkHandler().startBlinkingAll();
                blinking = true;
            }
        } else {
            if (blinking) {
                YolBi.instance.getPacketBlinkHandler().stopAll();
                blinking = false;
            }

            if (hadInventoryOpened) {
                allowMove();
                hadInventoryOpened = false;
            }
        }
    }

    @Listener(Priority.LOW)
    public void onUpdate(EventUpdate event) {
        if (isInventoryOpened()) {
            allowMove();

            if (noSprint.is("Enabled")) {
                mc.gameSettings.keyBindSprint.setPressed(false);
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @Listener(Priority.LOW)
    public void onEntityAction(EventEntityAction event) {
        if (isInventoryOpened()) {
            allowMove();

            if (noSprint.is("Spoof")) {
                event.setSprinting(false);
            }
        }
    }

    private boolean isInventoryOpened() {
        return mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest;
    }

    private void allowMove() {
        GameSettings settings = mc.gameSettings;
        KeyBinding[] keys = {settings.keyBindForward, settings.keyBindBack, settings.keyBindLeft, settings.keyBindRight, settings.keyBindJump};

        for (KeyBinding key : keys) {
            key.setPressed(Keyboard.isKeyDown(key.getKeyCode()));
        }

        hadInventoryOpened = true;
    }
}
