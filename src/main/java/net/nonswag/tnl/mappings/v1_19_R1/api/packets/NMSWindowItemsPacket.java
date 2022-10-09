package net.nonswag.tnl.mappings.v1_19_R1.api.packets;

import net.minecraft.core.NonNullList;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.world.item.ItemStack;
import net.nonswag.tnl.listener.api.packets.WindowItemsPacket;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;

import javax.annotation.Nonnull;
import java.util.List;

public final class NMSWindowItemsPacket extends WindowItemsPacket {

    NMSWindowItemsPacket(int windowId, @Nonnull List<org.bukkit.inventory.ItemStack> items) {
        super(windowId, items);
    }

    @Nonnull
    @Override
    public ClientboundContainerSetContentPacket build() {
        NonNullList<ItemStack> items = NonNullList.create();
        for (org.bukkit.inventory.ItemStack item : getItems()) items.add(CraftItemStack.asNMSCopy(item));
        return new ClientboundContainerSetContentPacket(getWindowId(), 0, items, ItemStack.EMPTY);
    }
}
