package net.nonswag.tnl.mappings.v1_19_R1.api.entity;

import net.minecraft.world.level.block.FallingBlock;
import net.nonswag.tnl.core.api.reflection.Reflection;
import net.nonswag.tnl.listener.api.entity.TNLEntity;
import net.nonswag.tnl.listener.api.entity.TNLFallingBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;

public class NMSFallingBlock extends FallingBlock implements TNLFallingBlock {

    public NMSFallingBlock(@Nonnull Location location, @Nonnull Material type) {
        super(((CraftWorld) Objects.nonnull(location.getWorld())).getHandle(), location.getX(), location.getY(), location.getZ(), ((CraftBlockData) type.createBlockData()).getState());
        ticksLived = 1;
        persist = true;
        setNoGravity(true);
    }

    @Override
    public void setType(@Nonnull Material type) {
        Reflection.Field.set(this, "block", ((CraftBlockData) type.createBlockData()).getState());
    }

    @Override
    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        setFlag(6, true);
    }

    @Override
    public boolean teleport(@Nonnull Location location) {
        setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        return true;
    }

    @Override
    public boolean teleport(@Nonnull Entity entity) {
        return teleport(entity.getLocation());
    }

    @Override
    public boolean teleport(@Nonnull TNLEntity entity) {
        return teleport(entity.bukkit());
    }

    @Override
    public void setCustomName(@Nonnull String customName) {
        IChatBaseComponent[] components = CraftChatMessage.fromString(customName);
        super.setCustomName(components[0]);
    }

    @Override
    public int getEntityId() {
        return super.getId();
    }

    @Nonnull
    @Override
    public CraftEntity bukkit() {
        return super.getBukkitEntity();
    }
}
