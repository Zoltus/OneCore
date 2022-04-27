package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.WorldsArgument;

import java.util.Arrays;
import java.util.stream.Stream;

import static sh.zoltus.onecore.configuration.yamls.Commands.WEATHER_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.WORLD_PH;
import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Weather implements IOneCommand {

    private Argument weatherArgument() {
        return new CustomArgument<>(NODES_WEATHER.getString(), (info) -> {
            String input = info.input();
            if (!Arrays.asList(WEATHER_SUGGESTIONS.getSplitArr()).contains(input.toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(WEATHER_INVALID_WEATHER.getString());
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), WEATHER_SUGGESTIONS.getSplitArr())));
    }

    public ApiCommand[] getCommands() {
        //registerSingleWords();
        return new ApiCommand[]{ //todo sameway than economy cmds
                //weather <weather>
                command(WEATHER_LABEL)
                        .withPermission(WEATHER_PERMISSION)
                        .withAliases(WEATHER_ALIASES)
                        .withArguments(weatherArgument())
                        .executesPlayer((p, args) -> {
                    changeWeather(p, args[0], p.getWorld());
                }),
                //weather <weather> <world>
                command(WEATHER_LABEL)
                        .withPermission(WEATHER_PERMISSION)
                        .withAliases(WEATHER_ALIASES)
                        .withArguments(weatherArgument(), new WorldsArgument())
                        .executes((sender, args) -> {
                    changeWeather(sender, args[0], args[1]);
                })
        };
    }

    /**
     * Registers single word weather commands
     */
    private void registerSingleWords() {
        for (String suggestion : WEATHER_SINGLE_WORD_CMDS.getSplitArr()) {
            command(suggestion)
                    .withPermission(WEATHER_PERMISSION)
                    .executesPlayer((player, args) -> {
                        changeWeather(player, suggestion, player.getWorld());
                    }).override();
        }
    }

    private void changeWeather(CommandSender sender, Object weatherObject, Object worldObject) {
        String weatherType = (String) weatherObject;
        World w = (World) worldObject;
        if (containsIgnoreCase(WEATHER_CLEAR_ALIASES, weatherType)) {
            w.setStorm(false);
            w.setThundering(false);
        } else if (containsIgnoreCase(WEATHER_STORM_ALIASES, weatherType)) {
            w.setStorm(true);
            w.setThundering(true);
        } else if (containsIgnoreCase(WEATHER_RAIN_ALIASES, weatherType)) {
            w.setStorm(true);
            w.setThundering(false);
        }
        sender.sendMessage(WEATHER_CHANGED.rp(WEATHER_PH, weatherType, WORLD_PH, w.getName()));
    }

    private boolean containsIgnoreCase(Commands langArr, String contains) {
        return Stream.of(langArr.getSplitArr()).anyMatch(s -> s.equalsIgnoreCase(contains));
    }
}
