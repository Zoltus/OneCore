package io.github.zoltus.onecore.player.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public interface IArgument {

    default String[] toSuggestion(String input, String[] list) {
        return filter(input, list);
    }

    default String[] playerSuggestions(String input) {
        return filter(input, Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).toArray(String[]::new));
    }

    default String[] filter(String input, String... suggestions) {
        return Arrays.stream(suggestions)
                .filter(word -> StringUtils.startsWithIgnoreCase(word, input))
                .toArray(String[]::new);
    }
}
