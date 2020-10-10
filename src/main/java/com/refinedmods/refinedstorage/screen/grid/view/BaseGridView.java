package com.refinedmods.refinedstorage.screen.grid.view;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.refinedmods.refinedstorage.screen.grid.filtering.GridFilterParser;
import com.refinedmods.refinedstorage.screen.grid.sorting.IGridSorter;
import com.refinedmods.refinedstorage.screen.grid.sorting.SortingDirection;
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseGridView implements IGridView {
    private final GridScreen screen;
    private boolean canCraft;

    private final IGridSorter defaultSorter;
    private final List<IGridSorter> sorters;

    private List<IGridStack> stacks = new ArrayList<>();
    protected final Map<UUID, IGridStack> map = new HashMap<>();

    protected BaseGridView(GridScreen screen, IGridSorter defaultSorter, List<IGridSorter> sorters) {
        this.screen = screen;
        this.defaultSorter = defaultSorter;
        this.sorters = sorters;
    }

    @Override
    public List<IGridStack> getStacks() {
        return stacks;
    }

    @Override
    public Collection<IGridStack> getAllStacks() {
        return map.values();
    }

    @Nullable
    @Override
    public IGridStack get(UUID id) {
        return map.get(id);
    }

    @Override
    public void sort() {
        if (!screen.canSort()) {
            return;
        }

        if (screen.getGrid().isGridActive()) {
            this.stacks = map.values().stream()
                    .filter(getActiveFilters())
                    .sorted(getActiveSort())
                    .collect(Collectors.toList());
        } else {
            this.stacks = Collections.emptyList();
        }

        this.screen.updateScrollbar();
    }

    private Comparator<IGridStack> getActiveSort() {
        IGrid grid = screen.getGrid();
        SortingDirection sortingDirection = grid.getSortingDirection() == IGrid.SORTING_DIRECTION_DESCENDING ? SortingDirection.DESCENDING : SortingDirection.ASCENDING;
        return Stream.concat(Stream.of(defaultSorter), sorters.stream().filter(s -> s.isApplicable(grid)))
                .map(sorter -> (Comparator<IGridStack>) (o1, o2) -> sorter.compare(o1, o2, sortingDirection))
                .reduce((l, r) -> r.thenComparing(l))
                .orElseThrow(IllegalStateException::new);  // There is at least 1 value in the stream (i.e. defaultSorter)
    }

    private Predicate<IGridStack> getActiveFilters() {
        IGrid grid = screen.getGrid();

        Predicate<IGridStack> filters = GridFilterParser.getFilters(
                grid,
                screen.getSearchFieldText(),
                (grid.getTabSelected() >= 0 && grid.getTabSelected() < grid.getTabs().size()) ? grid.getTabs().get(grid.getTabSelected()).getFilters() : grid.getFilters()
        );

        if (screen.getGrid().getViewType() != IGrid.VIEW_TYPE_CRAFTABLES) {
            return stack -> {
                // If this is a crafting stack,
                // and there is a regular matching stack in the view too,
                // and we aren't in "view only craftables" mode,
                // we don't want the duplicate stacks and we will remove this stack.
                if (stack.isCraftable() &&
                        stack.getOtherId() != null &&
                        map.containsKey(stack.getOtherId())) {
                    return false;
                }

                return filters.test(stack);
            };
        } else {
            return filters;
        }
    }

    @Override
    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    @Override
    public boolean canCraft() {
        return canCraft;
    }
}
