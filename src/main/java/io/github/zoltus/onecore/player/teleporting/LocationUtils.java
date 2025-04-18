package io.github.zoltus.onecore.player.teleporting;

import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

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

    //todo async/rename?
    public static void teleportWMountSafeAsync(Player p, Location loc) {
        Location safeLoc = getSafeLocation(p, loc);
        if (safeLoc == null) {
            TP_NO_SAFE_LOCATIONS.send(p);
        } else {
            if (loc.getWorld() != null) {
                //Removes falldamage. Incase paper doesnt.
                p.setFallDistance(0);
                Entity vehicle = p.getVehicle();

                //Scans leashed entitys for the player
                final List<LivingEntity> leashedEntitys;
                if (p.hasPermission(Config.TELEPORT_WITH_LEASH.asPermission())) {
                    leashedEntitys = getLeashedEntities(p);
                } else {
                    leashedEntitys = null;
                }

                PaperLib.teleportAsync(p, safeLoc).thenRun(() -> {
                    //Teleport with vehicle
                    if (vehicle != null && p.hasPermission(Config.TELEPORT_WITH_VEHICLE.asPermission())) {
                        //todo cleanup this world mess?
                        //Tape fix to be able to teleport with horse while riding it.
                        //So it only works if there is more than 1 world.
                        for (World world : Bukkit.getWorlds()) {
                            if (!world.equals(p.getWorld())) {
                                vehicle.teleport(new Location(world, 0, 0, 0));
                            }
                        }
                        vehicle.teleport(loc);
                        vehicle.setFallDistance(-Float.MAX_VALUE);
                        vehicle.addPassenger(p);
                    }

                    //Teleport with leash
                    if (leashedEntitys != null && !leashedEntitys.isEmpty()
                            && p.hasPermission(Config.TELEPORT_WITH_LEASH.asPermission())) {
                        for (LivingEntity leashed : leashedEntitys) {
                            leashed.teleport(loc);
                            p.setLeashHolder(leashed);
                        }
                    }
                });
            }
        }
    }

    private static List<LivingEntity> getLeashedEntities(Player p) {
        return p.getNearbyEntities(10, 10, 10).stream()
                .filter(entity -> entity instanceof LivingEntity leashed
                        && leashed.isLeashed()
                        && leashed.getLeashHolder().equals(p)
                )
                .map(entity -> (LivingEntity) entity)
                .toList();
    }


    //If player is on creative it will teleport to any loc
    public static Location getSafeLocation(Player p, Location loc) {
        return p.getGameMode() != GameMode.CREATIVE
                && p.getGameMode() != GameMode.SPECTATOR
                && !p.isInvulnerable() ? getSafeLoc(p, loc) : loc;
    }

    private static boolean isSafeLoc(Player p, Location feet) {
        Block feetBlock = feet.getBlock();
        Block headBlock = feet.clone().add(0, 1, 0).getBlock();
        Block belowBlock = feet.clone().add(0, -1, 0).getBlock();
        boolean feetCheck = feetBlock.isPassable() && !isDamageBlock(feetBlock);
        boolean headCheck = headBlock.isPassable() && !isDamageBlock(headBlock);
        boolean belowCheck = (!p.isFlying() && !belowBlock.isPassable())
                || (p.isFlying() && belowBlock.isPassable())
                || (p.isFlying() && !belowBlock.isPassable())
                && !isDamageBlock(belowBlock);
        return feetCheck && headCheck && belowCheck;
    }

    private static boolean checkLoc(Player p, Location loc) {
        World w = loc.getWorld();
        return w != null && w.getWorldBorder().isInside(loc) && isSafeLoc(p, loc);
    }

    //TODO if player is flying it teleports 1 too high
    private static Location getSafeLoc(Player p, Location start) {
        //if orginal location is safe return it
        if (checkLoc(p, start)) {
            return start;
        }
        int radius = 4;
        int startX = start.getBlockX();
        int startY = start.getBlockY();
        int startZ = start.getBlockZ();
        int closestDistance = Integer.MAX_VALUE;
        Location closest = null;
        for (int x = startX - radius; x < startX + radius; x++) {
            for (int y = startY - radius + 1; y < startY + radius - 1; y++) {
                for (int z = startZ - radius; z < startZ + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z, start.getYaw(), start.getPitch());
                    int distance = (int) start.distance(loc);
                    //Sets the location if its safe and changes it if new location is closer
                    if (checkLoc(p, loc) && distance < closestDistance) {
                        closestDistance = distance;
                        closest = loc;
                    }
                }
            }
        }
        //center Location
        if (closest != null) {
            closest.setX(closest.getX() + 0.5);
            closest.setZ(closest.getZ() + 0.5);
            return closest;
        } else {
            return null;
        }
    }
}
