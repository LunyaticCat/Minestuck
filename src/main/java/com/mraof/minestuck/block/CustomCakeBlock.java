package com.mraof.minestuck.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public abstract class CustomCakeBlock extends CakeBlock
{
	protected CustomCakeBlock(Properties builder)
	{
		super(builder);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!worldIn.isRemote)
		{
			return this.eatCake(worldIn, pos, state, player) ? ActionResultType.SUCCESS : ActionResultType.PASS;
		}
		else
		{
			ItemStack itemstack = player.getHeldItem(hand);
			return this.eatCake(worldIn, pos, state, player) ? ActionResultType.SUCCESS : itemstack.isEmpty() ? ActionResultType.CONSUME : ActionResultType.PASS;
		}
	}
	
	public boolean eatCake(World worldIn, BlockPos pos, BlockState state, PlayerEntity player)
	{
		if (!player.canEat(false))
		{
			return false;
		}
		else
		{
			player.addStat(Stats.EAT_CAKE_SLICE);
			applyEffects(worldIn, pos, state, player);
			int i = state.get(BITES);
			
			if (i < 6)
			{
				worldIn.setBlockState(pos, state.with(BITES, i + 1), Constants.BlockFlags.DEFAULT);
			}
			else
			{
				worldIn.removeBlock(pos, false);
			}
			
			return true;
		}
	}
	
	protected abstract void applyEffects(World worldIn, BlockPos pos, BlockState state, PlayerEntity player);
}