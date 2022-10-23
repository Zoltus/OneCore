package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import io.github.zoltus.onecore.player.command.arguments.WorldsArgument;
import lombok.SneakyThrows;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.stream.Stream;

public class Time implements IOneCommand {

    private Argument<?> timeArg() {
        return new CustomArgument<>(new StringArgument(Lang.NODES_TIME.getString()), info -> toTime(info.input()))
                .replaceSuggestions(ArgumentSuggestions.strings(info ->
                        toSuggestion(info.currentArg(), Commands.TIME_SUGGESTIONS.getAsArray())
                ));
    }

    @Override
    public void init() {
        registerSingleWordTime();
        //TIME <TIME>
        ArgumentTree arg0 = timeArg()
                .executesPlayer((p, args) -> {
                    changeTime(p, (long) args[0], p.getWorld());
                });
        //TIME <TIME> <world>
        ArgumentTree arg1 = new WorldsArgument()
                .executes((sender, args) -> {
                    changeTime(sender, (long) args[0], (World) args[1]);
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
        for (String suggestion : Commands.TIME_SINGLE_WORD_CMDS.getAsArray()) {
            new Command(suggestion)
                    .withPermission(Commands.TIME_PERMISSION)
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
        } else if (containsIgnoreCase(Commands.TIME_DAY_ALIASES, arg)) {
            return 0;
        } else if (containsIgnoreCase(Commands.TIME_NIGHT_ALIASES, arg)) {
            return 14000;
        } else if (containsIgnoreCase(Commands.TIME_MORNING_ALIASES, arg)) {
            return 23000;
        } else if (containsIgnoreCase(Commands.TIME_AFTERNOON_ALIASES, arg)) {
            return 12000;
        } else {
            throw new CustomArgument.CustomArgumentException(Lang.TIME_INVALID_TIME.getString());
        }
    }

    private void changeTime(CommandSender sender, long time, World w) {
        w.setTime(time);
        sender.sendMessage(Lang.TIME_CHANGED.rp(IConfig.TIME_PH, w.getTime(), IConfig.WORLD_PH, w.getName()));
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
