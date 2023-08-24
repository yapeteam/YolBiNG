package life;

import lombok.val;
import lombok.var;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author TIMER_err
 * @link github.com/TIMER-err
 */

@SuppressWarnings("DuplicatedCode")
public class StreamLoader {
    public static void loadLib(String name) {
        val libExtension = System.getProperty("os.name").toLowerCase().contains("win") ? ".dll" : ".dylib";
        val libSteam = StreamLoader.class.getResourceAsStream("/" + name + libExtension);
        if (libSteam == null)
            throw new RuntimeException("Failed to load Native Lib.");
        val tempRender = new BufferedInputStream(libSteam);
        try {
            val dllFile = new File(name + libExtension);
            try (val tempWriter = new FileOutputStream(dllFile)) {
                var buffer = new byte[1024];
                while (tempRender.read(buffer) > 0) {
                    tempWriter.write(buffer);
                    buffer = new byte[1024];
                }
                tempRender.close();
            }
            System.out.println(dllFile.getAbsolutePath());
            System.load(dllFile.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static native void load();
}
