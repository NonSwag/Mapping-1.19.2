package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.ItemTakePacket;

import javax.annotation.Nonnull;

public final class NMSItemTakePacket extends ItemTakePacket {

    NMSItemTakePacket(int itemId, int collectorId, int amount) {
        super(itemId, collectorId, amount);
    }

    @Nonnull
    @Override
    public ClientboundTakeItemEntityPacket build() {
        return new ClientboundTakeItemEntityPacket(getItemId(), getCollectorId(), getAmount());
    }
}
