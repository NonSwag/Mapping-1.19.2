package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.packets.outgoing.BlockBreakAnimationPacket;

import javax.annotation.Nonnull;

public final class NMSBlockBreakAnimationPacket extends BlockBreakAnimationPacket {

    NMSBlockBreakAnimationPacket(@Nonnull BlockLocation location, int state) {
        super(location, state);
    }

    @Nonnull
    @Override
    public ClientboundBlockDestructionPacket build() {
        BlockPos position = new BlockPos(getLocation().getX(), getLocation().getY(), getLocation().getZ());
        return new ClientboundBlockDestructionPacket(getLocation().getBlock().hashCode(), position, getState());
    }
}
