package sh.zoltus.onecore.utils;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import sh.zoltus.onecore.OneCore;

public class SlowingScheduler extends BukkitRunnable {

    long ticks;

    @Override
    public void run() {
    }

    public void changeSpeed(OneCore pl, long ticks, boolean async) {
        this.cancel();
        if (this.ticks != ticks) {
            if (async) {
                this.runTaskTimerAsynchronously(pl, 0, ticks);
            } else {
                this.runTaskTimer(pl, 0, ticks);
            }
        }
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        try {
            return super.runTaskLater(plugin, delay);
        } catch (IllegalArgumentException e) {

        }

        return super.runTaskLater(plugin, delay);
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
       return super.runTaskTimer(plugin, delay, period);
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }
}
