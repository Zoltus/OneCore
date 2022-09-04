package sh.zoltus.onecore.player.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.data.configuration.IConfig;
import sh.zoltus.onecore.data.configuration.OneYml;
import sh.zoltus.onecore.data.configuration.Yamls;

public interface IOneCommand {

    OneCore plugin = OneCore.getPlugin();
    OneYml cmds = Yamls.COMMANDS.getYml();

    //Inits commands&registers sht
    void init();

    //Short cut for making new ApiCommand("") todo add to ApiCommand class with static import
    default ApiCommand command(IConfig enumz) {
        return command(enumz.getString());
    }

    //Short cut for making new ApiCommand("")
    default ApiCommand command(String label) {
        return new ApiCommand(label);
    }

    //Registers all ApiCommands in the command and registers listeners
    default void register() {
        if (Listener.class.isAssignableFrom(getClass())) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener) this, plugin);
        }
        init();
    }

    //For chatpreview
    default BaseComponent[] toComponents(String text) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
    }

    default String[] toSuggestion(String input, String[] list) {
        return ApiCommand.filter(input, list);
    }

    /**
     * Checks if command is enabled in commands.yml
     *
     * @return boolean
     */
    static boolean isEnabled(Class<? extends IOneCommand> clazz) {
        return cmds.getOrSetDefault("Data." + clazz.getSimpleName().toLowerCase() + ".enabled", true);
    }
}
