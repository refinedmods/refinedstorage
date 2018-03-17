package com.raoulvdberge.refinedstorage.gui;

import com.raoulvdberge.refinedstorage.RSGui;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiReaderWriter;
import com.raoulvdberge.refinedstorage.container.*;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.integration.mcmp.IntegrationMCMP;
import com.raoulvdberge.refinedstorage.integration.mcmp.RSMCMPAddon;
import com.raoulvdberge.refinedstorage.tile.*;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.TileCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.TilePortableGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    private Container getContainer(int ID, EntityPlayer player, TileEntity tile) {
        switch (ID) {
            case RSGui.CONTROLLER:
                return new ContainerController((TileController) tile, player);
            case RSGui.GRID:
                return new ContainerGrid(((TileGrid) tile).getNode(), new ResizableDisplayDummy(), (TileGrid) tile, player);
            case RSGui.PORTABLE_GRID:
                return new ContainerGrid((TilePortableGrid) tile, new ResizableDisplayDummy(), (TilePortableGrid) tile, player);
            case RSGui.DISK_DRIVE:
                return new ContainerDiskDrive((TileDiskDrive) tile, player);
            case RSGui.IMPORTER:
                return new ContainerImporter((TileImporter) tile, player);
            case RSGui.EXPORTER:
                return new ContainerExporter((TileExporter) tile, player);
            case RSGui.DETECTOR:
                return new ContainerDetector((TileDetector) tile, player);
            case RSGui.SOLDERER:
                return new ContainerSolderer((TileSolderer) tile, player);
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
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == RSGui.WIRELESS_GRID) {
            return getGridContainer(player, x, y, z);
        } else if (ID == RSGui.FILTER) {
            return getFilterContainer(player, x);
        } else if (ID == RSGui.WIRELESS_CRAFTING_MONITOR) {
            return getCraftingMonitorContainer(player, x, y);
        }

        return getContainer(ID, player, IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, new BlockPos(x, y, z)) : world.getTileEntity(new BlockPos(x, y, z)));
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = IntegrationMCMP.isLoaded() ? RSMCMPAddon.unwrapTile(world, new BlockPos(x, y, z)) : world.getTileEntity(new BlockPos(x, y, z));

        switch (ID) {
            case RSGui.CONTROLLER:
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile);
            case RSGui.GRID:
            case RSGui.PORTABLE_GRID:
                IGrid grid = ID == RSGui.GRID ? ((TileGrid) tile).getNode() : (TilePortableGrid) tile;
                GuiGrid gui = new GuiGrid(null, grid);
                gui.inventorySlots = new ContainerGrid(grid, gui, null, player);
                return gui;
            case RSGui.WIRELESS_GRID:
                return getGridGui(player, x, y, z);
            case RSGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), ((TileDiskDrive) tile).getNode(), "gui/disk_drive.png");
            case RSGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile));
            case RSGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile));
            case RSGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile));
            case RSGui.SOLDERER:
                return new GuiSolderer((ContainerSolderer) getContainer(ID, player, tile));
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
            case RSGui.CRAFTING_MONITOR:
                return new GuiCraftingMonitor((ContainerCraftingMonitor) getContainer(ID, player, tile), ((TileCraftingMonitor) tile).getNode());
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
                return getCraftingMonitorGui(player, x, y);
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

    private IGrid getGrid(EntityPlayer player, int hand, int networkDimension, int id) {
        return API.instance().getWirelessGridRegistry().get(id).create(player, EnumHand.values()[hand], networkDimension);
    }

    private GuiGrid getGridGui(EntityPlayer player, int hand, int networkDimension, int id) {
        IGrid grid = getGrid(player, hand, networkDimension, id);

        GuiGrid gui = new GuiGrid(null, grid);
        gui.inventorySlots = new ContainerGrid(grid, gui, null, player);
        return gui;
    }

    private ContainerGrid getGridContainer(EntityPlayer player, int hand, int networkDimension, int id) {
        return new ContainerGrid(getGrid(player, hand, networkDimension, id), new ResizableDisplayDummy(), null, player);
    }

    private WirelessCraftingMonitor getCraftingMonitor(EntityPlayer player, int hand, int networkDimension) {
        return new WirelessCraftingMonitor(networkDimension, player.getHeldItem(EnumHand.values()[hand]));
    }

    private GuiCraftingMonitor getCraftingMonitorGui(EntityPlayer player, int hand, int networkDimension) {
        WirelessCraftingMonitor craftingMonitor = getCraftingMonitor(player, hand, networkDimension);

        return new GuiCraftingMonitor(new ContainerCraftingMonitor(craftingMonitor, null, player), craftingMonitor);
    }

    private ContainerCraftingMonitor getCraftingMonitorContainer(EntityPlayer player, int hand, int networkDimension) {
        return new ContainerCraftingMonitor(getCraftingMonitor(player, hand, networkDimension), null, player);
    }

    private ContainerFilter getFilterContainer(EntityPlayer player, int hand) {
        return new ContainerFilter(player, player.getHeldItem(EnumHand.values()[hand]));
    }
}
