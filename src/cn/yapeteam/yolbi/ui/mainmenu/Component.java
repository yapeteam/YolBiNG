package cn.yapeteam.yolbi.ui.mainmenu;

/**
 * @author TIMER_err
 */
public interface Component {
    void init();

    void update();

    void drawComponent(int mouseX, int mouseY, float partialTicks);

    void mouseClicked(float mouseX, float mouseY, int mouseButton);

    void mouseReleased(float mouseX, float mouseY, int state);
}
