package sh.zoltus.onecore.listeners;

import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.ChatColor;
import sh.zoltus.onecore.configuration.OneYml;
import sh.zoltus.onecore.configuration.Yamls;
import sh.zoltus.onecore.configuration.yamls.Config;

import java.util.ArrayList;
import java.util.List;

public class ConsoleFilter implements Filter {

    private static final OneYml filters = Yamls.Console_Filter.getYml();

    @Getter
    private static ConsoleFilter consoleFilter;

    /**
     * Inits custom consoleFilter
     */
    //todo clean up
    public static ConsoleFilter init() {
        if (consoleFilter != null) {
            return consoleFilter;
        } if (Config.USER_CONSOLE_FILTER.getBoolean()) {
            consoleFilter = new ConsoleFilter();
        }
        return consoleFilter;
    }

    private ConsoleFilter() {
        ((Logger) LogManager.getRootLogger()).addFilter(this);
        filters.getOrSetDefault("Data", new ArrayList<>());
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

    //todo benchmark, improve
    private boolean hideListContains(String message) {
        List<String> hideKeys = (List<String>) filters.getList("Hide");
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