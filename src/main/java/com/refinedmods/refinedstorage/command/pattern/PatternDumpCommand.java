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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

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

            context.getSource().sendSuccess(() -> Component.literal("Crafting task factory ID: ").setStyle(Styles.YELLOW).append(Component.literal(pattern.getCraftingTaskFactoryId().toString()).setStyle(Styles.WHITE)), false);

            if (!pattern.isValid()) {
                context.getSource().sendFailure(Component.literal("Pattern is invalid! Reason: ").append(pattern.getErrorMessage()));
            } else {
                context.getSource().sendSuccess(() -> Component.literal("Processing: ").setStyle(Styles.YELLOW).append(Component.literal(String.valueOf(processing)).setStyle(Styles.WHITE)), false);
                context.getSource().sendSuccess(() -> Component.literal("Exact: ").setStyle(Styles.YELLOW).append(Component.literal(String.valueOf(exact)).setStyle(Styles.WHITE)), false);
                context.getSource().sendSuccess(() -> Component.literal("Has allowed tag list: ").setStyle(Styles.YELLOW).append(Component.literal(String.valueOf(allowedTagList != null)).setStyle(Styles.WHITE)), false);

                if (pattern.isProcessing()) {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        final int ii = i;
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(() -> Component.literal("Item inputs in slot " + ii + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                final int jj = j;
                                context.getSource().sendSuccess(() -> Component.literal("- Possibility #" + jj + ": " + pattern.getInputs().get(ii).get(jj).getCount() + "x ").append(pattern.getInputs().get(ii).get(jj).getHoverName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedItemTags().get(i)) {
                                context.getSource().sendSuccess(() -> Component.literal("- Allowed item tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    for (int i = 0; i < pattern.getFluidInputs().size(); ++i) {
                        final int ii = i;
                        if (!pattern.getFluidInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(() -> Component.literal("Fluid inputs in slot " + ii + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getFluidInputs().get(i).size(); ++j) {
                                final int jj = j;
                                context.getSource().sendSuccess(() -> Component.literal("- Possibility #" + jj + ": " + pattern.getFluidInputs().get(ii).get(jj).getAmount() + " mB ").append(pattern.getFluidInputs().get(ii).get(jj).getDisplayName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedFluidTags().get(i)) {
                                context.getSource().sendSuccess(() -> Component.literal("- Allowed fluid tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    context.getSource().sendSuccess(() -> Component.literal("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendSuccess(() -> Component.literal("- " + output.getCount() + "x ").append(output.getHoverName()), false);
                    }

                    context.getSource().sendSuccess(() -> Component.literal("Fluid outputs").setStyle(Styles.YELLOW), false);
                    for (FluidStack output : pattern.getFluidOutputs()) {
                        context.getSource().sendSuccess(() -> Component.literal("- " + output.getAmount() + " mB ").append(output.getDisplayName()), false);
                    }
                } else {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        final int ii = i;
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendSuccess(() -> Component.literal("Inputs in slot " + ii + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                final int jj = j;
                                context.getSource().sendSuccess(() -> Component.literal("- Possibility #" + jj + ": " + pattern.getInputs().get(ii).get(jj).getCount() + "x ").append(pattern.getInputs().get(ii).get(jj).getHoverName()), false);
                            }
                        }
                    }

                    context.getSource().sendSuccess(() -> Component.literal("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendSuccess(() -> Component.literal("- " + output.getCount() + "x ").append(output.getHoverName()), false);
                    }

                    boolean anyByproducts = false;

                    for (ItemStack byproduct : pattern.getByproducts()) {
                        if (!byproduct.isEmpty()) {
                            if (!anyByproducts) {
                                context.getSource().sendSuccess(() -> Component.literal("Byproducts").setStyle(Styles.YELLOW), false);

                                anyByproducts = true;
                            }

                            context.getSource().sendSuccess(() -> Component.literal("- " + byproduct.getCount() + "x ").append(byproduct.getHoverName()), false);
                        }
                    }
                }
            }
        } else {
            context.getSource().sendFailure(Component.literal("You need to be holding a pattern in your hand."));
        }

        return 0;
    }
}
