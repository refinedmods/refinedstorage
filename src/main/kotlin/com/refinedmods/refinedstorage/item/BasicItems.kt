package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.thinkslynk.fabric.annotations.registry.RegisterItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup

open class CoreItem(type: Type) : Item(Settings().group(RS.MAIN_GROUP)){
    enum class Type {
        CONSTRUCTION, DESTRUCTION
    }
}

@RegisterItem(RS.ID, "construction_core")
class ConstructionCore : CoreItem(CoreItem.Type.CONSTRUCTION)
@RegisterItem(RS.ID, "destruction_core")
class DestructionCore : CoreItem(CoreItem.Type.CONSTRUCTION)


@RegisterItem(RS.ID, "silicon")
class SiliconItem : Item(Settings().group(RS.MAIN_GROUP))
@RegisterItem(RS.ID, "storage_housing")
class StorageHousingItem : Item(Settings().group(RS.MAIN_GROUP))
@RegisterItem(RS.ID, "quartz_enriched_iron")
class QuartzEnrichedIronItem : Item(Settings().group(RS.MAIN_GROUP))
@RegisterItem(RS.ID,"processor_binding")
class ProcessorBindingItem : Item(Settings().group(RS.MAIN_GROUP))