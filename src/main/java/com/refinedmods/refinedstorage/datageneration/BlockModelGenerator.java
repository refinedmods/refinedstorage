package com.refinedmods.refinedstorage.datageneration;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.block.ControllerBlock;
import com.refinedmods.refinedstorage.block.DetectorBlock;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.util.BlockColorMap;
import com.refinedmods.refinedstorage.util.ColorMap;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockModelGenerator extends BlockStateProvider {
    private static final ResourceLocation BOTTOM = new ResourceLocation(RS.ID, "block/bottom");

    private final BlockModels models;

    public BlockModelGenerator(PackOutput output, String id, ExistingFileHelper existingFileHelper) {
        super(output, id, existingFileHelper);
        models = new BlockModels(this);
    }

    @Override
    protected void registerStatesAndModels() {
        genNorthCutoutModels(RSBlocks.GRID, false);
        genNorthCutoutModels(RSBlocks.CRAFTING_GRID, false);
        genNorthCutoutModels(RSBlocks.PATTERN_GRID, false);
        genNorthCutoutModels(RSBlocks.FLUID_GRID, false);
        genNorthCutoutModels(RSBlocks.CRAFTING_MONITOR, false);
        genNorthCutoutModels(RSBlocks.CRAFTER_MANAGER, false);
        genNorthCutoutModels(RSBlocks.DISK_MANIPULATOR, true);
        genControllerModels(RSBlocks.CONTROLLER);
        genControllerModels(RSBlocks.CREATIVE_CONTROLLER);
        genCrafterModels();
        genCubeAllCutoutModels(RSBlocks.RELAY);
        genCubeAllCutoutModels(RSBlocks.NETWORK_TRANSMITTER);
        genCubeAllCutoutModels(RSBlocks.NETWORK_RECEIVER);
        genSecurityManagerModels();
        genDetectorModels();
        genWirelessTransmitterModels();
    }

    private void genWirelessTransmitterModels() {
        RSBlocks.WIRELESS_TRANSMITTER.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = RSBlocks.WIRELESS_TRANSMITTER.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.wirelessTransmitterBlock(block, state -> {
                if (Boolean.FALSE.equals(state.getValue(NetworkNodeBlock.CONNECTED))) {
                    return models.createWirelessTransmitterNonEmissiveModel(
                            "block/" + folderName + "/disconnected",
                            resourceLocation(folderName, "cutouts/disconnected")
                    );
                } else {
                    ModelFile model = models.createWirelessTransmitterModel(
                            "block/" + folderName + "/" + color,
                            resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 0);
        });
    }

    private void genDetectorModels() {
        RSBlocks.DETECTOR.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = RSBlocks.DETECTOR.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (Boolean.FALSE.equals(state.getValue(DetectorBlock.POWERED))) {
                    return models.createDetectorNonEmissiveModel(
                            "block/" + folderName + "/off",
                            resourceLocation(folderName, "cutouts/off")
                    );
                } else {
                    ModelFile model = models.createDetectorModel(
                            "block/" + folderName + "/" + color,
                            resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            });
        });
    }

    private void genSecurityManagerModels() {
        RSBlocks.SECURITY_MANAGER.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = RSBlocks.SECURITY_MANAGER.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.horizontalRSBlock(block, state -> {
                if (Boolean.FALSE.equals(state.getValue(NetworkNodeBlock.CONNECTED))) {
                    return models.createCubeCutoutNonEmissiveModel(
                            "block/" + folderName + "/disconnected",
                            BOTTOM,
                            BOTTOM,
                            resourceLocation(folderName, "top"),
                            resourceLocation(folderName, "cutouts/top_disconnected"),
                            resourceLocation(folderName, "right"),
                            resourceLocation(folderName, "cutouts/right_disconnected"),
                            resourceLocation(folderName, "left"),
                            resourceLocation(folderName, "cutouts/left_disconnected"),
                            resourceLocation(folderName, "front"),
                            resourceLocation(folderName, "cutouts/front_disconnected"),
                            resourceLocation(folderName, "back"),
                            resourceLocation(folderName, "cutouts/back_disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeCutoutModel(
                            "block/" + folderName + "/" + color,
                            BOTTOM,
                            BOTTOM,
                            resourceLocation(folderName, "top"),
                            resourceLocation(folderName, "cutouts/top" + "_" + color),
                            resourceLocation(folderName, "right"),
                            resourceLocation(folderName, "cutouts/right" + "_" + color),
                            resourceLocation(folderName, "left"),
                            resourceLocation(folderName, "cutouts/left" + "_" + color),
                            resourceLocation(folderName, "front"),
                            resourceLocation(folderName, "cutouts/front" + "_" + color),
                            resourceLocation(folderName, "back"),
                            resourceLocation(folderName, "cutouts/back" + "_" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 180);
        });
    }

    private <T extends Block> void genCubeAllCutoutModels(BlockColorMap<T> blockMap) {
        blockMap.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = blockMap.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (Boolean.FALSE.equals(state.getValue(NetworkNodeBlock.CONNECTED))) {
                    return models.createCubeAllCutoutNonEmissiveModel(
                            "block/" + folderName + "/disconnected",
                            resourceLocation(folderName, folderName),
                            resourceLocation(folderName, folderName),
                            resourceLocation(folderName, "cutouts/disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeAllCutoutModel(
                            "block/" + folderName + "/" + color,
                            resourceLocation(folderName, folderName),
                            resourceLocation(folderName, folderName),
                            resourceLocation(folderName, "cutouts/" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            });
        });
    }

    private void genCrafterModels() {
        RSBlocks.CRAFTER.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = RSBlocks.CRAFTER.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.anyDirectionalRSBlock(block, state -> {
                if (Boolean.FALSE.equals(state.getValue(NetworkNodeBlock.CONNECTED))) {
                    return models.createCubeCutoutNonEmissiveModel(
                            "block/" + folderName + "/disconnected",
                            BOTTOM,
                            BOTTOM,
                            resourceLocation(folderName, "top"),
                            resourceLocation(folderName, "cutouts/top_disconnected"),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_disconnected"),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_disconnected"),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_disconnected"),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_disconnected")
                    );
                } else {
                    ModelFile model = models.createCubeCutoutModel(
                            "block/" + folderName + "/" + color,
                            BOTTOM,
                            BOTTOM,
                            resourceLocation(folderName, "top"),
                            resourceLocation(folderName, "cutouts/top_" + color),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_" + color),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_" + color),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_" + color),
                            resourceLocation(folderName, "side"),
                            resourceLocation(folderName, "cutouts/side_" + color)
                    );

                    simpleBlockItem(block, model);
                    return model;
                }
            }, 180);
        });
    }

    private <T extends Block> void genControllerModels(BlockColorMap<T> blockMap) {
        blockMap.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = RSBlocks.CONTROLLER.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            models.simpleBlockStateModel(block, state -> {
                if (state.getValue(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.OFF)) {
                    return models.createCubeAllCutoutNonEmissiveModel(
                            "block/" + folderName + "/off",
                            resourceLocation(folderName, "off"),
                            resourceLocation(folderName, "off"),
                            resourceLocation(folderName, "cutouts/off")
                    );
                } else if (state.getValue(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_OFF)) {
                    return models.createControllerNearlyCutoutModel(
                            "block/" + folderName + "/nearly_off",
                            resourceLocation(folderName, "off"),
                            resourceLocation(folderName, "on"),
                            resourceLocation(folderName, "cutouts/nearly_off"),
                            resourceLocation(folderName, "cutouts/nearly_off_gray")
                    );
                } else if (state.getValue(ControllerBlock.ENERGY_TYPE).equals(ControllerBlock.EnergyType.NEARLY_ON)) {
                    return models.createControllerNearlyCutoutModel(
                            "block/" + folderName + "/nearly_on",
                            resourceLocation(folderName, "off"),
                            resourceLocation(folderName, "on"),
                            resourceLocation(folderName, "cutouts/nearly_on"),
                            resourceLocation(folderName, "cutouts/nearly_on_gray")
                    );
                } else {
                    ModelFile model = models.createCubeAllCutoutModel(
                            "block/" + folderName + "/" + color,
                            resourceLocation(folderName, "off"),
                            resourceLocation(folderName, "on"),
                            resourceLocation(folderName, "cutouts/" + color)
                    );

                    final ResourceLocation energyType = new ResourceLocation(RS.ID, "energy_type");
                    itemModels().getBuilder(BuiltInRegistries.BLOCK.getKey(block).getPath())
                            .override()
                            .predicate(energyType, 0)
                            .model(models().getExistingFile(
                                    new ResourceLocation(RS.ID, "block/" + folderName + "/off")
                            )).end().override()
                            .predicate(energyType, 1)
                            .model(models().getExistingFile(
                                    new ResourceLocation(RS.ID, "block/" + folderName + "/nearly_off")
                            )).end().override()
                            .predicate(energyType, 2)
                            .model(models().getExistingFile(
                                    new ResourceLocation(RS.ID, "block/" + folderName + "/nearly_on")
                            )).end().override()
                            .predicate(energyType, 3)
                            .model(model).end();

                    return model;
                }
            });
        });
    }

    private <T extends Block> void genNorthCutoutModels(BlockColorMap<T> blockMap, boolean useLoader) {
        blockMap.forEach((color, registryObject) -> {
            Block block = registryObject.get();
            String folderName = blockMap.get(ColorMap.DEFAULT_COLOR).getId().getPath();

            ModelFile disconnected = models.createCubeNorthCutoutNonEmissiveModel(
                    "block/" + folderName + "/disconnected",
                    BOTTOM,
                    resourceLocation(folderName, "top"),
                    resourceLocation(folderName, "front"),
                    resourceLocation(folderName, "back"),
                    resourceLocation(folderName, "right"),
                    resourceLocation(folderName, "left"),
                    resourceLocation(folderName, "right"),
                    resourceLocation(folderName, "cutouts/disconnected")
            );
            ModelFile connected = models.createCubeNorthCutoutModel(
                    "block/" + folderName + "/" + color,
                    BOTTOM,
                    resourceLocation(folderName, "top"),
                    resourceLocation(folderName, "front"),
                    resourceLocation(folderName, "back"),
                    resourceLocation(folderName, "right"),
                    resourceLocation(folderName, "left"),
                    resourceLocation(folderName, "right"),
                    resourceLocation(folderName, "cutouts/" + color)
            );

            //generate Item Model
            simpleBlockItem(block, connected);

            if (useLoader) {
                models.customLoaderRSBlock(block, resourceLocation(folderName, "loader"), connected, disconnected);
            } else {
                models.horizontalRSBlock(block, state -> {
                    if (Boolean.FALSE.equals(state.getValue(NetworkNodeBlock.CONNECTED))) {
                        return disconnected;
                    } else {
                        return connected;
                    }
                }, 180);
            }
        });
    }

    private ResourceLocation resourceLocation(String folderName, String name) {
        return new ResourceLocation(RS.ID, "block/" + folderName + "/" + name);
    }
}
