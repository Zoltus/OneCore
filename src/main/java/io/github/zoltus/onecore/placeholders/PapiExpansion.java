package io.github.zoltus.onecore.placeholders;

import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.economy.OneEconomy;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PapiExpansion extends PlaceholderExpansion {

    private final OneCore plugin;

    public PapiExpansion(OneCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "onecore";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Zoltus";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        LinkedHashMap<UUID, Double> balances = OneEconomy.getBalances();
        //baltop_1
        String[] split = params.split("_");
        if (!split[0].equals("baltopname") && !split[0].equals("baltopbalance")) {
            return null;
        }
        //Defaults to baltop1
        int rank = NumberUtils.toInt(split[1]) - 1;
        if (rank < 0) {
            rank = 0;
        }
        int index = 0;
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            if (index == rank) {
                if (split[0].equals("baltopname")) {
                    return entry.getKey().toString();
                } else if (split[0].equals("baltopbalance")) {
                    return entry.getValue().toString();
                }
            }
            index++;
        }
        return null;
    }
}
