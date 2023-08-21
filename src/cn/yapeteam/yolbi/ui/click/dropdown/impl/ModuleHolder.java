package cn.yapeteam.yolbi.ui.click.dropdown.impl;

import cn.yapeteam.yolbi.util.misc.TimerUtil;
import lombok.Getter;
import cn.yapeteam.yolbi.module.Module;
import cn.yapeteam.yolbi.values.Value;

import java.util.ArrayList;

@Getter
public class ModuleHolder {

    private Module module;

    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil settingsShownTimer = new TimerUtil();

    private boolean lastEnabled;

    private final ArrayList<SettingHolder> settings = new ArrayList<>();

    private boolean settingsShown;

    public ModuleHolder(Module m) {
        this.module = m;

        for(Value s : m.getValues()) {
            settings.add(new SettingHolder(s));
        }
    }

    public void updateState() {
        boolean enabled = module.isEnabled();

        if(enabled != lastEnabled) {
            timer.reset();
        }

        lastEnabled = module.isEnabled();
    }

    public void setSettingsShown(boolean shown) {
        if(settingsShown != shown) {
            this.settingsShown = shown;

            settingsShownTimer.reset();
        }
    }

}
