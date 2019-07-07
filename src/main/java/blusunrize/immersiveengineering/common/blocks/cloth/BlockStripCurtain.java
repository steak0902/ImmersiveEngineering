/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.blocks.cloth;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.api.shader.CapabilityShader;
import blusunrize.immersiveengineering.client.models.IOBJModelCallback;
import blusunrize.immersiveengineering.common.blocks.BlockIETileProvider;
import blusunrize.immersiveengineering.common.blocks.ItemBlockIEBase;
import blusunrize.immersiveengineering.common.util.ItemNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class BlockStripCurtain extends BlockIETileProvider
{

	public BlockStripCurtain()
	{
		super("strip_curtain", Block.Properties.create(Material.CLOTH).hardnessAndResistance(0.8F), ItemBlockIEBase.class,
				IOBJModelCallback.PROPERTY, CapabilityShader.BLOCKSTATE_PROPERTY);
		setLightOpacity(0);
		setHasColours();
		setNotNormalBlock();
	}

	@Nullable
	@Override
	public TileEntity createBasicTE(BlockState state)
	{
		return new TileEntityBalloon();
	}

	/*TODO why is this here? Added in the original 1.8 commit
		@Override
		@OnlyIn(Dist.CLIENT)
		public int getRenderColour(IBlockState state, @Nullable IBlockReader worldIn, @Nullable BlockPos pos, int tintIndex)
		{
			return 0xFFFFFF;
		}*/

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced)
	{
		if(ItemNBTHelper.hasKey(stack, "colour"))
		{
			String hexCol = Integer.toHexString(ItemNBTHelper.getInt(stack, "colour"));
			tooltip.add(new TranslationTextComponent(Lib.DESC_INFO+"colour", "<hexcol="+hexCol+":#"+hexCol+">"));
		}
	}

	@Override
	public boolean allowHammerHarvest(BlockState blockState)
	{
		return true;
	}
}
