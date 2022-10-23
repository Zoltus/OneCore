package io.github.zoltus.onecore.player.command.commands.admin;

import io.github.zoltus.onecore.data.configuration.IConfig;
import io.github.zoltus.onecore.data.configuration.yamls.Commands;
import io.github.zoltus.onecore.data.configuration.yamls.Lang;
import io.github.zoltus.onecore.player.command.Command;
import io.github.zoltus.onecore.player.command.IOneCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.File;

public class SystemInfo implements IOneCommand {

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

        sender.sendMessage(Lang.SYSTEM_OS.rp(IConfig.SYSTEM_PH, System.getProperty("os.name") + " " + System.getProperty("os.arch")));
        sender.sendMessage(Lang.SYSTEM_VERSION.rp(IConfig.VERSION_PH, System.getProperty("os.version")));
        sender.sendMessage(Lang.SYSTEM_USERNAME.rp(IConfig.USERNAME_PH, System.getProperty("user.name")));
        sender.sendMessage(Lang.SYSTEM_PROCESSORS.rp(IConfig.PROCESSORS_PH, String.valueOf(Runtime.getRuntime().availableProcessors())));
        sender.sendMessage(Lang.SYSTEM_JAVA_VERSION.rp(IConfig.VERSION_PH, System.getProperty("java.version")));
        sender.sendMessage(Lang.SYSTEM_RAM_USAGE.rp(IConfig.USED_PH, String.valueOf(Runtime.getRuntime().freeMemory() / 1048576), IConfig.TOTAL_PH, String.valueOf((Runtime.getRuntime().maxMemory() / 1048576))));
        sender.sendMessage(Lang.SYSTEM_DISK_USAGE.rp(IConfig.USED_PH, usedSpace, IConfig.TOTAL_PH, totalSpace));
        sender.sendMessage(Lang.SYSTEM_SERVER_VERSION.rp(IConfig.VERSION_PH, Bukkit.getServer().getBukkitVersion()));
    }
}