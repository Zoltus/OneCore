package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_TIME;

public class TimeArgument extends CustomArgument<Long, String> implements IArgument {

    private final static String[] day_aliases = Commands.TIME_DAY_ALIASES.getAsArray();
    private final static String[] night_aliases = Commands.TIME_NIGHT_ALIASES.getAsArray();
    private final static String[] morning_aliases = Commands.TIME_MORNING_ALIASES.getAsArray();
    private final static String[] afternoon_aliases = Commands.TIME_AFTERNOON_ALIASES.getAsArray();
    //Added only if player weather
    private final static String[] reset_aliases = Commands.PTIME_RESET_ALIASES.getAsArray();


    public TimeArgument(boolean isPlayerTime) {
        this(isPlayerTime, "");
    }

    public TimeArgument(boolean isPlayerTime, String add) {
        super(new StringArgument(NODES_TIME.getString()), info -> {
            Long time = toTime(info.input());
            if (time == null) {
                throw CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(Lang.TIME_INVALID_TIME.getString()));
            } else {
                return time;
            }
        });
        replaceSuggestions(ArgumentSuggestions.strings(info -> {
            String[] allAliases = ArrayUtils.addAll(day_aliases, night_aliases);
            allAliases = ArrayUtils.addAll(allAliases, morning_aliases);
            allAliases = ArrayUtils.addAll(allAliases, afternoon_aliases);
            if (isPlayerTime) {
                allAliases = ArrayUtils.addAll(allAliases, reset_aliases);
            }
            return toSuggestion(info.currentArg(), allAliases);
        }));
    }

    private static boolean containsIgnoreCase(Commands langArr, String contains) {
        return Stream.of(langArr.getAsArray()).anyMatch(s -> s.equalsIgnoreCase(contains));
    }

    public static Long toTime(String arg) {
        if (isTicks(arg)) {
            return Long.parseLong(arg);
        } else if (containsIgnoreCase(Commands.TIME_DAY_ALIASES, arg)) {
            return 0L;
        } else if (containsIgnoreCase(Commands.TIME_NIGHT_ALIASES, arg)) {
            return 14000L;
        } else if (containsIgnoreCase(Commands.TIME_MORNING_ALIASES, arg)) {
            return 23000L;
        } else if (containsIgnoreCase(Commands.TIME_AFTERNOON_ALIASES, arg)) {
            return 12000L;
        } else if (containsIgnoreCase(Commands.PTIME_RESET_ALIASES, arg)) {
            return -1L;
        } else {
            return null;
        }
    }

    private static boolean isTicks(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
