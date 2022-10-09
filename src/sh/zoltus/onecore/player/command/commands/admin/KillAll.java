package sh.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.ArgumentTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import sh.zoltus.onecore.player.command.Command;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.util.List;

import static sh.zoltus.onecore.data.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.data.configuration.yamls.Lang.*;

public class KillAll implements IOneCommand {

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
        new Command(KILLALL_LABEL)
                .withPermission(KILLALL_PERMISSION)
                .withAliases(KILLALL_ALIASES)
                .then(arg0.then(arg1)).override();
    }

    private EntityTypeArgument entityArg() {
        return new EntityTypeArgument(NODES_ENTITY_TYPE.getString());
    }

    private Argument<?> rangeArgument() {
        return new CustomArgument<>(new StringArgument(NODES_RANGE.getString()), (info) -> {
            try {
                return Double.parseDouble(info.input());
            } catch (Exception e) {
                throw new CustomArgument.CustomArgumentException(INVALID_RANGE.getString());
            }
        });
    }

    //todo if i add killall all, makesure it only deletes livingentities
    private void removeEntities(Player p, EntityType type, String range, List<Entity> entities) {
        List<Entity> list = entities.stream()
                .filter(entity -> entity.getType() == type && entity.getType() != EntityType.PLAYER).toList();
        list.forEach(Entity::remove);
        p.sendMessage(KILLALL_REMOVED_ENTITYS.rp(AMOUNT_PH, list.size(), TYPE_PH, type.name(), RADIUS_PH, range));
    }
}
