package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.item.info.IItemInfo;
import net.minecraft.util.ResourceLocation;

public abstract class ItemNetworkItem extends ItemEnergyItem /*implements INetworkItemProvider*/ {
    private static final String NBT_CONTROLLER_X = "ControllerX";
    private static final String NBT_CONTROLLER_Y = "ControllerY";
    private static final String NBT_CONTROLLER_Z = "ControllerZ";
    private static final String NBT_DIMENSION_ID = "DimensionID";

    public ItemNetworkItem(IItemInfo info, int energyCapacity) {
        super(info, energyCapacity);

        addPropertyOverride(new ResourceLocation("connected"), (stack, world, entity) -> (entity != null && /*isValid(stack)*/false) ? 1.0f : 0.0f);
    }

    /* TODO
    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            applyNetwork(stack, n -> n.getNetworkItemHandler().open(player, player.getHeldItem(hand)), player::sendMessage);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }

    public void applyNetwork(ItemStack stack, Consumer<INetwork> networkConsumer, Consumer<TextComponentTranslation> errorConsumer) {
        if (!isValid(stack)) {
            errorConsumer.accept(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
        } else {
            World networkWorld = DimensionManager.getWorld(getDimensionId(stack));

            TileEntity network;

            if (networkWorld != null && ((network = networkWorld.getTileEntity(new BlockPos(getX(stack), getY(stack), getZ(stack)))) instanceof INetwork)) {
                networkConsumer.accept((INetwork) network);
            } else {
                errorConsumer.accept(new TextComponentTranslation("misc.refinedstorage:network_item.not_found"));
            }
        }
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);

        if (isValid(stack)) {
            tooltip.add(I18n.format("misc.refinedstorage:network_item.tooltip", getX(stack), getY(stack), getZ(stack)));
        }
    }

    @Override
    public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);

        Block block = world.getBlockState(pos).getBlock();

        if (block == RSBlocks.CONTROLLER) {
            CompoundNBT tag = stack.getTagCompound();

            if (tag == null) {
                tag = new CompoundNBT();
            }

            tag.putInt(NBT_CONTROLLER_X, pos.getX());
            tag.putInt(NBT_CONTROLLER_Y, pos.getY());
            tag.putInt(NBT_CONTROLLER_Z, pos.getZ());
            tag.putInt(NBT_DIMENSION_ID, player.dimension);

            stack.setTagCompound(tag);

            return EnumActionResult.SUCCESS;
        }

        return EnumActionResult.PASS;
    }

    public static int getDimensionId(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_DIMENSION_ID);
    }

    public static int getX(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_X);
    }

    public static int getY(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_Y);
    }

    public static int getZ(ItemStack stack) {
        return stack.getTagCompound().getInteger(NBT_CONTROLLER_Z);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public boolean isValid(ItemStack stack) {
        return stack.hasTagCompound()
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_X)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Y)
            && stack.getTagCompound().hasKey(NBT_CONTROLLER_Z)
            && stack.getTagCompound().hasKey(NBT_DIMENSION_ID);
    }*/
}
