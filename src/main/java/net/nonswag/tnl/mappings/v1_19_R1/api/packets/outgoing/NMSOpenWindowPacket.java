package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.inventory.MenuType;
import net.nonswag.tnl.listener.api.packets.outgoing.OpenWindowPacket;

import javax.annotation.Nonnull;

public final class NMSOpenWindowPacket extends OpenWindowPacket {

    NMSOpenWindowPacket(int windowId, @Nonnull Type type, @Nonnull String title) {
        super(windowId, type, title);
    }

    @Nonnull
    @Override
    public ClientboundOpenScreenPacket build() {
        return new ClientboundOpenScreenPacket(getWindowId(), wrappedType(), Component.literal(getTitle()));
    }
    
    @Nonnull
    private MenuType<?> wrappedType() {
        return switch (getType()) {
            case CHEST_9X1 -> MenuType.GENERIC_9x1;
            case CHEST_9X2 -> MenuType.GENERIC_9x2;
            case CHEST_9X3 -> MenuType.GENERIC_9x3;
            case CHEST_9X4 -> MenuType.GENERIC_9x4;
            case CHEST_9X5 -> MenuType.GENERIC_9x5;
            case CHEST_9X6 -> MenuType.GENERIC_9x6;
            case DISPENSER -> MenuType.GENERIC_3x3;
            case ANVIL -> MenuType.ANVIL;
            case BEACON -> MenuType.BEACON;
            case BLAST_FURNACE -> MenuType.BLAST_FURNACE;
            case BREWING_STAND -> MenuType.BREWING_STAND;
            case WORKBENCH -> MenuType.CRAFTING;
            case ENCHANTER -> MenuType.ENCHANTMENT;
            case FURNACE -> MenuType.FURNACE;
            case GRINDSTONE -> MenuType.GRINDSTONE;
            case HOPPER -> MenuType.HOPPER;
            case LECTERN -> MenuType.LECTERN;
            case LOOM -> MenuType.LOOM;
            case MERCHANT -> MenuType.MERCHANT;
            case SHULKER_BOX -> MenuType.SHULKER_BOX;
            case SMITHING_TABLE -> MenuType.SMITHING;
            case SMOKER -> MenuType.SMOKER;
            case CARTOGRAPHY_TABLE -> MenuType.CARTOGRAPHY_TABLE;
            case STONECUTTER -> MenuType.STONECUTTER;
        };
    }
}
