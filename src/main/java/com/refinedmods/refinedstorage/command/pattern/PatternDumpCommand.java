package com.refinedmods.refinedstorage.command.pattern;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.AllowedTagList;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.render.Styles;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;

public class PatternDumpCommand implements Command<CommandSource> {
    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("dump")
            .executes(new PatternDumpCommand());
    }

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ItemStack stack = context.getSource().asPlayer().inventory.getCurrentItem();

        if (stack.getItem() instanceof PatternItem) {
            boolean processing = PatternItem.isProcessing(stack);
            boolean exact = PatternItem.isExact(stack);
            AllowedTagList allowedTagList = PatternItem.getAllowedTags(stack);

            CraftingPattern pattern = PatternItem.fromCache(context.getSource().getWorld(), stack);

            context.getSource().sendFeedback(new StringTextComponent("Crafting task factory ID: ").setStyle(Styles.YELLOW).append(new StringTextComponent(pattern.getCraftingTaskFactoryId().toString()).setStyle(Styles.WHITE)), false);

            if (!pattern.isValid()) {
                context.getSource().sendErrorMessage(new StringTextComponent("Pattern is invalid! Reason: ").append(pattern.getErrorMessage()));
            } else {
                context.getSource().sendFeedback(new StringTextComponent("Processing: ").setStyle(Styles.YELLOW).append(new StringTextComponent(String.valueOf(processing)).setStyle(Styles.WHITE)), false);
                context.getSource().sendFeedback(new StringTextComponent("Exact: ").setStyle(Styles.YELLOW).append(new StringTextComponent(String.valueOf(exact)).setStyle(Styles.WHITE)), false);
                context.getSource().sendFeedback(new StringTextComponent("Has allowed tag list: ").setStyle(Styles.YELLOW).append(new StringTextComponent(String.valueOf(allowedTagList != null)).setStyle(Styles.WHITE)), false);

                if (pattern.isProcessing()) {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendFeedback(new StringTextComponent("Item inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").append(pattern.getInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedItemTags().get(i)) {
                                context.getSource().sendFeedback(new StringTextComponent("- Allowed item tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    for (int i = 0; i < pattern.getFluidInputs().size(); ++i) {
                        if (!pattern.getFluidInputs().get(i).isEmpty()) {
                            context.getSource().sendFeedback(new StringTextComponent("Fluid inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getFluidInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getFluidInputs().get(i).get(j).getAmount() + " mB ").append(pattern.getFluidInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedFluidTags().get(i)) {
                                context.getSource().sendFeedback(new StringTextComponent("- Allowed fluid tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getCount() + "x ").append(output.getDisplayName()), false);
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Fluid outputs").setStyle(Styles.YELLOW), false);
                    for (FluidStack output : pattern.getFluidOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getAmount() + " mB ").append(output.getDisplayName()), false);
                    }
                } else {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendFeedback(new StringTextComponent("Inputs in slot " + i + ":").setStyle(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").append(pattern.getInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Outputs").setStyle(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getCount() + "x ").append(output.getDisplayName()), false);
                    }

                    boolean anyByproducts = false;

                    for (ItemStack byproduct : pattern.getByproducts()) {
                        if (!byproduct.isEmpty()) {
                            if (!anyByproducts) {
                                context.getSource().sendFeedback(new StringTextComponent("Byproducts").setStyle(Styles.YELLOW), false);

                                anyByproducts = true;
                            }

                            context.getSource().sendFeedback(new StringTextComponent("- " + byproduct.getCount() + "x ").append(byproduct.getDisplayName()), false);
                        }
                    }
                }
            }
        } else {
            context.getSource().sendErrorMessage(new StringTextComponent("You need to be holding a pattern in your hand."));
        }

        return 0;
    }
}
