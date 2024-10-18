package io.github.zoltus.onecore.utils;

import io.github.zoltus.onecore.OneCore;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;

public class SpeedChangeScheduler {
    private BukkitTask task;
    private final OneCore plugin;
    private final boolean async;
    private final Consumer<SpeedChangeScheduler> consumer;

    private SpeedChangeScheduler(OneCore plugin, long ticks, long delay, boolean async, Consumer<SpeedChangeScheduler> consumer) {
        this.async = async;
        this.plugin = plugin;
        this.consumer = consumer;
        scheduleTask(ticks, delay);
    }

    public static SpeedChangeScheduler run(OneCore plugin, long ticks, long delay, boolean async, Consumer<SpeedChangeScheduler> consumer) {
        return new SpeedChangeScheduler(plugin, ticks, delay, async, consumer);
    }

    public void reSchedule(long ticks, long delay) {
        if (task != null) {
            task.cancel();
        }
        scheduleTask(ticks, delay);
    }

    private void scheduleTask(long ticks, long delay) {
        Runnable runnable = () -> consumer.accept(this);
        this.task = async
                ? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, ticks)
                : Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, ticks);
    }
}