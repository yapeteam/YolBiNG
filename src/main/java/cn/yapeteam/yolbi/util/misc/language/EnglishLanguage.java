package cn.yapeteam.yolbi.util.misc.language;

import cn.yapeteam.yolbi.util.misc.LanguageUtil;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.misc.language
 * don't mind
 * @date 2023/8/21 23:31
 */
public class EnglishLanguage extends LanguageUtil {
    public static void onAddJsonValue() {
        if (!languogeJson.isEmpty()) languogeJson.clear();
        languogeJson.put("module.combat.Killaura","Killara");
    }
}
