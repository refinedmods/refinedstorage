package refinedstorage.tile.autocrafting;

import refinedstorage.tile.TileController;

public class ProcessingCraftingTask implements ICraftingTask {
    private CraftingPattern pattern;

    public ProcessingCraftingTask(CraftingPattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public CraftingPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(TileController controller) {
        return false;
    }
}
