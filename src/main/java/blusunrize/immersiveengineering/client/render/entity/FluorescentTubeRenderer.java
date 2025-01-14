/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.client.render.entity;

import blusunrize.immersiveengineering.client.render.IEOBJItemRenderer;
import blusunrize.immersiveengineering.client.utils.IERenderTypes;
import blusunrize.immersiveengineering.client.utils.RenderUtils;
import blusunrize.immersiveengineering.common.entities.FluorescentTubeEntity;
import blusunrize.immersiveengineering.common.items.FluorescentTubeItem;
import blusunrize.immersiveengineering.common.items.IEItems.Misc;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;

public class FluorescentTubeRenderer extends EntityRenderer<FluorescentTubeEntity>
{
	static double sqrt2Half = Math.sqrt(2)/2;
	public static final double[][] octagon = {
			{1, 0}, {sqrt2Half, sqrt2Half}, {0, 1}, {-sqrt2Half, sqrt2Half},
			{-1, 0}, {-sqrt2Half, -sqrt2Half}, {0, -1}, {sqrt2Half, -sqrt2Half}
	};
	TextureAtlasSprite tex;

	public FluorescentTubeRenderer(EntityRenderDispatcher renderManager)
	{
		super(renderManager);
		shadowStrength = 0;
		shadowRadius = 0;
	}

	@Override
	public ResourceLocation getTextureLocation(FluorescentTubeEntity entity)
	{
		return null;
	}

	@Override
	public void render(FluorescentTubeEntity entity, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
	{
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, 1, 0);
		matrixStackIn.mulPose(new Quaternion(0, entityYaw+90, 0, true));
		matrixStackIn.pushPose();
		matrixStackIn.translate(0, 0, .03125);
		matrixStackIn.mulPose(new Quaternion(entity.angleHorizontal, 0, 0, true));
		matrixStackIn.translate(0, -entity.TUBE_LENGTH/2, 0);
		drawTube(entity.active, entity.rgb, matrixStackIn, bufferIn, packedLightIn, 0);
		matrixStackIn.popPose();
		matrixStackIn.translate(-0.25, -1, 0);
		if(tex==null)
			tex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(new ResourceLocation("minecraft:block/iron_block"));

		VertexConsumer builder = bufferIn.getBuffer(IERenderTypes.getPositionTex(InventoryMenu.BLOCK_ATLAS));
		RenderUtils.renderTexturedBox(builder, matrixStackIn,
				0, 0, 0,
				.0625F, 1, .0625F,
				tex.getU0(), tex.getV0(), tex.getU1(), tex.getV1());
		RenderUtils.renderTexturedBox(builder, matrixStackIn,
				.0625F, .9375F, 0,
				.25F, 1, .0625F,
				tex.getU0(), tex.getV0(), tex.getU1(), tex.getV1());

		matrixStackIn.popPose();
	}

	private static ItemStack tube = ItemStack.EMPTY;
	private static ItemStack tubeActive = ItemStack.EMPTY;

	static void drawTube(boolean active, float[] rgb, PoseStack matrixStack, MultiBufferSource buffer, int light,
						 int overlay)
	{
		if(tube.isEmpty())
			tube = new ItemStack(Misc.fluorescentTube);
		if(tubeActive.isEmpty())
		{
			tubeActive = new ItemStack(Misc.fluorescentTube);
			FluorescentTubeItem.setLit(tubeActive, 1);
		}
		matrixStack.translate(-.5, .25, -.5);
		ItemStack renderStack = active?tubeActive: tube;
		FluorescentTubeItem.setRGB(renderStack, rgb);
		IEOBJItemRenderer.INSTANCE.renderByItem(renderStack, TransformType.FIXED, matrixStack, buffer, light, overlay);
	}
}
