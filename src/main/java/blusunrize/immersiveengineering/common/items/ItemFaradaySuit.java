/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.items;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.tool.IElectricEquipment;
import blusunrize.immersiveengineering.common.IEContent;
import blusunrize.immersiveengineering.common.util.IEDamageSources.ElectricDamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Map;

public class ItemFaradaySuit extends ArmorItem implements IElectricEquipment
{
	public static IArmorMaterial mat;

	public ItemFaradaySuit(EquipmentSlotType type)
	{
		super(mat, type, new Properties().maxStackSize(1).group(ImmersiveEngineering.itemGroup));
		String name = "faraday_suit_"+type.getName().toLowerCase(Locale.ENGLISH);
		IEContent.registeredIEItems.add(this);
	}

	@Override
	public void onStrike(ItemStack s, EquipmentSlotType eqSlot, LivingEntity p, Map<String, Object> cache,
						 @Nullable DamageSource dSource, ElectricSource eSource)
	{
		if(!(dSource instanceof ElectricDamageSource))
			return;
		ElectricDamageSource dmg = (ElectricDamageSource)dSource;
		if(dmg.source.level < 1.75)
		{
			if(cache.containsKey("faraday"))
				cache.put("faraday", (1<<armorType.getIndex())|((Integer)cache.get("faraday")));
			else
				cache.put("faraday", 1<<armorType.getIndex());
			if(cache.containsKey("faraday")&&(Integer)cache.get("faraday")==(1<<4)-1)
				dmg.dmg = 0;
		}
		else
		{
			dmg.dmg *= 1.2;
			if((!(p instanceof PlayerEntity)||!((PlayerEntity)p).abilities.isCreativeMode)&&
					s.attemptDamageItem(2, Item.random, (dmg.getTrueSource() instanceof ServerPlayerEntity)?(ServerPlayerEntity)dmg.getTrueSource(): null))
				p.setItemStackToSlot(eqSlot, ItemStack.EMPTY);
		}
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type)
	{
		return "immersiveengineering:textures/models/armor_faraday"+(slot==EquipmentSlotType.LEGS?"_legs": "")+".png";
	}
}