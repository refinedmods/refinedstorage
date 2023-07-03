# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Ported to Minecraft 1.20.1.

### Fixed

- Fixed GUI side buttons not working sometimes when using Refined Storage with addons.

## [v1.11.6] - 2023-03-30

### Fixed

- Fixed not being able to search with JEI when the Grid is open.
- Fixed a bunch of issues where chunks would unintentionally be loaded by RS.
- Reduced block updates when a controller is turning on and off constantly.

## [v1.11.5] - 2023-02-12

### Fixed

- Fixed some craftable items not showing as craftable in JEI
- Fixed Grid crashing on exit if JEI mod is not used
- Fixed rare multithreading crash
- Fixed Constructor being able to drop more than the maximum stack size for an item 

## [v1.11.4] - 2022-12-20

### Fixed

- Fixed duplication bug in the Interface.

## [v1.11.3] - 2022-12-20

### Fixed

- Fixed external storage cache being de-synced from the network cache.
- Fixed external storage using an out of date block entity for getting handler.
- Fixed inventory slots being reused incorrectly in rare cases in the JEI transfer handler.

### Changed

- Increased packet size limit.

## [v1.11.2] - 2022-12-17

### Added

- Available items indicator in JEI now updates while JEI is open.

### Fixed

- Fixed chained crafters not taking over the name of the root crafter.
- Fixed lag when opening JEI in large systems.
- Made Refined Storage more robust against crashes when moving network blocks by unconventional means.

## [v1.11.1] - 2022-10-30

### Fixed

- Fixed not using Forge silicon tag for recipes.
- Small corrections to the Korean translation.

## [v1.11.0] - 2022-09-30

### Changed

- Ported to Minecraft 1.19.2.

## [v1.10.5] - 2023-02-12

### Fixed

- Fixed rare multithreading crash
- Fixed Constructor being able to drop more than the maximum stack size for an item

## [v1.10.4] - 2022-12-20

### Fixed

- Fixed external storage cache being de-synced from the network cache.
- Fixed external storage using an out of date block entity for getting handler.
- Fixed chained crafters not taking over the name of the root crafter.
- Made Refined Storage more robust against crashes when moving network blocks by unconventional means.
- Fixed duplication bug in the Interface.

### Changed

- Increased packet size limit.

## [v1.10.3] - 2022-08-06

### Fixed

- Fixed Destructor dupe bug
- Fixed being unable to insert items into the Storage Monitor
- Fixed Storage Monitor not showing all matching items in non-exact mode
- Fixed items getting lost on Creative Disk when more than 2,147,483,647 of one type is stored
- Fixed incorrect autocrafting keybind prompt on macOS
- Fixed crashing the game when a network block was removed in a bad way
- Fixed duplication bug with Constructors and Shulker Boxes
- Fixed breaking blocks with pickaxe taking too much time

### Changed

- Improved performance for grid updates
- Improved waterlogging to behave more like vanilla
- JEI version 9.7.1 or higher is now required

### Added

- Forge relocation and Packing Tape blacklist tag entries to prevent block movement
- Japanese translations for certain autocrafting actions

## [v1.10.2] - 2022-03-26

### Changed

- Ported to Minecraft 1.18.2.

## [v1.10.1] - 2022-03-26

### Fixed

- Fixed crash when opening alternatives screen.

## [v1.10.0] - 2022-01-25

### Fixed

- Fixed multiple bugs related to transferring recipes into the Crafting Grid.
- Processing patterns now use the order of items/fluids specified in the pattern
  by [@necauqua](https://github.com/necauqua) and [@Darkere](https://github.com/Darkere).
- Fixed autocrafting task getting stuck if two tasks fulfilled each others requirements.
- Fixed fluid autocrafting breaking when using 2 stacks of the same fluid in a pattern.
- Amount specifying screen is now limited to valid values.
- Fixed crash on servers when starting with latest Forge.

## [v1.10.0-beta.4] - 2021-12-28

### Fixed

- Fixed client crash when hovering over a fluid in the Fluid Grid by [@jackodsteel](https://github.com/jackodsteel).
- Fixed random client crashes when starting the game.

### Changed

- Update Korean translation by [@mindy15963](https://github.com/mindy15963).

## [v1.10.0-beta.3] - 2021-12-17

### Fixed

- Fixed networks and network devices being removed when a chunk unloads.

## [v1.10.0-beta.2] - 2021-12-16

### Fixed

- Fixed all Refined Storage advancements being granted when joining a world.
- Fixed potential Pattern crash when loading Minecraft.

## [v1.10.0-beta.1] - 2021-12-15

### Fixed

- Fixed Relay not working.
- Fixed Wireless Transmitter only working upright.
- Fixed Portable Grid not opening when pointing at a block.
- Fixed being able to circumvent locked slots by scrolling.

### Changed

- Added more slots to the Pattern Grid.
- Combined fluid and item view in the Pattern Grid.
- Ported to Minecraft 1.18.1.
- Focused side buttons now display their tooltip properly.
- Improved performance of retrieving patterns by [@metalshark](https://github.com/metalshark).

## [v1.9.18] - 2022-05-18

### Fixed

- Fixed potential Pattern crash when loading Minecraft.

## [v1.9.17] - 2022-01-30

### Added

- More slots for the Pattern Grid.

### Changed

- Combined fluid and item view in the Pattern Grid.
- Processing patterns now use the order of items/fluids specified in the pattern.
- Amount specifying screen is now limited to valid values.

### Fixed

- Fixed Relay not working.
- Fixed Wireless Transmitter only working upright.
- Fixed Portable Grid not opening when pointing at a block.
- Fixed being able to circumvent locked slots by scrolling.
- Fixed multiple bugs related to transferring recipes into the crafting grid.
- Fixed autocrafting task getting stuck if two tasks fulfilled each others requirements.
- Fixed fluid autocrafting breaking when using 2 stacks of the same fluid in a pattern.

## [v1.9.16] - 2021-11-16

### Added

- Added Covers for all cable types.
- Added Polish translation.
- Added Italian translation.
- Addons can now override how crafters insert items.

### Changed

- Improved JEI integration to pick the best option when transferring items.

### Fixed

- Fixed an issue where too many items in a grid would kick the player.
- Fixed an issue where the portable grid does not open from Inventory anymore after some use.
- Fixed craftable view in grids not showing items that were already in storage.
- Fixed Wireless Crafting Monitor not working as Curio.
- Fixed wrong slot being locked in some cases when opening a wireless item.
- Slightly sped up External Storage item look up.
- Fixed extraction from Storage Monitor not respecting maximum stack size.

## [v1.9.15] - 2021-07-25

### Fixed

- Fixed Refined Storage Addons compatibility.

## [v1.9.14] - 2021-07-25

### Added

- Implemented Curios support.

## [v1.9.13] - 2021-06-14

### Added

- Added some performance improvements for autocrafting.

### Fixed

- Fixed count on Storage Monitor having Z fighting.
- Fixed items on Storage Monitor not being flat.
- Fixed crash when using an External Storage on a fluid inventory.
- Fixed a memory leak in the pattern cache.
- Fixed Detector crashing when dyed.
- Fixed autocrafting being stuck after clicking "Start".
- Fixed Crafting Monitor not being able to show hours.
- Fixed capacity rendering of infinite storages.
- Fixed wrong alignment for the JEI request autocrafting tooltip.
- Fixed mobs getting stuck in Refined Storage cables.
- Fixed dismantling storage blocks ignoring stack size.
- Fixed Ice and Fire banners breaking with Refined Storage.
- Fixed empty keybinding causing GL errors.
- Fixed some parts of the Japanese translation.
- Fixed rendering issue on blocks when using OptiFine.

### Removed

- Removed experimental pipeline nagging message.

## [v1.9.12] - 2021-02-07

### Fixed

- Fixed some issues when using the Grid when it's offline.
- Fixed crafting events not being fired in some cases in the Grid.
- Fixed not being able to set fluid filter slot output quantity.
- Fixed mod id search not working for Industrial Foregoing.
- Fixed fluid autocrafting duplicating fluids.
- Fixed some Grid crashes.
- Fixed constructor not using compare mode correctly in some cases.
- Fixed duplication bug in the Interface.

## [v1.9.11] - 2021-01-03

### Fixed

- Fixed disks and network devices not loading when they did not previously exist
  - If you are affected by this please go to the world/data/ folder and remove the ".temp" ending from the files
    before
    launching.

## [v1.9.10] - 2021-01-02

### Changed

- Update Japanese translation.

### Fixed

- Improve performance of the Grid view.
- Fixed Disk Manipulator model glitches.
- Improve performance of the Disk Manipulator.
- Fixed being unable to set quantity in output slots of the Pattern Grid.
- Fixed External Storage in fluid mode losing track of fluids sometimes.
- Added code to avoid / minimize data corruption issues caused by Minecraft.
- Fixed processing autocrafting orders stealing items from each other.
- Fixed Constructor in fluid mode voiding fluid source blocks in front of it.
- Fixed crash when recoloring blocks that have no rotation component.
- Fixed reloading resource packs breaking Refined Storage textures.

## [v1.9.9] - 2020-11-14

### Fixed

- Fixed Refined Storage sidebuttons displaying over the JEI bookmark pagination buttons.
- Fixed issue where Crafters may fail to recognize an inventory/tank for some patterns.
- Fixed issue where the Crafter Manager can crash on invalid patterns.
- Fixed issue where alternatives in the Pattern Grid weren't being saved properly.
- Fixed not being able to change the Exporter filter slot count with regulator mode without closing and re-opening the
  container.

## [v1.9.8] - 2020-10-24

### Added

- Added a JEI synchronized (two-way) search box mode to the Grid.
- Added a nag message when a player joins the world that asks the player to enable the experimental Forge lighting
  pipeline to ensure correct rendering.

### Fixed

- Fixed server crash when scrolling in Grid.
- Fixed various issues with Grid interactions working without power.
- Fixed changing rotation not updating blocks.

## [v1.9.7] - 2020-10-04

### Added

- Added functionality to move items in the Grid with shift/ctrl + scrolling.

### Changed

- Changed JEI transfer error mechanics.

### Fixed

- Fixed crash when opening Controller GUI.
- Fixed dye being consumed without effect in some cases.
- Fixed deadlock caused by Portable Grid.
- Fixed custom tooltips not working in the Grid.

## [v1.9.6] - 2020-09-25

### Added

- Port to Minecraft 1.16.3.
- Added colored block variants.
- Added functionality to show missing items in the JEI transfer screen.
- Added functionality to request missing items from autocrafting in the JEI transfer screen.
- Added client config option to remember the Grid search query.

### Fixed

- Fixed Portable Grid losing enchantments when placing and breaking.

## [v1.9.5] - 2020-09-06

### Added

- Re-added the `/refinedstorage disk create <player> <id>` command.
- Added the `/refinedstorage disk list` command.
- Added the `/refinedstorage disk list <player>` command.
- Added the `/refinedstorage network list <dimension>` command.
- Added the `/refinedstorage network get <dimension> <pos>` command.
- Added the `/refinedstorage network get <dimension> <pos> autocrafting list` command.
- Added the `/refinedstorage network get <dimension> <pos> autocrafting get <id>` command.
- Added the `/refinedstorage network get <dimension> <pos> autocrafting cancel` command.
- Added the `/refinedstorage network get <dimension> <pos> autocrafting cancel <id>` command.
- Added JEI ghost ingredient dragging support.

### Fixed

- Fixed text field not being focused in amount specifying screens.

## [v1.9.4] - 2020-08-30

### Fixed

- Fixed JEI recipes/usages keys not working in Grids.

## [v1.9.3-beta] - 2020-08-24

### Added

- Port to Minecraft 1.16.2.

### Changed

- Updated Japanese translation.
- Updated Taiwanese translation.
- Refactored autocrafting code.

### Fixed

- Fixed duplication bug with the Constructor.

## [v1.9.2b-beta] - 2020-09-11

### Fixed

- Fixed duplication bug with the Constructor.

## [v1.9.2-beta] - 2020-07-17

### Added

- Re-added interdimensional networks with the Network Transmitter and Network Receiver.
- Re-added MouseTweaks integration.

### Changed

- Networks that are in a chunk that isn't loaded will no longer work, they will turn off. Chunkload the Controller to
  maintain a functioning network over long distances.

### Fixed

- Fixed crash with Forge version 67.
- Fixed cases where Refined Storage unwillingly acts like a chunkloader.
- Fixed Network Transmitters being able to connect to any network device.
- Fixed Crafting Tweaks buttons being in the wrong position after changing the size configuration of the Grid.

## [v1.9.1-beta] - 2020-07-14

### Fixed

- Fixed server crash.

## [v1.9.0-beta] - 2020-07-14

### Added

- Port to Minecraft 1.16.

### Fixed

- Fixed wrench requiring shift click to rotate blocks.

## [v1.8.8] - 2020-07-13

### Fixed

- Fixed duplication bug and weird behavior in the Crafting Grid matrix.

## [v1.8.7] - 2020-07-11

### Fixed

- Fixed Regulator mode item and fluid counts not saving properly.
- Fixed Wireless Crafting Monitor not closing properly.
- Fixed Controller always using energy, even when disabled with redstone.
- Fixed internal crafting inventory not being returned when Controller is broken.
- Fixed bug where autocrafting tasks started on the same tick make the wrong assumption about available items and
  fluids.
- Fixed bug where the "To craft" amount in the Crafting Preview window is wrong.
- Fixed bug where non-pattern items are able to be inserted into the Crafter Manager (Darkere)
- Fixed performance issue where shapes of cable blocks were constantly being recalculated.

### Changed

- Drastically improved shift clicking performance in Crafting Grid.

### Removed

- Removed autocrafting engine version from crafting preview screen.

## [v1.8.6-beta] - 2020-06-26

### Fixed

- Fixed Constructor duplication bug.

## [v1.8.5-beta] - 2020-06-18

### Added

- Re-added all the language files.
- Japanese translations.

### Fixed

- Fixed Portable Grid voiding the disk when extracting with full inventory.
- Fixed Constructor extracting 2 buckets when placing fluid.
- Fixed Stack Overflow error with regulator upgrades.
- Fixed visual bug with the Detector not updating its values.
- Fixed Constructor placing the filtered item instead of the extracted.
- Fixed duplication bug with filter slots.
- Fixed shift crafting in a Grid not using the player.
- Fixed bug where shift clicking gives too many items.

### Changed

- Cancelling a crafting task now also unlocks all Crafters related to that task.
- External Storage will now always show the exact maximum capacity as reported by the attached inventory.
- Crafters no longer expose their inventory to the side they are facing.
- Changed package name to `com.refinedmods.refinedstorage`, this is a breaking change for addons.

## [v1.8.4-beta] - 2020-05-26

### Fixed

- Fixed autocrafting Crafting Monitor crash.

## [v1.8.3-beta] - 2020-04-29

### Added

- A new experimental autocrafting engine that's enabled by default. This should improve autocrafting performance.
- The Regulator Upgrade that can be inserted into a Exporter. This ensures a certain amount of items and fluids is
  kept in stock in a connected inventory.
- Debug logging on the server when an expensive operation occurs.

### Fixed

- Fixed Exporter not exporting anything when using a Stack Upgrade and there isn't space for 64 items in the inventory.
- Fixed Controller always using the base usage even when turned off.
- Fixed severe memory leak in the storage cache.

### Changed

- Wireless Transmitters can now be placed on any block and in any direction.

## [v1.8.2-beta] - 2020-04-25

### Added

- Refined Storage silicon is now present in `forge:silicon` tag for mod compatibility.
- Waterlogging to all cable blocks.
- Create zh_tw translation.
- Re-added zh_cn translation.

### Fixed

- Fixed storage block dropping extra processor.

### Changed

- Updated pt_br translation.

## [v1.8.1-beta] - 2020-01-30

### Added

- Port to Minecraft 1.15.2.
- Fluid support for the Storage Monitor.

## [v1.8.0-beta] - 2020-01-21

### Added

- Port to Minecraft 1.15.

## [v1.7.3-beta] - 2019-12-30

### Fixed

- Fixed severe energy update lag introduced by version 1.7.2.

## [v1.7.2-beta] - 2019-12-29

### Added

- Resource packs can now define the font colors that Refined Storage GUIs need to use.

### Fixed

- Fixed crash when loading a network.
- Fixed being able to drain energy from the Refined Storage Controller.
- Fixed the Grid crashing on a item/fluid update-heavy storage system.
- Fixed the Grid displaying the old quantity when shift clicking an entire stack out.
- Fixed crash with the Disk Manipulator and using item/fluid filters when inserting into the network.
- Fixed the network being able to run off 1 FE/t.

### Changed

- Patterns being added or removed from the network are now propagated as well to clients that are watching a Grid.
- When pressing ESCAPE in the search box on the Grid or Crafter Manager, focus on the search bar will be lost first
  before closing the GUI immediately. Then on the next ESCAPE press, the GUI will be closed.

## [v1.7.1-alpha] - 2019-11-19

### Fixed

- Fixed Pattern Grid causing world hanging on load.
- Fixed External Storage not refreshing when the storage is broken or replaced.
- Fixed delay in block update when placing a cable block.
- Fixed holder of cable blocks sometimes conflicting with a cable connection while rendering.
- Fixed being able to move wireless items in inventory when using a keybinding to open.
- Fixed crash when breaking a Grid, Crafting Monitor, Crafter Manager or Portable Grid when another player is still
  using it.

### Changed

- The Pattern Grid now switches automatically between crafting pattern and processing pattern mode when using JEI
  transfer.
- The Pattern Grid now switches automatically between crafting pattern and processing pattern mode when re-inserting an
  existing Pattern.
- Grids now do not sort if you interact with it while holding shift.

### Removed

- Exact mode for processing patterns no longer exist, you can now define per-slot which item/fluid tags are allowed to
  be used by autocrafting, by using CTRL + click on the filter slot in the Pattern Grid.
- Removed migration code for the development builds that were released on Discord (not on CurseForge). If you used the
  development builds and never used version 1.7.0 before, first switch to 1.7.0, open your world, modify a storage disk,
  and then upgrade to 1.7.1.

## [v1.7.0-alpha] - 2019-11-02

### Added

- Port to Minecraft 1.14.

### Fixed

- Fixed the Fluid Grid not having a View type setting.

### Changed

- Oredict mode for Patterns has been replaced with "Exact mode" (by default on). When exact mode is off, Refined Storage
  will use equivalent items or fluids from the Minecraft item/fluid tag system.
- Grid filtering with "$" now does filtering based on item/fluid tag name instead of oredict name.
- When binding a network item to a network you can now bind to any network block, not only the Controller.

### Removed

- The Reader and Writer, this will return later in an addon mod.
- Cross dimensional functionality on the Network Transmitter for the moment, this will return later.
- Covers.

## [v1.7.0+10] - 2019-10-29

### Added

- Re-added oredict mode as "exact mode" (for fluids too!).
- Re-added the Crafter Manager.
- Re-added the Crafting Monitor.
- Re-added the Wireless Crafting Monitor.

### Fixed

- Fixed the ugly checkboxes
- Misc bugfixes and crash fixes.

## [v1.7.0+9] - 2019-10-28

### Added

- Re-add the Crafter and autocrafting.

### Fixed

- Misc bugfixes and crash fixes.

## [v1.7.0+8] - 2019-10-27

### Added

- Re-added the Constructor.
- Re-added the Destructor.
- Re-added the Disk Manipulator.
- Re-added the Portable Grid.

## [v1.7.0+7] - 2019-10-22

### Fixed

- Fixed a crash that can happen when opening a world.

## [v1.7.0+6] - 2019-10-22

### Added

- Re-added the Network Transmitter (not cross dimensional yet).
- Re-added the Network Receiver.
- Re-added the Relay.
- Re-added the Detector.
- Re-added the Security Manager.
- Re-added the Interface.
- Re-added the Fluid Interface.
- Re-added the Wireless Transmitter.
- Re-added the Storage Monitor.
- Re-added the Wireless Grid.
- Re-added the Wireless Fluid Grid.

### Fixed

- Misc bugfixes and crash fixes.

## [v1.7.0+5] - 2019-10-17

### Added

- Re-added the External Storage.
- Re-added the Importer.
- Re-added the Exporter.

### Removed

- Cutting Tool (you may get a Forge warning about that one, it's safe to ignore).
- The "compare nbt" side button, replaced it with "exact mode".

## [v1.7.0+4] - 2019-10-15

### Added

- Re-added all the storage blocks.
- Re-added JEI integration.

### Fixed

- Misc bugfixes and crash fixes.

## [v1.7.0+3] - 2019-10-12

### Added

- Re-added the Crafting Grid.
- Re-added the Pattern Grid.
- Re-added the Fluid Grid.
- Re-added Optifine compatibility.

## [v1.7.0+2] - 2019-10-10

### Added

- More config values.

### Fixed

- Misc bugfixes and crash fixes.

### Removed

- Free dirt every 10 ticks.

## [v1.7.0+1] - 2019-10-09

### Added

- Re-added the Controller.
- Re-added the Disk Drive.
- Re-added the Grid.

## [v1.6.16] - 2020-04-26

### Fixed

- Fixed erroring controller tile entity.
- Fixed Inventory Tweaks sorting not respecting locked slots.
- Fixed OpenComputers driver voiding excess fluids.
- Fixed being able to move wireless items in inventory.

### Changed

- Updated Russian translation.

## [v1.6.15] - 2019-07-21

### Fixed

- Fixed recipes with more than 1 bucket of fluid not transferring from JEI.
- Fixed oredict crafting patterns redefining recipes.
- Fixed Portable Grids not keeping their enchantments when placed.
- Fixed JEI hotkeys not working on fluid filter slots.
- Fixed crash when opening Crafter Manager with FTB Quests installed.
- Fixed a bug where the container slots weren't synced when opening a Grid.

### Changed

- Shortened crafting text for the Russion translation to fix Grid overlays.
- GregTech Community Edition Wires and Machines are now banned from rendering on Refined Storage patterns because they are causing crashes.

## [v1.6.14] - 2019-03-23

### Fixed

- Fixed server crash

## [v1.6.13] - 2019-03-23

### Added

- Added keybindings to open wireless items. The default one set to open a Wireless Crafting Grid from Refined Storage Addons is CTRL + G.
- Added Grid quantity formatting for item counts over 1 billion.

### Changed

- Updated German translation.
- Updated Chinese translation.
- The Constructor and Destructor now interacts with the world using their owner's profile.

### Fixed

- Fixed Interface with Crafting Upgrade being stuck if an earlier item configuration has missing items or fluids.
- Fixed wrong item count for oredict patterns.
- Fixed autocrafting duplication bug.
- Fixed Crafting Pattern not rendering tile entity items like a chest.

## [v1.6.12] - 2018-11-28

### Added

- Added a completion percentage to the Crafting Monitor.

### Changed

- Updated Russian translation.
- Increased the speed of autocrafting.

### Fixed

- Fixed External Storage sending storage updates when it is disabled.
- Fixed slight performance issue with loading Crafters from disk.
- Fixed storage GUIs overflowing on large numbers.

## [v1.6.11] - 2018-11-24

### Fixed

- Fixed blocks neighboring a controller breaking when returning from a dimension in a unchunkloaded area.

## [v1.6.10] - 2018-11-23

### Added

- Added fluid functions for the fluid autocrafting to the OpenComputers integration.

### Changed

- Updated Russian translation.
- Slightly increased performance of the External Storage.

### Fixed

- Fixed client FPS stalling when using "@" mod search in the Grid.
- Fixed client FPS stalling when using "#" tooltip search in the Grid.
- Fixed fluid inputs/outputs in the Pattern Grid not being set when you re-insert a Pattern with fluid inputs/outputs.
- Fixed bug where the Pattern Grid doesn't update it's output slot when manually configuring a crafting pattern.
- Fixed network node scanning allowing multiple controllers in some cases.
- Fixed OpenComputers integration not giving back a crafting task instance in the schedule task API.
- Fixed OpenComputers integration causing log spam when getting processing patterns.
- Fixed OpenComputers voiding items with extract item API when there is no inventory space.
- Fixed CraftingTweaks buttons resetting sometimes in the Crafting Grid.
- Fixed Refined Storage jars not being signed.
- Fixed crafting task stalling when there's not enough space in the inventory.
- Fixed another duplication bug with a disconnected Crafting Grid.
- Fixed oredict mode in autocrafting not working at all.

### Removed

- Removed getMissingItem.
- Removed the Interdimensional Upgrade, Network Transmitters are now cross dimensional by default.
- Removed the per block FE cost of the Network Transmitter, it draws a fixed amount of FE/t now.

## [v1.6.9] - 2018-10-27

### Changed

- You can now interact with the fluid container input slot in the Fluid Interface.

### Fixed

- Fixed OpenComputers "unknown error" when using extract item API.
- Fixed client FPS stuttering when opening a Crafting Grid.
- Fixed rare Grid crashing issue.

## [v1.6.8] - 2018-10-20

### Fixed

- Fixed Ender IO incompatibility.

## [v1.6.7] - 2018-10-19

### Changed

- The Processor Binding recipe now only gives 8 items instead of 16.

### Fixed

- Fixed the Raw Processor recipes not taking oredicted silicon.
- Fixed the Processor Binding recipe not taking oredicted slimeballs.

## [v1.6.6] - 2018-10-18

### Added

- Added new Crafter modes: ignore redstone signal, redstone signal unlocks autocrafting, redstone signal locks autocrafting and redstone pulse inserts next set.
- Added a config option to configure the autocrafting calculation timeout in milliseconds.
- Added throttling for network devices that can request autocrafting.

### Changed

- Renamed Cut Processors to Raw Processors and those are now made with Processor Binding instead of a Cutting Tool.
- You can no longer start a crafting task if it has missing items or fluids.
- The Security Manager now supports Security Cards that have no player assigned to them. It is the default security card for players that aren't configured.
- If no default Security Card is configured in the Security Manager, an unconfigured player is allowed to do everything in the network. Create a default Security Card.

### Fixed

- Fixed an autocrafting bug where it crashed when external inventories couldn't be filled.
- Fixed a duplication bug with a disconnected Crafting Grid.
- Fixed oredict autocrafting sometimes reporting that a craftable item is missing.
- Fixed fluid autocrafting without item inputs locking when there's not enough space for the fluids.
- Fixed Grid "last changed" date not changing when using clear button or JEI transfer.
- Fixed a duplication bug when pressing clear on a Wireless Crafting Grid from Refined Storage Addons.
- Fixed a duplication bug with autocrafting and External Storages.
- Fixed Crafting Manager displaying wrong name for chained crafters connected to some blocks.
- Fixed crafting task losing internal buffer when network runs out of energy.

### Removed

- Removed handling of reusable items in autocrafting, to avoid problems.

## [v1.6.5] - 2018-09-11

### Changed

- The Pattern Grid in fluid mode now supports up to 64 buckets in the input and output processing slots.

### Fixed

- Fixed Refined Storage silicon's oredict entry being registered too late.
- Fixed duplication bug with filter slots.

## [v1.6.4] - 2018-09-02

### Changed

- Rewrote autocrafting again, bringing performance up to par with other autocrafting mods.
- Autocrafting now reserves items and fluids in an internal inventory to avoid having the storage network steal stacks required for autocrafting.
- Reworked the Crafting Monitor to be more condensed and more clear.

### Fixed

- Fixed not being able to craft upgrades that require enchanted books.
- Fixed quick jittering of the Grid and Crafting Monitor when opening them because the tabs appear.

### Removed

- Removed left / right click functionality on filter slots to increase / decrease the amount, replaced that functionality with a dialog.

## [v1.6.3] - 2018-08-02

### Added

- Re-added a single mode Wrench that can rotate blocks and break Refined Storage covers.

### Fixed

- Fixed crash with Wireless Fluid Grid.
- Fixed Reborn Storage crafting being slower than normal.

## [v1.6.2] - 2018-07-30

### Fixed

- Fixed Grid searching not working.

## [v1.6.1] - 2018-07-30

### Added

- Added fluid autocrafting.
- Added Crafting Upgrade support for fluids on the Exporter, Constructor and Fluid Interface.
- Added config option to hide covers in the creative mode tabs and JEI.

### Changed

- The Portable Grid now supports fluid disks.
- Filters now support fluids and can be inserted in the Fluid Grid.
- You can now keep fluids in stock by attaching a External Storage in fluid mode to a Fluid Interface with a Crafting Upgrade.
- You can now specify the amount to export in the Fluid Interface.
- Updated Russian translation.
- Overhauled and updated German translation.
- The Crafting Upgrade no longer schedules requests when there are items or fluids missing.
- Made the Crafting Preview window bigger.

### Fixed

- Fixed crash log when opening Pattern Grid GUI.
- Fixed being able to put non fluid containers in Fluid Interface input slot.
- Fixed Grid filters not updating Grid.

### Removed

- Removed "emit signal when item is being autocrafted" option in the Detector.

## [v1.6.0] - 2018-07-20

### Added

- Added the Cutting Tool.
- Added covers.
- Added new storage disk system where the storage disk data (items, fluids) are stored off the disk itself, in another file (refinedstorage_disks.dat). The disk itself only stores its ID.
- Added /createdisk command which creates a disk based on the disk ID. Turn on advanced tooltips to see the disk ID on a disk item.
- Added config option to configure controller max receive rate.
- Added config option to configure energy capacity of Refined Storage items.
- Added config option to change Reader / Writer channel energy capacity.
- Added a fully charged regular Controller to the creative menu.
- Added a missing config option for Crafter Manager energy usage.
- Added support for Disk Drive / Storage Block storage and capacity to OC integration.
- Added "Search box mode" button to the Crafter Manager.

### Changed

- Renamed "Printed Processors" to "Cut Processors".
- Rewrote autocrafting.
- Rewrote network energy storage.
- The Controller item now shows a durability bar for the energy.
- You can no longer put a Filter in filter slots to gain additional filter slots.
- You can now re-insert Processing Patterns in the Pattern Grid and have the inputs and outputs be completed.
- If an Interface is configured to expose the entire network storage (by configuring no export slots), it will no longer expose the entire RS storage, due to performance issues.
- The Portable Grid no longer exposes a inventory for crossmod interaction, due to performance issues.
- The Crafting Monitor is now resizable and its size can be configured (stretched, small, medium, large).
- The Crafting Monitor now splits its tasks over tabs.
- An empty blacklist now means: accept any item. An empty whitelist now means: don't accept any item (an empty whitelist USED to mean: accept any item).
- The Importer now skips over empty slots.
- The Exporter now round-robins over every configured item or fluid to export instead of exporting them all at once.
- Updated Russian translation.
- Autocrafting tasks that take longer than 5 seconds to CALCULATE (NOT execute) are automatically stopped to avoid server strain.
- Changed fluid storage progression to be 64k - 256k - 1024k - 4096k.
- Made all IO blocks have a blacklist instead of a whitelist by default.

### Fixed

- Fixed bug where pattern was recipe pattern was creatable when there was no recipe output.
- Fixed a crash when breaking an Ender IO conduit with the Destructor.
- Fixed bug where storage disks in Portable Grids could be moved into themselves.
- Fixed the Crafter crashing when opening it while connected to a Primal Tech Grill or Kiln.
- Fixed bug where Crafting Upgrade on Interface kept too many items in stock.
- Fixed bug where External Storage could only handle 1 fluid inventory per block.
- Fixed shift clicking a created pattern going into Grid inventory.
- Fixed crash when moving a wireless item with the number keys.
- Fixed bug where item storage tracker didn't save sometimes.
- Fixed bug where External Storage doesn't detect new inventory when rotating.
- Fixed JEI recipe transferring in Pattern Grid allowing non-processing recipes in processing mode and vice-versa.
- Fixed using Interfaces for minimum stock levels failing when requester is also an Interface.
- Fixed ItemZoom incompatibility in Grid and crafting preview window.
- Fixed shift clicking upgrades into Interface making upgrades go to import slots.
- Fixed duplication glitch with storages.
- Prevent accidental Grid scrollbar click after clicking JEI recipe transfer button.

### Removed

- Removed Regulator mode in the Exporter.
- Removed MCMultiPart integration.
- Removed Project E integration.
- Removed blocking mode in autocrafting.
- Removed the Wrench.
- Removed "void excess items or fluids" functionality on storages.
- Removed the Solderer.
- Removed "compare oredict" buttons on Exporter, Importer, etc.
- Removed ConnectedTexturesMod integration for fullbright textures, RS now has fullbright textures natively.
- Removed autocrafting with fluids (the bucket filling mechanic). This will be replaced in a later version with native fluid autocrafting, where Crafters can insert fluids to external inventories.

## [v1.5.34] - 2018-05-22

### Added

- Added OR search operator to the Grid with "|".
- Added new `getPattern(stack:table)` function for OpenComputers integration.

### Changed

- Empty patterns can no longer be inserted in the pattern result slot in the Pattern Grid with hoppers.
- `getPatterns()` now only returns all the outputs, this to limit memory usage in OpenComputers (only affects OC integration).
- Allow crafters to be daisy-chained.

### Fixed

- Fixed repeated key events not getting handled in some cases.

## [v1.5.33] - 2018-04-22

### Added

- Added Crafter Manager.

### Changed

- Patterns in the Crafter slots now automatically render the output without pressing shift.
- Increased Grid performance.
- Various internal refactors.

### Fixed

- Fixed Disk Manipulator not extracting items.
- Fixed filter slots not caring about max stack size.
- Fixed model warning about Portable Grid.
- Fixed crash when autocompleting Ender IO recipes from JEI.
- Fixed Grid not always using all combinations when using JEI autocompletion.

## [v1.5.32] - 2018-03-08

### Added

- Added Spanish translation.

### Changed

- Changed stack quantity of craftable items from 1 to 0 to fix Quantity Sorting.
- Changed fluid stack amount to not display "0" anymore.
- Disk Manipulator in fluid mode will now extract a bucket at a time instead of 1 mB (or 64 buckets at a time with a Stack Upgrade instead of 64 mB).

### Fixed

- Fixed issue where the Pattern Grid can only overwrite patterns when blank ones are present.
- Fixed not being able to extract half a stack of items with max stack size 1 in Grid when using right click.
- Fixed 2 same stacks using capabilities without NBT tag not treated equal.
- Fixed NBT/metadata check on exporting in an Interface.
- Fixed Disk Manipulator being stuck on unemptiable, non-empty disks.
- Fixed orientations of the Portable Grid.
- Fixed crafting event in Crafting Grid being fired twice.
- Fixed a crash when the Constructor tries to place a block when a multipart is attached to it.
- Fixed an autocrafting crash.
- Attempted to fix FPS drop on Grid sorting.

## [v1.5.31] - 2017-12-31

### Changed

- Storage disk and block stored and capacity counts are formatted now in the tooltip.
- Improved the "cannot craft! loop in processing..." error message.
- Made the Disk Manipulator unsided (inserting goes to insert slots and extracting from output slots).

### Fixed

- Fixed error logs when toggling the Pattern Grid from and to processing mode.
- Fixed pattern slots in Crafters not being accessible.
- Fixed rare Grid crash.
- Fixed OpenComputers cable showing up in Grid as air.

## [v1.5.30] - 2017-12-24

### Fixed

- Fixed crashing bug when MCMultiPart is not installed.

## [v1.5.29] - 2017-12-23

### Changed

- Update Forge to 2577 (minimum Forge version required is now 2555 for MC 1.12.2).

### Fixed

- Fixed bug where MCMP multiparts were blocking RS network connections.
- Fixed Reader/Writers for energy extracting energy when not needed.

## [v1.5.28] - 2017-12-13

### Changed

- Item Reader/Writers can now store 16 stacks.
- Fluid Reader/Writers can now store 16 buckets.
- Energy Reader/Writers can now store 16000 FE.

### Fixed

- Fixed Writers not pushing energy.

## [v1.5.27-beta] - 2017-12-09

### Fixed

- Fixed non-oredict patterns not consuming resources.

## [v1.5.26-beta] - 2017-12-09

### Added

- Added Funky Locomotion integration.

### Fixed

- Fixed Exporter in Regulator Mode not regulating properly when same item is specified multiple times.
- Fixed air appearing in Grid.
- Fixed config categories not correctly appearing in ingame config GUI.
- Fixed craftable items showing "1 total" if not stored in system in Grid.
- Minor fixes to autocrafting.

### Removed

- Removed "detailed" Grid view type variant, made detailed tooltips a config option instead.

## [v1.5.25] - 2017-11-28

### Fixed

- Fixed not being able to autocraft different Storage Drawers' wood drawers.
- Fixed not being able to autocraft certain Modular Routers items.
- Fixed last modified date not being sent when extracting from an External Storage.

## [v1.5.24] - 2017-11-26

### Added

- Added "Last modified" sorting option in the Grid.
- Added a "detailed" variant for every Grid view type option, to disable the modified information on the tooltip.

### Changed

- The Grid now displays last modified information (player name and date) and size on tooltips of stacks.

### Fixed

- Fixed Exporter with Stack Upgrade not working correctly in Regulator Mode.
- Fixed crash with the Constructor.
- Fixed patterns being able to crash when no inputs are provided.
- Fixed possible crash with network scanning.

### Removed

- Removed craft-only mode for the Exporter.

## [v1.5.23] - 2017-11-13

### Fixed

- Fixed duplication bug with autocrafting.
- Fixed Fluid Interface with Stack Upgrade not exporting fluids.
- Fixed fluids in Fluid Grid not showing actual mB on tooltip when pressing CTRL + SHIFT.

## [v1.5.22] - 2017-11-11

### Added

- Added oredict, blocking, processing, ore inputs access to OpenComputers API.
- Added shortcut to clear Grid crafting matrix (CTRL+X).

### Changed

- The Crafter can now only store 1 stack size pattern per slot.
- You can now re-insert a Pattern in the pattern output slot in the Pattern Grid to modify an existing pattern.
- The Refined Storage jar is now signed.
- Updated Chinese translation.

### Fixed

- Fixed not being able to use JEI R and U keys on Grid with tabs.
- Fixed lag when opening a Grid with lots of items by offloading the grid sorting to another thread.
- Performance improvement when adding patterns to the network.

## [v1.5.21] - 2017-10-19

### Changed

- Updated Portuguese (Brazilian) translation.

### Fixed

- Fixed crash with External Storage.
- Fixed stack-crafting in the crafting grid (crafting table) causing lag on a dedicated server.
- Fixed cable blocks, Wireless Transmitter, Detector and Portable Grid acting as full blocks (being able to place torches on them etc).

## [v1.5.20] - 2017-10-09

### Fixed

- Restore MC 1.12.0 compatibility.

## [v1.5.19] - 2017-10-08

### Changed

- Updated Forge to 2493 (MC 1.12.2).

### Fixed

- Fixed Refined Storage blocks requiring a pickaxe to be broken.
- Fixed Grid GUI crash.
- Fixed device names overflowing Controller GUI.
- Fixed high CPU load when Refined Storage GUIs are open.
- Fixed not being able to extract Mekanism tanks and bins from the Grid.
- Fixed not being able to craft Immersive Engineering Revolver.
- Fixed rare bug when server crashes on startup due to network node not existing.

## [v1.5.18] - 2017-09-08

### Added

- Added Project E integration for the External Storage on the Transmutation Table.
- Added Project E integration for the energy values of Solderer items.
- Added support for more than 4 grid tabs in the Grid by putting filters IN filters.
- Added protection for other mods causing crashes when drawing an item or display name.

### Changed

- Reader and Writer blocks now face the block you're placing it on, not the player.
- Pressing SHIFT over an item in the Grid will no longer display the full unformatted count, instead, use CTRL + SHIFT and it will be displayed in the tooltip.
- The Fortune Upgrade doesn't use NBT anymore to store the fortune level.

### Fixed

- Fixed network not disconnecting when Controller is broken.
- Fixed bug where when multiple Fortune Upgrades are inserted, it chooses the first Fortune Upgrade instead of the highest one.
- Fixed some translations having too big "Craft" text.
- Fixed crash with GUI when toggling the Grid size quickly.
- Fixed scrollbar not scrolling correctly when clicked with mouse when grid tabs are visible.
- Fixed Reader and Writers GUIs still displaying channels even if not connected.
- Fixed Solderer resetting progress when the inventory changes.

## [v1.5.17] - 2017-08-19

### Added

- Re-added support for OpenComputers.

### Fixed

- Fixed crash with Grid.

## [v1.5.16] - 2017-08-09

### Fixed

- Fixed crash when placing a Controller.
- Fixed crash when configuring an Exporter.
- Fixed Refined Storage not running in MC 1.12 and only on MC 1.12.1.

## [v1.5.15] - 2017-08-09

### Added

- Added InventoryTweaks Grid sorting.
- Added InventoryTweaks inventory sort ability in Refined Storage GUIs.
- Added CTM integration for Disk Manipulator.

### Changed

- Updated Forge to 2444 (MC 1.12.1).

### Fixed

- Fixed possible rare dupe bug with Importer.
- Fixed Shulker Box dupe bug with Destructor.
- Fixed Grid crash with search history.
- Fixed Grid crash with search field.
- Fixed External Storage not working without Storage Drawers.
- Fixed External Storage not calculating max stack size in the calculation of it's capacity display in the GUI.
- Fixed Refined Storage not drawing small text correctly with Unicode font.
- Fixed dupe bug with External Storage connected to an item handler.

## [v1.5.14] - 2017-08-03

### Added

- Added config option to modify the Solderer speed per Speed Upgrade, defaulting to 22.5% faster per upgrade, making it 90% faster on a fully upgraded Solderer.
- Added CTM integration.

### Changed

- Updated Forge to 2426.
- Updated French translation.

### Fixed

- Fixed more crashes relating to scrollbar in GUIs.
- Fixed crash with Detector.
- Fixed bug where pattern create button wasn't visible when grid tabs were selected.
- Fixed performance issue with Controllers turning off and on and Interfaces.
- Fixed Interfaces exposing network inventory don't hide storages that are disconnected.

## [v1.5.13] - 2017-07-20

### Fixed

- Fixed Wireless Fluid Grid not using up energy.
- Fixed Wireless Crafting Monitor remaining in network item list.

## [v1.5.12] - 2017-07-17

### Added

- Added additional API for grids.

### Changed

- The Network Transmitter now uses 1 FE/t per block instead of 4 FE/t.

## [v1.5.11] - 2017-07-16

### Fixed

- Fixed not being able to smelt quartz into silicon.
- Fixed Grid extracting wrong enchanted books.

## [v1.5.10] - 2017-07-15

### Fixed

- Fixed crash relating to MCMP.

### Changed

- Converted Solderer recipes to JSON.
- Implemented controller update throttling, should fix lag issues with controllers that constantly turn off and on.

## [v1.5.9] - 2017-07-10

### Fixed

- Fixed not being able to extract anything when connecting an External Storage to Storage Drawers.

## [v1.5.8] - 2017-07-08

### Changed

- Updated Forge to 2400.
- Updated Storage Drawers API.
- Autocrafting can now fill water bottles with water from the fluid storage - regular bottles or pattern for regular bottles are required.

### Fixed

- Fixed bug where disks have to be re-inserted in the Disk Drive in order to work again after rejoining a chunk.
- Fixed bug where items inserted in Storage Drawers through External Storage with a Drawer Controller wouldn't respect drawer priority rules.
- Fixed crash on server when getting an advancement.

### Removed

- Removed migration support for worlds from MC 1.10.2. To migrate your world to MC 1.12 from MC 1.10.2, first open it in MC 1.11.2.

## [v1.5.7] - 2017-07-04

### Added

- Added advancements.

### Changed

- Exposed pattern inventory for Pattern Grid.

### Fixed

- Fixed crashes relating to scrollbar in GUIs.

## [v1.5.6] - 2017-06-29

### Changed

- Updated Forge to stable 2387.

### Fixed

- Fixed bug where players couldn't place regular blocks next to secured networks.

### Removed

- Removed Processing Pattern Encoder, that functionality is now available in the Pattern Grid.

## [v1.5.5-beta] - 2017-06-25

### Changed

- Updated Forge to 2363.

## [v1.5.4-beta] - 2017-06-24

### Fixed

- Fixed External Storage crashing.
- Fixed crash when node data mismatches between world and dat file.

## [v1.5.3-beta] - 2017-06-24

### Added

- The Portable Grid now exposes an inventory for interaction with other mods or vanilla.
- The Interface now exposes the entire storage inventory (if no slots are set for exporting) for interaction with other mods or vanilla.

### Changed

- Updated Forge to 2359.
- Updated MCMultiPart to 2.2.1.

### Fixed

- Fixed Solderer crashing.
- Fixed Solderer being able to work with insufficient ingredients.
- Fixed Interface extracting from itself when trying to keep items in stock.
- Fixed Quartz Enriched Iron recipe only giving 1 instead of 4.
- Fixed Debug Storage disks not working correctly.
- Fixed Disk Drive giving incorrect capacity for creative and debug storage disks.

### Removed

- The Relay now reacts instantly to a redstone signal again, removed throttling for it.

## [v1.5.2-beta] - 2017-06-20

### Fixed

- Fixed a bug where loading nodes would abort when a single node has an error while reading.
- Fixed Filters not persisting correctly in Portable Grid.

## [v1.5.1-beta] - 2017-06-20

### Added

- Re-added MCMultiPart support.
- Added back crafting recipes.

### Changed

- Updated Forge to 2340.
- Changed Grid recipe.
- Changed Crafting Monitor recipe.

### Fixed

- Fixed Filters not persisting correctly in Wireless Grid and Wireless Crafting Monitor.
- Fixed Disk Drive recipe not using ore dictionary for chest.
- Fixed crash when getting tooltip for grid item.

### Removed

- Removed Tesla integration.
- Removed RS energy units, the entire mod is powered with Forge Energy now.

## [v1.5.0-alpha] - 2017-06-14

### Added

- Port to Minecraft 1.12.
- The Portable Grid now doesn't despawn anymore when dropped in the world.

### Fixed

- Fixed bug where oredict autocrafting didn't work in some cases.

### Removed

- Removed MCMultiPart support (will be re-added as soon as MCMultiPart for MC 1.12 is available).
- Removed OpenComputers support (will be re-added as soon as OpenComputers for MC 1.12 is available).
- Removed crafting recipes, until Forge adds the recipe system back.

## [v1.4.20] - 2017-07-15

### Fixed

- Fixed crash relating to MCMP.

## [v1.4.19] - 2017-07-15

### Added

- Implemented controller update throttling, should fix lag issues with controllers that constantly turn off and on.

### Fixed

- Fixed bug where players couldn't place regular blocks next to secured networks.
- Fixed crashes relating to scrollbar in GUIs.
- Fixed bug where disks have to be re-inserted in the Disk Drive in order to work again after rejoining a chunk.

## [v1.4.18] - 2017-06-24

### Fixed

- Fixed Solderer crashing.
- Fixed Interface extracting from itself when trying to keep items in stock.
- Fixed Solderer being able to work with insufficient ingredients.
- Fixed Disk Drive giving incorrect capacity for creative and debug storage disks.
- Fixed External Storage crashing.
- Fixed crash when node data mismatches between world and dat file.

### Removed

- The Relay now reacts instantly to a redstone signal again, removed throttling for it.

## [v1.4.17] - 2017-06-20

### Fixed

- Fixed Filters not persisting correctly in Wireless Grid, Wireless Crafting Monitor and Portable Grid.
- Fixed Disk Drive recipe not using ore dictionary for chest.
- Fixed a bug where loading nodes would abort when a single node has an error while reading.

## [v1.4.16] - 2017-06-14

### Added

- The Portable Grid now doesn't despawn anymore when dropped in the world.

### Fixed

- Fixed bug where oredict autocrafting didn't work in some cases.

## [v1.4.15] - 2017-06-14

### Changed

- Updated Storage Drawers API, fixes crashes.

## [v1.4.14] - 2017-06-13

### Fixed

- Fixed Solderer not accepting books made in anvil.

## [v1.4.13] - 2017-06-13

### Added

- The Portable Grid now has an indicator whether it's connected or disconnected and shows the disk.

### Fixed

- Fixed Portable Grid model.
- Fixed ore dictionary causing problems with Solderer.
- Fixed ore dictionary items not showing up in JEI for the Solderer.

### Removed

- Removed Quartz Enriched Iron ore dictionary entry.

## [v1.4.12] - 2017-06-10

### Added

- Added Korean translation.
- Implemented block update throttling when network turns on and off.

### Changed

- Updated Forge to 2315.
- Updated JEI to 4.5.0.
- You can now shift click items from the Grid crafting slots to the player inventory when the Grid is disconnected.

### Fixed

- Fixed error logs when watching a Controller when a network changes.

### Removed

- Removed Collosal Chests integration.

## [v1.4.11] - 2017-06-05

### Added

- Added support for External Storage on Interfaces and other Refined Storage blocks, so you can keep items in stock easier.
- You now have to sneak to place the Portable Grid in the world.

### Changed

- The Machine Casing now requires 1 piece of stone in the middle.
- Changed recipe of Disk Drive to no longer require a Solderer.
- Changed recipe of Interface to no longer require a Basic Processor, but a Machine Casing instead.

### Fixed

- Fixed bug where storages that are removed remain visible.
- Fixed bug where the GUI didn't close when a block is broken, causing a dupe bug with the Portable Grid.

### Removed

- Removed debug log configuration option, as it's no longer needed.
- Removed "autocraft on redstone signal" option in the Crafter, use an External Storage in combination with an Interface with the Crafting Upgrade instead.

## [v1.4.10-beta] - 2017-05-25

### Changed

- Improved performance of network scanning.

### Fixed

- Fixed crash when attempting to get direction of a node.
- Fixed bug where some network parts don't want to connect to the storage system.

## [v1.4.9-beta] - 2017-05-24

### Fixed

- Fixed bug where inventory data was lost sometimes upon opening the world.

## [v1.4.8-beta] - 2017-05-24

### Fixed

- Fixed missing config categories in ingame config.
- Fixed Controller not working anymore after changing redstone setting.
- Fixed crash when placing or destroying network blocks.

## [v1.4.7-beta] - 2017-05-23

### Added

- Added config option "debugLog" that logs diagnostic info to help developers to fix the inventory loss bug, please enable it if you are experiencing this issue.

### Fixed

- Fixed bug where Portable Grid would dupe in inventory.
- Worked around an autocrafting bug to prevent crashes.

## [v1.4.6-beta] - 2017-05-17

### Changed

- Performance improvement to network scanning.

### Fixed

- Fixed Wrench opening GUIs while performing action.
- Fixed client Grid GUI clearing and causing crashes after starting an autocrafting request.

### Removed

- Removed debug output from v1.4.5.

## [v1.4.5-beta] - 2017-05-14

### Added

- Added Portable Grid.
- Added OpenComputers integration.

### Changed

- Updated Forge to 2296.
- Removed ticking tile entities, every tile entity in RS is non-ticking now.

### Fixed

- Fixed Crafting Tweaks buttons positioned wrongly.
- Fixed Crafting Tweaks keybindings interfering with RS keybindings.
- Fixed crash when updating storages.
- Fixed no tooltips for fluid filter slots.
- Fixed Disk Manipulator in fluid mode not showing fluids.
- Fixed dupe bug in the Interface.

## [v1.4.4-beta] - 2017-04-27

### Changed

- Updated Forge to 2284.

### Fixed

- Fixed Disk Manipulator crashing due to empty stack.
- Fixed issue where empty stacks show up in the system.
- Fixed Storage Monitor not respecting security settings.

## [v1.4.3-beta] - 2017-04-22

### Added

- Display progress bar on JEI recipes for the Solderer.

### Changed

- Updated Forge to 2282.
- Updated JEI version.
- Updated MCMultiPart version.
- You can now shift click Grid Filters into a Grid instead of manually inserting them.
- You can now use up and down arrows to scroll through Grid search history.
- Shift clicking patterns in the Pattern Grid now puts the patterns in the pattern slot.
- Storage Monitors don't render any quantity text when no item is specified to monitor anymore.
- The Solderer inventory isn't sided anymore.
- Small performance improvement: only sort the storages when needed.

### Fixed

- Fixed bug where disks in Disk Drive didn't respect access type or void excess stacks option.
- Fixed crash in Disk Manipulator.
- Fixed oredict not working.
- Fixed Grid crash.
- Fixed Fluid Grid not formatting large quantities correctly.

## [v1.4.2-beta] - 2017-04-01

### Added

- Implemented support for the Forge update JSON system.
- Added integration for MCMultiPart, this is an optional dependency.
- You can now specify more items to export, import, filter, etc. by inserting the Filter item.
- Made the keybinding to focus on the Grid search bar configurable.

### Changed

- Updated Forge to 2261.
- The Detector no longer outputs a strong redstone signal.
- Fire event on completion of an autocrafting task.
- Fire "player crafting" event when shift clicking in the grid.

### Fixed

- Fixed a crash with the Constructor.
- Fixed Crafting Pattern model.
- Fixed Quartz Enriched Iron and the block form of it not having an oredictionary entry.
- Fixed crash in storage cache.
- Fixed slow oredict comparisons causing TPS lag.
- Fixed controller model warning during launch.
- Fixed not rendering some tooltips correctly.
- Fixed crash with External Storage.
- Fixed Interface duping items on extract-only storages.
- Fixed controls menu showing unlocalized text for Refined Storage keybindings.
- Autocrafting bugfixes.
- Improved memory usage of some models.
- Performance improvements related to storage inserting and extracting.

### Removed

- Removed support for the Deep Storage Unit API.

## [v1.4.1-beta] - 2017-02-19

### Added

- Added Storage Monitor.

### Changed

- Updated Forge to 2232.

### Fixed

- Fixed Processing Pattern Encoder and Security Manager recipes not supporting oredict workbench and chest.
- Fixed network nodes not respecting redstone mode.
- Fixed "Clear" and "Create Pattern" buttons not working correctly when using Grid Filter tabs.
- Fixed Wrench in Dismantling Mode voiding Storage Block contents.
- Fixed OPs not having global permissions on secured storage systems.
- Fixed crash when Destructor tries to break secured network block.
- Fixed Fluid Interface not dropping inventory contents.
- Fixed Disk Manipulator crash.

## [v1.4.0-beta] - 2017-02-06

### Added

- Added Security Manager.
- Added Security Card.
- Added Wireless Fluid Grid.
- Added craft-only toggle to Exporter.
- Added Reader.
- Added Writer.
- Added blocking mode to patterns in autocrafting.
- Added Grid size toggle (stretched, small, medium, large).
- Added dismantling mode to the Wrench.
- Added Block of Quartz Enriched Iron.
- Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance.
- Added filtering slots for the Crafting Monitor.
- Added way to hide tasks created in an automated way in the Crafting Monitor.
- Added Grid sorting by ID.
- Added Solderer particles.
- Added ore dictionary Grid filter (use `$` as prefix like in JEI).

### Changed

- You can now bind multiple crafters with the same pattern to machines, to spread or balance out autocrafting.
- Fluid Grid now first tries to get buckets from your inventory instead of the storage.
- Updated Forge to 2226.
- Updated Chinese translation.
- Converting blocks instead of ingots to Printed Processors is now a little faster.
- The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time.
- Ignore damage for damageable items when transferring into crafting grid.
- Ignore tags from given items when transferring into crafting grid.
- Removed sidedness from fluid interface.
- Using tab in a grid that isn't in autoselected mode will focus on the search box.
- Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it.
- Increased size of Detector textbox.
- Handle breaking and placing blocks better for Constructor and Destructor.
- Pressing shift while starting a crafting task will skip the crafting preview.

### Fixed

- Fixed Regulator mode not working.
- Fixed Fluid Interface still being sided.
- Fixed Constructor not working on Botania flowers.
- Fixed Wireless Transmitter working even if it was disabled with redstone mode.
- Fixed Solderer not accepting books created in an Anvil.
- Fixed bug where network machines stopped working on chunk borders.
- Fixed memes not working.
- Fixed External Storage crashes.
- Fixed Constructor in liquid mode being able to place fluids <1000 mB.
- Fixed Solderer recipe conflicts, allowing for easier automation.
- Fixed stack upgrades not working in exporter when stack size is 16.
- Fixed crash when rotating External Storage.
- Fixed disk textures not working on latest Forge.
- Fixed crash when placing head with Constructor.
- Autocrafting bugfixes.
- Made sure External Storage always has the correct inventory in world.

## [v1.3.5-alpha] - 2016-12-14

### Added

- Added regulator mode to Exporter.

### Changed

- Updated French translation.

### Fixed

- Fixed TPS lag on very large crafting tasks.
- Fixed not being able to use autocrafting on some EnderIO items.
- Fixed server crash with ore dictionary checks.
- Fixed Controller not using energy.
- Fixed dupe bug when inserting bucket in Fluid Grid.
- Fixed not being able to start autocrafting for storage disks.
- Fixed oredict button not having the correct position on a small resolution.
- Fixed Constructor not using Crafting Upgrade when in item dropping mode.

## [v1.3.4-alpha] - 2016-12-10

### Added

- Added option to check for oredict in the Grid Filter.
- Added option to use a mod filter in the Grid Filter.
- Added option to use a whitelist or blacklist in the Grid Filter.
- Added Grid tabs using Grid Filters.
- Added configuration option to enable large fonts in Grid.

### Changed

- The Grid now resizes based on screen size (max rows can be configured).
- Made Solderer tooltip less big.
- Made the Interface sideless, you can just insert or extract from any side.

### Fixed

- Fixed bug with opening a network item with food in offhand.
- Fixed not respecting "Extract only" option for storages.
- Fixed a few autocrafting bugs.
- Fixed a crash with the Disk Manipulator.

## [v1.3.3-alpha] - 2016-12-06

### Changed

- Updated Forge to 2188.

### Fixed

- Fixed not being able to start a crafting task.

## [v1.3.2-alpha] - 2016-12-04

### Fixed

- Fixed being able to exceed max stack size while shift clicking.
- Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool.
- Fixed client crash when placing network blocks.

## [v1.3.1-alpha] - 2016-12-04

### Changed

- Updated Forge to 2180.
- Made Upgrades stackable.

### Fixed

- Fixed Disk Drive not noticing a Storage Disk being shift clicked out of the GUI.

## [v1.3.0-alpha] - 2016-12-03

### Added

- Port to Minecraft 1.11.

### Removed

- Removed RF support, use Forge Energy instead.
- Removed IC2 support.
- Removed MCMultiPart support (will be re-added as soon as MCMultiPart for MC 1.11 is available).

## [v1.2.26] - 2017-06-10

### Fixed

- Fixed Interface duping items on extract-only storages.
- Fixed calculating crafting resources for more than 9 unique items, for addon mods.

## [v1.2.25] - 2017-03-25

### Changed

- Fire event on completion of an autocrafting task.
- Fire player crafting event when shift clicking in the grid.
- Allow INodeNetwork instances to return an ItemStack for display in Controller GUI.

## [v1.2.24] - 2017-03-18

### Changed

- Made the keybinding to focus on the Grid search bar configurable.

### Fixed

- Autocrafting bugfixes.

## [v1.2.23] - 2017-03-11

### Added

- Implemented support for the Forge update JSON system.

### Changed

- The Detector no longer outputs a strong redstone signal.

### Fixed

- Fixed crash in storage cache.
- Fixed Crafting Pattern model.
- Fixed Constructor not working on Botania flowers.
- Fixed Disk Manipulator crash.
- Fixed slow oredict comparisons causing TPS lag.

## [v1.2.22] - 2017-02-19

### Fixed

- Fixed recipe for Processing Pattern Encoder not using oredictionary for the workbench.
- Fixed Fluid Interface not dropping inventory contents.
- Fixed glitchy upgrade recipes in the Solderer.

## [v1.2.21] - 2017-02-07

### Fixed

- Fixed crash when placing head with Constructor.

## [v1.2.20] - 2017-02-02

### Added

- Added Solderer particles.
- Added Grid sorting by ID.

### Fixed

- Fixed client side crash with cables.

## [v1.2.19] - 2017-02-01

### Added

- Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance.

### Changed

- Updated cable part back texture and Construction and Destruction Core textures.
- Updated Forge to 2221.
- Updated Chinese translation.
- Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it.
- Increased size of Detector textbox.
- Autocrafting bugfixes.
- Handle breaking and placing blocks better for Constructor and Destructor.

### Fixed

- Fixed stack upgrades not working in exporter when stack size is 16.
- Fixed crash when rotating External Storage.
- Fixed disk textures not working on latest Forge.

## [v1.2.18] - 2017-01-22

### Changed

- Fluid Grid now first tries to get buckets from your inventory instead of the storage.
- Performance improvements with oredict autocrafting.

### Fixed

- Fixed client side crash with cable.
- Fixed client side crash with disk drive.
- Fixed crash with external storage in fluid mode.

## [v1.2.17] - 2017-01-12

### Added

- Add Ore Dictionary grid filter (use $ as prefix like in JEI).

### Changed

- Ignore damage for damageable items when transferring into crafting grid.
- Ignore tags from given items when transferring into crafting grid.
- Removed sidedness from fluid interface.
- Using tab in a grid that isn't in autoselected mode will focus on the search box.
- The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time.

### Fixed

- Fixed Constructor in liquid mode being able to place fluids <1000 mB.
- Fixed Solderer recipe conflicts, allowing for easier automation.
- Fixed machines not connecting with cable after Controller.

## [v1.2.16] - 2017-01-04

### Fixed

- Fixed an autocrafting regression.
- Fixed crash with External Storage.

## [v1.2.15] - 2017-01-03

### Fixed

- Fixed Grid Filter hiding everything when 2 or more items are in it.
- Fixed External Storage crash when breaking a connected inventory.
- Autocrafting bugfixes.

## [v1.2.14] - 2016-12-24

### Fixed

- Fixed server crash.

## [v1.2.13] - 2016-12-23

### Fixed

- Fixed memes not working.
- Fixed controller causing network rebuild on every neighbor change.
- Fixed Wireless Transmitter working even if it was disabled with redstone mode.
- Fixed Solderer not accepting books created in an Anvil.
- Autocrafting bugfixes.
- Made sure External Storage always has the correct inventory in world.

## [v1.2.12] - 2016-12-16

### Changed

- Updated French translation.

### Fixed

- Fixed TPS lag on very large crafting tasks.
- Fixed not being able to use autocrafting on some EnderIO items.
- Fixed not being able to start autocrafting for storage disks.
- Fixed oredict button not having the correct position on a small resolution.
- Fixed Constructor not using Crafting Upgrade when in item dropping mode.

## [v1.2.11] - 2016-12-10

### Added

- Added configuration option to enable large fonts in Grid.

### Changed

- The Grid now resizes based on screen size (max rows can be configured).
- Made the Interface sideless, you can just insert or extract from any side.

## [v1.2.10] - 2016-12-09

### Changed

- Made Solderer tooltip less big.

### Fixed

- Fixed a crash with the Disk Manipulator.
- Fixed not respecting "Extract only" option for storages.
- Fixed bug with opening a network item with food in offhand.
- Fixed other fluid storages going negative when using void excess fluids option.
- A few autocrafting bugfixes.

## [v1.2.9] - 2016-12-06

### Changed

- Updated Forge to 2185.

### Fixed

- Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool.

## [v1.2.8] - 2016-11-30

### Fixed

- Fixed autocrafting bugs.

## [v1.2.7] - 2016-11-29

### Changed

- Updated German translation.

### Fixed

- Fixed not being able to place disks in Disk Drives on servers.

## [v1.2.6] - 2016-11-26

### Changed

- Processing patterns can now insert buckets.

### Fixed

- Fixed crash with Exporters in fluid mode.

### Removed

- Removed Solderer progress percentage text.

## [v1.2.5] - 2016-11-24

### Added

- Added "View Recipes" JEI toggle in Solderer.

### Changed

- The Constructor can now place fireworks.
- Updated Forge to 2151.

### Fixed

- Fixed a bunch of autocrafting bugs.
- Fixed Grid search not working correctly.
- Fixed items disappearing from Solderer inventory.
- Fixed being able to take fluids that have less than 1000 millibuckets filled in Fluid Grid.
- Fixed Constructor being able to place fluids that have less than 1000 millibuckets.
- Fixed Exporter and Importer not working properly with fluids.
- Fixed inserting new stack type with right click in Grid causing a desync.
- Fixed Constructor not calling block place event.
- Fixed shift clicking non disk items in the Disk Manipulator voiding them.

## [v1.2.4] - 2016-11-10

### Added

- Added tooltip search with #.

### Changed

- Mod search can now also take mod name instead of just id.

### Fixed

- Fixed bug where Disk Manipulator doesn't save disks.
- Fixed Disk Drive stored quantity GUI text hovering over other text.
- Fixed External Storage being in item and fluid mode at the same time.
- Fixed Wrench working when player is not sneaking.
- Fixed External Storage cache counting items up when extracting.
- Fixed External Storage cache not working properly on Compacting Drawers.

### Removed

- Removed ability to put External Storages on Refined Storage network blocks.

## [v1.2.3] - 2016-11-06

### Fixed

- Fixed fluid cache updating wrongly.
- Fixed Exporter scheduling too many crafting tasks.

## [v1.2.2] - 2016-11-05

### Fixed

- Fixed item voiding when exporting to a chest with a storage in Extract Only mode.
- Various autocrafting fixes.

## [v1.2.1] - 2016-11-05

### Added

- Added Wireless Crafting Monitor (with temporary textures).
- Added support for JEI R and U keys in Grids.

### Changed

- You can now decompose storage disks if the item count is below zero by any chance.

### Fixed

- Fixed crafting upgrade having weird behavior.
- Fixed external storage not updating when loading chunk.
- Fixed external storage crash.
- Fixed weird autocrafting behavior.

### Removed

- Removed controller explosions when multiple controllers are connected to the same network.

## [v1.2.0] - 2016-11-03

### Added

- Added new autocrafting system.
- Added ore dictionary autocrafting.
- Added recipe transfer handler for Processing Pattern Encoder.
- Added void excess items functionality to storage blocks.
- Added config option to configure RS to EU conversion rates.
- Added ability to toggle between insert and extract, only insert and only extract mode in storage blocks.
- Added Silk Touch Upgrade for Destructor.
- Added Fortune Upgrade for Destructor.
- Added ore dictionary compare toggle to storage I/O blocks.
- Added disk leds to Disk Drive block that shows the disks.
- Added disk leds to Disk Manipulator block that shows the disks.
- Added Wrench, has two modes: configuration saving / reading mode, and rotation mode.
- Stack upgrade in Importer / Exporter in fluid mode and Fluid Interface now transfers 64 buckets at once.

### Changed

- Changed storage GUIs.
- Changed default EU conversion rate to be 1:8 with RS.
- The Constructor can now drop items in the world.
- The Constructor can now place skulls.
- The Destructor can now pick up items in the world.
- Storage disks and storage blocks now don't despawn anymore when dropped in the world.
- Grid item and fluid quantity now only rounds to 1 digit after comma.
- Items count can no longer overflow, and will max out at the maximum integer value.
- Updated Storage Drawers API.
- Controller sorts by energy usage in GUI (highest to lowest).
- Detector without any filter will detect based on total items or fluids stored.
- Limited network transmitter usage to 1000 RS/t.

### Fixed

- Fixed lag issues caused by External Storage.
- Fixed resetting a stack of patterns yields 1 blank pattern.
- Fixed being able to pipe items in the export slots of the Interface.
- Fixed Interface being stuck when item isn't accepted in storage.
- Fixed items with colored name being uncolored in Grid.
- Fixed fluid rendering bugging out side buttons.
- Fixed item count going negative when using the Disk Manipulator.
- Fixed Storage Drawer quantities not updating properly on Void Drawers.
- Fixed Disk Manipulator blocking items transferring in some cases.
- Fixed External Storage crafting recipe not supporting ore dictionary chests.
- Fixed when shift clicking crafting recipe and inventory is full items are dropping on the ground instead of going in the system.
- Fixed glitchy rendering of cable parts in item form.
- Fixed Destructor being able to break bedrock.
- Fixed External Storage thinking that items are inserted in Extra Utilities Trash Cans.
- Fixed Grid quantities being unreadable when using unicode font.
- Fixed disconnecting when Storage Disk or Storage Block is too big.

## [v1.2.0-beta.8] - 2016-11-03

### Fixed

- More autocrafting issues.

## [v1.2.0-beta.7] - 2016-11-03

### Fixed

- More autocrafting issues.
- External Storage crashes and TPS lag issues.
- Mekanism recipes are autocraftable again.

## [v1.2.0-beta.6] - 2016-11-02

### Fixed

- More autocrafting issues.
- Oredict autocrafting has been improved.

## [v1.2.0-beta.5] - 2016-10-30

### Fixed

- More autocrafting issues.
- External Storage TPS lag issues.

## [v1.2.0-beta.4] - 2016-10-27

### Fixed

- More autocrafting and TPS issues.

## [v1.2.0-beta.3] - 2016-10-25

### Fixed

- More autocrafting issues.

## [v1.2.0-beta.2] - 2016-10-24

### Fixed

- Extreme TPS issues while crafting.
- Laggy disk drive rendering.

## [v1.2.0-beta.1] - 2016-10-23

### Changed

- First beta release for v1.2.0, featuring a new autocrafting system.

## [v1.1.3] - 2016-10-07

### Fixed

- Fixed some clients not starting up due to too many Disk Drive model permutations.

## [v1.1.2] - 2016-10-03

### Added

- Added recipe transfer handler for Solderer.

### Changed

- It is now possible to start a crafting task even if the crafting preview says you can't.

### Fixed

- Fixed crash with JEI when changing screens in autocrafting.
- Fixed not being able to start autocrafting in other dimensions with Network Transmitter / Network Receivers.
- Fixed JEI overlay disappearing now and again.
- Fixed Detector hitbox.

## [v1.1.1] - 2016-09-28

### Fixed

- Fixed crash on servers.

## [v1.1.0] - 2016-09-28

### Added

- New art by CyanideX.
- Added crafting preview screen.
- Added max crafting task depth.
- Added helpful tooltips to Solderer and Processing Pattern Encoder.

### Changed

- Every machine now compares on damage and NBT by default.
- Updated JEI, fixes crashes.
- Detector amount text field doesn't autoselect anymore.

### Fixed

- Fixed crash with Disk Manipulator.
- Fixed autocrafting not giving back byproducts.

## [v1.0.5] - 2016-09-21

### Added

- Importer now takes a Destruction Core, and Exporter a Construction Core.
- Added Disk Manipulator.
- Added ingame config.
- Added the ability to see the output of a Pattern by holding shift.
- Exporter in fluid mode and Fluid Interface no longer duplicates fluids that are less than 1 bucket.

### Changed

- Changed default Grid sorting type to quantity.
- Updated Dutch translation.
- Updated Chinese translation.
- When a machine is in use by a crafting pattern, inserting of items from other patterns will be avoided.

### Fixed

- Fixed crafting a complex item causes the process to flow off the Crafting Monitor's GUI.
- Fixed shift clicking from Grid when player inventory is full throwing items in the world.

## [v1.0.4] - 2016-09-17

### Fixed

- Fixed lag caused by Crafter.

## [v1.0.3] - 2016-09-17

### Added

- Added integration for Forge energy.

### Changed

- Solderer now accepts items from any side, allowing easier automation.
- Solderer is now intelligent about items in slots, and will only accept an item if it is part of a recipe.
- Changed recipe for upgrades in the Solderer, they now just take 1 of the unique item instead of 2, using redstone instead.
- Updated to Forge 2088.

### Fixed

- Fixed item loading issue.
- Fixed fluid autocrafting scheduling too much crafting tasks for buckets.
- Fixed blocks in hand facing wrong direction.

## [v1.0.2] - 2016-09-14

### Changed

- +64 in crafting start GUI now gives 64 from the first time instead of 65.

### Fixed

- Fixed processing patterns not handling item insertion sometimes.

### Removed

- Removed crafting task limit in crafting start GUI.

## [v1.0.1] - 2016-09-13

### Added

- Added "autocrafting mode" in Detector, to check if an item is being crafted. If no item is specified, it'll emit a signal if anything is crafting.
- Added an option for the Crafter to trigger autocrafting with a redstone signal.

### Changed

- Updated to Forge 2084.

### Fixed

- Fixed advanced tooltips showing in Grid when not configured to do so.
- Optimized crafting pattern loading.

## [v1.0.0] - 2016-09-12

### Added

- Interface now supports Crafting Upgrade.
- Implemented multithreaded autocrafting.

### Changed

- Processing patterns now hold their items back for pushing until all the required items are gathered from the system.
- Reworked Crafting Monitor GUI.
- When shift clicking a recipe in the Crafting Grid, the player inventory is now leveraged as well.
- Updated to Forge 2077.
- Due to the new crafting system, all Crafting Patterns made before 1.0 have to be re-made.

### Fixed

- Fixed item and fluid storage stored count having incorrect values at times.
- Fixed problems relating to Crafting Upgrade (scheduling a task wrongly, blocking other tasks, etc).
- Fixed machines breaking on long distances.
- Fixed Controller rebuilding network graph on energy change.
- Fixed fluids not caring about NBT tags.
- Fixed fluids that have less than 1 bucket stored render only partly in Fluid Grid.
- Fixed Fluid Interface voiding bucket when shift clicking to out slot.
- Fixed wrong machine connection logic.

## [v0.9.4-beta] - 2016-08-27

### Changed

- Reduced explosion radius when multiple controllers are connected to the same network.

### Fixed

- Fixed mod not working without JEI.
- Little fixes in German translation.
- Reverted network changes that caused buggy behavior.

## [v0.9.3-beta] - 2016-08-26

### Added

- Added Chinese translation.
- Added Crafting Tweaks integration.

### Changed

- Updated German translation for Fluid Storage.
- Updated Dutch translation for Fluid Storage.
- Reworked storage network code, should fix weird machine disconnection issues.

### Fixed

- Fixed that the Fluid Storage Disk recipe returns an invalid disk.

## [v0.9.2-beta] - 2016-08-25

### Fixed

- Fixed not being able to take out items from Wireless Grid cross-dimensionally.

## [v0.9.1-beta] - 2016-08-24

### Fixed

- Fixed server crash with Grid.

## [v0.9.0-beta] - 2016-08-24

### Added

- Added fluid storage.
- Added Russian translation.

### Changed

- Energy usage of Wireless Grid is now configurable.
- Wireless Transmitters can now only be placed on Cable.
- Priority field and detector amount field can now display 4 digits at a time.

### Fixed

- Fixed crash with Grid.
- Fixed Grid Filter only updating the Grid when reopening the GUI.
- Fixed Wireless Grid not working cross dimensionally.
- Fixed Grid not displaying items after changing redstone mode.
- Fixed Wireless Transmitter crashing when it is transmitting to a removed dimension.
- Fixed disassembling stacked Storage Blocks only returns 1 set of items.
