/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.*;
import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import blusunrize.immersiveengineering.api.crafting.IngredientWithSize;
import blusunrize.immersiveengineering.api.energy.ThermoelectricHandler;
import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.api.multiblocks.MultiblockHandler;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.api.shader.CapabilityShader;
import blusunrize.immersiveengineering.api.shader.ShaderRegistry;
import blusunrize.immersiveengineering.api.tool.BulletHandler;
import blusunrize.immersiveengineering.api.tool.BulletHandler.IBullet;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ConveyorHandler;
import blusunrize.immersiveengineering.api.tool.ConveyorHandler.ItemAgeAccessor;
import blusunrize.immersiveengineering.api.tool.ExternalHeaterHandler;
import blusunrize.immersiveengineering.api.tool.assembler.AssemblerHandler;
import blusunrize.immersiveengineering.api.tool.assembler.FluidStackRecipeQuery;
import blusunrize.immersiveengineering.api.tool.assembler.FluidTagRecipeQuery;
import blusunrize.immersiveengineering.api.utils.SetRestrictedField;
import blusunrize.immersiveengineering.api.utils.TemplateWorldCreator;
import blusunrize.immersiveengineering.api.wires.GlobalWireNetwork;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.localhandlers.EnergyTransferHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.LocalNetworkHandler;
import blusunrize.immersiveengineering.api.wires.localhandlers.WireDamageHandler;
import blusunrize.immersiveengineering.api.wires.redstone.CapabilityRedstoneNetwork;
import blusunrize.immersiveengineering.api.wires.redstone.RedstoneNetworkHandler;
import blusunrize.immersiveengineering.api.wires.utils.WirecoilUtils;
import blusunrize.immersiveengineering.client.utils.ClocheRenderFunctions;
import blusunrize.immersiveengineering.common.blocks.*;
import blusunrize.immersiveengineering.common.blocks.IEBlocks.*;
import blusunrize.immersiveengineering.common.blocks.cloth.*;
import blusunrize.immersiveengineering.common.blocks.generic.ScaffoldingBlock;
import blusunrize.immersiveengineering.common.blocks.generic.*;
import blusunrize.immersiveengineering.common.blocks.metal.*;
import blusunrize.immersiveengineering.common.blocks.metal.MetalLadderBlock.CoverType;
import blusunrize.immersiveengineering.common.blocks.metal.conveyors.*;
import blusunrize.immersiveengineering.common.blocks.multiblocks.IEMultiblocks;
import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import blusunrize.immersiveengineering.common.blocks.plant.HempBlock;
import blusunrize.immersiveengineering.common.blocks.plant.PottedHempBlock;
import blusunrize.immersiveengineering.common.blocks.stone.PartialConcreteBlock;
import blusunrize.immersiveengineering.common.blocks.stone.StoneMultiBlock;
import blusunrize.immersiveengineering.common.blocks.wooden.BarrelBlock;
import blusunrize.immersiveengineering.common.blocks.wooden.CraftingTableBlock;
import blusunrize.immersiveengineering.common.blocks.wooden.*;
import blusunrize.immersiveengineering.common.config.IECommonConfig;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.crafting.DefaultAssemblerAdapter;
import blusunrize.immersiveengineering.common.crafting.IngredientWithSizeSerializer;
import blusunrize.immersiveengineering.common.crafting.fluidaware.IngredientFluidStack;
import blusunrize.immersiveengineering.common.entities.*;
import blusunrize.immersiveengineering.common.items.*;
import blusunrize.immersiveengineering.common.items.IEItems.Ingredients;
import blusunrize.immersiveengineering.common.items.IEItems.Molds;
import blusunrize.immersiveengineering.common.items.IEItems.Tools;
import blusunrize.immersiveengineering.common.items.IEItems.Weapons;
import blusunrize.immersiveengineering.common.items.ToolUpgradeItem.ToolUpgrade;
import blusunrize.immersiveengineering.common.util.*;
import blusunrize.immersiveengineering.common.util.fakeworld.TemplateWorld;
import blusunrize.immersiveengineering.common.util.fluids.ConcreteFluid;
import blusunrize.immersiveengineering.common.util.fluids.IEFluid;
import blusunrize.immersiveengineering.common.util.fluids.PotionFluid;
import blusunrize.immersiveengineering.common.util.loot.IELootFunctions;
import blusunrize.immersiveengineering.common.wires.CapabilityInit;
import blusunrize.immersiveengineering.common.wires.IEWireTypes;
import blusunrize.immersiveengineering.common.world.IEWorldGen;
import blusunrize.immersiveengineering.common.world.OreRetrogenFeature;
import blusunrize.immersiveengineering.common.world.Villages;
import blusunrize.immersiveengineering.mixin.accessors.ConcretePowderBlockAccess;
import blusunrize.immersiveengineering.mixin.accessors.ItemEntityAccess;
import blusunrize.immersiveengineering.mixin.accessors.TemplateAccess;
import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static blusunrize.immersiveengineering.ImmersiveEngineering.MODID;
import static blusunrize.immersiveengineering.api.tool.assembler.AssemblerHandler.defaultAdapter;
import static blusunrize.immersiveengineering.common.util.fluids.IEFluid.createBuilder;

@Mod.EventBusSubscriber(modid = MODID, bus = Bus.MOD)
public class IEContent
{
	public static List<Block> registeredIEBlocks = new ArrayList<>();
	public static List<Item> registeredIEItems = new ArrayList<>();
	public static List<Class<? extends BlockEntity>> registeredIETiles = new ArrayList<>();
	public static List<Fluid> registeredIEFluids = new ArrayList<>();

	public static IEFluid fluidCreosote;
	public static IEFluid fluidPlantoil;
	public static IEFluid fluidEthanol;
	public static IEFluid fluidBiodiesel;
	public static IEFluid fluidConcrete;
	public static IEFluid fluidHerbicide;
	public static Fluid fluidPotion;

	public static final Feature<OreConfiguration> ORE_RETROGEN = new OreRetrogenFeature(OreConfiguration.CODEC);

	public static void modConstruction()
	{
		/*BULLETS*/
		BulletItem.initBullets();
		/*WIRES*/
		IEWireTypes.modConstruction();
		/*CONVEYORS*/
		ConveyorHandler.registerMagnetSuppression((entity, iConveyorTile) -> {
			CompoundTag data = entity.getPersistentData();
			if(!data.getBoolean(Lib.MAGNET_PREVENT_NBT))
				data.putBoolean(Lib.MAGNET_PREVENT_NBT, true);
		}, (entity, iConveyorTile) -> {
			entity.getPersistentData().remove(Lib.MAGNET_PREVENT_NBT);
		});
		ConveyorHandler.registerConveyorHandler(BasicConveyor.NAME, BasicConveyor.class, BasicConveyor::new);
		ConveyorHandler.registerConveyorHandler(RedstoneConveyor.NAME, RedstoneConveyor.class, RedstoneConveyor::new);
		ConveyorHandler.registerConveyorHandler(DropConveyor.NAME, DropConveyor.class, DropConveyor::new);
		ConveyorHandler.registerConveyorHandler(VerticalConveyor.NAME, VerticalConveyor.class, VerticalConveyor::new);
		ConveyorHandler.registerConveyorHandler(SplitConveyor.NAME, SplitConveyor.class, SplitConveyor::new);
		ConveyorHandler.registerConveyorHandler(ExtractConveyor.NAME, ExtractConveyor.class, ExtractConveyor::new);
		ConveyorHandler.registerConveyorHandler(CoveredConveyor.NAME, CoveredConveyor.class, CoveredConveyor::new);
		ConveyorHandler.registerConveyorHandler(DropCoveredConveyor.NAME, DropCoveredConveyor.class, DropCoveredConveyor::new);
		ConveyorHandler.registerConveyorHandler(VerticalCoveredConveyor.NAME, VerticalCoveredConveyor.class, VerticalCoveredConveyor::new);
		ConveyorHandler.registerConveyorHandler(ExtractCoveredConveyor.NAME, ExtractCoveredConveyor.class, ExtractCoveredConveyor::new);
		ConveyorHandler.registerConveyorHandler(SplitCoveredConveyor.NAME, SplitCoveredConveyor.class, SplitCoveredConveyor::new);
		ConveyorHandler.registerSubstitute(new ResourceLocation(MODID, "conveyor"), new ResourceLocation(MODID, "uncontrolled"));
		/*SHADERS*/
		ShaderRegistry.rarityWeightMap.put(Rarity.COMMON, 9);
		ShaderRegistry.rarityWeightMap.put(Rarity.UNCOMMON, 7);
		ShaderRegistry.rarityWeightMap.put(Rarity.RARE, 5);
		ShaderRegistry.rarityWeightMap.put(Rarity.EPIC, 3);
		ShaderRegistry.rarityWeightMap.put(Lib.RARITY_MASTERWORK, 1);

		fluidCreosote = new IEFluid("creosote", new ResourceLocation("immersiveengineering:block/fluid/creosote_still"),
				new ResourceLocation("immersiveengineering:block/fluid/creosote_flow"), createBuilder(1100, 3000));
		fluidPlantoil = new IEFluid("plantoil", new ResourceLocation("immersiveengineering:block/fluid/plantoil_still"),
				new ResourceLocation("immersiveengineering:block/fluid/plantoil_flow"), createBuilder(925, 2000));
		fluidEthanol = new IEFluid("ethanol", new ResourceLocation("immersiveengineering:block/fluid/ethanol_still"),
				new ResourceLocation("immersiveengineering:block/fluid/ethanol_flow"), createBuilder(789, 1000));
		fluidBiodiesel = new IEFluid("biodiesel", new ResourceLocation("immersiveengineering:block/fluid/biodiesel_still"),
				new ResourceLocation("immersiveengineering:block/fluid/biodiesel_flow"), createBuilder(789, 1000));
		fluidConcrete = new ConcreteFluid();
		fluidPotion = new PotionFluid();
		fluidHerbicide = new IEFluid("herbicide", new ResourceLocation("immersiveengineering:block/fluid/herbicide_still"),
				new ResourceLocation("immersiveengineering:block/fluid/herbicide_flow"), createBuilder(789, 1000));

		Block.Properties sheetmetalProperties = Block.Properties.of(Material.METAL).sound(SoundType.METAL).strength(3, 10);
		ImmersiveEngineering.proxy.registerContainersAndScreens();

		Map<EnumMetals, Integer> oreMiningLevels = ImmutableMap.<EnumMetals, Integer>builder()
				.put(EnumMetals.COPPER, 1)
				.put(EnumMetals.ALUMINUM, 1)
				.put(EnumMetals.LEAD, 2)
				.put(EnumMetals.SILVER, 2)
				.put(EnumMetals.NICKEL, 2)
				.put(EnumMetals.URANIUM, 2)
				.build();
		Map<EnumMetals, Integer> storageMiningLevels = ImmutableMap.<EnumMetals, Integer>builder()
				.put(EnumMetals.COPPER, 1)
				.put(EnumMetals.ALUMINUM, 1)
				.put(EnumMetals.LEAD, 2)
				.put(EnumMetals.SILVER, 2)
				.put(EnumMetals.NICKEL, 2)
				.put(EnumMetals.URANIUM, 2)
				.put(EnumMetals.CONSTANTAN, 2)
				.put(EnumMetals.ELECTRUM, 2)
				.put(EnumMetals.STEEL, 2)
				.build();

		for(EnumMetals m : EnumMetals.values())
		{
			String name = m.tagName();
			Block storage;
			Block ore = null;
			Item nugget;
			Item ingot;
			Item plate = new IEBaseItem("plate_"+name);
			Item dust = new IEBaseItem("dust_"+name);
			IEBaseBlock sheetmetal = new IEBaseBlock("sheetmetal_"+name, sheetmetalProperties, BlockItemIE::new);
			addSlabFor(sheetmetal);
			if(m.shouldAddOre())
			{
				ore = new IEBaseBlock("ore_"+name,
						Block.Properties.of(Material.STONE)
								.strength(3, 5)
								.requiresCorrectToolForDrops()
								.harvestTool(ToolType.PICKAXE)
								.harvestLevel(oreMiningLevels.get(m)), BlockItemIE::new);
			}
			if(!m.isVanillaMetal())
			{
				storage = new IEBaseBlock("storage_"+name, Block.Properties.of(Material.METAL)
						.sound(m==EnumMetals.STEEL?SoundType.NETHERITE_BLOCK: SoundType.METAL)
						.strength(5, 10)
						.requiresCorrectToolForDrops()
						.harvestTool(ToolType.PICKAXE)
						.harvestLevel(storageMiningLevels.get(m)), BlockItemIE::new);
				nugget = new IEBaseItem("nugget_"+name);
				ingot = new IEBaseItem("ingot_"+name);
				addSlabFor((IEBaseBlock)storage);
			}
			else if(m==EnumMetals.IRON)
			{
				storage = Blocks.IRON_BLOCK;
				ore = Blocks.IRON_ORE;
				nugget = Items.IRON_NUGGET;
				ingot = Items.IRON_INGOT;
			}
			else if(m==EnumMetals.GOLD)
			{
				storage = Blocks.GOLD_BLOCK;
				ore = Blocks.GOLD_ORE;
				nugget = Items.GOLD_NUGGET;
				ingot = Items.GOLD_INGOT;
			}
			else
				throw new RuntimeException("Unkown vanilla metal: "+m.name());
			IEBlocks.Metals.storage.put(m, storage);
			if(ore!=null)
				IEBlocks.Metals.ores.put(m, ore);
			IEBlocks.Metals.sheetmetal.put(m, sheetmetal);
			IEItems.Metals.plates.put(m, plate);
			IEItems.Metals.nuggets.put(m, nugget);
			IEItems.Metals.ingots.put(m, ingot);
			IEItems.Metals.dusts.put(m, dust);
		}
		for(DyeColor dye : DyeColor.values())
		{
			IEBaseBlock sheetmetal = new IEBaseBlock("sheetmetal_colored_"+dye.getName(), sheetmetalProperties, BlockItemIE::new);
			MetalDecoration.coloredSheetmetal.put(dye, sheetmetal);
			addSlabFor(sheetmetal);
		}
		Block.Properties stoneDecoProps = Block.Properties.of(Material.STONE)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
				.harvestTool(ToolType.PICKAXE)
				.strength(2, 10);
		Block.Properties stoneDecoPropsNotSolid = Block.Properties.of(Material.STONE)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
				.harvestTool(ToolType.PICKAXE)
				.strength(2, 10)
				.noOcclusion();
		Block.Properties stoneDecoLeadedProps = Block.Properties.of(Material.STONE)
				.sound(SoundType.STONE)
				.requiresCorrectToolForDrops()
				.harvestTool(ToolType.PICKAXE)
				.strength(2, 180);

		StoneDecoration.cokebrick = new IEBaseBlock("cokebrick", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.blastbrick = new IEBaseBlock("blastbrick", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.blastbrickReinforced = new IEBaseBlock("blastbrick_reinforced", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.coke = new IEBaseBlock("coke", stoneDecoProps,
				(b, prop) -> new BlockItemIE(b, prop).setBurnTime(3200*10));
		StoneDecoration.hempcrete = new IEBaseBlock("hempcrete", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.concrete = new IEBaseBlock("concrete", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.concreteTile = new IEBaseBlock("concrete_tile", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.concreteLeaded = new IEBaseBlock("concrete_leaded", stoneDecoLeadedProps, BlockItemIE::new);
		StoneDecoration.alloybrick = new IEBaseBlock("alloybrick", stoneDecoProps, BlockItemIE::new);
		StoneDecoration.concreteThreeQuarter = new PartialConcreteBlock("concrete_three_quarter", 12);
		StoneDecoration.concreteQuarter = new PartialConcreteBlock("concrete_quarter", 4);
		StoneDecoration.concreteSheet = new PartialConcreteBlock("concrete_sheet", 1);

		IEBlocks.StoneDecoration.insulatingGlass = new IEBaseBlock("insulating_glass", stoneDecoPropsNotSolid, BlockItemIE::new);
		IEBlocks.StoneDecoration.concreteSprayed = new IEBaseBlock("concrete_sprayed", Block.Properties.of(Material.STONE)
				.strength(.2F, 1)
				.noOcclusion(), BlockItemIE::new)
				.setHammerHarvest();
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.cokebrick);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.blastbrick);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.blastbrickReinforced);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.coke);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.hempcrete);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.concrete);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.concreteTile);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.concreteLeaded);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.insulatingGlass);
		addSlabFor((IEBaseBlock)IEBlocks.StoneDecoration.alloybrick);

		StoneDecoration.hempcreteStairs = new IEStairsBlock("stairs_hempcrete", stoneDecoProps, (IEBaseBlock)StoneDecoration.hempcrete);
		StoneDecoration.concreteStairs[0] = new IEStairsBlock("stairs_concrete", stoneDecoProps, (IEBaseBlock)StoneDecoration.concrete);
		StoneDecoration.concreteStairs[1] = new IEStairsBlock("stairs_concrete_tile", stoneDecoProps, (IEBaseBlock)StoneDecoration.concreteTile);
		StoneDecoration.concreteStairs[2] = new IEStairsBlock("stairs_concrete_leaded", stoneDecoLeadedProps, (IEBaseBlock)StoneDecoration.concreteLeaded);
		StoneDecoration.coresample = new HorizontalFacingBlock<>("coresample", IETileTypes.CORE_SAMPLE,
				stoneDecoPropsNotSolid, ($1, $2) -> null);

		Block.Properties standardWoodProperties = Block.Properties.of(Material.WOOD)
				.sound(SoundType.WOOD)
				.strength(2, 5);
		Supplier<BlockBehaviour.Properties> createNonBlockingWoodProperties =
				() -> Block.Properties.of(Material.WOOD)
						.sound(SoundType.WOOD)
						.strength(2, 5)
						.isViewBlocking((state, blockReader, pos) -> false);
		Block.Properties standardWoodPropertiesNotSolid = createNonBlockingWoodProperties.get().noOcclusion();
		Block.Properties standardWoodPropertiesNoOverlay = createNonBlockingWoodProperties.get();
		for(TreatedWoodStyles style : TreatedWoodStyles.values())
		{
			IEBaseBlock baseBlock = new IEBaseBlock("treated_wood_"+style.name().toLowerCase(Locale.US), standardWoodProperties, BlockItemIE::new)
					.setHasFlavour(true);
			WoodenDecoration.treatedWood.put(style, baseBlock);
			addSlabFor(baseBlock);
			WoodenDecoration.treatedStairs.put(style,
					new IEStairsBlock("stairs_treated_wood_"+style.name().toLowerCase(Locale.US), standardWoodPropertiesNotSolid, baseBlock));
		}
		WoodenDecoration.treatedFence = new IEFenceBlock("treated_fence", standardWoodPropertiesNoOverlay);
		WoodenDecoration.treatedScaffolding = new ScaffoldingBlock("treated_scaffold", standardWoodPropertiesNotSolid);

		WoodenDevices.craftingTable = new CraftingTableBlock("craftingtable", IETileTypes.CRAFTING_TABLE, standardWoodPropertiesNotSolid);
		WoodenDevices.workbench = new DeskBlock<>("workbench", IETileTypes.MOD_WORKBENCH);
		WoodenDevices.circuitTable = new DeskBlock<>("circuit_table", IETileTypes.CIRCUIT_TABLE);
		WoodenDevices.gunpowderBarrel = new GunpowderBarrelBlock("gunpowder_barrel");
		WoodenDevices.woodenBarrel = BarrelBlock.make("wooden_barrel", false);
		WoodenDevices.turntable = new TurntableBlock("turntable");
		WoodenDevices.crate = new CrateBlock("crate", false);
		WoodenDevices.reinforcedCrate = new CrateBlock("reinforced_crate", true);
		WoodenDevices.logicUnit = new HorizontalFacingBlock<>("logic_unit", IETileTypes.LOGIC_UNIT,
				standardWoodPropertiesNotSolid);

		WoodenDevices.sorter = SorterBlock.make("sorter", false);
		WoodenDevices.itemBatcher = new ItemBatcherBlock(standardWoodProperties);
		WoodenDevices.fluidSorter = SorterBlock.make("fluid_sorter", true);
		WoodenDevices.windmill = new WindmillBlock("windmill");
		WoodenDevices.watermill = new WatermillBlock("watermill");
		WoodenDecoration.treatedPost = new PostBlock("treated_post", standardWoodPropertiesNoOverlay);
		WoodenDevices.treatedWallmount = new WallmountBlock("treated_wallmount", standardWoodPropertiesNoOverlay);
		Misc.hempPlant = new HempBlock("hemp");
		Misc.pottedHemp = new PottedHempBlock("potted_hemp");
		WoodenDecoration.sawdust = new SawdustBlock();

		Cloth.cushion = new CushionBlock();
		Cloth.balloon = new BalloonBlock();
		Cloth.curtain = new StripCurtainBlock();
		Cloth.shaderBanner = new ShaderBannerStandingBlock();
		Cloth.shaderBannerWall = new ShaderBannerWallBlock();

		Misc.fakeLight = new FakeLightBlock();


		Block.Properties defaultMetalProperties = Block.Properties.of(Material.METAL)
				.sound(SoundType.METAL)
				.requiresCorrectToolForDrops()
				.harvestTool(ToolType.PICKAXE)
				.strength(3, 15);
		Supplier<BlockBehaviour.Properties> createMetalPropertiesNoOverlay =
				() -> Block.Properties.of(Material.METAL)
						.sound(SoundType.METAL)
						.strength(3, 15)
						.requiresCorrectToolForDrops()
						.harvestTool(ToolType.PICKAXE)
						.isViewBlocking((state, blockReader, pos) -> false);
		Block.Properties metalPropertiesNotSolid = createMetalPropertiesNoOverlay.get().noOcclusion();
		Block.Properties metalPropertiesNoOverlay = createMetalPropertiesNoOverlay.get();
		MetalDecoration.lvCoil = new IEBaseBlock("coil_lv", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.mvCoil = new IEBaseBlock("coil_mv", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.hvCoil = new IEBaseBlock("coil_hv", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.engineeringRS = new IEBaseBlock("rs_engineering", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.engineeringHeavy = new IEBaseBlock("heavy_engineering", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.engineeringLight = new IEBaseBlock("light_engineering", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.generator = new IEBaseBlock("generator", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.radiator = new IEBaseBlock("radiator", defaultMetalProperties, BlockItemIE::new);
		MetalDecoration.steelFence = new IEFenceBlock("steel_fence", metalPropertiesNoOverlay);
		MetalDecoration.aluFence = new IEFenceBlock("alu_fence", metalPropertiesNoOverlay);
		MetalDecoration.steelWallmount = new WallmountBlock("steel_wallmount", metalPropertiesNoOverlay);
		MetalDecoration.aluWallmount = new WallmountBlock("alu_wallmount", metalPropertiesNoOverlay);
		MetalDecoration.steelPost = new PostBlock("steel_post", metalPropertiesNoOverlay);
		MetalDecoration.aluPost = new PostBlock("alu_post", metalPropertiesNoOverlay);
		MetalDecoration.lantern = new LanternBlock("lantern");
		MetalDecoration.slopeSteel = new StructuralArmBlock("steel_slope");
		MetalDecoration.slopeAlu = new StructuralArmBlock("alu_slope");
		for(CoverType t : CoverType.values())
			MetalDecoration.metalLadder.put(t, new MetalLadderBlock(t));
		for(MetalScaffoldingType type : MetalScaffoldingType.values())
		{
			String name = type.name().toLowerCase(Locale.ENGLISH);
			IEBaseBlock steelBlock = new ScaffoldingBlock("steel_scaffolding_"+name, metalPropertiesNotSolid);
			IEBaseBlock aluBlock = new ScaffoldingBlock("alu_scaffolding_"+name, metalPropertiesNotSolid);
			MetalDecoration.steelScaffolding.put(type, steelBlock);
			MetalDecoration.aluScaffolding.put(type, aluBlock);
			MetalDecoration.steelScaffoldingStair.put(type, new IEStairsBlock("stairs_steel_scaffolding_"+name,
					metalPropertiesNotSolid, steelBlock));
			MetalDecoration.aluScaffoldingStair.put(type, new IEStairsBlock("stairs_alu_scaffolding_"+name,
					metalPropertiesNotSolid, aluBlock));
			addSlabFor(steelBlock);
			addSlabFor(aluBlock);
		}
		for(String cat : new String[]{WireType.LV_CATEGORY, WireType.MV_CATEGORY, WireType.HV_CATEGORY})
		{
			Connectors.ENERGY_CONNECTORS.put(
					new ImmutablePair<>(cat, false), BasicConnectorBlock.forPower(cat, false)
			);
			Connectors.ENERGY_CONNECTORS.put(
					new ImmutablePair<>(cat, true), BasicConnectorBlock.forPower(cat, true)
			);
		}

		Connectors.connectorStructural = new BasicConnectorBlock<>("connector_structural", IETileTypes.CONNECTOR_STRUCTURAL);
		Connectors.postTransformer = new PostTransformerBlock();
		Connectors.transformer = new TransformerBlock();
		Connectors.transformerHV = new TransformerHVBlock();
		Connectors.breakerswitch = new BreakerSwitchBlock<>("breaker_switch", IETileTypes.BREAKER_SWITCH);
		Connectors.redstoneBreaker = new BreakerSwitchBlock<>("redstone_breaker", IETileTypes.REDSTONE_BREAKER);
		Connectors.currentTransformer = new EnergyMeterBlock();
		Connectors.connectorRedstone = new BasicConnectorBlock<>("connector_redstone", IETileTypes.CONNECTOR_REDSTONE);
		Connectors.connectorProbe = new BasicConnectorBlock<>("connector_probe", IETileTypes.CONNECTOR_PROBE);
		Connectors.connectorBundled = new BasicConnectorBlock<>("connector_bundled", IETileTypes.CONNECTOR_BUNDLED);

		Connectors.feedthrough = new FeedthroughBlock();

		MetalDevices.fluidPlacer = new IETileProviderBlock<>("fluid_placer", IETileTypes.FLUID_PLACER,
				metalPropertiesNotSolid);
		MetalDevices.razorWire = new RazorWireBlock();
		MetalDevices.toolbox = new HorizontalFacingBlock<>("toolbox_block", IETileTypes.TOOLBOX, metalPropertiesNoOverlay, ($1, $2) -> null);
		MetalDevices.capacitorLV = new IETileProviderBlock<>("capacitor_lv", IETileTypes.CAPACITOR_LV, defaultMetalProperties);
		MetalDevices.capacitorMV = new IETileProviderBlock<>("capacitor_mv", IETileTypes.CAPACITOR_MV, defaultMetalProperties);
		MetalDevices.capacitorHV = new IETileProviderBlock<>("capacitor_hv", IETileTypes.CAPACITOR_HV, defaultMetalProperties);
		MetalDevices.capacitorCreative = new IETileProviderBlock<>("capacitor_creative", IETileTypes.CAPACITOR_CREATIVE, defaultMetalProperties);
		MetalDevices.barrel = BarrelBlock.make("metal_barrel", true);
		MetalDevices.fluidPump = new FluidPumpBlock();
		MetalDevices.blastFurnacePreheater = new BlastFurnacePreheaterBlock();
		MetalDevices.furnaceHeater = new FurnaceHeaterBlock(defaultMetalProperties);
		MetalDevices.dynamo = new HorizontalFacingBlock<>("dynamo", IETileTypes.DYNAMO, defaultMetalProperties);
		MetalDevices.thermoelectricGen = new IETileProviderBlock<>("thermoelectric_generator", IETileTypes.THERMOELECTRIC_GEN,
				defaultMetalProperties);
		MetalDevices.electricLantern = new ElectricLanternBlock("electric_lantern");
		MetalDevices.chargingStation = new HorizontalFacingBlock<>("charging_station", IETileTypes.CHARGING_STATION,
				metalPropertiesNoOverlay);
		MetalDevices.fluidPipe = new FluidPipeBlock(metalPropertiesNoOverlay);
		MetalDevices.sampleDrill = new SampleDrillBlock();
		MetalDevices.teslaCoil = new TeslaCoilBlock();
		MetalDevices.floodlight = new FloodlightBlock("floodlight");
		MetalDevices.turretChem = new TurretBlock<>("turret_chem", IETileTypes.TURRET_CHEM);
		MetalDevices.turretGun = new TurretBlock<>("turret_gun", IETileTypes.TURRET_GUN);
		MetalDevices.cloche = new ClocheBlock();
		for(EnumMetals metal : new EnumMetals[]{EnumMetals.IRON, EnumMetals.STEEL, EnumMetals.ALUMINUM, EnumMetals.COPPER})
			MetalDevices.chutes.put(metal, new ChuteBlock(metal, metalPropertiesNotSolid));

		Multiblocks.cokeOven = new StoneMultiBlock<>("coke_oven", IETileTypes.COKE_OVEN, true);
		Multiblocks.blastFurnace = new StoneMultiBlock<>("blast_furnace", IETileTypes.BLAST_FURNACE, true);
		Multiblocks.alloySmelter = new StoneMultiBlock<>("alloy_smelter", IETileTypes.ALLOY_SMELTER, true);
		Multiblocks.blastFurnaceAdv = new StoneMultiBlock<>("advanced_blast_furnace", IETileTypes.BLAST_FURNACE_ADVANCED, false);
		Multiblocks.crusher = new MetalMultiblockBlock<>("crusher", IETileTypes.CRUSHER);
		Multiblocks.sawmill = new MetalMultiblockBlock<>("sawmill", IETileTypes.SAWMILL);
		Multiblocks.silo = new MetalMultiblockBlock<>("silo", IETileTypes.SILO);
		Multiblocks.tank = new MetalMultiblockBlock<>("tank", IETileTypes.SHEETMETAL_TANK);
		Multiblocks.arcFurnace = new MetalMultiblockBlock<>("arc_furnace", IETileTypes.ARC_FURNACE);
		Multiblocks.assembler = new MetalMultiblockBlock<>("assembler", IETileTypes.ASSEMBLER);
		Multiblocks.autoWorkbench = new MetalMultiblockBlock<>("auto_workbench", IETileTypes.AUTO_WORKBENCH);
		Multiblocks.bucketWheel = new MetalMultiblockBlock<>("bucket_wheel", IETileTypes.BUCKET_WHEEL);
		Multiblocks.excavator = new MetalMultiblockBlock<>("excavator", IETileTypes.EXCAVATOR);
		Multiblocks.metalPress = new MetalMultiblockBlock<>("metal_press", IETileTypes.METAL_PRESS);
		Multiblocks.bottlingMachine = new MetalMultiblockBlock<>("bottling_machine", IETileTypes.BOTTLING_MACHINE);
		Multiblocks.fermenter = new MetalMultiblockBlock<>("fermenter", IETileTypes.FERMENTER);
		Multiblocks.squeezer = new MetalMultiblockBlock<>("squeezer", IETileTypes.SQUEEZER);
		Multiblocks.mixer = new MetalMultiblockBlock<>("mixer", IETileTypes.MIXER);
		Multiblocks.refinery = new MetalMultiblockBlock<>("refinery", IETileTypes.REFINERY);
		Multiblocks.dieselGenerator = new MetalMultiblockBlock<>("diesel_generator", IETileTypes.DIESEL_GENERATOR);
		Multiblocks.lightningrod = new MetalMultiblockBlock<>("lightning_rod", IETileTypes.LIGHTNING_ROD);

		Tools.hammer = new HammerItem();
		Tools.wirecutter = new WirecutterItem();
		Tools.screwdriver = new ScrewdriverItem();
		Tools.voltmeter = new VoltmeterItem();
		Tools.manual = new ManualItem();
		IEItems.Tools.steelPick = IETools.createPickaxe(Lib.MATERIAL_Steel, "pickaxe_steel");
		IEItems.Tools.steelShovel = IETools.createShovel(Lib.MATERIAL_Steel, "shovel_steel");
		IEItems.Tools.steelAxe = IETools.createAxe(Lib.MATERIAL_Steel, "axe_steel");
		IEItems.Tools.steelHoe = IETools.createHoe(Lib.MATERIAL_Steel, "hoe_steel");
		IEItems.Tools.steelSword = IETools.createSword(Lib.MATERIAL_Steel, "sword_steel");
		for(EquipmentSlot slot : EquipmentSlot.values())
			if(slot.getType()==Type.ARMOR)
				IEItems.Tools.steelArmor.put(slot, new SteelArmorItem(slot));
		Tools.toolbox = new ToolboxItem();
		IEItems.Misc.hempSeeds = new IESeedItem(Misc.hempPlant);
		IEItems.Ingredients.stickTreated = new IEBaseItem("stick_treated");
		IEItems.Ingredients.stickIron = new IEBaseItem("stick_iron");
		IEItems.Ingredients.stickSteel = new IEBaseItem("stick_steel");
		IEItems.Ingredients.stickAluminum = new IEBaseItem("stick_aluminum");
		IEItems.Ingredients.hempFiber = new IEBaseItem("hemp_fiber");
		IEItems.Ingredients.hempFabric = new IEBaseItem("hemp_fabric");
		IEItems.Ingredients.coalCoke = new IEBaseItem("coal_coke")
				.setBurnTime(3200);
		IEItems.Ingredients.slag = new IEBaseItem("slag");
		IEItems.Ingredients.componentIron = new IEBaseItem("component_iron");
		IEItems.Ingredients.componentSteel = new IEBaseItem("component_steel");
		IEItems.Ingredients.waterwheelSegment = new IEBaseItem("waterwheel_segment");
		IEItems.Ingredients.windmillBlade = new IEBaseItem("windmill_blade");
		IEItems.Ingredients.windmillSail = new IEBaseItem("windmill_sail");
		IEItems.Ingredients.woodenGrip = new IEBaseItem("wooden_grip");
		IEItems.Ingredients.gunpartBarrel = new RevolverpartItem("gunpart_barrel");
		IEItems.Ingredients.gunpartDrum = new RevolverpartItem("gunpart_drum");
		IEItems.Ingredients.gunpartHammer = new RevolverpartItem("gunpart_hammer");
		IEItems.Ingredients.dustCoke = new IEBaseItem("dust_coke");
		IEItems.Ingredients.dustHopGraphite = new IEBaseItem("dust_hop_graphite");
		IEItems.Ingredients.ingotHopGraphite = new IEBaseItem("ingot_hop_graphite");
		IEItems.Ingredients.wireCopper = new IEBaseItem("wire_copper");
		IEItems.Ingredients.wireElectrum = new IEBaseItem("wire_electrum");
		IEItems.Ingredients.wireAluminum = new IEBaseItem("wire_aluminum");
		IEItems.Ingredients.wireSteel = new IEBaseItem("wire_steel");
		IEItems.Ingredients.wireLead = new IEBaseItem("wire_lead");
		IEItems.Ingredients.dustSaltpeter = new IEBaseItem("dust_saltpeter");
		IEItems.Ingredients.dustSulfur = new IEBaseItem("dust_sulfur");
		IEItems.Ingredients.dustWood = new IEBaseItem("dust_wood")
				.setBurnTime(100);
		IEItems.Ingredients.electronTube = new IEBaseItem("electron_tube");
		IEItems.Ingredients.circuitBoard = new IEBaseItem("circuit_board");
		IEItems.Ingredients.emptyCasing = new IEBaseItem("empty_casing");
		IEItems.Ingredients.emptyShell = new IEBaseItem("empty_shell");
		for(WireType t : WireType.getIEWireTypes())
			IEItems.Misc.wireCoils.put(t, new WireCoilItem(t));
		Item.Properties moldProperties = new Item.Properties().stacksTo(1);
		Molds.moldPlate = new IEBaseItem("mold_plate", moldProperties);
		Molds.moldGear = new IEBaseItem("mold_gear", moldProperties);
		Molds.moldRod = new IEBaseItem("mold_rod", moldProperties);
		Molds.moldBulletCasing = new IEBaseItem("mold_bullet_casing", moldProperties);
		Molds.moldWire = new IEBaseItem("mold_wire", moldProperties);
		Molds.moldPacking4 = new IEBaseItem("mold_packing_4", moldProperties);
		Molds.moldPacking9 = new IEBaseItem("mold_packing_9", moldProperties);
		Molds.moldUnpacking = new IEBaseItem("mold_unpacking", moldProperties);
		IEItems.Misc.graphiteElectrode = new GraphiteElectrodeItem();
		IEItems.Misc.coresample = new CoresampleItem();
		Tools.drill = new DrillItem();
		Tools.drillheadIron = new DrillheadItem(DrillheadItem.IRON);
		Tools.drillheadSteel = new DrillheadItem(DrillheadItem.STEEL);
		Tools.buzzsaw = new BuzzsawItem();
		Tools.sawblade = new SawbladeItem("sawblade", 10000, 8f, 9f);
		Tools.rockcutter = new RockcutterItem("rockcutter", 5000, 5f, 9f);
		Tools.surveyTools = new SurveyToolsItem();
		Weapons.revolver = new RevolverItem();
		Weapons.speedloader = new SpeedloaderItem();
		Weapons.chemthrower = new ChemthrowerItem();
		Weapons.railgun = new RailgunItem();
		for(ResourceLocation bulletType : BulletHandler.getAllKeys())
		{
			IBullet bullet = BulletHandler.getBullet(bulletType);
			if(bullet.isProperCartridge())
				Weapons.bullets.put(bullet, new BulletItem(bullet));
		}
		IEItems.Misc.powerpack = new PowerpackItem();
		for(ToolUpgrade upgrade : ToolUpgrade.values())
			IEItems.Misc.toolUpgrades.put(upgrade, new ToolUpgradeItem(upgrade));
		IEItems.Misc.jerrycan = new JerrycanItem();
		IEItems.Misc.shader = new ShaderItem();
		IEItems.Misc.blueprint = new EngineersBlueprintItem();
		IEItems.Misc.earmuffs = new EarmuffsItem();
		for(EquipmentSlot slot : EquipmentSlot.values())
			if(slot.getType()==Type.ARMOR)
				IEItems.Misc.faradaySuit.put(slot, new FaradaySuitItem(slot));
		IEItems.Misc.fluorescentTube = new FluorescentTubeItem();
		IEItems.Misc.shield = new IEShieldItem();
		IEItems.Misc.skyhook = new SkyhookItem();
		IEItems.Misc.maintenanceKit = new MaintenanceKitItem();
		IEItems.Misc.cartWoodenCrate = new IEMinecartItem("woodencrate")
		{
			@Override
			public IEMinecartEntity createCart(Level world, double x, double y, double z, ItemStack stack)
			{
				return new CrateMinecartEntity(CrateMinecartEntity.TYPE, world, x, y, z);
			}
		};
		IEItems.Misc.cartReinforcedCrate = new IEMinecartItem("reinforcedcrate")
		{
			@Override
			public IEMinecartEntity createCart(Level world, double x, double y, double z, ItemStack stack)
			{
				return new ReinforcedCrateMinecartEntity(ReinforcedCrateMinecartEntity.TYPE, world, x, y, z);
			}
		};
		IEItems.Misc.cartWoodenBarrel = new IEMinecartItem("woodenbarrel")
		{
			@Override
			public IEMinecartEntity createCart(Level world, double x, double y, double z, ItemStack stack)
			{
				return new BarrelMinecartEntity(BarrelMinecartEntity.TYPE, world, x, y, z);
			}
		};
		IEItems.Misc.cartMetalBarrel = new IEMinecartItem("metalbarrel")
		{
			@Override
			public IEMinecartEntity createCart(Level world, double x, double y, double z, ItemStack stack)
			{
				return new MetalBarrelMinecartEntity(MetalBarrelMinecartEntity.TYPE, world, x, y, z);
			}
		};
		IEItems.Misc.logicCircuitBoard = new LogicCircuitBoardItem();

		IEItems.Misc.bannerPatternHammer = addBanner("hammer", "hmr");
		IEItems.Misc.bannerPatternBevels = addBanner("bevels", "bvl");
		IEItems.Misc.bannerPatternOrnate = addBanner("ornate", "orn");
		IEItems.Misc.bannerPatternTreatedWood = addBanner("treated_wood", "twd");
		IEItems.Misc.bannerPatternWindmill = addBanner("windmill", "wnd");
		IEItems.Misc.bannerPatternWolfR = addBanner("wolf_r", "wlfr");
		IEItems.Misc.bannerPatternWolfL = addBanner("wolf_l", "wlfl");
		IEItems.Misc.bannerPatternWolf = addBanner("wolf", "wlf");

		IEItems.Misc.iconBirthday = new FakeIconItem("birthday");
		IEItems.Misc.iconLucky = new FakeIconItem("lucky");
		IEItems.Misc.iconDrillbreak = new FakeIconItem("drillbreak");
		IEItems.Misc.iconRavenholm = new FakeIconItem("ravenholm");

		createConveyorBlocks();
		BulletHandler.emptyCasing = new ItemStack(Ingredients.emptyCasing);
		BulletHandler.emptyShell = new ItemStack(Ingredients.emptyShell);
		IEWireTypes.setup();
		EntityDataSerializers.registerSerializer(IEFluid.OPTIONAL_FLUID_STACK);

		ClocheRenderFunctions.init();

		DeferredWorkQueue.runLater(IELootFunctions::register);
		IEShaders.commonConstruction();
		IEMultiblocks.init();
		BlueprintCraftingRecipe.registerDefaultCategories();
		IETileTypes.REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		IEStats.init();
		populateAPI();
	}

	public static void createConveyorBlocks()
	{
		for(ResourceLocation rl : ConveyorHandler.classRegistry.keySet())
		{
			Block b = new ConveyorBlock(rl);
			ConveyorHandler.conveyorBlocks.put(rl, b);
		}
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event)
	{
		checkNonNullNames(registeredIEBlocks);
		for(Block block : registeredIEBlocks)
			event.getRegistry().register(block);
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		for(Rarity r : ShaderRegistry.rarityWeightMap.keySet())
			IEItems.Misc.shaderBag.put(r, new ShaderBagItem(r));
		checkNonNullNames(registeredIEItems);
		for(Item item : registeredIEItems)
			event.getRegistry().register(item);
	}

	@SubscribeEvent
	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event)
	{
		event.getRegistry().register(ORE_RETROGEN.setRegistryName(new ResourceLocation(ImmersiveEngineering.MODID, "ore_retro")));
	}

	private static <T extends IForgeRegistryEntry<T>> void checkNonNullNames(Collection<T> coll)
	{
		int numNull = 0;
		for(T b : coll)
			if(b.getRegistryName()==null)
			{
				IELogger.logger.info("Null name for {} (class {})", b, b.getClass());
				++numNull;
			}
		if(numNull > 0)
			System.exit(1);
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event)
	{
		checkNonNullNames(registeredIEFluids);
		for(Fluid fluid : registeredIEFluids)
			event.getRegistry().register(fluid);
	}

	@SubscribeEvent
	public static void registerPotions(RegistryEvent.Register<MobEffect> event)
	{
		/*POTIONS*/
		IEPotions.init();
	}

	private static <T extends Block & IIEBlock> BlockIESlab addSlabFor(T b)
	{
		BlockIESlab<T> ret = new BlockIESlab<>(
				"slab_"+b.getRegistryName().getPath(),
				Block.Properties.copy(b),
				BlockItemIE::new,
				b
		);
		IEBlocks.toSlab.put(b, ret);
		return ret;
	}

	@SubscribeEvent
	public static void registerEntityTypes(RegistryEvent.Register<EntityType<?>> event)
	{
		event.getRegistry().registerAll(
				ChemthrowerShotEntity.TYPE,
				FluorescentTubeEntity.TYPE,
				IEExplosiveEntity.TYPE,
				RailgunShotEntity.TYPE,
				RevolvershotEntity.TYPE,
				RevolvershotFlareEntity.TYPE,
				RevolvershotHomingEntity.TYPE,
				SkylineHookEntity.TYPE,
				WolfpackShotEntity.TYPE,
				CrateMinecartEntity.TYPE,
				ReinforcedCrateMinecartEntity.TYPE,
				BarrelMinecartEntity.TYPE,
				MetalBarrelMinecartEntity.TYPE,
				SawbladeEntity.TYPE
		);
	}

	@SubscribeEvent
	public static void registerTEs(RegistryEvent.Register<BlockEntityType<?>> event)
	{
		ConveyorBeltTileEntity.registerConveyorTEs(event);
	}

	public static void init(ParallelDispatchEvent ev)
	{
		/*WORLDGEN*/
		ev.enqueueWork(
				() -> {
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.COPPER), "copper", IEServerConfig.ORES.ore_copper);
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.ALUMINUM), "bauxite", IEServerConfig.ORES.ore_bauxite);
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.LEAD), "lead", IEServerConfig.ORES.ore_lead);
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.SILVER), "silver", IEServerConfig.ORES.ore_silver);
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.NICKEL), "nickel", IEServerConfig.ORES.ore_nickel);
					IEWorldGen.addOreGen(Metals.ores.get(EnumMetals.URANIUM), "uranium", IEServerConfig.ORES.ore_uranium);
					IEWorldGen.registerMineralVeinGen();
				}
		);

		CapabilityShader.register();
		CapabilityInit.register();
		CapabilitySkyhookData.register();
		CapabilityRedstoneNetwork.register();
		ShaderRegistry.itemShader = IEItems.Misc.shader;
		ShaderRegistry.itemShaderBag = IEItems.Misc.shaderBag;
		ShaderRegistry.itemExamples.add(new ItemStack(Weapons.revolver));
		ShaderRegistry.itemExamples.add(new ItemStack(Tools.drill));
		ShaderRegistry.itemExamples.add(new ItemStack(Weapons.chemthrower));
		ShaderRegistry.itemExamples.add(new ItemStack(Weapons.railgun));
		ShaderRegistry.itemExamples.add(new ItemStack(IEItems.Misc.shield));

		/*ASSEMBLER RECIPE ADAPTERS*/
		//Fluid Ingredients
		AssemblerHandler.registerSpecialIngredientConverter((o) ->
		{
			if(o instanceof IngredientFluidStack)
				return new FluidTagRecipeQuery(((IngredientFluidStack)o).getFluidTagInput());
			else
				return null;
		});
		// Buckets
		// TODO add "duplicates" of the fluid-aware recipes that only use buckets, so that other mods using similar
		//  code don't need explicit compat?
		AssemblerHandler.registerSpecialIngredientConverter((o) ->
		{
			final ItemStack[] matching = o.getItems();
			if(!o.isVanilla()||matching.length!=1)
				return null;
			final Item potentialBucket = matching[0].getItem();
			if(!(potentialBucket instanceof BucketItem))
				return null;
			//Explicitly check for vanilla-style non-dynamic container items
			//noinspection deprecation
			if(!potentialBucket.hasCraftingRemainingItem()||potentialBucket.getCraftingRemainingItem()!=Items.BUCKET)
				return null;
			final Fluid contained = ((BucketItem)potentialBucket).getFluid();
			return new FluidStackRecipeQuery(new FluidStack(contained, FluidAttributes.BUCKET_VOLUME));
		});
		// Milk is a weird special case
		AssemblerHandler.registerSpecialIngredientConverter(o -> {
			final ItemStack[] matching = o.getItems();
			if(!o.isVanilla()||matching.length!=1||matching[0].getItem()!=Items.MILK_BUCKET||!ForgeMod.MILK.isPresent())
				return null;
			return new FluidStackRecipeQuery(new FluidStack(ForgeMod.MILK.get(), FluidAttributes.BUCKET_VOLUME));
		});

		fluidCreosote.block.setEffect(IEPotions.flammable, 100, 0);
		fluidEthanol.block.setEffect(MobEffects.CONFUSION, 70, 0);
		fluidBiodiesel.block.setEffect(IEPotions.flammable, 100, 1);
		fluidConcrete.block.setEffect(MobEffects.MOVEMENT_SLOWDOWN, 20, 3);
		fluidCreosote.setBurnTime(800);

		ChemthrowerEffects.register();

		RailgunProjectiles.register();

		ExternalHeaterHandler.registerHeatableAdapter(FurnaceBlockEntity.class, new DefaultFurnaceAdapter());

		ThermoelectricHandler.registerSourceInKelvin(Blocks.MAGMA_BLOCK, 1300);
		//TODO tags?
		ThermoelectricHandler.registerSourceInKelvin(Blocks.ICE, 273);
		ThermoelectricHandler.registerSourceInKelvin(Blocks.PACKED_ICE, 240);
		ThermoelectricHandler.registerSourceInKelvin(Blocks.BLUE_ICE, 200);
		ThermoelectricHandler.registerSourceInKelvin(IETags.getTagsFor(EnumMetals.URANIUM).storage, 2000);
		//ThermoelectricHandler.registerSourceInKelvin(new ResourceLocation("forge:storage_blocks/yellorium"), 2000);
		//ThermoelectricHandler.registerSourceInKelvin(new ResourceLocation("forge:storage_blocks/plutonium"), 4000);
		//ThermoelectricHandler.registerSourceInKelvin(new ResourceLocation("forge:storage_blocks/blutonium"), 4000);

		/*MULTIBLOCKS*/
		MultiblockHandler.registerMultiblock(IEMultiblocks.FEEDTHROUGH);
		MultiblockHandler.registerMultiblock(IEMultiblocks.LIGHTNING_ROD);
		MultiblockHandler.registerMultiblock(IEMultiblocks.DIESEL_GENERATOR);
		MultiblockHandler.registerMultiblock(IEMultiblocks.REFINERY);
		MultiblockHandler.registerMultiblock(IEMultiblocks.MIXER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.SQUEEZER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.FERMENTER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.BOTTLING_MACHINE);
		MultiblockHandler.registerMultiblock(IEMultiblocks.COKE_OVEN);
		MultiblockHandler.registerMultiblock(IEMultiblocks.ALLOY_SMELTER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.BLAST_FURNACE);
		MultiblockHandler.registerMultiblock(IEMultiblocks.CRUSHER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.SAWMILL);
		MultiblockHandler.registerMultiblock(IEMultiblocks.ADVANCED_BLAST_FURNACE);
		MultiblockHandler.registerMultiblock(IEMultiblocks.METAL_PRESS);
		MultiblockHandler.registerMultiblock(IEMultiblocks.ASSEMBLER);
		MultiblockHandler.registerMultiblock(IEMultiblocks.AUTO_WORKBENCH);
		MultiblockHandler.registerMultiblock(IEMultiblocks.EXCAVATOR);
		MultiblockHandler.registerMultiblock(IEMultiblocks.BUCKET_WHEEL);
		MultiblockHandler.registerMultiblock(IEMultiblocks.ARC_FURNACE);
		MultiblockHandler.registerMultiblock(IEMultiblocks.SILO);
		MultiblockHandler.registerMultiblock(IEMultiblocks.SHEETMETAL_TANK);
		MultiblockHandler.registerMultiblock(IEMultiblocks.EXCAVATOR_DEMO);

		/*BLOCK ITEMS FROM CRATES*/
		IEApi.forbiddenInCrates.add(
				stack -> stack.getItem().is(IETags.forbiddenInCrates)||
						Block.byItem(stack.getItem()) instanceof ShulkerBoxBlock
		);

		FluidPipeTileEntity.initCovers();
		LocalNetworkHandler.register(EnergyTransferHandler.ID, EnergyTransferHandler::new);
		LocalNetworkHandler.register(RedstoneNetworkHandler.ID, RedstoneNetworkHandler::new);
		LocalNetworkHandler.register(WireDamageHandler.ID, WireDamageHandler::new);

		ev.enqueueWork(() -> {
			Villages.init();
			ShaderRegistry.compileWeight();
		});
	}

	public static Item addBanner(String name, String id)
	{
		String enumName = MODID+"_"+name;
		id = "ie_"+id;
		BannerPattern pattern = BannerPattern.create(enumName.toUpperCase(), enumName, id, true);
		Item patternItem = new BannerPatternItem(pattern, new Item.Properties().tab(ImmersiveEngineering.ITEM_GROUP));
		patternItem.setRegistryName(ImmersiveEngineering.MODID, "bannerpattern_"+name);
		IEContent.registeredIEItems.add(patternItem);
		return patternItem;
	}

	public static void populateAPI()
	{
		SetRestrictedField.startInitializing(false);
		ApiUtils.disableTicking.setValue(EventHandler.REMOVE_FROM_TICKING::add);
		IngredientWithSize.SERIALIZER.setValue(IngredientWithSizeSerializer.INSTANCE);
		BlueprintCraftingRecipe.blueprintItem.setValue(IEItems.Misc.blueprint);
		ExcavatorHandler.setSetDirtyCallback(IESaveData::markInstanceDirty);
		TemplateMultiblock.setCallbacks(
				bs -> Utils.getPickBlock(
						bs, new BlockHitResult(Vec3.ZERO, Direction.DOWN, BlockPos.ZERO, false),
						ImmersiveEngineering.proxy.getClientPlayer()
				),
				(loc, server) -> {
					try
					{
						return StaticTemplateManager.loadStaticTemplate(loc, server);
					} catch(IOException e)
					{
						throw new RuntimeException(e);
					}
				},
				template -> ((TemplateAccess)template).getPalettes()
		);
		defaultAdapter = new DefaultAssemblerAdapter();
		WirecoilUtils.COIL_USE.setValue(WireCoilItem::doCoilUse);
		AssemblerHandler.registerRecipeAdapter(Recipe.class, defaultAdapter);
		BulletHandler.GET_BULLET_ITEM.setValue(Weapons.bullets::get);
		ChemthrowerHandler.SOLIDIFY_CONCRETE_POWDER.setValue(
				(world, pos) -> {
					Block b = world.getBlockState(pos).getBlock();
					if(b instanceof ConcretePowderBlock)
						world.setBlock(pos, ((ConcretePowderBlockAccess)b).getConcrete(), 3);
				}
		);
		WireDamageHandler.GET_WIRE_DAMAGE.setValue(IEDamageSources::causeWireDamage);
		GlobalWireNetwork.SANITIZE_CONNECTIONS.setValue(IEServerConfig.WIRES.sanitizeConnections::get);
		GlobalWireNetwork.VALIDATE_CONNECTIONS.setValue(IECommonConfig.validateNet::get);
		ConveyorHandler.ITEM_AGE_ACCESS.setValue(new ItemAgeAccessor()
		{
			@Override
			public int getAgeNonsided(ItemEntity entity)
			{
				return ((ItemEntityAccess)entity).getAgeNonsided();
			}

			@Override
			public void setAge(ItemEntity entity, int newAge)
			{
				((ItemEntityAccess)entity).setAge(newAge);
			}
		});
		TemplateWorldCreator.CREATOR.setValue(TemplateWorld::new);
		SetRestrictedField.lock(false);
	}
}
