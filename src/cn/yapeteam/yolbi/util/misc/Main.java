package cn.yapeteam.yolbi.util.misc;

import cn.yapeteam.yolbi.util.misc.language.ChineseLanguage;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.misc
 * don't mind
 * @date 2023/8/21 22:34
 */
public class Main {
    public static void main(String[] args) {
        ChineseLanguage.onAddJsonValue();
        System.out.println(LanguageUtil.languogeJson);
        System.out.println(LanguageUtil.languogeJson.get("1"));

        //LanguageUtil languageUtil = new LanguageUtil();
        //languageUtil.loadLanguage("Chinese.json");
        //LanguageUtil.loadLanguage("简体中文");
    }
}
