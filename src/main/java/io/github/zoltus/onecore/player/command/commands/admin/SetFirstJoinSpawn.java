package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LocationArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.commands.regular.Spawn;
import org.bukkit.Location;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.NODES_LOCATION;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.SETSPAWN_SET;

public class SetFirstJoinSpawn implements ICommand {

    @Override
    public void init() {
        //setspawn <location>
        Argument<?> arg0 = new LocationArgument(NODES_LOCATION.getString())
                .executesPlayer((p, args) -> {
                    Spawn.setFirstJoinSpawn((Location) args.get(0));
                    SETSPAWN_SET.send(p);
                });
        //setspawn
        new Command(Commands.SET_FIRST_JOIN_SPAWN_LABEL)
                .withPermission(Commands.SET_FIRST_JOIN_SPAWN_PERMISSION)
                .withAliases(Commands.SET_FIRST_JOIN_SPAWN_ALIASES)
                .executesPlayer((p, args) -> {
                    Spawn.setFirstJoinSpawn(p.getLocation());
                    SETSPAWN_SET.send(p);
                }).then(arg0).override();
    }
}
