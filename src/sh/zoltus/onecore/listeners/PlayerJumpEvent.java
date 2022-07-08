package sh.zoltus.onecore.listeners;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO
 * SLABS, half blocks
 * boolean boat = false;
 * boolean shulker = false;
 * boat height 0.5625
 * width 1.375
 * List<Entity> nearby = p.getNearbyEntities(1.5, 0.5626, 1.5);
 * <p>
 * 1.9 15w45a Player's jump height is increased from 1.125 blocks to 1.25 blocks. Jump height increased from 1 3⁄16 blocks to 1 4⁄16 blocks.
 * 1.17 20w49a Player's jump while sneaking ignores sculk sensors
 * <a href="https://www.mcpk.wiki/wiki/Vertical_Movement_Formulas">...</a>
 */
public class PlayerJumpEvent implements Listener {
    //https://www.mcpk.wiki/wiki/Vertical_Movement_Formulas
    //https://www.mcpk.wiki/wiki/Status_Effects

    private static final double VELOCITY_GAP = 0.41999998688697815;

    private final List<Material> climbable = new ArrayList<>(Arrays.asList(
            Material.LADDER, Material.VINE,
            Material.SCAFFOLDING, Material.WEEPING_VINES,
            Material.WEEPING_VINES_PLANT, Material.TWISTING_VINES,
            Material.TWISTING_VINES_PLANT, Material.RED_BED,
             Material.CAVE_VINES_PLANT, Material.CAVE_VINES
    ));

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerStatisticIncrementEvent(PlayerStatisticIncrementEvent e) {
        Player p = e.getPlayer();
        /*
         * 0.05840002059942-.....151 ,0.0001 OG
         * Doesnt count as jump if jump stats from climbable block, check if player in on wall,
         * can false positive of exacly perfectly in the wall line
         * Doesnt count slabs
         */
        if (e.getStatistic() == Statistic.JUMP) {
            double velocity = p.getVelocity().getY();
            int groundY = p.getLocation().getBlockY();
            boolean isClimbing = climbable.contains(p.getLocation().getBlock().getType());
            boolean serverGround = !(p.getLocation().getY() % groundY >= 0.05840002059942151);
            boolean clientGround = p.isOnGround();
            //noinspection StatementWithEmptyBody
            if (clientGround && serverGround && velocity >= VELOCITY_GAP && !isClimbing) {
                // p.sendMessage("Jump2");
            }
        }
    }
}