package net.nonswag.tnl.mappings.v1_19_R1.listeners;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.nonswag.tnl.core.api.file.helper.JsonHelper;
import net.nonswag.tnl.core.api.language.Language;
import net.nonswag.tnl.core.api.logger.Logger;
import net.nonswag.tnl.core.api.message.Message;
import net.nonswag.tnl.holograms.api.Hologram;
import net.nonswag.tnl.holograms.api.event.InteractEvent;
import net.nonswag.tnl.listener.Bootstrap;
import net.nonswag.tnl.listener.api.event.TNLEvent;
import net.nonswag.tnl.listener.api.gui.AnvilGUI;
import net.nonswag.tnl.listener.api.gui.GUI;
import net.nonswag.tnl.listener.api.gui.GUIItem;
import net.nonswag.tnl.listener.api.gui.Interaction;
import net.nonswag.tnl.listener.api.mods.ModMessage;
import net.nonswag.tnl.listener.api.packets.OpenWindowPacket;
import net.nonswag.tnl.listener.api.packets.SetSlotPacket;
import net.nonswag.tnl.listener.api.player.TNLPlayer;
import net.nonswag.tnl.listener.api.player.manager.ResourceManager;
import net.nonswag.tnl.listener.api.player.npc.FakePlayer;
import net.nonswag.tnl.listener.api.serializer.ModPacketSerializer;
import net.nonswag.tnl.listener.api.settings.Settings;
import net.nonswag.tnl.listener.api.sign.SignMenu;
import net.nonswag.tnl.listener.events.*;
import net.nonswag.tnl.listener.events.mods.labymod.LabyPlayerMessageEvent;
import net.nonswag.tnl.mappings.v1_19_R1.api.player.NMSPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public class PacketListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPacket(@Nonnull PlayerPacketEvent event) {
        TNLPlayer player = event.getPlayer();
        if (event.getPacket() instanceof ServerboundChatPacket packet) {
            PlayerChatEvent chatEvent = new PlayerChatEvent(player, packet.message());
            if (Settings.BETTER_CHAT.getValue() && !chatEvent.isCommand()) {
                player.messenger().chat(chatEvent);
                event.setCancelled(true);
            }
        } else if (event.getPacket() instanceof ServerboundClientCommandPacket packet) {
            if (packet.getAction().equals(ServerboundClientCommandPacket.Action.REQUEST_STATS)) {
                event.setCancelled(true);
            }
        } else if (event.getPacket() instanceof ServerboundClientInformationPacket packet) {
            Language language = Language.fromLocale(packet.language());
            Language old = player.data().getLanguage();
            if (!language.equals(Language.UNKNOWN) && !language.equals(old)) {
                player.data().setLanguage(language);
                new PlayerLanguageChangeEvent(player, old).call();
            }
        } else if (event.getPacket() instanceof ServerboundCustomPayloadPacket packet) {
            event.setCancelled(true);
            String namespace = packet.getIdentifier().getNamespace();
            if (!namespace.equals("labymod3")) return;
            try {
                byte[] data = new byte[packet.data.readableBytes()];
                packet.data.readBytes(data);
                ByteBuf buf = Unpooled.wrappedBuffer(data);
                String key = ModPacketSerializer.readString(buf, Short.MAX_VALUE);
                JsonElement message = JsonHelper.parse(ModPacketSerializer.readString(buf, Short.MAX_VALUE));
                ModMessage modMessage = new ModMessage(packet.getIdentifier().getPath(), key, message);
                player.labymod().handleMessage(modMessage);
                new LabyPlayerMessageEvent(player.labymod(), modMessage).call();
            } catch (Exception e) {
                Logger.error.println("An error occurred while reading a mod message from <'" + namespace + "'>", e);
            }
        } else if (event.getPacket() instanceof ServerboundInteractPacket packet) {
            Entity entity = packet.getTarget(((CraftWorld) player.worldManager().getWorld()).getHandle());
            if (entity != null) {
                if (!player.delay("entity-interact", 50)) return;
                TNLEvent entityEvent;
                if (packet.getActionType().equals(ServerboundInteractPacket.ActionType.ATTACK)) {
                    entityEvent = new EntityDamageByPlayerEvent(player, entity.getBukkitEntity());
                } else {
                    entityEvent = new PlayerInteractAtEntityEvent(player, entity.getBukkitEntity());
                }
                if (!entityEvent.call()) event.setCancelled(true);
            } else {
                FakePlayer fakePlayer = player.npcFactory().getFakePlayer(packet.getEntityId());
                if (fakePlayer != null) {
                    if (!player.delay("fakeplayer-interact", 50)) return;
                    FakePlayer.InteractEvent.Type type = packet.getActionType().equals(ServerboundInteractPacket.ActionType.ATTACK) ?
                            FakePlayer.InteractEvent.Type.LEFT_CLICK : FakePlayer.InteractEvent.Type.RIGHT_CLICK;
                    fakePlayer.onInteract().accept(new FakePlayer.InteractEvent(player, fakePlayer, type));
                } else {
                    if (!player.delay("hologram-interact", 50)) return;
                    InteractEvent.Type type = packet.getActionType().equals(ServerboundInteractPacket.ActionType.ATTACK) ?
                            InteractEvent.Type.LEFT_CLICK : InteractEvent.Type.RIGHT_CLICK;
                    for (Hologram hologram : Hologram.getHolograms()) {
                        for (int i : player.hologramManager().getIds(hologram)) {
                            if (packet.getEntityId() != i) continue;
                            hologram.onInteract().accept(new InteractEvent(hologram, player, type));
                            return;
                        }
                    }
                }
            }
        } else if (event.getPacket() instanceof ServerboundPlayerActionPacket packet) {
            switch (packet.getAction()) {
                case STOP_DESTROY_BLOCK, START_DESTROY_BLOCK, ABORT_DESTROY_BLOCK -> {
                    PlayerDamageBlockEvent.BlockDamageType damageType = PlayerDamageBlockEvent.BlockDamageType.fromString(packet.getAction().name());
                    if (damageType.isUnknown()) return;
                    BlockPos position = packet.getPos();
                    Block block = new Location(player.worldManager().getWorld(), position.getX(), position.getY(), position.getZ()).getBlock();
                    Block relative = block.getRelative(packet.getDirection().getStepX(), packet.getDirection().getStepY(), packet.getDirection().getStepZ());
                    if (relative.getType().equals(Material.FIRE)) {
                        position = new BlockPos(relative.getX(), relative.getY(), relative.getZ());
                        block = new Location(player.worldManager().getWorld(), position.getX(), position.getY(), position.getZ()).getBlock();
                    }
                    PlayerDamageBlockEvent blockEvent = new PlayerDamageBlockEvent(player, block, damageType);
                    event.setCancelled(!blockEvent.call());
                    if (blockEvent.isCancelled()) return;
                    if (blockEvent.getBlockDamageType().isInteraction(false)) {
                        Bootstrap.getInstance().sync(() -> {
                            BlockFace[] faces = {BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.UP, BlockFace.DOWN};
                            for (BlockFace blockFace : faces) {
                                Block rel = blockEvent.getBlock().getRelative(blockFace);
                                player.worldManager().sendBlockChange(rel.getLocation(), rel.getBlockData());
                                rel.getState().update(true, false);
                            }
                        });
                    } else if (blockEvent.getBlockDamageType().isItemAction()) {
                        player.inventoryManager().updateInventory();
                    }
                }
            }
        } else if (event.getPacket() instanceof ServerboundCommandSuggestionPacket packet) {
            String[] args = packet.getCommand().split(" ");
            int index = packet.getId();
            if (args.length == 0) event.setCancelled(true);
            else if (args[0].startsWith("/")) {
                if (!Settings.TAB_COMPLETER.getValue() && !player.permissionManager().hasPermission(Settings.TAB_COMPLETE_BYPASS_PERMISSION.getValue())) {
                    event.setCancelled(true);
                }
            }
            /*
        } else if (event.getPacket() instanceof PacketPlayInBlockPlace packet) {
            ItemStack itemStack = null;
            if (packet.b().equals(EnumHand.MAIN_HAND)) {
                itemStack = player.inventoryManager().getInventory().getItemInMainHand();
            } else if (packet.b().equals(EnumHand.OFF_HAND)) {
                itemStack = player.inventoryManager().getInventory().getItemInOffHand();
            }
            if (itemStack == null || !itemStack.getType().equals(Material.GLASS_BOTTLE)) return;
            Block target = player.worldManager().getTargetBlock(5, FluidCollisionMode.ALWAYS);
            if (!(target != null && (target.getType().equals(Material.WATER)
                    || (target.getBlockData() instanceof Waterlogged && ((Waterlogged) target.getBlockData()).isWaterlogged())
                    || target.getType().equals(Material.KELP) || target.getType().equals(Material.KELP_PLANT)))) {
                for (int i = 0; i < 6; i++) {
                    target = player.worldManager().getTargetBlock(i, FluidCollisionMode.ALWAYS);
                    if (target != null && (target.getType().equals(Material.WATER)
                            || (target.getBlockData() instanceof Waterlogged
                            && ((Waterlogged) target.getBlockData()).isWaterlogged())
                            || target.getType().equals(Material.KELP)
                            || target.getType().equals(Material.KELP_PLANT))) {
                        break;
                    }
                }
            }
            if (target != null && (target.getType().equals(Material.WATER)
                    || (target.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged())
                    || target.getType().equals(Material.KELP) || target.getType().equals(Material.KELP_PLANT))) {
                ItemStack itemStack1 = player.inventoryManager().getInventory().getItemInOffHand();
                if (!itemStack.getType().equals(Material.GLASS_BOTTLE) && !itemStack1.getType().equals(Material.GLASS_BOTTLE)) {
                    return;
                }
                PlayerBottleFillEvent.Hand hand = packet.b().equals(EnumHand.MAIN_HAND) ? PlayerBottleFillEvent.Hand.MAIN_HAND : PlayerBottleFillEvent.Hand.OFF_HAND;
                PlayerBottleFillEvent fillEvent = new PlayerBottleFillEvent(player, TNLItem.create(itemStack), target, hand);
                if (fillEvent.getHand().isMainHand()) {
                    player.inventoryManager().getInventory().setItemInMainHand(fillEvent.getItemStack());
                } else player.inventoryManager().getInventory().setItemInOffHand(fillEvent.getItemStack());
                if (!fillEvent.call()) event.setCancelled(true);
                if (fillEvent.getReplacement() == null) return;
                var leftover = player.inventoryManager().getInventory().addItem(fillEvent.getReplacement());
                player.inventoryManager().updateInventory();
                if (leftover.isEmpty()) return;
                Bootstrap.getInstance().sync(() -> leftover.values().forEach(item ->
                        player.worldManager().getWorld().dropItemNaturally(player.worldManager().getLocation(), item)));
            }
             */
        } else if (event.getPacket() instanceof ServerboundSignUpdatePacket packet) {
            SignMenu menu = player.interfaceManager().getSignMenu();
            if (menu == null) return;
            event.setCancelled(true);
            if (menu.getResponse() != null) {
                Bootstrap.getInstance().sync(() -> {
                    boolean success = menu.getResponse().test(player, packet.getLines());
                    if (!success && menu.isReopenOnFail()) {
                        player.interfaceManager().openVirtualSignEditor(menu);
                    }
                });
            }
            if (menu.getLocation() != null) player.worldManager().sendBlockChange(menu.getLocation());
            player.interfaceManager().closeSignMenu();
        } else if (event.getPacket() instanceof ServerboundRenameItemPacket packet) {
            GUI gui = player.interfaceManager().getGUI();
            if (!(gui instanceof AnvilGUI anvil)) return;
            event.setCancelled(true);
            for (AnvilGUI.TextInputEvent textInputEvent : anvil.getTextInputEvents()) {
                textInputEvent.onTextInput(player, packet.getName());
            }
        } else if (event.getPacket() instanceof ServerboundResourcePackPacket packet) {
            ((NMSPlayer) player).resourceManager().setStatus(switch (packet.getAction()) {
                case ACCEPTED -> ResourceManager.Status.ACCEPTED;
                case DECLINED -> ResourceManager.Status.DECLINED;
                case FAILED_DOWNLOAD -> ResourceManager.Status.FAILED_DOWNLOAD;
                case SUCCESSFULLY_LOADED -> ResourceManager.Status.SUCCESSFULLY_LOADED;
            });
        } else if (event.getPacket() instanceof ServerboundContainerClickPacket packet) {
            GUI gui = player.interfaceManager().getGUI();
            if (gui == null) return;
            int slot = packet.getSlotNum();
            if (slot < gui.getSize() && slot >= 0) {
                Interaction.Type type = Interaction.Type.fromNMS(packet.getButtonNum(), packet.getClickType().name());
                gui.getClickListener().onClick(player, slot, type);
                GUIItem item = gui.getItem(slot);
                if (item != null) for (Interaction interaction : item.getInteractions(type)) {
                    interaction.getAction().accept(player);
                }
            } else if (slot >= gui.getSize()) {
                event.setPacketField("slotNum", slot - gui.getSize() + 9);
                event.setPacketField("containerId", 0);
            }
            event.setCancelled(true);
            event.reply(SetSlotPacket.create(SetSlotPacket.Inventory.COURSER, -1, null));
            player.inventoryManager().updateInventory();
            player.interfaceManager().updateGUI();
        } else if (event.getPacket() instanceof ServerboundContainerClosePacket) {
            GUI gui = player.interfaceManager().getGUI();
            if (gui == null) return;
            event.setCancelled(true);
            if (!gui.getCloseListener().onClose(player, false)) {
                event.reply(OpenWindowPacket.create(gui.getSize() / 9, Message.format(gui.getTitle())));
                player.interfaceManager().updateGUI(gui);
            } else {
                if (gui.getCloseSound() != null) player.soundManager().playSound(gui.getCloseSound());
                player.interfaceManager().closeGUI(false);
            }
        } else if (event.getPacket() instanceof ServerboundPickItemPacket packet) {
            PlayerItemPickEvent pickEvent = new PlayerItemPickEvent(player, packet.getSlot());
            if (!pickEvent.call()) event.setCancelled(true);
        } else if (event.getPacket() instanceof ServerboundUseItemOnPacket packet) {
            BlockPos position = packet.getHitResult().getBlockPos();
            Block block = new Location(player.worldManager().getWorld(), position.getX(), position.getY(), position.getZ()).getBlock();
            if (block.getLocation().distance(player.worldManager().getLocation()) > 10) {
                event.setCancelled(true);
                return;
            }
            Direction direction = packet.getHitResult().getDirection();
            BlockFace face = player.worldManager().getFacing().getOppositeFace();
            try {
                face = BlockFace.valueOf(direction.name());
            } catch (Exception ignored) {
            } finally {
                ItemStack itemStack;
                if (packet.getHand().equals(InteractionHand.MAIN_HAND)) {
                    itemStack = player.inventoryManager().getInventory().getItemInMainHand();
                } else itemStack = player.inventoryManager().getInventory().getItemInOffHand();
                PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, block, face, itemStack);
                if (!interactEvent.call()) {
                    event.setCancelled(true);
                    interactEvent.getPlayer().inventoryManager().updateInventory();
                }
                Bootstrap.getInstance().sync(() -> {
                    for (BlockFace f : BlockFace.values()) {
                        player.worldManager().sendBlockChange(interactEvent.getClickedBlock().getRelative(f));
                    }
                }, 1);
            }
        } else if (event.getPacket() instanceof ClientboundAddEntityPacket packet) {
            var type = event.<EntityType<?>>getPacketField("type");
            if (type == null) return;
            if (Settings.BETTER_FALLING_BLOCKS.getValue() && type.equals(EntityType.FALLING_BLOCK)) {
                event.setCancelled(true);
            } else if (Settings.BETTER_TNT.getValue() && type.equals(EntityType.TNT)) event.setCancelled(true);
        } else if (event.getPacket() instanceof ClientboundResourcePackPacket packet) {
            ((NMSPlayer) player).resourceManager().setResourcePackUrl(packet.getUrl());
            ((NMSPlayer) player).resourceManager().setResourcePackHash(packet.getHash());
        } else if (event.getPacket() instanceof ServerboundContainerClosePacket) {
            GUI gui = player.interfaceManager().getGUI();
            if (gui == null) return;
            if (gui.getCloseSound() != null) player.soundManager().playSound(gui.getCloseSound());
            gui.getCloseListener().onClose(player, true);
        }
    }
}
