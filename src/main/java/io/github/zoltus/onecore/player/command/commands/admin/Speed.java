package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.OfflinePlayerArgument;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import io.github.zoltus.onecore.player.nbt.NBTPlayer;

import java.util.Arrays;
import java.util.List;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class Speed implements ICommand {

    private final List<String> speedArgs = Arrays.asList(SPEED_MODE_FLY.getString(), SPEED_MODE_WALK.getString());

    private Argument<?> speedIntArg() {
        return new CustomArgument<>(new StringArgument(NODES_SPEED.getString()), info -> {
            try {
                float speed = Float.parseFloat(info.input());
                if (speed > 10.0f) {
                    speed = 10.0f;
                } else if (speed < 0.0001f) {
                    speed = 0.0001f;
                }
                return speed;
            } catch (Exception ex) {
                throw CustomArgument.CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(SPEED_MODE_INVALID_MODE.getString()));
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info ->
                toSuggestion(info.currentArg(), new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"})));
    }

    private Argument<?> speedModeArg() {
        return new CustomArgument<>(
                new StringArgument(SPEED_MODE_FLY.getString() + "/" + SPEED_MODE_WALK.getString()), info -> {
            String input = info.input();
            if (!speedArgs.contains(input.toLowerCase())) {
                throw CustomArgument.CustomArgumentException.fromBaseComponents(TextComponent.fromLegacyText(SPEED_MODE_INVALID_SPEED.getString()));
            } else {
                return input;
            }
        }).replaceSuggestions(ArgumentSuggestions.strings(info -> toSuggestion(info.currentArg(), speedArgs.toArray(new String[0]))));
    }

    @Override
    public void init() {
        //speed <amount>
        Argument<?> arg0 = speedIntArg()
                .executesPlayer((sender, args) -> {
                    handle(sender, sender, (float) args.get(0), null);
                });
        //speed <amount> <player>
        Argument<?> arg1 = new OfflinePlayerArgument()
                .executes((player, args) -> {
                    handle(player, (OfflinePlayer) args.get(1), (float) args.get(0), null);
                });
        //speed <amount> <player> <fly/walk>
        Argument<?> arg2 = speedModeArg()
                .executes((sender, args) -> {
                    handle(sender, (OfflinePlayer) args.get(1), (float) args.get(0), (String) args.get(2));
                });
        new Command(Commands.SPEED_LABEL)
                .withPermission(Commands.SPEED_PERMISSION_OTHER)
                .withAliases(Commands.SPEED_ALIASES)
                .then(arg0.then(arg1.then(arg2)))
                .override();
    }

    private void handle(CommandSender sender, OfflinePlayer offTarget, float speed, String mode) {
        Player target = offTarget.getPlayer();

        if (target != null) {
            mode = setOnlineSpeed(target, speed, mode);
        } else {
            mode = setOfflineSpeed(offTarget, speed, mode);
        }

        if (sender == offTarget.getPlayer()) {
            SPEED_YOUR_SPEED_SET.send(sender, AMOUNT_PH, speed, MODE_PH, mode);
        } else {
            SPEED_YOU_SET_SPEED.send(sender, AMOUNT_PH, speed, MODE_PH, mode, PLAYER_PH, offTarget.getName());
            if (target != null) {
                SPEED_YOUR_SPEED_SET.send(target, AMOUNT_PH, speed, MODE_PH, mode);
            }
        }
    }

    private String setOnlineSpeed(Player onTarget, float speed, String mode) {
        if (mode == null) {
            mode = onTarget.isFlying() ? SPEED_MODE_FLY.getString() : SPEED_MODE_WALK.getString();
        }
        if (mode.equalsIgnoreCase(SPEED_MODE_FLY.getString())) {
            onTarget.setFlySpeed(speedToSmallNumbers(speed, true));
        } else {
            onTarget.setWalkSpeed(speedToSmallNumbers(speed, false));
        }
        return mode;
    }

    private String setOfflineSpeed(OfflinePlayer offP, float speed, String mode) {
        NBTPlayer nbtPlayer = new NBTPlayer(offP);
        if (mode == null) {
            mode = nbtPlayer.getFlying() ? SPEED_MODE_FLY.getString() : SPEED_MODE_WALK.getString();
        }

        if (mode.equalsIgnoreCase(SPEED_MODE_FLY.getString())) {
            nbtPlayer.setFlySpeed(speedToSmallNumbers(speed, true));
        } else {
            nbtPlayer.setWalkSpeed(speedToSmallNumbers(speed, false));
        }
        nbtPlayer.save();
        return mode;
    }


    private float speedToSmallNumbers(float userSpeed, boolean isFly) {
        float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1.0f;
        //todo maxspeed
//        if (!hasbypass) {
//        }
        if (userSpeed < 1.0f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
