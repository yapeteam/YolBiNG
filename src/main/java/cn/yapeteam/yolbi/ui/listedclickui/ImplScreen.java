package cn.yapeteam.yolbi.ui.listedclickui;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.ModuleCategory;
import cn.yapeteam.yolbi.module.impl.visual.ClickUI;
import cn.yapeteam.yolbi.module.impl.visual.ClientTheme;
import cn.yapeteam.yolbi.ui.listedclickui.component.AbstractComponent;
import cn.yapeteam.yolbi.ui.listedclickui.component.impl.Panel;
import cn.yapeteam.yolbi.util.render.ColorUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @author TIMER_err
 */
public class ImplScreen extends GuiScreen {
    @Getter
    private final CopyOnWriteArrayList<Panel> panels = new CopyOnWriteArrayList<>();
    public static final float
            panelStartX = 12, panelY = 22, panelWidth = 90, panelTopHeight = 17, panelMaxHeight = 200, panelSpacing = 5,
            moduleHeight = 19, moduleSpacing = 0.5f,
            valueSpacing = 0.5f,
            keyBindHeight = 10;
    public static Color[] MainTheme = new Color[]{new Color(26, 25, 26), new Color(31, 30, 31), new Color(5, 134, 105), new Color(38, 37, 38), new Color(45, 45, 45)};

    private boolean init = false;
    @Getter
    private Panel currentPanel = null;
    @Getter
    private static ClientTheme clientThemeModuleInstance;
    private static ClickUI guiModuleInstance;

    @Override
    public void initGui() {
        if (!init) {
            panels.clear();
            float x = panelStartX;
            for (ModuleCategory category : ModuleCategory.values()) {
                Panel panel = new Panel(this, category);
                panel.setX(x);
                panel.setY(panelY);
                panel.setWidth(panelWidth);
                panel.setHeight(panelTopHeight + Math.min(
                        YolBi.instance.getModuleManager().getModulesByCategory(category).size() *
                        (moduleHeight + moduleSpacing), panelMaxHeight
                ));
                panels.add(panel);
                x += panelWidth + panelSpacing;
            }
            panels.forEach(AbstractComponent::init);
            currentPanel = panels.get(panels.size() - 1);
            clientThemeModuleInstance = YolBi.instance.getModuleManager().getModule(ClientTheme.class);
            guiModuleInstance = YolBi.instance.getModuleManager().getModule(ClickUI.class);
            init = true;
        }
        if (OpenGlHelper.shadersSupported && mc.thePlayer != null && guiModuleInstance.getBlur().getValue()) {
            if (mc.entityRenderer.theShaderGroup != null) {
                mc.entityRenderer.theShaderGroup.deleteShaderGroup();
            }
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
            int radius = guiModuleInstance.getBlurRadius().getValue();
            mc.entityRenderer.getShaderGroup().getShaders().get(0).getShaderManager().getShaderUniform("Radius").set(radius);
            mc.entityRenderer.getShaderGroup().getShaders().get(1).getShaderManager().getShaderUniform("Radius").set(radius);
        }
    }

    public void update() {
        float wheel = Mouse.getDWheel();
        panels.forEach(p -> {
            p.setWheel(wheel);
            p.update();
        });
        MainTheme[2] = new Color(YolBi.instance.getModuleManager().getModule(ClientTheme.class).getColor(0));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        update();
        panels.forEach(p -> p.drawComponent(mouseX, mouseY, partialTicks, null));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Panel toTop = null;
        for (Panel panel : panels.stream().filter(p -> isHovering(p.getX(), p.getY(), p.getWidth(), p.getHeight(), mouseX, mouseY)).collect(Collectors.toList())) {
            if (toTop == null)
                toTop = panel;
            if (panels.indexOf(panel) > panels.indexOf(toTop))
                toTop = panel;
        }
        if (toTop != null) {
            panels.remove(toTop);
            panels.add(toTop);//置顶
            currentPanel = toTop;
        }
        panels.forEach(p -> p.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        panels.forEach(p -> p.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        panels.forEach(p -> p.keyTyped(typedChar, keyCode));
    }

    @Override
    public void onGuiClosed() {
        if (OpenGlHelper.shadersSupported && Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            if (mc.entityRenderer.theShaderGroup != null) {
                mc.entityRenderer.theShaderGroup.deleteShaderGroup();
                mc.entityRenderer.theShaderGroup = null;
            }
        }
    }

    public static int getComponentColor(int index) {
        return guiModuleInstance.getRainbow().getValue() ? ColorUtil.rainbow(10, index / 10, 1, 1, 1).getRGB() : clientThemeModuleInstance.getColor(index);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return guiModuleInstance.getPauseGame().getValue();
    }

    public boolean isHovering(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}
