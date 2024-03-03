package xyz.nifeather.fexp.features.bonemeal;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Dispenser;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.slf4j.Logger;
import xiamomc.pluginbase.Annotations.Initializer;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.utilities.NmsRecord;

import java.util.List;

public class BonemealListener extends FPluginObject implements Listener
{
    private final BonemealHandler handler = new BonemealHandler();

    private final Logger logger = FeatherExperience.getInstance().getSLF4JLogger();

    @EventHandler
    public void onDispense(BlockDispenseEvent e)
    {
        var sourceBlock = e.getBlock();

        if (sourceBlock.getType() != Material.DISPENSER)
            return;

        var dispenser = (Dispenser)sourceBlock.getState();

        if (!(dispenser.getBlockData() instanceof Directional directional))
        {
            logger.warn("A dispenser has a BlockData that's not a Directional?!");
            return;
        }

        var facing = directional.getFacing();
        var targetLoc = dispenser.getLocation();
        targetLoc.setX(targetLoc.x() + facing.getModX());
        targetLoc.setY(targetLoc.y() + facing.getModY());
        targetLoc.setZ(targetLoc.z() + facing.getModZ());

        var item = e.getItem();
        var targetBlock = sourceBlock.getWorld().getBlockAt(targetLoc);

        if (!handler.onBonemeal(item, targetBlock))
            return;

        var world = targetBlock.getWorld();
        var location = targetBlock.getLocation().add(0.5, 0.5, 0.5);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 20, 0.25, 0.25, 0.25);

        e.setItem(new ItemStack(Material.AIR, 64));

        for (ItemStack i : dispenser.getInventory())
        {
            if (i == null) continue;

            if (i.getType() != Material.BONE_MEAL) continue;

            i.setAmount(i.getAmount() - 1);

            if (i.getAmount() <= 0)
                i.setType(Material.AIR);

            break;
        }
    }

    private final List<Player> rightClicked = new ObjectArrayList<>();

    @Initializer
    private void load()
    {
        this.addSchedule(this::update);
    }

    private void update()
    {
        this.addSchedule(this::update);

        rightClicked.clear();
    }

    @EventHandler
    public void onUseItem(PlayerInteractEvent e)
    {
        // If not clicking on a block, skip
        if (!e.hasBlock()) return;

        // Check action
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        var player = e.getPlayer();

        // Get block and its material etc.
        var block = e.getClickedBlock();
        assert block != null;

        var item = e.getItem();

        if (item == null || item.getType() != Material.BONE_MEAL)
            return;

        if (rightClicked.contains(player))
            return;

        if (!handler.onBonemeal(item, block))
            return;
        else
        {
            var gamemode = player.getGameMode();

            if (gamemode == GameMode.SURVIVAL || gamemode == GameMode.ADVENTURE)
                item.setAmount(item.getAmount() - 1);
        }

        // 操作成功，向列表添加此玩家
        rightClicked.add(player);

        // Add to block
        if (e.getHand() == EquipmentSlot.HAND)
            rightClicked.add(player);

        // Determine which hand to swing
        var hand = e.getHand();

        if (hand == EquipmentSlot.HAND)
            player.swingMainHand();
        else
            player.swingOffHand();

        // Spawn particle and play sound
        var world = block.getWorld();
        var location = block.getLocation().add(0.5, 0.5, 0.5);
        world.spawnParticle(Particle.VILLAGER_HAPPY, location, 20, 0.25, 0.25, 0.25);
        world.playSound(location, Sound.ITEM_BONE_MEAL_USE, 1, 1);
    }
}
