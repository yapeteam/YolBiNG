package cn.yapeteam.yolbi.filesystem;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.values.Value;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.ColorValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

@SuppressWarnings("DuplicatedCode")
@Getter
public class FileSystem {
    private final File yolbiDir;
    private final File configDir;

    public FileSystem() {
        File mcDir = Minecraft.getMinecraft().mcDataDir;

        yolbiDir = new File(mcDir, "YolBiNextGen");

        if (!yolbiDir.exists()) {
            boolean ignored = yolbiDir.mkdir();
        }

        configDir = new File(yolbiDir, "configs");

        if (!configDir.exists()) {
            boolean ignored = configDir.mkdir();
        }
    }

    public void saveConfig(String configName) {
        configName = configName.toLowerCase();

        try {
            File configFile = new File(configDir, configName + ".txt");

            if (!configFile.exists()) {
                boolean ignored = configFile.createNewFile();
            }

            PrintWriter writer = new PrintWriter(configFile);

            ArrayList<String> toWrite = new ArrayList<>();

            for (Module m : YolBi.instance.getModuleManager().modules) {
                toWrite.add("State:" + m.getName() + ":" + m.isEnabled());

                if (!m.getValues().isEmpty()) {
                    for (Value<?> value : m.getValues()) {
                        if (value instanceof BooleanValue) {
                            BooleanValue booleanValue = (BooleanValue) value;
                            toWrite.add("Setting:" + m.getName() + ":" + booleanValue.getName() + ":" + booleanValue.getValue());
                        } else if (value instanceof ModeValue) {
                            ModeValue<?> modeValue = (ModeValue<?>) value;
                            toWrite.add("Setting:" + m.getName() + ":" + modeValue.getName() + ":" + modeValue.getValue());
                        } else if (value instanceof NumberValue) {
                            NumberValue<?> numberValue = (NumberValue<?>) value;
                            toWrite.add("Setting:" + m.getName() + ":" + numberValue.getName() + ":" + numberValue.getValue());
                        } else if (value instanceof ColorValue) {
                            ColorValue colorValue = (ColorValue) value;
                            toWrite.add("Setting:" + m.getName() + ":" + colorValue.getName() + ":" + colorValue.getValue().getRGB());
                        }
                    }
                }
            }

            for (String s : toWrite) {
                writer.println(s);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean loadConfig(String configName, boolean defaultConfig) {
        try {
            File configFile = new File(configDir, configName + ".txt");

            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));

                ArrayList<String> lines = new ArrayList<>();

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();

                for (String s : lines) {
                    String[] infos = s.split(":");

                    if (infos.length >= 3) {
                        String type = infos[0];
                        String moduleName = infos[1];

                        Module m = YolBi.instance.getModuleManager().getModuleByName(moduleName);

                        if (m != null) {
                            switch (type) {
                                case "State":
                                    if (defaultConfig) {
                                        m.setEnabledSilently(Boolean.parseBoolean(infos[2]));
                                    } else {
                                        m.setEnabled(Boolean.parseBoolean(infos[2]));
                                    }
                                    break;
                                case "Setting":
                                    Value<?> value = m.getValueByName(infos[2]);

                                    if (value != null) {
                                        if (value instanceof BooleanValue) {
                                            BooleanValue booleanValue = (BooleanValue) value;
                                            booleanValue.setValue(Boolean.parseBoolean(infos[3]));
                                        } else if (value instanceof ModeValue) {
                                            ModeValue<?> modeValue = (ModeValue<?>) value;
                                            modeValue.setMode(infos[3]);
                                        } else if (value instanceof NumberValue) {
                                            NumberValue<?> numberValue = (NumberValue<?>) value;
                                            numberValue.setValue(Double.parseDouble(infos[3]));
                                        } else if (value instanceof ColorValue) {
                                            ColorValue colorValue = (ColorValue) value;
                                            colorValue.setValue(new Color(Integer.parseInt(infos[3])));
                                        }
                                        break;
                                    }
                            }
                        }
                    }
                }

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void saveKeybinds() {
        try {
            File keybindsFile = new File(yolbiDir, "keybinds.txt");

            if (!keybindsFile.exists()) {
                boolean ignored = keybindsFile.createNewFile();
            }

            PrintWriter writer = new PrintWriter(keybindsFile);

            ArrayList<String> toWrite = new ArrayList<>();

            for (Module m : YolBi.instance.getModuleManager().modules) {
                toWrite.add(m.getName() + ":" + m.getKey());
            }

            for (String s : toWrite) {
                writer.println(s);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadKeybinds() {
        try {
            File keybindsFile = new File(yolbiDir, "keybinds.txt");

            if (keybindsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(keybindsFile));

                ArrayList<String> lines = new ArrayList<>();

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();

                for (String s : lines) {
                    String[] infos = s.split(":");

                    if (infos.length == 2) {
                        String moduleName = infos[0];
                        int key = Integer.parseInt(infos[1]);

                        Module m = YolBi.instance.getModuleManager().getModuleByName(moduleName);

                        if (m != null) {
                            m.setKey(key);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAccounts(ArrayList<String> alts) {
        try {
            File accountsFile = new File(yolbiDir, "Accounts.txt");

            if (!accountsFile.exists()) {
                boolean ignored = accountsFile.createNewFile();
            }

            PrintWriter writer = new PrintWriter(accountsFile);

            ArrayList<String> toWrite = new ArrayList<>();


            for (String s : alts) {
                toWrite.add(s);
            }

            for (String s : toWrite) {
                writer.println(s);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> loadAccounts() {
        try {
            File accountsFile = new File(yolbiDir, "Accounts.txt");

            if (accountsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(accountsFile));

                ArrayList<String> lines = new ArrayList<>();

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();
                return lines;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void saveDefaultConfig() {
        saveConfig("default");
    }

    public void loadDefaultConfig() {
        loadConfig("default", true);
    }
}