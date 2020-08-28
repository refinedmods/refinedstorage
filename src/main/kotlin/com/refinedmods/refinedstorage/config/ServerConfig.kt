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
    }
}