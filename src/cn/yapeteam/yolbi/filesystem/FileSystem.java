package cn.yapeteam.yolbi.filesystem;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.values.impl.BooleanValue;
import cn.yapeteam.yolbi.values.impl.NumberValue;
import cn.yapeteam.yolbi.values.impl.ModeValue;
import net.minecraft.client.Minecraft;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.values.Value;

import java.io.*;
import java.util.ArrayList;

public class FileSystem {
    private final File vestigeDir;
    private final File vestigeConfigDir;

    public FileSystem() {
        File mcDir = Minecraft.getMinecraft().mcDataDir;

        vestigeDir = new File(mcDir, "Vestige v3");

        if (!vestigeDir.exists()) {
            vestigeDir.mkdir();
        }

        vestigeConfigDir = new File(vestigeDir, "configs");

        if (!vestigeConfigDir.exists()) {
            vestigeConfigDir.mkdir();
        }
    }

    public void saveConfig(String configName) {
        configName = configName.toLowerCase();

        try {
            File configFile = new File(vestigeConfigDir, configName + ".txt");

            if (!configFile.exists()) {
                configFile.createNewFile();
            }

            PrintWriter writer = new PrintWriter(configFile);

            ArrayList<String> toWrite = new ArrayList<>();

            for (Module m : YolBi.instance.getModuleManager().modules) {
                toWrite.add("State:" + m.getName() + ":" + m.isEnabled());

                if (!m.getValues().isEmpty()) {
                    for (Value<?> value : m.getValues()) {
                        if (value instanceof BooleanValue) {
                            BooleanValue boolSetting = (BooleanValue) value;
                            toWrite.add("Setting:" + m.getName() + ":" + boolSetting.getName() + ":" + boolSetting.getValue());
                        } else if (value instanceof ModeValue) {
                            ModeValue<?> modeSetting = (ModeValue<?>) value;
                            toWrite.add("Setting:" + m.getName() + ":" + modeSetting.getName() + ":" + modeSetting.getValue());
                        } else if (value instanceof NumberValue) {
                            NumberValue<?> numberValue = (NumberValue<?>) value;
                            toWrite.add("Setting:" + m.getName() + ":" + numberValue.getName() + ":" + numberValue.getValue());
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
            File configFile = new File(vestigeConfigDir, configName + ".txt");

            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));

                ArrayList<String> lines = new ArrayList<>();

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();

                for (String s : lines) {
                    String infos[] = s.split(":");

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
                                    Value<?> setting = m.getSettingByName(infos[2]);

                                    if (setting != null) {
                                        if (setting instanceof BooleanValue) {
                                            BooleanValue boolSetting = (BooleanValue) setting;
                                            boolSetting.setValue(Boolean.parseBoolean(infos[3]));
                                        } else if (setting instanceof ModeValue) {
                                            ModeValue<?> modeSetting = (ModeValue<?>) setting;
                                            modeSetting.setValue(infos[3]);
                                        } else if (setting instanceof NumberValue) {
                                            NumberValue<?> doubleSetting = (NumberValue<?>) setting;
                                            doubleSetting.setValue(Double.parseDouble(infos[3]));
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
            File keybindsFile = new File(vestigeDir, "keybinds.txt");

            if (!keybindsFile.exists()) {
                keybindsFile.createNewFile();
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
            File keybindsFile = new File(vestigeDir, "keybinds.txt");

            if (keybindsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(keybindsFile));

                ArrayList<String> lines = new ArrayList<>();

                String line;

                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                reader.close();

                for (String s : lines) {
                    String infos[] = s.split(":");

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

    public void saveDefaultConfig() {
        saveConfig("default");
    }

    public void loadDefaultConfig() {
        loadConfig("default", true);
    }

}