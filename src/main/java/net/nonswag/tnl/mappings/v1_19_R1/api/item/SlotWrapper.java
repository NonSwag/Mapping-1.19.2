package net.nonswag.tnl.mappings.v1_19_R1.api.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.nonswag.tnl.listener.api.item.SlotType;

import javax.annotation.Nonnull;

public interface SlotWrapper {

    @Nonnull
    default EquipmentSlot wrap(@Nonnull SlotType type) {
        return switch (type) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        };
    }
}
