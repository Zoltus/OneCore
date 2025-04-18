package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.EntityTypeArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.List;

import static io.github.zoltus.onecore.data.configuration.PlaceHolder.*;
import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class KillAll implements ICommand {

    @Override
    public void init() {
        //killall <type>
        Argument<?> arg0 = entityArg()
                .executesPlayer((p, args) -> {
                    EntityType type = (EntityType) args.get(0);
                    removeEntities(p, type, "*", p.getWorld().getEntities());
                });
        //killall <type> <range>
        Argument<?> arg1 = rangeArgument()
                .executesPlayer((p, args) -> {
                    double range = (double) args.get(1);
                    EntityType type = (EntityType) args.get(0);
                    removeEntities(p, type, String.valueOf(range), p.getNearbyEntities(range, range, range));
                });
        new Command(Commands.KILLALL_LABEL)
                .withPermission(Commands.KILLALL_PERMISSION)
                .withAliases(Commands.KILLALL_ALIASES)
                .then(arg0.then(arg1)).override();
    }

    private EntityTypeArgument entityArg() {
        return new EntityTypeArgument(NODES_ENTITY_TYPE.get());
    }

    private Argument<?> rangeArgument() {
        return new CustomArgument<>(new StringArgument(NODES_RANGE.get()), info -> {
            try {
                return Double.parseDouble(info.input());
            } catch (Exception e) {
                throw CustomArgument.CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(INVALID_RANGE.get()));
            }
        });
    }

    private void removeEntities(Player p, EntityType type, String range, List<Entity> entities) {
        List<Entity> list = entities.stream()
                .filter(entity -> entity.getType() == type && entity.getType() != EntityType.PLAYER).toList();
        list.forEach(Entity::remove);
        KILLALL_REMOVED_ENTITYS.rb(AMOUNT_PH, list.size())
                .rb(TYPE_PH, type.name())
                .rb(RADIUS_PH, range)
                .send(p);
    }
}
