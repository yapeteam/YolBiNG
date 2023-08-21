package cn.yapeteam.yolbi.ui.click.dropdown.impl;

import cn.yapeteam.yolbi.util.misc.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.values.Value;

public class SettingHolder {

    private Value setting;

    @Getter
    @Setter
    private boolean holdingMouse;

    @Getter
    private final TimerUtil timer = new TimerUtil();

    public SettingHolder(Value s) {
        this.setting = s;
    }

    public void click() {
        timer.reset();
    }

    public <T extends Value> T getSetting() {
        return (T) setting;
    }

}
