package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.papermc.lib.PaperLib;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Map;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.TP_NO_SAFE_LOCATIONS;

public class LocationUtils {

    private static boolean isDamageBlock(Block b) {
        return switch (b.getType()) {
            case LAVA, LAVA_CAULDRON, END_PORTAL,
                    NETHER_PORTAL, FIRE, CAMPFIRE,
                    SOUL_FIRE, SOUL_CAMPFIRE, MAGMA_BLOCK, VOID_AIR,
                    CACTUS, SWEET_BERRY_BUSH -> true;
            default -> false;
        };
    }

    public static void teleportSafeAsync(Player p, Location loc) {
        Location safeLoc = getSafeLocation(p, loc);
        if (safeLoc == null) {
            p.sendMessage(TP_NO_SAFE_LOCATIONS.getString());
        } else {
            if (loc.getWorld() != null) {
                Entity vehicle = p.getVehicle();
                //todo move player tele here and test.
                if (Config.TELEPORT_WITH_VEHICLE.getBoolean() && vehicle != null) {
                    PaperLib.teleportAsync(vehicle, safeLoc);
                    PaperLib.teleportAsync(p, safeLoc);
                    vehicle.addPassenger(p);
                } else {
                    PaperLib.teleportAsync(p, safeLoc);
                }
            }
        }
    }

    //If player is on creative it will teleport to any loc
    public static Location getSafeLocation(Player p, Location loc) {
        return p.getGameMode() != GameMode.CREATIVE && !p.isInvulnerable() ? getSafeLoc(p, loc) : loc;
    }

    private static boolean isSafeLoc(Player p, Location feet) {
        Block feetBlock = feet.getBlock();
        Block headBlock = feet.clone().add(0, 1, 0).getBlock();
        Block belowBlock = feet.clone().add(0, -1, 0).getBlock();
        boolean feetCheck = feetBlock.isPassable() && !isDamageBlock(feetBlock);
        boolean headCheck = headBlock.isPassable() && !isDamageBlock(headBlock);
        boolean belowCheck = (!p.isFlying() && !belowBlock.isPassable()) && !isDamageBlock(belowBlock);
        return feetCheck && headCheck && belowCheck;
    }

    private static boolean checkLoc(Player p, Location loc) {
        World w = loc.getWorld();
        return w != null && w.getWorldBorder().isInside(loc) && isSafeLoc(p, loc);
    }

    private static Location getSafeLoc(Player p, Location start) {
        int radius = 4;
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        Map.Entry<Integer, Location> entry = null;
        for (int x = startX - radius; x < startX + radius; x++) {
            for (int y = startY - radius + 1; y < startY + radius - 1; y++) {
                for (int z = startZ - radius; z < startZ + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    int distance = (int) start.distance(loc);
                    //Sets the location if its safe and changes it if new location is closer
                    if (checkLoc(p, loc) && (entry == null || entry.getKey() > distance)) {
                        entry = Map.entry(distance, loc);
                    }
                }
            }
        }
        return entry == null ? null : entry.getValue();
    }
}
