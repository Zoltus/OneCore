package io.github.zoltus.onecore.utils;

import io.github.zoltus.onecore.OneCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class SlowingScheduler {
    private BukkitTask task;
    private long ticks;
    private final OneCore plugin;
    private final boolean async;
    private final Runnable runnable;

    public SlowingScheduler(OneCore plugin, long ticks, boolean async, Runnable runnable) {
        this.async = async;
        this.ticks = ticks;
        this.plugin = plugin;
        this.runnable = runnable;
        run();
    }

    public void reSchedule(long ticks) {
        if (task != null) {
            task.cancel();
        }
        if (this.ticks != ticks) {
            this.ticks = ticks;
            run();
        }
    }

    private void run() {
        if (async) {
           this.task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, 0, ticks);
        } else {
            this.task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, 0, ticks);
        }
    }
}
