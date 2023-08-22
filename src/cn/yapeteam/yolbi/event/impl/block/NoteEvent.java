package cn.yapeteam.yolbi.event.impl.block;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.BlockPos;

@Getter
@AllArgsConstructor
public class NoteEvent extends Event {
    private final BlockPos pos;
    private final int note, pitch;
}
