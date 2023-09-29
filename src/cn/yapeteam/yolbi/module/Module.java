package cn.yapeteam.yolbi.module;

import cn.yapeteam.yolbi.YolBi;
import cn.yapeteam.yolbi.ui.noti.Notification;
import cn.yapeteam.yolbi.util.IMinecraft;
import cn.yapeteam.yolbi.values.Value;
import com.mojang.realmsclient.gui.ChatFormatting;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
public abstract class Module implements IMinecraft {

    private String name = null;
    private ModuleCategory category = null;
    private int key = 0;

    private boolean enabled = false;

    private boolean listening = false;
    protected EventListenType listenType = null;

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
            YolBi.instance.getNotificationManager().add(new Notification("Module toggled: " + name + (enabled ? " enabled" : " disabled")));
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
        setEnabled(!this.enabled);
    }

    public final void toggleSilently() {
        setEnabledSilently(!this.enabled);
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