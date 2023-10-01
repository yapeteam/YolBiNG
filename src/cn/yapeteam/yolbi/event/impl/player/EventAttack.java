package cn.yapeteam.yolbi.event.impl.player;

import cn.yapeteam.yolbi.event.type.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.event.impl.player
 * don't mind
 * @date 2023/8/22 17:00
 */

@Setter
@Getter
@AllArgsConstructor
public class EventAttack extends CancellableEvent {
    private Entity targetEntity;
}
