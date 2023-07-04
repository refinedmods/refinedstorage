# Refined Storage Changelog

### 0.7.18

**Bugfixes**

- Fixed cables sending updates when not needed
- Fixed cables not connecting to foreign machines that implement the API

### 0.7.17

**Bugfixes**

- Fixed getting wrong items back in grid
- Fixed wrong item getting crafted
- Fixed server lag with exporter and importer
- Updated Forge to 1969
- Updated JEI to 3.6.x

**Features**

- New crafting settings gui
- Tweaked some textures

### 0.7.16

**Features**

- Added support for Storage Drawers void upgrade
- Added support for Deep Storage Unit API again

**Bugfixes**

- Fixed NPE in machine searching
- Fixed a bug with interface giving negative amounts of items
- Fixed crash when using scroll wheel

### 0.7.15

**Bugfixes**

- Fixed not being able to scroll with the scroll wheel using MouseTweaks
- Fixed grid search box mode only changing after reopening gui

### 0.7.14

**Bugfixes**

- Updated Forge to build 1965
- Fixed item overflow bug with storage drawers and external storage

**Features**

- Added shift clicking support to every inventory (for upgrades etc)
- Added grid filtering options: @ for searching on mod items, # for searching on tooltips
- Added a way to clear patterns (shift + right click in inventory)
- Tweaked some recipes
- Tweaked energy usage in some machines

### 0.7.13

**Features**

- Added ability to triple click in grid

### 0.7.12

**Bugfixes**

- Fixed creative storage blocks and disks not working
- Fixed interface overflowing

### 0.7.11

**Bugfixes**

- Fixed crash with wireless grid
- Fixed high RF/t usage on external storage
- Fixed that requesting crafting processing task yields too many tasks

**Features**

- Right click on grid search bar clears the search query

### 0.7.10

**Bugfixes**

- Fixed inventories not saving correctly
- Fixed that the player can't shift-click patterns into the last 3 slots of the Crafter

### 0.7.9

**Bugfixes**

- Fixed not being able to place sugar cane
- Fixed not being able to place seeds
- Fixed stacks not splitting between storages correctly
- Fixed storage not saving ItemStack capabilities
- Fixed dropping items into crafting grid with mouse won't work if your mouse is in between items
- Fixed controller still drawing power even if disabled

**Features**

- Added an API
- Added Storage Drawers integration
- Added handling for patterns that return the same item
- Added stack splitting between multiple storages
- Added handling for patterns that give back the same item
- Increased cable recipe to 12 cables

### 0.7.8

**Bugfixes**

- Updated to Forge 1951
- Fixed crash on some worlds
- Improved Grid performance when sorting on quantity

### 0.7.7

**Bugfixes**

- Fixed buggy reequip animation on wireless grid
- Fixed solderer not supporting ore dictionary
- Fixed recipes not supporting ore dictionary
- Fixed destructor not being able to destroy some blocks
- Fixed not being able to place or destroy sugar cane
- Fixed storage blocks not being dismantable
- Fixed getting more items than needed sometimes
- New items now go to the first available storage that has items in it already
- Tweak some recipes
- Performance improvements

**Features**

- Added the Stack Upgrade
- Added Quartz Enriched Iron Block
- Added French translation by Leventovitch

### 0.7.6

**Bugfixes**

- Updated to Forge 1932
- Fixed not being able to start an autocraft

### 0.7.5

**Bugfixes**

- Performance improvements
- Fixed wrong ascending / descending order in Grid
- Fixed autocrafting not giving back byproducts
- Fixed Solderer causing too many chunk updates
- Fixed Solderer slot sides being weird

### 0.7.4

**Bugfixes**

- Updated to Forge 1922
- Performance improvements

### 0.7.3

**Bugfixes**

- Fixed grid performance by not sending grid data so often
- Fixed silicon + quartz enriched iron not having oredict names
- Broke Interface block inventory compatibility: make sure to take all your items out of your Interface blocks before
  you apply the update

**Features**

- Crafting tasks are now sorted from new to old in the Crafting Monitor

### 0.7.2

**Bugfixes**

- Fixed Importer getting stuck on slot

### 0.7.1

**Bugfixes**

- Fixed NPE in some tiles
- Fixed going out of crafting GUI not restoring state (scrollbar and search term)
- Fixed not being able to create a pattern in disconnected Pattern Grid
- Fixed not being able to place cake or string
- Performance improvement to Grids

### 0.7

**Bugfixes**

- Fixed Crafting Grid / Pattern Grid not throwing items on break

**Features**

- Port to Minecraft 1.9.4

### 0.6.15

**Bugfixes**

- Fixed Solderer sides being weird
- Fixed Solderer causing too many block updates

### 0.6.14

**Bugfixes**

- Performance improvements
- Fixed wrong ascending / descending order in Grid

### 0.6.13

**Bugfixes**

- Performance improvements

### 0.6.12

This is a bugfix release containing all fixes from the 1.9.4 version.

**Bugfixes**

- Fixed Crafting Grid / Pattern Grid not throwing items on break
- Fixed NPE in some tiles
- Fixed going out of crafting GUI not restoring state (scrollbar and search term)
- Fixed not being able to place cake or string
- Performance improvement to Grids
- Fixed Importer getting stuck on slot
- Fixed silicon + quartz enriched iron not having oredict names

**Features**

- Crafting tasks are now sorted from new to old in the Crafting Monitor

### 0.6.11

**Bugfixes**

- Fixed crafting patterns crashing when item of an input or output no longer exists
- Fixed Grid letting the current held item flicker
- Fixed Importer / Exporter / External Storage not being able to push or pull out of the other side of a double chest
- Converted all inventories in the mod to Forge's item handler capability system

### 0.6.10

**Bugfixes**

- Fixed Processing Patterns not working
- Fixed not being able to request more than 1 item at once
- Fixed crash with the Solderer
- Increased max crafting request size to 500

### 0.6.9

**Bugfixes**

- Fixed bug where machines wouldn't disconnect / connect when needed outside of chunk
- Fixed not being able to toggle redstone mode in a Wireless Transmitter
- Fixed same machine being connected to the network multiple times
- Fixed External Storage not working
- Reduced network usage

**Features**

- Added automation for the Solderer: every side corresponds to a slot (see the wiki)

### 0.6.8

**Bugfixes**

- Performance improvements
- Fixed CTRL + pick block on machines crashing game

### 0.6.7

**Bugfixes**

- Performance improvements

### 0.6.6

**Bugfixes**

- Fixed being able to insert non-allowed items in inventories with hoppers
- Fixed Processing Pattern Encoder not using up a Pattern

### 0.6.5

**Bugfixes**

- Performance improvements for servers
- Performance improvements for client scrollbars

**Features**

- Updated Forge to build 1907

### 0.6.4

**Bugfixes**

- Performance improvements

### 0.6.3

**Bugfixes**

- Performance improvements

### 0.6.2

**Bugfixes**

- Fixed race condition with crafting tasks
- Fixed pressing escape in crafting settings GUI not going back to grid GUI
- Fixed losing autoselection in Grid when clicking on slot with autoselection mode
- Fixed being able to pick up from pattern result slot
- Added a max crafting quantity per request cap (hardcoded to 100)

**Features**

- Upgrades now draw extra energy

### 0.6.1

**Bugfixes**

- Fixed NPE on world load
- Fixed Destructor crashing when removing a connected machine

### 0.6

**Bugfixes**

- Fixed Destructor not playing block break sound
- Fixed Constructor not playing block place sound
- Fixed picking up from crafting result slot
- Fixed being able to use right click on crafting result slot
- Fixed item duplication issue with the Interface
- Fixed Importers and Exporters not working when changing facing with a wrench
- Fixed Crafting Grid not respecting remainder in recipes
- Fixed Crafting Grid giving back the wrong amount of items when shift clicking
- Fixed items disappearing in Grid when doing a weird combination of inputs
- Fixed Solderer not stacking items
- Fixed Importer voiding Storage Disks from the Disk Drive
- Fixed Controller not saving energy

**Features**

- Added autocrafting
- Massive performance improvements which reduces lag and lets machines connect almost instantly
- Added the Pattern Grid
- Added the Crafting Monitor
- Added the Crafter
- Added the Processing Pattern Encoder
- Added a Pattern item
- Added the Wireless Transmitter
- Added Speed Upgrades which are applicable on a bunch of machines
- Added Range Upgrades for in the Wireless Transmitter
- Added Crafting Upgrades
- Changed Grid modes to have a autoselected option
- Added recipe category -> item JEI integration
- Added Storage Housing

### 0.5.6

**Bugfixes**

- Fixed sorting crash
- Fixed autofocusing on priority field in storage GUIs
- Fixed controller causing lag when energy level changes

### 0.5.5

**Bugfixes**

- Fixed several crashes
- Updated to Forge 1859

**Features**

- Energy level on Controller is maintained

### 0.5.4

**Bugfixes**

- Fixed machines out of the Controller's chunk range only connecting after block break when rejoining the world
- Fixed scrollbar skipping some rows when scrolling with mouse wheel
- Fixed machines from a long distance not being visible in the Controller

**Features**

- Shift clicking on placing Constructor and Destructor will have opposite direction

### 0.5.3

**Bugfixes**

- Fixed not being able to open a Grid that is 256 blocks away from the Controller
- Changed block hardness levels

**Features**

- Made the mod way less network intensive
- Added a Creative Wireless Grid

### 0.5.2

**Bugfixes**

- Items that don't exist anymore, won't be added to storage again to avoid crashes
- Fixed not being able to run the mod without JEI

### 0.5.1

**Bugfixes**

- Fix Disk Drive crashing with an AbstractMethodException

### 0.5

**Bugfixes**

- Fixed clicking sound in Grid
- Fixed a bunch of crashes
- Fixed Exporter not exporting is some cases
- Fixed Importer not importing in some cases
- Fixed Controller drawing RF every 20 ticks instead of every tick
- Fixed not being able to shift click from Crafting Grid crafting slots
- Fixed new items inserted after crafting in Grid being laggy
- Fixed flickering of items in Grid
- Fixed getting a stack of unstackable items from Grid
- Fixed Cable not having a collision box
- Check if the Constructor can actually place said block in the world

**Features**

- New textures
- Updated to the latest Forge and JEI
- Renamed Drives to Disk Drives
- Renamed Storage Cells to Storage Disks
- Removed Wireless Transmitters
- Wireless Grid is now bound to a Controller instead of a Grid
- Drives have a better interface and there are now blacklist and whitelist filters for the Storage Disks in it too.
- Destructors have the ability to whitelist and blacklist certain items now
- Shift clicking stuff in the Interface
- Scrollbar in Grid and Crafting Grid
- Made the normal Grid 1 row larger
- Display of connected machines in the Controller GUI
- Deep Storage Unit integration (with this several barrel mods are now supported too!)
- Machines don't need to be connected with cables anymore, they can be next to each other too
- Made the amount text in the Grid for items smaller
- Nice formatting for items >= 1K (pressing shift while hovering over an item will still display the real item count)
- When placing Importer, Exporter or External Storage with SHIFT, it will have the opposite direction. This is for easy
  placement behind other blocks (furnaces for example)
- Added mass crafting of items with shift in Crafting Grid
- Added JEI recipe transfering in Crafting Grid
- Grid can synchronize with JEI
- Side buttons in machine GUIs are now left, not right

Special thanks to [GustoniaEagle](https://github.com/gustoniaeagle) for the new textures,
and [tomevoll](https://github.com/tomevoll) for providing a bunch of patches and helping me debug some bugs.

### 0.4.1

**Bugfixes**

- Fix ID duplication issues

### 0.4

**Bugfixes**

- Cables now have actual collision
- Fullness percentage in Creative Storage Blocks going under 0%
- The Controller shouldn't display the base usage when not working
- Fix Minecraft reporting that retrieving Grid type fails
- Check isItemValidForSlot on trying to push to inventories

**Features**

- Relays
- Interfaces

### 0.3

- Initial release for Minecraft 1.9
