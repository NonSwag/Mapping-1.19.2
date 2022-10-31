package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.world.phys.Vec3;
import net.nonswag.tnl.listener.api.packets.outgoing.EntityVelocityPacket;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;

public final class NMSEntityVelocityPacket extends EntityVelocityPacket {

    NMSEntityVelocityPacket(int entityId, @Nonnull Vector vector) {
        super(entityId, vector);
    }

    @Nonnull
    @Override
    public ClientboundSetEntityMotionPacket build() {
        return new ClientboundSetEntityMotionPacket(getEntityId(), new Vec3(getVector().getX(), getVector().getY(), getVector().getZ()));
    }
}
