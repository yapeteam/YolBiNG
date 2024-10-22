package cn.yapeteam.yolbi.render;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.event.Listener;
import cn.yapeteam.yolbi.event.impl.game.EventLoop;
import cn.yapeteam.yolbi.render.shader.Shader;
import cn.yapeteam.yolbi.util.render.DrawUtil;
import cn.yapeteam.yolbi.util.render.gaussianblur.GaussianFilter;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class RenderEngine {
    public static final RenderEngine instance = new RenderEngine();
    private final CopyOnWriteArrayList<Shader> shaderList = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Runnable> tasks = new CopyOnWriteArrayList<>();
    private final Map<Integer, Integer> textureMap = new HashMap<>();
    private final ArrayList<Integer> disposing = new ArrayList<>();
    private static final int maxThreads = 10;

    public RenderEngine() {
        YolBi.instance.getEventManager().register(this);
    }

    private volatile int threads = 0;

    private void dispose(Shader shader) {
        disposing.add(shader.identifier);
        Runnable connect = () -> {
            synchronized ((Object) threads) {
                threads++;
                int width = (int) shader.width, height = (int) shader.height;
                int size = width * height;
                ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
                for (int i = 0; i < size; i++) {
                    int x = i % width, y = i / width;
                    image.setRGB(x, y, shader.dispose(x, y, sr.getScaledWidth(), sr.getScaledHeight(), 0));
                }
                image = shader.antiAlias ? new GaussianFilter(shader.level / 2f).filter(image, null) : image;
                BufferedImage finalImage = image;
                Runnable task = () -> textureMap.put(
                        shader.identifier,
                        TextureUtil.uploadTextureImageAllocate(
                                TextureUtil.glGenTextures(),
                                finalImage,
                                true, false
                        )
                );
                if (shader.multithreading)
                    tasks.add(task);
                else task.run();
                threads--;
                disposing.remove((Object) shader.identifier);
            }
        };
        if (shader.multithreading)
            new Thread(connect).start();
        else connect.run();
    }

    @Listener
    private void run(EventLoop e) {
        for (Runnable task : tasks) {
            task.run();
            tasks.remove(task);
        }
        for (Shader shader : shaderList) {
            dispose(shader);
            shaderList.remove(shader);
        }
    }

    public void render(Shader shader, float x, float y, int color) {
        Integer identifier = textureMap.get(shader.identifier);
        if (identifier == null && !shaderList.contains(shader) && !disposing.contains(shader.identifier)) {
            if (shader.multithreading)
                shaderList.add(shader);
            else {
                dispose(shader);
                identifier = textureMap.get(shader.identifier);
            }
        }
        if (identifier != null)
            DrawUtil.drawImage(identifier, x, y, shader.getRealWidth(), shader.getRealHeight(), color);
    }
}
