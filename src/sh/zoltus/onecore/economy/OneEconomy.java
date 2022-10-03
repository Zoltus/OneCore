package sh.zoltus.onecore.economy;

import com.google.common.io.Files;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import sh.zoltus.onecore.OneCore;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static sh.zoltus.onecore.data.configuration.yamls.Config.CURRENCY_PLURAL;
import static sh.zoltus.onecore.data.configuration.yamls.Config.CURRENCY_SINGULAR;

public final class OneEconomy implements Economy {
    @Getter
    private static final ConcurrentHashMap<UUID, Double> balances = new ConcurrentHashMap<>();

    private final Logger logger;
    private final OneCore plugin;
    @Getter private final String name = "OneEconomy";

    public boolean isEnabled() {
        return (plugin.getVault() != null);
    }

    //Economy
    public String currencyNamePlural() {
        return CURRENCY_PLURAL.getString();
    }

    public String currencyNameSingular() {
        return CURRENCY_SINGULAR.getString();
    }

    public OneEconomy(OneCore plugin) {
        this.plugin = plugin;
        this.logger = createLogger();
    }

    private Logger createLogger() {
        try {
            File logFile = new File(plugin.getDataFolder().getAbsolutePath() + "/logs/Economy.log");
            Files.createParentDirs(logFile);
            Files.touch(logFile);
            Logger logger = Logger.getLogger(getName());
            logger.setUseParentHandlers(false);
            LogFormatter formatter = new LogFormatter();
            FileHandler handler = new FileHandler(logFile.getAbsolutePath());
            handler.setFormatter(formatter);
            logger.addHandler(handler);
            return logger;
        } catch (SecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String message) {
        logger.info(message);
    }

    private static class LogFormatter extends Formatter {
        private final Date dat = new Date();
        private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

        public synchronized String format(LogRecord record) {
            dat.setTime(record.getMillis());
            return df.format(dat) + " : " + record.getLevel().getName() + " : " + record.getMessage() + "\n";

        }
    }

    /**
     * Format amount into a human readable String This provides translation into economy specific formatting to improve consistency between plugins.
     *
     * @param summ money
     * @return Human readable string describing amount
     */
    public String format(double summ) {
        return String.valueOf(summ);
    }

    /**
     * todo to settings
     * Some economy plugins round off after a certain number of digits. This function returns the number of digits the plugin keeps or -1 if no rounding occurs.
     *
     * @return roundedAmount
     */
    public int fractionalDigits() {
        return 3;
    }

    @Override
    public double getBalance(String player) {
        return getBalance(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public double getBalance(OfflinePlayer offP) {
        return balances.get(offP.getUniqueId());
        //  return balances.compute(offP.getUniqueId(), (k, v) -> v == null ? 0 : v);
    }

    @Override
    public boolean has(String player, double amount) {
        return has(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public boolean has(OfflinePlayer offP, double amount) {
        return getBalance(offP) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offP, double amount) {
        if (!has(offP, amount)) {
            return new EconomyResponse(1, 1, EconomyResponse.ResponseType.FAILURE, "failed");
        } else {
            log("Withdrawing " + amount + " from " + offP.getName());
            double newBalance = getBalance(offP) - amount;
            balances.put(offP.getUniqueId(), newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "sucsess withdraw");
        }
    }

    @Override
    public EconomyResponse depositPlayer(String player, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(player), amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offP, double amount) {
        double newBalance = getBalance(offP) + amount;
        if (Double.isInfinite(newBalance) || Double.isNaN(newBalance)) {
            return new EconomyResponse(1, 1, EconomyResponse.ResponseType.FAILURE, "failed deposit");
        } else {
            log("Depositing " + amount + " to " + offP.getName());
            balances.put(offP.getUniqueId(), newBalance);
            return new EconomyResponse(amount, newBalance, EconomyResponse.ResponseType.SUCCESS, "sucsess deposit");
        }
    }

    @Override
    public boolean hasAccount(String player) {
        return hasAccount(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public boolean hasAccount(OfflinePlayer offP) {
        return balances.containsKey(offP.getUniqueId());
    }

    @Override
    public boolean createPlayerAccount(String player) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(player));
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        balances.put(player.getUniqueId(), 0D);
        return true;
    }

    @Override
    public boolean createPlayerAccount(String player, String world) {
        return createPlayerAccount(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return createPlayerAccount(player);
    }

    @Override
    public boolean hasAccount(String player, String world) {
        return hasAccount(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String player, String world) {
        return getBalance(Bukkit.getOfflinePlayer(player), world);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String player, String world, double amount) {
        return has(Bukkit.getOfflinePlayer(player), world, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String player, String world, double amount) {
        return withdrawPlayer(Bukkit.getOfflinePlayer(player), world, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String player, String world, double amount) {
        return depositPlayer(Bukkit.getOfflinePlayer(player), world, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return depositPlayer(player, amount);
    }

    //Todo bank support
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse createBank(String bank, String player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse createBank(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse deleteBank(String bank) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse bankBalance(String bank) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse bankHas(String bank, double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse bankWithdraw(String bank, double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse bankDeposit(String bank, double amount) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse isBankOwner(String bank, String player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse isBankOwner(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse isBankMember(String bank, String player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public EconomyResponse isBankMember(String bank, OfflinePlayer player) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "OneCore does not support banks!");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    public OneCore plugin() {
        return plugin;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OneEconomy) obj;
        return Objects.equals(this.plugin, that.plugin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plugin);
    }

    @Override
    public String toString() {
        return "OneEconomy[" +
                "plugin=" + plugin + ']';
    }


}
