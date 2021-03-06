package com.mraof.minestuck.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mraof.minestuck.Minestuck;
import com.mraof.minestuck.entity.consort.ConsortEntity;
import com.mraof.minestuck.entity.consort.EnumConsort;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConsortItemTrigger implements ICriterionTrigger<ConsortItemTrigger.Instance>
{
	private static final ResourceLocation ID = new ResourceLocation(Minestuck.MOD_ID, "consort_item");
	private final Map<PlayerAdvancements, Listeners> listenersMap = Maps.newHashMap();
	
	@Override
	public ResourceLocation getId()
	{
		return ID;
	}
	
	@Override
	public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
	{
		Listeners listeners = listenersMap.get(playerAdvancementsIn);
		if(listeners == null)
		{
			listeners = new Listeners(playerAdvancementsIn);
			listenersMap.put(playerAdvancementsIn, listeners);
		}
		listeners.add(listener);
	}
	
	@Override
	public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listener)
	{
		Listeners listeners = listenersMap.get(playerAdvancementsIn);
		if(listeners != null)
		{
			listeners.remove(listener);
			if(listeners.isEmpty())
				listenersMap.remove(playerAdvancementsIn);
		}
	}
	
	@Override
	public void removeAllListeners(PlayerAdvancements playerAdvancementsIn)
	{
		listenersMap.remove(playerAdvancementsIn);
	}
	
	@Override
	public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context)
	{
		String table = json.has("table") ? JSONUtils.getString(json, "table") : null;
		ItemPredicate item = ItemPredicate.deserialize(json.get("item"));
		EnumConsort.MerchantType type = json.has("type") ? EnumConsort.MerchantType.getFromName(JSONUtils.getString(json, "type")) : null;
		return new Instance(table, item, type);
	}
	
	public void trigger(ServerPlayerEntity player, String table, ItemStack item, ConsortEntity consort)
	{
		Listeners listeners = listenersMap.get(player.getAdvancements());
		if(listeners != null)
			listeners.trigger(table, item, consort.merchantType);
	}
	
	public static class Instance extends CriterionInstance
	{
		private final String table;
		private final ItemPredicate item;
		private final EnumConsort.MerchantType type;
		
		public Instance(String table, ItemPredicate item, EnumConsort.MerchantType type)
		{
			super(ID);
			this.table = table;
			this.item = Objects.requireNonNull(item);
			this.type = type;
		}
		
		public static Instance forType(EnumConsort.MerchantType type)
		{
			return new Instance(null, ItemPredicate.ANY, type);
		}
		
		public boolean test(String table, ItemStack item, EnumConsort.MerchantType type)
		{
			return (this.table == null || this.table.equals(table)) && this.item.test(item) && (this.type == null || this.type == type);
		}
		
		@Override
		public JsonElement serialize()
		{
			JsonObject json = new JsonObject();
			if(table != null)
				json.addProperty("table", table);
			json.add("item", item.serialize());
			if(type != null)
				json.addProperty("type", type.toString().toLowerCase());
			
			return json;
		}
	}
	
	static class Listeners
	{
		private final PlayerAdvancements playerAdvancements;
		private final Set<Listener<Instance>> listeners = Sets.newHashSet();
		
		public Listeners(PlayerAdvancements playerAdvancementsIn)
		{
			this.playerAdvancements = playerAdvancementsIn;
		}
		
		public boolean isEmpty()
		{
			return listeners.isEmpty();
		}
		
		public void add(Listener<Instance> listener)
		{
			this.listeners.add(listener);
		}
		
		public void remove(Listener<Instance> listener)
		{
			this.listeners.remove(listener);
		}
		
		public void trigger(String table, ItemStack item, EnumConsort.MerchantType type)
		{
			List<Listener<Instance>> list = Lists.newArrayList();
			for(Listener<Instance> listener : listeners)
				if(listener.getCriterionInstance().test(table, item, type))
					list.add(listener);
			
			list.forEach((listener) -> listener.grantCriterion(playerAdvancements));
		}
	}
}