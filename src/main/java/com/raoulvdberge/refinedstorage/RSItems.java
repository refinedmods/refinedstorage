package com.raoulvdberge.refinedstorage;

import com.raoulvdberge.refinedstorage.item.*;
import net.minecraftforge.registries.ObjectHolder;

public final class RSItems {
    public static final ItemStorageDisk STORAGE_DISK = new ItemStorageDisk();
    public static final ItemWirelessGrid WIRELESS_GRID = new ItemWirelessGrid();
    public static final ItemWirelessFluidGrid WIRELESS_FLUID_GRID = new ItemWirelessFluidGrid();
    public static final ItemWirelessCraftingMonitor WIRELESS_CRAFTING_MONITOR = new ItemWirelessCraftingMonitor();
    @ObjectHolder(RS.ID + ":quartz_enriched_iron")
    public static final ItemQuartzEnrichedIron QUARTZ_ENRICHED_IRON = null;
    @ObjectHolder(RS.ID + ":construction_core")
    public static final ItemCore CONSTRUCTION_CORE = null;
    @ObjectHolder(RS.ID + ":destruction_core")
    public static final ItemCore DESTRUCTION_CORE = null;
    @ObjectHolder(RS.ID + ":silicon")
    public static final ItemSilicon SILICON = null;
    @ObjectHolder(RS.ID + ":raw_basic_processor")
    public static final ItemProcessor RAW_BASIC_PROCESSOR = null;
    @ObjectHolder(RS.ID + ":raw_improved_processor")
    public static final ItemProcessor RAW_IMPROVED_PROCESSOR = null;
    @ObjectHolder(RS.ID + ":raw_advanced_processor")
    public static final ItemProcessor RAW_ADVANCED_PROCESSOR = null;
    @ObjectHolder(RS.ID + ":basic_processor")
    public static final ItemProcessor BASIC_PROCESSOR = null;
    @ObjectHolder(RS.ID + ":improved_processor")
    public static final ItemProcessor IMPROVED_PROCESSOR = null;
    @ObjectHolder(RS.ID + ":advanced_processor")
    public static final ItemProcessor ADVANCED_PROCESSOR = null;
    public static final ItemStoragePart STORAGE_PART = new ItemStoragePart();
    public static final ItemPattern PATTERN = new ItemPattern();
    public static final ItemUpgrade UPGRADE = new ItemUpgrade();
    public static final ItemStorageHousing STORAGE_HOUSING = new ItemStorageHousing();
    public static final ItemFilter FILTER = new ItemFilter();
    @ObjectHolder(RS.ID + ":network_card")
    public static final ItemNetworkCard NETWORK_CARD = null;
    public static final ItemFluidStorageDisk FLUID_STORAGE_DISK = new ItemFluidStorageDisk();
    public static final ItemFluidStoragePart FLUID_STORAGE_PART = new ItemFluidStoragePart();
    @ObjectHolder(RS.ID + ":security_card")
    public static final ItemSecurityCard SECURITY_CARD = null;
    @ObjectHolder(RS.ID + ":cutting_tool")
    public static final ItemCuttingTool CUTTING_TOOL = null;
    public static final ItemCover COVER = new ItemCover();
    public static final ItemHollowCover HOLLOW_COVER = new ItemHollowCover();
    public static final ItemWrench WRENCH = new ItemWrench();
    @ObjectHolder(RS.ID + ":processor_binding")
    public static final ItemProcessorBinding PROCESSOR_BINDING = null;
}
