package net.nonswag.tnl.mappings.v1_19_R1.api.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.effect.MobEffectInstance;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.item.FoodProperties;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.nbt.NBTTag;
import net.nonswag.tnl.mappings.v1_19_R1.api.nbt.NBT;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.potion.CraftPotionEffectType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class NMSItem extends TNLItem {

    public NMSItem(@Nonnull ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public int getMaxDurability() {
        return CraftItemStack.asNMSCopy(this).getItem().getMaxDamage();
    }

    @Nonnull
    @Override
    public NMSItem modifyNBT(@Nonnull String nbt) {
        try {
            setNBT(getNBT().append(new NBT(TagParser.parseTag(nbt))));
        } catch (CommandSyntaxException e) {
            Logger.error.println("Failed to modify item nbt", e);
        }
        return this;
    }

    @Nullable
    @Override
    public FoodProperties getFoodProperties() {
        var properties = CraftItemStack.asNMSCopy(this).getItem().getFoodProperties();
        if (properties == null) return null;
        HashMap<PotionEffect, Float> effects = new HashMap<>();
        for (Pair<MobEffectInstance, Float> pair : properties.getEffects()) {
            MobEffectInstance first = pair.getFirst();
            CraftPotionEffectType type = new CraftPotionEffectType(first.getEffect());
            effects.put(new PotionEffect(type, first.getDuration(), first.getAmplifier(), first.isAmbient(), first.isVisible(), first.showIcon()), pair.getSecond());
        }
        return new FoodProperties(properties.getNutrition(), properties.getSaturationModifier(), properties.isMeat(), properties.canAlwaysEat(), properties.isFastFood(), effects);
    }

    @Override
    public boolean isFood() {
        return CraftItemStack.asNMSCopy(this).getItem().getFoodProperties() != null;
    }

    @Nonnull
    @Override
    public NBT getNBT() {
        return new NBT(CraftItemStack.asNMSCopy(this).getOrCreateTag());
    }

    @Nonnull
    @Override
    public NMSItem setNBT(@Nonnull NBTTag nbt) {
        var item = CraftItemStack.asNMSCopy(this);
        item.setTag(nbt.versioned());
        setItemMeta(CraftItemStack.getItemMeta(item));
        return this;
    }

    @Nonnull
    @Override
    public HoverEvent<HoverEvent.ShowItem> asHoverEvent() {
        return super.asHoverEvent();
    }
}
