package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeCraftingMonitor;
import com.raoulvdberge.refinedstorage.container.*;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, PlayerEntity player, TileEntity tile) {
        switch (ID) {
            case RSGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case RSGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case RSGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case RSGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case RSGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case RSGui.DESTRUCTOR:
                return new ContainerDestructor((TileDestructor) tile, player);
            case RSGui.CONSTRUCTOR:
                return new ContainerConstructor((TileConstructor) tile, player);
            case RSGui.STORAGE:
                return new ContainerStorage((TileStorage) tile, player);
            case RSGui.EXTERNAL_STORAGE:
                return new ContainerExternalStorage((TileExternalStorage) tile, player);
            case RSGui.RELAY:
                return new ContainerRelay((TileRelay) tile, player);
            case RSGui.INTERFACE:
                return new ContainerInterface((TileInterface) tile, player);
            case RSGui.CRAFTING_MONITOR:
                return new ContainerCraftingMonitor(((TileCraftingMonitor) tile).getNode(), (TileCraftingMonitor) tile, player);
            case RSGui.WIRELESS_TRANSMITTER:
                return new ContainerWirelessTransmitter((TileWirelessTransmitter) tile, player);
            case RSGui.CRAFTER:
                return new ContainerCrafter((TileCrafter) tile, player);
            case RSGui.NETWORK_TRANSMITTER:
                return new ContainerNetworkTransmitter((TileNetworkTransmitter) tile, player);
            case RSGui.FLUID_INTERFACE:
                return new ContainerFluidInterface((TileFluidInterface) tile, player);
            case RSGui.FLUID_STORAGE:
                return new ContainerFluidStorage((TileFluidStorage) tile, player);
            case RSGui.DISK_MANIPULATOR:
                return new ContainerDiskManipulator((TileDiskManipulator) tile, player);
            case RSGui.READER_WRITER:
                return new ContainerReaderWriter((IGuiReaderWriter) ((TileNode) tile).getNode(), (TileBase) tile, player);
            case RSGui.SECURITY_MANAGER:
                return new ContainerSecurityManager((TileSecurityManager) tile, player);
            case RSGui.STORAGE_MONITOR:
                return new ContainerStorageMonitor((TileStorageMonitor) tile, player);
            case RSGui.CRAFTER_MANAGER:
                return new ContainerCrafterManager((TileCrafterManager) tile, player, new ResizableDisplayDummy());
            default:
                return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        if (ID == RSGui.FILTER) {
            return getFilterContainer(player, x);
        } else if (ID == RSGui.WIRELESS_CRAFTING_MONITOR) {
            return getCraftingMonitorContainer(player, x);
        }

        return getContainer(ID, player, world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, PlayerEntity player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RSGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case RSGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), ((TileDiskDrive) tile).getNode(), "gui/disk_drive.png");
            case RSGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case RSGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case RSGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case RSGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile));
            case RSGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile));
            case RSGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), ((TileStorage) tile).getNode());
            case RSGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), ((TileExternalStorage) tile).getNode());
            case RSGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile));
            case RSGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile));
            case RSGui.CRAFTING_MONITOR: {
                NetworkNodeCraftingMonitor node = ((TileCraftingMonitor) tile).getNode();
                GuiCraftingMonitor gui = new GuiCraftingMonitor(null, node);
                gui.inventorySlots = new ContainerCraftingMonitor(node, (TileCraftingMonitor) tile, player);
                return gui;
            }
            case RSGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile));
            case RSGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile));
            case RSGui.FILTER:
                return new GuiFilter(getFilterContainer(player, x));
            case RSGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile);
            case RSGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile));
            case RSGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), ((TileFluidStorage) tile).getNode());
            case RSGui.DISK_MANIPULATOR:
                return new GuiDiskManipulator((ContainerDiskManipulator) getContainer(ID, player, tile));
            case RSGui.WIRELESS_CRAFTING_MONITOR:
                return getWirelessCraftingMonitorGui(player, x);
            case RSGui.READER_WRITER:
                return new GuiReaderWriter((ContainerReaderWriter) getContainer(ID, player, tile), (IGuiReaderWriter) ((TileNode) tile).getNode());
            case RSGui.SECURITY_MANAGER:
                return new GuiSecurityManager((ContainerSecurityManager) getContainer(ID, player, tile), (TileSecurityManager) tile);
            case RSGui.STORAGE_MONITOR:
                return new GuiStorageMonitor((ContainerStorageMonitor) getContainer(ID, player, tile));
            case RSGui.CRAFTER_MANAGER:
                GuiCrafterManager crafterManagerGui = new GuiCrafterManager(((TileCrafterManager) tile).getNode());
                crafterManagerGui.setContainer(new ContainerCrafterManager((TileCrafterManager) tile, player, crafterManagerGui));
                return crafterManagerGui;
            default:
                return null;
        }
    }

    private WirelessCraftingMonitor getWirelessCraftingMonitor(PlayerEntity player, int invIndex) {
        return new WirelessCraftingMonitor(player.inventory.getStackInSlot(invIndex));
    }

    private GuiCraftingMonitor getWirelessCraftingMonitorGui(PlayerEntity player, int invIndex) {
        WirelessCraftingMonitor craftingMonitor = getWirelessCraftingMonitor(player, invIndex);

        GuiCraftingMonitor gui = new GuiCraftingMonitor(null, craftingMonitor);
        gui.inventorySlots = new ContainerCraftingMonitor(craftingMonitor, null, player);
        return gui;
    }

    private ContainerCraftingMonitor getCraftingMonitorContainer(PlayerEntity player, int invIndex) {
        return new ContainerCraftingMonitor(getWirelessCraftingMonitor(player, invIndex), null, player);
    }

    private ContainerFilter getFilterContainer(PlayerEntity player, int hand) {
        return new ContainerFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }
}
