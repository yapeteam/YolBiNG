package cn.yapeteam.yolbi.module.impl.movement;

import cn.yapeteam.yolbi.event.impl.EntityActionEvent;
import cn.yapeteam.yolbi.event.impl.TickEvent;
import cn.yapeteam.yolbi.event.impl.UpdateEvent;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import cn.yapeteam.yolbi.Vestige;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.Priority;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.Module;

public class InventoryMove extends Module {

    private final ModeValue noSprint = new ModeValue("No sprint", "Disabled", "Disabled", "Enabled", "Spoof");
    private final BooleanValue blink = new BooleanValue("Blink", false);

    private boolean hadInventoryOpened;

    private boolean blinking;

    public InventoryMove() {
        super("Inventory Move", ModuleCategory.MOVEMENT);
        this.addValues(noSprint, blink);
    }

    @Override
    public void onDisable() {
        if(blinking) {
            Vestige.instance.getPacketBlinkHandler().stopAll();
            blinking = false;
        }
    }

    @Listener(Priority.LOW)
    public void onTick(TickEvent event) {
        if(isInventoryOpened()) {
            allowMove();

            if(noSprint.is("Enabled")) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            }

            if(blink.getValue()) {
                Vestige.instance.getPacketBlinkHandler().startBlinkingAll();
                blinking = true;
            }
        } else {
            if(blinking) {
                Vestige.instance.getPacketBlinkHandler().stopAll();
                blinking = false;
            }

            if(hadInventoryOpened) {
                allowMove();
                hadInventoryOpened = false;
            }
        }
    }

    @Listener(Priority.LOW)
    public void onUpdate(UpdateEvent event) {
        if(isInventoryOpened()) {
            allowMove();

            if(noSprint.is("Enabled")) {
                mc.gameSettings.keyBindSprint.pressed = false;
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @Listener(Priority.LOW)
    public void onEntityAction(EntityActionEvent event) {
        if(isInventoryOpened()) {
            allowMove();

            if(noSprint.is("Spoof")) {
                event.setSprinting(false);
            }
        }
    }

    private boolean isInventoryOpened() {
        return mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiChest;
    }

    private void allowMove() {
        GameSettings settings = mc.gameSettings;
        KeyBinding keys[] = {settings.keyBindForward, settings.keyBindBack, settings.keyBindLeft, settings.keyBindRight, settings.keyBindJump};

        for(KeyBinding key : keys) {
            key.pressed = Keyboard.isKeyDown(key.getKeyCode());
        }

        hadInventoryOpened = true;
    }

}
