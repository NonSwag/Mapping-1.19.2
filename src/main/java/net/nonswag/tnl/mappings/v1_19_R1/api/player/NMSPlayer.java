package net.nonswag.tnl.mappings.v1_19_R1.api.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.nonswag.core.api.annotation.FieldsAreNullableByDefault;
import net.nonswag.core.api.annotation.MethodsReturnNonnullByDefault;
import net.nonswag.core.api.logger.Logger;
import net.nonswag.core.api.message.Message;
import net.nonswag.core.api.reflection.Reflection;
import net.nonswag.tnl.listener.Bootstrap;
import net.nonswag.tnl.listener.Listener;
import net.nonswag.tnl.listener.api.entity.TNLEntity;
import net.nonswag.tnl.listener.api.entity.TNLEntityLiving;
import net.nonswag.tnl.listener.api.entity.TNLEntityPlayer;
import net.nonswag.tnl.listener.api.location.BlockLocation;
import net.nonswag.tnl.listener.api.mods.labymod.LabyPlayer;
import net.nonswag.tnl.listener.api.packets.outgoing.*;
import net.nonswag.tnl.listener.api.player.Skin;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.player.manager.*;
import net.nonswag.tnl.listener.api.sign.SignMenu;
import net.nonswag.tnl.mappings.v1_19_R1.api.player.channel.PlayerChannelHandler;
import net.nonswag.tnl.mappings.v1_19_R1.api.player.manager.NMSResourceManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@FieldsAreNullableByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class NMSPlayer extends TNLPlayer {
    private PermissionManager permissionManager;
    private DataManager dataManager;
    private LabyPlayer labymod;
    private SoundManager soundManager;
    private NPCFactory npcFactory;
    private HologramManager hologramManager;
    private Messenger messenger;
    private ScoreboardManager scoreboardManager;
    private InterfaceManager interfaceManager;
    private WorldManager worldManager;
    private EnvironmentManager environmentManager;
    private HealthManager healthManager;
    private CombatManager combatManager;
    private SkinManager skinManager;
    private InventoryManager inventoryManager;
    private DebugManager debugManager;
    private AttributeManager attributeManager;
    private MetaManager metaManager;
    private EffectManager effectManager;
    private AbilityManager abilityManager;
    private ServerManager serverManager;
    private CinematicManger cinematicManger;
    private TitleManager titleManager;
    private ParticleManager particleManager;
    private BossBarManager bossBarManager;
    private CooldownManager cooldownManager;
    private NMSResourceManager resourceManager;
    private Pipeline pipeline;

    public NMSPlayer(@Nonnull Player player) {
        super(player);
    }

    @Nonnull
    private ServerPlayer nms() {
        return ((CraftPlayer) bukkit()).getHandle();
    }

    @Nonnull
    private ServerGamePacketListenerImpl playerConnection() {
        return nms().connection;
    }

    @Nonnull
    private ServerLevel worldServer() {
        return ((CraftWorld) bukkit().getWorld()).getHandle();
    }

    @Override
    public void setName(@Nonnull Plugin plugin, @Nonnull String name) {
        GameProfile profile = nms().gameProfile;
        Reflection.Field.set(profile, "name", name);
        Listener.getOnlinePlayers().forEach(all -> {
            all.abilityManager().hide(plugin, this);
            all.abilityManager().show(plugin, this);
        });
    }

    @Override
    public net.nonswag.tnl.listener.api.player.GameProfile getGameProfile() {
        return new net.nonswag.tnl.listener.api.player.GameProfile(getUniqueId(), getName(), skinManager().getSkin());
    }

    @Override
    public int getPing() {
        return nms().latency;
    }

    @Nonnull
    @Override
    public Pose getPlayerPose() {
        return switch (nms().getPose()) {
            case DIGGING -> Pose.DIGGING;
            case ROARING -> Pose.ROARING;
            case CROAKING -> Pose.CROAKING;
            case EMERGING -> Pose.EMERGING;
            case SNIFFING -> Pose.SNIFFING;
            case LONG_JUMPING -> Pose.LONG_JUMPING;
            case USING_TONGUE -> Pose.USING_TONGUE;
            case CROUCHING -> Pose.SNEAKING;
            case DYING -> Pose.DYING;
            case FALL_FLYING -> Pose.FALL_FLYING;
            case SLEEPING -> Pose.SLEEPING;
            case SPIN_ATTACK -> Pose.SPIN_ATTACK;
            case STANDING -> Pose.STANDING;
            case SWIMMING -> Pose.SWIMMING;
        };
    }

    @Override
    public void setPlayerPose(@Nonnull Pose pose) {
        nms().setPose(switch (pose) {
            case SNEAKING -> net.minecraft.world.entity.Pose.CROUCHING;
            case DYING -> net.minecraft.world.entity.Pose.DYING;
            case FALL_FLYING -> net.minecraft.world.entity.Pose.FALL_FLYING;
            case SLEEPING -> net.minecraft.world.entity.Pose.SLEEPING;
            case SPIN_ATTACK -> net.minecraft.world.entity.Pose.SPIN_ATTACK;
            case DIGGING -> net.minecraft.world.entity.Pose.DIGGING;
            case ROARING -> net.minecraft.world.entity.Pose.ROARING;
            case CROAKING -> net.minecraft.world.entity.Pose.CROAKING;
            case EMERGING -> net.minecraft.world.entity.Pose.EMERGING;
            case SNIFFING -> net.minecraft.world.entity.Pose.SNIFFING;
            case LONG_JUMPING -> net.minecraft.world.entity.Pose.LONG_JUMPING;
            case USING_TONGUE -> net.minecraft.world.entity.Pose.USING_TONGUE;
            case STANDING -> net.minecraft.world.entity.Pose.STANDING;
            case SWIMMING -> net.minecraft.world.entity.Pose.SWIMMING;
        });
    }

    @Override
    public void setPing(int ping) {
        nms().latency = ping;
    }

    @Nonnull
    @Override
    public PermissionManager permissionManager() {
        if (permissionManager == null) permissionManager = new PermissionManager() {

            @Nonnull
            @Override
            public Map<String, Boolean> getPermissions() {
                Map<String, Boolean> permissions = Reflection.Field.get(attachment, "permissions");
                if (permissions == null) Reflection.Field.set(attachment, "permissions", permissions = new HashMap<>());
                return permissions;
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return permissionManager;
    }

    @Nonnull
    @Override
    public DataManager data() {
        if (dataManager == null) dataManager = new DataManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return dataManager;
    }

    @Nonnull
    @Override
    public LabyPlayer labymod() {
        if (labymod == null) labymod = new LabyPlayer() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return labymod;
    }

    @Nonnull
    @Override
    public SoundManager soundManager() {
        if (soundManager == null) soundManager = new SoundManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return soundManager;
    }

    @Nonnull
    @Override
    public NPCFactory npcFactory() {
        if (npcFactory == null) npcFactory = new NPCFactory() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return npcFactory;
    }

    @Nonnull
    @Override
    public HologramManager hologramManager() {
        if (hologramManager == null) hologramManager = new HologramManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return hologramManager;
    }

    @Nonnull
    @Override
    public Messenger messenger() {
        if (messenger == null) messenger = new Messenger() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return messenger;
    }

    @Nonnull
    @Override
    public ScoreboardManager scoreboardManager() {
        if (scoreboardManager == null) scoreboardManager = new ScoreboardManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return scoreboardManager;
    }

    @Nonnull
    @Override
    public InterfaceManager interfaceManager() {
        if (interfaceManager == null) interfaceManager = new InterfaceManager() {
            @Override
            public void openVirtualSignEditor(@Nonnull SignMenu signMenu) {
                closeGUI(false);
                Location loc = worldManager().getLocation();
                BlockLocation location = new BlockLocation(worldManager().getWorld(), loc.getBlockX(), Math.max(loc.getBlockY() - 5, 0), loc.getBlockZ());
                signMenu.setLocation(location);
                BlockPos position = new BlockPos(location.getX(), location.getY(), location.getZ());
                OpenSignEditorPacket editor = OpenSignEditorPacket.create(location);
                Material material = Material.getMaterial(signMenu.getType().name());
                CraftBlockData blockData = (CraftBlockData) Objects.requireNonNullElse(material, Material.SPRUCE_WALL_SIGN).createBlockData();
                SignBlockEntity tileEntitySign = new SignBlockEntity(position, blockData.getState());
                for (int line = 0; line < signMenu.getLines().length; line++) {
                    tileEntitySign.setMessage(line, Component.literal(Message.format(signMenu.getLines()[line], getPlayer())));
                }
                worldManager().sendBlockChange(location, blockData);
                PacketBuilder.of(Objects.requireNonNull(tileEntitySign.getUpdatePacket())).send(getPlayer());
                editor.send(getPlayer());
                this.signMenu = signMenu;
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return interfaceManager;
    }

    @Nonnull
    @Override
    public WorldManager worldManager() {
        if (worldManager == null) worldManager = new WorldManager() {
            @Override
            public boolean isInRain() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void strikeLightning(@Nonnull Location location, boolean effect, boolean sound) {
                LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, worldServer());
                lightning.setPos(location.getX(), location.getY(), location.getZ());
                lightning.setVisualOnly(effect);
                lightning.setSilent(!sound);
                PacketBuilder.of(lightning.getAddEntityPacket()).send(getPlayer());
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return worldManager;
    }

    @Nonnull
    @Override
    public EnvironmentManager environmentManager() {
        if (environmentManager == null) environmentManager = new EnvironmentManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return environmentManager;
    }

    @Nonnull
    @Override
    public HealthManager healthManager() {
        if (healthManager == null) healthManager = new HealthManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return healthManager;
    }

    @Nonnull
    @Override
    public CombatManager combatManager() {
        if (combatManager == null) combatManager = new CombatManager() {
            @Override
            public void exitCombat() {
                nms().onLeaveCombat();
            }

            @Override
            public void enterCombat() {
                nms().onEnterCombat();
            }

            @Override
            public void setKiller(@Nullable TNLPlayer player) {
                bukkit().setKiller(player != null ? player.bukkit() : null);
            }

            @Override
            public void setLastDamager(@Nullable LivingEntity damager) {
                throw new UnsupportedOperationException();
            }

            @Nullable
            @Override
            public LivingEntity getLastDamager() {
                EntityDamageEvent cause = bukkit().getLastDamageCause();
                return cause != null ? (LivingEntity) cause.getEntity() : null;
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return combatManager;
    }

    @Nonnull
    @Override
    public SkinManager skinManager() {
        if (skinManager == null) skinManager = new SkinManager() {

            @Nullable
            private Skin skin = null;

            @Nonnull
            @Override
            public Skin getSkin() {
                if (this.skin == null) {
                    GameProfile profile = nms().gameProfile;
                    Collection<Property> textures = profile.getProperties().get("textures");
                    for (Property texture : textures) {
                        this.skin = new Skin(texture.getValue(), texture.getSignature());
                        break;
                    }
                    this.skin = Skin.getSkin(getPlayer().getUniqueId());
                }
                return skin;
            }

            @Override
            public void disguise(@Nonnull TNLEntity entity, @Nonnull TNLPlayer receiver) {
                if (getPlayer().equals(receiver)) return;
                RemoveEntitiesPacket.create(getPlayer().bukkit()).send(receiver);
                int id = entity.getEntityId();
                RemoveEntitiesPacket.create(id).send(receiver);
                if (entity instanceof TNLEntityPlayer player) {
                    PlayerInfoPacket.create(player, PlayerInfoPacket.Action.REMOVE_PLAYER).send(receiver);
                    Reflection.Field.set(entity, Entity.class, "id", getPlayer().getEntityId());
                    PlayerInfoPacket.create(player, PlayerInfoPacket.Action.ADD_PLAYER).send(receiver);
                    NamedEntitySpawnPacket.create(player).send(receiver);
                } else if (entity instanceof TNLEntityLiving livingEntity) {
                    Reflection.Field.set(entity, Entity.class, "id", getPlayer().getEntityId());
                    LivingEntitySpawnPacket.create(livingEntity.bukkit()).send(receiver);
                    EntityEquipmentPacket.create(livingEntity.bukkit()).send(receiver);
                } else {
                    Reflection.Field.set(entity, Entity.class, "id", getPlayer().getEntityId());
                    AddEntityPacket.create(entity.bukkit()).send(receiver);
                }
                EntityMetadataPacket.create(entity.bukkit()).send(receiver);
                EntityHeadRotationPacket.create(entity.bukkit()).send(receiver);
                Reflection.Field.set(entity, Entity.class, "id", id);
            }

            @Override
            public void setCapeVisibility(boolean visible) {
                cape = visible;
                nms().getEntityData().set(net.minecraft.world.entity.player.Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) (cape ? 127 : 126));
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return skinManager;
    }

    @Nonnull
    @Override
    public InventoryManager inventoryManager() {
        if (inventoryManager == null) inventoryManager = new InventoryManager() {
            @Override
            public void dropItem(@Nonnull ItemStack item, @Nonnull Consumer<org.bukkit.entity.Item> after) {
                Bootstrap.getInstance().sync(() -> {
                    ItemEntity drop = nms().drop(CraftItemStack.asNMSCopy(item), true, true, false);
                    if (!(drop instanceof org.bukkit.entity.Item)) return;
                    after.accept((org.bukkit.entity.Item) drop.getBukkitEntity());
                });
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return inventoryManager;
    }

    @Nonnull
    @Override
    public DebugManager debugManager() {
        if (debugManager == null) debugManager = new DebugManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return debugManager;
    }

    @Nonnull
    @Override
    public AttributeManager attributeManager() {
        if (attributeManager == null) attributeManager = new AttributeManager() {
            @Nonnull
            @Override
            public AttributeInstance getAttribute(@Nonnull Attribute attribute) {
                AttributeInstance instance = bukkit().getAttribute(attribute);
                assert instance != null;
                return instance;
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return attributeManager;
    }

    @Nonnull
    @Override
    public MetaManager metaManager() {
        if (metaManager == null) metaManager = new MetaManager() {
            @Override
            public void setFlag(int flag, boolean value) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean getFlag(int flag) {
                throw new UnsupportedOperationException();
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return metaManager;
    }

    @Nonnull
    @Override
    public EffectManager effectManager() {
        if (effectManager == null) effectManager = new EffectManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return effectManager;
    }

    @Nonnull
    @Override
    public AbilityManager abilityManager() {
        if (abilityManager == null) abilityManager = new AbilityManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return abilityManager;
    }

    @Nonnull
    @Override
    public ServerManager serverManager() {
        if (serverManager == null) serverManager = new ServerManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return serverManager;
    }

    @Nonnull
    @Override
    public CinematicManger cinematicManger() {
        if (cinematicManger == null) cinematicManger = new CinematicManger() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return cinematicManger;
    }

    @Nonnull
    @Override
    public TitleManager titleManager() {
        if (titleManager == null) titleManager = new TitleManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return titleManager;
    }

    @Nonnull
    @Override
    public ParticleManager particleManager() {
        if (particleManager == null) particleManager = new ParticleManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return particleManager;
    }

    @Nonnull
    @Override
    public BossBarManager bossBarManager() {
        if (bossBarManager == null) bossBarManager = new BossBarManager() {
            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return bossBarManager;
    }

    @Nonnull
    @Override
    public CooldownManager cooldownManager() {
        if (cooldownManager == null) cooldownManager = new CooldownManager() {
            @Override
            public void resetAttackCooldown() {
                setAttackCooldown(0);
            }

            @Override
            public void setAttackCooldown(float cooldown) {
                nms().oAttackAnim = cooldown;
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return cooldownManager;
    }

    @Nonnull
    @Override
    public NMSResourceManager resourceManager() {
        if (resourceManager == null) resourceManager = new NMSResourceManager() {
            @Nonnull
            @Override
            public NMSPlayer getPlayer() {
                return NMSPlayer.this;
            }
        };
        return resourceManager;
    }

    @Nonnull
    @Override
    public Pipeline pipeline() {
        return pipeline == null ? pipeline = new Pipeline() {

            @Nonnull
            private final String name = getName() + "-TNLListener";
            @Getter
            private boolean injected = false;

            @Override
            public <P> void sendPacket(@Nonnull P p, @Nullable net.nonswag.tnl.listener.api.packets.PacketSendListener listener) {
                if (p instanceof Packet<?> packet) playerConnection().send(packet, listener != null ? new PacketSendListener() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess(getPlayer());
                    }

                    @Nullable
                    @Override
                    public Packet<?> onFailure() {
                        return listener.onFailure(getPlayer());
                    }
                } : null);
                else throw new IllegalArgumentException("<'%s'> is not a packet".formatted(p.getClass().getName()));
            }

            @Override
            @SuppressWarnings("deprecation")
            public void uninject() {
                try {
                    Channel channel = nms().networkManager.channel;
                    if (channel.pipeline().get(name) != null) {
                        channel.eventLoop().submit(() -> channel.pipeline().remove(name));
                    }
                    getInjections().clear();
                    data().export();
                } catch (Exception e) {
                    Logger.error.println(e);
                } finally {
                    players.remove(bukkit());
                    injected = false;
                }
            }

            @Override
            public void inject() {
                try {
                    ChannelPipeline pipeline = nms().networkManager.channel.pipeline();
                    pipeline.addBefore("packet_handler", name, new PlayerChannelHandler() {
                        @Nonnull
                        @Override
                        public TNLPlayer getPlayer() {
                            return NMSPlayer.this;
                        }

                        @Override
                        @Deprecated
                        public boolean handleInjections(@Nonnull Object packet) {
                            return NMSPlayer.this.handleInjections(packet);
                        }
                    });
                    injected = true;
                } catch (Exception e) {
                    Logger.error.println(e);
                    uninject();
                }
            }

            @Nonnull
            @Override
            public TNLPlayer getPlayer() {
                return NMSPlayer.this;
            }
        } : pipeline;
    }
}
