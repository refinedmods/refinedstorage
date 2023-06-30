# Refined Storage Changelog

### 1.1.3

- Fixed some clients not starting up due to too many Disk Drive model permutations (raoulvdberge)

### 1.1.2

- Added recipe transfer handler for Solderer (way2muchnoise)
- It is now possible to start a crafting task even if the crafting preview says you can't (raoulvdberge)
- Fixed crash with JEI when changing screens in autocrafting (raoulvdberge)
- Fixed not being able to start autocrafting in other dimensions with Network Transmitter / Network Receivers (
  raoulvdberge)
- Fixed JEI overlay disappearing now and again (raoulvdberge)
- Fixed Detector hitbox (raoulvdberge)

### 1.1.1

- Fixed crash on servers (raoulvdberge)

### 1.1

- New art by CyanideX (CyanideX)
- Added crafting preview screen (way2muchnoise)
- Added max crafting task depth (raoulvdberge)
- Added helpful tooltips to Solderer and Processing Pattern Encoder (raoulvdberge)
- Every machine now compares on damage and NBT by default (raoulvdberge)
- Updated JEI, fixes crashes (way2muchnoise)
- Fixed crash with Disk Manipulator (way2muchnoise)
- Fixed autocrafting not giving back byproducts (raoulvdberge)
- Detector amount text field doesn't autoselect anymore (raoulvdberge)

### 1.0.5

- Fixed crafting a complex item causes the process to flow off the Crafting Monitor's GUI (raoulvdberge)
- Fixed shift clicking from Grid when player inventory is full throwing items in the world (raoulvdberge)
- Importer now takes a Destruction Core, and Exporter a Construction Core (raoulvdberge)
- Added Disk Manipulator (way2muchnoise)
- Added ingame config (way2muchnoise)
- Added the ability to see the output of a Pattern by holding shift (raoulvdberge)
- When a machine is in use by a crafting pattern, inserting of items from other patterns will be avoided (raoulvdberge)
- Exporter in fluid mode and Fluid Interface no longer duplicates fluids that are less than 1 bucket (raoulvdberge)
- Changed default Grid sorting type to quantity (raoulvdberge)
- Updated Dutch translation (raoulvdberge)
- Updated Chinese translation (TartaricAcid)

### 1.0.4

- Fixed lag caused by Crafter (raoulvdberge)

### 1.0.3

- Fixed item loading issue (raoulvdberge)
- Added integration for Forge energy (raoulvdberge)
- Solderer now accepts items from any side, allowing easier automation (raoulvdberge)
- Solderer is now intelligent about items in slots, and will only accept an item if it is part of a recipe (
  raoulvdberge)
- Changed recipe for upgrades in the Solderer, they now just take 1 of the unique item instead of 2, using redstone
  instead (raoulvdberge)
- Fixed fluid autocrafting scheduling too much crafting tasks for buckets (raoulvdberge)
- Fixed blocks in hand facing wrong direction (raoulvdberge)
- Updated to Forge 2088 (raoulvdberge)

### 1.0.2

- Fixed processing patterns not handling item insertion sometimes (raoulvdberge)
- Removed crafting task limit in crafting start GUI (raoulvdberge)
- +64 in crafting start GUI now gives 64 from the first time instead of 65 (raoulvdberge)

### 1.0.1

- Fixed advanced tooltips showing in Grid when not configured to do so (raoulvdberge)
- Added "autocrafting mode" in Detector, to check if an item is being crafted. If no item is specified, it'll emit a
  signal if anything is crafting (raoulvdberge)
- Added an option for the Crafter to trigger autocrafting with a redstone signal (raoulvdberge)
- Optimized crafting pattern loading (raoulvdberge)
- Updated to Forge 2084 (raoulvdberge)

### 1.0

**NOTE:** Due to the new crafting system, all Crafting Patterns made before 1.0 have to be re-made.

- Implemented multithreaded autocrafting (raoulvdberge)
- Processing patterns now hold their items back for pushing until all the required items are gathered from the system (
  raoulvdberge)
- Fixed item and fluid storage stored count having incorrect values at times (raoulvdberge)
- Reworked Crafting Monitor GUI (raoulvdberge)
- Fixed problems relating to Crafting Upgrade (scheduling a task wrongly, blocking other tasks, etc) (raoulvdberge)
- Interface now supports Crafting Upgrade (raoulvdberge)
- When shift clicking a recipe in the Crafting Grid, the player inventory is now leveraged as well (raoulvdberge)
- Fixed machines breaking on long distances (raoulvdberge)
- Fixed Controller rebuilding network graph on energy change (raoulvdberge)
- Fixed fluids not caring about NBT tags (raoulvdberge)
- Fixed fluids that have less than 1 bucket stored render only partly in Fluid Grid (raoulvdberge)
- Fixed Fluid Interface voiding bucket when shift clicking to out slot (raoulvdberge)
- Fixed wrong machine connection logic (raoulvdberge)
- Updated to Forge 2077 (raoulvdberge)

### 0.9.4

- Little fixes in German translation (ThexXTURBOXx)
- Fixed mod not working without JEI (raoulvdberge)
- Reverted network changes that caused buggy behavior (raoulvdberge)
- Reduced explosion radius when multiple controllers are connected to the same network (raoulvdberge)

### 0.9.3

- Updated German translation for Fluid Storage (0blu)
- Updated Dutch translation for Fluid Storage (raoulvdberge)
- Added Chinese translation (TartaricAcid)
- Added Crafting Tweaks integration (blay09)
- Reworked storage network code, should fix weird machine disconnection issues (raoulvdberge)
- Fixed that the Fluid Storage Disk recipe returns an invalid disk (raoulvdberge)

### 0.9.2

**Bugfixes**

- Fixed not being able to take out items from Wireless Grid cross-dimensionally

### 0.9.1

**Bugfixes**

- Fixed server crash with Grid

### 0.9

**Bugfixes**

- Fixed crash with Grid
- Fixed Grid Filter only updating the Grid when reopening the GUI
- Fixed Wireless Grid not working cross dimensionally
- Fixed Grid not displaying items after changing redstone mode
- Fixed Wireless Transmitter crashing when it is transmitting to a removed dimension
- Fixed disassembling stacked Storage Blocks only returns 1 set of items
- Priority field and detector amount field can now display 4 digits at a time

**Features**

- Added fluid storage
- Added Russian translation by CorwinTheCat
- Energy usage of Wireless Grid is now configurable
- Wireless Transmitters can now only be placed on Cable

### 0.8.20

**Bugfixes**

- Fixed crash with Grid

### 0.8.19

**Bugfixes**

- Fixed item duplication bug with External Storage
- Fixed External Storage taking too long to update storage
- Fixed crash with Grid
- Fixed crash when shift clicking unsupported item in a slot

### 0.8.18

**Bugfixes**

- Fixed Detector mode not persisting
- Fixed bug where scrollbar didn't scroll correctly and thus hiding some items
- Fixed Network Transmitter not dropping inventory when broken

### 0.8.17

**Bugfixes**

- Fixed Grid causing sorting lag on the client

### 0.8.16

**Bugfixes**

- Fixed issue with IC2 integration causing console spam
- Slight performance increase and network efficiency improvement in all GUI's
- Slight performance increase in Grid GUI
- Fixed not being able to change some configs in blocks
- Fixed serverside configs not syncing up with clientside
- Fixed not being able to move inventory items in Grid GUI's to hotbar via the number keys
- Fixed Relays when being in "Ignore Redstone" mode using up energy
- Fixed Crafter facing bottom side on placement
- Improved collisions of Cable parts
- You now have to click the actual cable part head in order to get the GUI open

**Features**

- Added German translation by ChillUpX
- Grid Filters can now only filter 9 items, but, Grids take 4 filters now instead
- Grid Filters can now be configured to compare on NBT and/ or damage
- It is now possible to shift click items to the Storage Device filters
- Updated to Forge 2046
- Updated Tesla
- Java 8 is now a requirement
- Added MCMultiPart integration for Cable Parts

### 0.8.15

**Bugfixes**

- Fixed server startup crash

### 0.8.14

**Features**

- Added Interdimensional Upgrade so the Network Transmitter can work over different dimensions

### 0.8.13

**Bugfixes**

- Fixed rendering crash with Disk Drive
- Fixed crash when quickly toggling sorting direction in Grid
- Fixed not being able to clear exporter row in interface

**Features**

- Added config option to set the base energy usage of the Controller (default is 0)
- Added Grid Filter item to filter items in any Grid
- Added support for processing patterns with big stacksizes
- Added Network Transmitter, Network Receiver and Network Cards
- The slot where the Wireless Grid is in in the Wireless Grid GUI is now disabled, so the item can't be thrown out of
  the inventory by accident
- Changed Relay recipe to use redstone torch instead of Basic Processor
- Placed machines now face the block they are placed on, like hoppers

**NOTE:** Config change: the config options for the energy capacity of the Controller and wether the Controller uses
energy are now in a different config category called "controller", if you changed these config options, don't forget the
change it under the new category.

### 0.8.12

**Bugfixes**

- Fixed dupe bug when shift clicking output slot in grid

### 0.8.11

**Bugfixes**

- Fixed minor dupe bug with JEI transferring
- Fixed exporter crafting upgrades taking priority over other tasks
- Solderer upgrades go to upgrades slots first now when shift clicking
- Fixed NPE with incorrectly initialized disks
- Fixed not being able to take out items of Grid 2K16
- Fixed not being able to start autocrafting for certain items (most notably IC2 items)

**Features**

- Added X button to Processing Pattern Encoder to clear configuration of inputs and outputs
- Added Grid view toggle buttons (regular, craftable items only, no craftable items)
- Added ability to shift click items into Importer, Exporter, Constructor, Destructor and Detector to set up whitelist /
  blacklist configurations easier
- Re-added opposite facing on shift click functionality
- Updated to Forge 2014

### 0.8.10

**Bugfixes**

- Fixed not being able to get some items out of Grid
- Fixed slight glitch in Constructor and Destructor model

### 0.8.9

**Bugfixes**

- Fixed bug where Grid crafting doesn't handle remainder sometimes
- Fixed caching issues with External Storage
- Fixed possible crash with Disk Drives

**Features**

- Added a model for the Constructor
- Added a model for the Destructor
- Wireless Transmitters next to each other without any cable or without being connected to a machine won't work anymore,
  they need to be explictly connected to a cable or other machine
- Some models / texture tweaks

### 0.8.8

**Bugfixes**

- Use ore dictionary for recipes with glass
- Fixed solderer not working with automation anymore

**Features**

- Texture tweaks

### 0.8.7

**Bugfixes**

- Improved detector model, add a better hitbox for it
- Improved the Wireless Transmitter texture
- Wireless Transmitter is now only bright red when connected
- Fixed crash with External Storage
- Fixed Detector not unpowering when disconnected from the network
- Made the Solderer beams be bright red when they are working
- Added better hitbox for the Solderer

### 0.8.6

**Bugfixes**

- Fixed External Storage disconnecting on world reload
- Fixed External Storage not updating correctly
- Fixed wireless signal starting from Controller instead of per Wireless Transmitter individually
- Fixed Controller's redstone state not saving
- Fixed crafting tasks not saving properly
- Huge performance improvements to large storage networks

**Features**

- Re-added Controllers exploding when two of them are connected to the same network
- Limited some blocks to only have a direction on the x-axis
- Decreased amount of block updates significantly
- Added new textures
- Added model for External Storage
- Added model for Importer
- Added model for Exporter
- Added model for Detector
- Removed opposite facing on placement mechanic
- Removed Quartz Enriched Iron Block

### 0.8.5

**Bugfixes**

- Fixed crash when Tesla API is not installed

### 0.8.4

**Bugfixes**

- Removed delay until grid items are visible
- Performance improvements

**Features**

- Added a debug storage disk
- Added tooltip to solderer progress bar that shows progress percentage
- Added support for the Tesla energy system
- Added support for the IC2 (EU) energy system
- Added a Portuguese (Brazilian) translation by ChaoticTabris
- Tweaked grid GUI

### 0.8.3

**Bugfixes**

- Fixed drawer controllers not working with external storage
- Fixed right click taking 64 items instead of 32 items

### 0.8.2

**Bugfixes**

- Fixed not being able to take items sometimes

**Features**

- It is now possible to use middle click multiple times for the same item in grid
- Made the mod configurable with a config file

### 0.8.1

**Bugfixes**

- Fixed upgrades from interface not dropping
- Fixed lag caused by constantly rebuilding storage

### 0.8

**Bugfixes**

- Fixed solderer not using extra RF/t with upgrades

**Features**

- Recompile for Minecraft 1.10

### 0.7.19

**Bugfixes**

- Fixed controller being buggy with reconnecting
- Fixed controller texture updating too slow when energy changes
- Fixed not being able to take item from grid at times
- Fixed external storage on storage drawer sending an itemcount of 0 over

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
