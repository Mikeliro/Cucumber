package com.blakebr0.cucumber.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeMod;

/*
 * Parts of the code used in this class is derived from Actually Additions,
 * by Ellpeck (https://github.com/Ellpeck/ActuallyAdditions)
 * Or Draconic Evolution, by brandon3055
 * (https://github.com/brandon3055/Draconic-Evolution)
 */
public final class BlockHelper {
	private static BlockRayTraceResult rayTraceBlocks(World world, PlayerEntity player, double reach, RayTraceContext.FluidMode fluidMode) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        Vector3d eyePos = player.getEyePosition(1.0F);
        float f2 = MathHelper.cos(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f3 = MathHelper.sin(-yaw * ((float) Math.PI / 180F) - (float) Math.PI);
        float f4 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
        float f5 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
        float f6 = f3 * f4;
        float f7 = f2 * f4;

        Vector3d vec3d1 = eyePos.add((double) f6 * reach, (double) f5 * reach, (double) f7 * reach);
        return world.rayTraceBlocks(new RayTraceContext(eyePos, vec3d1, RayTraceContext.BlockMode.OUTLINE, fluidMode, player));
	}

	public static BlockRayTraceResult rayTraceBlocks(World world, PlayerEntity player) {
		return rayTraceBlocks(world, player, RayTraceContext.FluidMode.NONE);
	}

	public static BlockRayTraceResult rayTraceBlocks(World world, PlayerEntity player, RayTraceContext.FluidMode fluidMode) {
        ModifiableAttributeInstance attribute = player.getAttribute(ForgeMod.REACH_DISTANCE.get());
        double reach = attribute != null ? attribute.getValue() : 5.0D;
		return rayTraceBlocks(world, player, reach, fluidMode);
	}

    public static boolean breakBlocksAOE(ItemStack stack, World world, PlayerEntity player, BlockPos pos) {
        if (world.isAirBlock(pos))
            return false;

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (!world.isRemote()) {
            world.playEvent(player, 2001, pos, Block.getStateId(state));
        } else {
            world.playEvent(2001, pos, Block.getStateId(state));
        }

        if (player.abilities.isCreativeMode) {
            block.onBlockHarvested(world, pos, state, player);
            if (block.removedByPlayer(state, world, pos, player, false, state.getFluidState())) {
                block.onPlayerDestroy(world, pos, state);
            }

            if (!world.isRemote()) {
                if (player instanceof ServerPlayerEntity) {
                    ((ServerPlayerEntity) player).connection.sendPacket(new SChangeBlockPacket(world, pos));
                }
            }

            return true;
        }

        stack.onBlockDestroyed(world, state, pos, player);

        if (!world.isRemote()) {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity mplayer = (ServerPlayerEntity) player;

                int xp = ForgeHooks.onBlockBreakEvent(world, mplayer.interactionManager.getGameType(), mplayer, pos);
                if (xp == -1) return false;

                TileEntity tile = world.getTileEntity(pos);
                if (block.removedByPlayer(state, world, pos, player, true, state.getFluidState())) {
                    block.onPlayerDestroy(world, pos, state);
                    block.harvestBlock(world, player, pos, state, tile, stack);
                    block.dropXpOnBlockBreak((ServerWorld) world, pos, xp);
                }

                mplayer.connection.sendPacket(new SChangeBlockPacket(world, pos));
                return true;
            }
        } else {
            if (block.removedByPlayer(state, world, pos, player, true, state.getFluidState())) {
                block.onPlayerDestroy(world, pos, state);
            }

            // TODO Figure out how to get whatever direction this is looking for
//			Minecraft.getInstance().getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, Minecraft.getInstance().objectMouseOver));

            return true;
        }

        return false;
    }
}
