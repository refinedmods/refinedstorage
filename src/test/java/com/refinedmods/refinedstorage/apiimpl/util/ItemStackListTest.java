package com.refinedmods.refinedstorage.apiimpl.util;

import com.refinedmods.refinedstorage.api.util.StackListResult;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemStackListTest extends MinecraftTest {
    private final ItemStackList list = new ItemStackList();

    @Test
    void Test_adding_a_stack_without_any_existing_stack_present() {
        // Arrange
        ItemStack toAdd = new ItemStack(Items.DIRT);

        // Act
        StackListResult<ItemStack> result = list.add(toAdd, 10);

        // Assert
        assertThat(result.getChange()).isEqualTo(10);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStack().getItem()).isEqualTo(Items.DIRT);
        assertThat(result.getStack().getCount()).isEqualTo(10);
        assertThat(result.getStack().getTag()).isNull();
        assertThat(result.getStack()).isNotSameAs(toAdd);

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void Test_adding_a_stack_with_an_existing_stack_present_should_merge_if_same_stack() {
        // Arrange
        ItemStack toAdd1 = new ItemStack(Items.DIRT);
        ItemStack toAdd2 = new ItemStack(Items.DIRT);

        // Act
        UUID id = list.add(toAdd1, 10).getId();
        StackListResult<ItemStack> result = list.add(toAdd2, 10);

        // Assert
        assertThat(result.getStack()).isNotSameAs(toAdd1).isNotSameAs(toAdd2);
        assertThat(result.getChange()).isEqualTo(10);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStack().getItem()).isEqualTo(Items.DIRT);
        assertThat(result.getStack().getCount()).isEqualTo(20);
        assertThat(result.getStack().getTag()).isNull();

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void Test_adding_a_stack_with_an_existing_stack_present_should_not_merge_if_different_item() {
        // Arrange
        ItemStack toAdd1 = new ItemStack(Items.DIRT, 1);
        ItemStack toAdd2 = new ItemStack(Items.GLASS);
        ItemStack toAdd3 = new ItemStack(Items.DIRT, 5);

        // Act
        list.add(toAdd1, 10);
        list.add(toAdd2, 10);
        list.add(toAdd3, 10);

        // Assert
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void Test_adding_a_stack_with_an_existing_stack_present_should_not_merge_if_same_item_but_different_tag() {
        // Arrange
        ItemStack toAdd1 = new ItemStack(Items.DIRT);
        ItemStack toAdd2 = new ItemStack(Items.GLASS);
        ItemStack toAdd3 = new ItemStack(Items.DIRT);
        CompoundNBT testTag = new CompoundNBT();
        testTag.putInt("a", 1);
        toAdd3.setTag(testTag);

        // Act
        list.add(toAdd1, 10);
        list.add(toAdd2, 10);
        list.add(toAdd3, 10);

        // Assert
        assertThat(list.size()).isEqualTo(3);
    }

    @Test
    void Test_adding_invalid_stacks_should_fail() {
        assertThrows(IllegalArgumentException.class, () -> list.add(ItemStack.EMPTY));
        assertThrows(IllegalArgumentException.class, () -> list.add(new ItemStack(Items.DIRT), -1));
        assertThrows(IllegalArgumentException.class, () -> list.add(new ItemStack(Items.DIRT), 0));
    }

    @Test
    void Test_adding_too_big_stacks_should_not_overflow() {
        ItemStack current = list.add(new ItemStack(Items.DIRT), Integer.MAX_VALUE - 5).getStack();
        assertThat(current.getCount()).isEqualTo(Integer.MAX_VALUE - 5);

        current = list.add(new ItemStack(Items.DIRT), 4).getStack();
        assertThat(current.getCount()).isEqualTo(Integer.MAX_VALUE - 1);

        current = list.add(new ItemStack(Items.DIRT), 1).getStack();
        assertThat(current.getCount()).isEqualTo(Integer.MAX_VALUE);

        current = list.add(new ItemStack(Items.DIRT), 1).getStack();
        assertThat(current.getCount()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void Test_removing_non_existent_item_should_give_back_nothing() {
        // Arrange
        ItemStack toAdd = new ItemStack(Items.GLASS);
        ItemStack toRemove = new ItemStack(Items.DIRT);

        // Act
        list.add(toAdd);
        StackListResult<ItemStack> result = list.remove(toRemove, 10);

        // Assert
        assertThat(list.size()).isEqualTo(1);
        assertThat(result).isNull();
    }

    @Test
    void Test_removing_half_of_an_existing_item() {
        // Arrange
        ItemStack toAdd1 = new ItemStack(Items.DIRT, 10);
        ItemStack toAdd2 = new ItemStack(Items.GLASS, 10);
        ItemStack toRemove = new ItemStack(Items.DIRT, 2);

        // Act
        list.add(toAdd1);
        list.add(toAdd2);

        StackListResult<ItemStack> result = list.remove(toRemove);

        // Assert
        assertThat(result.getStack().getItem()).isEqualTo(Items.DIRT);
        assertThat(result.getStack().getCount()).isEqualTo(8);
        assertThat(result.getStack().getTag()).isNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getChange()).isEqualTo(-2);

        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    void Test_removing_an_existing_item_completely() {
        // Arrange
        ItemStack toAdd1 = new ItemStack(Items.DIRT, 10);
        ItemStack toAdd2 = new ItemStack(Items.GLASS, 10);
        ItemStack toRemove = new ItemStack(Items.DIRT, 10);

        // Act
        list.add(toAdd1);
        list.add(toAdd2);

        StackListResult<ItemStack> result = list.remove(toRemove);

        // Assert
        assertThat(result.getStack().getItem()).isEqualTo(Items.DIRT);
        assertThat(result.getStack().getCount()).isEqualTo(10);
        assertThat(result.getStack().getTag()).isNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getChange()).isEqualTo(-10);

        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    void Test_removing_an_existing_item_with_more_than_is_available() {
        // Arrange
        ItemStack toAdd = new ItemStack(Items.DIRT, 10);
        ItemStack toRemove = new ItemStack(Items.DIRT, 100);

        // Act
        list.add(toAdd);

        StackListResult<ItemStack> result = list.remove(toRemove);

        // Assert
        assertThat(result.getStack().getItem()).isEqualTo(Items.DIRT);
        assertThat(result.getStack().getCount()).isEqualTo(10);
        assertThat(result.getStack().getTag()).isNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getChange()).isEqualTo(-10);

        assertThat(list.isEmpty()).isTrue();
    }
}
