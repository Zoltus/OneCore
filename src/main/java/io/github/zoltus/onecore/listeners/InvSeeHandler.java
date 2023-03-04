package io.github.zoltus.onecore.listeners;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

public class InvSeeHandler implements Listener {

    //in bimap you can easily get key from value
    private static final BiMap<UUID, Inventory> inventorys = HashBiMap.create();
    private static final BiMap<UUID, Inventory> eInventorys = HashBiMap.create();

    public static void handle(Player sender, OfflinePlayer offTarget, boolean isEnderChest) {
        Inventory inv;
        UUID uuid = offTarget.getUniqueId();
        Player onlineTarget = offTarget.getPlayer();
        BiMap<UUID, Inventory> invMap = isEnderChest ? eInventorys : inventorys;
        //if inv is in edit it uses it
        if (invMap.containsKey(uuid)) {
            inv = invMap.get(uuid);
        } else if (onlineTarget != null) {
            //if player is online it uses onlineinv
            inv = isEnderChest ? onlineTarget.getEnderChest() : onlineTarget.getInventory();
        } else {
            //if player is offline it uses offlineinv
            NBTPlayer nbtPlayer = new NBTPlayer(offTarget);
            inv = Bukkit.createInventory(null, 36);
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
        invMap.put(uuid, inv);
        sender.openInventory(inv);
    }

    /**
     * Swaps offline inv to online inventory when target logs in
     *
     * @param e event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        handleJoin(e, false);
        handleJoin(e, true);
    }

    /**
     * Basicly swaps offlineinv to online if player joins in middle of editing
     *
     * @param e            event
     * @param isEnderChest check if we use enderchest or not
     */
    private void handleJoin(PlayerJoinEvent e, boolean isEnderChest) {
        Player target = e.getPlayer();
        UUID uuid = target.getUniqueId();
        BiMap<UUID, Inventory> invMap = isEnderChest ? eInventorys : inventorys;
        if (invMap.containsKey(uuid)) {
            //Gets offlineInv based on the uuid
            Inventory offlineInv = invMap.get(uuid);
            //if chooses if enderchest or inventory is used
            Inventory onlineInv = isEnderChest ? target.getEnderChest() : target.getInventory();
            //Changes offline inv to online
            invMap.put(uuid, onlineInv);
            //copy contents from saved inv to players inv
            onlineInv.setStorageContents(offlineInv.getStorageContents());
            //opens players inv to viewers and ignores inv owner
            //Opens online inventory for all the users
            List<HumanEntity> viewers = offlineInv.getViewers();
            //Will loop backwards since opening other inv will remove viewer from list
            //And would create concurrent modification exception
            for (int i = viewers.size() - 1; i >= 0; --i) {
                viewers.get(i).openInventory(target.getInventory());
            }
        }
    }


    /**
     * Prevents players without permission to edit the inventory and only view it
     *
     * @param e event
     */
    @EventHandler
    public void onEditEvent1(InventoryClickEvent e) {
        handleInventoryEdit(e, false);
        handleInventoryEdit(e, true);
    }

    public void handleInventoryEdit(InventoryClickEvent e, boolean isEnderChest) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        BiMap<UUID, Inventory> invMap = isEnderChest ? eInventorys : inventorys;

        //If player has only viewperm to invs
        String permission = isEnderChest ? Commands.ENDERCHEST_EDIT_PERMISSION.asPermission() : Commands.INVSEE_EDIT_PERMISSION.asPermission();

        if (invMap.inverse().containsKey(inv) && !p.hasPermission(permission)) {
            //If player owns the inv he can freely edit it
            if (inv != null && inv.getHolder() != null && inv.getHolder() == p)
                return;
            //Prevents player from editing his own enderchest
            if (!isEnderChest || !invMap.get(p.getUniqueId()).equals(p.getEnderChest())) {
                e.setCancelled(true);
            }
        }
    }

    /**
     * If inv editor closes inventory and target has left the server it will set items for him
     *
     * @param e event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        handleInventoryClose(e, false);
        handleInventoryClose(e, true);
    }

    private void handleInventoryClose(InventoryCloseEvent e, boolean isEnderChest) {
        BiMap<UUID, Inventory> invMap = isEnderChest ? eInventorys : inventorys;
        Inventory inv = e.getInventory();
        if (invMap.containsValue(inv) && inv.getViewers().isEmpty()) {
            //inverse maps and gets by inventory
            OfflinePlayer offP = Bukkit.getOfflinePlayer(invMap.inverse().get(inv));
            invMap.remove(offP.getUniqueId());
            //If player has logged in after opening inv it sets to his onlineinv
            Optional.ofNullable(offP.getPlayer())
                    .ifPresentOrElse(p -> p.getInventory().setStorageContents(inv.getStorageContents()), () -> {
                        setOfflineInventory(offP, inv, isEnderChest);   //Sets to players offline inv if player is offline
                    });
        }
    }


    /**
     * Sets inventory to offline player.
     *
     * @param offline player
     * @param inv     to set to offline player
     */
    private void setOfflineInventory(OfflinePlayer offline, Inventory inv, boolean isEnderChest) {
        NBTPlayer nbtPlayer = new NBTPlayer(offline);
        Map<Integer, ItemStack> updatedItems = Stream.of(inv.getContents())
                .collect(HashMap::new, (map, stack) -> map.put(map.size(), stack), Map::putAll);
        if (isEnderChest) {
            nbtPlayer.setEnderItems(updatedItems);
        } else {
            nbtPlayer.setInventoryItems(updatedItems);
        }
        nbtPlayer.save();
    }
}
