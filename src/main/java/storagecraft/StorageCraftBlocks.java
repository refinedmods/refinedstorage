package storagecraft;

import storagecraft.block.BlockCable;
import storagecraft.block.BlockController;
import storagecraft.block.BlockDetector;
import storagecraft.block.BlockDrive;
import storagecraft.block.BlockExporter;
import storagecraft.block.BlockGrid;
import storagecraft.block.BlockImporter;
import storagecraft.block.BlockMachineCasing;
import storagecraft.block.BlockSolderer;
import storagecraft.block.BlockExternalStorage;

public class StorageCraftBlocks
{
	public static final BlockController CONTROLLER = new BlockController();
	public static final BlockCable CABLE = new BlockCable();
	public static final BlockGrid GRID = new BlockGrid();
	public static final BlockDrive DRIVE = new BlockDrive();
	public static final BlockExternalStorage EXTERNAL_STORAGE = new BlockExternalStorage();
	public static final BlockImporter IMPORTER = new BlockImporter();
	public static final BlockExporter EXPORTER = new BlockExporter();
	public static final BlockDetector DETECTOR = new BlockDetector();
	public static final BlockMachineCasing MACHINE_CASING = new BlockMachineCasing();
	public static final BlockSolderer SOLDERER = new BlockSolderer();
}
