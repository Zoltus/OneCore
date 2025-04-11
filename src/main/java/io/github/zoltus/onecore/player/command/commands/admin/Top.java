package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import io.github.zoltus.onecore.player.teleporting.LocationUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Top implements ICommand {
    @Override
    public void init() {
        //top <player>
        Argument<Player> arg0 = new PlayerArgument()
                .withPermission(Commands.TOP_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    executes(sender, (Player) args.get(0));
                });
        //top
        new Command(Commands.TOP_LABEL)
                .withPermission(Commands.TOP_PERMISSION)
                .withAliases(Commands.TOP_ALIASES)
                .executesPlayer((p, args) -> {
                    executes(p, p);
                }).then(arg0)
                .override();
    }

    private void executes(CommandSender sender, Player target) {
        World w = target.getWorld();
        Location loc = target.getLocation();
        //Gets highest block and adds +1 to y
        loc.setY((double) w.getHighestBlockYAt(loc) + 1);
        //Resets player velocity so player is falling and teleports to water floating, it wont just fall throught because of the velocity
        LocationUtils.teleportWMountSafeAsync(target, loc);
        if (target == sender) {
            TOP_TELPORTED.send(target);
        } else {
            TOP_TELEPORTED_TARGET.rb(PLAYER_PH, target.getName()).send(target);
        }
    }
}
