package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.LocationArgument;
import org.bukkit.Location;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;
import sh.zoltus.onecore.player.command.commands.regular.Spawn;
import sh.zoltus.onecore.utils.PreLocation;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.NODES_LOCATION;
import static sh.zoltus.onecore.configuration.yamls.Lang.SETSPAWN_SET;

public class SetSpawn implements IOneCommand {

    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                //setspawn
                command(SETSPAWN_LABEL)
                        .withPermission(SETSPAWN_PERMISSION)
                        .withAliases(SETSPAWN_ALIASES)
                        .executesPlayer((p, args) -> {
                    Spawn.setSpawn(new PreLocation(p.getLocation()));
                    p.sendMessage(SETSPAWN_SET.getString());
                }),
                //setspawn <location>
                command(SETSPAWN_LABEL)
                        .withPermission(SETSPAWN_PERMISSION)
                        .withAliases(SETSPAWN_ALIASES)
                        .withArguments(new LocationArgument(NODES_LOCATION.getString()))
                        .executesPlayer((p, args) -> {
                    Spawn.setSpawn(new PreLocation((Location) args[0]));
                    p.sendMessage(SETSPAWN_SET.getString());
                })
        };
    }
}
