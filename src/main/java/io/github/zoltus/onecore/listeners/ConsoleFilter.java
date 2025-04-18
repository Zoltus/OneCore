package io.github.zoltus.onecore.listeners;

import io.github.zoltus.onecore.data.configuration.OneYml;
import io.github.zoltus.onecore.data.configuration.Yamls;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.ChatColor;
import io.github.zoltus.onecore.data.configuration.yamls.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ConsoleFilter implements Filter {

    @Getter
    private static ConsoleFilter consoleFilter;

    /**
     * Inits custom consoleFilter
     */
    public static ConsoleFilter init() {
        if (consoleFilter == null && Config.USER_CONSOLE_FILTER.getBoolean()) {
            consoleFilter = new ConsoleFilter();
        }
        return consoleFilter;
    }

    private ConsoleFilter() {
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(this);
    }

    /**
     * Checks if message contains keys from hidelist
     * if it does it hides the message from console
     *
     * @param message from console
     * @return result
     */
    private Result checkMessage(String message) {
        return hideListContains(ChatColor.stripColor(message)) ? Result.DENY : Result.ACCEPT;
    }

    private boolean hideListContains(String message) {
        OneYml filters = Yamls.CONSOLE_FILTER.getYml();
        List<String> hideKeys = filters.getOrDefault("Hide", new ArrayList<>());
        if (hideKeys != null && !hideKeys.isEmpty()) {
            for (String hideKey : hideKeys) {
                if (message.contains(hideKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    public State getState() {
        return State.STARTED;
    }

    public void initialize() {
    }

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    @Override
    public boolean stop(long timeout, TimeUnit timeUnit) {
        return false;
    }

    public void start() {
    }

    public void stop() {
    }

    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }

    public Result filter(LogEvent e) {
        return checkMessage(e.getMessage().getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return checkMessage(message.toString());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return checkMessage(message.getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8, Object arg9) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5,
                         Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12,
                         Object arg13) {
        return checkMessage(message);
    }
}