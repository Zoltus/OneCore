package io.github.zoltus.onecore.player.command.commands.admin;

import dev.jorel.commandapi.arguments.ChatArgument;
import io.github.zoltus.onecore.data.configuration.LangBuilder;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Config;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Broadcast implements ICommand {

    @Override
    public void init() {
        new Command(Commands.BROADCAST_LABEL)
                .withPermission(Commands.BROADCAST_PERMISSION)
                .withAliases(Commands.BROADCAST_ALIASES)
                .then(new ChatArgument(Lang.NODES_MESSAGE.get())
                        .executes((sender, args) -> {
                            BaseComponent[] components = (BaseComponent[]) args.get(0);
                            String message = Lang.BROADCAST_PREFIX.get() + BaseComponent.toLegacyText(components);
                            LangBuilder langBuilder = new LangBuilder(message);
                            Player[] players = Bukkit.getOnlinePlayers().toArray(Player[]::new);
                            langBuilder.send(players);

                            if (Config.BROADCAST_SOUND_ENABLED.getBoolean()) {
                                for (Player player : players) {
                                    String soundName = Config.BROADCAST_SOUND.get().toString();
                                    Sound sound = Registry.SOUNDS.get(NamespacedKey.minecraft(soundName));
                                    float volume = Config.BROADCAST_SOUND_VOLUME.getFloat();
                                    float pitch = Config.BROADCAST_SOUND_PITCH.getFloat();
                                    player.playSound(player, sound, volume, pitch);
                                }
                            }
                        })
                ).override();
    }
}
