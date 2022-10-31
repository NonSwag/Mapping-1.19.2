package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.SetSlotPacket;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class NMSSetSlotPacket extends SetSlotPacket {

    NMSSetSlotPacket(@Nonnull Inventory inventory, int slot, @Nullable ItemStack itemStack) {
        super(inventory, slot, itemStack);
    }

    @Nonnull
    @Override
    public ClientboundContainerSetSlotPacket build() {
        return new ClientboundContainerSetSlotPacket(getInventory().getId(), 0, getSlot(), CraftItemStack.asNMSCopy(getItemStack()));
    }
}
