package io.github.zoltus.onecore.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.session.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent;

import java.util.Set;
import java.util.logging.Level;

public class WGFlags implements Listener {

    private final WorldGuard worldGuard;
    private final WorldGuardPlugin worldGuardPlugin;
    private RegionContainer regionContainer;
    private SessionManager sessionManager;

    private static final StateFlag HANGING_PHYSICS = new StateFlag("hanging-physics", true);
    private static final SetFlag<Material> BLOCK_BREAK_WHITELIST = new SetFlag<>("block-break-whitelist", new BlockFlag(null));
    private static final SetFlag<Material> BLOCK_PLACE_WHITELIST = new SetFlag<>("block-place-whitelist", new BlockFlag(null));

    //todo cleanup
    public WGFlags(WorldGuardPlugin worldGuardPlugin) {
        this.worldGuard = WorldGuard.getInstance();
        this.worldGuardPlugin = worldGuardPlugin;
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            // create a flag with the name "block-updates", defaulting to true
            registry.register(HANGING_PHYSICS);
            registry.register(BLOCK_BREAK_WHITELIST);
            registry.register(BLOCK_PLACE_WHITELIST);
        } catch (FlagConflictException e) {
            Bukkit.getLogger().log(Level.WARNING, "Worldguard Flag \"block-updates\" already exists!");
        }
    }

    //Must be called onEnable
    public void onEnable() {
        this.sessionManager = worldGuard.getPlatform().getSessionManager();
        this.regionContainer = worldGuard.getPlatform().getRegionContainer();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerItemDamageEvent(HangingBreakEvent e) {
        HangingBreakEvent.RemoveCause cause = e.getCause();
        Location location = e.getEntity().getLocation();
        //todo bypass
        if ((cause == HangingBreakEvent.RemoveCause.OBSTRUCTION
                || cause == HangingBreakEvent.RemoveCause.PHYSICS)
                && flagApplies(location, HANGING_PHYSICS)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (!hasBypass(e.getPlayer()) && hasBlock(BLOCK_BREAK_WHITELIST, e.getBlock())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (!hasBypass(e.getPlayer()) && hasBlock(BLOCK_PLACE_WHITELIST, e.getBlock())) {
            e.setCancelled(true);
        }
    }

    private boolean hasBlock(SetFlag<Material> flag, Block block) {
        for (ProtectedRegion region : regionContainer.createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(block.getLocation()))
                .getRegions()) {
            Set<Material> materials = region.getFlag(flag);
            if (materials != null && !materials.isEmpty()) {
                if (materials.contains(block.getType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasBypass(Player p) {
        return sessionManager.hasBypass(worldGuardPlugin.wrapPlayer(p), BukkitAdapter.adapt(p.getWorld()));
    }

    private boolean flagApplies(Location location, StateFlag flag) {
        return (regionContainer.createQuery().queryState(BukkitAdapter.adapt(location), null, flag) == StateFlag.State.DENY);
    }
}

























