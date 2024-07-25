package cn.yapeteam.yolbi.util.network.http.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Various utilities for IO interaction with bytes, streams, files, etc.
 * <p>
 * Borrowed from Quack.
 * <p>
 * Created by covers1624 on 14/1/21.
 */
public class IOUtils {

    //32k buffer.
    private static final ThreadLocal<byte[]> bufferCache = ThreadLocal.withInitial(() -> new byte[32 * 1024]);

    /**
     * Returns a static per-thread cached 32k buffer for IO operations.
     *
     * @return The buffer.
     */
    public static byte[] getCachedBuffer() {
        return bufferCache.get();
    }

    /**
     * Copies the content of an {@link InputStream} to an {@link OutputStream}.
     *
     * @param is The {@link InputStream}.
     * @param os The {@link OutputStream}.
     * @throws IOException If something is bork.
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = bufferCache.get();
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }

    /**
     * Reads an {@link InputStream} to a byte array.
     *
     * @param is The InputStream.
     * @return The bytes.
     * @throws IOException If something is bork.
     */
    public static byte[] toBytes(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        copy(is, os);
        return os.toByteArray();
    }

    /**
     * Creates the parent directories of the given path if they don't exist.
     *
     * @param path The path.
     * @return The same path.
     * @throws IOException If an error occurs creating the directories.
     */
    public static Path makeParents(Path path) throws IOException {
        path = path.toAbsolutePath();
        Path parent = path.getParent();
        if (Files.notExists(parent)) {
            Files.createDirectories(parent);
        }
        return path;
    }
}
