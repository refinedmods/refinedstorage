package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

@RegisterItem(RS.ID, "quartz_enriched_iron")
class QuartzEnrichedIronItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "silicon")
class SiliconItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID,"processor_binding")
class ProcessorBindingItem : Item(Settings().group(RS.MAIN_GROUP))

// wrench

// pattern

// filter

@RegisterItem(RS.ID, "storage_housing")
class StorageHousingItem : Item(Settings().group(RS.MAIN_GROUP))

// network_card

// security_card

@RegisterItem(RS.ID, "construction_core")
class ConstructionCoreItem : Item(Settings().group(RS.MAIN_GROUP))
@RegisterItem(RS.ID, "destruction_core")
class DestructionCoreItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "raw_basic_processor")
class RawBasicProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "raw_improved_processor")
class RawImprovedProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "raw_advanced_processor")
class RawAdvancedProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "basic_processor")
class BasicProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "improved_processor")
class ImprovedProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "advanced_processor")
class AdvancedProcessorItem : Item(Settings().group(RS.MAIN_GROUP))

// upgrade

// speed_upgrade

// range_upgrade

// crafting_upgrade

// stack_upgrade

// silk_touch_upgrade

// fortune_1_upgrade

// fortune_2_upgrade

// fortune_3_upgrade

// wireless_grid

// creative_wireless_grid

// wireless_fluid_grid

// creative_wireless_fluid_grid

@RegisterItem(RS.ID, "1k_storage_part")
class OneKStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "4k_storage_part")
class FourKStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "16k_storage_part")
class SixteenKStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "64k_storage_part")
class SixtyFourKStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

// 1k_storage_disk

// 4k_storage_disk

// 16k_storage_disk

// 64k_storage_disk

// creative_storage_disk

@RegisterItem(RS.ID, "64k_fluid_storage_part")
class SixtyFourKFluidStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "256k_fluid_storage_part")
class TwoHundredFiftySixKFluidStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "1024k_fluid_storage_part")
class ThousandTwentyFourKFluidStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

@RegisterItem(RS.ID, "4096k_fluid_storage_part")
class FourThousandNinetySixKFluidStoragePartItem : Item(Settings().group(RS.MAIN_GROUP))

// 64k_fluid_storage_disk

// 256k_fluid_storage_disk

// 1024k_fluid_storage_disk

// 4096k_fluid_storage_disk

// creative_fluid_storage_disk

// wireless_crafting_monitor

// creative_wireless_crafting_monitor