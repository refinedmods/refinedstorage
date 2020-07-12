package com.refinedmods.refinedstorage.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
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
    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("patterndump")
            .requires(cs -> cs.hasPermissionLevel(0))
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

            context.getSource().sendFeedback(new StringTextComponent("Crafting task factory ID: ").func_230530_a_(Styles.YELLOW).func_230529_a_(new StringTextComponent(pattern.getCraftingTaskFactoryId().toString()).func_230530_a_(Styles.WHITE)), false);

            if (!pattern.isValid()) {
                context.getSource().sendFeedback(new StringTextComponent("Pattern is invalid! Reason: ").func_230529_a_(pattern.getErrorMessage()).func_230530_a_(Styles.RED), false);
            } else {
                context.getSource().sendFeedback(new StringTextComponent("Processing: ").func_230530_a_(Styles.YELLOW).func_230529_a_(new StringTextComponent(String.valueOf(processing)).func_230530_a_(Styles.WHITE)), false);
                context.getSource().sendFeedback(new StringTextComponent("Exact: ").func_230530_a_(Styles.YELLOW).func_230529_a_(new StringTextComponent(String.valueOf(exact)).func_230530_a_(Styles.WHITE)), false);
                context.getSource().sendFeedback(new StringTextComponent("Has allowed tag list: ").func_230530_a_(Styles.YELLOW).func_230529_a_(new StringTextComponent(String.valueOf(allowedTagList != null)).func_230530_a_(Styles.WHITE)), false);

                if (pattern.isProcessing()) {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendFeedback(new StringTextComponent("Item inputs in slot " + i + ":").func_230530_a_(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").func_230529_a_(pattern.getInputs().get(i).get(j).getDisplayName()), false);
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
                            context.getSource().sendFeedback(new StringTextComponent("Fluid inputs in slot " + i + ":").func_230530_a_(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getFluidInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getFluidInputs().get(i).get(j).getAmount() + " mB ").func_230529_a_(pattern.getFluidInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }

                        if (allowedTagList != null) {
                            for (ResourceLocation allowed : allowedTagList.getAllowedFluidTags().get(i)) {
                                context.getSource().sendFeedback(new StringTextComponent("- Allowed fluid tag: " + allowed.toString()), false);
                            }
                        }
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Outputs").func_230530_a_(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getCount() + "x ").func_230529_a_(output.getDisplayName()), false);
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Fluid outputs").func_230530_a_(Styles.YELLOW), false);
                    for (FluidStack output : pattern.getFluidOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getAmount() + " mB ").func_230529_a_(output.getDisplayName()), false);
                    }
                } else {
                    for (int i = 0; i < pattern.getInputs().size(); ++i) {
                        if (!pattern.getInputs().get(i).isEmpty()) {
                            context.getSource().sendFeedback(new StringTextComponent("Inputs in slot " + i + ":").func_230530_a_(Styles.YELLOW), false);

                            for (int j = 0; j < pattern.getInputs().get(i).size(); ++j) {
                                context.getSource().sendFeedback(new StringTextComponent("- Possibility #" + j + ": " + pattern.getInputs().get(i).get(j).getCount() + "x ").func_230529_a_(pattern.getInputs().get(i).get(j).getDisplayName()), false);
                            }
                        }
                    }

                    context.getSource().sendFeedback(new StringTextComponent("Outputs").func_230530_a_(Styles.YELLOW), false);
                    for (ItemStack output : pattern.getOutputs()) {
                        context.getSource().sendFeedback(new StringTextComponent("- " + output.getCount() + "x ").func_230529_a_(output.getDisplayName()), false);
                    }

                    boolean anyByproducts = false;

                    for (ItemStack byproduct : pattern.getByproducts()) {
                        if (!byproduct.isEmpty()) {
                            if (!anyByproducts) {
                                context.getSource().sendFeedback(new StringTextComponent("Byproducts").func_230530_a_(Styles.YELLOW), false);

                                anyByproducts = true;
                            }

                            context.getSource().sendFeedback(new StringTextComponent("- " + byproduct.getCount() + "x ").func_230529_a_(byproduct.getDisplayName()), false);
                        }
                    }
                }
            }
        } else {
            context.getSource().sendFeedback(new StringTextComponent("You need to be holding a pattern in your hand.").func_230530_a_(Styles.RED), false);
        }

        return 0;
    }
}
