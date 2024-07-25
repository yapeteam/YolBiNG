package cn.yapeteam.yolbi.ui.listedclickui.component;

import cn.yapeteam.yolbi.util.render.RenderUtil;
import cn.yapeteam.yolbi.util.render.Stencil;
import lombok.Getter;

/**
 * @author TIMER_err
 */
public class Limitation {
    public Limitation(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Getter
    private final float x, y, width, height;

    public void start() {
        start(() -> RenderUtil.drawRect(x, y, x + width, y + height, -1));
    }

    public void start(Runnable connect) {
        Stencil.write(false);
        connect.run();
        Stencil.erase(true);
    }

    public void end() {
        Stencil.dispose();
    }
}
