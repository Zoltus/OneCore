package sh.zoltus.onecore.player.command.commands;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.configuration.yamls.Commands;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.arguments.WorldsArgument;

import java.util.stream.Stream;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class Time implements IOneCommand {

    private Argument<?> timeArg() {
        return new CustomArgument<>(new StringArgument(NODES_TIME.getString()), info -> toTime(info.input()))
                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                        toSuggestion(info.currentArg(), TIME_SUGGESTIONS.getSplitArr())
                ));
    }

    public ApiCommand[] getCommands() {
        registerSingleWordTime();
        return new ApiCommand[]{
                //TIME <TIME>
                command(TIME_LABEL)
                        .withPermission(TIME_PERMISSION)
                        .withAliases(TIME_ALIASES)
                        .withArguments(timeArg())
                        .executesPlayer((p, args) -> {
                    changeTime(p, (long) args[0], p.getWorld());
                }),
                //TIME <TIME> <world>
                command(TIME_LABEL)
                        .withPermission(TIME_PERMISSION)
                        .withAliases(TIME_ALIASES)
                        .withArguments(timeArg(), new WorldsArgument())
                        .executes((sender, args) -> {
                    changeTime(sender, (long) args[0], (World) args[1]);
                })
        };
    }

    /**
     * Registers single word time commands
     */
    private void registerSingleWordTime() {
        for (String suggestion : TIME_SINGLE_WORD_CMDS.getSplitArr()) {
            command(suggestion)
                    .withPermission(TIME_PERMISSION)
                    .executesPlayer((player, args) -> {
                        changeTime(player, toTime(suggestion), player.getWorld());
                    }).override();
        }
    }

    //todo clean up
    @SneakyThrows
    private long toTime(String arg) {
        if (isTicks(arg)) {
            return Long.parseLong(arg);
        } else if (containsIgnoreCase(TIME_DAY_ALIASES, arg)) {
            return 0;
        } else if (containsIgnoreCase(TIME_NIGHT_ALIASES, arg)) {
            return 14000;
        } else if (containsIgnoreCase(TIME_MORNING_ALIASES, arg)) {
            return 23000;
        } else if (containsIgnoreCase(TIME_AFTERNOON_ALIASES, arg)) {
            return 12000;
        } else {
            throw new CustomArgument.CustomArgumentException(TIME_INVALID_TIME.getString());
        }
    }

    private void changeTime(CommandSender sender, long time, World w) {
        w.setTime(time);
        sender.sendMessage(TIME_CHANGED.rp(TIME_PH, w.getTime(), WORLD_PH, w.getName()));
    }

    private boolean containsIgnoreCase(Commands langArr, String contains) {
        return Stream.of(langArr.getSplitArr()).anyMatch(s -> s.equalsIgnoreCase(contains));
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
