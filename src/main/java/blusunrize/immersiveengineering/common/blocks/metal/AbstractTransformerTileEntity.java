package blusunrize.immersiveengineering.common.blocks.metal;

import blusunrize.immersiveengineering.api.IEProperties;
import blusunrize.immersiveengineering.api.wires.Connection;
import blusunrize.immersiveengineering.api.wires.ConnectionPoint;
import blusunrize.immersiveengineering.api.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.wires.WireType;
import blusunrize.immersiveengineering.api.wires.utils.WireUtils;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IStateBasedDirectional;
import blusunrize.immersiveengineering.common.blocks.generic.ImmersiveConnectableTileEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import java.util.Collection;
import java.util.Set;

import static blusunrize.immersiveengineering.api.wires.WireType.MV_CATEGORY;

public abstract class AbstractTransformerTileEntity extends ImmersiveConnectableTileEntity
		implements IStateBasedDirectional
{
	protected static final int RIGHT_INDEX = 0;
	protected static final int LEFT_INDEX = 1;
	protected WireType leftType;
	protected WireType rightType;
	protected Set<String> acceptableLowerWires = ImmutableSet.of(WireType.LV_CATEGORY);

	public AbstractTransformerTileEntity(BlockEntityType<? extends ImmersiveConnectableTileEntity> type)
	{
		super(type);
	}

	protected boolean canAttach(WireType toAttach, @Nullable WireType atConn, @Nullable WireType other)
	{
		if(atConn!=null)
			return false;
		String higherCat = getHigherWiretype();
		String attachCat = toAttach.getCategory();
		if(other==null)
			return higherCat.equals(attachCat)||acceptableLowerWires.contains(attachCat);
		boolean isHigher = higherCat.equals(toAttach.getCategory());
		boolean isOtherHigher = higherCat.equals(other.getCategory());
		if(isHigher^isOtherHigher)
		{
			if(isHigher)
				return true;
			else
				return acceptableLowerWires.contains(attachCat);
		}
		else
			return false;
	}

	public String getHigherWiretype()
	{
		return MV_CATEGORY;
	}

	@Override
	public Collection<ConnectionPoint> getConnectionPoints()
	{
		return ImmutableList.of(new ConnectionPoint(worldPosition, RIGHT_INDEX), new ConnectionPoint(worldPosition, LEFT_INDEX));
	}

	@Override
	public Iterable<? extends Connection> getInternalConnections()
	{
		return ImmutableList.of(new Connection(worldPosition, LEFT_INDEX, RIGHT_INDEX));
	}

	@Override
	public Property<Direction> getFacingProperty()
	{
		return IEProperties.FACING_HORIZONTAL;
	}

	@Override
	public boolean mirrorFacingOnPlacement(LivingEntity placer)
	{
		return false;
	}

	@Override
	public boolean canHammerRotate(Direction side, Vec3 hit, LivingEntity entity)
	{
		return false;
	}

	@Override
	public boolean canRotate(Direction axis)
	{
		return false;
	}

	@Override
	public Vec3 getConnectionOffset(@Nonnull Connection con, ConnectionPoint here)
	{
		return getConnectionOffset(con, con.getEndFor(worldPosition).getIndex()==RIGHT_INDEX);
	}

	@Override
	public void removeCable(Connection connection, ConnectionPoint attachedPoint)
	{
		WireType type = connection!=null?connection.type: null;
		if(type==null)
			leftType = rightType = null;
		else
		{
			switch(attachedPoint.getIndex())
			{
				case LEFT_INDEX:
					leftType = null;
					break;
				case RIGHT_INDEX:
					rightType = null;
					break;
			}
		}
		updateMirrorState();
		this.markContainingBlockForUpdate(null);
	}

	public void connectCable(WireType cableType, ConnectionPoint target, IImmersiveConnectable other, ConnectionPoint otherTarget)
	{
		switch(target.getIndex())
		{
			case LEFT_INDEX:
				this.leftType = cableType;
				break;
			case RIGHT_INDEX:
				this.rightType = cableType;
				break;
		}
		updateMirrorState();
	}

	public boolean canConnectCable(WireType cableType, ConnectionPoint target, Vec3i offset)
	{
		switch(target.getIndex())
		{
			case LEFT_INDEX:
				return canAttach(cableType, leftType, rightType);
			case RIGHT_INDEX:
				return canAttach(cableType, rightType, leftType);
		}
		return false;
	}

	public void writeCustomNBT(CompoundTag nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		if(leftType!=null)
			nbt.putString("leftType", leftType.getUniqueName());
		if(rightType!=null)
			nbt.putString("rightType", rightType.getUniqueName());
	}

	@Override
	public void readCustomNBT(@Nonnull CompoundTag nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		if(nbt.contains("leftType"))
			leftType = WireUtils.getWireTypeFromNBT(nbt, "leftType");
		else
			leftType = null;
		if(nbt.contains("rightType"))
			rightType = WireUtils.getWireTypeFromNBT(nbt, "rightType");
		else
			rightType = null;
	}

	@Override
	public boolean canConnect()
	{
		return true;
	}

	protected abstract Vec3 getConnectionOffset(Connection con, boolean right);

	protected void updateMirrorState()
	{
	}
}
