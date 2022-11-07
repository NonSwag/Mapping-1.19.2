package net.nonswag.tnl.mappings.v1_19_R1.api.packets.outgoing;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.phys.Vec3;
import net.nonswag.core.api.annotation.FieldsAreNonnullByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.border.VirtualBorder;
import net.nonswag.tnl.listener.api.item.SlotType;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.location.BlockPosition;
import net.nonswag.tnl.listener.api.location.Position;
import net.nonswag.tnl.listener.api.nbt.NBTTag;
import net.nonswag.tnl.listener.api.packets.outgoing.*;
import net.nonswag.tnl.listener.api.player.Hand;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_19_R1.boss.CraftBossBar;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.nullable;
import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.wrap;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class OutgoingPacketManager implements Outgoing {

    @Override
    public ChatPreviewPacket chatPreviewPacket(int queryId, @Nullable net.kyori.adventure.text.Component query) {
        return new ChatPreviewPacket(queryId, query) {
            @Override
            public ClientboundChatPreviewPacket build() {
                return new ClientboundChatPreviewPacket(getQueryId(), nullable(getQuery()));
            }
        };
    }

    @Override
    public SetSimulationDistancePacket setSimulationDistancePacket(int simulationDistance) {
        return new SetSimulationDistancePacket(simulationDistance) {
            @Override
            public ClientboundSetSimulationDistancePacket build() {
                return new ClientboundSetSimulationDistancePacket(getSimulationDistance());
            }
        };
    }

    @Override
    public SetCarriedItemPacket setCarriedItemPacket(int slot) {
        return new SetCarriedItemPacket(slot) {
            @Override
            public ClientboundSetCarriedItemPacket build() {
                return new ClientboundSetCarriedItemPacket(getSlot());
            }
        };
    }

    @Override
    public SetDisplayObjectivePacket setDisplayObjectivePacket(int slot, @Nullable String objectiveName) {
        return new SetDisplayObjectivePacket(slot, objectiveName) {
            @Override
            public ClientboundSetDisplayObjectivePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeByte(getSlot());
                buffer.writeUtf(getObjectiveName());
                return new ClientboundSetDisplayObjectivePacket(buffer);
            }
        };
    }

    @Override
    public BlockDestructionPacket blockDestructionPacket(int id, BlockPosition position, int state) {
        return new BlockDestructionPacket(id, position, state) {
            @Override
            public ClientboundBlockDestructionPacket build() {
                return new ClientboundBlockDestructionPacket(getId(), wrap(getPosition()), getState());
            }
        };
    }

    @Override
    public SetExperiencePacket setExperiencePacket(float experienceProgress, int totalExperience, int experienceLevel) {
        return new SetExperiencePacket(experienceProgress, totalExperience, experienceLevel) {
            @Override
            public ClientboundSetExperiencePacket build() {
                return new ClientboundSetExperiencePacket(getExperienceProgress(), getTotalExperience(), getExperienceLevel());
            }
        };
    }

    @Override
    public StopSoundPacket stopSoundPacket(@Nullable NamespacedKey sound, @Nullable SoundCategory category) {
        return new StopSoundPacket(sound, category) {
            @Override
            public ClientboundStopSoundPacket build() {
                return new ClientboundStopSoundPacket(nullable(getSound()), nullable(getCategory()));
            }
        };
    }

    @Override
    public BossEventPacket bossEventPacket(BossEventPacket.Action action, BossBar bossBar) {
        return new BossEventPacket(action, bossBar) {
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

    @Override
    public CameraPacket cameraPacket(int targetId) {
        return new CameraPacket(targetId) {
            @Override
            public ClientboundSetCameraPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getTargetId());
                return new ClientboundSetCameraPacket(buffer);
            }
        };
    }

    @Override
    public SystemChatPacket systemChatPacket(net.kyori.adventure.text.Component message, boolean overlay) {
        return new SystemChatPacket(message, overlay) {
            @Override
            public ClientboundSystemChatPacket build() {
                return new ClientboundSystemChatPacket(getMessage(), null, isOverlay());
            }
        };
    }

    @Override
    public ContainerClosePacket containerClosePacket(int windowId) {
        return new ContainerClosePacket(windowId) {
            @Override
            public ClientboundContainerClosePacket build() {
                return new ClientboundContainerClosePacket(getWindowId());
            }
        };
    }

    @Override
    public CooldownPacket cooldownPacket(Material item, int cooldown) {
        return new CooldownPacket(item, cooldown) {
            @Override
            public ClientboundCooldownPacket build() {
                return new ClientboundCooldownPacket(CraftMagicNumbers.getItem(getItem()), getCooldown());
            }
        };
    }

    @Override
    public CustomPayloadPacket customPayloadPacket(NamespacedKey channel, byte[]... bytes) {
        return new CustomPayloadPacket(channel, bytes) {
            @Override
            public ClientboundCustomPayloadPacket build() {
                return new ClientboundCustomPayloadPacket(wrap(getChannel()), new FriendlyByteBuf(Unpooled.wrappedBuffer(getBytes())));
            }
        };
    }

    @Override
    public AnimationPacket animationPacket(int entityId, AnimationPacket.Animation animation) {
        return new AnimationPacket(entityId, animation) {
            @Override
            public ClientboundAnimatePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(getAnimation().getId());
                return new ClientboundAnimatePacket(buffer);
            }
        };
    }

    @Override
    public EntityAttachPacket entityAttachPacket(int holderId, int leashedId) {
        return new EntityAttachPacket(holderId, leashedId) {
            @Override
            public ClientboundSetEntityLinkPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeInt(getLeashedId());
                buffer.writeInt(getHolderId());
                return new ClientboundSetEntityLinkPacket(buffer);
            }
        };
    }

    @Override
    public RemoveEntitiesPacket removeEntitiesPacket(int... entityIds) {
        return new RemoveEntitiesPacket(entityIds) {
            @Override
            public ClientboundRemoveEntitiesPacket build() {
                return new ClientboundRemoveEntitiesPacket(getEntityIds());
            }
        };
    }

    @Override
    public EntityEquipmentPacket entityEquipmentPacket(int entityId, HashMap<SlotType, TNLItem> equipment) {
        return new EntityEquipmentPacket(entityId, equipment) {
            @Override
            public ClientboundSetEquipmentPacket build() {
                List<Pair<EquipmentSlot, net.minecraft.world.item.ItemStack>> equipment = new ArrayList<>();
                getEquipment().forEach((slot, itemStack) -> equipment.add(new Pair<>(wrap(slot), CraftItemStack.asNMSCopy(itemStack))));
                return new ClientboundSetEquipmentPacket(getEntityId(), equipment);
            }
        };
    }

    @Override
    public GameStateChangePacket gameStateChangePacket(GameStateChangePacket.Identifier identifier, float state) {
        return new GameStateChangePacket(identifier, state) {
            @Override
            public ClientboundGameEventPacket build() {
                return new ClientboundGameEventPacket(wrap(getIdentifier()), getState());
            }
        };
    }

    @Override
    public EntityStatusPacket entityStatusPacket(int entityId, EntityStatusPacket.Status status) {
        return new EntityStatusPacket(entityId, status) {
            @Override
            public ClientboundEntityEventPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeInt(getEntityId());
                buffer.writeByte(getStatus().getId());
                return new ClientboundEntityEventPacket(buffer);
            }
        };
    }

    @Override
    public AddEntityPacket addEntityPacket(int entityId, UUID uniqueId, Position position, EntityType entityType, int entityData, Vector velocity, double headYaw) {
        return new AddEntityPacket(entityId, uniqueId, position, entityType, entityData, velocity, headYaw) {
            @Override
            public ClientboundAddEntityPacket build() {
                return new ClientboundAddEntityPacket(getEntityId(), getUniqueId(), getPosition().getX(), getPosition().getY(),
                        getPosition().getZ(), getPosition().getPitch(), getPosition().getYaw(), wrap(getEntityType()),
                        getEntityData(), new Vec3(getVelocity().getX(), getVelocity().getY(), getVelocity().getZ()), getHeadYaw());
            }
        };
    }

    @Override
    public <W> EntityMetadataPacket<W> entityMetadataPacket(int entityId, W dataWatcher, boolean updateAll) {
        return new EntityMetadataPacket<>(entityId, dataWatcher, updateAll) {
            @Override
            public ClientboundSetEntityDataPacket build() {
                return new ClientboundSetEntityDataPacket(getEntityId(), (SynchedEntityData) getMetadata(), isUpdateAll());
            }
        };
    }

    @Override
    public <W> EntityMetadataPacket<W> entityMetadataPacket(Entity entity, boolean updateAll) {
        return entityMetadataPacket(entity.getEntityId(), (W) ((CraftEntity) entity).getHandle().getEntityData(), updateAll);
    }

    @Override
    public EntityHeadRotationPacket entityHeadRotationPacket(int entityId, float yaw) {
        return new EntityHeadRotationPacket(entityId, yaw) {
            @Override
            public ClientboundRotateHeadPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(Mth.floor(getYaw() * 256f / 360f));
                return new ClientboundRotateHeadPacket(buffer);
            }
        };
    }

    @Override
    public EntityBodyRotationPacket entityBodyRotationPacket(int entityId, float rotation) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TeleportEntityPacket teleportEntityPacket(int entityId, Position position, boolean onGround) {
        return new TeleportEntityPacket(entityId, position, onGround) {
            @Override
            public ClientboundTeleportEntityPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeDouble(getPosition().getX());
                buffer.writeDouble(getPosition().getY());
                buffer.writeDouble(getPosition().getZ());
                buffer.writeByte(Mth.floor(getPosition().getYaw() * 256.0F / 360.0F));
                buffer.writeByte(Mth.floor(getPosition().getPitch() * 256.0F / 360.0F));
                buffer.writeBoolean(isOnGround());
                return new ClientboundTeleportEntityPacket(buffer);
            }
        };
    }

    @Override
    public SetEntityMotionPacket setEntityMotionPacket(int entityId, Vector velocity) {
        return new SetEntityMotionPacket(entityId, velocity) {
            @Override
            public ClientboundSetEntityMotionPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeShort(getVelocity().getBlockX());
                buffer.writeShort(getVelocity().getBlockY());
                buffer.writeShort(getVelocity().getBlockZ());
                return new ClientboundSetEntityMotionPacket(buffer);
            }
        };
    }

    @Override
    public LivingEntitySpawnPacket livingEntitySpawnPacket(LivingEntity entity) {
        return new LivingEntitySpawnPacket(entity) {
            @Override
            public ClientboundAddEntityPacket build() {
                return new ClientboundAddEntityPacket(((CraftLivingEntity) getEntity()).getHandle());
            }
        };
    }

    @Override
    public MapChunkPacket mapChunkPacket(Chunk chunk, int section) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SetPassengersPacket setPassengersPacket(int holderId, int[] passengers) {
        return new SetPassengersPacket(holderId, passengers) {
            @Override
            public ClientboundSetPassengersPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getHolderId());
                buffer.writeVarIntArray(getPassengers());
                return new ClientboundSetPassengersPacket(buffer);
            }
        };
    }

    @Override
    public NamedEntitySpawnPacket namedEntitySpawnPacket(HumanEntity human) {
        return new NamedEntitySpawnPacket(human) {
            @Override
            public ClientboundAddPlayerPacket build() {
                return new ClientboundAddPlayerPacket(((CraftHumanEntity) getHuman()).getHandle());
            }
        };
    }

    @Override
    public OpenSignEditorPacket openSignEditorPacket(BlockPosition position) {
        return new OpenSignEditorPacket(position) {
            @Override
            public ClientboundOpenSignEditorPacket build() {
                return new ClientboundOpenSignEditorPacket(wrap(getPosition()));
            }
        };
    }

    @Override
    public OpenBookPacket openBookPacket(Hand hand) {
        return new OpenBookPacket(hand) {
            @Override
            public ClientboundOpenBookPacket build() {
                return new ClientboundOpenBookPacket(wrap(getHand()));
            }
        };
    }

    @Override
    public MoveVehiclePacket moveVehiclePacket(Position position) {
        return new MoveVehiclePacket(position) {
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

    @Override
    public OpenScreenPacket openScreenPacket(int containerId, OpenScreenPacket.Type type, net.kyori.adventure.text.Component title) {
        return new OpenScreenPacket(containerId, type, title) {
            @Override
            public ClientboundOpenScreenPacket build() {
                return new ClientboundOpenScreenPacket(getContainerId(), wrap(getType()), wrap(getTitle()));
            }
        };
    }

    @Override
    public PlayerInfoPacket playerInfoPacket(Player player, PlayerInfoPacket.Action action) {
        return new PlayerInfoPacket(player, action) {
            @Override
            public ClientboundPlayerInfoPacket build() {
                return new ClientboundPlayerInfoPacket(wrap(getAction()), ((CraftPlayer) getPlayer()).getHandle());
            }
        };
    }

    @Override
    public ContainerSetSlotPacket containerSetSlotPacket(int containerId, int stateId, int slot, @Nullable ItemStack itemStack) {
        return new ContainerSetSlotPacket(containerId, stateId, slot, itemStack) {
            @Override
            public ClientboundContainerSetSlotPacket build() {
                return new ClientboundContainerSetSlotPacket(getContainerId(), getStateId(), getSlot(), CraftItemStack.asNMSCopy(getItemStack()));
            }
        };
    }

    @Override
    public UpdateTimePacket updateTimePacket(long age, long timestamp, boolean cycle) {
        return new UpdateTimePacket(age, timestamp, cycle) {
            @Override
            public ClientboundSetTimePacket build() {
                return new ClientboundSetTimePacket(getAge(), getTimestamp(), isCycle());
            }
        };
    }

    @Override
    public ContainerSetDataPacket containerSetDataPacket(int containerId, int propertyId, int value) {
        return new ContainerSetDataPacket(containerId, propertyId, value) {
            @Override
            public ClientboundContainerSetDataPacket build() {
                return new ClientboundContainerSetDataPacket(getContainerId(), getPropertyId(), getValue());
            }
        };
    }

    @Override
    public ContainerSetContentPacket containerSetContentPacket(int containerId, int stateId, List<TNLItem> content, TNLItem cursor) {
        return new ContainerSetContentPacket(containerId, stateId, content, cursor) {
            @Override
            public ClientboundContainerSetContentPacket build() {
                NonNullList<net.minecraft.world.item.ItemStack> items = NonNullList.create();
                for (org.bukkit.inventory.ItemStack item : getContent()) items.add(CraftItemStack.asNMSCopy(item));
                return new ClientboundContainerSetContentPacket(getContainerId(), getStateId(), items, wrap(getCursor()));
            }
        };
    }

    @Override
    public InitializeBorderPacket initializeBorderPacket(VirtualBorder virtualBorder) {
        return new InitializeBorderPacket(virtualBorder) {
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

    @Override
    public SetBorderSizePacket setBorderSizePacket(double size) {
        return new SetBorderSizePacket(size) {
            @Override
            public ClientboundSetBorderSizePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getSize());
                return new ClientboundSetBorderSizePacket(buffer);
            }
        };
    }

    @Override
    public SetBorderLerpSizePacket setBorderLerpSizePacket(double oldSize, double newSize, long lerpTime) {
        return new SetBorderLerpSizePacket(oldSize, newSize, lerpTime) {
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

    @Override
    public SetBorderCenterPacket setBorderCenterPacket(VirtualBorder.Center center) {
        return new SetBorderCenterPacket(center) {
            @Override
            public ClientboundSetBorderCenterPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getCenter().x());
                buffer.writeDouble(getCenter().z());
                return new ClientboundSetBorderCenterPacket(buffer);
            }
        };
    }

    @Override
    public SetBorderWarningDelayPacket setBorderWarningDelayPacket(int warningDelay) {
        return new SetBorderWarningDelayPacket(warningDelay) {
            @Override
            public ClientboundSetBorderWarningDelayPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getWarningDelay());
                return new ClientboundSetBorderWarningDelayPacket(buffer);
            }
        };
    }

    @Override
    public SetBorderWarningDistancePacket setBorderWarningDistancePacket(int warningDistance) {
        return new SetBorderWarningDistancePacket(warningDistance) {
            @Override
            public ClientboundSetBorderWarningDistancePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getWarningDistance());
                return new ClientboundSetBorderWarningDistancePacket(buffer);
            }
        };
    }

    @Override
    public SelectAdvancementsTabPacket selectAdvancementsTabPacket(@Nullable NamespacedKey tab) {
        return new SelectAdvancementsTabPacket(tab) {
            @Override
            public ClientboundSelectAdvancementsTabPacket build() {
                return new ClientboundSelectAdvancementsTabPacket(nullable(tab));
            }
        };
    }

    @Override
    public HorseScreenOpenPacket horseScreenOpenPacket(int containerId, int size, int entityId) {
        return new HorseScreenOpenPacket(containerId, size, entityId) {
            @Override
            public ClientboundHorseScreenOpenPacket build() {
                return new ClientboundHorseScreenOpenPacket(getContainerId(), getSize(), getEntityId());
            }
        };
    }

    @Override
    public CommandSuggestionsPacket commandSuggestionsPacket(int completionId, CommandSuggestionsPacket.Suggestions suggestions) {
        return new CommandSuggestionsPacket(completionId, suggestions) {
            @Override
            public ClientboundCommandSuggestionsPacket build() {
                return new ClientboundCommandSuggestionsPacket(getCompletionId(), wrap(getSuggestions()));
            }
        };
    }

    @Override
    public SetDisplayChatPreviewPacket setDisplayChatPreviewPacket(boolean enabled) {
        return new SetDisplayChatPreviewPacket(enabled) {
            @Override
            public ClientboundSetDisplayChatPreviewPacket build() {
                return new ClientboundSetDisplayChatPreviewPacket(isEnabled());
            }
        };
    }

    @Override
    public ResourcePackPacket resourcePackPacket(String url, @Nullable String hash, @Nullable net.kyori.adventure.text.Component prompt, boolean required) {
        return new ResourcePackPacket(url, hash, prompt, required) {
            @Override
            public ClientboundResourcePackPacket build() {
                return new ClientboundResourcePackPacket(getUrl(), String.valueOf(getHash()), isRequired(), nullable(getPrompt()));
            }
        };
    }

    @Override
    public SetPlayerTeamPacket setPlayerTeamPacket(String name, SetPlayerTeamPacket.Option option, @Nullable SetPlayerTeamPacket.Parameters parameters, List<String> entries) {
        return new SetPlayerTeamPacket(name, option, parameters, entries) {
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

    @Override
    public TagQueryPacket tagQueryPacket(int transactionId, @Nullable NBTTag tag) {
        return new TagQueryPacket(transactionId, tag) {
            @Override
            public ClientboundTagQueryPacket build() {
                return new ClientboundTagQueryPacket(getTransactionId(), getTag() != null ? getTag().versioned() : null);
            }
        };
    }

    @Override
    public SetChunkCacheRadiusPacket setChunkCacheRadiusPacket(int radius) {
        return new SetChunkCacheRadiusPacket(radius) {
            @Override
            public ClientboundSetChunkCacheRadiusPacket build() {
                return new ClientboundSetChunkCacheRadiusPacket(getRadius());
            }
        };
    }

    @Override
    public RotateHeadPacket rotateHeadPacket(int entityId, byte yaw) {
        return new RotateHeadPacket(entityId, yaw) {
            @Override
            public ClientboundRotateHeadPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeByte(getYaw());
                return new ClientboundRotateHeadPacket(buffer);
            }
        };
    }

    @Override
    public TakeItemEntityPacket takeItemEntityPacket(int entityId, int playerId, int amount) {
        return new TakeItemEntityPacket(entityId, playerId, amount) {
            @Override
            public ClientboundTakeItemEntityPacket build() {
                return new ClientboundTakeItemEntityPacket(getEntityId(), getPlayerId(), getAmount());
            }
        };
    }

    @Override
    public SetChunkCacheCenterPacket setChunkCacheCenterPacket(int x, int z) {
        return new SetChunkCacheCenterPacket(x, z) {
            @Override
            public ClientboundSetChunkCacheCenterPacket build() {
                return new ClientboundSetChunkCacheCenterPacket(getX(), getZ());
            }
        };
    }

    @Override
    public ChangeDifficultyPacket changeDifficultyPacket(Difficulty difficulty, boolean locked) {
        return new ChangeDifficultyPacket(difficulty, locked) {
            @Override
            public ClientboundChangeDifficultyPacket build() {
                return new ClientboundChangeDifficultyPacket(wrap(getDifficulty()), isLocked());
            }
        };
    }

    @Override
    public KeepAlivePacket keepAlivePacket(long id) {
        return new KeepAlivePacket(id) {
            @Override
            public ClientboundKeepAlivePacket build() {
                return new ClientboundKeepAlivePacket(getId());
            }
        };
    }

    @Override
    public SetActionBarTextPacket setActionBarTextPacket(net.kyori.adventure.text.Component text) {
        return new SetActionBarTextPacket(text) {
            @Override
            public ClientboundSetActionBarTextPacket build() {
                return new ClientboundSetActionBarTextPacket(wrap(getText()));
            }
        };
    }

    @Override
    public DisconnectPacket disconnectPacket(net.kyori.adventure.text.Component reason) {
        return new DisconnectPacket(reason) {
            @Override
            public ClientboundDisconnectPacket build() {
                return new ClientboundDisconnectPacket(wrap(getReason()));
            }
        };
    }

    @Override
    public ForgetLevelChunkPacket forgetLevelChunkPacket(int x, int z) {
        return new ForgetLevelChunkPacket(x, z) {
            @Override
            public ClientboundForgetLevelChunkPacket build() {
                return new ClientboundForgetLevelChunkPacket(getX(), getZ());
            }
        };
    }

    @Override
    public TabListPacket tabListPacket(net.kyori.adventure.text.Component header, net.kyori.adventure.text.Component footer) {
        return new TabListPacket(header, footer) {
            @Override
            public ClientboundTabListPacket build() {
                ClientboundTabListPacket packet = new ClientboundTabListPacket(wrap(getHeader()), wrap(getFooter()));
                packet.adventure$header = getHeader();
                packet.adventure$footer = getFooter();
                return packet;
            }
        };
    }

    @Override
    public PingPacket pingPacket(int id) {
        return new PingPacket(id) {
            @Override
            public ClientboundPingPacket build() {
                return new ClientboundPingPacket(getId());
            }
        };
    }

    @Override
    public BlockChangedAckPacket blockChangedAckPacket(int sequence) {
        return new BlockChangedAckPacket(sequence) {
            @Override
            public ClientboundBlockChangedAckPacket build() {
                return new ClientboundBlockChangedAckPacket(getSequence());
            }
        };
    }

    @Override
    public TitlePacket.SetTitlesAnimation setTitlesAnimation(int timeIn, int timeStay, int timeOut) {
        return new TitlePacket.SetTitlesAnimation(timeIn, timeStay, timeOut) {
            @Override
            public ClientboundSetTitlesAnimationPacket build() {
                return new ClientboundSetTitlesAnimationPacket(getTimeIn(), getTimeStay(), getTimeOut());
            }
        };
    }

    @Override
    public TitlePacket.SetTitleText setTitleText(net.kyori.adventure.text.Component text) {
        return new TitlePacket.SetTitleText(text) {
            @Override
            public ClientboundSetTitleTextPacket build() {
                return new ClientboundSetTitleTextPacket(wrap(getText()));
            }
        };
    }

    @Override
    public TitlePacket.SetSubtitleText setSubtitleText(net.kyori.adventure.text.Component text) {
        return new TitlePacket.SetSubtitleText(text) {
            @Override
            public ClientboundSetSubtitleTextPacket build() {
                return new ClientboundSetSubtitleTextPacket(wrap(getText()));
            }
        };
    }

    @Override
    public TitlePacket.ClearTitles clearTitles(boolean resetTimes) {
        return new TitlePacket.ClearTitles(resetTimes) {
            @Override
            public ClientboundClearTitlesPacket build() {
                return new ClientboundClearTitlesPacket(isResetTimes());
            }
        };
    }

    @Override
    public SetEntityLinkPacket setEntityLinkPacket(int leashHolderId, int leashedEntityId) {
        return new SetEntityLinkPacket(leashHolderId, leashedEntityId) {
            @Override
            public ClientboundSetEntityLinkPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeInt(getLeashHolderId());
                buffer.writeInt(getLeashedEntityId());
                return new ClientboundSetEntityLinkPacket(buffer);
            }
        };
    }

    @Override
    public BlockEventPacket blockEventPacket(BlockPosition position, Material blockType, int type, int data) {
        return new BlockEventPacket(position, blockType, type, data) {
            @Override
            public ClientboundBlockEventPacket build() {
                return new ClientboundBlockEventPacket(wrap(getPosition()), CraftMagicNumbers.getBlock(getBlockType()), getType(), getData());
            }
        };
    }

    @Override
    public <P> PacketBuilder map(P packet) {
        Function<P, PacketBuilder> original = p -> new PacketBuilder() {
            @Override
            public P build() {
                return packet;
            }
        };
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
            return original.apply(packet);
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
            if (option.needsParameters()) parameters = wrap(new ClientboundSetPlayerTeamPacket.Parameters(buffer));
            List<String> entries = new ArrayList<>();
            if (option.needsEntries()) entries = buffer.readCollection(ArrayList::new, FriendlyByteBuf::readUtf);
            return SetPlayerTeamPacket.create(name, option, parameters, entries);
        } else if (packet instanceof ClientboundUpdateTagsPacket instance) {
        } else if (packet instanceof ClientboundSetSimulationDistancePacket instance) {
            return SetSimulationDistancePacket.create(instance.simulationDistance());
        } else if (packet instanceof ClientboundChatPreviewPacket instance) {
            return ChatPreviewPacket.create(instance.queryId(), nullable(instance.preview()));
        } else if (packet instanceof ClientboundLevelChunkPacketData instance) {
            return original.apply(packet);
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
            return original.apply(packet);
        } else if (packet instanceof ClientboundTakeItemEntityPacket instance) {
            return TakeItemEntityPacket.create(instance.getItemId(), instance.getPlayerId(), instance.getAmount());
        } else if (packet instanceof ClientboundSetChunkCacheCenterPacket instance) {
            return SetChunkCacheCenterPacket.create(instance.getX(), instance.getZ());
        } else if (packet instanceof ClientboundCustomPayloadPacket instance) {
            return CustomPayloadPacket.create(wrap(instance.getIdentifier()), instance.getData().array());
        } else if (packet instanceof ClientboundSectionBlocksUpdatePacket instance) {
        } else if (packet instanceof ClientboundBlockDestructionPacket instance) {
            return BlockDestructionPacket.create(instance.getId(), wrap(instance.getPos()), instance.getProgress());
        } else if (packet instanceof ClientboundUpdateRecipesPacket instance) {
        } else if (packet instanceof ClientboundDisconnectPacket instance) {
            return DisconnectPacket.create(wrap(instance.getReason()));
        } else if (packet instanceof ClientboundSoundEntityPacket instance) {
        } else if (packet instanceof ClientboundPingPacket instance) {
            return PingPacket.create(instance.getId());
        } else if (packet instanceof ClientboundPlayerChatHeaderPacket instance) {
        } else if (packet instanceof ClientboundSetEntityDataPacket instance) {
        } else if (packet instanceof ClientboundOpenSignEditorPacket instance) {
            return OpenSignEditorPacket.create(wrap(instance.getPos()));
        } else if (packet instanceof ClientboundBlockChangedAckPacket instance) {
            return BlockChangedAckPacket.create(instance.sequence());
        } else if (packet instanceof ClientboundSetBorderCenterPacket instance) {
            return SetBorderCenterPacket.create(new VirtualBorder.Center(instance.getNewCenterX(), instance.getNewCenterZ()));
        } else if (packet instanceof ClientboundAddExperienceOrbPacket instance) {
        } else if (packet instanceof ClientboundMerchantOffersPacket instance) {

        } else if (packet instanceof ClientboundRemoveEntitiesPacket instance) {
            return RemoveEntitiesPacket.create(instance.getEntityIds().toArray(new int[]{}));
        } else if (packet instanceof ClientboundSetBorderWarningDistancePacket instance) {
            return SetBorderWarningDistancePacket.create(instance.getWarningBlocks());
        } else if (packet instanceof ClientboundSetSubtitleTextPacket instance) {
            return TitlePacket.SetSubtitleText.create(wrap(instance.getText()));
        } else if (packet instanceof ClientboundBlockEntityDataPacket instance) {
        } else if (packet instanceof ClientboundUpdateAttributesPacket instance) {
        } else if (packet instanceof ClientboundExplodePacket instance) {
        } else if (packet instanceof ClientboundPlayerCombatEnterPacket instance) {
        } else if (packet instanceof ClientboundBlockEventPacket instance) {
            return BlockEventPacket.create(wrap(instance.getPos()), CraftMagicNumbers.getMaterial(instance.getBlock()), instance.getB0(), instance.getB1());
        } else if (packet instanceof ClientboundSetEntityLinkPacket instance) {
            FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
            instance.write(buffer);
            SetEntityLinkPacket.create(buffer.readInt(), buffer.readInt());
        } else if (packet instanceof ClientboundCommandsPacket instance) {
        } else if (packet instanceof ClientboundLevelParticlesPacket instance) {
        } else if (packet instanceof ClientboundPlayerCombatKillPacket instance) {
        } else if (packet instanceof ClientboundSetTitleTextPacket instance) {
            return TitlePacket.SetTitleText.create(wrap(instance.getText()));
        } else if (packet instanceof ClientboundSoundPacket instance) {
        } else if (packet instanceof ClientboundContainerSetSlotPacket instance) {
            return ContainerSetSlotPacket.create(instance.getContainerId(), instance.getSlot(), wrap(instance.getItem()));
        } else if (packet instanceof ClientboundRecipePacket instance) {
        } else if (packet instanceof ClientboundPlaceGhostRecipePacket instance) {
        } else if (packet instanceof ClientboundBlockUpdatePacket instance) {
        } else if (packet instanceof ClientboundSetDefaultSpawnPositionPacket instance) {
        } else if (packet instanceof ClientboundOpenScreenPacket instance) {
            assert instance.getType() != null : "The screen type cannot be null";
            return OpenScreenPacket.create(instance.getContainerId(), wrap(instance.getType()), wrap(instance.getTitle()));
        } else if (packet instanceof ClientboundSetEquipmentPacket instance) {
        } else if (packet instanceof ClientboundSetTitlesAnimationPacket instance) {
            return TitlePacket.SetTitlesAnimation.create(instance.getFadeIn(), instance.getStay(), instance.getFadeOut());
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
            return ContainerSetDataPacket.create(instance.getContainerId(), instance.getId(), instance.getValue());
        } else if (packet instanceof ClientboundSetEntityMotionPacket instance) {
            Vector velocity = new Vector(instance.getXa(), instance.getYa(), instance.getZa());
            return SetEntityMotionPacket.create(instance.getId(), velocity);
        } else if (packet instanceof ClientboundSetBorderSizePacket instance) {
            return SetBorderSizePacket.create(instance.getSize());
        } else if (packet instanceof ClientboundPlayerChatPacket instance) {
        } else if (packet instanceof ClientboundSetBorderWarningDelayPacket instance) {
            return SetBorderWarningDelayPacket.create(instance.getWarningDelay());
        } else if (packet instanceof ClientboundTabListPacket instance) {
            @SuppressWarnings("ConstantConditions")
            var header = instance.adventure$header != null ? instance.adventure$header : wrap(instance.getHeader());
            @SuppressWarnings("ConstantConditions")
            var footer = instance.adventure$footer != null ? instance.adventure$footer : wrap(instance.getHeader());
            return TabListPacket.create(header, footer);
        } else if (packet instanceof ClientboundChangeDifficultyPacket instance) {
            return ChangeDifficultyPacket.create(wrap(instance.getDifficulty()), instance.isLocked());
        } else if (packet instanceof ClientboundKeepAlivePacket instance) {
            return KeepAlivePacket.create(instance.getId());
        } else if (packet instanceof ClientboundClearTitlesPacket instance) {
            return TitlePacket.ClearTitles.create(instance.shouldResetTimes());
        } else if (packet instanceof ClientboundSetActionBarTextPacket instance) {
            return SetActionBarTextPacket.create(nullable(instance.getText()));
        } else if (packet instanceof ClientboundMapItemDataPacket instance) {
        } else if (packet instanceof ClientboundForgetLevelChunkPacket instance) {
            return ForgetLevelChunkPacket.create(instance.getX(), instance.getZ());
        } else if (packet instanceof ClientboundPlayerAbilitiesPacket instance) {
        } else if (packet instanceof ClientboundResourcePackPacket instance) {
            return ResourcePackPacket.create(instance.getUrl(), instance.getHash(), nullable(instance.getPrompt()), instance.isRequired());
        } else if (packet instanceof ClientboundCooldownPacket instance) {
            return CooldownPacket.create(wrap(instance.getItem()), instance.getDuration());
        } else if (packet instanceof ClientboundContainerClosePacket instance) {
            return ContainerClosePacket.create(instance.getContainerId());
        } else if (packet instanceof ClientboundTeleportEntityPacket instance) {
            Position position = new Position(instance.getX(), instance.getY(), instance.getZ(), instance.getyRot(), instance.getxRot());
            return TeleportEntityPacket.create(instance.getId(), position, instance.isOnGround());
        } else if (packet instanceof ClientboundRespawnPacket instance) {
        } else if (packet instanceof ClientboundBossEventPacket instance) {
            // write to buffer and read values UUID, Enum OperationType,
        } else if (packet instanceof ClientboundSystemChatPacket instance) {
            if (instance.adventure$content() != null) {
                return SystemChatPacket.create(instance.adventure$content(), instance.overlay());
            } else if (instance.content() != null) {
                return SystemChatPacket.create(net.kyori.adventure.text.Component.text(instance.content()), instance.overlay());
            } else throw new IllegalArgumentException("Must supply either adventure component or string json content");
        } else if (packet instanceof ClientboundAddEntityPacket instance) {
            Position position = new Position(instance.getX(), instance.getY(), instance.getZ(), instance.getYRot(), instance.getXRot());
            return AddEntityPacket.create(instance.getId(), instance.getUUID(), position, wrap(instance.getType()),
                    instance.getData(), new Vector(instance.getXa(), instance.getYa(), instance.getZa()), instance.getYHeadRot());
        } else if (packet instanceof ClientboundLevelEventPacket instance) {
        } else if (packet instanceof ClientboundUpdateMobEffectPacket instance) {
        } else if (packet instanceof ClientboundSetBorderLerpSizePacket instance) {
            return SetBorderLerpSizePacket.create(instance.getOldSize(), instance.getNewSize(), instance.getLerpTime());
        } else if (packet instanceof ClientboundUpdateAdvancementsPacket instance) {
        } else if (packet instanceof ClientboundRemoveMobEffectPacket instance) {
        } else if (packet instanceof ClientboundSetHealthPacket instance) {
        } else if (packet instanceof ClientboundServerDataPacket instance) {
        } else if (packet instanceof ClientboundSetPassengersPacket instance) {
            return SetPassengersPacket.create(instance.getVehicle(), instance.getPassengers());
        } else if (packet instanceof ClientboundSetScorePacket instance) {
        } else if (packet instanceof ClientboundPlayerLookAtPacket instance) {
        } else if (packet instanceof ClientboundLevelChunkWithLightPacket instance) {
            return original.apply(packet);
        }
        if (!unmapped.contains(packet.getClass().getName())) {
            unmapped.add(packet.getClass().getName());
            Logger.warn.println("Unmapped outgoing (vanilla) packet: " + packet.getClass().getName());
        }
        return original.apply(packet);
    }

    private static final List<String> unmapped = new ArrayList<>();
}
