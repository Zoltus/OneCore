package sh.zoltus.onecore.listeners;


import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import sh.zoltus.onecore.OneCore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static sh.zoltus.onecore.configuration.yamls.Config.*;

public record SignColorHandler(OneCore plugin) implements Listener {

    //todo https://github.com/Shopkeepers/Shopkeepers/blob/b776ac4163b24e38e6d7d3fe2b741607cfe94e52/src/main/java/com/nisovin/shopkeepers/util/TextUtils.java
    //
    // for detecting #8f8f8f
    private static final Pattern pattern = Pattern.compile("#\\p{XDigit}{6}");
    // for detecting §x§8§f§8§f§8§f
    private static final Pattern pattern2 = Pattern
            .compile("&x&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}&\\p{XDigit}");
    /**
     * Sign save converts Readable colors to Minecraft colors §.
     *
     * @param e Event
     */
    @EventHandler
    public void onSignSave(SignChangeEvent e) {
        if (e.getPlayer().hasPermission(SIGN_COLOR_PERMISSION.getAsPermission())) {
            for (int line = 0; line < e.getLines().length; line++) {
                String text = e.getLine(line);
                e.setLine(line, toMineHex(text));
            }
        }
    }

    /**
     * Sign open converts Minecraft colors to Readable colors & #hex
     *
     * @param e Event
     * @Permission OneCore.EditSign
     */
    @EventHandler // before sign is opened
    public void onSignEdit(PlayerSwapHandItemsEvent e) {
        Player p = e.getPlayer();
        Block b = p.getTargetBlockExact(5);
        //If sign shift edit is disabled, return
        if (!SIGN_SHIFT_EDIT_ENABLED.getBoolean()
                || !p.hasPermission(SIGN_SHIFT_EDIT_PERMISSION.getAsPermission())
                || !p.isSneaking() || b == null)
            return;
        BlockState state = b.getState();
        if (state instanceof Sign sign) {
            e.setCancelled(true);
            BlockBreakEvent breakEvent = new BlockBreakEvent(b, p);
            Bukkit.getServer().getPluginManager().callEvent(breakEvent);
            if (!breakEvent.isCancelled()) {
                breakEvent.setCancelled(true);
                handleSignOpen(p, sign);
            }
        }
    }

    /**
     * Todo check if multiple players can edit sign
     * Replaces all colorcodes with & so player can edit them and then opens the sign after 2Ticks to prevent bug
     *
     * @param p    Player who views the sign
     * @param sign sign to be opened
     */
    private void handleSignOpen(Player p, Sign sign) {
        IntStream.range(0, sign.getLines().length).forEach(line -> {
            String text = sign.getLine(line);
            sign.setLine(line, toNormal(text));
        });
        sign.update(true);
        //prevents bug with 2tick delay
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.openSign(sign), 2L);
    }


    /**
     * Converts §x§8§f§8§f§8§f to #8f8f8f
     *
     * @param message Message containing symbolized hex (§x§8§f§8§f§8§f)
     * @return returns converted message
     */
    public static String toNormal(String message) {
        message = message.replaceAll("§", "&");
        Matcher matcher = pattern2.matcher(message);
        StringBuilder sb = new StringBuilder(message);
        while (matcher.find()) {
            String hex = matcher.group()
                    .replaceAll("&", "")
                    .replaceFirst("x", "#");
            sb.replace(matcher.start(), matcher.end(), hex);
        }
        return sb.toString();
    }

    /**
     * Converts #8f8f8f to §x§8§f§8§f§8§f
     *
     * @param message Message containing hex (#8f8f8f)
     * @return returns converted message
     */
    public static String toMineHex(String message) {
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder(message);
        while (matcher.find()) {
            String color = matcher.group();
            sb.replace(matcher.start(), matcher.end(), String.valueOf(ChatColor.of(color)));
        }
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }
}
