package sh.zoltus.onecore.player.command;

import dev.jorel.commandapi.CommandAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.configuration.IConfig;
import sh.zoltus.onecore.configuration.OneYml;
import sh.zoltus.onecore.configuration.Yamls;

import java.util.stream.Stream;

public interface IOneCommand {

    OneCore plugin = OneCore.getPlugin();
    OneYml cmds = Yamls.COMMANDS.getYml();

    ApiCommand[] getCommands();

    //Short cut for making new ApiCommand("") todo add to ApiCommand class with static import
    default ApiCommand command(IConfig enumz) {
        return command(enumz.getString());
    }

    //Short cut for making new ApiCommand("")
    default ApiCommand command(String label) {
        return new ApiCommand(label);
    }

    //Registers all ApiCommands in the command
    default void register() {
        if (Listener.class.isAssignableFrom(getClass())) {
            Bukkit.getServer().getPluginManager().registerEvents((Listener) this, plugin);
        }
        ApiCommand[] cmds = getCommands();
        CommandAPI.unregister(cmds[0].getName(), true); //Gets cmd[0] so it unregisters core cmd only
        //Override wont work because it would unregister other cmds,
        //Override would work if all cmds would be as subcommands /home set, /home delete
        //But instead I have /speed <amount> ect
        //todo system/list for apicommands registered so could use overridelike system
        Stream.of(cmds).forEach(ApiCommand::register);
    }

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
    default boolean isEnabled() {
        return cmds.getOrSetDefault("Data." + getClass().getSimpleName().toLowerCase() + ".enabled", true);
    }
}
