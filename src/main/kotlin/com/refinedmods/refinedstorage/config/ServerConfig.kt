package com.refinedmods.refinedstorage.config

import net.minecraftforge.common.ForgeConfigSpec

class ServerConfig {
    private val builder: ForgeConfigSpec.Builder = Builder()
    private val spec: ForgeConfigSpec
    val upgrades: Upgrades
    val controller: Controller
    val cable: Cable
    val grid: Grid
    val diskDrive: DiskDrive
    val storageBlock: StorageBlock
    val fluidStorageBlock: FluidStorageBlock
    val externalStorage: ExternalStorage
    val importer: Importer
    val exporter: Exporter
    val networkReceiver: NetworkReceiver
    val networkTransmitter: NetworkTransmitter
    val relay: Relay
    val detector: Detector
    val securityManager: SecurityManager
    val `interface`: Interface
    val fluidInterface: FluidInterface
    val wirelessTransmitter: WirelessTransmitter
    val storageMonitor: StorageMonitor
    val wirelessGrid: WirelessGrid
    val wirelessFluidGrid: WirelessFluidGrid
    val constructor: Constructor
    val destructor: Destructor
    val diskManipulator: DiskManipulator
    val portableGrid: PortableGrid
    val crafter: Crafter
    val crafterManager: CrafterManager
    val craftingMonitor: CraftingMonitor
    val wirelessCraftingMonitor: WirelessCraftingMonitor
    val autocrafting: Autocrafting
    fun getSpec(): ForgeConfigSpec {
        return spec
    }

    inner class Controller {
        private val useEnergy: ForgeConfigSpec.BooleanValue
        private val capacity: ForgeConfigSpec.IntValue
        private val baseUsage: ForgeConfigSpec.IntValue
        private val maxTransfer: ForgeConfigSpec.IntValue
        fun getUseEnergy(): Boolean {
            return useEnergy.get()
        }

        fun getCapacity(): Int {
            return capacity.get()
        }

        fun getBaseUsage(): Int {
            return baseUsage.get()
        }

        fun getMaxTransfer(): Int {
            return maxTransfer.get()
        }

        init {
            builder.push("controller")
            useEnergy = builder.comment("Whether the Controller uses energy").define("useEnergy", true)
            capacity = builder.comment("The energy capacity of the Controller").defineInRange("capacity", 32000, 0, Int.MAX_VALUE)
            baseUsage = builder.comment("The base energy used by the Controller").defineInRange("baseUsage", 0, 0, Int.MAX_VALUE)
            maxTransfer = builder.comment("The maximum energy that the Controller can receive").defineInRange("maxTransfer", Int.MAX_VALUE, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Cable {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("cable")
            usage = builder.comment("The energy used by the Cable").defineInRange("usage", 0, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class DiskDrive {
        private val usage: ForgeConfigSpec.IntValue
        private val diskUsage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        fun getDiskUsage(): Int {
            return diskUsage.get()
        }

        init {
            builder.push("diskDrive")
            usage = builder.comment("The energy used by the Disk Drive").defineInRange("usage", 0, 0, Int.MAX_VALUE)
            diskUsage = builder.comment("The energy used per disk in the Disk Drive").defineInRange("diskUsage", 1, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Grid {
        private val gridUsage: ForgeConfigSpec.IntValue
        private val craftingGridUsage: ForgeConfigSpec.IntValue
        private val patternGridUsage: ForgeConfigSpec.IntValue
        private val fluidGridUsage: ForgeConfigSpec.IntValue
        fun getGridUsage(): Int {
            return gridUsage.get()
        }

        fun getCraftingGridUsage(): Int {
            return craftingGridUsage.get()
        }

        fun getPatternGridUsage(): Int {
            return patternGridUsage.get()
        }

        fun getFluidGridUsage(): Int {
            return fluidGridUsage.get()
        }

        init {
            builder.push("grid")
            gridUsage = builder.comment("The energy used by Grids").defineInRange("gridUsage", 2, 0, Int.MAX_VALUE)
            craftingGridUsage = builder.comment("The energy used by Crafting Grids").defineInRange("craftingGridUsage", 4, 0, Int.MAX_VALUE)
            patternGridUsage = builder.comment("The energy used by Pattern Grids").defineInRange("patternGridUsage", 4, 0, Int.MAX_VALUE)
            fluidGridUsage = builder.comment("The energy used by Fluid Grids").defineInRange("fluidGridUsage", 2, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Upgrades {
        private val rangeUpgradeUsage: ForgeConfigSpec.IntValue
        private val speedUpgradeUsage: ForgeConfigSpec.IntValue
        private val craftingUpgradeUsage: ForgeConfigSpec.IntValue
        private val stackUpgradeUsage: ForgeConfigSpec.IntValue
        private val silkTouchUpgradeUsage: ForgeConfigSpec.IntValue
        private val fortune1UpgradeUsage: ForgeConfigSpec.IntValue
        private val fortune2UpgradeUsage: ForgeConfigSpec.IntValue
        private val fortune3UpgradeUsage: ForgeConfigSpec.IntValue
        private val regulatorUpgradeUsage: ForgeConfigSpec.IntValue
        fun getRangeUpgradeUsage(): Int {
            return rangeUpgradeUsage.get()
        }

        fun getSpeedUpgradeUsage(): Int {
            return speedUpgradeUsage.get()
        }

        fun getCraftingUpgradeUsage(): Int {
            return craftingUpgradeUsage.get()
        }

        fun getStackUpgradeUsage(): Int {
            return stackUpgradeUsage.get()
        }

        fun getSilkTouchUpgradeUsage(): Int {
            return silkTouchUpgradeUsage.get()
        }

        fun getFortune1UpgradeUsage(): Int {
            return fortune1UpgradeUsage.get()
        }

        fun getFortune2UpgradeUsage(): Int {
            return fortune2UpgradeUsage.get()
        }

        fun getFortune3UpgradeUsage(): Int {
            return fortune3UpgradeUsage.get()
        }

        fun getRegulatorUpgradeUsage(): Int {
            return regulatorUpgradeUsage.get()
        }

        init {
            builder.push("upgrades")
            rangeUpgradeUsage = builder.comment("The additional energy used by the Range Upgrade").defineInRange("rangeUpgradeUsage", 8, 0, Int.MAX_VALUE)
            speedUpgradeUsage = builder.comment("The additional energy used by the Speed Upgrade").defineInRange("speedUpgradeUsage", 2, 0, Int.MAX_VALUE)
            craftingUpgradeUsage = builder.comment("The additional energy used by the Crafting Upgrade").defineInRange("craftingUpgradeUsage", 5, 0, Int.MAX_VALUE)
            stackUpgradeUsage = builder.comment("The additional energy used by the Stack Upgrade").defineInRange("stackUpgradeUsage", 12, 0, Int.MAX_VALUE)
            silkTouchUpgradeUsage = builder.comment("The additional energy used by the Silk Touch Upgrade").defineInRange("silkTouchUpgradeUsage", 15, 0, Int.MAX_VALUE)
            fortune1UpgradeUsage = builder.comment("The additional energy used by the Fortune 1 Upgrade").defineInRange("fortune1UpgradeUsage", 10, 0, Int.MAX_VALUE)
            fortune2UpgradeUsage = builder.comment("The additional energy used by the Fortune 2 Upgrade").defineInRange("fortune2UpgradeUsage", 12, 0, Int.MAX_VALUE)
            fortune3UpgradeUsage = builder.comment("The additional energy used by the Fortune 3 Upgrade").defineInRange("fortune3UpgradeUsage", 14, 0, Int.MAX_VALUE)
            regulatorUpgradeUsage = builder.comment("The additional energy used by the Regulator Upgrade").defineInRange("regulatorUpgradeUsage", 15, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class StorageBlock {
        private val oneKUsage: ForgeConfigSpec.IntValue
        private val fourKUsage: ForgeConfigSpec.IntValue
        private val sixteenKUsage: ForgeConfigSpec.IntValue
        private val sixtyFourKUsage: ForgeConfigSpec.IntValue
        private val creativeUsage: ForgeConfigSpec.IntValue
        fun getOneKUsage(): Int {
            return oneKUsage.get()
        }

        fun getFourKUsage(): Int {
            return fourKUsage.get()
        }

        fun getSixteenKUsage(): Int {
            return sixteenKUsage.get()
        }

        fun getSixtyFourKUsage(): Int {
            return sixtyFourKUsage.get()
        }

        fun getCreativeUsage(): Int {
            return creativeUsage.get()
        }

        init {
            builder.push("storageBlock")
            oneKUsage = builder.comment("The energy used by the 1k Storage Block").defineInRange("oneKUsage", 2, 0, Int.MAX_VALUE)
            fourKUsage = builder.comment("The energy used by the 4k Storage Block").defineInRange("fourKUsage", 4, 0, Int.MAX_VALUE)
            sixteenKUsage = builder.comment("The energy used by the 16k Storage Block").defineInRange("sixteenKUsage", 6, 0, Int.MAX_VALUE)
            sixtyFourKUsage = builder.comment("The energy used by the 64k Storage Block").defineInRange("sixtyFourKUsage", 8, 0, Int.MAX_VALUE)
            creativeUsage = builder.comment("The energy used by the Creative Storage Block").defineInRange("creativeUsage", 10, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class FluidStorageBlock {
        private val sixtyFourKUsage: ForgeConfigSpec.IntValue
        private val twoHundredFiftySixKUsage: ForgeConfigSpec.IntValue
        private val thousandTwentyFourKUsage: ForgeConfigSpec.IntValue
        private val fourThousandNinetySixKUsage: ForgeConfigSpec.IntValue
        private val creativeUsage: ForgeConfigSpec.IntValue
        fun getSixtyFourKUsage(): Int {
            return sixtyFourKUsage.get()
        }

        fun getTwoHundredFiftySixKUsage(): Int {
            return twoHundredFiftySixKUsage.get()
        }

        fun getThousandTwentyFourKUsage(): Int {
            return thousandTwentyFourKUsage.get()
        }

        fun getFourThousandNinetySixKUsage(): Int {
            return fourThousandNinetySixKUsage.get()
        }

        fun getCreativeUsage(): Int {
            return creativeUsage.get()
        }

        init {
            builder.push("fluidStorageBlock")
            sixtyFourKUsage = builder.comment("The energy used by the 64k Fluid Storage Block").defineInRange("sixtyFourKUsage", 2, 0, Int.MAX_VALUE)
            twoHundredFiftySixKUsage = builder.comment("The energy used by the 256k Fluid Storage Block").defineInRange("twoHundredFiftySixKUsage", 4, 0, Int.MAX_VALUE)
            thousandTwentyFourKUsage = builder.comment("The energy used by the 1024k Fluid Storage Block").defineInRange("thousandTwentyFourKUsage", 6, 0, Int.MAX_VALUE)
            fourThousandNinetySixKUsage = builder.comment("The energy used by the 4096k Fluid Storage Block").defineInRange("fourThousandNinetySixKUsage", 8, 0, Int.MAX_VALUE)
            creativeUsage = builder.comment("The energy used by the Creative Fluid Storage Block").defineInRange("creativeUsage", 10, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class ExternalStorage {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("externalStorage")
            usage = builder.comment("The energy used by the External Storage").defineInRange("usage", 6, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Importer {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("importer")
            usage = builder.comment("The energy used by the Importer").defineInRange("usage", 1, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Exporter {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("exporter")
            usage = builder.comment("The energy used by the Exporter").defineInRange("usage", 1, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class NetworkReceiver {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("networkReceiver")
            usage = builder.comment("The energy used by the Network Receiver").defineInRange("usage", 0, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class NetworkTransmitter {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("networkTransmitter")
            usage = builder.comment("The energy used by the Network Transmitter").defineInRange("usage", 64, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Relay {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("relay")
            usage = builder.comment("The energy used by the Relay").defineInRange("usage", 1, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Detector {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("detector")
            usage = builder.comment("The energy used by the Detector").defineInRange("usage", 2, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class SecurityManager {
        private val usage: ForgeConfigSpec.IntValue
        private val usagePerCard: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        fun getUsagePerCard(): Int {
            return usagePerCard.get()
        }

        init {
            builder.push("securityManager")
            usage = builder.comment("The energy used by the Security Manager").defineInRange("usage", 4, 0, Int.MAX_VALUE)
            usagePerCard = builder.comment("The additional energy used by Security Cards in the Security Manager").defineInRange("usagePerCard", 10, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Interface {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("interface")
            usage = builder.comment("The energy used by the Interface").defineInRange("usage", 2, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class FluidInterface {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("fluidInterface")
            usage = builder.comment("The energy used by the Fluid Interface").defineInRange("usage", 2, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class WirelessTransmitter {
        private val usage: ForgeConfigSpec.IntValue
        private val baseRange: ForgeConfigSpec.IntValue
        private val rangePerUpgrade: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        fun getBaseRange(): Int {
            return baseRange.get()
        }

        fun getRangePerUpgrade(): Int {
            return rangePerUpgrade.get()
        }

        init {
            builder.push("wirelessTransmitter")
            usage = builder.comment("The energy used by the Wireless Transmitter").defineInRange("usage", 8, 0, Int.MAX_VALUE)
            baseRange = builder.comment("The base range of the Wireless Transmitter").defineInRange("baseRange", 16, 0, Int.MAX_VALUE)
            rangePerUpgrade = builder.comment("The additional range per Range Upgrade in the Wireless Transmitter").defineInRange("rangePerUpgrade", 8, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class StorageMonitor {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("storageMonitor")
            usage = builder.comment("The energy used by the Storage Monitor").defineInRange("usage", 3, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class WirelessGrid {
        private val useEnergy: ForgeConfigSpec.BooleanValue
        private val capacity: ForgeConfigSpec.IntValue
        private val openUsage: ForgeConfigSpec.IntValue
        private val extractUsage: ForgeConfigSpec.IntValue
        private val insertUsage: ForgeConfigSpec.IntValue
        fun getUseEnergy(): Boolean {
            return useEnergy.get()
        }

        fun getCapacity(): Int {
            return capacity.get()
        }

        fun getOpenUsage(): Int {
            return openUsage.get()
        }

        fun getExtractUsage(): Int {
            return extractUsage.get()
        }

        fun getInsertUsage(): Int {
            return insertUsage.get()
        }

        init {
            builder.push("wirelessGrid")
            useEnergy = builder.comment("Whether the Wireless Grid uses energy").define("useEnergy", true)
            capacity = builder.comment("The energy capacity of the Wireless Grid").defineInRange("capacity", 3200, 0, Int.MAX_VALUE)
            openUsage = builder.comment("The energy used by the Wireless Grid to open").defineInRange("openUsage", 30, 0, Int.MAX_VALUE)
            extractUsage = builder.comment("The energy used by the Wireless Grid to extract items").defineInRange("extractUsage", 5, 0, Int.MAX_VALUE)
            insertUsage = builder.comment("The energy used by the Wireless Grid to insert items").defineInRange("insertUsage", 5, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class WirelessFluidGrid {
        private val useEnergy: ForgeConfigSpec.BooleanValue
        private val capacity: ForgeConfigSpec.IntValue
        private val openUsage: ForgeConfigSpec.IntValue
        private val extractUsage: ForgeConfigSpec.IntValue
        private val insertUsage: ForgeConfigSpec.IntValue
        fun getUseEnergy(): Boolean {
            return useEnergy.get()
        }

        fun getCapacity(): Int {
            return capacity.get()
        }

        fun getOpenUsage(): Int {
            return openUsage.get()
        }

        fun getExtractUsage(): Int {
            return extractUsage.get()
        }

        fun getInsertUsage(): Int {
            return insertUsage.get()
        }

        init {
            builder.push("wirelessFluidGrid")
            useEnergy = builder.comment("Whether the Wireless Fluid Grid uses energy").define("useEnergy", true)
            capacity = builder.comment("The energy capacity of the Wireless Fluid Grid").defineInRange("capacity", 3200, 0, Int.MAX_VALUE)
            openUsage = builder.comment("The energy used by the Wireless Fluid Grid to open").defineInRange("openUsage", 30, 0, Int.MAX_VALUE)
            extractUsage = builder.comment("The energy used by the Wireless Fluid Grid to extract fluids").defineInRange("extractUsage", 5, 0, Int.MAX_VALUE)
            insertUsage = builder.comment("The energy used by the Wireless Fluid Grid to insert fluids").defineInRange("insertUsage", 5, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class PortableGrid {
        private val useEnergy: ForgeConfigSpec.BooleanValue
        private val capacity: ForgeConfigSpec.IntValue
        private val openUsage: ForgeConfigSpec.IntValue
        private val extractUsage: ForgeConfigSpec.IntValue
        private val insertUsage: ForgeConfigSpec.IntValue
        fun getUseEnergy(): Boolean {
            return useEnergy.get()
        }

        fun getCapacity(): Int {
            return capacity.get()
        }

        fun getOpenUsage(): Int {
            return openUsage.get()
        }

        fun getExtractUsage(): Int {
            return extractUsage.get()
        }

        fun getInsertUsage(): Int {
            return insertUsage.get()
        }

        init {
            builder.push("portableGrid")
            useEnergy = builder.comment("Whether the Portable Grid uses energy").define("useEnergy", true)
            capacity = builder.comment("The energy capacity of the Portable Grid").defineInRange("capacity", 3200, 0, Int.MAX_VALUE)
            openUsage = builder.comment("The energy used by the Portable Grid to open").defineInRange("openUsage", 30, 0, Int.MAX_VALUE)
            extractUsage = builder.comment("The energy used by the Portable Grid to extract items or fluids").defineInRange("extractUsage", 5, 0, Int.MAX_VALUE)
            insertUsage = builder.comment("The energy used by the Portable Grid to insert items or fluids").defineInRange("insertUsage", 5, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Constructor {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("constructor")
            usage = builder.comment("The energy used by the Constructor").defineInRange("usage", 3, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Destructor {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("destructor")
            usage = builder.comment("The energy used by the Destructor").defineInRange("usage", 3, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class DiskManipulator {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("diskManipulator")
            usage = builder.comment("The energy used by the Disk Manipulator").defineInRange("usage", 4, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Crafter {
        private val usage: ForgeConfigSpec.IntValue
        private val patternUsage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        fun getPatternUsage(): Int {
            return patternUsage.get()
        }

        init {
            builder.push("crafter")
            usage = builder.comment("The energy used by the Crafter").defineInRange("usage", 4, 0, Int.MAX_VALUE)
            patternUsage = builder.comment("The energy used for every Pattern in the Crafter").defineInRange("patternUsage", 1, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class CrafterManager {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("crafterManager")
            usage = builder.comment("The energy used by the Crafter Manager").defineInRange("usage", 8, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class CraftingMonitor {
        private val usage: ForgeConfigSpec.IntValue
        fun getUsage(): Int {
            return usage.get()
        }

        init {
            builder.push("craftingMonitor")
            usage = builder.comment("The energy used by the Crafting Monitor").defineInRange("usage", 8, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class WirelessCraftingMonitor {
        private val useEnergy: ForgeConfigSpec.BooleanValue
        private val capacity: ForgeConfigSpec.IntValue
        private val openUsage: ForgeConfigSpec.IntValue
        private val cancelUsage: ForgeConfigSpec.IntValue
        private val cancelAllUsage: ForgeConfigSpec.IntValue
        fun getUseEnergy(): Boolean {
            return useEnergy.get()
        }

        fun getCapacity(): Int {
            return capacity.get()
        }

        fun getOpenUsage(): Int {
            return openUsage.get()
        }

        fun getCancelUsage(): Int {
            return cancelUsage.get()
        }

        fun getCancelAllUsage(): Int {
            return cancelAllUsage.get()
        }

        init {
            builder.push("wirelessCraftingMonitor")
            useEnergy = builder.comment("Whether the Wireless Crafting Monitor uses energy").define("useEnergy", true)
            capacity = builder.comment("The energy capacity of the Wireless Crafting Monitor").defineInRange("capacity", 3200, 0, Int.MAX_VALUE)
            openUsage = builder.comment("The energy used by the Wireless Crafting Monitor to open").defineInRange("openUsage", 30, 0, Int.MAX_VALUE)
            cancelUsage = builder.comment("The energy used by the Wireless Crafting Monitor to cancel a crafting task").defineInRange("cancelUsage", 5, 0, Int.MAX_VALUE)
            cancelAllUsage = builder.comment("The energy used by the Wireless Crafting Monitor to cancel all crafting tasks").defineInRange("cancelAllUsage", 10, 0, Int.MAX_VALUE)
            builder.pop()
        }
    }

    inner class Autocrafting {
        private val calculationTimeoutMs: ForgeConfigSpec.IntValue
        fun getCalculationTimeoutMs(): Int {
            return calculationTimeoutMs.get()
        }

        init {
            builder.push("autocrafting")
            calculationTimeoutMs = builder.comment("The autocrafting calculation timeout in milliseconds, crafting tasks taking longer than this to calculate are cancelled to avoid server strain").defineInRange("calculationTimeoutMs", 5000, 5000, Int.MAX_VALUE)
            builder.pop()
        }
    }

    init {
        upgrades = Upgrades()
        controller = Controller()
        cable = Cable()
        grid = Grid()
        diskDrive = DiskDrive()
        storageBlock = StorageBlock()
        fluidStorageBlock = FluidStorageBlock()
        externalStorage = ExternalStorage()
        importer = Importer()
        exporter = Exporter()
        networkReceiver = NetworkReceiver()
        networkTransmitter = NetworkTransmitter()
        relay = Relay()
        detector = Detector()
        securityManager = SecurityManager()
        `interface` = Interface()
        fluidInterface = FluidInterface()
        wirelessTransmitter = WirelessTransmitter()
        storageMonitor = StorageMonitor()
        wirelessGrid = WirelessGrid()
        wirelessFluidGrid = WirelessFluidGrid()
        constructor = Constructor()
        destructor = Destructor()
        diskManipulator = DiskManipulator()
        portableGrid = PortableGrid()
        crafter = Crafter()
        crafterManager = CrafterManager()
        craftingMonitor = CraftingMonitor()
        wirelessCraftingMonitor = WirelessCraftingMonitor()
        autocrafting = Autocrafting()
        spec = builder.build()
    }
}