package cn.yapeteam.yolbi.ui.listedclickui.component;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author TIMER_err
 */
@Getter
public abstract class AbstractComponent implements Component {
    private final ArrayList<AbstractComponent> childComponents = new ArrayList<>();
    @Setter
    private float x, y, width, height;
    @Setter
    private boolean dragging = false;
    @Setter
    private float wheel = 0;
    private final AbstractComponent parent;

    public AbstractComponent(AbstractComponent parent) {
        this.parent = parent;
    }

    @Override
    public void init() {
        childComponents.forEach(AbstractComponent::init);
    }

    @Override
    public void update() {
        childComponents.forEach(AbstractComponent::update);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation) {
        childComponents.forEach(c -> c.drawComponent(mouseX, mouseY, partialTicks, limitation));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        if (isHovering(x, y, width, height, mouseX, mouseY))
            dragging = true;
        childComponents.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        dragging = false;
        childComponents.forEach(c -> c.mouseReleased(mouseX, mouseY, state));
    }


    @Override
    public void keyTyped(char typedChar, int keyCode) {
        childComponents.forEach(c -> c.keyTyped(typedChar, keyCode));
    }

    public boolean isHovering(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isHovering(float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}
