package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.WorldsArgument;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.stream.Stream;

public class Weather implements ICommand {

    private Argument<?> weatherArgument() {
        return new CustomArgument<>(new StringArgument(Lang.NODES_WEATHER.getString()), info -> {
            String input = info.input();
            if (!Arrays.asList(Commands.WEATHER_SUGGESTIONS.getAsArray()).contains(input.toLowerCase())) {
                throw new CustomArgument.CustomArgumentException(Lang.WEATHER_INVALID_WEATHER.getString());
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), Commands.WEATHER_SUGGESTIONS.getAsArray())));
    }

    @Override
    public void init() {
        registerSingleWords(); //todo sameway than economy cmds
        //weather <weather>
        Argument<?> arg0 = weatherArgument()
                .executesPlayer((p, args) -> {
                    changeWeather(p, args.get(0), p.getWorld());
                });
        //weather <weather> <world>
        Argument<?> arg1 = new WorldsArgument()
                .executes((sender, args) -> {
                    changeWeather(sender, args.get(0), args.get(1));
                });
        new Command(Commands.WEATHER_LABEL)
                .withPermission(Commands.WEATHER_PERMISSION)
                .withAliases(Commands.WEATHER_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    /**
     * Registers single word weather commands
     */
    private void registerSingleWords() {
        for (String suggestion : Commands.WEATHER_SINGLE_WORD_CMDS.getAsArray()) {
            new Command(suggestion)
                    .withPermission(Commands.WEATHER_PERMISSION)
                    .executesPlayer((player, args) -> {
                        changeWeather(player, suggestion, player.getWorld());
                    }).override();
        }
    }

    private void changeWeather(CommandSender sender, Object weatherObject, Object worldObject) {
        String weatherType = (String) weatherObject;
        World w = (World) worldObject;
        if (containsIgnoreCase(Commands.WEATHER_CLEAR_ALIASES, weatherType)) {
            w.setStorm(false);
            w.setWeatherDuration(0);
            w.setThundering(false);
        } else if (containsIgnoreCase(Commands.WEATHER_STORM_ALIASES, weatherType)) {
            w.setStorm(true);
            w.setThundering(true);
        } else if (containsIgnoreCase(Commands.WEATHER_RAIN_ALIASES, weatherType)) {
            w.setStorm(true);
            w.setThundering(false);
        }
        Lang.WEATHER_CHANGED.send(sender, IConfig.WEATHER_PH, weatherType, IConfig.WORLD_PH, w.getName());
    }

    private boolean containsIgnoreCase(Commands langArr, String contains) {
        return Stream.of(langArr.getAsArray()).anyMatch(s -> s.equalsIgnoreCase(contains));
    }
}
