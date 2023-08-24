package life;

import lombok.val;
import lombok.var;
import net.minecraft.client.Minecraft;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author TIMER_err
 * @link github.com/TIMER-err
 */

@SuppressWarnings("DuplicatedCode")
public class StreamLoader {
    public static HashMap<String, Class<?>> map = new HashMap<>();

    @SuppressWarnings("unused")
    public Class<?> loadClassFromStream(InputStream stream, String name) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (map.containsKey(name))
            return map.get(name);

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
        byte[] classData = outStream.toByteArray();
        Class<?> clz;
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        clz = (Class<?>) defineClass.invoke(Minecraft.class.getClassLoader(), null, classData, 0, classData.length);
        System.out.println("load " + clz.getName() + " " + Thread.currentThread().getName());
        map.put(name, clz);
        return clz;
    }

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

    public static native Class<?> load(String name);
}
