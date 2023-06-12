package io.github.zoltus.onecore.player.command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface IArgument {

    default String[] toSuggestion(String input, String[] list) {
        return filter(input, list);
    }

    default String[] playerSuggestions(String input) {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            list.add(name);
        }
        return filter(input, list.toArray(new String[0]));
    }

    default String[] filter(String input, String... suggestions) {
        List<String> list = new ArrayList<>();
        for (String word : suggestions) {
            if (StringUtils.startsWithIgnoreCase(word, input)) {
                list.add(word);
            }
        }
        return list.toArray(new String[0]);
    }
}
