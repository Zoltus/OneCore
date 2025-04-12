package io.github.zoltus.onecore.listeners;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InvseeHandler implements Listener {
    // PrepareInventoryResultEvent PrepareItemCraftEvent PrepareItemEnchantEvent
    //InventoryDragEvent
    //Tracks inventory viewers for swapping offline inv to online when user joins
    private static final BiMap<UUID, Inventory> regInv = HashBiMap.create();
    private static final BiMap<UUID, Inventory> enderInv = HashBiMap.create();

    public static void openInventory(Player sender, OfflinePlayer target, boolean isEnderChest) {
        Inventory inv;
        UUID targetUUID = target.getUniqueId();
        Player onlineTarget = target.getPlayer();
        BiMap<UUID, Inventory> invMap = isEnderChest ? enderInv : regInv;

        if (invMap.containsKey(targetUUID)) { // use offline inv
            inv = invMap.get(targetUUID);
        } else if (onlineTarget != null) { // Use online inv
            inv = isEnderChest ? onlineTarget.getEnderChest() : onlineTarget.getInventory();
        } else { // read offlineInv
            NBTPlayer nbtPlayer = new NBTPlayer(target);
            InventoryType type = isEnderChest ? InventoryType.ENDER_CHEST : InventoryType.PLAYER;
            inv = Bukkit.createInventory(null, type);
            //Chooses if it uses enderchesitems or normal inv items
            Map<Integer, ItemStack> items = isEnderChest ? nbtPlayer.getEnderItems() : nbtPlayer.getInventory();
            items.forEach((key, item) -> {
                int slot = key;
                //Check it to make sure inv is same size, and will only set storage contents
                if (slot > -1 && slot < inv.getSize()) {
                    inv.setItem(slot, item);
                }
            });
        }
        //Puts inventory to the list
        invMap.put(targetUUID, inv);
        sender.openInventory(inv);
    }


    /**
     * Prevents players without permission to edit the inventory and only view it
     *
     * @param e event
     */
    @EventHandler
    public void onInvseeEdit(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p) {
            Inventory inv = e.getClickedInventory();
            if (inv == null) return;
            boolean isEnderChest = inv.getType() == InventoryType.ENDER_CHEST;
            BiMap<UUID, Inventory> invMap = isEnderChest ? enderInv : regInv;
            if (invMap.inverse().containsKey(inv)) {
                String permission = isEnderChest
                        ? Commands.ENDERCHEST_EDIT_PERMISSION.asPermission()
                        : Commands.INVSEE_EDIT_PERMISSION.asPermission();
                // If player has only viewperm to invs
                if (!p.hasPermission(permission)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    /**
     * Basicly swaps offlineinv to online if player joins in middle of editing
     *
     * @param e event
     */
    @EventHandler
    public void swapOfflineInvToOnlineOnJoin(PlayerJoinEvent e) {
        Player target = e.getPlayer();
        UUID uuid = target.getUniqueId();
        List<BiMap<UUID, Inventory>> invs = List.of(regInv, enderInv);
        invs.forEach(inv -> {
            if (inv.containsKey(uuid)) {
                Inventory offlineInv = inv.get(uuid); // Get joining players inv from map
                // Get joining players online inv reg/ender depending which list looping
                Inventory onlineInv = inv == regInv ? target.getInventory() : target.getEnderChest();
                inv.put(uuid, onlineInv); // Swap offline inv to online on the map
                onlineInv.setStorageContents(offlineInv.getStorageContents()); // Copy offline inv to online inv
                // Get old invs viewers as copy since its modified when opening other inv for viewers
                List<HumanEntity> viewers = new ArrayList<>(offlineInv.getViewers());
                // Open the online inv for all viewers
                viewers.forEach(viewer -> viewer.openInventory(onlineInv));
            }
        });
    }

    /**
     * If inv editor closes inventory and target has left the server it will set items for him
     *
     * @param e event
     */
    @EventHandler
    public void saveOfflineInvOnClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        InventoryHolder holder = e.getInventory().getHolder();
        boolean isEnderChest = inv.getType() == InventoryType.ENDER_CHEST;
        BiMap<UUID, Inventory> invMap = isEnderChest ? enderInv : regInv;
        // Only save if target is offline
        if (holder instanceof OfflinePlayer target
                && !target.isOnline()
                && invMap.containsKey(target.getUniqueId())) {
            setOfflineInventory(target, inv);
            // Remove tracking if last viewer of inv view
            if (inv.getViewers().isEmpty()) {
                invMap.remove(target.getUniqueId());
            }
        }
    }


    /**
     * Sets inventory to offline player.
     * writes to nbt file
     *
     * @param offline player
     * @param inv     to set to offline player
     */
    private void setOfflineInventory(OfflinePlayer offline, Inventory inv) {
        NBTPlayer nbtPlayer = new NBTPlayer(offline);
        Map<Integer, ItemStack> updatedItems = new HashMap<>();
        ItemStack[] contents = inv.getContents(); // Get online inventory contents

        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if (stack != null && stack.getType() != Material.AIR) {
                updatedItems.put(i, stack); // Copy valid items
            }
        }
        if (inv.getType() == InventoryType.ENDER_CHEST) {
            nbtPlayer.setEnderItems(updatedItems);
        } else {
            nbtPlayer.setInventoryItems(updatedItems);
        }
        nbtPlayer.save();
    }
}

