package life;

import lombok.val;
import lombok.var;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author TIMER_err
 * @link github.com/TIMER-err
 */

@SuppressWarnings("DuplicatedCode")
public class StreamLoader {
    public static void unzip(InputStream zipFile, String desDirectory) throws Exception {
        File desDir = new File(desDirectory);
        desDir.mkdir();
        // 读入流
        ZipInputStream zipInputStream = new ZipInputStream(zipFile);
        // 遍历每一个文件
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String unzipFilePath = desDirectory + File.separator + zipEntry.getName();
            System.out.println(unzipFilePath);
            if (zipEntry.isDirectory()) { // 文件夹
                // 直接创建
                mkdir(new File(unzipFilePath));
            } else { // 文件
                File file = new File(unzipFilePath);
                // 创建父目录
                mkdir(file.getParentFile());
                // 写出文件流
                BufferedOutputStream bufferedOutputStream =
                        new BufferedOutputStream(Files.newOutputStream(Paths.get(unzipFilePath)));
                byte[] bytes = new byte[1024];
                int readLen;
                while ((readLen = zipInputStream.read(bytes)) != -1) {
                    bufferedOutputStream.write(bytes, 0, readLen);
                }
                bufferedOutputStream.close();
            }
            zipInputStream.closeEntry();
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    // 如果父目录不存在则创建
    public static void mkdir(File file) {
        if (null == file || file.exists()) {
            return;
        }
        mkdir(file.getParentFile());
        file.mkdir();
    }

    public static void loadLib(String name) {
        val libExtension = System.getProperty("os.name").toLowerCase().contains("win") ? ".dll" : ".dylib";
        val libSteam = StreamLoader.class.getResourceAsStream("/" + name + libExtension);
        if (libSteam == null)
            throw new RuntimeException("Failed to load Native Lib.");
        val tempRender = new BufferedInputStream(libSteam);
        try {
            val libFile = new File(name + libExtension);
            try (val tempWriter = new FileOutputStream(libFile)) {
                var buffer = new byte[1024];
                while (tempRender.read(buffer) > 0) {
                    tempWriter.write(buffer);
                    buffer = new byte[1024];
                }
                tempRender.close();
            }
            if (System.getProperty("os.name").toLowerCase().contains("win"))
                unzip(StreamLoader.class.getResourceAsStream("/g++win64.zip"), new File("").getAbsolutePath());
            System.out.println(libFile.getAbsolutePath());
            System.load(libFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static native void load();
}
