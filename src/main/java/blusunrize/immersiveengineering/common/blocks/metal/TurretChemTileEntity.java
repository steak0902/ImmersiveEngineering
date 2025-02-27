/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.common.config.IEServerConfig;
import blusunrize.immersiveengineering.common.IETileTypes;
import blusunrize.immersiveengineering.common.entities.ChemthrowerShotEntity;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurretChemTileEntity extends TurretTileEntity
{
	public FluidTank tank = new FluidTank(4*FluidAttributes.BUCKET_VOLUME);
	public boolean ignite = false;

	public TurretChemTileEntity()
	{
		super(IETileTypes.TURRET_CHEM.get());
	}

	@Override
	protected double getRange()
	{
		return 8;
	}

	@Override
	protected boolean canActivate()
	{
		return tank.getFluidAmount() > 0&&this.energyStorage.getEnergyStored() >= IEServerConfig.MACHINES.turret_chem_consumption.get();
	}

	@Override
	protected int getChargeupTicks()
	{
		return 10;
	}

	@Override
	protected int getActiveTicks()
	{
		return 1;
	}

	@Override
	protected boolean loopActivation()
	{
		return true;
	}

	@Override
	protected void activate()
	{
		FluidStack fs = this.tank.getFluid().copy();
		if(!fs.isEmpty())
		{
			int consumed = IEServerConfig.TOOLS.chemthrower_consumption.get();
			int energy = IEServerConfig.MACHINES.turret_chem_consumption.get();
			if(consumed <= fs.getAmount()&&this.energyStorage.extractEnergy(energy, true) >= energy)
			{
				tank.drain(consumed, FluidAction.EXECUTE);
				this.energyStorage.extractEnergy(energy, false);
				Vec3 v = getGunToTargetVec(target).normalize();

				int split = 8;
				boolean isGas = fs.getFluid().getAttributes().isGaseous();

				float scatter = isGas?.15f: .05f;
				float range = isGas?.5f: 1f;
//				if(getUpgrades(stack).getBoolean("focus"))
//				{
//					range += .25f;
//					scatter -= .025f;
//				}
				boolean ignite = ChemthrowerHandler.isFlammable(fs.getFluid())&&this.ignite;
				for(int i = 0; i < split; i++)
				{
					Vec3 vecDir = v.add(Utils.RAND.nextGaussian()*scatter, Utils.RAND.nextGaussian()*scatter, Utils.RAND.nextGaussian()*scatter);
					Vec3 throwerPos = getGunPosition();
					ChemthrowerShotEntity chem = new ChemthrowerShotEntity(level, throwerPos.x+v.x*0.875, throwerPos.y+v.y*0.875,
							throwerPos.z+v.z*0.875, 0, 0, 0, fs);
					chem.setDeltaMovement(vecDir.scale(range));
					if(ignite)
						chem.setSecondsOnFire(10);
					if(!level.isClientSide)
						level.addFreshEntity(chem);
				}
				if(tick%4==0)
				{
					if(ignite)
						level.playSound(null, getBlockPos(), IESounds.sprayFire, SoundSource.BLOCKS, .5F, 1.5F);
					else
						level.playSound(null, getBlockPos(), IESounds.spray, SoundSource.BLOCKS, .5F, .75F);
				}
			}
		}
	}

	@Override
	public void receiveMessageFromClient(CompoundTag message)
	{
		super.receiveMessageFromClient(message);
		if(message.contains("ignite", NBT.TAG_BYTE))
			ignite = message.getBoolean("ignite");
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		tank.readFromNBT(nbt.getCompound("tank"));
		ignite = nbt.getBoolean("ignite");
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		nbt.put("tank", tank.writeToNBT(new CompoundTag()));
		nbt.putBoolean("ignite", ignite);
	}

	private LazyOptional<IFluidHandler> tankCap = registerConstantCap(tank);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
	{
		if(!isDummy()&&capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY&&(facing==null||facing==Direction.DOWN||facing==this.getFacing().getOpposite()))
			return tankCap.cast();
		return super.getCapability(capability, facing);
	}
}