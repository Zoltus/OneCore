package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.player.command.IArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_WORLD_NAME;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.WORLD_NOT_FOUND;

public class WorldsArgument extends CustomArgument<World, String> implements IArgument {

    public WorldsArgument() {
        this("");
    }

    public WorldsArgument(String add) {
        super(new StringArgument(NODES_WORLD_NAME.get() + add), info -> {
            World world = Bukkit.getWorld(info.input());
            if (world == null) {
                throw CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(WORLD_NOT_FOUND.get()));
            } else {
                return world;
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> worldSuggestions(info.currentArg())));
    }

    private String[] worldSuggestions(String input) {
        List<String> list = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            String name = world.getName();
            list.add(name);
        }
        return filter(input, list.toArray(new String[0]));
    }
}
