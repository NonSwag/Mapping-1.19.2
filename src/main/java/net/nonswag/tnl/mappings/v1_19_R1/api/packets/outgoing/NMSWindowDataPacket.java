package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundContainerSetDataPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.WindowDataPacket;

import javax.annotation.Nonnull;

public final class NMSWindowDataPacket extends WindowDataPacket {

    NMSWindowDataPacket(int windowId, int property, int value) {
        super(windowId, property, value);
    }

    @Nonnull
    @Override
    public ClientboundContainerSetDataPacket build() {
        return new ClientboundContainerSetDataPacket(getWindowId(), getProperty(), getValue());
    }
}
