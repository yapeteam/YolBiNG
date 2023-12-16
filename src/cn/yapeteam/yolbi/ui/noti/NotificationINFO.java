package cn.yapeteam.yolbi.ui.noti;

import cn.yapeteam.yolbi.util.render.animation.Animation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.ui.noti
 * don't mind
 * @date 2023/12/16 21:21
 */

@Setter
@Getter
public class NotificationINFO {
    private String text;
    private Animation animation;
    private NotificationInfoType type;

    public NotificationINFO(String text, Animation animation, NotificationInfoType type) {
        this.text = text;
        this.animation = animation;
        this.type = type;
        animation.run(1);
    }
}



