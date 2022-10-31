package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.nonswag.tnl.listener.api.packets.outgoing.CloseWindowPacket;

import javax.annotation.Nonnull;

public final class NMSCloseWindowPacket extends CloseWindowPacket {

    NMSCloseWindowPacket(int windowId) {
        super(windowId);
    }

    @Nonnull
    @Override
    public ClientboundContainerClosePacket build() {
        return new ClientboundContainerClosePacket(getWindowId());
    }
}
