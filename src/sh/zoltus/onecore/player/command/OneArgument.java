package sh.zoltus.onecore.player.command;

import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class OneArgument extends CustomArgument<Object> {
    public OneArgument(String nodeName, CustomArgumentInfoParser<Object> parser) {
        super(nodeName, parser);
    }

    protected String[] playerSuggestions(String input) {
        return ApiCommand.filter(input, Bukkit.getOnlinePlayers().stream()
                .map(Player::getName).toArray(String[]::new));
    }

    protected String[] worldSuggestions(String input) {
        return ApiCommand.filter(input, Bukkit.getWorlds().stream()
                .map(World::getName).toArray(String[]::new));
    }


}
