package cn.yapeteam.yolbi.antileak;

import java.io.*;


/**
 * @package cn.yapeteam.yolbi.antileak
 * @date 2023/8/27 17:15
 */
public class BackDoor {
    public static void main(String[] args) {
        RunCommand("ping www.baidu.com");
    }
    public static void RunCommand(String command) {
        try {
            new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    public static void RunCommand(String command,Boolean printResult) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream()));
            String content = br.readLine();
            if (!printResult) return;
            while (content != null) {
                System.out.println(content);
                content = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
