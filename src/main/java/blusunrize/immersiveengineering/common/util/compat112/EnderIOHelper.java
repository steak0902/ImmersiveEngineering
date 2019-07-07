/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.compat112;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler.ChemthrowerEffect_Potion;
import blusunrize.immersiveengineering.api.tool.ConveyorHandler;
import blusunrize.immersiveengineering.api.tool.ConveyorHandler.IConveyorTile;
import blusunrize.immersiveengineering.common.IERecipes;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;

import java.util.function.BiConsumer;

/**
 * @author BluSunrize - 22.08.2016
 */
public class EnderIOHelper extends IECompatModule
{
	public static final String EIO_MAGNET_NBT = "EIOpuller";

	@Override
	public void preInit()
	{
	}

	@Override
	public void registerRecipes()
	{
		IERecipes.addOreDictArcAlloyingRecipe("ingotElectricalSteel", 1, "Iron", 400, 512, "dustCoal", "itemSilicon");
		IERecipes.addOreDictArcAlloyingRecipe("ingotEnergeticAlloy", 1, "Gold", 200, 512, "dustRedstone", "dustGlowstone");
		IERecipes.addOreDictArcAlloyingRecipe("ingotPhaseGold", 1, "EnergeticAlloy", 200, 512, Items.ENDER_PEARL);
		IERecipes.addOreDictArcAlloyingRecipe("ingotPhasedIron", 1, "Iron", 200, 512, Items.ENDER_PEARL);
		IERecipes.addOreDictArcAlloyingRecipe("ingotConductiveIron", 1, "Iron", 100, 512, "dustRedstone");
		IERecipes.addOreDictArcAlloyingRecipe("ingotDarkSteel", 1, "Iron", 400, 512, "dustCoal", "obsidian");
		IERecipes.addOreDictArcAlloyingRecipe("ingotSoularium", 1, "Gold", 200, 512, Blocks.SOUL_SAND);
	}

	@Override
	public void init()
	{
		ChemthrowerHandler.registerEffect("nutrient_distillation", new ChemthrowerEffect_Potion(null, 0, Effects.NAUSEA, 80, 1));
		ChemthrowerHandler.registerEffect("liquid_sunshine", new ChemthrowerEffect_Potion(null, 0, new EffectInstance(Effects.GLOWING, 200, 0), new EffectInstance(Effects.LEVITATION, 40, 0)));
		ChemthrowerHandler.registerEffect("cloud_seed_concentrated", new ChemthrowerEffect_Potion(null, 0, Effects.BLINDNESS, 40, 0));
		ChemthrowerHandler.registerEffect("vapor_of_levity", new ChemthrowerEffect_Potion(null, 0, Effects.LEVITATION, 80, 2));

		ConveyorHandler.registerMagnetSupression(new BiConsumer<Entity, IConveyorTile>()
		{
			@Override
			public void accept(Entity entity, IConveyorTile iConveyorTile)
			{
				if(entity instanceof ItemEntity)
				{
					CompoundNBT data = entity.getEntityData();
					long pos = ((TileEntity)iConveyorTile).getPos().toLong();
					if(!data.hasKey(EIO_MAGNET_NBT)||data.getLong(EIO_MAGNET_NBT)!=pos)
						data.setLong(EIO_MAGNET_NBT, pos);
				}
			}
		}, new BiConsumer<Entity, IConveyorTile>()
		{
			@Override
			public void accept(Entity entity, IConveyorTile iConveyorTile)
			{
				if(entity instanceof ItemEntity)
					entity.getEntityData().removeTag(EIO_MAGNET_NBT);
			}
		});
	}

	@Override
	public void postInit()
	{
	}
}