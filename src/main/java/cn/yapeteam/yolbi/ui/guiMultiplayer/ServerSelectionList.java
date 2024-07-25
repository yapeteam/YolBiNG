package cn.yapeteam.yolbi.ui.guiMultiplayer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

public class ServerSelectionList extends GuiListExtended
{
    private final GuiMultiplayer owner;
    private final List<ServerListEntryNormal> field_148198_l = Lists.<ServerListEntryNormal>newArrayList();
    private final List<ServerListEntryLanDetected> field_148199_m = Lists.<ServerListEntryLanDetected>newArrayList();
    private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    private int selectedSlotIndex = -1;

    public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn)
    {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.owner = ownerIn;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiListExtended.IGuiListEntry getListEntry(int index)
    {
        if (index < this.field_148198_l.size())
        {
            return (GuiListExtended.IGuiListEntry)this.field_148198_l.get(index);
        }
        else
        {
            index = index - this.field_148198_l.size();

            if (index == 0)
            {
                return this.lanScanEntry;
            }
            else
            {
                --index;
                return (GuiListExtended.IGuiListEntry)this.field_148199_m.get(index);
            }
        }
    }

    protected int getSize()
    {
        return this.field_148198_l.size() + 1 + this.field_148199_m.size();
    }

    public void setSelectedSlotIndex(int selectedSlotIndexIn)
    {
        this.selectedSlotIndex = selectedSlotIndexIn;
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(int slotIndex)
    {
        return slotIndex == this.selectedSlotIndex;
    }

    public int getSelected()
    {
        return this.selectedSlotIndex;
    }

    public void updateOnlineServers(ServerList serverList)
    {
        this.field_148198_l.clear();

        for (int i = 0; i < serverList.countServers(); ++i)
        {
            this.field_148198_l.add(new ServerListEntryNormal(this.owner, serverList.getServerData(i)));
        }
    }

    public void updateNetworkServers(List<LanServerDetector.LanServer> lanServers)
    {
        this.field_148199_m.clear();

        for (LanServerDetector.LanServer lanserverdetector$lanserver : lanServers)
        {
            this.field_148199_m.add(new ServerListEntryLanDetected(this.owner, lanserverdetector$lanserver));
        }
    }

    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 30;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return super.getListWidth() + 85;
    }
}
