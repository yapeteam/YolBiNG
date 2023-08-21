package cn.yapeteam.yolbi.ui.listedclickui.component;

/**
 * @author TIMER_err
 */
public interface Component {
    void init();

    void update();

    void drawComponent(int mouseX, int mouseY, float partialTicks, Limitation limitation);

    void mouseClicked(float mouseX, float mouseY, int mouseButton);

    void mouseReleased(float mouseX, float mouseY, int state);

    void keyTyped(char typedChar, int keyCode);
}
