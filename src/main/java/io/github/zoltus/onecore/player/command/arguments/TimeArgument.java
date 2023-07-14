package io.github.zoltus.onecore.player.command.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.IArgument;

import java.util.ArrayList;
import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_TIME;

public class TimeArgument extends CustomArgument<Long, String> implements IArgument {

    private final ArrayList<String> suggestions = new ArrayList<>() {{
        addAll(Commands.TIME_DAY_ALIASES.getList());
        addAll(Commands.TIME_NIGHT_ALIASES.getList());
        addAll(Commands.TIME_MORNING_ALIASES.getList());
        addAll(Commands.TIME_AFTERNOON_ALIASES.getList());
    }};

    public TimeArgument() {
        this("");
    }

    public TimeArgument(String add) {
        super(new StringArgument(NODES_TIME.getString()), info -> {
            Long time = toTime(info.input());
            if (time == null) {
                throw CustomArgument.CustomArgumentException.fromString(Lang.TIME_INVALID_TIME.getString());
            } else {
                return time;
            }
        });
        replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), suggestions.toArray(new String[0]))));
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
