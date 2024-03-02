# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

-   Added Hungarian translation.

## [1.13.0-beta.2] - 2024-02-16

### Fixed

-   Fixed JEI transfer in the Pattern Grid wrongly choosing "Processing" mode.
-   Fixed JEI transfer not working in single player.

## [1.13.0-beta.1] - 2024-02-12

### Added

-   Ported to Minecraft 1.20.4.

## [1.12.4] - 2023-11-05

### Added

-   Re-added compatibility with Inventory Sorter.

### Fixed

-   Various world corruption issues if Refined Storage blocks were removed unexpectedly or with another mod.
-   Fixed Grid search bar not being able to lose focus.
-   Fixed JEI transfer crash for larger processing recipes.
-   Fixed rare autocrafting crash.
-   Fixed some models in inactive state being emissive.
-   Fixed Controller item not rendering energy state correctly.
-   Fixed duplication bug with the Crafting Grid.
-   Fixed fluid duplication bug with the Importer and GregTechCEu machines.
-   Attempted to fix issue where Grid doesn't load items and requires a client restart.
-   Fixed Crafter Manager slots being able to sort with Inventory Sorter.
-   Fixed duplication bug in the Crafter with Inventory Sorter.

## [1.12.3] - 2023-07-07

### Fixed

-   Fixed not being able to type "e" in Grid search box.

## [1.12.2] - 2023-07-05

### Fixed

-   The Grid detailed tooltip now has a small font size again.
-   Fixed not being able to close GUIs anymore with autoselected search box mode.
-   Fixed lingering tooltips of side buttons.

## [1.12.1] - 2023-07-03

### Fixed

-   Fixed packages not being published to Maven.

## [1.12.0] - 2023-07-03

### Changed

-   Ported to Minecraft 1.20.1.

### Fixed

-   Fixed GUI side buttons not working sometimes when using Refined Storage with addons.

## [1.11.7] - 2023-11-12

### Fixed

-   Various world corruption issues if Refined Storage blocks were removed unexpectedly or with another mod.
-   Fixed JEI transfer crash for larger processing recipes.
-   Fixed rare autocrafting crash.
-   Fixed duplication bug with the Crafting Grid.
-   Fixed fluid duplication bug with the Importer and GregTechCEu machines.
-   Attempted to fix issue where Grid doesn't load items and requires a client restart.

## [1.11.6] - 2023-03-30

### Fixed

-   Fixed not being able to search with JEI when the Grid is open.
-   Fixed a bunch of issues where chunks would unintentionally be loaded by RS.
-   Reduced block updates when a controller is turning on and off constantly.

## [1.11.5] - 2023-02-12

### Fixed

-   Fixed some craftable items not showing as craftable in JEI
-   Fixed Grid crashing on exit if JEI mod is not used
-   Fixed rare multithreading crash
-   Fixed Constructor being able to drop more than the maximum stack size for an item 

## [1.11.4] - 2022-12-20

### Fixed

-   Fixed duplication bug in the Interface.

## [1.11.3] - 2022-12-20

### Fixed

-   Fixed external storage cache being de-synced from the network cache.
-   Fixed external storage using an out of date block entity for getting handler.
-   Fixed inventory slots being reused incorrectly in rare cases in the JEI transfer handler.

### Changed

-   Increased packet size limit.

## [1.11.2] - 2022-12-17

### Added

-   Available items indicator in JEI now updates while JEI is open.

### Fixed

-   Fixed chained crafters not taking over the name of the root crafter.
-   Fixed lag when opening JEI in large systems.
-   Made Refined Storage more robust against crashes when moving network blocks by unconventional means.

## [1.11.1] - 2022-10-30

### Fixed

-   Fixed not using Forge silicon tag for recipes.
-   Small corrections to the Korean ation.

## [1.11.0] - 2022-09-30

### Changed

-   Ported to Minecraft 1.19.2.

## [1.10.6] - 2023-11-26

### Fixed

-   Fixed a bunch of issues where chunks would unintentionally be loaded by RS.
-   Reduced block updates when a controller is turning on and off constantly.
-   Various world corruption issues if Refined Storage blocks were removed unexpectedly or with another mod.
-   Fixed JEI transfer crash for larger processing recipes.
-   Fixed rare autocrafting crash.
-   Fixed duplication bug with the Crafting Grid.
-   Fixed fluid duplication bug with the Importer in certain situations.
-   Attempted to fix issue where Grid doesn't load items and requires a client restart.

## [1.10.5] - 2023-02-12

### Fixed

-   Fixed rare multithreading crash
-   Fixed Constructor being able to drop more than the maximum stack size for an item

## [1.10.4] - 2022-12-20

### Fixed

-   Fixed external storage cache being de-synced from the network cache.
-   Fixed external storage using an out of date block entity for getting handler.
-   Fixed chained crafters not taking over the name of the root crafter.
-   Made Refined Storage more robust against crashes when moving network blocks by unconventional means.
-   Fixed duplication bug in the Interface.

### Changed

-   Increased packet size limit.

## [1.10.3] - 2022-08-06

### Fixed

-   Fixed Destructor dupe bug
-   Fixed being unable to insert items into the Storage Monitor
-   Fixed Storage Monitor not showing all matching items in non-exact mode
-   Fixed items getting lost on Creative Disk when more than 2,147,483,647 of one type is stored
-   Fixed incorrect autocrafting keybind prompt on macOS
-   Fixed crashing the game when a network block was removed in a bad way
-   Fixed duplication bug with Constructors and Shulker Boxes
-   Fixed breaking blocks with pickaxe taking too much time

### Changed

-   Improved performance for grid updates
-   Improved waterlogging to behave more like vanilla
-   JEI version 9.7.1 or higher is now required

### Added

-   Forge relocation and Packing Tape blacklist tag entries to prevent block movement
-   Japanese translations for certain autocrafting actions

## [1.10.2] - 2022-03-26

### Changed

-   Ported to Minecraft 1.18.2.

## [1.10.1] - 2022-03-26

### Fixed

-   Fixed crash when opening alternatives screen.

## [1.10.0] - 2022-01-25

### Fixed

-   Fixed multiple bugs related to transferring recipes into the Crafting Grid.
-   Processing patterns now use the order of items/fluids specified in the pattern
    by [@necauqua](https://github.com/necauqua) and [@Darkere](https://github.com/Darkere).
-   Fixed autocrafting task getting stuck if two tasks fulfilled each others requirements.
-   Fixed fluid autocrafting breaking when using 2 stacks of the same fluid in a pattern.
-   Amount specifying screen is now limited to valid values.
-   Fixed crash on servers when starting with latest Forge.

## [1.10.0-beta.4] - 2021-12-28

### Fixed

-   Fixed client crash when hovering over a fluid in the Fluid Grid by [@jackodsteel](https://github.com/jackodsteel).
-   Fixed random client crashes when starting the game.

### Changed

-   Update Korean translation by [@mindy15963](https://github.com/mindy15963).

## [1.10.0-beta.3] - 2021-12-17

### Fixed

-   Fixed networks and network devices being removed when a chunk unloads.

## [1.10.0-beta.2] - 2021-12-16

### Fixed

-   Fixed all Refined Storage advancements being granted when joining a world.
-   Fixed potential Pattern crash when loading Minecraft.

## [1.10.0-beta.1] - 2021-12-15

### Fixed

-   Fixed Relay not working.
-   Fixed Wireless Transmitter only working upright.
-   Fixed Portable Grid not opening when pointing at a block.
-   Fixed being able to circumvent locked slots by scrolling.

### Changed

-   Added more slots to the Pattern Grid.
-   Combined fluid and item view in the Pattern Grid.
-   Ported to Minecraft 1.18.1.
-   Focused side buttons now display their tooltip properly.
-   Improved performance of retrieving patterns by [@metalshark](https://github.com/metalshark).

## [1.9.18] - 2022-05-18

### Fixed

-   Fixed potential Pattern crash when loading Minecraft.

## [1.9.17] - 2022-01-30

### Added

-   More slots for the Pattern Grid.

### Changed

-   Combined fluid and item view in the Pattern Grid.
-   Processing patterns now use the order of items/fluids specified in the pattern.
-   Amount specifying screen is now limited to valid values.

### Fixed

-   Fixed Relay not working.
-   Fixed Wireless Transmitter only working upright.
-   Fixed Portable Grid not opening when pointing at a block.
-   Fixed being able to circumvent locked slots by scrolling.
-   Fixed multiple bugs related to transferring recipes into the crafting grid.
-   Fixed autocrafting task getting stuck if two tasks fulfilled each others requirements.
-   Fixed fluid autocrafting breaking when using 2 stacks of the same fluid in a pattern.

## [1.9.16] - 2021-11-16

### Added

-   Added Covers for all cable types.
-   Added Polish translation.
-   Added Italian translation.
-   Addons can now override how crafters insert items.

### Changed

-   Improved JEI integration to pick the best option when transferring items.

### Fixed

-   Fixed an issue where too many items in a grid would kick the player.
-   Fixed an issue where the portable grid does not open from Inventory anymore after some use.
-   Fixed craftable view in grids not showing items that were already in storage.
-   Fixed Wireless Crafting Monitor not working as Curio.
-   Fixed wrong slot being locked in some cases when opening a wireless item.
-   Slightly sped up External Storage item look up.
-   Fixed extraction from Storage Monitor not respecting maximum stack size.

## [1.9.15] - 2021-07-25

### Fixed

-   Fixed Refined Storage Addons compatibility.

## [1.9.14] - 2021-07-25

### Added

-   Implemented Curios support.

## [1.9.13] - 2021-06-14

### Added

-   Added some performance improvements for autocrafting.

### Fixed

-   Fixed count on Storage Monitor having Z fighting.
-   Fixed items on Storage Monitor not being flat.
-   Fixed crash when using an External Storage on a fluid inventory.
-   Fixed a memory leak in the pattern cache.
-   Fixed Detector crashing when dyed.
-   Fixed autocrafting being stuck after clicking "Start".
-   Fixed Crafting Monitor not being able to show hours.
-   Fixed capacity rendering of infinite storages.
-   Fixed wrong alignment for the JEI request autocrafting tooltip.
-   Fixed mobs getting stuck in Refined Storage cables.
-   Fixed dismantling storage blocks ignoring stack size.
-   Fixed Ice and Fire banners breaking with Refined Storage.
-   Fixed empty keybinding causing GL errors.
-   Fixed some parts of the Japanese translation.
-   Fixed rendering issue on blocks when using OptiFine.

### Removed

-   Removed experimental pipeline nagging message.

## [1.9.12] - 2021-02-07

### Fixed

-   Fixed some issues when using the Grid when it's offline.
-   Fixed crafting events not being fired in some cases in the Grid.
-   Fixed not being able to set fluid filter slot output quantity.
-   Fixed mod id search not working for Industrial Foregoing.
-   Fixed fluid autocrafting duplicating fluids.
-   Fixed some Grid crashes.
-   Fixed constructor not using compare mode correctly in some cases.
-   Fixed duplication bug in the Interface.

## [1.9.11] - 2021-01-03

### Fixed

-   Fixed disks and network devices not loading when they did not previously exist
    -   If you are affected by this please go to the world/data/ folder and remove the ".temp" ending from the files
        before
        launching.

## [1.9.10] - 2021-01-02

### Changed

-   Update Japanese translation.

### Fixed

-   Improve performance of the Grid view.
-   Fixed Disk Manipulator model glitches.
-   Improve performance of the Disk Manipulator.
-   Fixed being unable to set quantity in output slots of the Pattern Grid.
-   Fixed External Storage in fluid mode losing track of fluids sometimes.
-   Added code to avoid / minimize data corruption issues caused by Minecraft.
-   Fixed processing autocrafting orders stealing items from each other.
-   Fixed Constructor in fluid mode voiding fluid source blocks in front of it.
-   Fixed crash when recoloring blocks that have no rotation component.
-   Fixed reloading resource packs breaking Refined Storage textures.

## [1.9.9] - 2020-11-14

### Fixed

-   Fixed Refined Storage sidebuttons displaying over the JEI bookmark pagination buttons.
-   Fixed issue where Crafters may fail to recognize an inventory/tank for some patterns.
-   Fixed issue where the Crafter Manager can crash on invalid patterns.
-   Fixed issue where alternatives in the Pattern Grid weren't being saved properly.
-   Fixed not being able to change the Exporter filter slot count with regulator mode without closing and re-opening the
    container.

## [1.9.8] - 2020-10-24

### Added

-   Added a JEI synchronized (two-way) search box mode to the Grid.
-   Added a nag message when a player joins the world that asks the player to enable the experimental Forge lighting
    pipeline to ensure correct rendering.

### Fixed

-   Fixed server crash when scrolling in Grid.
-   Fixed various issues with Grid interactions working without power.
-   Fixed changing rotation not updating blocks.

## [1.9.7] - 2020-10-04

### Added

-   Added functionality to move items in the Grid with shift/ctrl + scrolling.

### Changed

-   Changed JEI transfer error mechanics.

### Fixed

-   Fixed crash when opening Controller GUI.
-   Fixed dye being consumed without effect in some cases.
-   Fixed deadlock caused by Portable Grid.
-   Fixed custom tooltips not working in the Grid.

## [1.9.6] - 2020-09-25

### Added

-   Port to Minecraft 1.16.3.
-   Added colored block variants.
-   Added functionality to show missing items in the JEI transfer screen.
-   Added functionality to request missing items from autocrafting in the JEI transfer screen.
-   Added client config option to remember the Grid search query.

### Fixed

-   Fixed Portable Grid losing enchantments when placing and breaking.

## [1.9.5] - 2020-09-06

### Added

-   Re-added the `/refinedstorage disk create <player> <id>` command.
-   Added the `/refinedstorage disk list` command.
-   Added the `/refinedstorage disk list <player>` command.
-   Added the `/refinedstorage network list <dimension>` command.
-   Added the `/refinedstorage network get <dimension> <pos>` command.
-   Added the `/refinedstorage network get <dimension> <pos> autocrafting list` command.
-   Added the `/refinedstorage network get <dimension> <pos> autocrafting get <id>` command.
-   Added the `/refinedstorage network get <dimension> <pos> autocrafting cancel` command.
-   Added the `/refinedstorage network get <dimension> <pos> autocrafting cancel <id>` command.
-   Added JEI ghost ingredient dragging support.

### Fixed

-   Fixed text field not being focused in amount specifying screens.

## [1.9.4] - 2020-08-30

### Fixed

-   Fixed JEI recipes/usages keys not working in Grids.

## [1.9.3-beta] - 2020-08-24

### Added

-   Port to Minecraft 1.16.2.

### Changed

-   Updated Japanese translation.
-   Updated Taiwanese translation.
-   Refactored autocrafting code.

### Fixed

-   Fixed duplication bug with the Constructor.

## [1.9.2-beta.2] - 2020-09-11

### Fixed

-   Fixed duplication bug with the Constructor.

## [1.9.2-beta.1] - 2020-07-17

### Added

-   Re-added interdimensional networks with the Network Transmitter and Network Receiver.
-   Re-added MouseTweaks integration.

### Changed

-   Networks that are in a chunk that isn't loaded will no longer work, they will turn off. Chunkload the Controller to
    maintain a functioning network over long distances.

### Fixed

-   Fixed crash with Forge version 67.
-   Fixed cases where Refined Storage unwillingly acts like a chunkloader.
-   Fixed Network Transmitters being able to connect to any network device.
-   Fixed Crafting Tweaks buttons being in the wrong position after changing the size configuration of the Grid.

## [1.9.1-beta] - 2020-07-14

### Fixed

-   Fixed server crash.

## [1.9.0-beta] - 2020-07-14

### Added

-   Port to Minecraft 1.16.

### Fixed

-   Fixed wrench requiring shift click to rotate blocks.

## [1.8.8] - 2020-07-13

### Fixed

-   Fixed duplication bug and weird behavior in the Crafting Grid matrix.

## [1.8.7] - 2020-07-11

### Fixed

-   Fixed Regulator mode item and fluid counts not saving properly.
-   Fixed Wireless Crafting Monitor not closing properly.
-   Fixed Controller always using energy, even when disabled with redstone.
-   Fixed internal crafting inventory not being returned when Controller is broken.
-   Fixed bug where autocrafting tasks started on the same tick make the wrong assumption about available items and
    fluids.
-   Fixed bug where the "To craft" amount in the Crafting Preview window is wrong.
-   Fixed bug where non-pattern items are able to be inserted into the Crafter Manager (Darkere)
-   Fixed performance issue where shapes of cable blocks were constantly being recalculated.

### Changed

-   Drastically improved shift clicking performance in Crafting Grid.

### Removed

-   Removed autocrafting engine version from crafting preview screen.

## [1.8.6-beta] - 2020-06-26

### Fixed

-   Fixed Constructor duplication bug.

## [1.8.5-beta] - 2020-06-18

### Added

-   Re-added all the language files.
-   Japanese translations.

### Fixed

-   Fixed Portable Grid voiding the disk when extracting with full inventory.
-   Fixed Constructor extracting 2 buckets when placing fluid.
-   Fixed Stack Overflow error with regulator upgrades.
-   Fixed visual bug with the Detector not updating its values.
-   Fixed Constructor placing the filtered item instead of the extracted.
-   Fixed duplication bug with filter slots.
-   Fixed shift crafting in a Grid not using the player.
-   Fixed bug where shift clicking gives too many items.

### Changed

-   Cancelling a crafting task now also unlocks all Crafters related to that task.
-   External Storage will now always show the exact maximum capacity as reported by the attached inventory.
-   Crafters no longer expose their inventory to the side they are facing.
-   Changed package name to `com.refinedmods.refinedstorage`, this is a breaking change for addons.

## [1.8.4-beta] - 2020-05-26

### Fixed

-   Fixed autocrafting Crafting Monitor crash.

## [1.8.3-beta] - 2020-04-29

### Added

-   A new experimental autocrafting engine that's enabled by default. This should improve autocrafting performance.
-   The Regulator Upgrade that can be inserted into a Exporter. This ensures a certain amount of items and fluids is
    kept in stock in a connected inventory.
-   Debug logging on the server when an expensive operation occurs.

### Fixed

-   Fixed Exporter not exporting anything when using a Stack Upgrade and there isn't space for 64 items in the inventory.
-   Fixed Controller always using the base usage even when turned off.
-   Fixed severe memory leak in the storage cache.

### Changed

-   Wireless Transmitters can now be placed on any block and in any direction.

## [1.8.2-beta] - 2020-04-25

### Added

-   Refined Storage silicon is now present in `forge:silicon` tag for mod compatibility.
-   Waterlogging to all cable blocks.
-   Create zh_tw translation.
-   Re-added zh_cn translation.

### Fixed

-   Fixed storage block dropping extra processor.

### Changed

-   Updated pt_br translation.

## [1.8.1-beta] - 2020-01-30

### Added

-   Port to Minecraft 1.15.2.
-   Fluid support for the Storage Monitor.

## [1.8.0-beta] - 2020-01-21

### Added

-   Port to Minecraft 1.15.

## [1.7.3-beta] - 2019-12-30

### Fixed

-   Fixed severe energy update lag introduced by version 1.7.2.

## [1.7.2-beta] - 2019-12-29

### Added

-   Resource packs can now define the font colors that Refined Storage GUIs need to use.

### Fixed

-   Fixed crash when loading a network.
-   Fixed being able to drain energy from the Refined Storage Controller.
-   Fixed the Grid crashing on a item/fluid update-heavy storage system.
-   Fixed the Grid displaying the old quantity when shift clicking an entire stack out.
-   Fixed crash with the Disk Manipulator and using item/fluid filters when inserting into the network.
-   Fixed the network being able to run off 1 FE/t.

### Changed

-   Patterns being added or removed from the network are now propagated as well to clients that are watching a Grid.
-   When pressing ESCAPE in the search box on the Grid or Crafter Manager, focus on the search bar will be lost first
    before closing the GUI immediately. Then on the next ESCAPE press, the GUI will be closed.

## [1.7.1-alpha] - 2019-11-19

### Fixed

-   Fixed Pattern Grid causing world hanging on load.
-   Fixed External Storage not refreshing when the storage is broken or replaced.
-   Fixed delay in block update when placing a cable block.
-   Fixed holder of cable blocks sometimes conflicting with a cable connection while rendering.
-   Fixed being able to move wireless items in inventory when using a keybinding to open.
-   Fixed crash when breaking a Grid, Crafting Monitor, Crafter Manager or Portable Grid when another player is still
    using it.

### Changed

-   The Pattern Grid now switches automatically between crafting pattern and processing pattern mode when using JEI
    transfer.
-   The Pattern Grid now switches automatically between crafting pattern and processing pattern mode when re-inserting an
    existing Pattern.
-   Grids now do not sort if you interact with it while holding shift.

### Removed

-   Exact mode for processing patterns no longer exist, you can now define per-slot which item/fluid tags are allowed to
    be used by autocrafting, by using CTRL + click on the filter slot in the Pattern Grid.
-   Removed migration code for the development builds that were released on Discord (not on CurseForge). If you used the
    development builds and never used version 1.7.0 before, first switch to 1.7.0, open your world, modify a storage disk,
    and then upgrade to 1.7.1.

## [1.7.0-alpha] - 2019-11-02

### Added

-   Port to Minecraft 1.14.

### Fixed

-   Fixed the Fluid Grid not having a View type setting.

### Changed

-   Oredict mode for Patterns has been replaced with "Exact mode" (by default on). When exact mode is off, Refined Storage
    will use equivalent items or fluids from the Minecraft item/fluid tag system.
-   Grid filtering with "$" now does filtering based on item/fluid tag name instead of oredict name.
-   When binding a network item to a network you can now bind to any network block, not only the Controller.

### Removed

-   The Reader and Writer, this will return later in an addon mod.
-   Cross dimensional functionality on the Network Transmitter for the moment, this will return later.
-   Covers.

## [1.7.0+10] - 2019-10-29

### Added

-   Re-added oredict mode as "exact mode" (for fluids too!).
-   Re-added the Crafter Manager.
-   Re-added the Crafting Monitor.
-   Re-added the Wireless Crafting Monitor.

### Fixed

-   Fixed the ugly checkboxes
-   Misc bugfixes and crash fixes.

## [1.7.0+9] - 2019-10-28

### Added

-   Re-add the Crafter and autocrafting.

### Fixed

-   Misc bugfixes and crash fixes.

## [1.7.0+8] - 2019-10-27

### Added

-   Re-added the Constructor.
-   Re-added the Destructor.
-   Re-added the Disk Manipulator.
-   Re-added the Portable Grid.

## [1.7.0+7] - 2019-10-22

### Fixed

-   Fixed a crash that can happen when opening a world.

## [1.7.0+6] - 2019-10-22

### Added

-   Re-added the Network Transmitter (not cross dimensional yet).
-   Re-added the Network Receiver.
-   Re-added the Relay.
-   Re-added the Detector.
-   Re-added the Security Manager.
-   Re-added the Interface.
-   Re-added the Fluid Interface.
-   Re-added the Wireless Transmitter.
-   Re-added the Storage Monitor.
-   Re-added the Wireless Grid.
-   Re-added the Wireless Fluid Grid.

### Fixed

-   Misc bugfixes and crash fixes.

## [1.7.0+5] - 2019-10-17

### Added

-   Re-added the External Storage.
-   Re-added the Importer.
-   Re-added the Exporter.

### Removed

-   Cutting Tool (you may get a Forge warning about that one, it's safe to ignore).
-   The "compare nbt" side button, replaced it with "exact mode".

## [1.7.0+4] - 2019-10-15

### Added

-   Re-added all the storage blocks.
-   Re-added JEI integration.

### Fixed

-   Misc bugfixes and crash fixes.

## [1.7.0+3] - 2019-10-12

### Added

-   Re-added the Crafting Grid.
-   Re-added the Pattern Grid.
-   Re-added the Fluid Grid.
-   Re-added Optifine compatibility.

## [1.7.0+2] - 2019-10-10

### Added

-   More config values.

### Fixed

-   Misc bugfixes and crash fixes.

### Removed

-   Free dirt every 10 ticks.

## [1.7.0+1] - 2019-10-09

### Added

-   Re-added the Controller.
-   Re-added the Disk Drive.
-   Re-added the Grid.

## [1.6.16] - 2020-04-26

### Fixed

-   Fixed erroring controller tile entity.
-   Fixed Inventory Tweaks sorting not respecting locked slots.
-   Fixed OpenComputers driver voiding excess fluids.
-   Fixed being able to move wireless items in inventory.

### Changed

-   Updated Russian translation.

## [1.6.15] - 2019-07-21

### Fixed

-   Fixed recipes with more than 1 bucket of fluid not transferring from JEI.
-   Fixed oredict crafting patterns redefining recipes.
-   Fixed Portable Grids not keeping their enchantments when placed.
-   Fixed JEI hotkeys not working on fluid filter slots.
-   Fixed crash when opening Crafter Manager with FTB Quests installed.
-   Fixed a bug where the container slots weren't synced when opening a Grid.

### Changed

-   Shortened crafting text for the Russion translation to fix Grid overlays.
-   GregTech Community Edition Wires and Machines are now banned from rendering on Refined Storage patterns because they are causing crashes.

## [1.6.14] - 2019-03-23

### Fixed

-   Fixed server crash

## [1.6.13] - 2019-03-23

### Added

-   Added keybindings to open wireless items. The default one set to open a Wireless Crafting Grid from Refined Storage Addons is CTRL + G.
-   Added Grid quantity formatting for item counts over 1 billion.

### Changed

-   Updated German translation.
-   Updated Chinese translation.
-   The Constructor and Destructor now interacts with the world using their owner's profile.

### Fixed

-   Fixed Interface with Crafting Upgrade being stuck if an earlier item configuration has missing items or fluids.
-   Fixed wrong item count for oredict patterns.
-   Fixed autocrafting duplication bug.
-   Fixed Crafting Pattern not rendering tile entity items like a chest.

## [1.6.12] - 2018-11-28

### Added

-   Added a completion percentage to the Crafting Monitor.

### Changed

-   Updated Russian translation.
-   Increased the speed of autocrafting.

### Fixed

-   Fixed External Storage sending storage updates when it is disabled.
-   Fixed slight performance issue with loading Crafters from disk.
-   Fixed storage GUIs overflowing on large numbers.

## [1.6.11] - 2018-11-24

### Fixed

-   Fixed blocks neighboring a controller breaking when returning from a dimension in a unchunkloaded area.

## [1.6.10] - 2018-11-23

### Added

-   Added fluid functions for the fluid autocrafting to the OpenComputers integration.

### Changed

-   Updated Russian translation.
-   Slightly increased performance of the External Storage.

### Fixed

-   Fixed client FPS stalling when using "@" mod search in the Grid.
-   Fixed client FPS stalling when using "#" tooltip search in the Grid.
-   Fixed fluid inputs/outputs in the Pattern Grid not being set when you re-insert a Pattern with fluid inputs/outputs.
-   Fixed bug where the Pattern Grid doesn't update it's output slot when manually configuring a crafting pattern.
-   Fixed network node scanning allowing multiple controllers in some cases.
-   Fixed OpenComputers integration not giving back a crafting task instance in the schedule task API.
-   Fixed OpenComputers integration causing log spam when getting processing patterns.
-   Fixed OpenComputers voiding items with extract item API when there is no inventory space.
-   Fixed CraftingTweaks buttons resetting sometimes in the Crafting Grid.
-   Fixed Refined Storage jars not being signed.
-   Fixed crafting task stalling when there's not enough space in the inventory.
-   Fixed another duplication bug with a disconnected Crafting Grid.
-   Fixed oredict mode in autocrafting not working at all.

### Removed

-   Removed getMissingItem.
-   Removed the Interdimensional Upgrade, Network Transmitters are now cross dimensional by default.
-   Removed the per block FE cost of the Network Transmitter, it draws a fixed amount of FE/t now.

## [1.6.9] - 2018-10-27

### Changed

-   You can now interact with the fluid container input slot in the Fluid Interface.

### Fixed

-   Fixed OpenComputers "unknown error" when using extract item API.
-   Fixed client FPS stuttering when opening a Crafting Grid.
-   Fixed rare Grid crashing issue.

## [1.6.8] - 2018-10-20

### Fixed

-   Fixed Ender IO incompatibility.

## [1.6.7] - 2018-10-19

### Changed

-   The Processor Binding recipe now only gives 8 items instead of 16.

### Fixed

-   Fixed the Raw Processor recipes not taking oredicted silicon.
-   Fixed the Processor Binding recipe not taking oredicted slimeballs.

## [1.6.6] - 2018-10-18

### Added

-   Added new Crafter modes: ignore redstone signal, redstone signal unlocks autocrafting, redstone signal locks autocrafting and redstone pulse inserts next set.
-   Added a config option to configure the autocrafting calculation timeout in milliseconds.
-   Added throttling for network devices that can request autocrafting.

### Changed

-   Renamed Cut Processors to Raw Processors and those are now made with Processor Binding instead of a Cutting Tool.
-   You can no longer start a crafting task if it has missing items or fluids.
-   The Security Manager now supports Security Cards that have no player assigned to them. It is the default security card for players that aren't configured.
-   If no default Security Card is configured in the Security Manager, an unconfigured player is allowed to do everything in the network. Create a default Security Card.

### Fixed

-   Fixed an autocrafting bug where it crashed when external inventories couldn't be filled.
-   Fixed a duplication bug with a disconnected Crafting Grid.
-   Fixed oredict autocrafting sometimes reporting that a craftable item is missing.
-   Fixed fluid autocrafting without item inputs locking when there's not enough space for the fluids.
-   Fixed Grid "last changed" date not changing when using clear button or JEI transfer.
-   Fixed a duplication bug when pressing clear on a Wireless Crafting Grid from Refined Storage Addons.
-   Fixed a duplication bug with autocrafting and External Storages.
-   Fixed Crafting Manager displaying wrong name for chained crafters connected to some blocks.
-   Fixed crafting task losing internal buffer when network runs out of energy.

### Removed

-   Removed handling of reusable items in autocrafting, to avoid problems.

## [1.6.5] - 2018-09-11

### Changed

-   The Pattern Grid in fluid mode now supports up to 64 buckets in the input and output processing slots.

### Fixed

-   Fixed Refined Storage silicon's oredict entry being registered too late.
-   Fixed duplication bug with filter slots.

## [1.6.4] - 2018-09-02

### Changed

-   Rewrote autocrafting again, bringing performance up to par with other autocrafting mods.
-   Autocrafting now reserves items and fluids in an internal inventory to avoid having the storage network steal stacks required for autocrafting.
-   Reworked the Crafting Monitor to be more condensed and more clear.

### Fixed

-   Fixed not being able to craft upgrades that require enchanted books.
-   Fixed quick jittering of the Grid and Crafting Monitor when opening them because the tabs appear.

### Removed

-   Removed left / right click functionality on filter slots to increase / decrease the amount, replaced that functionality with a dialog.

## [1.6.3] - 2018-08-02

### Added

-   Re-added a single mode Wrench that can rotate blocks and break Refined Storage covers.

### Fixed

-   Fixed crash with Wireless Fluid Grid.
-   Fixed Reborn Storage crafting being slower than normal.

## [1.6.2] - 2018-07-30

### Fixed

-   Fixed Grid searching not working.

## [1.6.1] - 2018-07-30

### Added

-   Added fluid autocrafting.
-   Added Crafting Upgrade support for fluids on the Exporter, Constructor and Fluid Interface.
-   Added config option to hide covers in the creative mode tabs and JEI.

### Changed

-   The Portable Grid now supports fluid disks.
-   Filters now support fluids and can be inserted in the Fluid Grid.
-   You can now keep fluids in stock by attaching a External Storage in fluid mode to a Fluid Interface with a Crafting Upgrade.
-   You can now specify the amount to export in the Fluid Interface.
-   Updated Russian translation.
-   Overhauled and updated German translation.
-   The Crafting Upgrade no longer schedules requests when there are items or fluids missing.
-   Made the Crafting Preview window bigger.

### Fixed

-   Fixed crash log when opening Pattern Grid GUI.
-   Fixed being able to put non fluid containers in Fluid Interface input slot.
-   Fixed Grid filters not updating Grid.

### Removed

-   Removed "emit signal when item is being autocrafted" option in the Detector.

## [1.6.0] - 2018-07-20

### Added

-   Added the Cutting Tool.
-   Added covers.
-   Added new storage disk system where the storage disk data (items, fluids) are stored off the disk itself, in another file (refinedstorage_disks.dat). The disk itself only stores its ID.
-   Added /createdisk command which creates a disk based on the disk ID. Turn on advanced tooltips to see the disk ID on a disk item.
-   Added config option to configure controller max receive rate.
-   Added config option to configure energy capacity of Refined Storage items.
-   Added config option to change Reader / Writer channel energy capacity.
-   Added a fully charged regular Controller to the creative menu.
-   Added a missing config option for Crafter Manager energy usage.
-   Added support for Disk Drive / Storage Block storage and capacity to OC integration.
-   Added "Search box mode" button to the Crafter Manager.

### Changed

-   Renamed "Printed Processors" to "Cut Processors".
-   Rewrote autocrafting.
-   Rewrote network energy storage.
-   The Controller item now shows a durability bar for the energy.
-   You can no longer put a Filter in filter slots to gain additional filter slots.
-   You can now re-insert Processing Patterns in the Pattern Grid and have the inputs and outputs be completed.
-   If an Interface is configured to expose the entire network storage (by configuring no export slots), it will no longer expose the entire RS storage, due to performance issues.
-   The Portable Grid no longer exposes a inventory for crossmod interaction, due to performance issues.
-   The Crafting Monitor is now resizable and its size can be configured (stretched, small, medium, large).
-   The Crafting Monitor now splits its tasks over tabs.
-   An empty blacklist now means: accept any item. An empty whitelist now means: don't accept any item (an empty whitelist USED to mean: accept any item).
-   The Importer now skips over empty slots.
-   The Exporter now round-robins over every configured item or fluid to export instead of exporting them all at once.
-   Updated Russian translation.
-   Autocrafting tasks that take longer than 5 seconds to CALCULATE (NOT execute) are automatically stopped to avoid server strain.
-   Changed fluid storage progression to be 64k - 256k - 1024k - 4096k.
-   Made all IO blocks have a blacklist instead of a whitelist by default.

### Fixed

-   Fixed bug where pattern was recipe pattern was creatable when there was no recipe output.
-   Fixed a crash when breaking an Ender IO conduit with the Destructor.
-   Fixed bug where storage disks in Portable Grids could be moved into themselves.
-   Fixed the Crafter crashing when opening it while connected to a Primal Tech Grill or Kiln.
-   Fixed bug where Crafting Upgrade on Interface kept too many items in stock.
-   Fixed bug where External Storage could only handle 1 fluid inventory per block.
-   Fixed shift clicking a created pattern going into Grid inventory.
-   Fixed crash when moving a wireless item with the number keys.
-   Fixed bug where item storage tracker didn't save sometimes.
-   Fixed bug where External Storage doesn't detect new inventory when rotating.
-   Fixed JEI recipe transferring in Pattern Grid allowing non-processing recipes in processing mode and vice-versa.
-   Fixed using Interfaces for minimum stock levels failing when requester is also an Interface.
-   Fixed ItemZoom incompatibility in Grid and crafting preview window.
-   Fixed shift clicking upgrades into Interface making upgrades go to import slots.
-   Fixed duplication glitch with storages.
-   Prevent accidental Grid scrollbar click after clicking JEI recipe transfer button.

### Removed

-   Removed Regulator mode in the Exporter.
-   Removed MCMultiPart integration.
-   Removed Project E integration.
-   Removed blocking mode in autocrafting.
-   Removed the Wrench.
-   Removed "void excess items or fluids" functionality on storages.
-   Removed the Solderer.
-   Removed "compare oredict" buttons on Exporter, Importer, etc.
-   Removed ConnectedTexturesMod integration for fullbright textures, RS now has fullbright textures natively.
-   Removed autocrafting with fluids (the bucket filling mechanic). This will be replaced in a later version with native fluid autocrafting, where Crafters can insert fluids to external inventories.

## [1.5.34] - 2018-05-22

### Added

-   Added OR search operator to the Grid with "|".
-   Added new `getPattern(stack:table)` function for OpenComputers integration.

### Changed

-   Empty patterns can no longer be inserted in the pattern result slot in the Pattern Grid with hoppers.
-   `getPatterns()` now only returns all the outputs, this to limit memory usage in OpenComputers (only affects OC integration).
-   Allow crafters to be daisy-chained.

### Fixed

-   Fixed repeated key events not getting handled in some cases.

## [1.5.33] - 2018-04-22

### Added

-   Added Crafter Manager.

### Changed

-   Patterns in the Crafter slots now automatically render the output without pressing shift.
-   Increased Grid performance.
-   Various internal refactors.

### Fixed

-   Fixed Disk Manipulator not extracting items.
-   Fixed filter slots not caring about max stack size.
-   Fixed model warning about Portable Grid.
-   Fixed crash when autocompleting Ender IO recipes from JEI.
-   Fixed Grid not always using all combinations when using JEI autocompletion.

## [1.5.32] - 2018-03-08

### Added

-   Added Spanish translation.

### Changed

-   Changed stack quantity of craftable items from 1 to 0 to fix Quantity Sorting.
-   Changed fluid stack amount to not display "0" anymore.
-   Disk Manipulator in fluid mode will now extract a bucket at a time instead of 1 mB (or 64 buckets at a time with a Stack Upgrade instead of 64 mB).

### Fixed

-   Fixed issue where the Pattern Grid can only overwrite patterns when blank ones are present.
-   Fixed not being able to extract half a stack of items with max stack size 1 in Grid when using right click.
-   Fixed 2 same stacks using capabilities without NBT tag not treated equal.
-   Fixed NBT/metadata check on exporting in an Interface.
-   Fixed Disk Manipulator being stuck on unemptiable, non-empty disks.
-   Fixed orientations of the Portable Grid.
-   Fixed crafting event in Crafting Grid being fired twice.
-   Fixed a crash when the Constructor tries to place a block when a multipart is attached to it.
-   Fixed an autocrafting crash.
-   Attempted to fix FPS drop on Grid sorting.

## [1.5.31] - 2017-12-31

### Changed

-   Storage disk and block stored and capacity counts are formatted now in the tooltip.
-   Improved the "cannot craft! loop in processing..." error message.
-   Made the Disk Manipulator unsided (inserting goes to insert slots and extracting from output slots).

### Fixed

-   Fixed error logs when toggling the Pattern Grid from and to processing mode.
-   Fixed pattern slots in Crafters not being accessible.
-   Fixed rare Grid crash.
-   Fixed OpenComputers cable showing up in Grid as air.

## [1.5.30] - 2017-12-24

### Fixed

-   Fixed crashing bug when MCMultiPart is not installed.

## [1.5.29] - 2017-12-23

### Changed

-   Update Forge to 2577 (minimum Forge version required is now 2555 for MC 1.12.2).

### Fixed

-   Fixed bug where MCMP multiparts were blocking RS network connections.
-   Fixed Reader/Writers for energy extracting energy when not needed.

## [1.5.28] - 2017-12-13

### Changed

-   Item Reader/Writers can now store 16 stacks.
-   Fluid Reader/Writers can now store 16 buckets.
-   Energy Reader/Writers can now store 16000 FE.

### Fixed

-   Fixed Writers not pushing energy.

## [1.5.27-beta] - 2017-12-09

### Fixed

-   Fixed non-oredict patterns not consuming resources.

## [1.5.26-beta] - 2017-12-09

### Added

-   Added Funky Locomotion integration.

### Fixed

-   Fixed Exporter in Regulator Mode not regulating properly when same item is specified multiple times.
-   Fixed air appearing in Grid.
-   Fixed config categories not correctly appearing in ingame config GUI.
-   Fixed craftable items showing "1 total" if not stored in system in Grid.
-   Minor fixes to autocrafting.

### Removed

-   Removed "detailed" Grid view type variant, made detailed tooltips a config option instead.

## [1.5.25] - 2017-11-28

### Fixed

-   Fixed not being able to autocraft different Storage Drawers' wood drawers.
-   Fixed not being able to autocraft certain Modular Routers items.
-   Fixed last modified date not being sent when extracting from an External Storage.

## [1.5.24] - 2017-11-26

### Added

-   Added "Last modified" sorting option in the Grid.
-   Added a "detailed" variant for every Grid view type option, to disable the modified information on the tooltip.

### Changed

-   The Grid now displays last modified information (player name and date) and size on tooltips of stacks.

### Fixed

-   Fixed Exporter with Stack Upgrade not working correctly in Regulator Mode.
-   Fixed crash with the Constructor.
-   Fixed patterns being able to crash when no inputs are provided.
-   Fixed possible crash with network scanning.

### Removed

-   Removed craft-only mode for the Exporter.

## [1.5.23] - 2017-11-13

### Fixed

-   Fixed duplication bug with autocrafting.
-   Fixed Fluid Interface with Stack Upgrade not exporting fluids.
-   Fixed fluids in Fluid Grid not showing actual mB on tooltip when pressing CTRL + SHIFT.

## [1.5.22] - 2017-11-11

### Added

-   Added oredict, blocking, processing, ore inputs access to OpenComputers API.
-   Added shortcut to clear Grid crafting matrix (CTRL+X).

### Changed

-   The Crafter can now only store 1 stack size pattern per slot.
-   You can now re-insert a Pattern in the pattern output slot in the Pattern Grid to modify an existing pattern.
-   The Refined Storage jar is now signed.
-   Updated Chinese translation.

### Fixed

-   Fixed not being able to use JEI R and U keys on Grid with tabs.
-   Fixed lag when opening a Grid with lots of items by offloading the grid sorting to another thread.
-   Performance improvement when adding patterns to the network.

## [1.5.21] - 2017-10-19

### Changed

-   Updated Portuguese (Brazilian) translation.

### Fixed

-   Fixed crash with External Storage.
-   Fixed stack-crafting in the crafting grid (crafting table) causing lag on a dedicated server.
-   Fixed cable blocks, Wireless Transmitter, Detector and Portable Grid acting as full blocks (being able to place torches on them etc).

## [1.5.20] - 2017-10-09

### Fixed

-   Restore MC 1.12.0 compatibility.

## [1.5.19] - 2017-10-08

### Changed

-   Updated Forge to 2493 (MC 1.12.2).

### Fixed

-   Fixed Refined Storage blocks requiring a pickaxe to be broken.
-   Fixed Grid GUI crash.
-   Fixed device names overflowing Controller GUI.
-   Fixed high CPU load when Refined Storage GUIs are open.
-   Fixed not being able to extract Mekanism tanks and bins from the Grid.
-   Fixed not being able to craft Immersive Engineering Revolver.
-   Fixed rare bug when server crashes on startup due to network node not existing.

## [1.5.18] - 2017-09-08

### Added

-   Added Project E integration for the External Storage on the Transmutation Table.
-   Added Project E integration for the energy values of Solderer items.
-   Added support for more than 4 grid tabs in the Grid by putting filters IN filters.
-   Added protection for other mods causing crashes when drawing an item or display name.

### Changed

-   Reader and Writer blocks now face the block you're placing it on, not the player.
-   Pressing SHIFT over an item in the Grid will no longer display the full unformatted count, instead, use CTRL + SHIFT and it will be displayed in the tooltip.
-   The Fortune Upgrade doesn't use NBT anymore to store the fortune level.

### Fixed

-   Fixed network not disconnecting when Controller is broken.
-   Fixed bug where when multiple Fortune Upgrades are inserted, it chooses the first Fortune Upgrade instead of the highest one.
-   Fixed some translations having too big "Craft" text.
-   Fixed crash with GUI when toggling the Grid size quickly.
-   Fixed scrollbar not scrolling correctly when clicked with mouse when grid tabs are visible.
-   Fixed Reader and Writers GUIs still displaying channels even if not connected.
-   Fixed Solderer resetting progress when the inventory changes.

## [1.5.17] - 2017-08-19

### Added

-   Re-added support for OpenComputers.

### Fixed

-   Fixed crash with Grid.

## [1.5.16] - 2017-08-09

### Fixed

-   Fixed crash when placing a Controller.
-   Fixed crash when configuring an Exporter.
-   Fixed Refined Storage not running in MC 1.12 and only on MC 1.12.1.

## [1.5.15] - 2017-08-09

### Added

-   Added InventoryTweaks Grid sorting.
-   Added InventoryTweaks inventory sort ability in Refined Storage GUIs.
-   Added CTM integration for Disk Manipulator.

### Changed

-   Updated Forge to 2444 (MC 1.12.1).

### Fixed

-   Fixed possible rare dupe bug with Importer.
-   Fixed Shulker Box dupe bug with Destructor.
-   Fixed Grid crash with search history.
-   Fixed Grid crash with search field.
-   Fixed External Storage not working without Storage Drawers.
-   Fixed External Storage not calculating max stack size in the calculation of it's capacity display in the GUI.
-   Fixed Refined Storage not drawing small text correctly with Unicode font.
-   Fixed dupe bug with External Storage connected to an item handler.

## [1.5.14] - 2017-08-03

### Added

-   Added config option to modify the Solderer speed per Speed Upgrade, defaulting to 22.5% faster per upgrade, making it 90% faster on a fully upgraded Solderer.
-   Added CTM integration.

### Changed

-   Updated Forge to 2426.
-   Updated French translation.

### Fixed

-   Fixed more crashes relating to scrollbar in GUIs.
-   Fixed crash with Detector.
-   Fixed bug where pattern create button wasn't visible when grid tabs were selected.
-   Fixed performance issue with Controllers turning off and on and Interfaces.
-   Fixed Interfaces exposing network inventory don't hide storages that are disconnected.

## [1.5.13] - 2017-07-20

### Fixed

-   Fixed Wireless Fluid Grid not using up energy.
-   Fixed Wireless Crafting Monitor remaining in network item list.

## [1.5.12] - 2017-07-17

### Added

-   Added additional API for grids.

### Changed

-   The Network Transmitter now uses 1 FE/t per block instead of 4 FE/t.

## [1.5.11] - 2017-07-16

### Fixed

-   Fixed not being able to smelt quartz into silicon.
-   Fixed Grid extracting wrong enchanted books.

## [1.5.10] - 2017-07-15

### Fixed

-   Fixed crash relating to MCMP.

### Changed

-   Converted Solderer recipes to JSON.
-   Implemented controller update throttling, should fix lag issues with controllers that constantly turn off and on.

## [1.5.9] - 2017-07-10

### Fixed

-   Fixed not being able to extract anything when connecting an External Storage to Storage Drawers.

## [1.5.8] - 2017-07-08

### Changed

-   Updated Forge to 2400.
-   Updated Storage Drawers API.
-   Autocrafting can now fill water bottles with water from the fluid storage - regular bottles or pattern for regular bottles are required.

### Fixed

-   Fixed bug where disks have to be re-inserted in the Disk Drive in order to work again after rejoining a chunk.
-   Fixed bug where items inserted in Storage Drawers through External Storage with a Drawer Controller wouldn't respect drawer priority rules.
-   Fixed crash on server when getting an advancement.

### Removed

-   Removed migration support for worlds from MC 1.10.2. To migrate your world to MC 1.12 from MC 1.10.2, first open it in MC 1.11.2.

## [1.5.7] - 2017-07-04

### Added

-   Added advancements.

### Changed

-   Exposed pattern inventory for Pattern Grid.

### Fixed

-   Fixed crashes relating to scrollbar in GUIs.

## [1.5.6] - 2017-06-29

### Changed

-   Updated Forge to stable 2387.

### Fixed

-   Fixed bug where players couldn't place regular blocks next to secured networks.

### Removed

-   Removed Processing Pattern Encoder, that functionality is now available in the Pattern Grid.

## [1.5.5-beta] - 2017-06-25

### Changed

-   Updated Forge to 2363.

## [1.5.4-beta] - 2017-06-24

### Fixed

-   Fixed External Storage crashing.
-   Fixed crash when node data mismatches between world and dat file.

## [1.5.3-beta] - 2017-06-24

### Added

-   The Portable Grid now exposes an inventory for interaction with other mods or vanilla.
-   The Interface now exposes the entire storage inventory (if no slots are set for exporting) for interaction with other mods or vanilla.

### Changed

-   Updated Forge to 2359.
-   Updated MCMultiPart to 2.2.1.

### Fixed

-   Fixed Solderer crashing.
-   Fixed Solderer being able to work with insufficient ingredients.
-   Fixed Interface extracting from itself when trying to keep items in stock.
-   Fixed Quartz Enriched Iron recipe only giving 1 instead of 4.
-   Fixed Debug Storage disks not working correctly.
-   Fixed Disk Drive giving incorrect capacity for creative and debug storage disks.

### Removed

-   The Relay now reacts instantly to a redstone signal again, removed throttling for it.

## [1.5.2-beta] - 2017-06-20

### Fixed

-   Fixed a bug where loading nodes would abort when a single node has an error while reading.
-   Fixed Filters not persisting correctly in Portable Grid.

## [1.5.1-beta] - 2017-06-20

### Added

-   Re-added MCMultiPart support.
-   Added back crafting recipes.

### Changed

-   Updated Forge to 2340.
-   Changed Grid recipe.
-   Changed Crafting Monitor recipe.

### Fixed

-   Fixed Filters not persisting correctly in Wireless Grid and Wireless Crafting Monitor.
-   Fixed Disk Drive recipe not using ore dictionary for chest.
-   Fixed crash when getting tooltip for grid item.

### Removed

-   Removed Tesla integration.
-   Removed RS energy units, the entire mod is powered with Forge Energy now.

## [1.5.0-alpha] - 2017-06-14

### Added

-   Port to Minecraft 1.12.
-   The Portable Grid now doesn't despawn anymore when dropped in the world.

### Fixed

-   Fixed bug where oredict autocrafting didn't work in some cases.

### Removed

-   Removed MCMultiPart support (will be re-added as soon as MCMultiPart for MC 1.12 is available).
-   Removed OpenComputers support (will be re-added as soon as OpenComputers for MC 1.12 is available).
-   Removed crafting recipes, until Forge adds the recipe system back.

## [1.4.20] - 2017-07-15

### Fixed

-   Fixed crash relating to MCMP.

## [1.4.19] - 2017-07-15

### Added

-   Implemented controller update throttling, should fix lag issues with controllers that constantly turn off and on.

### Fixed

-   Fixed bug where players couldn't place regular blocks next to secured networks.
-   Fixed crashes relating to scrollbar in GUIs.
-   Fixed bug where disks have to be re-inserted in the Disk Drive in order to work again after rejoining a chunk.

## [1.4.18] - 2017-06-24

### Fixed

-   Fixed Solderer crashing.
-   Fixed Interface extracting from itself when trying to keep items in stock.
-   Fixed Solderer being able to work with insufficient ingredients.
-   Fixed Disk Drive giving incorrect capacity for creative and debug storage disks.
-   Fixed External Storage crashing.
-   Fixed crash when node data mismatches between world and dat file.

### Removed

-   The Relay now reacts instantly to a redstone signal again, removed throttling for it.

## [1.4.17] - 2017-06-20

### Fixed

-   Fixed Filters not persisting correctly in Wireless Grid, Wireless Crafting Monitor and Portable Grid.
-   Fixed Disk Drive recipe not using ore dictionary for chest.
-   Fixed a bug where loading nodes would abort when a single node has an error while reading.

## [1.4.16] - 2017-06-14

### Added

-   The Portable Grid now doesn't despawn anymore when dropped in the world.

### Fixed

-   Fixed bug where oredict autocrafting didn't work in some cases.

## [1.4.15] - 2017-06-14

### Changed

-   Updated Storage Drawers API, fixes crashes.

## [1.4.14] - 2017-06-13

### Fixed

-   Fixed Solderer not accepting books made in anvil.

## [1.4.13] - 2017-06-13

### Added

-   The Portable Grid now has an indicator whether it's connected or disconnected and shows the disk.

### Fixed

-   Fixed Portable Grid model.
-   Fixed ore dictionary causing problems with Solderer.
-   Fixed ore dictionary items not showing up in JEI for the Solderer.

### Removed

-   Removed Quartz Enriched Iron ore dictionary entry.

## [1.4.12] - 2017-06-10

### Added

-   Added Korean translation.
-   Implemented block update throttling when network turns on and off.

### Changed

-   Updated Forge to 2315.
-   Updated JEI to 4.5.0.
-   You can now shift click items from the Grid crafting slots to the player inventory when the Grid is disconnected.

### Fixed

-   Fixed error logs when watching a Controller when a network changes.

### Removed

-   Removed Collosal Chests integration.

## [1.4.11] - 2017-06-05

### Added

-   Added support for External Storage on Interfaces and other Refined Storage blocks, so you can keep items in stock easier.
-   You now have to sneak to place the Portable Grid in the world.

### Changed

-   The Machine Casing now requires 1 piece of stone in the middle.
-   Changed recipe of Disk Drive to no longer require a Solderer.
-   Changed recipe of Interface to no longer require a Basic Processor, but a Machine Casing instead.

### Fixed

-   Fixed bug where storages that are removed remain visible.
-   Fixed bug where the GUI didn't close when a block is broken, causing a dupe bug with the Portable Grid.

### Removed

-   Removed debug log configuration option, as it's no longer needed.
-   Removed "autocraft on redstone signal" option in the Crafter, use an External Storage in combination with an Interface with the Crafting Upgrade instead.

## [1.4.10-beta] - 2017-05-25

### Changed

-   Improved performance of network scanning.

### Fixed

-   Fixed crash when attempting to get direction of a node.
-   Fixed bug where some network parts don't want to connect to the storage system.

## [1.4.9-beta] - 2017-05-24

### Fixed

-   Fixed bug where inventory data was lost sometimes upon opening the world.

## [1.4.8-beta] - 2017-05-24

### Fixed

-   Fixed missing config categories in ingame config.
-   Fixed Controller not working anymore after changing redstone setting.
-   Fixed crash when placing or destroying network blocks.

## [1.4.7-beta] - 2017-05-23

### Added

-   Added config option "debugLog" that logs diagnostic info to help developers to fix the inventory loss bug, please enable it if you are experiencing this issue.

### Fixed

-   Fixed bug where Portable Grid would dupe in inventory.
-   Worked around an autocrafting bug to prevent crashes.

## [1.4.6-beta] - 2017-05-17

### Changed

-   Performance improvement to network scanning.

### Fixed

-   Fixed Wrench opening GUIs while performing action.
-   Fixed client Grid GUI clearing and causing crashes after starting an autocrafting request.

### Removed

-   Removed debug output from v1.4.5.

## [1.4.5-beta] - 2017-05-14

### Added

-   Added Portable Grid.
-   Added OpenComputers integration.

### Changed

-   Updated Forge to 2296.
-   Removed ticking tile entities, every tile entity in RS is non-ticking now.

### Fixed

-   Fixed Crafting Tweaks buttons positioned wrongly.
-   Fixed Crafting Tweaks keybindings interfering with RS keybindings.
-   Fixed crash when updating storages.
-   Fixed no tooltips for fluid filter slots.
-   Fixed Disk Manipulator in fluid mode not showing fluids.
-   Fixed dupe bug in the Interface.

## [1.4.4-beta] - 2017-04-27

### Changed

-   Updated Forge to 2284.

### Fixed

-   Fixed Disk Manipulator crashing due to empty stack.
-   Fixed issue where empty stacks show up in the system.
-   Fixed Storage Monitor not respecting security settings.

## [1.4.3-beta] - 2017-04-22

### Added

-   Display progress bar on JEI recipes for the Solderer.

### Changed

-   Updated Forge to 2282.
-   Updated JEI version.
-   Updated MCMultiPart version.
-   You can now shift click Grid Filters into a Grid instead of manually inserting them.
-   You can now use up and down arrows to scroll through Grid search history.
-   Shift clicking patterns in the Pattern Grid now puts the patterns in the pattern slot.
-   Storage Monitors don't render any quantity text when no item is specified to monitor anymore.
-   The Solderer inventory isn't sided anymore.
-   Small performance improvement: only sort the storages when needed.

### Fixed

-   Fixed bug where disks in Disk Drive didn't respect access type or void excess stacks option.
-   Fixed crash in Disk Manipulator.
-   Fixed oredict not working.
-   Fixed Grid crash.
-   Fixed Fluid Grid not formatting large quantities correctly.

## [1.4.2-beta] - 2017-04-01

### Added

-   Implemented support for the Forge update JSON system.
-   Added integration for MCMultiPart, this is an optional dependency.
-   You can now specify more items to export, import, filter, etc. by inserting the Filter item.
-   Made the keybinding to focus on the Grid search bar configurable.

### Changed

-   Updated Forge to 2261.
-   The Detector no longer outputs a strong redstone signal.
-   Fire event on completion of an autocrafting task.
-   Fire "player crafting" event when shift clicking in the grid.

### Fixed

-   Fixed a crash with the Constructor.
-   Fixed Crafting Pattern model.
-   Fixed Quartz Enriched Iron and the block form of it not having an oredictionary entry.
-   Fixed crash in storage cache.
-   Fixed slow oredict comparisons causing TPS lag.
-   Fixed controller model warning during launch.
-   Fixed not rendering some tooltips correctly.
-   Fixed crash with External Storage.
-   Fixed Interface duping items on extract-only storages.
-   Fixed controls menu showing unlocalized text for Refined Storage keybindings.
-   Autocrafting bugfixes.
-   Improved memory usage of some models.
-   Performance improvements related to storage inserting and extracting.

### Removed

-   Removed support for the Deep Storage Unit API.

## [1.4.1-beta] - 2017-02-19

### Added

-   Added Storage Monitor.

### Changed

-   Updated Forge to 2232.

### Fixed

-   Fixed Processing Pattern Encoder and Security Manager recipes not supporting oredict workbench and chest.
-   Fixed network nodes not respecting redstone mode.
-   Fixed "Clear" and "Create Pattern" buttons not working correctly when using Grid Filter tabs.
-   Fixed Wrench in Dismantling Mode voiding Storage Block contents.
-   Fixed OPs not having global permissions on secured storage systems.
-   Fixed crash when Destructor tries to break secured network block.
-   Fixed Fluid Interface not dropping inventory contents.
-   Fixed Disk Manipulator crash.

## [1.4.0-beta] - 2017-02-06

### Added

-   Added Security Manager.
-   Added Security Card.
-   Added Wireless Fluid Grid.
-   Added craft-only toggle to Exporter.
-   Added Reader.
-   Added Writer.
-   Added blocking mode to patterns in autocrafting.
-   Added Grid size toggle (stretched, small, medium, large).
-   Added dismantling mode to the Wrench.
-   Added Block of Quartz Enriched Iron.
-   Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance.
-   Added filtering slots for the Crafting Monitor.
-   Added way to hide tasks created in an automated way in the Crafting Monitor.
-   Added Grid sorting by ID.
-   Added Solderer particles.
-   Added ore dictionary Grid filter (use `$` as prefix like in JEI).

### Changed

-   You can now bind multiple crafters with the same pattern to machines, to spread or balance out autocrafting.
-   Fluid Grid now first tries to get buckets from your inventory instead of the storage.
-   Updated Forge to 2226.
-   Updated Chinese translation.
-   Converting blocks instead of ingots to Printed Processors is now a little faster.
-   The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time.
-   Ignore damage for damageable items when transferring into crafting grid.
-   Ignore tags from given items when transferring into crafting grid.
-   Removed sidedness from fluid interface.
-   Using tab in a grid that isn't in autoselected mode will focus on the search box.
-   Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it.
-   Increased size of Detector textbox.
-   Handle breaking and placing blocks better for Constructor and Destructor.
-   Pressing shift while starting a crafting task will skip the crafting preview.

### Fixed

-   Fixed Regulator mode not working.
-   Fixed Fluid Interface still being sided.
-   Fixed Constructor not working on Botania flowers.
-   Fixed Wireless Transmitter working even if it was disabled with redstone mode.
-   Fixed Solderer not accepting books created in an Anvil.
-   Fixed bug where network machines stopped working on chunk borders.
-   Fixed memes not working.
-   Fixed External Storage crashes.
-   Fixed Constructor in liquid mode being able to place fluids &lt;1000 mB.
-   Fixed Solderer recipe conflicts, allowing for easier automation.
-   Fixed stack upgrades not working in exporter when stack size is 16.
-   Fixed crash when rotating External Storage.
-   Fixed disk textures not working on latest Forge.
-   Fixed crash when placing head with Constructor.
-   Autocrafting bugfixes.
-   Made sure External Storage always has the correct inventory in world.

## [1.3.5-alpha] - 2016-12-14

### Added

-   Added regulator mode to Exporter.

### Changed

-   Updated French translation.

### Fixed

-   Fixed TPS lag on very large crafting tasks.
-   Fixed not being able to use autocrafting on some EnderIO items.
-   Fixed server crash with ore dictionary checks.
-   Fixed Controller not using energy.
-   Fixed dupe bug when inserting bucket in Fluid Grid.
-   Fixed not being able to start autocrafting for storage disks.
-   Fixed oredict button not having the correct position on a small resolution.
-   Fixed Constructor not using Crafting Upgrade when in item dropping mode.

## [1.3.4-alpha] - 2016-12-10

### Added

-   Added option to check for oredict in the Grid Filter.
-   Added option to use a mod filter in the Grid Filter.
-   Added option to use a whitelist or blacklist in the Grid Filter.
-   Added Grid tabs using Grid Filters.
-   Added configuration option to enable large fonts in Grid.

### Changed

-   The Grid now resizes based on screen size (max rows can be configured).
-   Made Solderer tooltip less big.
-   Made the Interface sideless, you can just insert or extract from any side.

### Fixed

-   Fixed bug with opening a network item with food in offhand.
-   Fixed not respecting "Extract only" option for storages.
-   Fixed a few autocrafting bugs.
-   Fixed a crash with the Disk Manipulator.

## [1.3.3-alpha] - 2016-12-06

### Changed

-   Updated Forge to 2188.

### Fixed

-   Fixed not being able to start a crafting task.

## [1.3.2-alpha] - 2016-12-04

### Fixed

-   Fixed being able to exceed max stack size while shift clicking.
-   Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool.
-   Fixed client crash when placing network blocks.

## [1.3.1-alpha] - 2016-12-04

### Changed

-   Updated Forge to 2180.
-   Made Upgrades stackable.

### Fixed

-   Fixed Disk Drive not noticing a Storage Disk being shift clicked out of the GUI.

## [1.3.0-alpha] - 2016-12-03

### Added

-   Port to Minecraft 1.11.

### Removed

-   Removed RF support, use Forge Energy instead.
-   Removed IC2 support.
-   Removed MCMultiPart support (will be re-added as soon as MCMultiPart for MC 1.11 is available).

## [1.2.26] - 2017-06-10

### Fixed

-   Fixed Interface duping items on extract-only storages.
-   Fixed calculating crafting resources for more than 9 unique items, for addon mods.

## [1.2.25] - 2017-03-25

### Changed

-   Fire event on completion of an autocrafting task.
-   Fire player crafting event when shift clicking in the grid.
-   Allow INodeNetwork instances to return an ItemStack for display in Controller GUI.

## [1.2.24] - 2017-03-18

### Changed

-   Made the keybinding to focus on the Grid search bar configurable.

### Fixed

-   Autocrafting bugfixes.

## [1.2.23] - 2017-03-11

### Added

-   Implemented support for the Forge update JSON system.

### Changed

-   The Detector no longer outputs a strong redstone signal.

### Fixed

-   Fixed crash in storage cache.
-   Fixed Crafting Pattern model.
-   Fixed Constructor not working on Botania flowers.
-   Fixed Disk Manipulator crash.
-   Fixed slow oredict comparisons causing TPS lag.

## [1.2.22] - 2017-02-19

### Fixed

-   Fixed recipe for Processing Pattern Encoder not using oredictionary for the workbench.
-   Fixed Fluid Interface not dropping inventory contents.
-   Fixed glitchy upgrade recipes in the Solderer.

## [1.2.21] - 2017-02-07

### Fixed

-   Fixed crash when placing head with Constructor.

## [1.2.20] - 2017-02-02

### Added

-   Added Solderer particles.
-   Added Grid sorting by ID.

### Fixed

-   Fixed client side crash with cables.

## [1.2.19] - 2017-02-01

### Added

-   Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance.

### Changed

-   Updated cable part back texture and Construction and Destruction Core textures.
-   Updated Forge to 2221.
-   Updated Chinese translation.
-   Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it.
-   Increased size of Detector textbox.
-   Autocrafting bugfixes.
-   Handle breaking and placing blocks better for Constructor and Destructor.

### Fixed

-   Fixed stack upgrades not working in exporter when stack size is 16.
-   Fixed crash when rotating External Storage.
-   Fixed disk textures not working on latest Forge.

## [1.2.18] - 2017-01-22

### Changed

-   Fluid Grid now first tries to get buckets from your inventory instead of the storage.
-   Performance improvements with oredict autocrafting.

### Fixed

-   Fixed client side crash with cable.
-   Fixed client side crash with disk drive.
-   Fixed crash with external storage in fluid mode.

## [1.2.17] - 2017-01-12

### Added

-   Add Ore Dictionary grid filter (use $ as prefix like in JEI).

### Changed

-   Ignore damage for damageable items when transferring into crafting grid.
-   Ignore tags from given items when transferring into crafting grid.
-   Removed sidedness from fluid interface.
-   Using tab in a grid that isn't in autoselected mode will focus on the search box.
-   The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time.

### Fixed

-   Fixed Constructor in liquid mode being able to place fluids &lt;1000 mB.
-   Fixed Solderer recipe conflicts, allowing for easier automation.
-   Fixed machines not connecting with cable after Controller.

## [1.2.16] - 2017-01-04

### Fixed

-   Fixed an autocrafting regression.
-   Fixed crash with External Storage.

## [1.2.15] - 2017-01-03

### Fixed

-   Fixed Grid Filter hiding everything when 2 or more items are in it.
-   Fixed External Storage crash when breaking a connected inventory.
-   Autocrafting bugfixes.

## [1.2.14] - 2016-12-24

### Fixed

-   Fixed server crash.

## [1.2.13] - 2016-12-23

### Fixed

-   Fixed memes not working.
-   Fixed controller causing network rebuild on every neighbor change.
-   Fixed Wireless Transmitter working even if it was disabled with redstone mode.
-   Fixed Solderer not accepting books created in an Anvil.
-   Autocrafting bugfixes.
-   Made sure External Storage always has the correct inventory in world.

## [1.2.12] - 2016-12-16

### Changed

-   Updated French translation.

### Fixed

-   Fixed TPS lag on very large crafting tasks.
-   Fixed not being able to use autocrafting on some EnderIO items.
-   Fixed not being able to start autocrafting for storage disks.
-   Fixed oredict button not having the correct position on a small resolution.
-   Fixed Constructor not using Crafting Upgrade when in item dropping mode.

## [1.2.11] - 2016-12-10

### Added

-   Added configuration option to enable large fonts in Grid.

### Changed

-   The Grid now resizes based on screen size (max rows can be configured).
-   Made the Interface sideless, you can just insert or extract from any side.

## [1.2.10] - 2016-12-09

### Changed

-   Made Solderer tooltip less big.

### Fixed

-   Fixed a crash with the Disk Manipulator.
-   Fixed not respecting "Extract only" option for storages.
-   Fixed bug with opening a network item with food in offhand.
-   Fixed other fluid storages going negative when using void excess fluids option.
-   A few autocrafting bugfixes.

## [1.2.9] - 2016-12-06

### Changed

-   Updated Forge to 2185.

### Fixed

-   Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool.

## [1.2.8] - 2016-11-30

### Fixed

-   Fixed autocrafting bugs.

## [1.2.7] - 2016-11-29

### Changed

-   Updated German translation.

### Fixed

-   Fixed not being able to place disks in Disk Drives on servers.

## [1.2.6] - 2016-11-26

### Changed

-   Processing patterns can now insert buckets.

### Fixed

-   Fixed crash with Exporters in fluid mode.

### Removed

-   Removed Solderer progress percentage text.

## [1.2.5] - 2016-11-24

### Added

-   Added "View Recipes" JEI toggle in Solderer.

### Changed

-   The Constructor can now place fireworks.
-   Updated Forge to 2151.

### Fixed

-   Fixed a bunch of autocrafting bugs.
-   Fixed Grid search not working correctly.
-   Fixed items disappearing from Solderer inventory.
-   Fixed being able to take fluids that have less than 1000 millibuckets filled in Fluid Grid.
-   Fixed Constructor being able to place fluids that have less than 1000 millibuckets.
-   Fixed Exporter and Importer not working properly with fluids.
-   Fixed inserting new stack type with right click in Grid causing a desync.
-   Fixed Constructor not calling block place event.
-   Fixed shift clicking non disk items in the Disk Manipulator voiding them.

## [1.2.4] - 2016-11-10

### Added

-   Added tooltip search with #.

### Changed

-   Mod search can now also take mod name instead of just id.

### Fixed

-   Fixed bug where Disk Manipulator doesn't save disks.
-   Fixed Disk Drive stored quantity GUI text hovering over other text.
-   Fixed External Storage being in item and fluid mode at the same time.
-   Fixed Wrench working when player is not sneaking.
-   Fixed External Storage cache counting items up when extracting.
-   Fixed External Storage cache not working properly on Compacting Drawers.

### Removed

-   Removed ability to put External Storages on Refined Storage network blocks.

## [1.2.3] - 2016-11-06

### Fixed

-   Fixed fluid cache updating wrongly.
-   Fixed Exporter scheduling too many crafting tasks.

## [1.2.2] - 2016-11-05

### Fixed

-   Fixed item voiding when exporting to a chest with a storage in Extract Only mode.
-   Various autocrafting fixes.

## [1.2.1] - 2016-11-05

### Added

-   Added Wireless Crafting Monitor (with temporary textures).
-   Added support for JEI R and U keys in Grids.

### Changed

-   You can now decompose storage disks if the item count is below zero by any chance.

### Fixed

-   Fixed crafting upgrade having weird behavior.
-   Fixed external storage not updating when loading chunk.
-   Fixed external storage crash.
-   Fixed weird autocrafting behavior.

### Removed

-   Removed controller explosions when multiple controllers are connected to the same network.

## [1.2.0] - 2016-11-03

### Added

-   Added new autocrafting system.
-   Added ore dictionary autocrafting.
-   Added recipe transfer handler for Processing Pattern Encoder.
-   Added void excess items functionality to storage blocks.
-   Added config option to configure RS to EU conversion rates.
-   Added ability to toggle between insert and extract, only insert and only extract mode in storage blocks.
-   Added Silk Touch Upgrade for Destructor.
-   Added Fortune Upgrade for Destructor.
-   Added ore dictionary compare toggle to storage I/O blocks.
-   Added disk leds to Disk Drive block that shows the disks.
-   Added disk leds to Disk Manipulator block that shows the disks.
-   Added Wrench, has two modes: configuration saving / reading mode, and rotation mode.
-   Stack upgrade in Importer / Exporter in fluid mode and Fluid Interface now transfers 64 buckets at once.

### Changed

-   Changed storage GUIs.
-   Changed default EU conversion rate to be 1:8 with RS.
-   The Constructor can now drop items in the world.
-   The Constructor can now place skulls.
-   The Destructor can now pick up items in the world.
-   Storage disks and storage blocks now don't despawn anymore when dropped in the world.
-   Grid item and fluid quantity now only rounds to 1 digit after comma.
-   Items count can no longer overflow, and will max out at the maximum integer value.
-   Updated Storage Drawers API.
-   Controller sorts by energy usage in GUI (highest to lowest).
-   Detector without any filter will detect based on total items or fluids stored.
-   Limited network transmitter usage to 1000 RS/t.

### Fixed

-   Fixed lag issues caused by External Storage.
-   Fixed resetting a stack of patterns yields 1 blank pattern.
-   Fixed being able to pipe items in the export slots of the Interface.
-   Fixed Interface being stuck when item isn't accepted in storage.
-   Fixed items with colored name being uncolored in Grid.
-   Fixed fluid rendering bugging out side buttons.
-   Fixed item count going negative when using the Disk Manipulator.
-   Fixed Storage Drawer quantities not updating properly on Void Drawers.
-   Fixed Disk Manipulator blocking items transferring in some cases.
-   Fixed External Storage crafting recipe not supporting ore dictionary chests.
-   Fixed when shift clicking crafting recipe and inventory is full items are dropping on the ground instead of going in the system.
-   Fixed glitchy rendering of cable parts in item form.
-   Fixed Destructor being able to break bedrock.
-   Fixed External Storage thinking that items are inserted in Extra Utilities Trash Cans.
-   Fixed Grid quantities being unreadable when using unicode font.
-   Fixed disconnecting when Storage Disk or Storage Block is too big.

## [1.2.0-beta.8] - 2016-11-03

### Fixed

-   More autocrafting issues.

## [1.2.0-beta.7] - 2016-11-03

### Fixed

-   More autocrafting issues.
-   External Storage crashes and TPS lag issues.
-   Mekanism recipes are autocraftable again.

## [1.2.0-beta.6] - 2016-11-02

### Fixed

-   More autocrafting issues.
-   Oredict autocrafting has been improved.

## [1.2.0-beta.5] - 2016-10-30

### Fixed

-   More autocrafting issues.
-   External Storage TPS lag issues.

## [1.2.0-beta.4] - 2016-10-27

### Fixed

-   More autocrafting and TPS issues.

## [1.2.0-beta.3] - 2016-10-25

### Fixed

-   More autocrafting issues.

## [1.2.0-beta.2] - 2016-10-24

### Fixed

-   Extreme TPS issues while crafting.
-   Laggy disk drive rendering.

## [1.2.0-beta.1] - 2016-10-23

### Changed

-   First beta release for v1.2.0, featuring a new autocrafting system.

## [1.1.3] - 2016-10-07

### Fixed

-   Fixed some clients not starting up due to too many Disk Drive model permutations.

## [1.1.2] - 2016-10-03

### Added

-   Added recipe transfer handler for Solderer.

### Changed

-   It is now possible to start a crafting task even if the crafting preview says you can't.

### Fixed

-   Fixed crash with JEI when changing screens in autocrafting.
-   Fixed not being able to start autocrafting in other dimensions with Network Transmitter / Network Receivers.
-   Fixed JEI overlay disappearing now and again.
-   Fixed Detector hitbox.

## [1.1.1] - 2016-09-28

### Fixed

-   Fixed crash on servers.

## [1.1.0] - 2016-09-28

### Added

-   New art by CyanideX.
-   Added crafting preview screen.
-   Added max crafting task depth.
-   Added helpful tooltips to Solderer and Processing Pattern Encoder.

### Changed

-   Every machine now compares on damage and NBT by default.
-   Updated JEI, fixes crashes.
-   Detector amount text field doesn't autoselect anymore.

### Fixed

-   Fixed crash with Disk Manipulator.
-   Fixed autocrafting not giving back byproducts.

## [1.0.5] - 2016-09-21

### Added

-   Importer now takes a Destruction Core, and Exporter a Construction Core.
-   Added Disk Manipulator.
-   Added ingame config.
-   Added the ability to see the output of a Pattern by holding shift.
-   Exporter in fluid mode and Fluid Interface no longer duplicates fluids that are less than 1 bucket.

### Changed

-   Changed default Grid sorting type to quantity.
-   Updated Dutch translation.
-   Updated Chinese translation.
-   When a machine is in use by a crafting pattern, inserting of items from other patterns will be avoided.

### Fixed

-   Fixed crafting a complex item causes the process to flow off the Crafting Monitor's GUI.
-   Fixed shift clicking from Grid when player inventory is full throwing items in the world.

## [1.0.4] - 2016-09-17

### Fixed

-   Fixed lag caused by Crafter.

## [1.0.3] - 2016-09-17

### Added

-   Added integration for Forge energy.

### Changed

-   Solderer now accepts items from any side, allowing easier automation.
-   Solderer is now intelligent about items in slots, and will only accept an item if it is part of a recipe.
-   Changed recipe for upgrades in the Solderer, they now just take 1 of the unique item instead of 2, using redstone instead.
-   Updated to Forge 2088.

### Fixed

-   Fixed item loading issue.
-   Fixed fluid autocrafting scheduling too much crafting tasks for buckets.
-   Fixed blocks in hand facing wrong direction.

## [1.0.2] - 2016-09-14

### Changed

-   \+64 in crafting start GUI now gives 64 from the first time instead of 65.

### Fixed

-   Fixed processing patterns not handling item insertion sometimes.

### Removed

-   Removed crafting task limit in crafting start GUI.

## [1.0.1] - 2016-09-13

### Added

-   Added "autocrafting mode" in Detector, to check if an item is being crafted. If no item is specified, it'll emit a signal if anything is crafting.
-   Added an option for the Crafter to trigger autocrafting with a redstone signal.

### Changed

-   Updated to Forge 2084.

### Fixed

-   Fixed advanced tooltips showing in Grid when not configured to do so.
-   Optimized crafting pattern loading.

## [1.0.0] - 2016-09-12

### Added

-   Interface now supports Crafting Upgrade.
-   Implemented multithreaded autocrafting.

### Changed

-   Processing patterns now hold their items back for pushing until all the required items are gathered from the system.
-   Reworked Crafting Monitor GUI.
-   When shift clicking a recipe in the Crafting Grid, the player inventory is now leveraged as well.
-   Updated to Forge 2077.
-   Due to the new crafting system, all Crafting Patterns made before 1.0 have to be re-made.

### Fixed

-   Fixed item and fluid storage stored count having incorrect values at times.
-   Fixed problems relating to Crafting Upgrade (scheduling a task wrongly, blocking other tasks, etc).
-   Fixed machines breaking on long distances.
-   Fixed Controller rebuilding network graph on energy change.
-   Fixed fluids not caring about NBT tags.
-   Fixed fluids that have less than 1 bucket stored render only partly in Fluid Grid.
-   Fixed Fluid Interface voiding bucket when shift clicking to out slot.
-   Fixed wrong machine connection logic.

## [0.9.4-beta] - 2016-08-27

### Changed

-   Reduced explosion radius when multiple controllers are connected to the same network.

### Fixed

-   Fixed mod not working without JEI.
-   Little fixes in German translation.
-   Reverted network changes that caused buggy behavior.

## [0.9.3-beta] - 2016-08-26

### Added

-   Added Chinese translation.
-   Added Crafting Tweaks integration.

### Changed

-   Updated German translation for Fluid Storage.
-   Updated Dutch translation for Fluid Storage.
-   Reworked storage network code, should fix weird machine disconnection issues.

### Fixed

-   Fixed that the Fluid Storage Disk recipe returns an invalid disk.

## [0.9.2-beta] - 2016-08-25

### Fixed

-   Fixed not being able to take out items from Wireless Grid cross-dimensionally.

## [0.9.1-beta] - 2016-08-24

### Fixed

-   Fixed server crash with Grid.

## [0.9.0-beta] - 2016-08-24

### Added

-   Added fluid storage.
-   Added Russian translation.

### Changed

-   Energy usage of Wireless Grid is now configurable.
-   Wireless Transmitters can now only be placed on Cable.
-   Priority field and detector amount field can now display 4 digits at a time.

### Fixed

-   Fixed crash with Grid.
-   Fixed Grid Filter only updating the Grid when reopening the GUI.
-   Fixed Wireless Grid not working cross dimensionally.
-   Fixed Grid not displaying items after changing redstone mode.
-   Fixed Wireless Transmitter crashing when it is transmitting to a removed dimension.
-   Fixed disassembling stacked Storage Blocks only returns 1 set of items.

## [0.8.20-beta] - 2016-08-18

### Fixed

-   Fixed crash with Grid.

## [0.8.19-beta] - 2016-08-13

### Fixed

-   Fixed item duplication bug with External Storage.
-   Fixed External Storage taking too long to update storage.
-   Fixed crash with Grid.
-   Fixed crash when shift clicking unsupported item in a slot.

## [0.8.18-beta] - 2016-08-11

### Fixed

-   Fixed Detector mode not persisting.
-   Fixed bug where scrollbar didn't scroll correctly and thus hiding some items.
-   Fixed Network Transmitter not dropping inventory when broken.

## [0.8.17-beta] - 2016-08-09

### Fixed

-   Fixed Grid causing sorting lag on the client.

## [0.8.16-beta] - 2016-08-09

### Added

-   Added German translation by ChillUpX.
-   Added MCMultiPart integration for Cable Parts.

### Changed

-   You now have to click the actual cable part head in order to get the GUI open.
-   Grid Filters can now only filter 9 items, but, Grids take 4 filters now instead.
-   Grid Filters can now be configured to compare on NBT and/ or damage.
-   It is now possible to shift click items to the Storage Device filters.
-   Updated to Forge 2046.
-   Updated Tesla.
-   Java 8 is now a requirement.
-   Slight performance increase and network efficiency improvement in all GUI's.
-   Slight performance increase in Grid GUI.
-   Improved collisions of Cable parts.

### Fixed

-   Fixed issue with IC2 integration causing console spam.
-   Fixed not being able to change some configs in blocks.
-   Fixed serverside configs not syncing up with clientside.
-   Fixed not being able to move inventory items in Grid GUI's to hotbar via the number keys.
-   Fixed Relays when being in "Ignore Redstone" mode using up energy.
-   Fixed Crafter facing bottom side on placement.

## [0.8.15-beta] - 2016-08-01

### Fixed

-   Fixed server startup crash.

## [0.8.14-beta] - 2016-08-01

### Added

-   Added Interdimensional Upgrade so the Network Transmitter can work over different dimensions.

## [0.8.13-beta] - 2016-07-31

### Added

-   Added config option to set the base energy usage of the Controller (default is 0).
-   Added Grid Filter item to filter items in any Grid.
-   Added support for processing patterns with big stacksizes.
-   Added Network Transmitter, Network Receiver and Network Cards.

### Changed

-   The slot where the Wireless Grid is in in the Wireless Grid GUI is now disabled, so the item can't be thrown out of the inventory by accident.
-   Changed Relay recipe to use redstone torch instead of Basic Processor.
-   Placed machines now face the block they are placed on, like hoppers.

### Fixed

-   Fixed rendering crash with Disk Drive.
-   Fixed crash when quickly toggling sorting direction in Grid.
-   Fixed not being able to clear exporter row in interface.

## [0.8.12-beta] - 2016-07-20

### Fixed

-   Fixed dupe bug when shift clicking output slot in grid.

## [0.8.11-beta] - 2016-07-19

### Added

-   Added X button to Processing Pattern Encoder to clear configuration of inputs and outputs.
-   Added Grid view toggle buttons (regular, craftable items only, no craftable items).
-   Added ability to shift click items into Importer, Exporter, Constructor, Destructor and Detector to set up whitelist / blacklist configurations easier.
-   Re-added opposite facing on shift click functionality.

### Changed

-   Solderer upgrades go to upgrades slots first now when shift clicking.
-   Updated to Forge 2014.

### Fixed

-   Fixed minor dupe bug with JEI transferring.
-   Fixed exporter crafting upgrades taking priority over other tasks.
-   Fixed NPE with incorrectly initialized disks.
-   Fixed not being able to take out items of Grid 2K16.
-   Fixed not being able to start autocrafting for certain items (most notably IC2 items).

## [0.8.10-beta] - 2016-07-13

### Fixed

-   Fixed not being able to get some items out of Grid.
-   Fixed slight glitch in Constructor and Destructor model.

## [0.8.9-beta] - 2016-07-10

### Added

-   Added a model for the Constructor.
-   Added a model for the Destructor.

### Changed

-   Wireless Transmitters next to each other without any cable or without being connected to a machine won't work anymore, they need to be explictly connected to a cable or other machine.
-   Some models / texture tweaks.

### Fixed

-   Fixed bug where Grid crafting doesn't handle remainder sometimes.
-   Fixed caching issues with External Storage.
-   Fixed possible crash with Disk Drives.

## [0.8.8-beta] - 2016-07-10

### Changed

-   Use ore dictionary for recipes with glass.
-   Texture tweaks.

### Fixed

-   Fixed solderer not working with automation anymore.

## [0.8.7-beta] - 2016-07-09

### Added

-   Added better hitbox for the Solderer.

### Changed

-   Wireless Transmitter is now only bright red when connected.
-   Improved detector model, add a better hitbox for it.
-   Improved the Wireless Transmitter texture.
-   Made the Solderer beams be bright red when they are working.

### Fixed

-   Fixed crash with External Storage.
-   Fixed Detector not unpowering when disconnected from the network.

## [0.8.6-beta] - 2016-07-09

### Added

-   Re-added Controllers exploding when two of them are connected to the same network.
-   Added new textures.
-   Added model for External Storage.
-   Added model for Importer.
-   Added model for Exporter.
-   Added model for Detector.

### Changed

-   Huge performance improvements to large storage networks.
-   Limited some blocks to only have a direction on the x-axis.
-   Decreased amount of block updates significantly.

### Fixed

-   Fixed External Storage disconnecting on world reload.
-   Fixed External Storage not updating correctly.
-   Fixed wireless signal starting from Controller instead of per Wireless Transmitter individually.
-   Fixed Controller's redstone state not saving.
-   Fixed crafting tasks not saving properly.

### Removed

-   Removed opposite facing on placement mechanic.
-   Removed Quartz Enriched Iron Block.

## [0.8.5-beta] - 2016-07-04

### Fixed

-   Fixed crash when Tesla API is not installed.

## [0.8.4-beta] - 2016-07-04

### Added

-   Added a debug storage disk.
-   Added tooltip to solderer progress bar that shows progress percentage.
-   Added support for the Tesla energy system.
-   Added support for the IC2 (EU) energy system.
-   Added a Portuguese (Brazilian) translation.

### Changed

-   Performance improvements.
-   Tweaked grid GUI.

### Removed

-   Removed delay until grid items are visible.

## [0.8.3-beta] - 2016-07-02

### Fixed

-   Fixed drawer controllers not working with external storage.
-   Fixed right click taking 64 items instead of 32 items.

## [0.8.2-beta] - 2016-07-01

### Changed

-   It is now possible to use middle click multiple times for the same item in grid.
-   Made the mod configurable with a config file.

### Fixed

-   Fixed not being able to take items sometimes.

## [0.8.1-beta] - 2016-06-30

### Fixed

-   Fixed upgrades from interface not dropping.
-   Fixed lag caused by constantly rebuilding storage.

## [0.8.0-beta] - 2016-06-25

### Changed

-   Recompile for Minecraft 1.10.

### Fixed

-   Fixed solderer not using extra RF/t with upgrades.

## [0.7.19-beta] - 2016-06-25

### Fixed

-   Fixed controller being buggy with reconnecting.
-   Fixed controller texture updating too slow when energy changes.
-   Fixed not being able to take item from grid at times.
-   Fixed external storage on storage drawer sending an item count of 0 over.

## [0.7.18-beta] - 2016-06-24

### Fixed

-   Fixed cables sending updates when not needed.
-   Fixed cables not connecting to foreign machines that implement the API.

## [0.7.17-beta] - 2016-06-24

### Changed

-   Updated Forge to 1969.
-   Updated JEI to 3.6.x.
-   Introduced new crafting settings GUI.
-   Tweaked some textures.

### Fixed

-   Fixed getting wrong items back in grid.
-   Fixed wrong item getting crafted.
-   Fixed server lag with exporter and importer.

## [0.7.16-beta] - 2016-06-19

### Added

-   Added support for Storage Drawers void upgrade.
-   Added support for Deep Storage Unit API again.

### Fixed

-   Fixed NPE in machine searching.
-   Fixed a bug with interface giving negative amounts of items.
-   Fixed crash when using scroll wheel.

## [0.7.15-beta] - 2016-06-18

### Fixed

-   Fixed not being able to scroll with the scroll wheel using MouseTweaks.
-   Fixed grid search box mode only changing after reopening GUI.

## [0.7.14-beta] - 2016-06-17

### Added

-   Added shift clicking support to every inventory (for upgrades etc).
-   Added grid filtering options: @ for searching on mod items, # for searching on tooltips.
-   Added a way to clear patterns (shift + right click in inventory).

### Changed

-   Updated Forge to build 1965.
-   Tweaked some recipes.
-   Tweaked energy usage in some machines.

### Fixed

-   Fixed item overflow bug with storage drawers and external storage.

## [0.7.13-beta] - 2016-06-16

### Added

-   Added ability to triple click in grid.

## [0.7.12-beta] - 2016-06-14

### Fixed

-   Fixed creative storage blocks and disks not working.
-   Fixed interface overflowing.

## [0.7.11-beta] - 2016-06-12

### Changed

-   Right click on grid search bar clears the search query.

### Fixed

-   Fixed crash with wireless grid.
-   Fixed high RF/t usage on external storage.
-   Fixed that requesting crafting processing task yields too many tasks.

## [0.7.10-beta] - 2016-06-11

### Fixed

-   Fixed inventories not saving correctly.
-   Fixed that the player can't shift-click patterns into the last 3 slots of the Crafter.

## [0.7.9-beta] - 2016-06-11

### Added

-   Added an API.
-   Added Storage Drawers integration.
-   Added handling for patterns that return the same item.
-   Added stack splitting between multiple storages.
-   Added handling for patterns that give back the same item.

### Changed

-   Increased cable recipe to 12 cables.

### Fixed

-   Fixed not being able to place sugar cane.
-   Fixed not being able to place seeds.
-   Fixed stacks not splitting between storages correctly.
-   Fixed storage not saving ItemStack capabilities.
-   Fixed dropping items into crafting grid with mouse won't work if your mouse is in between items.
-   Fixed controller still drawing power even if disabled.

## [0.7.8-beta] - 2016-06-04

### Changed

-   Updated to Forge 1951.

### Fixed

-   Fixed crash on some worlds.
-   Improved Grid performance when sorting on quantity.

## [0.7.7-beta] - 2016-06-04

### Added

-   Added the Stack Upgrade.
-   Added Quartz Enriched Iron Block.
-   Added French translation by Leventovitch.

### Changed

-   New items now go to the first available storage that has items in it already.
-   Tweaked some recipes.

### Fixed

-   Fixed buggy reequip animation on wireless grid.
-   Fixed solderer not supporting ore dictionary.
-   Fixed recipes not supporting ore dictionary.
-   Fixed destructor not being able to destroy some blocks.
-   Fixed not being able to place or destroy sugar cane.
-   Fixed storage blocks not being dismantable.
-   Fixed getting more items than needed sometimes.
-   Performance improvements.

## [0.7.6-beta] - 2016-05-29

### Changed

-   Updated to Forge 1932.

### Fixed

-   Fixed not being able to start an autocraft.

## [0.7.5-beta] - 2016-05-29

### Fixed

-   Fixed wrong ascending / descending order in Grid.
-   Fixed autocrafting not giving back byproducts.
-   Fixed Solderer causing too many chunk updates.
-   Fixed Solderer slot sides being weird.
-   Performance improvements.

## [0.7.4-beta] - 2016-05-27

### Changed

-   Updated to Forge 1922.

### Fixed

-   Performance improvements.

## [0.7.3-beta] - 2016-05-25

### Changed

-   Crafting tasks are now sorted from new to old in the Crafting Monitor.
-   Broke Interface block inventory compatibility: make sure to take all your items out of your Interface blocks before you apply the update.

### Fixed

-   Fixed grid performance by not sending grid data so often.
-   Fixed silicon + quartz enriched iron not having oredict names.

## [0.7.2-beta] - 2016-05-24

### Fixed

-   Fixed Importer getting stuck on slot.

## [0.7.1-beta] - 2016-05-24

### Fixed

-   Fixed NPE in some tiles.
-   Fixed going out of crafting GUI not restoring state (scrollbar and search term).
-   Fixed not being able to create a pattern in disconnected Pattern Grid.
-   Fixed not being able to place cake or string.
-   Performance improvement to Grids.

## [0.7.0-beta] - 2016-05-23

### Added

-   Port to Minecraft 1.9.4.

### Fixed

-   Fixed Crafting Grid / Pattern Grid not throwing items on break.

## [0.6.15-alpha] - 2016-05-29

### Fixed

-   Fixed Solderer sides being weird.
-   Fixed Solderer causing too many block updates.

## [0.6.14-alpha] - 2016-05-28

### Fixed

-   Fixed wrong ascending / descending order in Grid.
-   Performance improvements.

## [0.6.13-alpha] - 2016-05-27

### Fixed

-   Performance improvements.

## [0.6.12-alpha] - 2016-05-25

### Changed

-   Crafting tasks are now sorted from new to old in the Crafting Monitor.

### Fixed

-   Fixed Crafting Grid / Pattern Grid not throwing items on break.
-   Fixed NPE in some tiles.
-   Fixed going out of crafting GUI not restoring state (scrollbar and search term).
-   Fixed not being able to place cake or string.
-   Fixed Importer getting stuck on slot.
-   Fixed silicon + quartz enriched iron not having oredict names.
-   Performance improvement to Grids.

## [0.6.11-alpha] - 2016-05-23

### Changed

-   Converted all inventories in the mod to Forge's item handler capability system.

### Fixed

-   Fixed crafting patterns crashing when item of an input or output no longer exists.
-   Fixed Grid letting the current held item flicker.
-   Fixed Importer / Exporter / External Storage not being able to push or pull out of the other side of a double chest.

## [0.6.10-alpha] - 2016-05-21

### Changed

-   Increased max crafting request size to 500.

### Fixed

-   Fixed Processing Patterns not working.
-   Fixed not being able to request more than 1 item at once.
-   Fixed crash with the Solderer.

## [0.6.9-alpha] - 2016-05-20

### Added

-   Added automation for the Solderer: every side corresponds to a slot (see the wiki).

### Fixed

-   Fixed bug where machines wouldn't disconnect / connect when needed outside of chunk.
-   Fixed not being able to toggle redstone mode in a Wireless Transmitter.
-   Fixed same machine being connected to the network multiple times.
-   Fixed External Storage not working.
-   Reduced network usage.

## [0.6.8-alpha] - 2016-05-19

### Fixed

-   Fixed CTRL + pick block on machines crashing game.
-   Performance improvements.

## [0.6.7-alpha] - 2016-05-19

### Fixed

-   Performance improvements.

## [0.6.6-alpha] - 2016-05-18

### Fixed

-   Fixed being able to insert non-allowed items in inventories with hoppers.
-   Fixed Processing Pattern Encoder not using up a Pattern.

## [0.6.5-alpha] - 2016-05-18

### Changed

-   Updated Forge to build 1907.

### Fixed

-   Performance improvements for servers.
-   Performance improvements for client scrollbars.

## [0.6.4-alpha] - 2016-05-17

### Fixed

-   Performance improvements.

## [0.6.3-alpha] - 2016-05-17

### Fixed

-   Performance improvements.

## [0.6.2-alpha] - 2016-05-17

### Added

-   Added a max crafting quantity per request cap (hardcoded to 100).

### Changed

-   Upgrades now draw extra energy.

### Fixed

-   Fixed race condition with crafting tasks.
-   Fixed pressing escape in crafting settings GUI not going back to grid GUI.
-   Fixed losing autoselection in Grid when clicking on slot with autoselection mode.
-   Fixed being able to pick up from pattern result slot.

## [0.6.1-alpha] - 2016-05-16

### Fixed

-   Fixed NPE on world load.
-   Fixed Destructor crashing when removing a connected machine.

## [0.6.0-alpha] - 2016-05-16

### Added

-   Added autocrafting.
-   Added the Pattern Grid.
-   Added the Crafting Monitor.
-   Added the Crafter.
-   Added the Processing Pattern Encoder.
-   Added a Pattern item.
-   Added the Wireless Transmitter.
-   Added Speed Upgrades which are applicable on a bunch of machines.
-   Added Range Upgrades for in the Wireless Transmitter.
-   Added Crafting Upgrades.
-   Added recipe category -> item JEI integration.
-   Added Storage Housing.

### Changed

-   Changed Grid modes to have a autoselected option.

### Fixed

-   Fixed Destructor not playing block break sound.
-   Fixed Constructor not playing block place sound.
-   Fixed picking up from crafting result slot.
-   Fixed being able to use right click on crafting result slot.
-   Fixed item duplication issue with the Interface.
-   Fixed Importers and Exporters not working when changing facing with a wrench.
-   Fixed Crafting Grid not respecting remainder in recipes.
-   Fixed Crafting Grid giving back the wrong amount of items when shift clicking.
-   Fixed items disappearing in Grid when doing a weird combination of inputs.
-   Fixed Solderer not stacking items.
-   Fixed Importer voiding Storage Disks from the Disk Drive.
-   Fixed Controller not saving energy.
-   Massive performance improvements which reduces lag and lets machines connect almost instantly.

## [0.5.6-alpha] - 2016-04-29

### Fixed

-   Fixed sorting crash.
-   Fixed autofocusing on priority field in storage GUIs.
-   Fixed controller causing lag when energy level changes.

## [0.5.5-alpha] - 2016-04-09

### Changed

-   Updated to Forge 1859.

### Fixed

-   Fixed several crashes.
-   Energy level on Controller is maintained.

## [0.5.4-alpha] - 2016-04-05

### Changed

-   Shift clicking on placing Constructor and Destructor will have opposite direction.

### Fixed

-   Fixed machines out of the Controller's chunk range only connecting after block break when rejoining the world.
-   Fixed scrollbar skipping some rows when scrolling with mouse wheel.
-   Fixed machines from a long distance not being visible in the Controller.

## [0.5.3-alpha] - 2016-04-04

### Added

-   Added a Creative Wireless Grid.

### Changed

-   Changed block hardness levels.

### Fixed

-   Fixed not being able to open a Grid that is 256 blocks away from the Controller.
-   Made the mod way less network intensive.

## [0.5.2-alpha] - 2016-04-03

### Added

-   Items that don't exist anymore, won't be added to storage again to avoid crashes.

### Fixed

-   Fixed not being able to run the mod without JEI.

## [0.5.1-alpha] - 2016-04-03

### Fixed

-   Fixed Disk Drive crashing with an `AbstractMethodException`.

## [0.5.0-alpha] - 2016-04-03

### Added

-   Deep Storage Unit integration (with this several barrel mods are now supported too!).
-   When placing Importer, Exporter or External Storage with SHIFT, it will have the opposite direction. This is for easy placement behind other blocks (furnaces for example).
-   Added mass crafting of items with shift in Crafting Grid.
-   Added JEI recipe transfering in Crafting Grid.
-   New textures.
-   Scrollbar in Grid and Crafting Grid.
-   Display of connected machines in the Controller GUI.
-   Nice formatting for items >= 1K (pressing shift while hovering over an item will still display the real item count).
-   Grid can now synchronize with JEI.

### Changed

-   Updated to the latest Forge and JEI.
-   Renamed Drives to Disk Drives.
-   Renamed Storage Cells to Storage Disks.
-   Wireless Grid is now bound to a Controller instead of a Grid.
-   Drives have a better interface and there are now blacklist and whitelist filters for the Storage Disks in it too.
-   Destructors have the ability to whitelist and blacklist certain items now.
-   Side buttons in machine GUIs are now left, not right.
-   Shift clicking stuff in the Interface.
-   Made the normal Grid 1 row larger.
-   Machines don't need to be connected with cables anymore, they can be next to each other too.
-   Made the amount text in the Grid for items smaller.

### Fixed

-   Fixed clicking sound in Grid.
-   Fixed a bunch of crashes.
-   Fixed Exporter not exporting is some cases.
-   Fixed Importer not importing in some cases.
-   Fixed Controller drawing RF every 20 ticks instead of every tick.
-   Fixed not being able to shift click from Crafting Grid crafting slots.
-   Fixed new items inserted after crafting in Grid being laggy.
-   Fixed flickering of items in Grid.
-   Fixed getting a stack of unstackable items from Grid.
-   Fixed Cable not having a collision box.
-   Check if the Constructor can actually place said block in the world.

### Removed

-   Removed Wireless Transmitters.

## [0.4.1-alpha] - 2016-03-24

### Fixed

-   Fixed ID duplication issues.

## [0.4.0-alpha] - 2016-03-21

### Added

-   Relays.
-   Interfaces.

### Changed

-   Cables now have actual collision.

### Fixed

-   Fix Minecraft reporting that retrieving Grid type fails.
-   Fullness percentage in Creative Storage Blocks going under 0%.
-   The Controller shouldn't display the base usage when not working.
-   Check if item is valid for slot before pushing to inventories.

## [0.3.0-alpha] - 2016-03-20

### Added

-   Initial release for Minecraft 1.9.

## [0.2.1-alpha] - 2016-02-03

### Added

-   Internal test release.

## [0.2.0-alpha] - 2016-01-31

### Added

-   Internal test release.

## [0.1.1-alpha] - 2016-01-31

### Added

-   Internal test release.

## [0.1.0-alpha] - 2016-01-03

### Added

-   Internal test release.

[Unreleased]: https://github.com/refinedmods/refinedstorage/compare/v1.13.0-beta.2...HEAD

[1.13.0-beta.2]: https://github.com/refinedmods/refinedstorage/compare/v1.13.0-beta.1...v1.13.0-beta.2

[1.13.0-beta.1]: https://github.com/refinedmods/refinedstorage/compare/v1.12.4...v1.13.0-beta.1

[1.12.4]: https://github.com/refinedmods/refinedstorage/compare/v1.12.3...v1.12.4

[1.12.3]: https://github.com/refinedmods/refinedstorage/compare/v1.12.2...v1.12.3

[1.12.2]: https://github.com/refinedmods/refinedstorage/compare/v1.12.1...v1.12.2

[1.12.1]: https://github.com/refinedmods/refinedstorage/compare/v1.12.0...v1.12.1

[1.12.0]: https://github.com/refinedmods/refinedstorage/compare/v1.11.7...v1.12.0

[1.11.7]: https://github.com/refinedmods/refinedstorage/compare/v1.11.6...v1.11.7

[1.11.6]: https://github.com/refinedmods/refinedstorage/compare/v1.11.5...v1.11.6

[1.11.5]: https://github.com/refinedmods/refinedstorage/compare/v1.11.4...v1.11.5

[1.11.4]: https://github.com/refinedmods/refinedstorage/compare/v1.11.3...v1.11.4

[1.11.3]: https://github.com/refinedmods/refinedstorage/compare/v1.11.2...v1.11.3

[1.11.2]: https://github.com/refinedmods/refinedstorage/compare/v1.11.1...v1.11.2

[1.11.1]: https://github.com/refinedmods/refinedstorage/compare/v1.11.0...v1.11.1

[1.11.0]: https://github.com/refinedmods/refinedstorage/compare/v1.10.6...v1.11.0

[1.10.6]: https://github.com/refinedmods/refinedstorage/compare/v1.10.5...v1.10.6

[1.10.5]: https://github.com/refinedmods/refinedstorage/compare/v1.10.4...v1.10.5

[1.10.4]: https://github.com/refinedmods/refinedstorage/compare/v1.10.3...v1.10.4

[1.10.3]: https://github.com/refinedmods/refinedstorage/compare/v1.10.2...v1.10.3

[1.10.2]: https://github.com/refinedmods/refinedstorage/compare/v1.10.1...v1.10.2

[1.10.1]: https://github.com/refinedmods/refinedstorage/compare/v1.10.0...v1.10.1

[1.10.0]: https://github.com/refinedmods/refinedstorage/compare/v1.10.0-beta.4...v1.10.0

[1.10.0-beta.4]: https://github.com/refinedmods/refinedstorage/compare/v1.10.0-beta.3...v1.10.0-beta.4

[1.10.0-beta.3]: https://github.com/refinedmods/refinedstorage/compare/v1.10.0-beta.2...v1.10.0-beta.3

[1.10.0-beta.2]: https://github.com/refinedmods/refinedstorage/compare/v1.10.0-beta.1...v1.10.0-beta.2

[1.10.0-beta.1]: https://github.com/refinedmods/refinedstorage/compare/v1.9.18...v1.10.0-beta.1

[1.9.18]: https://github.com/refinedmods/refinedstorage/compare/v1.9.17...v1.9.18

[1.9.17]: https://github.com/refinedmods/refinedstorage/compare/v1.9.16...v1.9.17

[1.9.16]: https://github.com/refinedmods/refinedstorage/compare/v1.9.15...v1.9.16

[1.9.15]: https://github.com/refinedmods/refinedstorage/compare/v1.9.14...v1.9.15

[1.9.14]: https://github.com/refinedmods/refinedstorage/compare/v1.9.13...v1.9.14

[1.9.13]: https://github.com/refinedmods/refinedstorage/compare/v1.9.12...v1.9.13

[1.9.12]: https://github.com/refinedmods/refinedstorage/compare/v1.9.11...v1.9.12

[1.9.11]: https://github.com/refinedmods/refinedstorage/compare/v1.9.10...v1.9.11

[1.9.10]: https://github.com/refinedmods/refinedstorage/compare/v1.9.9...v1.9.10

[1.9.9]: https://github.com/refinedmods/refinedstorage/compare/v1.9.8...v1.9.9

[1.9.8]: https://github.com/refinedmods/refinedstorage/compare/v1.9.7...v1.9.8

[1.9.7]: https://github.com/refinedmods/refinedstorage/compare/v1.9.6...v1.9.7

[1.9.6]: https://github.com/refinedmods/refinedstorage/compare/v1.9.5...v1.9.6

[1.9.5]: https://github.com/refinedmods/refinedstorage/compare/v1.9.4...v1.9.5

[1.9.4]: https://github.com/refinedmods/refinedstorage/compare/v1.9.3-beta...v1.9.4

[1.9.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.9.2-beta.2...v1.9.3-beta

[1.9.2-beta.2]: https://github.com/refinedmods/refinedstorage/compare/v1.9.2-beta.1...v1.9.2-beta.2

[1.9.2-beta.1]: https://github.com/refinedmods/refinedstorage/compare/v1.9.1-beta...v1.9.2-beta.1

[1.9.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.9.0-beta...v1.9.1-beta

[1.9.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.8...v1.9.0-beta

[1.8.8]: https://github.com/refinedmods/refinedstorage/compare/v1.8.7...v1.8.8

[1.8.7]: https://github.com/refinedmods/refinedstorage/compare/v1.8.6-beta...v1.8.7

[1.8.6-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.5-beta...v1.8.6-beta

[1.8.5-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.4-beta...v1.8.5-beta

[1.8.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.3-beta...v1.8.4-beta

[1.8.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.2-beta...v1.8.3-beta

[1.8.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.1-beta...v1.8.2-beta

[1.8.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.8.0-beta...v1.8.1-beta

[1.8.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.7.3-beta...v1.8.0-beta

[1.7.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.7.2-beta...v1.7.3-beta

[1.7.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.7.1-alpha...v1.7.2-beta

[1.7.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0-alpha...v1.7.1-alpha

[1.7.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+10...v1.7.0-alpha

[1.7.0+10]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+9...v1.7.0+10

[1.7.0+9]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+8...v1.7.0+9

[1.7.0+8]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+7...v1.7.0+8

[1.7.0+7]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+6...v1.7.0+7

[1.7.0+6]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+5...v1.7.0+6

[1.7.0+5]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+4...v1.7.0+5

[1.7.0+4]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+3...v1.7.0+4

[1.7.0+3]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+2...v1.7.0+3

[1.7.0+2]: https://github.com/refinedmods/refinedstorage/compare/v1.7.0+1...v1.7.0+2

[1.7.0+1]: https://github.com/refinedmods/refinedstorage/compare/v1.6.16...v1.7.0+1

[1.6.16]: https://github.com/refinedmods/refinedstorage/compare/v1.6.15...v1.6.16

[1.6.15]: https://github.com/refinedmods/refinedstorage/compare/v1.6.14...v1.6.15

[1.6.14]: https://github.com/refinedmods/refinedstorage/compare/v1.6.13...v1.6.14

[1.6.13]: https://github.com/refinedmods/refinedstorage/compare/v1.6.12...v1.6.13

[1.6.12]: https://github.com/refinedmods/refinedstorage/compare/v1.6.11...v1.6.12

[1.6.11]: https://github.com/refinedmods/refinedstorage/compare/v1.6.10...v1.6.11

[1.6.10]: https://github.com/refinedmods/refinedstorage/compare/v1.6.9...v1.6.10

[1.6.9]: https://github.com/refinedmods/refinedstorage/compare/v1.6.8...v1.6.9

[1.6.8]: https://github.com/refinedmods/refinedstorage/compare/v1.6.7...v1.6.8

[1.6.7]: https://github.com/refinedmods/refinedstorage/compare/v1.6.6...v1.6.7

[1.6.6]: https://github.com/refinedmods/refinedstorage/compare/v1.6.5...v1.6.6

[1.6.5]: https://github.com/refinedmods/refinedstorage/compare/v1.6.4...v1.6.5

[1.6.4]: https://github.com/refinedmods/refinedstorage/compare/v1.6.3...v1.6.4

[1.6.3]: https://github.com/refinedmods/refinedstorage/compare/v1.6.2...v1.6.3

[1.6.2]: https://github.com/refinedmods/refinedstorage/compare/v1.6.1...v1.6.2

[1.6.1]: https://github.com/refinedmods/refinedstorage/compare/v1.6.0...v1.6.1

[1.6.0]: https://github.com/refinedmods/refinedstorage/compare/v1.5.34...v1.6.0

[1.5.34]: https://github.com/refinedmods/refinedstorage/compare/v1.5.33...v1.5.34

[1.5.33]: https://github.com/refinedmods/refinedstorage/compare/v1.5.32...v1.5.33

[1.5.32]: https://github.com/refinedmods/refinedstorage/compare/v1.5.31...v1.5.32

[1.5.31]: https://github.com/refinedmods/refinedstorage/compare/v1.5.30...v1.5.31

[1.5.30]: https://github.com/refinedmods/refinedstorage/compare/v1.5.29...v1.5.30

[1.5.29]: https://github.com/refinedmods/refinedstorage/compare/v1.5.28...v1.5.29

[1.5.28]: https://github.com/refinedmods/refinedstorage/compare/v1.5.27-beta...v1.5.28

[1.5.27-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.26-beta...v1.5.27-beta

[1.5.26-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.25...v1.5.26-beta

[1.5.25]: https://github.com/refinedmods/refinedstorage/compare/v1.5.24...v1.5.25

[1.5.24]: https://github.com/refinedmods/refinedstorage/compare/v1.5.23...v1.5.24

[1.5.23]: https://github.com/refinedmods/refinedstorage/compare/v1.5.22...v1.5.23

[1.5.22]: https://github.com/refinedmods/refinedstorage/compare/v1.5.21...v1.5.22

[1.5.21]: https://github.com/refinedmods/refinedstorage/compare/v1.5.20...v1.5.21

[1.5.20]: https://github.com/refinedmods/refinedstorage/compare/v1.5.19...v1.5.20

[1.5.19]: https://github.com/refinedmods/refinedstorage/compare/v1.5.18...v1.5.19

[1.5.18]: https://github.com/refinedmods/refinedstorage/compare/v1.5.17...v1.5.18

[1.5.17]: https://github.com/refinedmods/refinedstorage/compare/v1.5.16...v1.5.17

[1.5.16]: https://github.com/refinedmods/refinedstorage/compare/v1.5.15...v1.5.16

[1.5.15]: https://github.com/refinedmods/refinedstorage/compare/v1.5.14...v1.5.15

[1.5.14]: https://github.com/refinedmods/refinedstorage/compare/v1.5.13...v1.5.14

[1.5.13]: https://github.com/refinedmods/refinedstorage/compare/v1.5.12...v1.5.13

[1.5.12]: https://github.com/refinedmods/refinedstorage/compare/v1.5.11...v1.5.12

[1.5.11]: https://github.com/refinedmods/refinedstorage/compare/v1.5.10...v1.5.11

[1.5.10]: https://github.com/refinedmods/refinedstorage/compare/v1.5.9...v1.5.10

[1.5.9]: https://github.com/refinedmods/refinedstorage/compare/v1.5.8...v1.5.9

[1.5.8]: https://github.com/refinedmods/refinedstorage/compare/v1.5.7...v1.5.8

[1.5.7]: https://github.com/refinedmods/refinedstorage/compare/v1.5.6...v1.5.7

[1.5.6]: https://github.com/refinedmods/refinedstorage/compare/v1.5.5-beta...v1.5.6

[1.5.5-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.4-beta...v1.5.5-beta

[1.5.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.3-beta...v1.5.4-beta

[1.5.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.2-beta...v1.5.3-beta

[1.5.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.1-beta...v1.5.2-beta

[1.5.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.5.0-alpha...v1.5.1-beta

[1.5.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.4.20...v1.5.0-alpha

[1.4.20]: https://github.com/refinedmods/refinedstorage/compare/v1.4.19...v1.4.20

[1.4.19]: https://github.com/refinedmods/refinedstorage/compare/v1.4.18...v1.4.19

[1.4.18]: https://github.com/refinedmods/refinedstorage/compare/v1.4.17...v1.4.18

[1.4.17]: https://github.com/refinedmods/refinedstorage/compare/v1.4.16...v1.4.17

[1.4.16]: https://github.com/refinedmods/refinedstorage/compare/v1.4.15...v1.4.16

[1.4.15]: https://github.com/refinedmods/refinedstorage/compare/v1.4.14...v1.4.15

[1.4.14]: https://github.com/refinedmods/refinedstorage/compare/v1.4.13...v1.4.14

[1.4.13]: https://github.com/refinedmods/refinedstorage/compare/v1.4.12...v1.4.13

[1.4.12]: https://github.com/refinedmods/refinedstorage/compare/v1.4.11...v1.4.12

[1.4.11]: https://github.com/refinedmods/refinedstorage/compare/v1.4.10-beta...v1.4.11

[1.4.10-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.9-beta...v1.4.10-beta

[1.4.9-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.8-beta...v1.4.9-beta

[1.4.8-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.7-beta...v1.4.8-beta

[1.4.7-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.6-beta...v1.4.7-beta

[1.4.6-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.5-beta...v1.4.6-beta

[1.4.5-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.4-beta...v1.4.5-beta

[1.4.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.3-beta...v1.4.4-beta

[1.4.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.2-beta...v1.4.3-beta

[1.4.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.1-beta...v1.4.2-beta

[1.4.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.4.0-beta...v1.4.1-beta

[1.4.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v1.3.5-alpha...v1.4.0-beta

[1.3.5-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.3.4-alpha...v1.3.5-alpha

[1.3.4-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.3.3-alpha...v1.3.4-alpha

[1.3.3-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.3.2-alpha...v1.3.3-alpha

[1.3.2-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.3.1-alpha...v1.3.2-alpha

[1.3.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.3.0-alpha...v1.3.1-alpha

[1.3.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v1.2.26...v1.3.0-alpha

[1.2.26]: https://github.com/refinedmods/refinedstorage/compare/v1.2.25...v1.2.26

[1.2.25]: https://github.com/refinedmods/refinedstorage/compare/v1.2.24...v1.2.25

[1.2.24]: https://github.com/refinedmods/refinedstorage/compare/v1.2.23...v1.2.24

[1.2.23]: https://github.com/refinedmods/refinedstorage/compare/v1.2.22...v1.2.23

[1.2.22]: https://github.com/refinedmods/refinedstorage/compare/v1.2.21...v1.2.22

[1.2.21]: https://github.com/refinedmods/refinedstorage/compare/v1.2.20...v1.2.21

[1.2.20]: https://github.com/refinedmods/refinedstorage/compare/v1.2.19...v1.2.20

[1.2.19]: https://github.com/refinedmods/refinedstorage/compare/v1.2.18...v1.2.19

[1.2.18]: https://github.com/refinedmods/refinedstorage/compare/v1.2.17...v1.2.18

[1.2.17]: https://github.com/refinedmods/refinedstorage/compare/v1.2.16...v1.2.17

[1.2.16]: https://github.com/refinedmods/refinedstorage/compare/v1.2.15...v1.2.16

[1.2.15]: https://github.com/refinedmods/refinedstorage/compare/v1.2.14...v1.2.15

[1.2.14]: https://github.com/refinedmods/refinedstorage/compare/v1.2.13...v1.2.14

[1.2.13]: https://github.com/refinedmods/refinedstorage/compare/v1.2.12...v1.2.13

[1.2.12]: https://github.com/refinedmods/refinedstorage/compare/v1.2.11...v1.2.12

[1.2.11]: https://github.com/refinedmods/refinedstorage/compare/v1.2.10...v1.2.11

[1.2.10]: https://github.com/refinedmods/refinedstorage/compare/v1.2.9...v1.2.10

[1.2.9]: https://github.com/refinedmods/refinedstorage/compare/v1.2.8...v1.2.9

[1.2.8]: https://github.com/refinedmods/refinedstorage/compare/v1.2.7...v1.2.8

[1.2.7]: https://github.com/refinedmods/refinedstorage/compare/v1.2.6...v1.2.7

[1.2.6]: https://github.com/refinedmods/refinedstorage/compare/v1.2.5...v1.2.6

[1.2.5]: https://github.com/refinedmods/refinedstorage/compare/v1.2.4...v1.2.5

[1.2.4]: https://github.com/refinedmods/refinedstorage/compare/v1.2.3...v1.2.4

[1.2.3]: https://github.com/refinedmods/refinedstorage/compare/v1.2.2...v1.2.3

[1.2.2]: https://github.com/refinedmods/refinedstorage/compare/v1.2.1...v1.2.2

[1.2.1]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0...v1.2.1

[1.2.0]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.8...v1.2.0

[1.2.0-beta.8]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.7...v1.2.0-beta.8

[1.2.0-beta.7]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.6...v1.2.0-beta.7

[1.2.0-beta.6]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.5...v1.2.0-beta.6

[1.2.0-beta.5]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.4...v1.2.0-beta.5

[1.2.0-beta.4]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.3...v1.2.0-beta.4

[1.2.0-beta.3]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.2...v1.2.0-beta.3

[1.2.0-beta.2]: https://github.com/refinedmods/refinedstorage/compare/v1.2.0-beta.1...v1.2.0-beta.2

[1.2.0-beta.1]: https://github.com/refinedmods/refinedstorage/compare/v1.1.3...v1.2.0-beta.1

[1.1.3]: https://github.com/refinedmods/refinedstorage/compare/v1.1.2...v1.1.3

[1.1.2]: https://github.com/refinedmods/refinedstorage/compare/v1.1.1...v1.1.2

[1.1.1]: https://github.com/refinedmods/refinedstorage/compare/v1.1.0...v1.1.1

[1.1.0]: https://github.com/refinedmods/refinedstorage/compare/v1.0.5...v1.1.0

[1.0.5]: https://github.com/refinedmods/refinedstorage/compare/v1.0.4...v1.0.5

[1.0.4]: https://github.com/refinedmods/refinedstorage/compare/v1.0.3...v1.0.4

[1.0.3]: https://github.com/refinedmods/refinedstorage/compare/v1.0.2...v1.0.3

[1.0.2]: https://github.com/refinedmods/refinedstorage/compare/v1.0.1...v1.0.2

[1.0.1]: https://github.com/refinedmods/refinedstorage/compare/v1.0.0...v1.0.1

[1.0.0]: https://github.com/refinedmods/refinedstorage/compare/v0.9.4-beta...v1.0.0

[0.9.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.9.3-beta...v0.9.4-beta

[0.9.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.9.2-beta...v0.9.3-beta

[0.9.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.9.1-beta...v0.9.2-beta

[0.9.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.9.0-beta...v0.9.1-beta

[0.9.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.20-beta...v0.9.0-beta

[0.8.20-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.19-beta...v0.8.20-beta

[0.8.19-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.18-beta...v0.8.19-beta

[0.8.18-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.17-beta...v0.8.18-beta

[0.8.17-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.16-beta...v0.8.17-beta

[0.8.16-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.15-beta...v0.8.16-beta

[0.8.15-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.14-beta...v0.8.15-beta

[0.8.14-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.13-beta...v0.8.14-beta

[0.8.13-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.12-beta...v0.8.13-beta

[0.8.12-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.11-beta...v0.8.12-beta

[0.8.11-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.10-beta...v0.8.11-beta

[0.8.10-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.9-beta...v0.8.10-beta

[0.8.9-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.8-beta...v0.8.9-beta

[0.8.8-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.7-beta...v0.8.8-beta

[0.8.7-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.6-beta...v0.8.7-beta

[0.8.6-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.5-beta...v0.8.6-beta

[0.8.5-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.4-beta...v0.8.5-beta

[0.8.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.3-beta...v0.8.4-beta

[0.8.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.2-beta...v0.8.3-beta

[0.8.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.1-beta...v0.8.2-beta

[0.8.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.8.0-beta...v0.8.1-beta

[0.8.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.19-beta...v0.8.0-beta

[0.7.19-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.18-beta...v0.7.19-beta

[0.7.18-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.17-beta...v0.7.18-beta

[0.7.17-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.16-beta...v0.7.17-beta

[0.7.16-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.15-beta...v0.7.16-beta

[0.7.15-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.14-beta...v0.7.15-beta

[0.7.14-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.13-beta...v0.7.14-beta

[0.7.13-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.12-beta...v0.7.13-beta

[0.7.12-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.11-beta...v0.7.12-beta

[0.7.11-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.10-beta...v0.7.11-beta

[0.7.10-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.9-beta...v0.7.10-beta

[0.7.9-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.8-beta...v0.7.9-beta

[0.7.8-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.7-beta...v0.7.8-beta

[0.7.7-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.6-beta...v0.7.7-beta

[0.7.6-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.5-beta...v0.7.6-beta

[0.7.5-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.4-beta...v0.7.5-beta

[0.7.4-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.3-beta...v0.7.4-beta

[0.7.3-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.2-beta...v0.7.3-beta

[0.7.2-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.1-beta...v0.7.2-beta

[0.7.1-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.7.0-beta...v0.7.1-beta

[0.7.0-beta]: https://github.com/refinedmods/refinedstorage/compare/v0.6.15-alpha...v0.7.0-beta

[0.6.15-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.14-alpha...v0.6.15-alpha

[0.6.14-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.13-alpha...v0.6.14-alpha

[0.6.13-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.12-alpha...v0.6.13-alpha

[0.6.12-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.11-alpha...v0.6.12-alpha

[0.6.11-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.10-alpha...v0.6.11-alpha

[0.6.10-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.9-alpha...v0.6.10-alpha

[0.6.9-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.8-alpha...v0.6.9-alpha

[0.6.8-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.7-alpha...v0.6.8-alpha

[0.6.7-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.6-alpha...v0.6.7-alpha

[0.6.6-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.5-alpha...v0.6.6-alpha

[0.6.5-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.4-alpha...v0.6.5-alpha

[0.6.4-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.3-alpha...v0.6.4-alpha

[0.6.3-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.2-alpha...v0.6.3-alpha

[0.6.2-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.1-alpha...v0.6.2-alpha

[0.6.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.6.0-alpha...v0.6.1-alpha

[0.6.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.6-alpha...v0.6.0-alpha

[0.5.6-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.5-alpha...v0.5.6-alpha

[0.5.5-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.4-alpha...v0.5.5-alpha

[0.5.4-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.3-alpha...v0.5.4-alpha

[0.5.3-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.2-alpha...v0.5.3-alpha

[0.5.2-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.1-alpha...v0.5.2-alpha

[0.5.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.5.0-alpha...v0.5.1-alpha

[0.5.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.4.1-alpha...v0.5.0-alpha

[0.4.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.4.0-alpha...v0.4.1-alpha

[0.4.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.3.0-alpha...v0.4.0-alpha

[0.3.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.2.1-alpha...v0.3.0-alpha

[0.2.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.2.0-alpha...v0.2.1-alpha

[0.2.0-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.1.1-alpha...v0.2.0-alpha

[0.1.1-alpha]: https://github.com/refinedmods/refinedstorage/compare/v0.1.0-alpha...v0.1.1-alpha

[0.1.0-alpha]: https://github.com/refinedmods/refinedstorage/releases/tag/v0.1.0-alpha
