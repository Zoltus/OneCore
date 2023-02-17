package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.WorldsArgument;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Time implements ICommand {

    private Argument<?> timeArg() {
        ArrayList<String> suggestions = new ArrayList<>();
        suggestions.addAll(Commands.TIME_DAY_ALIASES.getList());
        suggestions.addAll(Commands.TIME_NIGHT_ALIASES.getList());
        suggestions.addAll(Commands.TIME_MORNING_ALIASES.getList());
        suggestions.addAll(Commands.TIME_AFTERNOON_ALIASES.getList());
        return new CustomArgument<>(new StringArgument(NODES_TIME.getString()), info -> {
            Long time = toTime(info.input());
            if (time == null) {
                throw new CustomArgument.CustomArgumentException(Lang.TIME_INVALID_TIME.getString());
            } else {
                return time;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                toSuggestion(info.currentArg(), suggestions.toArray(new String[0]))
        ));
    }

    @Override
    public void init() {
        registerSingleWordTime();
        //TIME <TIME>
        Argument<?> arg0 = timeArg()
                .executesPlayer((p, args) -> {
                    changeTime(p, (long) args.get(0), p.getWorld());
                });
        //TIME <TIME> <world>
        Argument<?> arg1 = new WorldsArgument()
                .executes((sender, args) -> {
                    changeTime(sender, (long) args.get(0), (World) args.get(1));
                });
        new Command(Commands.TIME_LABEL)
                .withPermission(Commands.TIME_PERMISSION)
                .withAliases(Commands.TIME_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    /**
     * Registers single word time commands
     */
    private void registerSingleWordTime() {
        doo(Commands.TIME_DAY_ALIASES.getList(),
                Commands.TIME_NIGHT_ALIASES.getList(),
                Commands.TIME_MORNING_ALIASES.getList(),
                Commands.TIME_AFTERNOON_ALIASES.getList()
        );
    }

    @SafeVarargs
    private void doo(List<String>... list) {
        for (List<String> strings : list) {
            for (String suggestion : strings) {
                new Command(suggestion)
                        .withPermission(Commands.TIME_PERMISSION)
                        .executesPlayer((p, args) -> {
                            changeTime(p, toTime(suggestion), p.getWorld());
                        }).override();
            }
        }
    }

    private Long toTime(String arg) {
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

    private void changeTime(CommandSender sender, long time, World w) {
        w.setTime(time);
        TIME_CHANGED.send(sender, TIME_PH, w.getTime(), WORLD_PH, w.getName());
    }

    private boolean containsIgnoreCase(Commands langArr, String contains) {
        return Stream.of(langArr.getAsArray()).anyMatch(s -> s.equalsIgnoreCase(contains));
    }

    //todo clean up
    private boolean isTicks(String s) {
        try {
            Long.parseLong(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
