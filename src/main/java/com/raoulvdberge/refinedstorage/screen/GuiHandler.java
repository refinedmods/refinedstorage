package com.raoulvdberge.refinedstorage.screen;

public class GuiHandler {
    /*
    private Object getContainer(int ID, PlayerEntity player, TileEntity tile) {
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
                return new GuiController((ContainerController) getContainer(ID, player, tile), (TileController) tile, player.inventory);
            case RSGui.DISK_DRIVE:
                return new GuiStorage((ContainerDiskDrive) getContainer(ID, player, tile), ((TileDiskDrive) tile).getNode(), "gui/disk_drive.png", player.inventory);
            case RSGui.IMPORTER:
                return new GuiImporter((ContainerImporter) getContainer(ID, player, tile), player.inventory);
            case RSGui.EXPORTER:
                return new GuiExporter((ContainerExporter) getContainer(ID, player, tile), player.inventory);
            case RSGui.DETECTOR:
                return new GuiDetector((ContainerDetector) getContainer(ID, player, tile), player.inventory);
            case RSGui.DESTRUCTOR:
                return new GuiDestructor((ContainerDestructor) getContainer(ID, player, tile), player.inventory);
            case RSGui.CONSTRUCTOR:
                return new GuiConstructor((ContainerConstructor) getContainer(ID, player, tile), player.inventory);
            case RSGui.STORAGE:
                return new GuiStorage((ContainerStorage) getContainer(ID, player, tile), ((TileStorage) tile).getNode(), player.inventory);
            case RSGui.EXTERNAL_STORAGE:
                return new GuiStorage((ContainerExternalStorage) getContainer(ID, player, tile), ((TileExternalStorage) tile).getNode(), player.inventory);
            case RSGui.RELAY:
                return new GuiRelay((ContainerRelay) getContainer(ID, player, tile), player.inventory);
            case RSGui.INTERFACE:
                return new GuiInterface((ContainerInterface) getContainer(ID, player, tile), player.inventory);
            case RSGui.CRAFTING_MONITOR: {
                NetworkNodeCraftingMonitor node = ((TileCraftingMonitor) tile).getNode();

                return new GuiCraftingMonitor(new ContainerCraftingMonitor(node, (TileCraftingMonitor) tile, player), node, player.inventory);
            }
            case RSGui.WIRELESS_TRANSMITTER:
                return new GuiWirelessTransmitter((ContainerWirelessTransmitter) getContainer(ID, player, tile), player.inventory);
            case RSGui.CRAFTER:
                return new GuiCrafter((ContainerCrafter) getContainer(ID, player, tile), player.inventory);
            case RSGui.FILTER:
                return new GuiFilter(getFilterContainer(player, x), player.inventory);
            case RSGui.NETWORK_TRANSMITTER:
                return new GuiNetworkTransmitter((ContainerNetworkTransmitter) getContainer(ID, player, tile), (TileNetworkTransmitter) tile, player.inventory);
            case RSGui.FLUID_INTERFACE:
                return new GuiFluidInterface((ContainerFluidInterface) getContainer(ID, player, tile), player.inventory);
            case RSGui.FLUID_STORAGE:
                return new GuiStorage((ContainerFluidStorage) getContainer(ID, player, tile), ((TileFluidStorage) tile).getNode(), player.inventory);
            case RSGui.DISK_MANIPULATOR:
                return new GuiDiskManipulator((ContainerDiskManipulator) getContainer(ID, player, tile), player.inventory);
            case RSGui.WIRELESS_CRAFTING_MONITOR:
                return getWirelessCraftingMonitorGui(player, x);
            case RSGui.READER_WRITER:
                return new GuiReaderWriter((ContainerReaderWriter) getContainer(ID, player, tile), (IGuiReaderWriter) ((TileNode) tile).getNode(), player.inventory);
            case RSGui.SECURITY_MANAGER:
                return new GuiSecurityManager((ContainerSecurityManager) getContainer(ID, player, tile), (TileSecurityManager) tile, player.inventory);
            case RSGui.STORAGE_MONITOR:
                return new GuiStorageMonitor((ContainerStorageMonitor) getContainer(ID, player, tile), player.inventory);
            case RSGui.CRAFTER_MANAGER:
                GuiCrafterManager crafterManagerGui = new GuiCrafterManager(((TileCrafterManager) tile).getNode(), player.inventory);

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

        return new GuiCraftingMonitor(new ContainerCraftingMonitor(craftingMonitor, null, player), craftingMonitor, player.inventory);
    }

    private ContainerCraftingMonitor getCraftingMonitorContainer(PlayerEntity player, int invIndex) {
        return new ContainerCraftingMonitor(getWirelessCraftingMonitor(player, invIndex), null, player);
    }

    private ContainerFilter getFilterContainer(PlayerEntity player, int hand) {
        return new ContainerFilter(player, player.getHeldItem(Hand.values()[hand]));
    }

     */
}
