package cn.yapeteam.yolbi;

import cn.yapeteam.yolbi.anticheat.Anticheat;
import cn.yapeteam.yolbi.command.CommandManager;
import cn.yapeteam.yolbi.event.EventManager;
import cn.yapeteam.yolbi.filesystem.FileSystem;
import cn.yapeteam.yolbi.font.FontManager;
import cn.yapeteam.yolbi.handler.client.BalanceHandler;
import cn.yapeteam.yolbi.handler.client.CameraHandler;
import cn.yapeteam.yolbi.handler.client.KeybindHandler;
import cn.yapeteam.yolbi.handler.client.SlotSpoofHandler;
import cn.yapeteam.yolbi.handler.packet.PacketBlinkHandler;
import cn.yapeteam.yolbi.handler.packet.PacketDelayHandler;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.module.ModuleManager;
import cn.yapeteam.yolbi.ui.menu.ConfigMenu;
import cn.yapeteam.yolbi.ui.noti.NotificationManager;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.util.render.FontUtil;
import de.florianmichael.viamcp.ViaMCP;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/*
 *                        _oo0oo_
 *                       o8888888o
 *                       88" . "88
 *                       (| -_- |)
 *                       0\  =  /0
 *                     ___/`---'\___
 *                   .' \\|     |// '.
 *                  / \\|||  :  |||// \
 *                 / _||||| -:- |||||- \
 *                |   | \\\  - /// |   |
 *                | \_|  ''\---/''  |_/ |
 *                \  .-\__  '-'  ___/-. /
 *              ___'. .'  /--.--\  `. .'___
 *           ."" '<  `.___\_<|>_/___.' >' "".
 *          | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *          \  \ `_.   \_ __\ /__ _/   .-` /  /
 *      =====`-.____`.___ \_____/___.-`___.-'=====
 *                        `=---='
 *
 *
 *      ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 *
 *            佛祖保佑     永不宕机     永无BUG
 */
@Getter
public class YolBi implements IMinecraft {

    public static final YolBi instance = new YolBi();

    public final String name = "YolBi";
    public final String version = "2.1";
    public boolean haveGoinTheConfig = false;

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private CommandManager commandManager;
    private NotificationManager notificationManager;

    private PacketDelayHandler packetDelayHandler;
    private PacketBlinkHandler packetBlinkHandler;
    private KeybindHandler keybindHandler;
    private BalanceHandler balanceHandler;
    private CameraHandler cameraHandler;
    private SlotSpoofHandler slotSpoofHandler;

    private Anticheat anticheat;

    private FileSystem fileSystem;

    private FontManager fontManager;

    @Setter
    private boolean destructed;

    public void start() throws IOException {
        eventManager = new EventManager();
        moduleManager = new ModuleManager();
        commandManager = new CommandManager();
        notificationManager = new NotificationManager();

        packetDelayHandler = new PacketDelayHandler();
        packetBlinkHandler = new PacketBlinkHandler();

        keybindHandler = new KeybindHandler();
        balanceHandler = new BalanceHandler();
        slotSpoofHandler = new SlotSpoofHandler();

        cameraHandler = new CameraHandler();

        anticheat = new Anticheat();

        fileSystem = new FileSystem();

        fontManager = new FontManager();

        fileSystem.loadDefaultConfig();
        fileSystem.loadKeybinds();

        moduleManager.modules.forEach(Module::onClientStarted);

        FontUtil.initFonts();

        try {
            ViaMCP.create();

            // In case you want a version slider like in the Minecraft options, you can use
            // this code here, please choose one of those:
            ViaMCP.INSTANCE.initAsyncSlider(); // For top left aligned slider
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        if (!destructed) {
            instance.fileSystem.saveDefaultConfig();
            instance.fileSystem.saveKeybinds();
        }
    }

    public GuiScreen getMainMenu() {
        if (!YolBi.instance.haveGoinTheConfig)
            return new ConfigMenu();
        return destructed ? new GuiMainMenu() : new cn.yapeteam.yolbi.ui.mainmenu.ImplScreen();
    }
}