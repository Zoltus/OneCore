package sh.zoltus.onecore.player.teleporting;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;

/**
 * PreLocation, Location without world, only world name
 * so it can be created before worlds are loaded
 */
public class PreLocation implements Serializable {
    private final transient Location loc;
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public PreLocation(Location loc) {
        worldName = loc.getWorld() == null ? null : loc.getWorld().getName();
        this.loc = loc;
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        yaw = loc.getYaw();
        pitch = loc.getPitch();
    }

    public Location toLocation() {
        return loc == null ? new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch) : loc;
    }

    @Override
    public String toString() {
        return worldName + ":" + x + ":" + y + ":" + z + ":" + yaw + ":" + pitch;
    }
}
