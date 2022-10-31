package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.level.border.WorldBorder;
import net.nonswag.tnl.listener.api.border.VirtualBorder;
import net.nonswag.tnl.listener.api.packets.outgoing.WorldBorderPacket;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import javax.annotation.Nonnull;

public final class NMSWorldBorderPacket extends WorldBorderPacket {

    NMSWorldBorderPacket(@Nonnull VirtualBorder virtualBorder, @Nonnull Action action) {
        super(virtualBorder, action);
    }

    @Nonnull
    @Override
    public Packet<ClientGamePacketListener> build() {
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.world = ((CraftWorld) getBorder().getWorld()).getHandle();
        worldBorder.setWarningBlocks(getBorder().getWarningDistance());
        worldBorder.setSize(getBorder().getSize());
        worldBorder.setCenter(getBorder().getCenter().x(), getBorder().getCenter().z());
        worldBorder.setDamagePerBlock(getBorder().getDamageAmount());
        worldBorder.setDamageSafeZone(getBorder().getDamageBuffer());
        worldBorder.setWarningTime(getBorder().getWarningTime());
        return switch (getAction()) {
            case SET_SIZE -> new ClientboundSetBorderSizePacket(worldBorder);
            case LERP_SIZE -> new ClientboundSetBorderLerpSizePacket(worldBorder);
            case SET_CENTER -> new ClientboundSetBorderCenterPacket(worldBorder);
            case INITIALIZE -> new ClientboundInitializeBorderPacket(worldBorder);
            case SET_WARNING_TIME -> new ClientboundSetBorderWarningDelayPacket(worldBorder);
            case SET_WARNING_BLOCKS -> new ClientboundSetBorderWarningDistancePacket(worldBorder);
        };
    }
}
