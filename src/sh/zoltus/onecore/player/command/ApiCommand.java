package sh.zoltus.onecore.player.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.executors.CommandExecutor;
import dev.jorel.commandapi.executors.ConsoleCommandExecutor;
import dev.jorel.commandapi.executors.PlayerCommandExecutor;
import org.apache.commons.lang.StringUtils;
import sh.zoltus.onecore.data.configuration.IConfig;
import sh.zoltus.onecore.player.User;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class ApiCommand extends CommandAPICommand {

    public ApiCommand(String commandName) {
        super(commandName);
    }

    public ApiCommand withPermission(IConfig enumz) {
        super.withPermission(enumz.asPermission());
        return this;
    }

    public static String[] filter(String input, String... suggestions) {
        return Arrays.stream(suggestions)
                .filter(word -> StringUtils.startsWithIgnoreCase(word, input))
                .toArray(String[]::new);
    }

    //Registers commands also separately
    public ApiCommand withSeparateSubcommands(ApiCommand... cmds) {
        for (ApiCommand cmd : cmds) {
            cmd.override();
            withSubcommand(cmd);
        }
        return this;
    }

    public ApiCommand withAliases(IConfig enumz) {
        if (!enumz.getString().isEmpty()) {
            String[] aliases = enumz.getSplitArr();
            super.withAliases(aliases);
        }
        return this;
    }

    public ApiCommand executesUser(BiConsumer<User, Object[]> executor) {
        super.executesPlayer((sender, args) -> {
            executor.accept(User.of(sender), args);
        });
        return this;
    }

    public ApiCommand withSubcommand(ApiCommand subcommand) {
        super.withSubcommand(subcommand);
        return this;
    }

    public ApiCommand withSubcommands(ApiCommand... cmds) {
        for (ApiCommand cmd : cmds) {
            this.withSubcommand(cmd);
        }
        return this;
    }

    public ApiCommand executes(CommandExecutor executor) {
        super.executes(executor);
        return this;
    }

    @Override
    public ApiCommand withPermission(String permission) {
        super.withPermission(permission);
        return this;
    }

    @Override
    public ApiCommand withAliases(String... aliases) {
        if (aliases.length != 0)
            super.withAliases(aliases);
        return this;
    }

    @Override
    public ApiCommand withArguments(Argument... args) {
        super.withArguments(args);
        return this;
    }

    @Override
    public ApiCommand executesPlayer(PlayerCommandExecutor executor) {
        super.executesPlayer(executor);
        return this;
    }

    @Override
    public ApiCommand executesConsole(ConsoleCommandExecutor executor) {
        super.executesConsole(executor);
        return this;
    }
}
