import net.minecraft.client.main.Main;
import net.minecraft.util.Session;

import java.util.Arrays;

public class Start
{
    public static void main(String[] args)
    {
        Main.session = new Session(
                "yuxiangll",
                "c4b265e2eca04f7997440d6fe48bfece",
                "eyJraWQiOiJhYzg0YSIsImFsZyI6IkhTMjU2In0.eyJ4dWlkIjoiMjUzNTQxNDM0MjQxNTk1NSIsImFnZyI6IkFkdWx0Iiwic3ViIjoiMDgzZGZhZWUtNDg2Ny00ZmVlLTkyMmYtZDMzYTJiZWEwOGI4IiwiYXV0aCI6IlhCT1giLCJucyI6ImRlZmF1bHQiLCJyb2xlcyI6W10sImlzcyI6ImF1dGhlbnRpY2F0aW9uIiwiZmxhZ3MiOlsidHdvZmFjdG9yYXV0aCIsIm1zYW1pZ3JhdGlvbl9zdGFnZTQiLCJvcmRlcnNfMjAyMiIsIm11bHRpcGxheWVyIl0sInByb2ZpbGVzIjp7Im1jIjoiYzRiMjY1ZTItZWNhMC00Zjc5LTk3NDQtMGQ2ZmU0OGJmZWNlIn0sInBsYXRmb3JtIjoiVU5LTk9XTiIsInl1aWQiOiJmYTAzNjkwMmZjZjRmMWVjYzdlMGUzMmFmZmM3MTBiYyIsIm5iZiI6MTcwNTc0ODk0OSwiZXhwIjoxNzA1ODM1MzQ5LCJpYXQiOjE3MDU3NDg5NDl9.xH698otCuX23XfaE1i2ohm-N3yE5yszjVjaHvvoIJng",
                "microsoft"
        );
        Main.main(concat(new String[] {"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }

    public static <T> T[] concat(T[] first, T[] second)
    {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
