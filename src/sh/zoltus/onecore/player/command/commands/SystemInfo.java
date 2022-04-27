package sh.zoltus.onecore.player.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import sh.zoltus.onecore.player.command.ApiCommand;
import sh.zoltus.onecore.player.command.IOneCommand;

import java.io.File;

import static sh.zoltus.onecore.configuration.yamls.Commands.*;
import static sh.zoltus.onecore.configuration.yamls.Lang.*;

public class SystemInfo implements IOneCommand {

    @Override
    public ApiCommand[] getCommands() {
        return new ApiCommand[]{
                command(SYSTEM_LABEL)
                        .withPermission(SYSTEM_PERMISSION)
                        .withAliases(SYSTEM_ALIASES)
                        .executes((sender, args) -> {
                    sendSystemInfo(sender);
                })
        };
    }


    private void sendSystemInfo(CommandSender sender) {
        File diskPartition = new File("/");
        long totalSpace = diskPartition.getTotalSpace() / (1024 * 1024 * 1024);
        long freeSpace = diskPartition.getUsableSpace() / (1024 * 1024 * 1024);
        long usedSpace = totalSpace - freeSpace;

        sender.sendMessage(SYSTEM_OS.rp(SYSTEM_PH, System.getProperty("os.name") + " " + System.getProperty("os.arch")));
        sender.sendMessage(SYSTEM_VERSION.rp(VERSION_PH, System.getProperty("os.version")));
        sender.sendMessage(SYSTEM_USERNAME.rp(USERNAME_PH, System.getProperty("user.name")));
        sender.sendMessage(SYSTEM_PROCESSORS.rp(PROCESSORS_PH, String.valueOf(Runtime.getRuntime().availableProcessors())));
        sender.sendMessage(SYSTEM_JAVA_VERSION.rp(VERSION_PH, System.getProperty("java.version")));
        sender.sendMessage(SYSTEM_RAM_USAGE.rp(USED_PH, String.valueOf(Runtime.getRuntime().freeMemory() / 1048576), TOTAL_PH, String.valueOf((Runtime.getRuntime().maxMemory() / 1048576))));
        sender.sendMessage(SYSTEM_DISK_USAGE.rp(USED_PH, usedSpace, TOTAL_PH, totalSpace));
        sender.sendMessage(SYSTEM_SERVER_VERSION.rp(VERSION_PH, Bukkit.getServer().getBukkitVersion()));
    }


}