/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.tool.ChemthrowerHandler;
import blusunrize.immersiveengineering.common.Config.IEConfig;
import blusunrize.immersiveengineering.common.entities.EntityChemthrowerShot;
import blusunrize.immersiveengineering.common.util.IESounds;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityTurretChem extends TileEntityTurret
{
	public static TileEntityType<TileEntityTurretChem> TYPE;

	public FluidTank tank = new FluidTank(4000);
	public boolean ignite = false;

	public TileEntityTurretChem()
	{
		super(TYPE);
	}

	@Override
	protected double getRange()
	{
		return 8;
	}

	@Override
	protected boolean canActivate()
	{
		return tank.getFluidAmount() > 0&&this.energyStorage.getEnergyStored() >= IEConfig.Machines.turret_chem_consumption;
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
		FluidStack fs = this.tank.getFluid();
		if(fs!=null&&fs.getFluid()!=null)
		{
			int consumed = IEConfig.Tools.chemthrower_consumption;
			int energy = IEConfig.Machines.turret_chem_consumption;
			if(consumed <= fs.amount&&this.energyStorage.extractEnergy(energy, true) >= energy)
			{
				tank.drain(consumed, true);
				this.energyStorage.extractEnergy(energy, false);
				Vec3d v = getGunToTargetVec(target).normalize();

				int split = 8;
				boolean isGas = fs.getFluid().isGaseous()||ChemthrowerHandler.isGas(fs.getFluid());

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
					Vec3d vecDir = v.add(Utils.RAND.nextGaussian()*scatter, Utils.RAND.nextGaussian()*scatter, Utils.RAND.nextGaussian()*scatter);
					Vec3d throwerPos = getGunPosition();
					EntityChemthrowerShot chem = new EntityChemthrowerShot(world, throwerPos.x+v.x*0.875, throwerPos.y+v.y*0.875,
							throwerPos.z+v.z*0.875, 0, 0, 0, fs);
					chem.motionX = vecDir.x*range;
					chem.motionY = vecDir.y*range;
					chem.motionZ = vecDir.z*range;
					if(ignite)
						chem.setFire(10);
					if(!world.isRemote)
						world.spawnEntity(chem);
				}
				if(tick%4==0)
					if(ignite)
						world.playSound(null, getPos(), IESounds.sprayFire, SoundCategory.BLOCKS, .5F, 1.5F);
					else
						world.playSound(null, getPos(), IESounds.spray, SoundCategory.BLOCKS, .5F, .75F);
			}
		}
	}


	@Override
	public void receiveMessageFromClient(CompoundNBT message)
	{
		super.receiveMessageFromClient(message);
		if(message.hasKey("ignite"))
			ignite = message.getBoolean("ignite");
	}

	@Override
	public void readCustomNBT(CompoundNBT nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		tank.readFromNBT(nbt.getCompound("tank"));
		ignite = nbt.getBoolean("ignite");
	}

	@Override
	public void writeCustomNBT(CompoundNBT nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		nbt.setTag("tank", tank.writeToNBT(new CompoundNBT()));
		nbt.setBoolean("ignite", ignite);
	}

	private LazyOptional<IFluidHandler> tankCap = registerConstantCap(tank);

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing)
	{
		if(!dummy&&capability==CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY&&(facing==null||facing==Direction.DOWN||facing==this.facing.getOpposite()))
			return tankCap.cast();
		return super.getCapability(capability, facing);
	}
}