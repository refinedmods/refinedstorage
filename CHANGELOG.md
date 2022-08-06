# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
