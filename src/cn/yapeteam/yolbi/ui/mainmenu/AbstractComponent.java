package cn.yapeteam.yolbi.ui.mainmenu;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * @author TIMER_err
 */
@Getter
public abstract class AbstractComponent implements Component {
    private final AbstractComponent parent;
    private final ArrayList<AbstractComponent> childComponents = new ArrayList<>();

    @Setter
    private float x, y, width, height;

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
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        childComponents.forEach(c -> c.drawComponent(mouseX, mouseY, partialTicks));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouseButton) {
        childComponents.forEach(c -> c.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int state) {
        childComponents.forEach(c -> c.mouseReleased(mouseX, mouseY, state));
    }

    public boolean isHovering(float x, float y, float width, float height, float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }

    public boolean isHovering(float mouseX, float mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}
