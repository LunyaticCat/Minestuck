package com.mraof.minestuck.network.data;

import com.mraof.minestuck.item.crafting.alchemy.GristSet;
import com.mraof.minestuck.network.PlayToClientPacket;
import com.mraof.minestuck.world.storage.ClientPlayerData;
import net.minecraft.network.PacketBuffer;

public class GristCachePacket implements PlayToClientPacket
{
	public final GristSet gristCache;
	public final boolean isEditmode;
	
	public GristCachePacket(GristSet gristCache, boolean isEditmode)
	{
		this.gristCache = gristCache;
		this.isEditmode = isEditmode;
	}
	
	@Override
	public void encode(PacketBuffer buffer)
	{
		gristCache.write(buffer);
		buffer.writeBoolean(isEditmode);
	}
	
	public static GristCachePacket decode(PacketBuffer buffer)
	{
		GristSet gristCache = GristSet.read(buffer);
		boolean isEditmode = buffer.readBoolean();
		return new GristCachePacket(gristCache, isEditmode);
	}
	
	@Override
	public void execute()
	{
		ClientPlayerData.handleDataPacket(this);
	}
}