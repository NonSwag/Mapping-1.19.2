package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.nonswag.tnl.listener.api.packets.MapChunkPacket;
import org.bukkit.Chunk;

import javax.annotation.Nonnull;

public final class NMSMapChunkPacket extends MapChunkPacket {

    NMSMapChunkPacket(@Nonnull Chunk chunk, int section) {
        super(chunk, section);
    }

    @Nonnull
    @Override
    public ClientboundLevelChunkPacketData build() {
        throw new UnsupportedOperationException();
    }
}
