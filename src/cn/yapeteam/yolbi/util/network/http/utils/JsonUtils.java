package cn.yapeteam.yolbi.util.network.http.utils;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Contains utilities for reading/writing and interacting with Json.
 * <p>
 * Many of the parse/write methods will take a {@link Type} object instead of a {@link Class} object.
 * This is to allow deserializing objects which have direct generic parameters through
 * the use of {@link TypeToken}. You can use {@link TypeToken} as follows:
 * <code>private static final Type STRING_LIST_TYPE = new com.google.gson.reflect.TypeToken&lt;List&lt;String&gt;&gt;(){ }.getType();</code>.
 * <p>
 * Borrowed from Quack.
 * <p>
 * Created by covers1624 on 11/11/21.
 */
public class JsonUtils {

    //region Deserialize

    /**
     * Deserialize Json from the given {@link Path} as the given {@link Type}.
     *
     * @param gson The {@link Gson} instance to use.
     * @param path The {@link Path} to read from.
     * @param t    The {@link Type} to deserialize from.
     * @return The Object deserialized from Json.
     * @throws JsonParseException Propagated from {@link Gson#fromJson(Reader, Type)},
     *                            thrown when Gson encounters an error deserializing the object.
     * @throws IOException        Thrown when an IO error occurs.
     */
    public static <T> T parse(Gson gson, Path path, Type t) throws IOException, JsonParseException {
        return parse(gson, Files.newInputStream(path), t);
    }

    /**
     * Deserialize Json from the given {@link String} as the given {@link Type}.
     *
     * @param gson The {@link Gson} instance to use.
     * @param str  The {@link String} representing the json to parse.
     * @param t    The {@link Type} to deserialize from.
     * @return The Object deserialized from Json.
     * @throws JsonParseException Propagated from {@link Gson#fromJson(Reader, Type)},
     *                            thrown when Gson encounters an error deserializing the object.
     * @throws IOException        Thrown when an IO error occurs.
     */
    public static <T> T parse(Gson gson, String str, Type t) throws IOException, JsonParseException {
        return parse(gson, new StringReader(str), t);
    }

    /**
     * Deserialize Json from the given {@link InputStream} as the given {@link Type}.
     *
     * @param gson The {@link Gson} instance to use.
     * @param is   The {@link InputStream} to read from.
     * @param t    The {@link Type} to deserialize from.
     * @return The Object deserialized from Json.
     * @throws JsonParseException Propagated from {@link Gson#fromJson(Reader, Type)},
     *                            thrown when Gson encounters an error deserializing the object.
     * @throws IOException        Thrown when an IO error occurs.
     */
    public static <T> T parse(Gson gson, InputStream is, Type t) throws IOException, JsonParseException {
        return parse(gson, new InputStreamReader(is), t);
    }

    /**
     * Deserialize Json from the given {@link Reader} as the given {@link Type}.
     *
     * @param gson   The {@link Gson} instance to use.
     * @param reader The {@link Reader} to read from.
     * @param t      The {@link Type} to deserialize from.
     * @return The Object deserialized from Json.
     * @throws JsonParseException Propagated from {@link Gson#fromJson(Reader, Type)},
     *                            thrown when Gson encounters an error deserializing the object.
     * @throws IOException        Thrown when an IO error occurs.
     */
    public static <T> T parse(Gson gson, Reader reader, Type t) throws IOException, JsonParseException {
        try (Reader r = reader) {
            return gson.fromJson(r, t);
        }
    }
    //endregion

    //region Serialize.

    /**
     * Serialize the provided Object to Json and write to the given {@link Path}.
     * <p>
     * Use this method when your <code>instance</code> doesn't have any direct generic parameters.
     *
     * @param gson     The {@link Gson} instance to use.
     * @param path     The {@link Path} to write to.
     * @param instance The Object instance to serialize.
     * @throws JsonIOException Propagated from {@link Gson#toJson(Object, Type, Appendable)},
     *                         thrown when Gson encounters an error serializing the instance.
     * @throws IOException     Thrown when an IO error occurs.
     */
    public static void write(Gson gson, Path path, Object instance) throws IOException, JsonIOException {
        write(gson, path, instance, instance.getClass());
    }

    /**
     * Serialize the provided Object to json and write it to the given {@link Path}.
     * <p>
     * Use this method directly if your <code>instance</code> has direct generic parameters.
     *
     * @param gson     The {@link Gson} instance to use.
     * @param path     The {@link Path} to write to.
     * @param instance The Object instance to serialize.
     * @param t        The {@link Type} of the Object instance.
     * @throws JsonIOException Propagated from {@link Gson#toJson(Object, Type, Appendable)},
     *                         thrown when Gson encounters an error serializing the instance.
     * @throws IOException     Thrown when an IO error occurs.
     */
    public static void write(Gson gson, Path path, Object instance, Type t) throws IOException, JsonIOException {
        write(gson, Files.newOutputStream(path), instance, t);
    }

    /**
     * Serialize the provided Object to Json and write to the given {@link OutputStream}.
     * <p>
     * Use this method when your <code>instance</code> doesn't have any direct generic parameters.
     *
     * @param gson     The {@link Gson} instance to use.
     * @param os       The {@link OutputStream} to write to.
     * @param instance The Object instance to serialize.
     * @throws JsonIOException Propagated from {@link Gson#toJson(Object, Type, Appendable)},
     *                         thrown when Gson encounters an error serializing the instance.
     * @throws IOException     Thrown when an IO error occurs.
     */
    public static void write(Gson gson, OutputStream os, Object instance) throws IOException, JsonIOException {
        write(gson, os, instance, instance.getClass());
    }

    /**
     * Serialize the provided Object to Json and write to the given OutputStream.
     * <p>
     * Use this method directly if your <code>instance</code> has direct generic parameters.
     *
     * @param gson     The {@link Gson} instance to use.
     * @param os       The {@link OutputStream} to write to.
     * @param instance The Object instance to write.
     * @param t        The {@link Type} of the Object instance.
     * @throws JsonIOException Propagated from {@link Gson#toJson(Object, Type, Appendable)},
     *                         thrown when Gson encounters an error serializing the instance.
     * @throws IOException     Thrown when an IO error occurs.
     */
    public static void write(Gson gson, OutputStream os, Object instance, Type t) throws IOException, JsonIOException {
        try (Writer writer = new OutputStreamWriter(os)) {
            gson.toJson(instance, t, writer);
        }
    }
    //endregion
}
