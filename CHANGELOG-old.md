# Refined Storage Changelog

### 1.4.20

- Fixed crash relating to MCMP (raoulvdberge)

### 1.4.19

- Fixed bug where players couldn't place regular blocks next to secured networks (raoulvdberge)
- Fixed crashes relating to scrollbar in GUIs (raoulvdberge)
- Fixed bug where disks have to be re-inserted in the Disk Drive in order to work again after rejoining a chunk (
  raoulvdberge)
- Implemented controller update throttling, should fix lag issues with controllers that constantly turn off and on (
  raoulvdberge)

### 1.4.18

- Fixed Solderer crashing (raoulvdberge)
- Fixed Interface extracting from itself when trying to keep items in stock (raoulvdberge)
- Fixed Solderer being able to work with insufficient ingredients (raoulvdberge)
- Fixed Disk Drive giving incorrect capacity for creative and debug storage disks (raoulvdberge)
- Fixed External Storage crashing (raoulvdberge)
- Fixed crash when node data mismatches between world and dat file (raoulvdberge)
- The Relay now reacts instantly to a redstone signal again, removed throttling for it (raoulvdberge)

### 1.4.17

- Fixed Filters not persisting correctly in Wireless Grid, Wireless Crafting Monitor and Portable Grid (raoulvdberge)
- Fixed Disk Drive recipe not using ore dictionary for chest (raoulvdberge)
- Fixed a bug where loading nodes would abort when a single node has an error while reading (raoulvdberge)

### 1.4.16

- The Portable Grid now doesn't despawn anymore when dropped in the world (raoulvdberge)
- Fixed bug where oredict autocrafting didn't work in some cases (way2muchnoise)

### 1.4.15

- Updated Storage Drawers API, fixes crashes (raoulvdberge)

### 1.4.14

- Fixed Solderer not accepting books made in anvil (raoulvdberge)

### 1.4.13

- Fixed Portable Grid model (raoulvdberge, CyanideX)
- The Portable Grid now has an indicator whether it's connected or disconnected and shows the disk (raoulvdberge,
  CyanideX)
- Fixed ore dictionary causing problems with Solderer (raoulvdberge)
- Fixed ore dictionary items not showing up in JEI for the Solderer (raoulvdberge)
- Removed Quartz Enriched Iron ore dictionary entry (raoulvdberge)

### 1.4.12

- Updated Forge to 2315 (raoulvdberge)
- Updated JEI to 4.5.0 (raoulvdberge)
- Removed Collosal Chests integration (raoulvdberge)
- You can now shift click items from the Grid crafting slots to the player inventory when the Grid is disconnected (
  raoulvdberge)
- Added Korean translation (01QueN10)
- Fixed error logs when watching a Controller when a network changes (raoulvdberge)
- Implemented block update throttling when network turns on and off (raoulvdberge)

### 1.4.11

- Removed debug log configuration option, as it's no longer needed (raoulvdberge)
- Removed "autocraft on redstone signal" option in the Crafter, use an External Storage in combination with an Interface
  with the Crafting Upgrade instead (raoulvdberge)
- Fixed bug where storages that are removed remain visible (raoulvdberge)
- Fixed bug where the GUI didn't close when a block is broken, causing a dupe bug with the Portable Grid (raoulvdberge)
- Added support for External Storage on Interfaces and other Refined Storage blocks, so you can keep items in stock
  easier (raoulvdberge)
- You now have to sneak to place the Portable Grid in the world (raoulvdberge)
- The Machine Casing now requires 1 piece of stone in the middle (raoulvdberge)
- Changed recipe of Disk Drive to no longer require a Solderer (raoulvdberge)
- Changed recipe of Interface to no longer require a Basic Processor, but a Machine Casing instead (raoulvdberge)

### 1.4.10

- Improved performance of network scanning (raoulvdberge)
- Fixed crash when attempting to get direction of a node (raoulvdberge)
- Fixed bug where some network parts don't want to connect to the storage system (raoulvdberge)

### 1.4.9

- Fixed bug where inventory data was lost sometimes upon opening the world (raoulvdberge)

### 1.4.8

- Fixed missing config categories in ingame config (raoulvdberge)
- Fixed Controller not working anymore after changing redstone setting (raoulvdberge)
- Fixed crash when placing or destroying network blocks (raoulvdberge)

### 1.4.7

- Fixed bug where Portable Grid would dupe in inventory (raoulvdberge)
- Worked around an autocrafting bug to prevent crashes (raoulvdberge)
- Added config option "debugLog" that logs diagnostic info to help developers to fix the inventory loss bug, please
  enable it if you are experiencing this issue (raoulvdberge)

### 1.4.6

- Performance improvement to network scanning (raoulvdberge)
- Removed debug output from 1.4.5 (raoulvdberge)
- Fixed Wrench opening GUIs while performing action (raoulvdberge)
- Fixed client Grid GUI clearing and causing crashes after starting an autocrafting request (raoulvdberge)

### 1.4.5

- Updated Forge to 2296 (raoulvdberge)
- Added Portable Grid (raoulvdberge)
- Added OpenComputers integration (thraaawn)
- Fixed Crafting Tweaks buttons positioned wrongly (blay09)
- Fixed Crafting Tweaks keybindings interfering with RS keybindings (blay09)
- Fixed crash when updating storages (raoulvdberge)
- Fixed no tooltips for fluid filter slots (raoulvdberge)
- Fixed Disk Manipulator in fluid mode not showing fluids (raoulvdberge)
- Fixed dupe bug in the Interface (raoulvdberge)
- Removed ticking tile entities, every tile entity in RS is non-ticking now (raoulvdberge)

### 1.4.4

- Updated Forge to 2284 (raoulvdberge)
- Fixed Disk Manipulator crashing due to empty stack (raoulvdberge)
- Fixed issue where empty stacks show up in the system (raoulvdberge)
- Fixed Storage Monitor not respecting security settings (raoulvdberge)

### 1.4.3

- Updated Forge to 2282 (raoulvdberge)
- Updated JEI version (raoulvdberge)
- Updated MCMultiPart version (raoulvdberge)
- Storage Monitors don't render any quantity text when no item is specified to monitor anymore (raoulvdberge)
- Fixed bug where disks in Disk Drive didn't respect access type or void excess stacks option (raoulvdberge)
- Fixed crash in Disk Manipulator (raoulvdberge)
- Fixed oredict not working (raoulvdberge)
- You can now shift click Grid Filters into a Grid instead of manually inserting them (raoulvdberge)
- The Solderer inventory isn't sided anymore (raoulvdberge)
- You can now use up and down arrows to scroll through Grid search history (raoulvdberge)
- Fixed Grid crash (raoulvdberge)
- Fixed Fluid Grid not formatting large quantities correctly (raoulvdberge)
- Small performance improvement: only sort the storages when needed (raoulvdberge)
- Display progress bar on JEI recipes for the Solderer (raoulvdberge)
- Shift clicking patterns in the Pattern Grid now puts the patterns in the pattern slot (raoulvdberge)

### 1.4.2

- Updated Forge to 2261 (raoulvdberge)
- Implemented support for the Forge update JSON system (raoulvdberge)
- Added integration for MCMultiPart, this is an optional dependency (raoulvdberge)
- You can now specify more items to export, import, filter, etc by inserting the Filter item (raoulvdberge)
- Fixed a crash with the Constructor (raoulvdberge)
- Fixed Crafting Pattern model (pauljoda)
- Fixed Quartz Enriched Iron and the block form of it not having an oredictionary entry (raoulvdberge)
- Fixed crash in storage cache (raoulvdberge)
- Fixed slow oredict comparisons causing TPS lag (raoulvdberge)
- The Detector no longer outputs a strong redstone signal (raoulvdberge)
- Made the keybinding to focus on the Grid search bar configurable (way2muchnoise)
- Autocrafting bugfixes (way2muchnoise)
- Fire event on completion of an autocrafting task (way2muchnoise)
- Fire playerCrafting event when shift clicking in the grid (way2muchnoise)
- Fixed controller model warning during launch (raoulvdberge)
- Improved memory usage of some models (raoulvdberge)
- Fixed not rendering some tooltips correctly (raoulvdberge)
- Removed support for the Deep Storage Unit API (raoulvdberge)
- Performance improvements related to storage inserting and extracting (raoulvdberge)
- Fixed crash with External Storage (raoulvdberge)
- Fixed Interface duping items on extract-only storages (raoulvdberge)
- Fixed controls menu showing unlocalized text for Refined Storage keybindings (raoulvdberge)

### 1.4.1

- Added Storage Monitor (raoulvdberge)
- Fixed Processing Pattern Encoder and Security Manager recipes not supporting oredict workbench and chest (VT-14)
- Fixed network nodes not respecting redstone mode (raoulvdberge)
- Fixed "Clear" and "Create Pattern" buttons not working correctly when using Grid Filter tabs (raoulvdberge)
- Fixed Wrench in Dismantling Mode voiding Storage Block contents (raoulvdberge)
- Fixed OPs not having global permissions on secured storage systems (raoulvdberge)
- Fixed crash when Destructor tries to break secured network block (raoulvdberge)
- Fixed Fluid Interface not dropping inventory contents (raoulvdberge)
- Fixed Disk Manipulator crash (raoulvdberge)
- Updated Forge to 2232 (raoulvdberge)

### 1.4

- Added Security Manager (raoulvdberge)
- Added Security Card (raoulvdberge)
- Added Wireless Fluid Grid (raoulvdberge)
- Added craft-only toggle to Exporter (raoulvdberge)
- Added Reader (raoulvdberge)
- Added Writer (raoulvdberge)
- Added blocking mode to patterns in autocrafting (InusualZ)
- Added Grid size toggle (stretched, small, medium, large) (raoulvdberge)
- Added dismantling mode to the Wrench (raoulvdberge)
- Added Block of Quartz Enriched Iron (raoulvdberge)
- You can now bind multiple crafters with the same pattern to machines, to spread or balance out autocrafting (
  way2muchnoise)
- Autocrafting bugfixes (way2muchnoise)
- Fixed Regulator mode not working (InusualZ)
- Fixed Fluid Interface still being sided (raoulvdberge)
- Fixed Constructor not working on Botania flowers (raoulvdberge)
- Fixed Wireless Transmitter working even if it was disabled with redstone mode (raoulvdberge)
- Fixed Solderer not accepting books created in an Anvil (raoulvdberge)
- Fixed bug where network machines stopped working on chunk borders (raoulvdberge)
- Fixed memes not working (raoulvdberge)
- Fixed External Storage crashes (raoulvdberge)
- Fixed Constructor in liquid mode being able to place fluids <1000 mB (raoulvdberge)
- Fixed Solderer recipe conflicts, allowing for easier automation (raoulvdberge)
- Fluid Grid now first tries to get buckets from your inventory instead of the storage (raoulvdberge)
- Remove sidedness from fluid interface (way2muchnoise)
- The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time (raoulvdberge)
- Ignore damage for damageable items when transferring into crafting grid (way2muchnoise)
- Ignore tags from given items when transferring into crafting grid (way2muchnoise)
- Add Ore Dictionary grid filter (use $ as prefix like in JEI) (way2muchnoise)
- Made sure External Storage always has the correct inventory in world (raoulvdberge)
- Using tab in a grid that isn't in autoselected mode will focus on the search box (raoulvdberge)
- Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance (
  way2muchnoise)
- Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it (raoulvdberge)
- Increased size of Detector textbox (way2muchnoise)
- Fixed stack upgrades not working in exporter when stack size is 16 (way2muchnoise)
- Fixed crash when rotating External Storage (raoulvdberge)
- Fixed disk textures not working on latest Forge (raoulvdberge)
- Handle breaking and placing blocks better for Constructor and Destructor (way2muchnoise)
- Updated Forge to 2226 (raoulvdberge)
- Updated Chinese translation (TartaricAcid)
- Added filtering slots for the Crafting Monitor (raoulvdberge)
- Added way to hide tasks created in an automated way in the Crafting Monitor (raoulvdberge)
- Added Grid sorting by ID (way2muchnoise)
- Added Solderer particles (raoulvdberge)
- Fixed crash when placing head with Constructor (raoulvdberge)
- Converting blocks instead of ingots to Printed Processors is now a little faster (raoulvdberge)
- Pressing shift while starting a crafting task will skip the crafting preview (raoulvdberge)

### 1.3.5

- Fixed TPS lag on very large crafting tasks (way2muchnoise)
- Fixed not being able to use autocrafting on some EnderIO items (way2muchnoise)
- Fixed server crash with ore dictionary checks (way2muchnoise)
- Fixed Controller not using energy (raoulvdberge)
- Fixed dupe bug when inserting bucket in Fluid Grid (raoulvdberge)
- Fixed not being able to start autocrafting for storage disks (raoulvdberge)
- Fixed oredict button not having the correct position on a small resolution (raoulvdberge)
- Fixed Constructor not using Crafting Upgrade when in item dropping mode (InusualZ)
- Updated French translation (Leventovitch)
- Added regulator mode to Exporter (InusualZ)

### 1.3.4

- Added option to check for oredict in the Grid Filter (raoulvdberge)
- Added option to use a mod filter in the Grid Filter (raoulvdberge)
- Added option to use a whitelist or blacklist in the Grid Filter (raoulvdberge)
- Added Grid tabs using Grid Filters (raoulvdberge)
- The Grid now resizes based on screen size (max rows can be configured) (raoulvdberge)
- Added configuration option to enable large fonts in Grid (raoulvdberge)
- Made Solderer tooltip less big (raoulvdberge)
- Fixed bug with opening a network item with food in offhand (raoulvdberge)
- Fixed not respecting "Extract only" option for storages (raoulvdberge)
- Made the Interface sideless, you can just insert or extract from any side (raoulvdberge)
- Fixed a few autocrafting bugs (way2muchnoise)
- Fixed a crash with the Disk Manipulator (way2muchnoise)

### 1.3.3

- Updated Forge to 2188 (raoulvdberge)
- Fixed not being able to start a crafting task (raoulvdberge)

### 1.3.2

- Fixed being able to exceed max stack size while shift clicking (raoulvdberge)
- Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool (raoulvdberge)
- Fixed client crash when placing network blocks (raoulvdberge)

### 1.3.1

- Updated Forge to 2180 (raoulvdberge)
- Made Upgrades stackable (raoulvdberge)
- Fixed Disk Drive not noticing a Storage Disk being shift clicked out of the GUI (raoulvdberge)

### 1.3

- Port to Minecraft 1.11 (raoulvdberge, way2muchnoise)
- Removed RF support, use Forge Energy instead (raoulvdberge)
- Removed IC2 support (raoulvdberge)
- Removed MCMultiPart support (will be re-added as soon as MCMultiPart for MC 1.11 is available) (raoulvdberge)

### 1.2.26

- Fixed Interface duping items on extract-only storages (raoulvdberge)
- Fixed calculating crafting resources for more than 9 unique items, for addon mods (ExpensiveKoala)

### 1.2.25

- Fire event on completion of an autocrafting task (way2muchnoise)
- Fire playerCrafting event when shift clicking in the grid (way2muchnoise)
- Allow INodeNetwork instances to return an ItemStack for display in Controller GUI (bmwalter68)

### 1.2.24

- Made the keybinding to focus on the Grid search bar configurable (way2muchnoise)
- Autocrafting bugfixes (way2muchnoise)

### 1.2.23

- Implemented support for the Forge update JSON system (raoulvdberge)
- Fixed crash in storage cache (raoulvdberge)
- Fixed Crafting Pattern model (pauljoda)
- Fixed Constructor not working on Botania flowers (raoulvdberge)
- Fixed Disk Manipulator crash (raoulvdberge)
- Fixed slow oredict comparisons causing TPS lag (raoulvdberge)
- The Detector no longer outputs a strong redstone signal (raoulvdberge)

### 1.2.22

- Fixed recipe for Processing Pattern Encoder not using oredictionary for the workbench (VT-14)
- Fixed Fluid Interface not dropping inventory contents (raoulvdberge)
- Fixed glitchy upgrade recipes in the Solderer (raoulvdberge)

### 1.2.21

- Fixed crash when placing head with Constructor (raoulvdberge)

### 1.2.20

- Fixed client side crash with cables (raoulvdberge)
- Added Solderer particles (raoulvdberge)
- Added Grid sorting by ID (way2muchnoise)

### 1.2.19

- Added integration for Collosal Chests for the External Storage, Importer and Exporter improving performance (
  way2muchnoise)
- Exposed the Network Card inventory of the Network Transmitter so other tiles can interact with it (raoulvdberge)
- Increased size of Detector textbox (way2muchnoise)
- Autocrafting bugfixes (way2muchnoise)
- Fixed stack upgrades not working in exporter when stack size is 16 (way2muchnoise)
- Fixed crash when rotating External Storage (raoulvdberge)
- Handle breaking and placing blocks better for Constructor and Destructor (way2muchnoise)
- Updated cable part back texture and Construction and Destruction Core textures (CyanideX)
- Updated Forge to 2221 (raoulvdberge)
- Fixed disk textures not working on latest Forge (raoulvdberge)
- Updated Chinese translation (TartaricAcid)

### 1.2.18

- Performance improvements with oredict autocrafting (way2muchnoise)
- Fixed client side crash with cable (raoulvdberge)
- Fixed client side crash with disk drive (raoulvdberge)
- Fixed crash with external storage in fluid mode (raoulvdberge)
- Fluid Grid now first tries to get buckets from your inventory instead of the storage (raoulvdberge)

### 1.2.17

- Ignore damage for damageable items when transferring into crafting grid (way2muchnoise)
- Ignore tags from given items when transferring into crafting grid (way2muchnoise)
- Remove sidedness from fluid interface (way2muchnoise)
- Using tab in a grid that isn't in autoselected mode will focus on the search box (raoulvdberge)
- Add Ore Dictionary grid filter (use $ as prefix like in JEI) (way2muchnoise)
- The Stack Upgrade in a Constructor in item dropping mode will drop stacks of items at a time (raoulvdberge)
- Fixed Constructor in liquid mode being able to place fluids <1000 mB (raoulvdberge)
- Fixed Solderer recipe conflicts, allowing for easier automation (raoulvdberge)
- Fixed machines not connecting with cable after Controller (raoulvdberge)

### 1.2.16

- Fixed an autocrafting regression (way2muchnoise)
- Fixed crash with External Storage (raoulvdberge)

### 1.2.15

- Autocrafting bugfixes (way2muchnoise)
- Fixed Grid Filter hiding everything when 2 or more items are in it (raoulvdberge)
- Fixed External Storage crash when breaking a connected inventory (raoulvdberge)

### 1.2.14

- Fixed server crash (way2muchnoise)

### 1.2.13

- Fixed memes not working (raoulvdberge)
- Fixed controller causing network rebuild on every neighbor change (raoulvdberge)
- Autocrafting bugfixes (way2muchnoise)
- Fixed Wireless Transmitter working even if it was disabled with redstone mode (raoulvdberge)
- Fixed Solderer not accepting books created in an Anvil (raoulvdberge)
- Made sure External Storage always has the correct inventory in world (raoulvdberge)

### 1.2.12

- Fixed TPS lag on very large crafting tasks (way2muchnoise)
- Fixed not being able to use autocrafting on some EnderIO items (way2muchnoise)
- Fixed not being able to start autocrafting for storage disks (raoulvdberge)
- Fixed oredict button not having the correct position on a small resolution (raoulvdberge)
- Fixed Constructor not using Crafting Upgrade when in item dropping mode (InusualZ)
- Updated French translation (Leventovitch)

### 1.2.11

- Made the Interface sideless, you can just insert or extract from any side (raoulvdberge)
- The Grid now resizes based on screen size (max rows can be configured) (raoulvdberge, way2muchnoise)
- Added configuration option to enable large fonts in Grid (raoulvdberge, way2muchnoise)

### 1.2.10

- A few autocrafting bugfixes (way2muchnoise)
- Fixed a crash with the Disk Manipulator (way2muchnoise)
- Fixed not respecting "Extract only" option for storages (raoulvdberge)
- Fixed bug with opening a network item with food in offhand (raoulvdberge)
- Fixed other fluid storages going negative when using void excess fluids option (raoulvdberge)
- Made Solderer tooltip less big (raoulvdberge)

### 1.2.9

- Updated Forge to 2185 (raoulvdberge)
- Fixed Wrench clearing NBT data when reset causing problems with Morph O Tool (raoulvdberge)

### 1.2.8

- Fixed autocrafting bugs (way2muchnoise)

### 1.2.7

- Updated German translation (LHS_Buster)
- Fixed not being able to place disks in Disk Drives on servers (raoulvdberge)

### 1.2.6

- Processing patterns can now insert buckets (way2muchnoise)
- Fixed crash with Exporters in fluid mode (raoulvdberge)
- Removed Solderer progress percentage text (raoulvdberge)

### 1.2.5

- The Constructor can now place fireworks (raoulvdberge)
- Added "View Recipes" JEI toggle in Solderer (way2muchnoise)
- Fixed a bunch of autocrafting bugs (way2muchnoise)
- Fixed Grid search not working correctly (raoulvdberge)
- Fixed items disappearing from Solderer inventory (way2muchnoise)
- Fixed being able to take fluids that have less than 1000 millibuckets filled in Fluid Grid (way2muchnoise)
- Fixed Constructor being able to place fluids that have less than 1000 millibuckets (way2muchnoise)
- Fixed Exporter and Importer not working properly with fluids (way2muchnoise)
- Fixed inserting new stack type with right click in Grid causing a desync (raoulvdberge)
- Fixed Constructor not calling block place event (raoulvdberge)
- Fixed shift clicking non disk items in the Disk Manipulator voiding them (way2muchnoise)
- Updated Forge to 2151 (way2muchnoise)

### 1.2.4

- Added tooltip search with # (raoulvdberge)
- Mod search can now also take mod name instead of just id (raoulvdberge)
- Fixed bug where Disk Manipulator doesn't save disks (raoulvdberge)
- Fixed Disk Drive stored quantity GUI text hovering over other text (raoulvdberge)
- Fixed External Storage being in item and fluid mode at the same time (raoulvdberge)
- Fixed Wrench working when player is not sneaking (raoulvdberge)
- Fixed External Storage cache counting items up when extracting (raoulvdberge)
- Fixed External Storage cache not working properly on Compacting Drawers (raoulvdberge)
- Removed ability to put External Storages on Refined Storage network blocks (raoulvdberge)

### 1.2.3

- Fixed fluid cache updating wrongly (raoulvdberge)
- Fixed Exporter scheduling too many crafting tasks (raoulvdberge)

### 1.2.2

- Various autocrafting fixes (way2muchnoise)
- Fixed item voiding when exporting to a chest with a storage in Extract Only mode (raoulvdberge)

### 1.2.1

- Added Wireless Crafting Monitor (with temporary textures) (raoulvdberge)
- Added support for JEI R and U keys in Grids (raoulvdberge)
- Fixed crafting upgrade having weird behavior (raoulvdberge)
- Fixed external storage not updating when loading chunk (raoulvdberge)
- Fixed external storage crash (raoulvdberge)
- Fixed weird autocrafting behavior (way2muchnoise)
- Removed controller explosions when multiple controllers are connected to the same network (raoulvdberge)
- You can now decompose storage disks if the item count is below zero by any chance (raoulvdberge)

### 1.2

- Added new autocrafting system (way2muchnoise)
- Added ore dictionary autocrafting (way2muchnoise)
- Added recipe transfer handler for Processing Pattern Encoder (way2muchnoise)
- Added void excess items functionality to storage blocks (geldorn, raoulvdberge, InusualZ)
- Added config option to configure RS to EU conversion rates (InusualZ)
- Added ability to toggle between insert and extract, only insert and only extract mode in storage blocks (InusualZ,
  raoulvdberge)
- Added Silk Touch Upgrade for Destructor (InusualZ)
- Added Fortune Upgrade for Destructor (InusualZ)
- Added ore dictionary compare toggle to storage I/O blocks (raoulvdberge)
- Added disk leds to Disk Drive block that shows the disks (raoulvdberge)
- Added disk leds to Disk Manipulator block that shows the disks (raoulvdberge)
- Added Wrench, has two modes: configuration saving / reading mode, and rotation mode (raoulvdberge)
- Changed storage GUIs (raoulvdberge)
- Changed default EU conversion rate to be 1:8 with RS (raoulvdberge)
- Controller sorts by energy usage in GUI (highest to lowest) (raoulvdberge)
- The Constructor can now drop items in the world (raoulvdberge)
- The Constructor can now place skulls (modmuss50)
- The Destructor can now pick up items in the world (InusualZ)
- Stack upgrade in Importer / Exporter in fluid mode and Fluid Interface now transfers 64 buckets at once (raoulvdberge)
- Detector without any filter will detect based on total items or fluids stored (raoulvdberge)
- Storage disks and storage blocks now don't despawn anymore when dropped in the world (raoulvdberge)
- Grid item and fluid quantity now only rounds to 1 digit after comma (raoulvdberge)
- Items count can no longer overflow, and will max out at the maximum integer value (raoulvdberge)
- Limited network transmitter usage to 1000 RS/t (raoulvdberge)
- Fixed lag issues caused by External Storage (raoulvdberge)
- Fixed resetting a stack of patterns yields 1 blank pattern (raoulvdberge)
- Fixed being able to pipe items in the export slots of the Interface (InusualZ)
- Fixed Interface being stuck when item isn't accepted in storage (InusualZ)
- Fixed items with colored name being uncolored in Grid (raoulvdberge)
- Fixed fluid rendering bugging out side buttons (raoulvdberge)
- Fixed item count going negative when using the Disk Manipulator (InusualZ)
- Fixed Storage Drawer quantities not updating properly on Void Drawers (geldorn)
- Fixed Disk Manipulator blocking items transferring in some cases (InusualZ)
- Fixed External Storage crafting recipe not supporting ore dictionary chests (raoulvdberge)
- Fixed when shift clicking crafting recipe and inventory is full items are dropping on the ground instead of going in
  the system (raoulvdberge)
- Fixed glitchy rendering of cable parts in item form (raoulvdberge)
- Fixed Destructor being able to break bedrock (InusualZ)
- Fixed External Storage thinking that items are inserted in Extra Utilities Trash Cans (InusualZ)
- Fixed Grid quantities being unreadable when using unicode font (raoulvdberge)
- Fixed disconnecting when Storage Disk or Storage Block is too big (raoulvdberge)
- Updated Storage Drawers API (raoulvdberge)

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
