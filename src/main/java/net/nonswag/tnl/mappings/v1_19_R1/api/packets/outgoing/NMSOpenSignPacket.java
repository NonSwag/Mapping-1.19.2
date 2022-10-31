package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.packets.outgoing.OpenSignPacket;

import javax.annotation.Nonnull;

public final class NMSOpenSignPacket extends OpenSignPacket {

    NMSOpenSignPacket(@Nonnull BlockLocation location) {
        super(location);
    }

    @Nonnull
    @Override
    public ClientboundOpenSignEditorPacket build() {
        return new ClientboundOpenSignEditorPacket(new BlockPos(getLocation().getX(), getLocation().getY(), getLocation().getZ()));
    }
}
