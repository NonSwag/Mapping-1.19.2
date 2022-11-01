package net.nonswag.tnl.mappings.v1_19_R1.api.wrapper;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.commands.arguments.ArgumentSignatures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.LastSeenMessages;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.nonswag.tnl.listener.api.item.SlotType;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.location.BlockPosition;
import net.nonswag.tnl.listener.api.packets.incoming.ChatCommandPacket;
import net.nonswag.tnl.listener.api.packets.incoming.SetBeaconPacket;
import net.nonswag.tnl.listener.api.packets.incoming.UseItemOnPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.GameStateChangePacket;
import net.nonswag.tnl.listener.api.packets.outgoing.OpenWindowPacket;
import net.nonswag.tnl.listener.api.packets.outgoing.PlayerInfoPacket;
import net.nonswag.tnl.listener.api.player.Hand;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class NMSHelper {

    @Nonnull
    public static EquipmentSlot wrap(@Nonnull SlotType type) {
        return switch (type) {
            case MAIN_HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case HELMET -> EquipmentSlot.HEAD;
            case CHESTPLATE -> EquipmentSlot.CHEST;
            case LEGGINGS -> EquipmentSlot.LEGS;
            case BOOTS -> EquipmentSlot.FEET;
        };
    }

    @Nonnull
    public static ClientboundGameEventPacket.Type wrap(@Nonnull GameStateChangePacket.Identifier identifier) {
        return switch (identifier.getId()) {
            case 0 -> ClientboundGameEventPacket.NO_RESPAWN_BLOCK_AVAILABLE;
            case 1 -> ClientboundGameEventPacket.START_RAINING;
            case 2 -> ClientboundGameEventPacket.STOP_RAINING;
            case 3 -> ClientboundGameEventPacket.CHANGE_GAME_MODE;
            case 4 -> ClientboundGameEventPacket.WIN_GAME;
            case 5 -> ClientboundGameEventPacket.DEMO_EVENT;
            case 6 -> ClientboundGameEventPacket.ARROW_HIT_PLAYER;
            case 7 -> ClientboundGameEventPacket.RAIN_LEVEL_CHANGE;
            case 8 -> ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE;
            case 9 -> ClientboundGameEventPacket.PUFFER_FISH_STING;
            case 10 -> ClientboundGameEventPacket.GUARDIAN_ELDER_EFFECT;
            case 11 -> ClientboundGameEventPacket.IMMEDIATE_RESPAWN;
            default -> throw new IllegalStateException("Unexpected value: " + identifier.getId());
        };
    }

    @Nonnull
    public static ClientboundPlayerInfoPacket.Action wrap(@Nonnull PlayerInfoPacket.Action action) {
        return switch (action) {
            case ADD_PLAYER -> ClientboundPlayerInfoPacket.Action.ADD_PLAYER;
            case REMOVE_PLAYER -> ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER;
            case UPDATE_LATENCY -> ClientboundPlayerInfoPacket.Action.UPDATE_LATENCY;
            case UPDATE_GAME_MODE -> ClientboundPlayerInfoPacket.Action.UPDATE_GAME_MODE;
            case UPDATE_DISPLAY_NAME -> ClientboundPlayerInfoPacket.Action.UPDATE_DISPLAY_NAME;
        };
    }

    @Nonnull
    public static MenuType<?> wrap(@Nonnull OpenWindowPacket.Type type) {
        return switch (type) {
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

    @Nonnull
    public static Vec3i wrap(@Nonnull Vector vector) {
        return new Vec3i(vector.getX(), vector.getY(), vector.getZ());
    }

    @Nonnull
    public static net.minecraft.world.item.ItemStack wrap(@Nonnull TNLItem item) {
        return CraftItemStack.asNMSCopy(item);
    }

    @Nullable
    public static MobEffect nullable(@Nullable SetBeaconPacket.Effect effect) {
        return effect != null ? MobEffect.byId(effect.id()) : null;
    }

    @Nullable
    public static ResourceLocation nullable(@Nullable NamespacedKey namespacedKey) {
        return namespacedKey != null ? wrap(namespacedKey) : null;
    }

    @Nonnull
    public static ResourceLocation wrap(@Nonnull NamespacedKey channel) {
        return new ResourceLocation(channel.getNamespace(), channel.getKey());
    }

    @Nonnull
    public static ArgumentSignatures wrap(@Nonnull ChatCommandPacket.Entry[] argumentSignatures) {
        List<ArgumentSignatures.Entry> entries = new ArrayList<>();
        for (ChatCommandPacket.Entry signature : argumentSignatures) {
            entries.add(new ArgumentSignatures.Entry(signature.getName(), new MessageSignature(signature.getSignature())));
        }
        return new ArgumentSignatures(entries);
    }

    @Nonnull
    public static net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update wrap(@Nonnull LastSeenMessages.Update lastSeenMessages) {
        List<net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry> entries = new ArrayList<>();
        lastSeenMessages.lastSeen().entries().forEach(entry -> entries.add(wrap(entry)));
        return new net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update(new net.nonswag.tnl.listener.api.chat.LastSeenMessages(entries), nullable(lastSeenMessages.lastReceived().orElse(null)));
    }

    @Nonnull
    public static net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry wrap(@Nonnull LastSeenMessages.Entry entry) {
        return new net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry(entry.profileId(), entry.lastSignature().bytes());
    }

    @Nullable
    public static net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry nullable(@Nullable LastSeenMessages.Entry entry) {
        return entry != null ? wrap(entry) : null;
    }

    @Nonnull
    public static LastSeenMessages.Update wrap(@Nonnull net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update lastSeenMessages) {
        List<LastSeenMessages.Entry> entries = new ArrayList<>();
        lastSeenMessages.getLastSeen().getEntries().forEach(entry -> entries.add(wrap(entry)));
        return new LastSeenMessages.Update(new LastSeenMessages(entries), Optional.ofNullable(nullable(lastSeenMessages.getLastReceived())));
    }

    @Nonnull
    public static LastSeenMessages.Entry wrap(@Nonnull net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry entry) {
        return new LastSeenMessages.Entry(entry.profileId(), new MessageSignature(entry.lastSignature()));
    }

    @Nullable
    public static LastSeenMessages.Entry nullable(@Nullable net.nonswag.tnl.listener.api.chat.LastSeenMessages.Entry entry) {
        return entry != null ? wrap(entry) : null;
    }

    @Nonnull
    public static BlockPos wrap(@Nonnull BlockPosition position) {
        return new BlockPos(position.getX(), position.getY(), position.getZ());
    }

    @Nonnull
    public static UseItemOnPacket.BlockTargetResult wrap(@Nonnull BlockHitResult result) {
        return new UseItemOnPacket.BlockTargetResult(result.getType().equals(HitResult.Type.MISS),
                wrap(result.getLocation()), wrap(result.getBlockPos()), wrap(result.getDirection()), result.isInside());
    }

    @Nonnull
    public static BlockHitResult wrap(@Nonnull UseItemOnPacket.BlockTargetResult result) {
        Vec3 location = new Vec3(result.getLocation().getX(), result.getLocation().getY(), result.getLocation().getZ());
        if (result.isMissed()) return BlockHitResult.miss(location, wrap(result.getSide()), wrap(result.getPosition()));
        return new BlockHitResult(location, wrap(result.getSide()), wrap(result.getPosition()), true);
    }

    @Nonnull
    public static Direction wrap(@Nonnull net.nonswag.tnl.listener.api.location.Direction direction) {
        return switch (direction) {
            case UP -> net.minecraft.core.Direction.UP;
            case DOWN -> net.minecraft.core.Direction.DOWN;
            case NORTH -> net.minecraft.core.Direction.NORTH;
            case SOUTH -> net.minecraft.core.Direction.SOUTH;
            case EAST -> net.minecraft.core.Direction.EAST;
            case WEST -> net.minecraft.core.Direction.WEST;
        };
    }

    @Nonnull
    public static net.nonswag.tnl.listener.api.location.Direction wrap(@Nonnull Direction direction) {
        return switch (direction) {
            case UP -> net.nonswag.tnl.listener.api.location.Direction.UP;
            case DOWN -> net.nonswag.tnl.listener.api.location.Direction.DOWN;
            case NORTH -> net.nonswag.tnl.listener.api.location.Direction.NORTH;
            case SOUTH -> net.nonswag.tnl.listener.api.location.Direction.SOUTH;
            case EAST -> net.nonswag.tnl.listener.api.location.Direction.EAST;
            case WEST -> net.nonswag.tnl.listener.api.location.Direction.WEST;
        };
    }

    @Nonnull
    public static Hand wrap(@Nonnull InteractionHand hand) {
        return switch (hand) {
            case MAIN_HAND -> Hand.MAIN_HAND;
            case OFF_HAND -> Hand.OFF_HAND;
        };
    }

    @Nonnull
    public static InteractionHand wrap(@Nonnull Hand hand) {
        return switch (hand) {
            case MAIN_HAND -> InteractionHand.MAIN_HAND;
            case OFF_HAND -> InteractionHand.OFF_HAND;
        };
    }

    @Nonnull
    public static BlockPos wrap(@Nonnull Location location) {
        return new BlockPos(location.getX(), location.getY(), location.getZ());
    }

    @Nullable
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static SetBeaconPacket.Effect wrap(@Nonnull Optional<MobEffect> optional) {
        if (optional.isEmpty()) return null;
        return new SetBeaconPacket.Effect(switch (optional.get().getCategory()) {
            case HARMFUL -> SetBeaconPacket.Effect.Category.HARMFUL;
            case NEUTRAL -> SetBeaconPacket.Effect.Category.NEUTRAL;
            case BENEFICIAL -> SetBeaconPacket.Effect.Category.BENEFICIAL;
        }, optional.get().getColor(), MobEffect.getId(optional.get()));
    }

    @Nonnull
    public static TNLItem wrap(@Nonnull net.minecraft.world.item.ItemStack item) {
        return TNLItem.create(CraftItemStack.asBukkitCopy(item));
    }

    @Nonnull
    public static NamespacedKey wrap(@Nonnull ResourceLocation resource) {
        return new NamespacedKey(resource.getNamespace(), resource.getPath());
    }

    @Nullable
    public static NamespacedKey nullable(@Nullable ResourceLocation resource) {
        return resource != null ? wrap(resource) : null;
    }

    @Nonnull
    public static HashMap<Integer, TNLItem> wrap(@Nonnull Int2ObjectMap<net.minecraft.world.item.ItemStack> changedSlots) {
        HashMap<Integer, TNLItem> result = new HashMap<>();
        changedSlots.forEach((integer, itemStack) -> result.put(integer, wrap(itemStack)));
        return result;
    }

    @Nonnull
    public static Int2ObjectMap<net.minecraft.world.item.ItemStack> wrap(@Nonnull HashMap<Integer, TNLItem> changedSlots) {
        Int2ObjectMap<net.minecraft.world.item.ItemStack> result = new Int2ObjectOpenHashMap<>();
        changedSlots.forEach((integer, itemStack) -> result.put((int) integer, wrap(itemStack)));
        return result;
    }

    @Nonnull
    public static ChatCommandPacket.Entry[] wrap(@Nonnull List<ArgumentSignatures.Entry> entries) {
        ChatCommandPacket.Entry[] signature = new ChatCommandPacket.Entry[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            ArgumentSignatures.Entry entry = entries.get(i);
            signature[i] = new ChatCommandPacket.Entry(entry.name(), entry.signature().bytes());
        }
        return signature;
    }

    @Nonnull
    public static Vector wrap(@Nonnull Vec3i vec3i) {
        return new Vector(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    @Nonnull
    public static Vector wrap(@Nonnull Vec3 vec3) {
        return new Vector(vec3.x(), vec3.y(), vec3.z());
    }

    @Nonnull
    public static BlockPosition wrap(@Nonnull BlockPos pos) {
        return new BlockPosition(pos.getX(), pos.getY(), pos.getZ());
    }
}
