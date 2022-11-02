package net.nonswag.tnl.mappings.v1_19_R1.api.packets.incoming;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.tnl.listener.api.item.TNLItem;
import net.nonswag.tnl.listener.api.location.BlockPosition;
import net.nonswag.tnl.listener.api.location.Direction;
import net.nonswag.tnl.listener.api.location.Position;
import net.nonswag.tnl.listener.api.mapper.Mapping;
import net.nonswag.tnl.listener.api.packets.incoming.*;
import net.nonswag.tnl.listener.api.player.Hand;
import org.bukkit.Difficulty;
import org.bukkit.NamespacedKey;
import org.bukkit.Rotation;
import org.bukkit.block.structure.Mirror;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.nullable;
import static net.nonswag.tnl.mappings.v1_19_R1.api.wrapper.NMSHelper.wrap;

public class IncomingPacketManager implements Mapping.PacketManager.Incoming {

    @Nonnull
    @Override
    public AcceptTeleportationPacket acceptTeleportationPacket(int id) {
        return new AcceptTeleportationPacket(id) {
            @Nonnull
            @Override
            public ServerboundAcceptTeleportationPacket build() {
                return new ServerboundAcceptTeleportationPacket(getId());
            }
        };
    }

    @Nonnull
    @Override
    public BlockEntityTagQueryPacket blockEntityTagQueryPacket(int transactionId, @Nonnull BlockPosition position) {
        return new BlockEntityTagQueryPacket(transactionId, position) {
            @Nonnull
            @Override
            public ServerboundBlockEntityTagQuery build() {
                return new ServerboundBlockEntityTagQuery(getTransactionId(), wrap(getPosition()));
            }
        };
    }

    @Nonnull
    @Override
    public ChangeDifficultyPacket changeDifficultyPacket(@Nonnull Difficulty difficulty) {
        return new ChangeDifficultyPacket(difficulty) {
            @Nonnull
            @Override
            public ServerboundChangeDifficultyPacket build() {
                return new ServerboundChangeDifficultyPacket(switch (getDifficulty()) {
                    case PEACEFUL -> net.minecraft.world.Difficulty.PEACEFUL;
                    case EASY -> net.minecraft.world.Difficulty.EASY;
                    case NORMAL -> net.minecraft.world.Difficulty.NORMAL;
                    case HARD -> net.minecraft.world.Difficulty.HARD;
                });
            }
        };
    }

    @Nonnull
    @Override
    public ChatAckPacket chatAckPacket(@Nonnull net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update lastSeenMessages) {
        return new ChatAckPacket(lastSeenMessages) {
            @Nonnull
            @Override
            public ServerboundChatAckPacket build() {
                return new ServerboundChatAckPacket(wrap(getLastSeenMessages()));
            }
        };
    }

    @Nonnull
    @Override
    public ChatCommandPacket chatCommandPacket(@Nonnull String command, @Nonnull Instant timeStamp, long salt, @Nonnull ChatCommandPacket.Entry[] argumentSignatures, boolean signedPreview, @Nonnull net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update lastSeenMessages) {
        return new ChatCommandPacket(command, timeStamp, salt, argumentSignatures, signedPreview, lastSeenMessages) {
            @Nonnull
            @Override
            public ServerboundChatCommandPacket build() {
                return new ServerboundChatCommandPacket(getCommand(), getTimeStamp(), getSalt(), wrap(getArgumentSignatures()), isSignedPreview(), wrap(getLastSeenMessages()));
            }
        };
    }

    @Nonnull
    @Override
    public ChatPacket chatPacket(@Nonnull String message, @Nonnull Instant timeStamp, long salt, @Nonnull byte[] signature, boolean signedPreview, @Nonnull net.nonswag.tnl.listener.api.chat.LastSeenMessages.Update lastSeenMessages) {
        return new ChatPacket(message, timeStamp, salt, signature, signedPreview, lastSeenMessages) {
            @Nonnull
            @Override
            public ServerboundChatPacket build() {
                return new ServerboundChatPacket(getMessage(), getTimeStamp(), getSalt(), new MessageSignature(getSignature()), isSignedPreview(), wrap(getLastSeenMessages()));
            }
        };
    }

    @Nonnull
    @Override
    public ChatPreviewPacket chatPreviewPacket(int queryId, @Nonnull String query) {
        return new ChatPreviewPacket(queryId, query) {
            @Nonnull
            @Override
            public ServerboundChatPreviewPacket build() {
                return new ServerboundChatPreviewPacket(getQueryId(), getQuery());
            }
        };
    }

    @Nonnull
    @Override
    public ClientCommandPacket clientCommandPacket(@Nonnull ClientCommandPacket.Action action) {
        return new ClientCommandPacket(action) {
            @Nonnull
            @Override
            public ServerboundClientCommandPacket build() {
                return new ServerboundClientCommandPacket(switch (getAction()) {
                    case PERFORM_RESPAWN -> ServerboundClientCommandPacket.Action.PERFORM_RESPAWN;
                    case REQUEST_STATS -> ServerboundClientCommandPacket.Action.REQUEST_STATS;
                });
            }
        };
    }

    @Nonnull
    @Override
    public ClientInformationPacket clientInformationPacket(@Nonnull String language, int viewDistance, @Nonnull ClientInformationPacket.ChatVisibility chatVisibility, boolean chatColors, int modelCustomisation, @Nonnull ClientInformationPacket.HandSide mainHand, boolean textFiltering, boolean listingAllowed) {
        return new ClientInformationPacket(language, viewDistance, chatVisibility, chatColors, modelCustomisation, mainHand, textFiltering, listingAllowed) {
            @Nonnull
            @Override
            public ServerboundClientInformationPacket build() {
                return new ServerboundClientInformationPacket(getLanguage(), getViewDistance(), switch (getChatVisibility()) {
                    case FULL -> ChatVisiblity.FULL;
                    case SYSTEM -> ChatVisiblity.SYSTEM;
                    case HIDDEN -> ChatVisiblity.HIDDEN;
                }, isChatColors(), getModelCustomisation(), switch (getMainHand()) {
                    case LEFT -> HumanoidArm.LEFT;
                    case RIGHT -> HumanoidArm.RIGHT;
                }, isTextFiltering(), isListingAllowed());
            }
        };
    }

    @Nonnull
    @Override
    public CommandSuggestionPacket commandSuggestionPacket(int id, @Nonnull String partialCommand) {
        return new CommandSuggestionPacket(id, partialCommand) {
            @Nonnull
            @Override
            public ServerboundCommandSuggestionPacket build() {
                return new ServerboundCommandSuggestionPacket(getId(), getPartialCommand());
            }
        };
    }

    @Nonnull
    @Override
    public CustomPayloadPacket customPayloadPacket(@Nonnull NamespacedKey channel, @Nonnull byte[] data) {
        return new CustomPayloadPacket(channel, data) {
            @Nonnull
            @Override
            public ServerboundCustomPayloadPacket build() {
                return new ServerboundCustomPayloadPacket(wrap(channel), new FriendlyByteBuf(Unpooled.buffer()).writeByteArray(data));
            }
        };
    }

    @Nonnull
    @Override
    public EditBookPacket editBookPacket(@Nullable String title, @Nonnull List<String> pages, int slot) {
        return new EditBookPacket(title, pages, slot) {
            @Nonnull
            @Override
            public ServerboundEditBookPacket build() {
                return new ServerboundEditBookPacket(getSlot(), getPages(), Optional.ofNullable(getTitle()));
            }
        };
    }

    @Nonnull
    @Override
    public EntityTagQueryPacket entityTagQueryPacket(int transactionId, int entityId) {
        return new EntityTagQueryPacket(transactionId, entityId) {
            @Nonnull
            @Override
            public ServerboundEntityTagQuery build() {
                return new ServerboundEntityTagQuery(getTransactionId(), getEntityId());
            }
        };
    }

    @Nonnull
    @Override
    public InteractPacket.Attack attack(int entityId, boolean sneaking) {
        return new InteractPacket.Attack(entityId, sneaking) {
            @Nonnull
            @Override
            public ServerboundInteractPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeEnum(ServerboundInteractPacket.ActionType.ATTACK);
                buffer.writeBoolean(isSneaking());
                return new ServerboundInteractPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public InteractPacket.Interact interactPacket(int entityId, boolean sneaking, @Nonnull Hand hand) {
        return new InteractPacket.Interact(entityId, sneaking, hand) {
            @Nonnull
            @Override
            public ServerboundInteractPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeEnum(ServerboundInteractPacket.ActionType.INTERACT);
                buffer.writeEnum(wrap(getHand()));
                buffer.writeBoolean(isSneaking());
                return new ServerboundInteractPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public InteractPacket.InteractAt interactAtPacket(int entityId, boolean sneaking, @Nonnull Hand hand, @Nonnull Vector location) {
        return new InteractPacket.InteractAt(entityId, sneaking, hand, location) {
            @Nonnull
            @Override
            public ServerboundInteractPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeEnum(ServerboundInteractPacket.ActionType.INTERACT_AT);
                buffer.writeFloat((float) getLocation().getX());
                buffer.writeFloat((float) getLocation().getY());
                buffer.writeFloat((float) getLocation().getZ());
                buffer.writeEnum(wrap(getHand()));
                buffer.writeBoolean(isSneaking());
                return new ServerboundInteractPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public JigsawGeneratePacket jigsawGeneratePacket(@Nonnull BlockPosition position, int levels, boolean keepJigsaws) {
        return new JigsawGeneratePacket(position, levels, keepJigsaws) {
            @Nonnull
            @Override
            public ServerboundJigsawGeneratePacket build() {
                return new ServerboundJigsawGeneratePacket(wrap(getPosition()), getLevels(), isKeepJigsaws());
            }
        };
    }

    @Nonnull
    @Override
    public KeepAlivePacket keepAlivePacket(long id) {
        return new KeepAlivePacket(id) {
            @Nonnull
            @Override
            public ServerboundKeepAlivePacket build() {
                return new ServerboundKeepAlivePacket(getId());
            }
        };
    }

    @Nonnull
    @Override
    public LockDifficultyPacket lockDifficultyPacket(boolean locked) {
        return new LockDifficultyPacket(locked) {
            @Nonnull
            @Override
            public ServerboundLockDifficultyPacket build() {
                return new ServerboundLockDifficultyPacket(isLocked());
            }
        };
    }

    @Nonnull
    @Override
    public MovePlayerPacket.Position movePlayerPacket(double x, double y, double z, boolean onGround) {
        return new MovePlayerPacket.Position(x, y, z, onGround) {
            @Nonnull
            @Override
            public ServerboundMovePlayerPacket.Pos build() {
                return new ServerboundMovePlayerPacket.Pos(getX(), getY(), getZ(), isOnGround());
            }
        };
    }

    @Nonnull
    @Override
    public MovePlayerPacket.PositionRotation movePlayerPacket(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        return new MovePlayerPacket.PositionRotation(x, y, z, yaw, pitch, onGround) {
            @Nonnull
            @Override
            public ServerboundMovePlayerPacket.PosRot build() {
                return new ServerboundMovePlayerPacket.PosRot(getX(), getY(), getZ(), getYaw(), getPitch(), isOnGround());
            }
        };
    }

    @Nonnull
    @Override
    public MovePlayerPacket.Rotation movePlayerPacket(float yaw, float pitch, boolean onGround) {
        return new MovePlayerPacket.Rotation(yaw, pitch, onGround) {
            @Nonnull
            @Override
            public ServerboundMovePlayerPacket.Rot build() {
                return new ServerboundMovePlayerPacket.Rot(getYaw(), getPitch(), isOnGround());
            }
        };
    }

    @Nonnull
    @Override
    public MovePlayerPacket.Status movePlayerPacket(boolean onGround) {
        return new MovePlayerPacket.Status(onGround) {
            @Nonnull
            @Override
            public ServerboundMovePlayerPacket.StatusOnly build() {
                return new ServerboundMovePlayerPacket.StatusOnly(isOnGround());
            }
        };
    }

    @Nonnull
    @Override
    public MoveVehiclePacket moveVehiclePacket(@Nonnull Position position) {
        return new MoveVehiclePacket(position) {
            @Nonnull
            @Override
            public ServerboundMoveVehiclePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeDouble(getPosition().getX());
                buffer.writeDouble(getPosition().getY());
                buffer.writeDouble(getPosition().getZ());
                buffer.writeFloat(getPosition().getYaw());
                buffer.writeFloat(getPosition().getPitch());
                return new ServerboundMoveVehiclePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public PaddleBoatPacket paddleBoatPacket(boolean left, boolean right) {
        return new PaddleBoatPacket(left, right) {
            @Nonnull
            @Override
            public ServerboundPaddleBoatPacket build() {
                return new ServerboundPaddleBoatPacket(isLeft(), isRight());
            }
        };
    }

    @Nonnull
    @Override
    public PickItemPacket pickItemPacket(int slot) {
        return new PickItemPacket(slot) {
            @Nonnull
            @Override
            public ServerboundPickItemPacket build() {
                return new ServerboundPickItemPacket(getSlot());
            }
        };
    }

    @Nonnull
    @Override
    public PlaceRecipePacket placeRecipePacket(int containerId, @Nonnull NamespacedKey recipe, boolean shift) {
        return new PlaceRecipePacket(containerId, recipe, shift) {
            @Nonnull
            @Override
            public ServerboundPlaceRecipePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeByte(getContainerId());
                buffer.writeResourceLocation(wrap(getRecipe()));
                buffer.writeBoolean(isShift());
                return new ServerboundPlaceRecipePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public PlayerAbilitiesPacket playerAbilitiesPacket(boolean flying) {
        return new PlayerAbilitiesPacket(flying) {
            @Nonnull
            @Override
            public ServerboundPlayerAbilitiesPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeByte(isFlying() ? 2 : 0);
                return new ServerboundPlayerAbilitiesPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public PlayerActionPacket playerActionPacket(@Nonnull PlayerActionPacket.Action action, @Nonnull BlockPosition position, @Nonnull Direction direction, int sequence) {
        return new PlayerActionPacket(action, position, direction, sequence) {
            @Nonnull
            @Override
            public ServerboundPlayerActionPacket build() {
                return new ServerboundPlayerActionPacket(switch (getAction()) {
                    case START_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK;
                    case ABORT_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.ABORT_DESTROY_BLOCK;
                    case STOP_DESTROY_BLOCK -> ServerboundPlayerActionPacket.Action.STOP_DESTROY_BLOCK;
                    case DROP_ALL_ITEMS -> ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS;
                    case DROP_ITEM -> ServerboundPlayerActionPacket.Action.DROP_ITEM;
                    case RELEASE_USE_ITEM -> ServerboundPlayerActionPacket.Action.RELEASE_USE_ITEM;
                    case SWAP_ITEM_WITH_OFFHAND -> ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND;
                }, wrap(getPosition()), wrap(getDirection()), getSequence());
            }
        };
    }

    @Nonnull
    @Override
    public PlayerCommandPacket playerCommandPacket(int entityId, @Nonnull PlayerCommandPacket.Action action, int data) {
        return new PlayerCommandPacket(entityId, action, data) {
            @Nonnull
            @Override
            public ServerboundPlayerCommandPacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeVarInt(getEntityId());
                buffer.writeEnum(switch (getAction()) {
                    case PRESS_SHIFT_KEY -> ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY;
                    case RELEASE_SHIFT_KEY -> ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
                    case STOP_SLEEPING -> ServerboundPlayerCommandPacket.Action.STOP_SLEEPING;
                    case START_SPRINTING -> ServerboundPlayerCommandPacket.Action.START_SPRINTING;
                    case STOP_SPRINTING -> ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
                    case START_RIDING_JUMP -> ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP;
                    case STOP_RIDING_JUMP -> ServerboundPlayerCommandPacket.Action.STOP_RIDING_JUMP;
                    case OPEN_INVENTORY -> ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY;
                    case START_FALL_FLYING -> ServerboundPlayerCommandPacket.Action.START_FALL_FLYING;
                });
                buffer.writeVarInt(getData());
                return new ServerboundPlayerCommandPacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public PlayerInputPacket playerInputPacket(float sideways, float forward, boolean jumping, boolean sneaking) {
        return new PlayerInputPacket(sideways, forward, jumping, sneaking) {
            @Nonnull
            @Override
            public ServerboundPlayerInputPacket build() {
                return new ServerboundPlayerInputPacket(getSideways(), getForward(), isJumping(), isSneaking());
            }
        };
    }

    @Nonnull
    @Override
    public PongPacket pongPacket(int id) {
        return new PongPacket(id) {
            @Nonnull
            @Override
            public ServerboundPongPacket build() {
                return new ServerboundPongPacket(getId());
            }
        };
    }

    @Nonnull
    @Override
    public RecipeBookChangeSettingsPacket recipeBookChangeSettingsPacket(@Nonnull RecipeBookChangeSettingsPacket.RecipeBookType category, boolean guiOpen, boolean filteringCraftable) {
        return new RecipeBookChangeSettingsPacket(category, guiOpen, filteringCraftable) {
            @Nonnull
            @Override
            public ServerboundRecipeBookChangeSettingsPacket build() {
                return new ServerboundRecipeBookChangeSettingsPacket(switch (getCategory()) {
                    case CRAFTING -> net.minecraft.world.inventory.RecipeBookType.CRAFTING;
                    case FURNACE -> net.minecraft.world.inventory.RecipeBookType.FURNACE;
                    case BLAST_FURNACE -> net.minecraft.world.inventory.RecipeBookType.BLAST_FURNACE;
                    case SMOKER -> net.minecraft.world.inventory.RecipeBookType.SMOKER;
                }, isGuiOpen(), isFilteringCraftable());
            }
        };
    }

    @Nonnull
    @Override
    public RecipeBookSeenRecipePacket recipeBookSeenRecipePacket(@Nonnull NamespacedKey recipe) {
        return new RecipeBookSeenRecipePacket(recipe) {
            @Nonnull
            @Override
            public ServerboundRecipeBookSeenRecipePacket build() {
                FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                buffer.writeResourceLocation(wrap(getRecipe()));
                return new ServerboundRecipeBookSeenRecipePacket(buffer);
            }
        };
    }

    @Nonnull
    @Override
    public RenameItemPacket renameItemPacket(@Nonnull String name) {
        return new RenameItemPacket(name) {
            @Nonnull
            @Override
            public ServerboundRenameItemPacket build() {
                return new ServerboundRenameItemPacket(getName());
            }
        };
    }

    @Nonnull
    @Override
    public ResourcePackPacket resourcePackPacket(@Nonnull ResourcePackPacket.Action action) {
        return new ResourcePackPacket(action) {
            @Nonnull
            @Override
            public ServerboundResourcePackPacket build() {
                return new ServerboundResourcePackPacket(switch (getAction()) {
                    case SUCCESSFULLY_LOADED -> ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED;
                    case DECLINED -> ServerboundResourcePackPacket.Action.DECLINED;
                    case FAILED_DOWNLOAD -> ServerboundResourcePackPacket.Action.FAILED_DOWNLOAD;
                    case ACCEPTED -> ServerboundResourcePackPacket.Action.ACCEPTED;
                });
            }
        };
    }

    @Nonnull
    @Override
    public SeenAdvancementsPacket seenAdvancementsPacket(@Nonnull SeenAdvancementsPacket.Action action, @Nullable NamespacedKey tab) {
        return new SeenAdvancementsPacket(action, tab) {
            @Nonnull
            @Override
            public ServerboundSeenAdvancementsPacket build() {
                return new ServerboundSeenAdvancementsPacket(switch (getAction()) {
                    case OPENED_TAB -> ServerboundSeenAdvancementsPacket.Action.OPENED_TAB;
                    case CLOSED_SCREEN -> ServerboundSeenAdvancementsPacket.Action.CLOSED_SCREEN;
                }, nullable(getTab()));
            }
        };
    }

    @Nonnull
    @Override
    public SelectTradePacket selectTradePacket(int trade) {
        return new SelectTradePacket(trade) {
            @Nonnull
            @Override
            public ServerboundSelectTradePacket build() {
                return new ServerboundSelectTradePacket(getTrade());
            }
        };
    }

    @Nonnull
    @Override
    public SetBeaconPacket setBeaconPacket(@Nullable SetBeaconPacket.Effect primary, @Nullable SetBeaconPacket.Effect secondary) {
        return new SetBeaconPacket(primary, secondary) {
            @Nonnull
            @Override
            public ServerboundSetBeaconPacket build() {
                return new ServerboundSetBeaconPacket(Optional.ofNullable(nullable(getPrimary())), Optional.ofNullable(nullable(getSecondary())));
            }
        };
    }

    @Nonnull
    @Override
    public SetCarriedItemPacket setCarriedItemPacket(int slot) {
        return new SetCarriedItemPacket(slot) {
            @Nonnull
            @Override
            public ServerboundSetCarriedItemPacket build() {
                return new ServerboundSetCarriedItemPacket(getSlot());
            }
        };
    }

    @Nonnull
    @Override
    public SetCommandBlockPacket setCommandBlockPacket(@Nonnull BlockPosition position, @Nonnull String command, @Nonnull SetCommandBlockPacket.Mode mode, boolean trackOutput, boolean conditional, boolean alwaysActive) {
        return new SetCommandBlockPacket(position, command, mode, trackOutput, conditional, alwaysActive) {
            @Nonnull
            @Override
            public ServerboundSetCommandBlockPacket build() {
                return new ServerboundSetCommandBlockPacket(wrap(getPosition()), getCommand(), switch (mode) {
                    case SEQUENCE -> CommandBlockEntity.Mode.SEQUENCE;
                    case AUTO -> CommandBlockEntity.Mode.AUTO;
                    case REDSTONE -> CommandBlockEntity.Mode.REDSTONE;
                }, isTrackOutput(), isConditional(), isAlwaysActive());
            }
        };
    }

    @Nonnull
    @Override
    public SetCommandMinecartPacket setCommandMinecartPacket(int entityId, @Nonnull String command, boolean trackOutput) {
        return new SetCommandMinecartPacket(entityId, command, trackOutput) {
            @Nonnull
            @Override
            public ServerboundSetCommandMinecartPacket build() {
                return new ServerboundSetCommandMinecartPacket(getEntityId(), getCommand(), isTrackOutput());
            }
        };
    }

    @Nonnull
    @Override
    public SetCreativeModeSlotPacket setCreativeModeSlotPacket(int slot, @Nonnull TNLItem item) {
        return new SetCreativeModeSlotPacket(slot, item) {
            @Nonnull
            @Override
            public ServerboundSetCreativeModeSlotPacket build() {
                return new ServerboundSetCreativeModeSlotPacket(getSlot(), wrap(getItem()));
            }
        };
    }

    @Nonnull
    @Override
    public SetJigsawBlockPacket setJigsawBlockPacket(@Nonnull BlockPosition position, @Nonnull NamespacedKey name, @Nonnull NamespacedKey target, @Nonnull NamespacedKey pool, @Nonnull String finalState, @Nonnull SetJigsawBlockPacket.JointType joint) {
        return new SetJigsawBlockPacket(position, name, target, pool, finalState, joint) {
            @Nonnull
            @Override
            public ServerboundSetJigsawBlockPacket build() {
                return new ServerboundSetJigsawBlockPacket(wrap(getPosition()), wrap(getName()), wrap(getTarget()),
                        wrap(getPool()), getFinalState(), switch (getJoint()) {
                    case ROLLABLE -> JigsawBlockEntity.JointType.ROLLABLE;
                    case ALIGNED -> JigsawBlockEntity.JointType.ALIGNED;
                });
            }
        };
    }

    @Nonnull
    @Override
    public SetStructureBlockPacket setStructureBlockPacket(@Nonnull BlockPosition position, @Nonnull SetStructureBlockPacket.Type type, @Nonnull SetStructureBlockPacket.Mode mode, @Nonnull String name, @Nonnull BlockPosition offset, @Nonnull Vector size, @Nonnull Mirror mirror, @Nonnull Rotation rotation, @Nonnull String metadata, boolean ignoreEntities, boolean showAir, boolean showBoundingBox, float integrity, long seed) {
        return new SetStructureBlockPacket(position, type, mode, name, offset, size, mirror, rotation, metadata, ignoreEntities, showAir, showBoundingBox, integrity, seed) {
            @Nonnull
            @Override
            public ServerboundSetStructureBlockPacket build() {
                return new ServerboundSetStructureBlockPacket(wrap(getPosition()), switch (getType()) {
                    case UPDATE_DATA -> StructureBlockEntity.UpdateType.UPDATE_DATA;
                    case SAVE_AREA -> StructureBlockEntity.UpdateType.SAVE_AREA;
                    case LOAD_AREA -> StructureBlockEntity.UpdateType.LOAD_AREA;
                    case SCAN_AREA -> StructureBlockEntity.UpdateType.SCAN_AREA;
                }, switch (getMode()) {
                    case SAVE -> StructureMode.SAVE;
                    case LOAD -> StructureMode.LOAD;
                    case CORNER -> StructureMode.CORNER;
                    case DATA -> StructureMode.DATA;
                }, getName(), wrap(getOffset()), wrap(getSize()), switch (getMirror()) {
                    case NONE -> net.minecraft.world.level.block.Mirror.NONE;
                    case FRONT_BACK -> net.minecraft.world.level.block.Mirror.FRONT_BACK;
                    case LEFT_RIGHT -> net.minecraft.world.level.block.Mirror.LEFT_RIGHT;
                }, switch (getRotation()) {
                    case NONE -> net.minecraft.world.level.block.Rotation.NONE;
                    case CLOCKWISE -> net.minecraft.world.level.block.Rotation.CLOCKWISE_90;
                    case FLIPPED -> net.minecraft.world.level.block.Rotation.CLOCKWISE_180;
                    case COUNTER_CLOCKWISE -> net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90;
                    default -> throw new IllegalStateException("Unexpected value: " + getRotation());
                }, getMetadata(), isIgnoreEntities(), isShowAir(), isShowBoundingBox(), getIntegrity(), getSeed());
            }
        };
    }

    @Nonnull
    @Override
    public SignUpdatePacket signUpdatePacket(@Nonnull BlockPosition position, @Nonnull String[] lines) {
        return new SignUpdatePacket(position, lines) {
            @Nonnull
            @Override
            public ServerboundSignUpdatePacket build() {
                return new ServerboundSignUpdatePacket(wrap(getPosition()), getLines()[0], getLines()[1], getLines()[2], getLines()[3]);
            }
        };
    }

    @Nonnull
    @Override
    public SwingPacket swingPacket(@Nonnull Hand hand) {
        return new SwingPacket(hand) {
            @Nonnull
            @Override
            public ServerboundSwingPacket build() {
                return new ServerboundSwingPacket(wrap(getHand()));
            }
        };
    }

    @Nonnull
    @Override
    public TeleportToEntityPacket teleportToEntityPacket(@Nonnull UUID target) {
        return new TeleportToEntityPacket(target) {
            @Nonnull
            @Override
            public ServerboundTeleportToEntityPacket build() {
                return new ServerboundTeleportToEntityPacket(getTarget());
            }
        };
    }

    @Nonnull
    @Override
    public UseItemOnPacket useItemOnPacket(@Nonnull Hand hand, @Nonnull UseItemOnPacket.BlockTargetResult target, int sequence) {
        return new UseItemOnPacket(hand, target, sequence) {
            @Nonnull
            @Override
            public ServerboundUseItemOnPacket build() {
                return new ServerboundUseItemOnPacket(wrap(getHand()), wrap(getTarget()), getSequence());
            }
        };
    }

    @Nonnull
    @Override
    public UseItemPacket useItemPacket(@Nonnull Hand hand, int sequence) {
        return new UseItemPacket(hand, sequence) {
            @Nonnull
            @Override
            public ServerboundUseItemPacket build() {
                return new ServerboundUseItemPacket(wrap(getHand()), getSequence());
            }
        };
    }

    @Nonnull
    @Override
    public WindowButtonClickPacket windowButtonClickPacket(int containerId, int buttonId) {
        return new WindowButtonClickPacket(containerId, buttonId) {
            @Nonnull
            @Override
            public ServerboundContainerButtonClickPacket build() {
                return new ServerboundContainerButtonClickPacket(getContainerId(), getButtonId());
            }
        };
    }

    @Nonnull
    @Override
    public WindowClickPacket windowClickPacket(int containerId, int stateId, int slot, int buttonId, @Nonnull WindowClickPacket.ClickType clickType, @Nonnull TNLItem item, @Nonnull HashMap<Integer, TNLItem> changedSlots) {
        return new WindowClickPacket(containerId, stateId, slot, buttonId, clickType, item, changedSlots) {
            @Nonnull
            @Override
            public ServerboundContainerClickPacket build() {
                return new ServerboundContainerClickPacket(getContainerId(), getStateId(), getSlot(), getButtonId(), switch (getClickType()) {
                    case PICKUP -> net.minecraft.world.inventory.ClickType.PICKUP;
                    case QUICK_MOVE -> net.minecraft.world.inventory.ClickType.QUICK_MOVE;
                    case SWAP -> net.minecraft.world.inventory.ClickType.SWAP;
                    case CLONE -> net.minecraft.world.inventory.ClickType.CLONE;
                    case THROW -> net.minecraft.world.inventory.ClickType.THROW;
                    case QUICK_CRAFT -> net.minecraft.world.inventory.ClickType.QUICK_CRAFT;
                    case PICKUP_ALL -> net.minecraft.world.inventory.ClickType.PICKUP_ALL;
                }, wrap(getItem()), wrap(getChangedSlots()));
            }
        };
    }

    @Nonnull
    @Override
    public WindowClosePacket windowClosePacket(int containerId) {
        return new WindowClosePacket(containerId) {
            @Nonnull
            @Override
            public ServerboundContainerClosePacket build() {
                return new ServerboundContainerClosePacket(getContainerId());
            }
        };
    }

    @Nonnull
    @Override
    public <P> PacketBuilder map(@Nonnull P packet) {
        if (packet instanceof ServerboundContainerClosePacket instance) {
            return WindowClosePacket.create(instance.getContainerId());
        } else if (packet instanceof ServerboundResourcePackPacket instance) {
            return switch (instance.getAction()) {
                case ACCEPTED -> ResourcePackPacket.create(ResourcePackPacket.Action.ACCEPTED);
                case DECLINED -> ResourcePackPacket.create(ResourcePackPacket.Action.DECLINED);
                case FAILED_DOWNLOAD -> ResourcePackPacket.create(ResourcePackPacket.Action.FAILED_DOWNLOAD);
                case SUCCESSFULLY_LOADED -> ResourcePackPacket.create(ResourcePackPacket.Action.SUCCESSFULLY_LOADED);
            };
        } else if (packet instanceof ServerboundUseItemPacket instance) {
            return UseItemPacket.create(wrap(instance.getHand()), instance.getSequence());
        } else if (packet instanceof ServerboundTeleportToEntityPacket instance) {
            FriendlyByteBuf buffer;
            instance.write(buffer = new FriendlyByteBuf(Unpooled.buffer()));
            return TeleportToEntityPacket.create(buffer.readUUID());
        } else if (packet instanceof ServerboundSwingPacket instance) {
            return SwingPacket.create(wrap(instance.getHand()));
        } else if (packet instanceof ServerboundSetStructureBlockPacket instance) {
            return SetStructureBlockPacket.create(wrap(instance.getPos()), switch (instance.getUpdateType()) {
                case LOAD_AREA -> SetStructureBlockPacket.Type.LOAD_AREA;
                case SAVE_AREA -> SetStructureBlockPacket.Type.SAVE_AREA;
                case UPDATE_DATA -> SetStructureBlockPacket.Type.UPDATE_DATA;
                case SCAN_AREA -> SetStructureBlockPacket.Type.SCAN_AREA;
            }, switch (instance.getMode()) {
                case DATA -> SetStructureBlockPacket.Mode.DATA;
                case LOAD -> SetStructureBlockPacket.Mode.LOAD;
                case SAVE -> SetStructureBlockPacket.Mode.SAVE;
                case CORNER -> SetStructureBlockPacket.Mode.CORNER;
            }, instance.getName(), wrap(instance.getOffset()), wrap(instance.getSize()), switch (instance.getMirror()) {
                case NONE -> Mirror.NONE;
                case FRONT_BACK -> Mirror.FRONT_BACK;
                case LEFT_RIGHT -> Mirror.LEFT_RIGHT;
            }, switch (instance.getRotation()) {
                case NONE -> Rotation.NONE;
                case CLOCKWISE_90 -> Rotation.CLOCKWISE;
                case CLOCKWISE_180 -> Rotation.FLIPPED;
                case COUNTERCLOCKWISE_90 -> Rotation.COUNTER_CLOCKWISE;
            }, instance.getData(), instance.isIgnoreEntities(), instance.isShowAir(), instance.isShowBoundingBox(), instance.getIntegrity(), instance.getSeed());
        } else if (packet instanceof ServerboundSetJigsawBlockPacket instance) {
            return SetJigsawBlockPacket.create(wrap(instance.getPos()), wrap(instance.getName()), wrap(instance.getTarget()),
                    wrap(instance.getPool()), instance.getFinalState(), switch (instance.getJoint()) {
                case ROLLABLE -> SetJigsawBlockPacket.JointType.ROLLABLE;
                case ALIGNED -> SetJigsawBlockPacket.JointType.ALIGNED;
            });
        } else if (packet instanceof ServerboundSetCreativeModeSlotPacket instance) {
            return SetCreativeModeSlotPacket.create(instance.getSlotNum(), wrap(instance.getItem()));
        } else if (packet instanceof ServerboundSetCommandMinecartPacket instance) {
            FriendlyByteBuf buffer;
            instance.write(buffer = new FriendlyByteBuf(Unpooled.buffer()));
            return SetCommandMinecartPacket.create(buffer.readVarInt(), buffer.readUtf(), buffer.readBoolean());
        } else if (packet instanceof ServerboundSetCommandBlockPacket instance) {
            return SetCommandBlockPacket.create(wrap(instance.getPos()), instance.getCommand(), switch (instance.getMode()) {
                case AUTO -> SetCommandBlockPacket.Mode.AUTO;
                case REDSTONE -> SetCommandBlockPacket.Mode.REDSTONE;
                case SEQUENCE -> SetCommandBlockPacket.Mode.SEQUENCE;
            }, instance.isTrackOutput(), instance.isConditional(), instance.isAutomatic());
        } else if (packet instanceof ServerboundSetCarriedItemPacket instance) {
            return SetCarriedItemPacket.create(instance.getSlot());
        } else if (packet instanceof ServerboundSetBeaconPacket instance) {
            return SetBeaconPacket.create(wrap(instance.getPrimary()), wrap(instance.getSecondary()));
        } else if (packet instanceof ServerboundSelectTradePacket instance) {
            return SelectTradePacket.create(instance.getItem());
        } else if (packet instanceof ServerboundSeenAdvancementsPacket instance) {
            return SeenAdvancementsPacket.create(switch (instance.getAction()) {
                case OPENED_TAB -> SeenAdvancementsPacket.Action.OPENED_TAB;
                case CLOSED_SCREEN -> SeenAdvancementsPacket.Action.CLOSED_SCREEN;
            }, nullable(instance.getTab()));
        } else if (packet instanceof ServerboundRecipeBookSeenRecipePacket instance) {
            return RecipeBookSeenRecipePacket.create(wrap(instance.getRecipe()));
        } else if (packet instanceof ServerboundRecipeBookChangeSettingsPacket instance) {
            return RecipeBookChangeSettingsPacket.create(switch (instance.getBookType()) {
                case CRAFTING -> RecipeBookChangeSettingsPacket.RecipeBookType.CRAFTING;
                case FURNACE -> RecipeBookChangeSettingsPacket.RecipeBookType.FURNACE;
                case BLAST_FURNACE -> RecipeBookChangeSettingsPacket.RecipeBookType.BLAST_FURNACE;
                case SMOKER -> RecipeBookChangeSettingsPacket.RecipeBookType.SMOKER;
            }, instance.isOpen(), instance.isFiltering());
        } else if (packet instanceof ServerboundPongPacket instance) {
            return PongPacket.create(instance.getId());
        } else if (packet instanceof ServerboundPlayerCommandPacket instance) {
            return PlayerCommandPacket.create(instance.getId(), switch (instance.getAction()) {
                case PRESS_SHIFT_KEY -> PlayerCommandPacket.Action.PRESS_SHIFT_KEY;
                case RELEASE_SHIFT_KEY -> PlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
                case STOP_SLEEPING -> PlayerCommandPacket.Action.STOP_SLEEPING;
                case START_SPRINTING -> PlayerCommandPacket.Action.START_SPRINTING;
                case STOP_SPRINTING -> PlayerCommandPacket.Action.STOP_SPRINTING;
                case START_RIDING_JUMP -> PlayerCommandPacket.Action.START_RIDING_JUMP;
                case STOP_RIDING_JUMP -> PlayerCommandPacket.Action.STOP_RIDING_JUMP;
                case OPEN_INVENTORY -> PlayerCommandPacket.Action.OPEN_INVENTORY;
                case START_FALL_FLYING -> PlayerCommandPacket.Action.START_FALL_FLYING;
            }, instance.getData());
        } else if (packet instanceof ServerboundPlayerAbilitiesPacket instance) {
            return PlayerAbilitiesPacket.create(instance.isFlying());
        } else if (packet instanceof ServerboundPlaceRecipePacket instance) {
            return PlaceRecipePacket.create(instance.getContainerId(), wrap(instance.getRecipe()), instance.isShiftDown());
        } else if (packet instanceof ServerboundPaddleBoatPacket instance) {
            return PaddleBoatPacket.create(instance.getLeft(), instance.getRight());
        } else if (packet instanceof ServerboundMoveVehiclePacket instance) {
            return MoveVehiclePacket.create(new Position(instance.getX(), instance.getY(), instance.getZ(), instance.getYRot(), instance.getXRot()));
        } else if (packet instanceof ServerboundMovePlayerPacket instance) {
            if (instance.hasPosition() && instance.hasRotation()) {
                return MovePlayerPacket.PositionRotation.create(instance.x, instance.y, instance.z, instance.yRot, instance.xRot, instance.isOnGround());
            } else if (instance.hasRotation()) {
                return MovePlayerPacket.Rotation.create(instance.yRot, instance.xRot, instance.isOnGround());
            } else if (instance.hasPosition()) {
                return MovePlayerPacket.Position.create(instance.x, instance.y, instance.z, instance.isOnGround());
            } else return MovePlayerPacket.Status.create(instance.isOnGround());
        } else if (packet instanceof ServerboundLockDifficultyPacket instance) {
            return LockDifficultyPacket.create(instance.isLocked());
        } else if (packet instanceof ServerboundKeepAlivePacket instance) {
            return KeepAlivePacket.create(instance.getId());
        } else if (packet instanceof ServerboundJigsawGeneratePacket instance) {
            return JigsawGeneratePacket.create(wrap(instance.getPos()), instance.levels(), instance.keepJigsaws());
        } else if (packet instanceof ServerboundEntityTagQuery instance) {
            return EntityTagQueryPacket.create(instance.getTransactionId(), instance.getEntityId());
        } else if (packet instanceof ServerboundEditBookPacket instance) {
            return EditBookPacket.create(instance.getTitle().orElse(null), instance.getPages(), instance.getSlot());
        } else if (packet instanceof ServerboundContainerButtonClickPacket instance) {
            return WindowButtonClickPacket.create(instance.getContainerId(), instance.getButtonId());
        } else if (packet instanceof ServerboundClientInformationPacket instance) {
            return ClientInformationPacket.create(instance.language(), instance.viewDistance(), switch (instance.chatVisibility()) {
                case FULL -> ClientInformationPacket.ChatVisibility.FULL;
                case SYSTEM -> ClientInformationPacket.ChatVisibility.SYSTEM;
                case HIDDEN -> ClientInformationPacket.ChatVisibility.HIDDEN;
            }, instance.chatColors(), instance.modelCustomisation(), switch (instance.mainHand()) {
                case LEFT -> ClientInformationPacket.HandSide.LEFT;
                case RIGHT -> ClientInformationPacket.HandSide.RIGHT;
            }, instance.textFilteringEnabled(), instance.allowsListing());
        } else if (packet instanceof ServerboundChatPreviewPacket instance) {
            return ChatPreviewPacket.create(instance.queryId(), instance.query());
        } else if (packet instanceof ServerboundChatAckPacket instance) {
            return ChatAckPacket.create(wrap(instance.lastSeenMessages()));
        } else if (packet instanceof ServerboundChangeDifficultyPacket instance) {
            return ChangeDifficultyPacket.create(switch (instance.getDifficulty()) {
                case PEACEFUL -> Difficulty.PEACEFUL;
                case EASY -> Difficulty.EASY;
                case NORMAL -> Difficulty.NORMAL;
                case HARD -> Difficulty.HARD;
            });
        } else if (packet instanceof ServerboundBlockEntityTagQuery instance) {
            return BlockEntityTagQueryPacket.create(instance.getTransactionId(), wrap(instance.getPos()));
        } else if (packet instanceof ServerboundAcceptTeleportationPacket instance) {
            return AcceptTeleportationPacket.create(instance.getId());
        } else if (packet instanceof ServerboundChatPacket instance) {
            return ChatPacket.create(instance.message(), instance.timeStamp(), instance.salt(),
                    instance.signature().bytes(), instance.signedPreview(), wrap(instance.lastSeenMessages()));
        } else if (packet instanceof ServerboundChatCommandPacket instance) {
            return ChatCommandPacket.create(instance.command(), instance.timeStamp(), instance.salt(),
                    wrap(instance.argumentSignatures().entries()), instance.signedPreview(), wrap(instance.lastSeenMessages()));
        } else if (packet instanceof ServerboundClientCommandPacket instance) {
            return ClientCommandPacket.create(switch (instance.getAction()) {
                case PERFORM_RESPAWN -> ClientCommandPacket.Action.PERFORM_RESPAWN;
                case REQUEST_STATS -> ClientCommandPacket.Action.REQUEST_STATS;
            });
        } else if (packet instanceof ServerboundCustomPayloadPacket instance) {
            return CustomPayloadPacket.create(wrap(instance.getIdentifier()), instance.getData().readByteArray());
        } else if (packet instanceof ServerboundInteractPacket instance) {
            return switch (instance.getActionType()) {
                case ATTACK -> InteractPacket.Attack.create(instance.getEntityId(), instance.isUsingSecondaryAction());
                case INTERACT -> {
                    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                    instance.write(buffer);
                    int entityId = buffer.readVarInt();
                    ServerboundInteractPacket.ActionType type = buffer.readEnum(ServerboundInteractPacket.ActionType.class);
                    InteractionHand hand = buffer.readEnum(InteractionHand.class);
                    boolean sneaking = buffer.readBoolean();
                    yield InteractPacket.Interact.create(entityId, sneaking, wrap(hand));
                }
                case INTERACT_AT -> {
                    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
                    instance.write(buffer);
                    int entityId = buffer.readVarInt();
                    ServerboundInteractPacket.ActionType type = buffer.readEnum(ServerboundInteractPacket.ActionType.class);
                    Vector location = new Vector(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
                    InteractionHand hand = buffer.readEnum(InteractionHand.class);
                    boolean sneaking = buffer.readBoolean();
                    yield InteractPacket.InteractAt.create(entityId, sneaking, wrap(hand), location);
                }
            };
        } else if (packet instanceof ServerboundPlayerActionPacket instance) {
            return PlayerActionPacket.create(switch (instance.getAction()) {
                case START_DESTROY_BLOCK -> PlayerActionPacket.Action.START_DESTROY_BLOCK;
                case ABORT_DESTROY_BLOCK -> PlayerActionPacket.Action.ABORT_DESTROY_BLOCK;
                case STOP_DESTROY_BLOCK -> PlayerActionPacket.Action.STOP_DESTROY_BLOCK;
                case DROP_ALL_ITEMS -> PlayerActionPacket.Action.DROP_ALL_ITEMS;
                case DROP_ITEM -> PlayerActionPacket.Action.DROP_ITEM;
                case RELEASE_USE_ITEM -> PlayerActionPacket.Action.RELEASE_USE_ITEM;
                case SWAP_ITEM_WITH_OFFHAND -> PlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND;
            }, wrap(instance.getPos()), wrap(instance.getDirection()), instance.getSequence());
        } else if (packet instanceof ServerboundCommandSuggestionPacket instance) {
            return CommandSuggestionPacket.create(instance.getId(), instance.getCommand());
        } else if (packet instanceof ServerboundPlayerInputPacket instance) {
            return PlayerInputPacket.create(instance.getXxa(), instance.getZza(), instance.isJumping(), instance.isShiftKeyDown());
        } else if (packet instanceof ServerboundSignUpdatePacket instance) {
            return SignUpdatePacket.create(wrap(instance.getPos()), instance.getLines());
        } else if (packet instanceof ServerboundRenameItemPacket instance) {
            return RenameItemPacket.create(instance.getName());
        } else if (packet instanceof ServerboundContainerClickPacket instance) {
            return WindowClickPacket.create(instance.getContainerId(), instance.getStateId(), instance.getSlotNum(), instance.getButtonNum(),
                    switch (instance.getClickType()) {
                        case PICKUP -> WindowClickPacket.ClickType.PICKUP;
                        case QUICK_MOVE -> WindowClickPacket.ClickType.QUICK_MOVE;
                        case SWAP -> WindowClickPacket.ClickType.SWAP;
                        case CLONE -> WindowClickPacket.ClickType.CLONE;
                        case THROW -> WindowClickPacket.ClickType.THROW;
                        case QUICK_CRAFT -> WindowClickPacket.ClickType.QUICK_CRAFT;
                        case PICKUP_ALL -> WindowClickPacket.ClickType.PICKUP_ALL;
                    }, wrap(instance.getCarriedItem()), wrap(instance.getChangedSlots()));
        } else if (packet instanceof ServerboundPickItemPacket instance) {
            return PickItemPacket.create(instance.getSlot());
        } else if (packet instanceof ServerboundUseItemOnPacket instance) {
            return UseItemOnPacket.create(wrap(instance.getHand()), wrap(instance.getHitResult()), instance.getSequence());
        }
        Logger.error.println("Unmapped incoming packet: " + packet.getClass().getName());
        return new PacketBuilder() {
            @Nonnull
            @Override
            public Object build() {
                return packet;
            }
        };
    }
}
