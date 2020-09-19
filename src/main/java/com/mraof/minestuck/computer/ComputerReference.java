package com.mraof.minestuck.computer;

import com.mraof.minestuck.tileentity.ComputerTileEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.GlobalPos;

import java.util.Objects;

public abstract class ComputerReference
{
	public static ComputerReference of(ComputerTileEntity te)
	{
		return new TEComputerReference(GlobalPos.of(Objects.requireNonNull(te.getWorld()).dimension.getType(), te.getPos()));
	}
	
	public static ComputerReference read(CompoundNBT nbt)
	{
		String type = nbt.getString("type");
		if(type.equals("tile_entity"))
			return TEComputerReference.create(nbt);
		else throw new IllegalStateException("Invalid computer type: " + type);
	}
	
	public CompoundNBT write(CompoundNBT nbt)
	{
		return nbt;
	}
	
	//TODO look over usages to limit force loading of dimensions
	public abstract ISburbComputer getComputer(MinecraftServer server);
	
	public abstract boolean matches(ISburbComputer computer);
	
	public abstract boolean isInNether();
	
	public abstract GlobalPos getPosForEditmode();
}