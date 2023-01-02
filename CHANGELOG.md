# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

- Fixed some craftable items not showing as craftable in JEI
- Fixed grid crashing on exit if JEI mod is not used

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
