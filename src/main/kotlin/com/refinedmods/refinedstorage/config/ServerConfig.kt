package com.refinedmods.refinedstorage.config

import reborncore.common.config.Config

open class ServerConfig {
    companion object{
        // Controller
        @JvmField
        @Config(config = "server", category = "controller", key = "useEnergy", comment = "Whether the Controller uses energy")
        var controllerUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "controller", key = "capacity", comment = "The energy capacity of the Controller")
        var controllerCapacity: Int = 32000

        @JvmField
        @Config(config = "server", category = "controller", key = "baseUsage", comment = "The base energy used by the Controller")
        var controllerBaseUsage: Int = 0

        @JvmField
        @Config(config = "server", category = "controller", key = "maxTransfer", comment = "The maximum energy that the Controller can receive")
        var controllerMaxTransfer: Int = Int.MAX_VALUE

        // Cable
        @JvmField
        @Config(config = "server", category = "cable", key = "usage", comment = "The energy used by the Cable")
        var cableUsage: Int = 0

        // Disk Drive
        @JvmField
        @Config(config = "server", category = "diskDrive", key = "usage", comment = "The energy used by the Disk Drive")
        var diskDriveUsage: Int = 0

        @JvmField
        @Config(config = "server", category = "diskDrive", key = "diskUsage", comment = "The energy used per disk in the Disk Drive")
        var diskDriveDiskUsage: Int = 1

        // Grids
        @JvmField
        @Config(config = "server", category = "grid", key = "gridUsage", comment = "The energy used by Grids")
        var gridUsage: Int = 2

        @JvmField
        @Config(config = "server", category = "grid", key = "craftingGridUsage", comment = "The energy used by Crafting Grids")
        var craftingGridUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "grid", key = "patternGridUsage", comment = "The energy used by Pattern Grids")
        var patternGridUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "grid", key = "fluidGridUsage", comment = "The energy used by Fluid Grids")
        var fluidGridUsage: Int = 2

        // Upgrades
        @JvmField
        @Config(config = "server", category = "upgrades", key = "rangeUpgradeUsage", comment = "The additional energy used by the Range Upgrade")
        var rangeUpgradeUsage: Int = 8

        @JvmField
        @Config(config = "server", category = "upgrades", key = "speedUpgradeUsage", comment = "The additional energy used by the Speed Upgrade")
        var speedUpgradeUsage: Int = 2

        @JvmField
        @Config(config = "server", category = "upgrades", key = "craftingUpgradeUsage", comment = "The additional energy used by the Crafting Upgrade")
        var craftingUpgradeUsage: Int = 5

        @JvmField
        @Config(config = "server", category = "upgrades", key = "stackUpgradeUsage", comment = "The additional energy used by the Stack Upgrade")
        var stackUpgradeUsage: Int = 12

        @JvmField
        @Config(config = "server", category = "upgrades", key = "silkTouchUpgradeUsage", comment = "The additional energy used by the Silk Touch Upgrade")
        var silkTouchUpgradeUsage: Int = 15

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune1UpgradeUsage", comment = "The additional energy used by the Fortune 1 Upgrade")
        var fortune1UpgradeUsage: Int = 10

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune2UpgradeUsage", comment = "The additional energy used by the Fortune 2 Upgrade")
        var fortune2UpgradeUsage: Int = 12

        @JvmField
        @Config(config = "server", category = "upgrades", key = "fortune3UpgradeUsage", comment = "The additional energy used by the Fortune 3 Upgrade")
        var fortune3UpgradeUsage: Int = 14

        @JvmField
        @Config(config = "server", category = "upgrades", key = "regulatorUpgradeUsage", comment = "The additional energy used by the Regulator Upgrade")
        var regulatorUpgradeUsage: Int = 15

        // Storage Block
        @JvmField
        @Config(config = "server", category = "storageBlock", key = "oneKUsage", comment = "The energy used by the 1k Storage Block")
        var oneKStorageBlockUsage: Int = 2

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "fourKUsage", comment = "The energy used by the 4k Storage Block")
        var fourKStorageBlockUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "sixteenKUsage", comment = "The energy used by the 16k Storage Block")
        var sixteenKStorageBlockUsage: Int = 6

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "sixtyFourKUsage", comment = "The energy used by the 64k Storage Block")
        var sixtyFourKStorageBlockUsage: Int = 8

        @JvmField
        @Config(config = "server", category = "storageBlock", key = "creativeUsage", comment = "The energy used by the Creative Storage Block")
        var creativeStorageBlockUsage: Int = 10

        // Fluid Storage Block
        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "sixtyFourKUsage", comment = "The energy used by the 64K Fluid Storage Block")
        var sixtyFourKFluidStorageBlockUsage: Int = 2

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "twoHundredFiftySixKUsage", comment = "The energy used by the 256K Fluid Storage Block")
        var twoHundredFiftySixKFluidStorageBlockUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "thousandTwentyFourKUsage", comment = "The energy used by the 1024K Fluid Storage Block")
        var thousandTwentyFourKFluidStorageBlockUsage: Int = 6

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "fourThousandNinetySixKUsage", comment = "The energy used by the 4096K Fluid Storage Block")
        var fourThousandNinetySixKFluidStorageBlockUsage: Int = 8

        @JvmField
        @Config(config = "server", category = "fluidStorageBlock", key = "creativeUsage", comment = "The energy used by the Creative Fluid Storage Block")
        var creativeFluidStorageBlockUsage: Int = 10

        // External Storage
        @JvmField
        @Config(config = "server", category = "externalStorage", key = "usage", comment = "The energy used by the External Storage")
        var externalStorageUsage: Int = 6

        // Importer
        @JvmField
        @Config(config = "server", category = "Importer", key = "usage", comment = "The energy used by the Importer")
        var importerUsage: Int = 1

        // Exporter
        @JvmField
        @Config(config = "server", category = "Exporter", key = "usage", comment = "The energy used by the Exporter")
        var exporterUsage: Int = 1

        // Network Receiver
        @JvmField
        @Config(config = "server", category = "networkReceiver", key = "usage", comment = "The energy used by the Network Receiver")
        var networkReceiverUsage: Int = 0

        // Network Transmitter
        @JvmField
        @Config(config = "server", category = "networkTransmitter", key = "usage", comment = "The energy used by the Network Transmitter")
        var networkTransmitterUsage: Int = 64

        // Relay
        @JvmField
        @Config(config = "server", category = "relay", key = "usage", comment = "The energy used by the Relay")
        var relayUsage: Int = 1

        // Detector
        @JvmField
        @Config(config = "server", category = "detector", key = "usage", comment = "The energy used by the Detector")
        var detectorUsage: Int = 2

        // Security Manager
        @JvmField
        @Config(config = "server", category = "securityManager", key = "usage", comment = "The energy used by the Security Manager")
        var securityManagerUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "securityManager", key = "usagePerCard", comment = "The additional energy used by Security Cards in the Security Manager")
        var securityManagerUsagePerCard: Int = 10

        // Interface
        @JvmField
        @Config(config = "server", category = "interface", key = "usage", comment = "The energy used by the Interface")
        var interfaceUsage: Int = 2

        // Fluid Interface
        @JvmField
        @Config(config = "server", category = "fluidInterface", key = "usage", comment = "The energy used by the Fluid Interface")
        var fluidInterfaceUsage: Int = 2

        // Wireless Transmitter
        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "usage", comment = "The energy used by the Wireless Transmitter")
        var wirelessTransmitterUsage: Int = 8

        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "baseRange", comment = "The base range of the Wireless Transmitter")
        var wirelessTransmitterBaseRange: Int = 16

        @JvmField
        @Config(config = "server", category = "wirelessTransmitter", key = "rangePerUpgrade", comment = "The additional range per Range Upgrade in the Wireless Transmitter")
        var wirelessTransmitterRangePerUpgrade: Int = 8

        // Storage Monitor
        @JvmField
        @Config(config = "server", category = "storageMonitor", key = "usage", comment = "The energy used by the Storage Monitor")
        var storageMonitorUsage: Int = 3

        // Wireless Grid
        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "useEnergy", comment = "Whether the Wireless Grid uses energy")
        var wirelessGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "capacity", comment = "The energy capacity of the Wireless Grid")
        var wirelessGridCapacity: Int = 3200

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "openUsage", comment = "the energy used by the Wireless Grid to open")
        var wirelessGridOpenUsage: Int = 30

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "extractUsage", comment = "The energy used by the Wireless Grid to extract items")
        var wirelessGridExtractUsage: Int = 5

        @JvmField
        @Config(config = "server", category = "wirelessGrid", key = "insertUsage", comment = "The energy used by the Wireless Grid to insert items")
        var wirelessGridInsertUsage: Int = 5

        // Wireless Fluid Grid
        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "useEnergy", comment = "Whether the Wireless Fluid Grid uses energy")
        var wirelessFluidGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "capacity", comment = "The energy capacity of the Wireless Fluid Grid")
        var wirelessFluidGridCapacity: Int = 3200

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "openUsage", comment = "The energy used by the Wireless Fluid Grid to open")
        var wirelessFluidGridOpenUsage: Int = 30

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "extractUsage", comment = "The energy used by the Wireless Fluid Grid to extract fluids")
        var wirelessFluidGridExtractUsage: Int = 5

        @JvmField
        @Config(config = "server", category = "wirelessFluidGrid", key = "insertUsage", comment = "The energy used by the Wireless Fluid Grid to insert fluids")
        var wirelessFluidGridInsertUsage: Int = 5

        // Portable Grid
        @JvmField
        @Config(config = "server", category = "portableGrid", key = "useEnergy", comment = "Whether the Portable Grid uses energy")
        var portableGridUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "capacity", comment = "The energy capacity of the Portable Grid")
        var portableGridCapacity: Int = 3200

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "openUsage", comment = "The energy used by the Portable Grid to open")
        var portableGridOpenUsage: Int = 30

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "extractUsage", comment = "The energy used by the Portable Grid to extract items or fluids")
        var portableGridExtractUsage: Int = 5

        @JvmField
        @Config(config = "server", category = "portableGrid", key = "insertUsage", comment = "The energy used by the Portable Grid to insert items or fluids")
        var portableGridInsertUsage: Int = 5

        // Constructor
        @JvmField
        @Config(config = "server", category = "constructor", key = "usage", comment = "The energy used by the Constructor")
        var constructorUsage: Int = 3

        // Destructor
        @JvmField
        @Config(config = "server", category = "destructor", key = "usage", comment = "The energy used by the Destructor")
        var destructorUsage: Int = 3

        // Disk Manipulator
        @JvmField
        @Config(config = "server", category = "diskManipulator", key = "usage", comment = "The energy used by the Disk Manipulator")
        var diskManipulatorUsage: Int = 4

        // Crafter
        @JvmField
        @Config(config = "server", category = "crafter", key = "usage", comment = "The energy used by the Crafter")
        var crafterUsage: Int = 4

        @JvmField
        @Config(config = "server", category = "crafter", key = "patternUsage", comment = "The energy used for every Pattern in the Crafter")
        var crafterPatternUsage: Int = 1

        // Crafter Manager
        @JvmField
        @Config(config = "server", category = "crafterManager", key = "usage", comment = "The energy used by the Crafter Manager")
        var crafterMonitorUsage: Int = 8

        // Crafting Monitor
        @JvmField
        @Config(config = "server", category = "craftingMonitor", key = "usage", comment = "The energy used by the Crafting Monitor")
        var craftingMonitorUsage: Int = 8

        // Wireless Crafting Monitor
        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "useEnergy", comment = "Whether the Wireless Crafting Monitor uses energy")
        var wirelessCraftingMonitorUseEnergy: Boolean = true

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "capacity", comment = "The energy capacity of the Wireless Crafting Monitor")
        var wirelessCraftingMonitorCapacity: Int = 3200

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "openUsage", comment = "The energy used by the Wireless Crafting Monitor to open")
        var wirelessCraftingMonitorOpenUsage: Int = 30

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "cancelUsage", comment = "The energy used by the Wireless Crafting Monitor to cancel a crafting task")
        var wirelessCraftingMonitorCancelUsage: Int = 5

        @JvmField
        @Config(config = "server", category = "wirelessCraftingMonitor", key = "cancelAllUsage", comment = "The energy used by the Wireless Crafting Monitor to cancel all crafting tasks")
        var wirelessCraftingMonitorCancelAllUsage: Int = 10

        // Autocrafting
        @JvmField
        @Config(config = "server", category = "autocrafting", key = "calculationTimeoutMs", comment = "The autocrafting calculation timeout in milliseconds, crafting tasks taking longer than this to calculate are cancelled to avoid server strain")
        var autocraftingCalculationTimeoutMs: Int = 5000
    }
}