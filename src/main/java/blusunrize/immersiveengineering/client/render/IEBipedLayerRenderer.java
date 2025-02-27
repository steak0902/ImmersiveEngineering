/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.client.render;

import blusunrize.immersiveengineering.client.models.ModelEarmuffs;
import blusunrize.immersiveengineering.client.models.ModelPowerpack;
import blusunrize.immersiveengineering.common.items.EarmuffsItem;
import blusunrize.immersiveengineering.common.items.IEItemInterfaces.IColouredItem;
import blusunrize.immersiveengineering.common.items.IEItems.Misc;
import blusunrize.immersiveengineering.common.items.PowerpackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class IEBipedLayerRenderer<E extends LivingEntity, M extends HumanoidModel<E>> extends RenderLayer<E, M>
{
	public static boolean rendersAssigned = false;
	private static ModelEarmuffs earmuffModel;

	public IEBipedLayerRenderer(RenderLayerParent<E, M> entityRendererIn)
	{
		super(entityRendererIn);
		if(earmuffModel==null)
			earmuffModel = new ModelEarmuffs(.0625f, 0, 64, 32);
	}

	@Override
	@ParametersAreNonnullByDefault
	public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, E living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
	{
		ItemStack earmuffs = EarmuffsItem.EARMUFF_GETTERS.getFrom(living);
		if(!earmuffs.isEmpty())
		{
			HumanoidModel<LivingEntity> model = ModelEarmuffs.getModel();
			model.setupAnim(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			RenderType type = model.renderType(new ResourceLocation(Misc.earmuffs.getArmorTexture(earmuffs, living, EquipmentSlot.HEAD, "overlay")));
			model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(type), packedLightIn, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
			int colour = ((IColouredItem)earmuffs.getItem()).getColourForIEItem(earmuffs, 0);
			type = model.renderType(new ResourceLocation(Misc.earmuffs.getArmorTexture(earmuffs, living, EquipmentSlot.HEAD, null)));
			model.renderToBuffer(matrixStackIn, bufferIn.getBuffer(type), packedLightIn, OverlayTexture.NO_OVERLAY,
					(colour >> 16&255)/255f, (colour >> 8&255)/255f, (colour&255)/255f, 1F);
		}

		ItemStack powerpack = PowerpackItem.POWERPACK_GETTER.getFrom(living);
		if(!powerpack.isEmpty())
			renderPowerpack(powerpack, matrixStackIn, bufferIn, packedLightIn, living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	private void renderPowerpack(ItemStack powerpack, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, E living, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
	{
		if(!powerpack.isEmpty())
			ModelPowerpack.render(
					living, powerpack,
					matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1,
					limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch
			);
	}
}