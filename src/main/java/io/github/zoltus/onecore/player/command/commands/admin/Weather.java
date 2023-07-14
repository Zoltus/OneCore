package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.Argument;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import io.github.zoltus.onecore.player.command.arguments.WeatherArgument;
import io.github.zoltus.onecore.player.command.arguments.WorldsArgument;
import io.github.zoltus.onecore.player.command.commands.WeatherMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

public class Weather implements ICommand {
    @Override
    public void init() {
        //weather <weather>
        Argument<?> arg0 = new WeatherArgument(false)
                .executesPlayer((p, args) -> {
                    changeWeather(p, (WeatherMode) args.get(0), p.getWorld());
                });
        //weather <weather> <world>
        Argument<?> arg1 = new WorldsArgument()
                .executes((sender, args) -> {
                    changeWeather(sender, (WeatherMode) args.get(0), (World) args.get(1));
                });
        new Command(Commands.WEATHER_LABEL)
                .withPermission(Commands.WEATHER_PERMISSION)
                .withAliases(Commands.WEATHER_ALIASES)
                .then(arg0.then(arg1))
                .override();
    }

    private void changeWeather(CommandSender sender, WeatherMode weatherType, World w) {
        switch (weatherType) {
            case CLEAR -> {
                w.setStorm(false);
                w.setWeatherDuration(0);
                w.setThundering(false);
            }
            case STORM -> {
                w.setStorm(true);
                w.setThundering(true);
            }
            case RAIN -> {
                w.setStorm(true);
                w.setThundering(false);
            }
        }
        Lang.WEATHER_CHANGED.send(sender, IConfig.WEATHER_PH, weatherType, IConfig.WORLD_PH, w.getName());
    }
}
