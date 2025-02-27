/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.items;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.excavator.ExcavatorHandler;
import blusunrize.immersiveengineering.api.excavator.MineralMix;
import blusunrize.immersiveengineering.api.excavator.MineralVein;
import blusunrize.immersiveengineering.api.excavator.MineralWorldInfo;
import blusunrize.immersiveengineering.client.utils.TimestampFormat;
import blusunrize.immersiveengineering.common.blocks.IEBaseBlock;
import blusunrize.immersiveengineering.common.blocks.IEBlocks.StoneDecoration;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import blusunrize.immersiveengineering.common.util.Utils;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CoresampleItem extends IEBaseItem
{
	public CoresampleItem()
	{
		super("coresample", new Properties().tab(ImmersiveEngineering.ITEM_GROUP));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> list, TooltipFlag flag)
	{
		getCoresampleInfo(stack, list, ChatFormatting.GRAY, world, true, true);
	}

	public static void getCoresampleInfo(ItemStack coresample, List<Component> list, ChatFormatting baseColor, @Nullable Level world, boolean showYield, boolean showTimestamp)
	{
		if(coresample.getOrCreateTag().contains("coords"))
			list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.outdated"));

		ColumnPos coords = getCoords(coresample);
		if(coords!=null)
		{
			List<VeinSampleData> veins = getVeins(coresample);
			if(!veins.isEmpty())
				veins.forEach(data -> {
					MutableComponent component = new TextComponent(
							Utils.formatDouble(data.getPercentageInTotalSample()*100, "0.00")+"% "
					);
					MineralMix mineral = data.getType();
					component.append(new TranslatableComponent(mineral.getTranslationKey()));
					list.add(component.withStyle(baseColor));
					if(showYield)
					{
						component = new TextComponent("  ");
						component.append(new TranslatableComponent(Lib.DESC_INFO+"coresample.saturation",
								Utils.formatDouble(data.getSaturation()*100, "0.00")
						));
						list.add(component.withStyle(ChatFormatting.DARK_GRAY));

						component = new TextComponent("  ");
						int yield = ExcavatorHandler.mineralVeinYield-data.getDepletion();
						yield *= (1-mineral.failChance);
						if(ExcavatorHandler.mineralVeinYield==0)
							component.append(new TranslatableComponent(Lib.DESC_INFO+"coresample.infinite"));
						else
							component.append(new TranslatableComponent(Lib.DESC_INFO+"coresample.yield",
									yield));
						list.add(component.withStyle(ChatFormatting.DARK_GRAY));
					}
				});
			else
				list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.noMineral").withStyle(baseColor));

			ResourceKey<Level> dimension = getDimension(coresample);
			if(dimension!=null)
			{
				String s2 = dimension.location().getPath();
				if(s2.toLowerCase(Locale.ENGLISH).startsWith("the_"))
					s2 = s2.substring(4);
				list.add(new TextComponent(Utils.toCamelCase(s2)).withStyle(baseColor));
			}
			ColumnPos pos = getCoords(coresample);
			if(pos!=null)
				list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.pos", pos.x, pos.z).withStyle(baseColor));

			if(showTimestamp)
			{
				boolean hasStamp = ItemNBTHelper.hasKey(coresample, "timestamp");
				if(hasStamp&&world!=null)
				{
					long timestamp = ItemNBTHelper.getLong(coresample, "timestamp");
					long dist = world.getGameTime()-timestamp;
					if(dist < 0)
						list.add(new TextComponent("Somehow this sample is dated in the future...are you a time traveller?!").withStyle(ChatFormatting.RED));
					else
						list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.timestamp", TimestampFormat.formatTimestamp(dist, TimestampFormat.DHM)).withStyle(baseColor));
				}
				else if(hasStamp)
					list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.timezone").withStyle(baseColor));
				else
					list.add(new TranslatableComponent(Lib.DESC_INFO+"coresample.noTimestamp").withStyle(baseColor));
			}
		}
	}


	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		if(player!=null&&player.isShiftKeyDown())
		{
			Level world = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			Direction side = ctx.getClickedFace();
			BlockState state = world.getBlockState(pos);
			BlockPlaceContext blockCtx = new BlockPlaceContext(ctx);
			if(!state.canBeReplaced(blockCtx))
				pos = pos.relative(side);

			if(!stack.isEmpty()&&player.mayUseItemAt(pos, side, stack)&&world.getBlockState(pos).canBeReplaced(blockCtx))
			{
				BlockState coresample = StoneDecoration.coresample.defaultBlockState();
				if(world.setBlock(pos, coresample, 3))
				{
					((IEBaseBlock)StoneDecoration.coresample).onIEBlockPlacedBy(blockCtx, coresample);
					SoundType soundtype = world.getBlockState(pos).getBlock().getSoundType(world.getBlockState(pos), world, pos, player);
					world.playSound(player, pos, soundtype.getPlaceSound(), SoundSource.BLOCKS, (soundtype.getVolume()+1.0F)/2.0F, soundtype.getPitch()*0.8F);
					stack.shrink(1);
				}
				return InteractionResult.SUCCESS;
			}
			else
				return InteractionResult.FAIL;
		}
		return super.useOn(ctx);
	}

	public static MineralMix[] getMineralMixes(ItemStack coresample)
	{
		return getVeins(coresample)
				.stream()
				.map(VeinSampleData::getType)
				.toArray(MineralMix[]::new);
	}

	public static ListTag getSimplifiedMineralList(ItemStack coresample)
	{
		ListTag outList = new ListTag();
		getVeins(coresample).stream()
				.map(VeinSampleData::getType)
				.map(MineralMix::getId)
				.map(ResourceLocation::toString)
				.map(StringTag::valueOf)
				.forEach(outList::add);
		return outList;
	}

	public static void setMineralInfo(ItemStack stack, MineralWorldInfo info, BlockPos pos)
	{
		if(info==null)
			return;
		List<Pair<MineralVein, Integer>> veins = info.getAllVeins();
		ListTag nbtList = new ListTag();
		veins.forEach(pair -> {
			VeinSampleData sampleData = new VeinSampleData(
					pair.getLeft().getMineral(),
					pair.getRight()/(double)info.getTotalWeight(),
					1-pair.getLeft().getFailChance(pos),
					pair.getLeft().getDepletion()
			);
			nbtList.add(sampleData.toNBT());
		});
		stack.getOrCreateTag().put("mineralInfo", nbtList);
	}

	public static List<VeinSampleData> getVeins(ItemStack stack)
	{
		if(!ItemNBTHelper.hasKey(stack, "mineralInfo", NBT.TAG_LIST))
			return ImmutableList.of();
		List<VeinSampleData> veins = new ArrayList<>();
		ListTag mineralInfoNBT = stack.getOrCreateTag().getList("mineralInfo", NBT.TAG_COMPOUND);
		for(Tag vein : mineralInfoNBT)
		{
			VeinSampleData data = VeinSampleData.fromNBT((CompoundTag)vein);
			if(data!=null)
				veins.add(data);
		}
		return veins;
	}

	@Nullable
	public static ColumnPos getCoords(@Nullable ItemStack stack)
	{
		if(stack!=null&&stack.hasTag()&&stack.getOrCreateTag().contains("x"))
			return new ColumnPos(stack.getOrCreateTag().getInt("x"), stack.getOrCreateTag().getInt("z"));
		else
			return null;
	}

	public static void setCoords(ItemStack stack, BlockPos pos)
	{
		stack.getOrCreateTag().putInt("x", pos.getX());
		stack.getOrCreateTag().putInt("z", pos.getZ());
	}

	@Nullable
	public static ResourceKey<Level> getDimension(ItemStack stack)
	{
		if(stack.hasTag()&&stack.getOrCreateTag().contains("dimension"))
		{
			ResourceLocation name = new ResourceLocation(stack.getOrCreateTag().getString("dimension"));
			return ResourceKey.create(Registry.DIMENSION_REGISTRY, name);
		}
		return null;
	}


	public static void setDimension(ItemStack stack, ResourceKey<Level> dimension)
	{
		stack.getOrCreateTag().putString("dimension", dimension.location().toString());
	}

	public static class VeinSampleData
	{
		private final MineralMix type;
		private final double percentageInTotalSample;
		private final double saturation;
		private final int depletion;

		public VeinSampleData(MineralMix type, double percentageInTotalSample, double saturation, int depletion)
		{
			this.type = type;
			this.percentageInTotalSample = percentageInTotalSample;
			this.saturation = saturation;
			this.depletion = depletion;
		}

		@Nullable
		public static VeinSampleData fromNBT(CompoundTag nbt)
		{
			MineralMix mineral = MineralMix.mineralList.get(new ResourceLocation(nbt.getString("mineral")));
			if(mineral==null)
				return null;
			return new VeinSampleData(
					mineral, nbt.getDouble("percentage"), nbt.getDouble("saturation"), nbt.getInt("depletion")
			);
		}

		public CompoundTag toNBT()
		{
			CompoundTag tag = new CompoundTag();
			tag.putDouble("percentage", percentageInTotalSample);
			tag.putString("mineral", type.getId().toString());
			tag.putInt("depletion", depletion);
			tag.putDouble("saturation", saturation);
			return tag;
		}

		public MineralMix getType()
		{
			return type;
		}

		public double getPercentageInTotalSample()
		{
			return percentageInTotalSample;
		}

		public double getSaturation()
		{
			return saturation;
		}

		public int getDepletion()
		{
			return depletion;
		}
	}
}
