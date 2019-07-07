/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.blocks.wooden;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.common.blocks.BlockIETileProvider;
import blusunrize.immersiveengineering.common.blocks.ItemBlockIEBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public class BlockModWorkbench extends BlockIETileProvider
{

	public BlockModWorkbench(String name)
	{
		super(name, Block.Properties.create(Material.WOOD).hardnessAndResistance(2, 5), ItemBlockIEBase.class,
				IEProperties.MULTIBLOCKSLAVE);
	}

	@Nullable
	@Override
	public TileEntity createBasicTE(BlockState state)
	{
		return new TileEntityModWorkbench();
	}
}
