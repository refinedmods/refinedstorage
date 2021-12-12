package com.refinedmods.refinedstorage.command.pattern;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.AllowedTagList;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class PatternDumpCommand implements Command<CommandSourceStack> {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return Commands.literal("dump")
            .executes(new PatternDumpCommand());
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().getPlayerOrException().getInventory().getSelected();

        if (stack.getItem() instanceof PatternItem) {
            boolean processing = PatternItem.isProcessing(stack);
            boolean exact = PatternItem.isExact(stack);
            AllowedTagList allowedTagList = PatternItem.getAllowedTags(stack);

            ICraftingPattern pattern = PatternItem.fromCache(context.getSource().getLevel(), stack);

            context.getSource().sendSuccess(new TextComponent("Crafting task factory ID: ").setStyle(Styles.YELLOW).append(new TextComponent(pattern.getCraftingTaskFactoryId().toString()).setStyle(Styles.WHITE)), false);

            if (!pattern.isValid()) {
                context.getSource().sendFailure(new TextComponent("Pattern is invalid! Reason: ").append(pattern.getErrorMessage()));
            } else {
                context.getSource().sendSuccess(new TextComponent("Processing: ").setStyle(Styles.YELLOW).append(new TextComponent(String.valueOf(processing)).setStyle(Styles.WHITE)), false);
                context.getSource().sendSuccess(new TextComponent("Exact: ").setStyle(Styles.YELLOW).append(new TextComponent(String.valueOf(exact)).setStyle(Styles.WHITE)), false);
                context.getSource().sendSuccess(new TextComponent("Has allowed tag list: ").setStyle(Styles.YELLOW).append(new TextComponent(String.valueOf(allowedTagList != null)).setStyle(Styles.WHITE)), false);

                if (pattern.isProcessing()) {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(new TextComponent("Item inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendSuccess(new TextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").append(pattern.getInputs().get(i).get(j).getHoverName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedItemTags().get(i)) {
                                context.getSource().sendSuccess(new TextComponent("- Allowed item tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    for (int i = 0; i < pattern.getFluidInputs().size(); ++i) {
                        if (!pattern.getFluidInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(new TextComponent("Fluid inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getFluidInputs().get(i).size(); ++j) {
                                context.getSource().sendSuccess(new TextComponent("- Possibility #" + j + ": " + pattern.getFluidInputs().get(i).get(j).getAmount() + " mB ").append(pattern.getFluidInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedFluidTags().get(i)) {
                                context.getSource().sendSuccess(new TextComponent("- Allowed fluid tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    context.getSource().sendSuccess(new TextComponent("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendSuccess(new TextComponent("- " + output.getCount() + "x ").append(output.getHoverName()), false);
                    }

                    context.getSource().sendSuccess(new TextComponent("Fluid outputs").setStyle(Styles.YELLOW), false);
                    for (FluidStack output : pattern.getFluidOutputs()) {
                        context.getSource().sendSuccess(new TextComponent("- " + output.getAmount() + " mB ").append(output.getDisplayName()), false);
                    }
                } else {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(new TextComponent("Inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendSuccess(new TextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").append(pattern.getInputs().get(i).get(j).getHoverName()), false);
                            }
                        }
                    }

                    context.getSource().sendSuccess(new TextComponent("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendSuccess(new TextComponent("- " + output.getCount() + "x ").append(output.getHoverName()), false);
                    }

                    boolean anyByproducts = false;

                    for (ItemStack byproduct : pattern.getByproducts()) {
                        if (!byproduct.isEmpty()) {
                            if (!anyByproducts) {
                                context.getSource().sendSuccess(new TextComponent("Byproducts").setStyle(Styles.YELLOW), false);

                                anyByproducts = true;
                            }

                            context.getSource().sendSuccess(new TextComponent("- " + byproduct.getCount() + "x ").append(byproduct.getHoverName()), false);
                        }
                    }
                }
            }
        } else {
            context.getSource().sendFailure(new TextComponent("You need to be holding a pattern in your hand."));
        }

        return 0;
    }
}
