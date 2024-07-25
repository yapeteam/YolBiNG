package cn.yapeteam.yolbi.event.impl.block;

import cn.yapeteam.yolbi.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

/**
 * @author yuxiangll
 * @package cn.yapeteam.yolbi.event.impl.block
 * don't mind
 * @date 2023/12/9 23:08
 */
@Setter
@Getter
public class EventBlockBB extends Event {
    private BlockPos blockPos;
    private Block block;
    private AxisAlignedBB axisAlignedBB;

    private double x;
    private double y;
    private double z;


    public EventBlockBB(BlockPos blockPos, Block block, AxisAlignedBB axisAlignedBB) {
        this.blockPos = blockPos;
        this.block = block;
        this.axisAlignedBB = axisAlignedBB;
        this.x = blockPos.getX();
        this.y = blockPos.getY();
        this.z = blockPos.getZ();
    }


}
