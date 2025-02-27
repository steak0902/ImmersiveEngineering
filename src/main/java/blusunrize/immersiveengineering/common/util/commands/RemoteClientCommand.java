/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.commands;

import blusunrize.immersiveengineering.common.network.MessageClientCommand;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author BluSunrize - 05.09.2016
 */
public class RemoteClientCommand
{
	public static LiteralArgumentBuilder<CommandSourceStack> clientComamnd(String name, MessageClientCommand.Type type)
	{
		LiteralArgumentBuilder<CommandSourceStack> ret = Commands.literal(name);
		ret.executes(context -> {
			MessageClientCommand.send(context, type);
			return Command.SINGLE_SUCCESS;
		});
		return ret;
	}
}
