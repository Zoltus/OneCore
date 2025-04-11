package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;

import java.util.Arrays;
import java.util.function.Predicate;

public class GamemodeArgument extends CustomArgument<GameMode, String> implements IArgument {

    public GamemodeArgument() {
        super(new StringArgument(Lang.NODES_GAMEMODE.get()), info -> {
            GameMode gm = getGamemode(info.input());
            if (gm == null) {
                throw CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(Lang.GAMEMODE_INVALID_GAMEMODE.get()));
            } else {
                return gm;
            }
        });
        replaceSuggestions(ArgumentSuggestions
                .strings(info -> toSuggestion(info.currentArg(), Commands.GAMEMODE_SUGGESTIONS.getAsArray())));
    }

    /**
     * @param input gamemode string
     * @return Gamemode based on aliases in config
     */
    private static GameMode getGamemode(String input) {
        Predicate<Commands> hasGm = gamemodes -> Arrays
                .stream(gamemodes.getAsArray()).anyMatch(input::equalsIgnoreCase);
        if (hasGm.test(Commands.GAMEMODE_ALIASES_SURVIVAL)) {
            return GameMode.SURVIVAL;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_CREATIVE)) {
            return GameMode.CREATIVE;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_ADVENTURE)) {
            return GameMode.ADVENTURE;
        } else if (hasGm.test(Commands.GAMEMODE_ALIASES_SPECTATOR)) {
            return GameMode.SPECTATOR;
        } else {
            return null;
        }
    }
}
