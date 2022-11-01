package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.border.VirtualBorder;
import net.nonswag.tnl.listener.api.item.SlotType;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.location.Position;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.packets.outgoing.*;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.boss.CraftBossBar;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.wrap;

public final class OutgoingPacketManager extends Mapping.PacketManager.Outgoing {

    @Nonnull
    @Override
    public BlockBreakAnimationPacket blockBreakAnimationPacket(@Nonnull BlockLocation location, int state) {
        return new BlockBreakAnimationPacket(location, state) {
            @NotNull
            @Override
            public ClientboundBlockDestructionPacket build() {
                return new ClientboundBlockDestructionPacket(getLocation().getBlock().hashCode(), wrap(getLocation()), getState());
            }
        };
    }

    @Nonnull
    @Override
    public BossBarPacket bossBarPacket(@Nonnull BossBarPacket.Action action, @Nonnull BossBar bossBar) {
        return new BossBarPacket(action, bossBar) {
            @NotNull
            @Override
            public ClientboundBossEventPacket build() {
                return switch (getAction()) {
                    case ADD -> ClientboundBossEventPacket.createAddPacket(((CraftBossBar) getBossBar()).getHandle());
                    case REMOVE -> ClientboundBossEventPacket.createRemovePacket(((CraftBossBar) getBossBar()).getHandle().getId());
                    case UPDATE_PCT -> ClientboundBossEventPacket.createUpdateProgressPacket(((CraftBossBar) getBossBar()).getHandle());
                    case UPDATE_NAME -> ClientboundBossEventPacket.createUpdateNamePacket(((CraftBossBar) getBossBar()).getHandle());
                    case UPDATE_STYLE -> ClientboundBossEventPacket.createUpdateStylePacket(((CraftBossBar) getBossBar()).getHandle());
                    case UPDATE_PROPERTIES -> ClientboundBossEventPacket.createUpdatePropertiesPacket(((CraftBossBar) getBossBar()).getHandle());
                };
            }
        };
    }

    @Nonnull
    @Override
    public CameraPacket cameraPacket(int targetId) {
        return new CameraPacket(targetId) {
            @NotNull
            @Override
            public ClientboundSetCameraPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getTargetId());
                return new ClientboundSetCameraPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public ChatPacket chatPacket(@Nonnull String message, @Nonnull ChatPacket.Type type, @Nonnull UUID sender) {
        return new ChatPacket(message, type, sender) {
            @NotNull
            @Override
            public ClientboundSystemChatPacket build() {
                return new ClientboundSystemChatPacket(Component.nullToEmpty(getMessage()), false);
            }
        };
    }

    @Nonnull
    @Override
    public CloseWindowPacket closeWindowPacket(int windowId) {
        return new CloseWindowPacket(windowId) {
            @NotNull
            @Override
            public ClientboundContainerClosePacket build() {
                return new ClientboundContainerClosePacket(getWindowId());
            }
        };
    }

    @Nonnull
    @Override
    public CooldownPacket cooldownPacket(@Nonnull Material item, int cooldown) {
        return new CooldownPacket(item, cooldown) {
            @NotNull
            @Override
            public ClientboundCooldownPacket build() {
                return new ClientboundCooldownPacket(CraftMagicNumbers.getItem(getItem()), getCooldown());
            }
        };
    }

    @Nonnull
    @Override
    public CustomPayloadPacket customPayloadPacket(@Nonnull String channel, @Nonnull byte[]... bytes) {
        return new CustomPayloadPacket(channel, bytes) {
            @NotNull
            @Override
            public ClientboundCustomPayloadPacket build() {
                return new ClientboundCustomPayloadPacket(new ResourceLocation(getChannel()), new FriendlyByteBuf(Unpooled.wrappedBuffer(getBytes())));
            }
        };
    }

    @Nonnull
    @Override
    public EntityAnimationPacket entityAnimationPacket(int entityId, @Nonnull EntityAnimationPacket.Animation animation) {
        return new EntityAnimationPacket(entityId, animation) {
            @NotNull
            @Override
            public ClientboundAnimatePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(getAnimation().getId());
                return new ClientboundAnimatePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public EntityAttachPacket entityAttachPacket(int holderId, int leashedId) {
        return new EntityAttachPacket(holderId, leashedId) {
            @NotNull
            @Override
            public ClientboundSetEntityLinkPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeInt(getLeashedId());
                buffer.writeInt(getHolderId());
                return new ClientboundSetEntityLinkPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public EntityDestroyPacket entityDestroyPacket(int... destroyIds) {
        return new EntityDestroyPacket() {
            @NotNull
            @Override
            public ClientboundRemoveEntitiesPacket build() {
                return new ClientboundRemoveEntitiesPacket(getDestroyIds());
            }
        };
    }

    @Nonnull
    @Override
    public EntityEquipmentPacket entityEquipmentPacket(int entityId, @Nonnull HashMap<SlotType, TNLItem> equipment) {
        return new EntityEquipmentPacket(entityId, equipment) {
            @NotNull
            @Override
            public ClientboundSetEquipmentPacket build() {
                List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = new ArrayList<>();
                getEquipment().forEach((slot, itemStack) -> equipment.add(new Pair<>(wrap(slot), CraftItemStack.asNMSCopy(itemStack))));
                return new ClientboundSetEquipmentPacket(getEntityId(), equipment);
            }
        };
    }

    @Nonnull
    @Override
    public GameStateChangePacket gameStateChangePacket(@Nonnull GameStateChangePacket.Identifier identifier, float state) {
        return new GameStateChangePacket(identifier, state) {
            @NotNull
            @Override
            public ClientboundGameEventPacket build() {
                return new ClientboundGameEventPacket(wrap(getIdentifier()), getState());
            }
        };
    }

    @Nonnull
    @Override
    public EntityStatusPacket entityStatusPacket(int entityId, @Nonnull EntityStatusPacket.Status status) {
        return new EntityStatusPacket(entityId, status) {
            @NotNull
            @Override
            public ClientboundEntityEventPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeInt(getEntityId());
                buffer.writeByte(getStatus().getId());
                return new ClientboundEntityEventPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public EntitySpawnPacket entitySpawnPacket(@Nonnull Entity entity) {
        return new EntitySpawnPacket(entity) {
            @NotNull
            @Override
            public ClientboundAddEntityPacket build() {
                return new ClientboundAddEntityPacket(((CraftEntity) getEntity()).getHandle());
            }
        };
    }

    @Nonnull
    @Override
    public <W> EntityMetadataPacket<W> entityMetadataPacket(int entityId, @Nonnull W dataWatcher, boolean updateAll) {
        return new EntityMetadataPacket<>(entityId, dataWatcher, updateAll) {
            @NotNull
            @Override
            public ClientboundSetEntityDataPacket build() {
                return new ClientboundSetEntityDataPacket(getEntityId(), (SynchedEntityData) getMetadata(), isUpdateAll());
            }
        };
    }

    @Nonnull
    @Override
    public <W> EntityMetadataPacket<W> entityMetadataPacket(@Nonnull Entity entity, boolean updateAll) {
        return entityMetadataPacket(entity.getEntityId(), (W) ((CraftEntity) entity).getHandle().getEntityData(), updateAll);
    }

    @Nonnull
    @Override
    public EntityHeadRotationPacket entityHeadRotationPacket(int entityId, float yaw) {
        return new EntityHeadRotationPacket(entityId, yaw) {
            @NotNull
            @Override
            public ClientboundRotateHeadPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(Mth.floor(getYaw() * 256f / 360f));
                return new ClientboundRotateHeadPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public EntityBodyRotationPacket entityBodyRotationPacket(int entityId, float rotation) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public EntityTeleportPacket entityTeleportPacket(int entityId, @Nonnull Position position) {
        return new EntityTeleportPacket(entityId, position) {
            @NotNull
            @Override
            public ClientboundTeleportEntityPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeDouble(getPosition().getX());
                buffer.writeDouble(getPosition().getY());
                buffer.writeDouble(getPosition().getZ());
                buffer.writeByte(Mth.floor(getPosition().getYaw() * 256.0F / 360.0F));
                buffer.writeByte(Mth.floor(getPosition().getPitch() * 256.0F / 360.0F));
                buffer.writeBoolean(false);
                return new ClientboundTeleportEntityPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public EntityVelocityPacket entityVelocityPacket(int entityId, @Nonnull Vector vector) {
        return new EntityVelocityPacket(entityId, vector) {
            @NotNull
            @Override
            public ClientboundSetEntityMotionPacket build() {
                Vec3 velocity = new Vec3(getVector().getX(), getVector().getY(), getVector().getZ());
                return new ClientboundSetEntityMotionPacket(getEntityId(), velocity);
            }
        };
    }

    @Nonnull
    @Override
    public LivingEntitySpawnPacket livingEntitySpawnPacket(@Nonnull LivingEntity entity) {
        return new LivingEntitySpawnPacket(entity) {
            @NotNull
            @Override
            public ClientboundAddEntityPacket build() {
                return new ClientboundAddEntityPacket(((CraftLivingEntity) getEntity()).getHandle());
            }
        };
    }

    @Nonnull
    @Override
    public MapChunkPacket mapChunkPacket(@Nonnull Chunk chunk, int section) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public MountPacket mountPacket(int holderId, int[] mounts) {
        return new MountPacket(holderId, mounts) {
            @NotNull
            @Override
            public ClientboundSetPassengersPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getHolderId());
                buffer.writeVarIntArray(getMounts());
                return new ClientboundSetPassengersPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public NamedEntitySpawnPacket namedEntitySpawnPacket(@Nonnull HumanEntity human) {
        return new NamedEntitySpawnPacket(human) {
            @NotNull
            @Override
            public ClientboundAddPlayerPacket build() {
                return new ClientboundAddPlayerPacket(((CraftHumanEntity) getHuman()).getHandle());
            }
        };
    }

    @Nonnull
    @Override
    public OpenSignPacket openSignPacket(@Nonnull BlockLocation location) {
        return new OpenSignPacket(location) {
            @NotNull
            @Override
            public ClientboundOpenSignEditorPacket build() {
                return new ClientboundOpenSignEditorPacket(wrap(getLocation()));
            }
        };
    }

    @Nonnull
    @Override
    public OpenWindowPacket openWindowPacket(int windowId, @Nonnull OpenWindowPacket.Type type, @Nonnull String title) {
        return new OpenWindowPacket(windowId, type, title) {
            @NotNull
            @Override
            public ClientboundOpenScreenPacket build() {
                return new ClientboundOpenScreenPacket(getWindowId(), wrap(getType()), Component.literal(getTitle()));
            }
        };
    }

    @Nonnull
    @Override
    public PlayerInfoPacket playerInfoPacket(@Nonnull Player player, @Nonnull PlayerInfoPacket.Action action) {
        return new PlayerInfoPacket(player, action) {
            @NotNull
            @Override
            public ClientboundPlayerInfoPacket build() {
                return new ClientboundPlayerInfoPacket(wrap(getAction()), ((CraftPlayer) getPlayer()).getHandle());
            }
        };
    }

    @Nonnull
    @Override
    public SetSlotPacket setSlotPacket(@Nonnull SetSlotPacket.Inventory inventory, int slot, @Nullable ItemStack itemStack) {
        return new SetSlotPacket(inventory, slot, itemStack) {
            @NotNull
            @Override
            public ClientboundContainerSetSlotPacket build() {
                return new ClientboundContainerSetSlotPacket(getInventory().getId(), 0, getSlot(), CraftItemStack.asNMSCopy(getItemStack()));
            }
        };
    }

    @Nonnull
    @Override
    public TitlePacket titlePacket(@Nonnull TitlePacket.Action action, @Nullable String text, int timeIn, int timeStay, int timeOut) {
        return new TitlePacket(action, text, timeIn, timeStay, timeOut) {
            @NotNull
            @Override
            public Packet<ClientGamePacketListener> build() {
                return switch (getAction()) {
                    case CLEAR -> new ClientboundClearTitlesPacket(false);
                    case RESET -> new ClientboundClearTitlesPacket(true);
                    case TITLE -> new ClientboundSetTitleTextPacket(Component.nullToEmpty(getText()));
                    case SUBTITLE -> new ClientboundSetSubtitleTextPacket(Component.nullToEmpty(getText()));
                    case ACTIONBAR -> new ClientboundSetActionBarTextPacket(Component.nullToEmpty(getText()));
                    case TIMES -> new ClientboundSetTitlesAnimationPacket(getTimeIn(), getTimeStay(), getTimeOut());
                };
            }
        };
    }

    @Nonnull
    @Override
    public UpdateTimePacket updateTimePacket(long age, long timestamp, boolean cycle) {
        return new UpdateTimePacket(age, timestamp, cycle) {
            @NotNull
            @Override
            public ClientboundSetTimePacket build() {
                return new ClientboundSetTimePacket(getAge(), getTimestamp(), isCycle());
            }
        };
    }

    @Nonnull
    @Override
    public WindowDataPacket windowDataPacket(int windowId, int property, int value) {
        return new WindowDataPacket(windowId, property, value) {
            @NotNull
            @Override
            public ClientboundContainerSetDataPacket build() {
                return new ClientboundContainerSetDataPacket(getWindowId(), getProperty(), getValue());
            }
        };
    }

    @Nonnull
    @Override
    public WindowItemsPacket windowItemsPacket(int windowId, @Nonnull List<ItemStack> items) {
        return new WindowItemsPacket(windowId, items) {
            @NotNull
            @Override
            public ClientboundContainerSetContentPacket build() {
                NonNullList<net.minecraft.world.item.ItemStack> items = NonNullList.create();
                for (org.bukkit.inventory.ItemStack item : getItems()) items.add(CraftItemStack.asNMSCopy(item));
                return new ClientboundContainerSetContentPacket(getWindowId(), 0, items, net.minecraft.world.item.ItemStack.EMPTY);
            }
        };
    }

    @Nonnull
    @Override
    public WorldBorderPacket worldBorderPacket(@Nonnull VirtualBorder virtualBorder, @Nonnull WorldBorderPacket.Action action) {
        return new WorldBorderPacket(virtualBorder, action) {
            @NotNull
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
        };
    }

    @Nonnull
    @Override
    public ResourcePackPacket resourcePackPacket(@Nonnull String url, @Nullable String hash, @Nullable String prompt, boolean required) {
        return new ResourcePackPacket(url, hash, prompt, required) {
            @NotNull
            @Override
            public ClientboundResourcePackPacket build() {
                return new ClientboundResourcePackPacket(getUrl(), String.valueOf(getHash()), isRequired(), getPrompt() != null ? Component.literal(getPrompt()) : null);
            }
        };
    }

    @Nonnull
    @Override
    public ItemTakePacket itemTakePacket(int itemId, int collectorId, int amount) {
        return new ItemTakePacket(itemId, collectorId, amount) {
            @NotNull
            @Override
            public ClientboundTakeItemEntityPacket build() {
                return new ClientboundTakeItemEntityPacket(getItemId(), getCollectorId(), getAmount());
            }
        };
    }

    @Nonnull
    @Override
    public <P> PacketBuilder map(@Nonnull P packet) {
        if (packet instanceof ClientboundInitializeBorderPacket instance) {
        } else if (packet instanceof ClientboundAnimatePacket instance) {
        } else if (packet instanceof ClientboundSetExperiencePacket instance) {
        } else if (packet instanceof ClientboundCommandSuggestionsPacket instance) {
        } else if (packet instanceof ClientboundSelectAdvancementsTabPacket instance) {
        } else if (packet instanceof ClientboundSetDisplayChatPreviewPacket instance) {
        } else if (packet instanceof ClientboundHorseScreenOpenPacket instance) {
        } else if (packet instanceof ClientboundMoveVehiclePacket instance) {
        } else if (packet instanceof ClientboundSetCameraPacket instance) {
        } else if (packet instanceof ClientboundGameEventPacket instance) {
        } else if (packet instanceof ClientboundStopSoundPacket instance) {
        } else if (packet instanceof ClientboundOpenBookPacket instance) {
        } else if (packet instanceof ClientboundLightUpdatePacket instance) {
        } else if (packet instanceof ClientboundSetCarriedItemPacket instance) {
        } else if (packet instanceof ClientboundSetDisplayObjectivePacket instance) {
        } else if (packet instanceof ClientboundSetTimePacket instance) {
        } else if (packet instanceof ClientboundContainerSetContentPacket instance) {
        } else if (packet instanceof ClientboundSetPlayerTeamPacket instance) {
        } else if (packet instanceof ClientboundUpdateTagsPacket instance) {
        } else if (packet instanceof ClientboundSetSimulationDistancePacket instance) {
        } else if (packet instanceof ClientboundChatPreviewPacket instance) {
        } else if (packet instanceof ClientboundLevelChunkPacketData instance) {
        } else if (packet instanceof ClientboundTagQueryPacket instance) {
        } else if (packet instanceof ClientboundSetChunkCacheRadiusPacket instance) {
        } else if (packet instanceof ClientboundRotateHeadPacket instance) {
        } else if (packet instanceof ClientboundLoginPacket instance) {
        } else if (packet instanceof ClientboundLightUpdatePacketData instance) {
        } else if (packet instanceof ClientboundTakeItemEntityPacket instance) {
        } else if (packet instanceof ClientboundSetChunkCacheCenterPacket instance) {
        } else if (packet instanceof ClientboundCustomPayloadPacket instance) {
        } else if (packet instanceof ClientboundSectionBlocksUpdatePacket instance) {
        } else if (packet instanceof ClientboundBlockDestructionPacket instance) {
        } else if (packet instanceof ClientboundUpdateRecipesPacket instance) {
        } else if (packet instanceof ClientboundDisconnectPacket instance) {
        } else if (packet instanceof ClientboundSoundEntityPacket instance) {
        } else if (packet instanceof ClientboundPingPacket instance) {
        } else if (packet instanceof ClientboundPlayerChatHeaderPacket instance) {
        } else if (packet instanceof ClientboundSetEntityDataPacket instance) {
        } else if (packet instanceof ClientboundOpenSignEditorPacket instance) {
        } else if (packet instanceof ClientboundBlockChangedAckPacket instance) {
        } else if (packet instanceof ClientboundSetBorderCenterPacket instance) {
        } else if (packet instanceof ClientboundAddExperienceOrbPacket instance) {
        } else if (packet instanceof ClientboundMerchantOffersPacket instance) {
        } else if (packet instanceof ClientboundRemoveEntitiesPacket instance) {
        } else if (packet instanceof ClientboundSetBorderWarningDistancePacket instance) {
        } else if (packet instanceof ClientboundSetSubtitleTextPacket instance) {
        } else if (packet instanceof ClientboundBlockEntityDataPacket instance) {
        } else if (packet instanceof ClientboundUpdateAttributesPacket instance) {
        } else if (packet instanceof ClientboundExplodePacket instance) {
        } else if (packet instanceof ClientboundPlayerCombatEnterPacket instance) {
        } else if (packet instanceof ClientboundBlockEventPacket instance) {
        } else if (packet instanceof ClientboundSetEntityLinkPacket instance) {
        } else if (packet instanceof ClientboundCommandsPacket instance) {
        } else if (packet instanceof ClientboundLevelParticlesPacket instance) {
        } else if (packet instanceof ClientboundPlayerCombatKillPacket instance) {
        } else if (packet instanceof ClientboundSetTitleTextPacket instance) {
        } else if (packet instanceof ClientboundSoundPacket instance) {
        } else if (packet instanceof ClientboundContainerSetSlotPacket instance) {
        } else if (packet instanceof ClientboundRecipePacket instance) {
        } else if (packet instanceof ClientboundPlaceGhostRecipePacket instance) {
        } else if (packet instanceof ClientboundBlockUpdatePacket instance) {
        } else if (packet instanceof ClientboundSetDefaultSpawnPositionPacket instance) {
        } else if (packet instanceof ClientboundOpenScreenPacket instance) {
        } else if (packet instanceof ClientboundSetEquipmentPacket instance) {
        } else if (packet instanceof ClientboundSetTitlesAnimationPacket instance) {
        } else if (packet instanceof ClientboundMoveEntityPacket instance) {
        } else if (packet instanceof ClientboundAddPlayerPacket instance) {
        } else if (packet instanceof ClientboundCustomChatCompletionsPacket instance) {
        } else if (packet instanceof ClientboundAwardStatsPacket instance) {
        } else if (packet instanceof ClientboundPlayerPositionPacket instance) {
        } else if (packet instanceof ClientboundPlayerInfoPacket instance) {
        } else if (packet instanceof ClientboundSetObjectivePacket instance) {
        } else if (packet instanceof ClientboundPlayerCombatEndPacket instance) {
        } else if (packet instanceof ClientboundCustomSoundPacket instance) {
        } else if (packet instanceof ClientboundEntityEventPacket instance) {
        } else if (packet instanceof ClientboundDeleteChatPacket instance) {
        } else if (packet instanceof ClientboundContainerSetDataPacket instance) {
        } else if (packet instanceof ClientboundSetEntityMotionPacket instance) {
        } else if (packet instanceof ClientboundSetBorderSizePacket instance) {
        } else if (packet instanceof ClientboundPlayerChatPacket instance) {
        } else if (packet instanceof ClientboundSetBorderWarningDelayPacket instance) {
        } else if (packet instanceof ClientboundTabListPacket instance) {
        } else if (packet instanceof ClientboundChangeDifficultyPacket instance) {
        } else if (packet instanceof ClientboundKeepAlivePacket instance) {
        } else if (packet instanceof ClientboundClearTitlesPacket instance) {
        } else if (packet instanceof ClientboundSetActionBarTextPacket instance) {
        } else if (packet instanceof ClientboundMapItemDataPacket instance) {
        } else if (packet instanceof ClientboundForgetLevelChunkPacket instance) {
        } else if (packet instanceof ClientboundPlayerAbilitiesPacket instance) {
        } else if (packet instanceof ClientboundResourcePackPacket instance) {
        } else if (packet instanceof ClientboundCooldownPacket instance) {
        } else if (packet instanceof ClientboundContainerClosePacket instance) {
        } else if (packet instanceof ClientboundTeleportEntityPacket instance) {
        } else if (packet instanceof ClientboundRespawnPacket instance) {
        } else if (packet instanceof ClientboundBossEventPacket instance) {
        } else if (packet instanceof ClientboundSystemChatPacket instance) {
        } else if (packet instanceof ClientboundAddEntityPacket instance) {
        } else if (packet instanceof ClientboundLevelEventPacket instance) {
        } else if (packet instanceof ClientboundUpdateMobEffectPacket instance) {
        } else if (packet instanceof ClientboundSetBorderLerpSizePacket instance) {
        } else if (packet instanceof ClientboundUpdateAdvancementsPacket instance) {
        } else if (packet instanceof ClientboundRemoveMobEffectPacket instance) {
        } else if (packet instanceof ClientboundSetHealthPacket instance) {
        } else if (packet instanceof ClientboundServerDataPacket instance) {
        } else if (packet instanceof ClientboundSetPassengersPacket instance) {
        } else if (packet instanceof ClientboundSetScorePacket instance) {
        } else if (packet instanceof ClientboundPlayerLookAtPacket instance) {
        } else if (packet instanceof ClientboundLevelChunkWithLightPacket instance) {
        }
        Logger.error.println("Unmapped outgoing (vanilla) packet: " + packet.getClass().getName());
        return new PacketBuilder() {
            @Nonnull
            @Override
            public P build() {
                return packet;
            }

            @Override
            public void send(@Nonnull TNLPlayer... players) {
                for (TNLPlayer player : players) player.pipeline().sendPacket(build());
            }
        };
    }
}