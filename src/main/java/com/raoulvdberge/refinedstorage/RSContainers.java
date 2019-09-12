package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.container.*;
import com.raoulvdberge.refinedstorage.container.factory.TileContainerFactory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;

public final class RSContainers {
    //@ObjectHolder(RS.ID + ":constructor")
    public static final ContainerType<ContainerConstructor> CONSTRUCTOR = null;
    //@ObjectHolder(RS.ID + ":controller")
    public static final ContainerType<ContainerController> CONTROLLER = null;
    //@ObjectHolder(RS.ID + ":crafter")
    public static final ContainerType<ContainerCrafter> CRAFTER = null;
    //@ObjectHolder(RS.ID + ":crafter_manager")
    public static final ContainerType<ContainerCrafter> CRAFTER_MANAGER = null;
    //@ObjectHolder(RS.ID + ":crafting_monitor")
    public static final ContainerType<ContainerCraftingMonitor> CRAFTING_MONITOR = null;
    //@ObjectHolder(RS.ID + ":destructor")
    public static final ContainerType<ContainerDestructor> DESTRUCTOR = null;
    //@ObjectHolder(RS.ID + ":detector")
    public static final ContainerType<ContainerDetector> DETECTOR = null;
    //@ObjectHolder(RS.ID + ":disk_drive")
    public static final ContainerType<ContainerDiskDrive> DISK_DRIVE = null;
    //@ObjectHolder(RS.ID + ":disk_manipulator")
    public static final ContainerType<ContainerDiskManipulator> DISK_MANIPULATOR = null;
    //@ObjectHolder(RS.ID + ":exporter")
    public static final ContainerType<ContainerExporter> EXPORTER = null;
    //@ObjectHolder(RS.ID + ":external_storage")
    public static final ContainerType<ContainerExternalStorage> EXTERNAL_STORAGE = null;
    //@ObjectHolder(RS.ID + ":filter")
    public static final ContainerType<ContainerFilter> FILTER = null;
    //@ObjectHolder(RS.ID + ":fluid_interface")
    public static final ContainerType<ContainerFluidInterface> FLUID_INTERFACE = null;
    //@ObjectHolder(RS.ID + ":fluid_storage")
    public static final ContainerType<ContainerFluidInterface> FLUID_STORAGE = null;
    //@ObjectHolder(RS.ID + ":grid")
    public static final ContainerType<ContainerGrid> GRID = null;
    //@ObjectHolder(RS.ID + ":importer")
    public static final ContainerType<ContainerImporter> IMPORTER = null;
    //@ObjectHolder(RS.ID + ":interface")
    public static final ContainerType<ContainerImporter> INTERFACE = null;
    //@ObjectHolder(RS.ID + ":network_transmitter")
    public static final ContainerType<ContainerNetworkTransmitter> NETWORK_TRANSMITTER = null;
    //@ObjectHolder(RS.ID + ":reader_writer")
    public static final ContainerType<ContainerReaderWriter> READER_WRITER = null;
    //@ObjectHolder(RS.ID + ":relay")
    public static final ContainerType<ContainerRelay> RELAY = null;
    //@ObjectHolder(RS.ID + ":security_manager")
    public static final ContainerType<ContainerSecurityManager> SECURITY_MANAGER = null;
    //@ObjectHolder(RS.ID + ":storage")
    public static final ContainerType<ContainerStorage> STORAGE = null;
    //@ObjectHolder(RS.ID + ":storage_monitor")
    public static final ContainerType<ContainerStorageMonitor> STORAGE_MONITOR = null;
    //@ObjectHolder(RS.ID + ":wireless_transmitter")
    public static final ContainerType<ContainerSecurityManager> WIRELESS_TRANSMITTER = null;

    public void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create(new TileContainerFactory<>((ContainerConstructor::new))).setRegistryName(RS.ID, "constructor"));
    }
}
