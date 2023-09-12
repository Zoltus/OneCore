package io.github.zoltus.onecore.player.teleporting;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * PreLocation, Location without world, only world name
 * so it can be created before worlds are loaded
 */
@Getter
public class PreLocation {
    private final String worldName;
    private final double x, y, z;
    private final float yaw, pitch;

    public PreLocation(Location loc) {
        this(loc.getWorld() == null ? null :
                        loc.getWorld().getName(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch());
    }

    public PreLocation(String world, double x, double y, double z, float yaw, float pitch) {
        this.worldName = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}
