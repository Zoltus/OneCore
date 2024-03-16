package io.github.zoltus.onecore.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

/**
 * PreLocation, Location without world, only world name
 * so it can be created before worlds are loaded
 */
@Getter
@Setter
public class HomeLocation extends PreLocation {

    private boolean toDelete;

    public HomeLocation(Location loc, boolean toDelete) {
        super(loc);
        this.toDelete = toDelete;
    }
}
