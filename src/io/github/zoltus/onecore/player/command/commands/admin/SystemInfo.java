package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.ICommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;

import static io.github.zoltus.onecore.data.configuration.yamls.Lang.*;

public class SystemInfo implements ICommand {

    @Override
    public void init() {
        new Command(Commands.SYSTEM_LABEL)
                .withPermission(Commands.SYSTEM_PERMISSION)
                .withAliases(Commands.SYSTEM_ALIASES)
                .executes((sender, args) -> sendSystemInfo(sender))
                .override();
    }

    private void sendSystemInfo(CommandSender sender) {
        File diskPartition = new File("/");
        long totalSpace = diskPartition.getTotalSpace() / (1024 * 1024 * 1024);
        long freeSpace = diskPartition.getUsableSpace() / (1024 * 1024 * 1024);
        long usedSpace = totalSpace - freeSpace;

        SYSTEM_OS.send(sender, SYSTEM_PH, System.getProperty("os.name") + " " + System.getProperty("os.arch"));
        SYSTEM_VERSION.send(sender, VERSION_PH, System.getProperty("os.version"));
        SYSTEM_USERNAME.send(sender, USERNAME_PH, System.getProperty("user.name"));
        SYSTEM_PROCESSORS.send(sender, PROCESSORS_PH, String.valueOf(Runtime.getRuntime().availableProcessors()));
        SYSTEM_JAVA_VERSION.send(sender, VERSION_PH, System.getProperty("java.version"));
        SYSTEM_RAM_USAGE.send(sender, USED_PH, String.valueOf(Runtime.getRuntime().freeMemory() / 1048576), TOTAL_PH, String.valueOf((Runtime.getRuntime().maxMemory() / 1048576)));
        SYSTEM_DISK_USAGE.send(sender, USED_PH, usedSpace, TOTAL_PH, totalSpace);
        SYSTEM_SERVER_VERSION.send(sender, VERSION_PH, Bukkit.getServer().getBukkitVersion());

    }
}