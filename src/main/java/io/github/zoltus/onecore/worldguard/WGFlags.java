package io.github.zoltus.onecore.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.zoltus.onecore.OneCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.logging.Level;

public class WGFlags implements Listener {

    public static StateFlag BLOCK_UPDATE = new StateFlag("hanging-physics", true);
    private RegionContainer regionContainer;
    private final WorldGuard worldGuard;

    //todo cleanup
    public WGFlags(OneCore plugin) {
        this.worldGuard = WorldGuard.getInstance();
        if (worldGuard == null) {
            plugin.getLogger().log(Level.WARNING, "WorldGuard not found, WorldGuard Extra Flags disabled.");
            return;
        }
        FlagRegistry registry = worldGuard.getFlagRegistry();

        try {
            // create a flag with the name "block-updates", defaulting to true
            registry.register(BLOCK_UPDATE);
        } catch (FlagConflictException e) {
            Bukkit.getLogger().log(Level.WARNING, "Worldguard Flag \"block-updates\" already exists!");
        }
    }

    //Must be called onEnable
    public void onEnable() {
        this.regionContainer = worldGuard.getPlatform().getRegionContainer();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemDamageEvent(HangingBreakEvent e) {
        HangingBreakEvent.RemoveCause cause = e.getCause();
        Location location = BukkitAdapter.adapt(e.getEntity().getLocation());
        if ((cause == HangingBreakEvent.RemoveCause.OBSTRUCTION || cause == HangingBreakEvent.RemoveCause.PHYSICS)
                && regionContainer.createQuery().queryState(location, null, BLOCK_UPDATE) == StateFlag.State.DENY) {
            Bukkit.broadcastMessage("test");
            e.setCancelled(true);
        }
    }
}
