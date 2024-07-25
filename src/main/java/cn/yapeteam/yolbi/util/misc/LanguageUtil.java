package cn.yapeteam.yolbi.util.misc;



import cn.yapeteam.yolbi.util.misc.language.ChineseLanguage;
import com.alibaba.fastjson.JSONObject;


/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.util.misc
 * don't mind
 * @date 2023/8/21 22:21
 */
public class LanguageUtil  {
    public static JSONObject languogeJson = new JSONObject();//创建一个json，给子类添加元素

    //获取当前语言
    public static String getString(String name){
        return languogeJson.get(name).toString();
    }






}
