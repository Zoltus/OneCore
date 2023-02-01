package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.commands.regular.Spawn;
import org.bukkit.Location;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SetSpawn implements ICommand {

    @Override
    public void init() {
        //setspawn <location>
        Argument<?> arg0 = new LocationArgument(NODES_LOCATION.getString())
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn((Location) args.get(0));
                    SETSPAWN_SET.send(p);
                });
        //setspawn
        new Command(Commands.SETSPAWN_LABEL)
                .withPermission(Commands.SETSPAWN_PERMISSION)
                .withAliases(Commands.SETSPAWN_ALIASES)
                .executesPlayer((p, args) -> {
                    Spawn.setSpawn(p.getLocation());
                    SETSPAWN_SET.send(p);
                }).then(arg0).override();
    }
}
