package sh.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;
import sh.zoltus.onecore.player.command.OneArgument;

import static sh.zoltus.onecore.configuration.yamls.Lang.WORLD_NOT_FOUND;

public class WorldsArgument extends OneArgument {

    public WorldsArgument() {
        this("");
    }

    public WorldsArgument(String add) {
        super(add, (info) -> {
            World world = Bukkit.getWorld(info.input());
            if (world == null) {
                throw new CustomArgument.CustomArgumentException(WORLD_NOT_FOUND.getString());
            } else {
                return world;
            }
        });
        replaceSuggestions(ArgumentSuggestions.strings(info -> worldSuggestions(info.currentArg())));
    }
}
