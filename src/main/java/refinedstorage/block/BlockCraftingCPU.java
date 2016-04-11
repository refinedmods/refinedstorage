package refinedstorage.block;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import refinedstorage.item.ItemBlockBase;
import refinedstorage.item.ItemBlockStorage;
import refinedstorage.tile.autocrafting.TileCraftingCPU;

import java.util.List;

public class BlockCraftingCPU extends BlockMachine {
    public static final PropertyEnum TYPE = PropertyEnum.create("type", EnumStorageType.class);

    public BlockCraftingCPU() {
        super("crafting_cpu");
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List subItems) {
        for (int i = 0; i <= 4; ++i) {
            subItems.add(ItemBlockStorage.initNBT(new ItemStack(item, 1, i)));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[]{
            DIRECTION,
            CONNECTED,
            TYPE
        });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(TYPE, EnumStorageType.getById(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumStorageType) state.getValue(TYPE)).getId();
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileCraftingCPU();
    }

    @Override
    public Item createItemForBlock() {
        return new ItemBlockBase(this, true);
    }
}
