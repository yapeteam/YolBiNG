package cn.yapeteam.yolbi.util.misc.language;


import cn.yapeteam.yolbi.util.misc.LanguageUtil;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.misc.language
 * don't mind
 * @date 2023/8/21 23:31
 */
public class ChineseLanguage  extends LanguageUtil{

    public static void onAddJsonValue() {
        if (!languogeJson.isEmpty()) languogeJson.clear();//每次都重置一遍
        languogeJson.put("module.combat.Killaura","杀戮光环");
    }
}
