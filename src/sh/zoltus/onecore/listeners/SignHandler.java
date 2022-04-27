package sh.zoltus.onecore.listeners;


import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import sh.zoltus.onecore.OneCore;
import sh.zoltus.onecore.utils.ColorUtils;

import static sh.zoltus.onecore.configuration.yamls.Config.SIGN_COLOR_PERMISSION;
import static sh.zoltus.onecore.configuration.yamls.Config.SIGN_EDIT_PERMISSION;

public record SignHandler(OneCore plugin) implements Listener {

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
                e.setLine(line, ColorUtils.colorizeAll(text));
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

        if (!p.hasPermission(SIGN_EDIT_PERMISSION.getAsPermission()) || !p.isSneaking() || b == null)
            return;

        BlockState state = b.getState();
        if (state instanceof org.bukkit.block.Sign sign) {
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
    private void handleSignOpen(Player p, org.bukkit.block.Sign sign) {
        for (int line = 0; line < sign.getLines().length; line++) {
            String text = sign.getLine(line);
            sign.setLine(line, ColorUtils.deColorizeAll(text));
        }
        sign.update(true);
        //prevents bug with 2tick delay
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> p.openSign(sign), 2L);
    }
}
