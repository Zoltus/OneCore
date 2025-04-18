package io.github.zoltus.onecore.player.command;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import io.github.zoltus.onecore.OneCore;
import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.Yamls;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public interface ICommand extends IArgument {
    OneCore plugin = OneCore.getPlugin();

    //Inits commands&registers sht
    void init();

    //Registers all ApiCommands in the command and registers listeners
    default void register() {
        if (Listener.class.isAssignableFrom(getClass())) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener) this, plugin);
        }
        init();
    }

    /**
     * Checks if command is enabled in commands.yml
     *
     * @return boolean
     */
    static boolean isEnabled(Class<? extends ICommand> clazz) {
        return Yamls.COMMANDS.getYml().getOrDefault("Data." + clazz.getSimpleName().toLowerCase() + ".enabled");
    }

    default Argument<String> multiLiteralArgument(IConfig label, IConfig aliases) {
        return new MultiLiteralArgument("todo", ArrayUtils.add(aliases.getAsArray(), label.get())); // todo
    }
}
