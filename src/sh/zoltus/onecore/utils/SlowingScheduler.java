package sh.zoltus.onecore.utils;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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

    @NonNull
    @Override
    public synchronized BukkitTask runTaskLater(@NonNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        try {
            return super.runTaskLater(plugin, delay);
        } catch (IllegalArgumentException e) {

        }

        return super.runTaskLater(plugin, delay);
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NonNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NonNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
       return super.runTaskTimer(plugin, delay, period);
    }

    @NonNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NonNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        ticks = delay;
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }
}
