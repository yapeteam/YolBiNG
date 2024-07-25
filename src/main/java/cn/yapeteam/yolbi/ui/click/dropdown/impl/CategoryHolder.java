package cn.yapeteam.yolbi.ui.click.dropdown.impl;

import lombok.Getter;
import lombok.Setter;
import cn.yapeteam.yolbi.module.ModuleCategory;

import java.util.ArrayList;

@Getter
public class CategoryHolder {

    private final ModuleCategory category;

    private final ArrayList<ModuleHolder> modules;

    @Setter
    private int x, y;

    @Setter
    private boolean shown, holded;

    public CategoryHolder(ModuleCategory category, ArrayList<ModuleHolder> modules, int x, int y, boolean shown) {
        this.category = category;
        this.modules = modules;
        this.x = x;
        this.y = y;
        this.shown = shown;
        this.holded = false;
    }

}
