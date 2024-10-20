package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.PlayerArgument;
import io.github.zoltus.onecore.player.command.arguments.WeatherArgument;
import io.github.zoltus.onecore.player.command.arguments.WeatherInput;
import io.github.zoltus.onecore.player.command.commands.WeatherMode;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PWeather implements ICommand {
    @Override
    public void init() {
        //pweather <weather>
        Argument<?> arg0 = new WeatherArgument(true)
                .executesPlayer((p, args) -> {
                    changeWeather(p, (WeatherInput) args.get(0), p);
                });
        //pweather <weather> <player>
        Argument<?> arg1 = new PlayerArgument()
                .withPermission(Commands.PWEATHER_PERMISSION_OTHER.asPermission())
                .executes((sender, args) -> {
                    changeWeather(sender, (WeatherInput) args.get(0), (Player) args.get(1));
                });
        new Command(Commands.PWEATHER_LABEL)
                .withPermission(Commands.PWEATHER_PERMISSION)
                .withAliases(Commands.PWEATHER_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private void changeWeather(CommandSender sender, WeatherInput weatherType, Player target) {
        String alias = weatherType.input();
        WeatherMode weatherMode = weatherType.weather();
        switch (weatherMode) {
            case CLEAR -> target.setPlayerWeather(WeatherType.CLEAR);
            case STORM -> target.setPlayerWeather(WeatherType.DOWNFALL);
            case NONE -> target.resetPlayerWeather();
        }
        if (sender != target) {
            Lang.PWEATHER_OTHER_CHANGED.send(sender, IConfig.WEATHER_PH, alias, IConfig.PLAYER_PH, target.getName());
        }
        Lang.PWEATHER_CHANGED.send(target, IConfig.WEATHER_PH, alias);
    }
}
