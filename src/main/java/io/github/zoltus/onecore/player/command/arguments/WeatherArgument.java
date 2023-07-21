package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IArgument;
import io.github.zoltus.onecore.player.command.commands.WeatherMode;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

public class WeatherArgument extends CustomArgument<WeatherInput, String> implements IArgument {

    private final static String[] rain_aliases = Commands.WEATHER_RAIN_ALIASES.getAsArray();
    private final static String[] clear_aliases = Commands.WEATHER_CLEAR_ALIASES.getAsArray();
    //Added only if server weather
    private final static String[] storm_aliases = Commands.WEATHER_STORM_ALIASES.getAsArray();
    //Added only if player weather
    private final static String[] reset_aliases = Commands.PWEATHER_RESET_ALIASES.getAsArray();

    public WeatherArgument(boolean isPlayerWeather) {
        this(isPlayerWeather, "");
    }

    public WeatherArgument(boolean isPlayerWeather, String add) {
        super(new StringArgument(Lang.NODES_WEATHER.getString() + add), info -> {
            String input = info.input();
            WeatherMode weatherMode;
            if (isPlayerWeather && containsIgnoreCase(reset_aliases, input.toLowerCase())) {
                weatherMode = WeatherMode.NONE;
            }else if (containsIgnoreCase(rain_aliases, input.toLowerCase())) {
                weatherMode = WeatherMode.RAIN;
            } else if (containsIgnoreCase(clear_aliases, input.toLowerCase())) {
                weatherMode = WeatherMode.CLEAR;
            } else if (!isPlayerWeather && containsIgnoreCase(storm_aliases, input.toLowerCase())) {
                weatherMode = WeatherMode.STORM;
            } else {
                throw CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(Lang.WEATHER_INVALID_WEATHER.getString()));
            }
            return new WeatherInput(input, weatherMode);
        });
        replaceSuggestions(ArgumentSuggestions.strings(info -> {
            String[] allAliases = ArrayUtils.addAll(rain_aliases, clear_aliases);
            if (!isPlayerWeather) {
                allAliases = ArrayUtils.addAll(allAliases, storm_aliases);
            } else {
                allAliases = ArrayUtils.addAll(allAliases, reset_aliases);
            }
            return toSuggestion(info.currentArg(), allAliases);
        }));
    }

    private static boolean containsIgnoreCase(String[] aliases, String contains) {
        return Stream.of(aliases).anyMatch(s -> s.equalsIgnoreCase(contains));
    }
}
