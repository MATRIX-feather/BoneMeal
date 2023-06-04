package xiamomc.bonemeal;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class BonemealListener implements Listener
{
    @EventHandler
    public void onUseItem(PlayerInteractEvent e)
    {
        // If not clicking on a block, skip
        if (!e.hasBlock()) return;

        // Check action
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Get block and its material etc.
        var block = e.getClickedBlock();
        assert block != null;

        var material = block.getType();
        var world = block.getWorld();

        // Check and consume item
        var player = e.getPlayer();
        var playerGamemode = player.getGameMode();
        var item = e.getItem();

        if (item == null) return;
        if (item.getType() != Material.BONE_MEAL) return;

        if (Bonemeal.VALID_FLOWERS.contains(material))
        {
            // Spawn item for flowers
            world.dropItemNaturally(block.getLocation(), new ItemStack(material));
        }
        else if (material == Material.SUGAR_CANE)
        {
            // Check for total height
            var searchLoc = block.getLocation().add(0.5, 0, 0.5);
            var caneHeight = 0;

            // Check for lower blocks
            for (int i = -1; i > -3; i--)
            {
                var pos = searchLoc.clone().add(0, i, 0);

                var currentBlock = world.getBlockAt(pos);
                if (currentBlock.getType() == Material.SUGAR_CANE)
                    caneHeight++;
                else
                    break;
            }

            // Check for upper blocks
            Block upperBlock = null;

            for (int i = 0; i < 3; i++)
            {
                var pos = searchLoc.clone().add(0, i, 0);

                var currentBlock = world.getBlockAt(pos);
                if (currentBlock.getType() == Material.SUGAR_CANE)
                    caneHeight++;
                else
                {
                    upperBlock = currentBlock;
                    break;
                }
            }

            if (caneHeight >= 3) return;

            // Set block
            upperBlock = upperBlock == null ? world.getBlockAt(searchLoc) : upperBlock;

            if (upperBlock.getType().isAir())
                upperBlock.setType(Material.SUGAR_CANE);
            else
                return;
        }
        else
        {
            // Not flower or sugar cane, return
            return;
        }

        if (playerGamemode == GameMode.SURVIVAL || playerGamemode == GameMode.ADVENTURE)
            item.setAmount(item.getAmount() - 1);

        // Determine which hand to swing
        var hand = e.getHand();

        if (hand == EquipmentSlot.HAND)
            player.swingMainHand();
        else
            player.swingOffHand();

        // Spawn particle
        var location = block.getLocation().add(0.5, 0.5, 0.5);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 20, 0.25, 0.25, 0.25);

        // Play sound
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 1, 1);
    }
}
