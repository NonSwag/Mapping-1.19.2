package net.nonswag.tnl.mappings.v1_19_R1.api.entity;

import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.nonswag.tnl.listener.api.entity.TNLArmorStand;
import net.nonswag.tnl.listener.api.item.SlotType;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.mappings.v1_19_R1.api.item.SlotWrapper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NMSArmorStand extends ArmorStand implements TNLArmorStand, SlotWrapper {

    public NMSArmorStand(@Nonnull World world, double x, double y, double z, float yaw, float pitch) {
        super(((CraftWorld) world).getHandle(), x, y, z);
        setRot(yaw, pitch);
    }

    @Override
    public void setX(double x) {
        super.moveTo(x, position().y(), position().z());
    }

    @Override
    public void setY(double y) {
        super.moveTo(position().x(), y, position().z());
    }

    @Override
    public void setZ(double z) {
        super.moveTo(position().x(), position().y(), z);
    }

    @Override
    public void updateSize() {
    }

    @Override
    public boolean doAITick() {
        return isEffectiveAi();
    }

    @Override
    public void setHeadRotation(float rotation) {
        setYHeadRot(rotation);
    }

    @Override
    public void killEntity() {
        remove(RemovalReason.KILLED);
    }

    @Override
    public void setArms(boolean arms) {
        setShowArms(arms);
    }

    @Override
    public boolean hasArms() {
        return isShowArms();
    }

    @Override
    public void setHeadPose(@Nullable Pose pose) {
        if (pose != null) super.setHeadPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public void setBodyPose(@Nullable Pose pose) {
        if (pose != null) super.setBodyPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public void setLeftArmPose(@Nullable Pose pose) {
        if (pose != null) super.setLeftArmPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public void setRightArmPose(@Nullable Pose pose) {
        if (pose != null) super.setRightArmPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public void setLeftLegPose(@Nullable Pose pose) {
        if (pose != null) super.setLeftLegPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public void setRightLegPose(@Nullable Pose pose) {
        if (pose != null) super.setRightLegPose(new Rotations(pose.getPitch(), pose.getYaw(), pose.getRoll()));
    }

    @Override
    public boolean isInteractable() {
        return false;
    }

    @Override
    public void setCustomName(@Nullable String customName) {
        setCustomName(customName != null ? Component.literal(customName) : null);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setInvisible(!visible);
    }

    @Override
    public void setGravity(boolean gravity) {
        super.setNoGravity(!gravity);
    }

    @Override
    public void setBasePlate(boolean flag) {
        super.setNoBasePlate(flag);
    }

    @Override
    public boolean hasBasePlate() {
        return false;
    }

    @Override
    public void setItemInMainHand(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public void setItemInOffHand(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public void setHelmet(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public void setChestplate(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public void setLeggings(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public void setBoots(@Nullable TNLItem item) {
        super.setItemSlot(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(item != null ? item.getItemStack() : null), true);
    }

    @Override
    public SynchedEntityData getDataWatcher() {
        return super.getEntityData();
    }

    @Override
    public void setLocation(@Nonnull Location location) {
        setLocation(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public void setLocation(double x, double y, double z) {
        moveTo(x, y, z);
    }

    @Override
    public void setLocation(double v, double v1, double v2, float v3, float v4) {

    }

    @Override
    public void setItem(@Nonnull SlotType slot, @Nonnull TNLItem item) {
        setItemSlot(wrap(slot), CraftItemStack.asNMSCopy(item.getItemStack()), true);
    }

    @Override
    public int getEntityId() {
        return super.getId();
    }

    @Nonnull
    @Override
    public CraftLivingEntity bukkit() {
        return (CraftLivingEntity) super.getBukkitEntity();
    }
}
