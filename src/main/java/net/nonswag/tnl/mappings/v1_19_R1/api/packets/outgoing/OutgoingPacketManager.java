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
import net.minecraft.world.phys.Vec3;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.border.VirtualBorder;
import net.nonswag.tnl.listener.api.item.SlotType;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.location.BlockPosition;
import net.nonswag.tnl.listener.api.location.Position;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.nbt.NBTTag;
import net.nonswag.tnl.listener.api.packets.outgoing.*;
import net.nonswag.tnl.listener.api.player.Hand;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BossBar;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.nullable;
import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.wrap;

public final class OutgoingPacketManager implements Mapping.PacketManager.Outgoing {

    @Nonnull
    @Override
    public ChatPreviewPacket chatPreviewPacket(int queryId, @Nullable String query) {
        return new ChatPreviewPacket(queryId, query) {
            @Nonnull
            @Override
            public ClientboundChatPreviewPacket build() {
                return new ClientboundChatPreviewPacket(getQueryId(), getQuery() != null ? Component.literal(getQuery()) : null);
            }
        };
    }

    @Nonnull
    @Override
    public SetSimulationDistancePacket setSimulationDistancePacket(int simulationDistance) {
        return new SetSimulationDistancePacket(simulationDistance) {
            @Nonnull
            @Override
            public ClientboundSetSimulationDistancePacket build() {
                return new ClientboundSetSimulationDistancePacket(getSimulationDistance());
            }
        };
    }

    @Nonnull
    @Override
    public SetCarriedItemPacket setCarriedItemPacket(int slot) {
        return new SetCarriedItemPacket(slot) {
            @Nonnull
            @Override
            public ClientboundSetCarriedItemPacket build() {
                return new ClientboundSetCarriedItemPacket(getSlot());
            }
        };
    }

    @Nonnull
    @Override
    public SetDisplayObjectivePacket setDisplayObjectivePacket(int slot, @Nullable String objectiveName) {
        return new SetDisplayObjectivePacket(slot, objectiveName) {
            @Nonnull
            @Override
            public ClientboundSetDisplayObjectivePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeByte(getSlot());
                buffer.writeUtf(getObjectiveName() != null ? getObjectiveName() : "");
                return new ClientboundSetDisplayObjectivePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public BlockDestructionPacket blockDestructionPacket(int id, @Nonnull BlockPosition position, int state) {
        return new BlockDestructionPacket(id, position, state) {
            @Nonnull
            @Override
            public ClientboundBlockDestructionPacket build() {
                return new ClientboundBlockDestructionPacket(getId(), wrap(getPosition()), getState());
            }
        };
    }

    @Nonnull
    @Override
    public SetExperiencePacket setExperiencePacket(float experienceProgress, int totalExperience, int experienceLevel) {
        return new SetExperiencePacket(experienceProgress, totalExperience, experienceLevel) {
            @Nonnull
            @Override
            public ClientboundSetExperiencePacket build() {
                return new ClientboundSetExperiencePacket(getExperienceProgress(), getTotalExperience(), getExperienceLevel());
            }
        };
    }

    @Nonnull
    @Override
    public StopSoundPacket stopSoundPacket(@Nullable NamespacedKey sound, @Nullable SoundCategory category) {
        return new StopSoundPacket(sound, category) {
            @Nonnull
            @Override
            public ClientboundStopSoundPacket build() {
                return new ClientboundStopSoundPacket(nullable(getSound()), nullable(getCategory()));
            }
        };
    }

    @Nonnull
    @Override
    public BossBarPacket bossBarPacket(@Nonnull BossBarPacket.Action action, @Nonnull BossBar bossBar) {
        return new BossBarPacket(action, bossBar) {
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
            @Override
            public ClientboundCustomPayloadPacket build() {
                return new ClientboundCustomPayloadPacket(new ResourceLocation(getChannel()), new FriendlyByteBuf(Unpooled.wrappedBuffer(getBytes())));
            }
        };
    }

    @Nonnull
    @Override
    public AnimationPacket animationPacket(int entityId, @Nonnull AnimationPacket.Animation animation) {
        return new AnimationPacket(entityId, animation) {
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
            @Override
            public ClientboundOpenSignEditorPacket build() {
                return new ClientboundOpenSignEditorPacket(wrap(getLocation()));
            }
        };
    }

    @Nonnull
    @Override
    public OpenBookPacket openBookPacket(@Nonnull Hand hand) {
        return new OpenBookPacket(hand) {
            @Nonnull
            @Override
            public ClientboundOpenBookPacket build() {
                return new ClientboundOpenBookPacket(wrap(getHand()));
            }
        };
    }

    @Nonnull
    @Override
    public MoveVehiclePacket moveVehiclePacket(@Nonnull Position position) {
        return new MoveVehiclePacket(position) {
            @Nonnull
            @Override
            public ClientboundMoveVehiclePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getPosition().getX());
                buffer.writeDouble(getPosition().getY());
                buffer.writeDouble(getPosition().getZ());
                buffer.writeFloat(getPosition().getYaw());
                buffer.writeFloat(getPosition().getPitch());
                return new ClientboundMoveVehiclePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public OpenWindowPacket openWindowPacket(int windowId, @Nonnull OpenWindowPacket.Type type, @Nonnull String title) {
        return new OpenWindowPacket(windowId, type, title) {
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
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
            @Nonnull
            @Override
            public ClientboundContainerSetDataPacket build() {
                return new ClientboundContainerSetDataPacket(getWindowId(), getProperty(), getValue());
            }
        };
    }

    @Nonnull
    @Override
    public ContainerSetContentPacket containerSetContentPacket(int containerId, int stateId, @Nonnull List<TNLItem> content, @Nonnull TNLItem cursor) {
        return new ContainerSetContentPacket(containerId, stateId, content, cursor) {
            @Nonnull
            @Override
            public ClientboundContainerSetContentPacket build() {
                NonNullList<net.minecraft.world.item.ItemStack> items = NonNullList.create();
                for (org.bukkit.inventory.ItemStack item : getContent()) items.add(CraftItemStack.asNMSCopy(item));
                return new ClientboundContainerSetContentPacket(getContainerId(), getStateId(), items, wrap(getCursor()));
            }
        };
    }

    @Nonnull
    @Override
    public InitializeBorderPacket initializeBorderPacket(@Nonnull VirtualBorder virtualBorder) {
        return new InitializeBorderPacket(virtualBorder) {
            @Nonnull
            @Override
            public ClientboundInitializeBorderPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(virtualBorder.getCenter().x());
                buffer.writeDouble(virtualBorder.getCenter().z());
                buffer.writeDouble(virtualBorder.getOldSize());
                buffer.writeDouble(virtualBorder.getNewSize());
                buffer.writeVarLong(virtualBorder.getLerpTime());
                buffer.writeVarInt(VirtualBorder.MAX_SIZE);
                buffer.writeVarInt(virtualBorder.getWarningDistance());
                buffer.writeVarInt(virtualBorder.getWarningDelay());
                return new ClientboundInitializeBorderPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SetBorderSizePacket setBorderSizePacket(double size) {
        return new SetBorderSizePacket(size) {
            @Nonnull
            @Override
            public ClientboundSetBorderSizePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getSize());
                return new ClientboundSetBorderSizePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SetBorderLerpSizePacket setBorderLerpSizePacket(double oldSize, double newSize, long lerpTime) {
        return new SetBorderLerpSizePacket(oldSize, newSize, lerpTime) {
            @Nonnull
            @Override
            public ClientboundSetBorderLerpSizePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getOldSize());
                buffer.writeDouble(getNewSize());
                buffer.writeVarLong(getLerpTime());
                return new ClientboundSetBorderLerpSizePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SetBorderCenterPacket setBorderCenterPacket(@Nonnull VirtualBorder.Center center) {
        return new SetBorderCenterPacket(center) {
            @Nonnull
            @Override
            public ClientboundSetBorderCenterPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getCenter().x());
                buffer.writeDouble(getCenter().z());
                return new ClientboundSetBorderCenterPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SetBorderWarningDelayPacket setBorderWarningDelayPacket(int warningDelay) {
        return new SetBorderWarningDelayPacket(warningDelay) {
            @Nonnull
            @Override
            public ClientboundSetBorderWarningDelayPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getWarningDelay());
                return new ClientboundSetBorderWarningDelayPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SetBorderWarningDistancePacket setBorderWarningDistancePacket(int warningDistance) {
        return new SetBorderWarningDistancePacket(warningDistance) {
            @Nonnull
            @Override
            public ClientboundSetBorderWarningDistancePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getWarningDistance());
                return new ClientboundSetBorderWarningDistancePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public SelectAdvancementsTabPacket selectAdvancementsTabPacket(@Nullable NamespacedKey tab) {
        return new SelectAdvancementsTabPacket(tab) {
            @Nonnull
            @Override
            public ClientboundSelectAdvancementsTabPacket build() {
                return new ClientboundSelectAdvancementsTabPacket(nullable(tab));
            }
        };
    }

    @Nonnull
    @Override
    public HorseScreenOpenPacket horseScreenOpenPacket(int containerId, int size, int entityId) {
        return new HorseScreenOpenPacket(containerId, size, entityId) {
            @Nonnull
            @Override
            public ClientboundHorseScreenOpenPacket build() {
                return new ClientboundHorseScreenOpenPacket(getContainerId(), getSize(), getEntityId());
            }
        };
    }

    @Nonnull
    @Override
    public CommandSuggestionsPacket commandSuggestionsPacket(int completionId, @Nonnull CommandSuggestionsPacket.Suggestions suggestions) {
        return new CommandSuggestionsPacket(completionId, suggestions) {
            @Nonnull
            @Override
            public ClientboundCommandSuggestionsPacket build() {
                return new ClientboundCommandSuggestionsPacket(getCompletionId(), wrap(getSuggestions()));
            }
        };
    }

    @Nonnull
    @Override
    public SetDisplayChatPreviewPacket setDisplayChatPreviewPacket(boolean enabled) {
        return new SetDisplayChatPreviewPacket(enabled) {
            @Nonnull
            @Override
            public ClientboundSetDisplayChatPreviewPacket build() {
                return new ClientboundSetDisplayChatPreviewPacket(isEnabled());
            }
        };
    }

    @Nonnull
    @Override
    public ResourcePackPacket resourcePackPacket(@Nonnull String url, @Nullable String hash, @Nullable String prompt, boolean required) {
        return new ResourcePackPacket(url, hash, prompt, required) {
            @Nonnull
            @Override
            public ClientboundResourcePackPacket build() {
                return new ClientboundResourcePackPacket(getUrl(), String.valueOf(getHash()), isRequired(), getPrompt() != null ? Component.literal(getPrompt()) : null);
            }
        };
    }

    @Nonnull
    @Override
    public SetPlayerTeamPacket setPlayerTeamPacket(@Nonnull String name, @Nonnull SetPlayerTeamPacket.Option option, @Nullable SetPlayerTeamPacket.Parameters parameters, @Nonnull List<String> entries) {
        return new SetPlayerTeamPacket(name, option, parameters, entries) {
            @Nonnull
            @Override
            public ClientboundSetPlayerTeamPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeUtf(getName());
                buffer.writeByte(getOption().ordinal());
                if (getOption().needsParameters()) {
                    if (getParameters() == null) throw new IllegalStateException("parameters not present but required");
                    buffer.writeComponent(Component.literal(getParameters().getDisplayName()));
                    buffer.writeByte(getParameters().packOptions());
                    buffer.writeUtf(switch (getParameters().getNameTagVisibility()) {
                        case ALWAYS -> "always";
                        case NEVER -> "never";
                        case HIDE_FOR_OTHER_TEAMS -> "hideForOtherTeams";
                        case HIDE_FOR_OWN_TEAM -> "hideForOwnTeam";
                    });
                    buffer.writeUtf(switch (getParameters().getCollisionRule()) {
                        case ALWAYS -> "always";
                        case NEVER -> "never";
                        case PUSH_OTHER_TEAMS -> "pushOtherTeams";
                        case PUSH_OWN_TEAM -> "pushOwnTeam";
                    });
                    buffer.writeEnum(wrap(getParameters().getColor()));
                    buffer.writeComponent(Component.literal(getParameters().getPrefix()));
                    buffer.writeComponent(Component.literal(getParameters().getSuffix()));
                }
                if (getOption().needsEntries()) buffer.writeCollection(getEntries(), FriendlyByteBuf::writeUtf);
                return new ClientboundSetPlayerTeamPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public TagQueryPacket tagQueryPacket(int transactionId, @Nullable NBTTag tag) {
        return new TagQueryPacket(transactionId, tag) {
            @Nonnull
            @Override
            public ClientboundTagQueryPacket build() {
                return new ClientboundTagQueryPacket(getTransactionId(), getTag() != null ? getTag().versioned() : null);
            }
        };
    }

    @Nonnull
    @Override
    public SetChunkCacheRadiusPacket setChunkCacheRadiusPacket(int radius) {
        return new SetChunkCacheRadiusPacket(radius) {
            @Nonnull
            @Override
            public ClientboundSetChunkCacheRadiusPacket build() {
                return new ClientboundSetChunkCacheRadiusPacket(getRadius());
            }
        };
    }

    @Nonnull
    @Override
    public RotateHeadPacket rotateHeadPacket(int entityId, byte yaw) {
        return new RotateHeadPacket(entityId, yaw) {
            @Nonnull
            @Override
            public ClientboundRotateHeadPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(getYaw());
                return new ClientboundRotateHeadPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public TakeItemEntityPacket takeItemEntityPacket(int entityId, int playerId, int amount) {
        return new TakeItemEntityPacket(entityId, playerId, amount) {
            @Nonnull
            @Override
            public ClientboundTakeItemEntityPacket build() {
                return new ClientboundTakeItemEntityPacket(getEntityId(), getPlayerId(), getAmount());
            }
        };
    }

    @Nonnull
    @Override
    public SetChunkCacheCenterPacket setChunkCacheCenterPacket(int x, int z) {
        return new SetChunkCacheCenterPacket(x, z) {
            @Nonnull
            @Override
            public ClientboundSetChunkCacheCenterPacket build() {
                return new ClientboundSetChunkCacheCenterPacket(getX(), getZ());
            }
        };
    }

    @Nonnull
    @Override
    public <P> PacketBuilder map(@Nonnull P packet) {
        if (packet instanceof ClientboundInitializeBorderPacket instance) {
            VirtualBorder border = new VirtualBorder(new VirtualBorder.Center(instance.getNewCenterX(), instance.getNewCenterZ()));
            border.setOldSize(instance.getOldSize());
            border.setNewSize(instance.getNewSize());
            border.setLerpTime(instance.getLerpTime());
            border.setWarningDistance(instance.getWarningBlocks());
            border.setWarningDelay(instance.getWarningTime());
            return InitializeBorderPacket.create(border);
        } else if (packet instanceof ClientboundAnimatePacket instance) {
            return AnimationPacket.create(instance.getId(), AnimationPacket.Animation.values()[instance.getAction()]);
        } else if (packet instanceof ClientboundSetExperiencePacket instance) {
            return SetExperiencePacket.create(instance.getExperienceProgress(), instance.getTotalExperience(), instance.getExperienceLevel());
        } else if (packet instanceof ClientboundCommandSuggestionsPacket instance) {
            return CommandSuggestionsPacket.create(instance.getId(), wrap(instance.getSuggestions()));
        } else if (packet instanceof ClientboundSelectAdvancementsTabPacket instance) {
            return SelectAdvancementsTabPacket.create(nullable(instance.getTab()));
        } else if (packet instanceof ClientboundSetDisplayChatPreviewPacket instance) {
            return SetDisplayChatPreviewPacket.create(instance.enabled());
        } else if (packet instanceof ClientboundHorseScreenOpenPacket instance) {
            return HorseScreenOpenPacket.create(instance.getContainerId(), instance.getSize(), instance.getEntityId());
        } else if (packet instanceof ClientboundMoveVehiclePacket instance) {
            return MoveVehiclePacket.create(new Position(instance.getX(), instance.getY(), instance.getZ(), instance.getYRot(), instance.getXRot()));
        } else if (packet instanceof ClientboundSetCameraPacket instance) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            instance.write(buffer);
            return CameraPacket.create(buffer.readVarInt());
        } else if (packet instanceof ClientboundGameEventPacket instance) {
            return GameStateChangePacket.create(wrap(instance.getEvent()), instance.getParam());
        } else if (packet instanceof ClientboundStopSoundPacket instance) {
            return StopSoundPacket.create(nullable(instance.getName()), nullable(instance.getSource()));
        } else if (packet instanceof ClientboundOpenBookPacket instance) {
            return OpenBookPacket.create(wrap(instance.getHand()));
        } else if (packet instanceof ClientboundLightUpdatePacket instance) {
        } else if (packet instanceof ClientboundSetCarriedItemPacket instance) {
            return SetCarriedItemPacket.create(instance.getSlot());
        } else if (packet instanceof ClientboundSetDisplayObjectivePacket instance) {
            return SetDisplayObjectivePacket.create(instance.getSlot(), instance.getObjectiveName());
        } else if (packet instanceof ClientboundSetTimePacket instance) {
            return UpdateTimePacket.create(instance.getGameTime(), instance.getDayTime(), instance.getDayTime() < 0);
        } else if (packet instanceof ClientboundContainerSetContentPacket instance) {
            return ContainerSetContentPacket.create(instance.getContainerId(), instance.getStateId(), wrap(instance.getItems(), 0), wrap(instance.getCarriedItem()));
        } else if (packet instanceof ClientboundSetPlayerTeamPacket instance) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            instance.write(buffer);
            String name = buffer.readUtf();
            SetPlayerTeamPacket.Option option = SetPlayerTeamPacket.Option.values()[buffer.readByte()];
            SetPlayerTeamPacket.Parameters parameters = null;
            if (option.needsParameters()) {
                parameters = wrap(new ClientboundSetPlayerTeamPacket.Parameters(buffer));
            }
            List<String> entries = new ArrayList<>();
            if (option.needsEntries()) entries = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
            return SetPlayerTeamPacket.create(name, option, parameters, entries);
        } else if (packet instanceof ClientboundUpdateTagsPacket instance) {
        } else if (packet instanceof ClientboundSetSimulationDistancePacket instance) {
            return SetSimulationDistancePacket.create(instance.simulationDistance());
        } else if (packet instanceof ClientboundChatPreviewPacket instance) {
            return ChatPreviewPacket.create(instance.queryId(), instance.preview() != null ? instance.preview().getString() : null);
        } else if (packet instanceof ClientboundLevelChunkPacketData instance) {
        } else if (packet instanceof ClientboundTagQueryPacket instance) {
            return TagQueryPacket.create(instance.getTransactionId(), nullable(instance.getTag()));
        } else if (packet instanceof ClientboundSetChunkCacheRadiusPacket instance) {
            return SetChunkCacheRadiusPacket.create(instance.getRadius());
        } else if (packet instanceof ClientboundRotateHeadPacket instance) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            instance.write(buffer);
            return RotateHeadPacket.create(buffer.readVarInt(), buffer.readByte());
        } else if (packet instanceof ClientboundLoginPacket instance) {
        } else if (packet instanceof ClientboundLightUpdatePacketData instance) {
        } else if (packet instanceof ClientboundTakeItemEntityPacket instance) {
            return TakeItemEntityPacket.create(instance.getItemId(), instance.getPlayerId(), instance.getAmount());
        } else if (packet instanceof ClientboundSetChunkCacheCenterPacket instance) {
            return SetChunkCacheCenterPacket.create(instance.getX(), instance.getZ());
        } else if (packet instanceof ClientboundCustomPayloadPacket instance) {
        } else if (packet instanceof ClientboundSectionBlocksUpdatePacket instance) {
        } else if (packet instanceof ClientboundBlockDestructionPacket instance) {
            return BlockDestructionPacket.create(instance.getId(), wrap(instance.getPos()), instance.getProgress());
        } else if (packet instanceof ClientboundUpdateRecipesPacket instance) {
        } else if (packet instanceof ClientboundDisconnectPacket instance) {
        } else if (packet instanceof ClientboundSoundEntityPacket instance) {
        } else if (packet instanceof ClientboundPingPacket instance) {
        } else if (packet instanceof ClientboundPlayerChatHeaderPacket instance) {
        } else if (packet instanceof ClientboundSetEntityDataPacket instance) {
        } else if (packet instanceof ClientboundOpenSignEditorPacket instance) {
        } else if (packet instanceof ClientboundBlockChangedAckPacket instance) {
        } else if (packet instanceof ClientboundSetBorderCenterPacket instance) {
            return SetBorderCenterPacket.create(new VirtualBorder.Center(instance.getNewCenterX(), instance.getNewCenterZ()));
        } else if (packet instanceof ClientboundAddExperienceOrbPacket instance) {
        } else if (packet instanceof ClientboundMerchantOffersPacket instance) {
        } else if (packet instanceof ClientboundRemoveEntitiesPacket instance) {
        } else if (packet instanceof ClientboundSetBorderWarningDistancePacket instance) {
            return SetBorderWarningDistancePacket.create(instance.getWarningBlocks());
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
            return SetBorderSizePacket.create(instance.getSize());
        } else if (packet instanceof ClientboundPlayerChatPacket instance) {
        } else if (packet instanceof ClientboundSetBorderWarningDelayPacket instance) {
            return SetBorderWarningDelayPacket.create(instance.getWarningDelay());
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
            return SetBorderLerpSizePacket.create(instance.getOldSize(), instance.getNewSize(), instance.getLerpTime());
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
