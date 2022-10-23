package io.github.zoltus.onecore.player.command;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface OneArgument {

    default String[] toSuggestion(String input, String[] list) {
        return ApiCommand.filter(input, list);
    }

    default String[] playerSuggestions(String input) {
        return ApiCommand.filter(input, Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).toArray(String[]::new));
    }
    default String[] worldSuggestions(String input) {
        return ApiCommand.filter(input, Bukkit.getWorlds().stream()
                .map(World::getName).toArray(String[]::new));
    }
}
