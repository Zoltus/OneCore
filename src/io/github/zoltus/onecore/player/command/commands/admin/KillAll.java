package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

public class KillAll implements ICommand {

    @Override
    public void init() {
        //killall <type>
        ArgumentTree arg0 = entityArg()
                .executesPlayer((p, args) -> {
                    EntityType type = (EntityType) args[0];
                    removeEntities(p, type, "*", p.getWorld().getEntities());
                });
        //killall <type> <range>
        ArgumentTree arg1 = rangeArgument()
                .executesPlayer((p, args) -> {
                    double range = (double) args[1];
                    EntityType type = (EntityType) args[0];
                    removeEntities(p, type, String.valueOf(range), p.getNearbyEntities(range, range, range));
                });
        new Command(Commands.KILLALL_LABEL)
                .withPermission(Commands.KILLALL_PERMISSION)
                .withAliases(Commands.KILLALL_ALIASES)
                .then(arg0.then(arg1)).override();
    }

    private EntityTypeArgument entityArg() {
        return new EntityTypeArgument(Lang.NODES_ENTITY_TYPE.getString());
    }

    private Argument<?> rangeArgument() {
        return new CustomArgument<>(new StringArgument(Lang.NODES_RANGE.getString()), (info) -> {
            try {
                return Double.parseDouble(info.input());
            } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(Lang.INVALID_RANGE.getString());
            }
        });
    }

    //todo if i add killall all, makesure it only deletes livingentities
    private void removeEntities(Player p, EntityType type, String range, List<Entity> entities) {
        List<Entity> list = entities.stream()
                .filter(entity -> entity.getType() == type && entity.getType() != EntityType.PLAYER).toList();
        list.forEach(Entity::remove);
        p.sendMessage(Lang.KILLALL_REMOVED_ENTITYS.rp(IConfig.AMOUNT_PH, list.size(), IConfig.TYPE_PH, type.name(), IConfig.RADIUS_PH, range));
    }
}
