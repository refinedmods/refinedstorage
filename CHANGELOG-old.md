# Refined Storage Changelog

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
