package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.values.Value;
import com.mojang.realmsclient.gui.ChatFormatting;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public abstract class Module implements IMinecraft {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private ModuleCategory category;

    @Getter
    @Setter
    private int key;

    private boolean enabled;

    private boolean listening;
    protected EventListenType listenType;

    private final ArrayList<Value<?>> values = new ArrayList<>();

    protected void onEnable() {

    }

    protected void onDisable() {

    }

    public void onClientStarted() {

    }

    public final void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                onEnable();
                if (listenType == EventListenType.AUTOMATIC) {
                    startListening();
                }
            } else {
                if (listenType == EventListenType.AUTOMATIC) {
                    stopListening();
                }
                onDisable();
            }
        }
    }

    public final void setEnabledSilently(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;

            if (enabled) {
                if (listenType == EventListenType.AUTOMATIC) {
                    startListening();
                }
            } else {
                if (listenType == EventListenType.AUTOMATIC) {
                    stopListening();
                }
            }
        }
    }

    public final void toggle() {
        this.enabled = !enabled;

        if (enabled) {
            onEnable();
            if (listenType == EventListenType.AUTOMATIC) {
                startListening();
            }
        } else {
            if (listenType == EventListenType.AUTOMATIC) {
                stopListening();
            }
            onDisable();
        }
    }

    public final void toggleSilently() {
        this.enabled = !enabled;

        if (enabled) {
            if (listenType == EventListenType.AUTOMATIC) {
                startListening();
            }
        } else {
            if (listenType == EventListenType.AUTOMATIC) {
                stopListening();
            }
        }
    }

    protected final void startListening() {
        if (!listening) {
            YolBi.instance.getEventManager().register(this);
            listening = true;
        }
    }

    protected final void stopListening() {
        if (listening) {
            YolBi.instance.getEventManager().unregister(this);
            listening = false;
        }
    }

    public void addValues(Value<?>... values) {
        this.values.addAll(Arrays.asList(values));
    }

    public Value<?> getValueByName(String name) {
        return values.stream().filter(v -> v.getName().equals(name)).findFirst().orElse(null);
    }

    public String getSuffix() {
        return null;
    }

    public final String getDisplayName() {
        return getDisplayName(ChatFormatting.GRAY);
    }

    public final String getDisplayName(ChatFormatting formatting) {
        String tag = getSuffix();

        if (tag == null || tag.equals("")) {
            return name;
        }

        return name + formatting + " " + tag;
    }
}