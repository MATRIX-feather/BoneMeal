package xiamomc.bonemeal.features.bonemeal;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xiamomc.bonemeal.MaterialTypes;
import xiamomc.bonemeal.XiaMoExperience;

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

        if (MaterialTypes.isSmallFlower(material))
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
        else if (MaterialTypes.isCoral(material))
        {
            if (!useOnCoral(player, block)) return;
        } else
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

    private final CustomCoralFeature customCoralFeature = new CustomCoralFeature(NoneFeatureConfiguration.CODEC);

    private boolean useOnCoral(Player player, Block clickedBlock)
    {
        // Check player and Level instance
        if (!(player instanceof CraftPlayer craftPlayer)) return false;

        var world = craftPlayer.getHandle().level();

        if (!(world instanceof ServerLevel serverLevel)) return false;

        var bukkitPos = clickedBlock.getLocation();
        var blockLoc = new BlockPos(bukkitPos.getBlockX(), bukkitPos.getBlockY(), bukkitPos.getBlockZ());

        // Check blocks
        var blockBelow = craftPlayer.getWorld().getBlockAt(bukkitPos.clone().add(0, -1, 0));
        if (!MaterialTypes.isCoralPlantable(blockBelow.getType())) return true;

        // Get LevelStem
        var logger = XiaMoExperience.getInstance().getSLF4JLogger();
        Registry<LevelStem> dimensions = null;
        try
        {
            dimensions = serverLevel.getServer().registryAccess()
                    .registryOrThrow(Registries.LEVEL_STEM);
        }
        catch (Throwable t)
        {
            logger.error("Unable to place feature: %s".formatted(t.getLocalizedMessage()));
            t.printStackTrace();
        }

        if (dimensions == null) return false;

        var key = serverLevel.dimension().location();
        LevelStem levelStem = dimensions.get(key);

        if (levelStem == null)
        {
            logger.warn("Unable to place feature since we can't place feature at world '%s'"
                    .formatted(serverLevel.dimension().location().toString()));

            return false;
        }

        // 使用自定义的CoralFeature来允许我们指定要放置的类型
        var material = clickedBlock.getType();
        customCoralFeature.setCoralType(this.toBlockMaterial(material));

        ChunkGenerator generator = levelStem.generator();
        var configuration = NoneFeatureConfiguration.INSTANCE;

        // Place feature
        return customCoralFeature.place(configuration, serverLevel,
                generator, RandomSource.create(), blockLoc);
    }

    @Nullable
    private Material toBlockMaterial(Material input)
    {
        return switch (input)
        {
            case TUBE_CORAL -> Material.TUBE_CORAL_BLOCK;
            case BRAIN_CORAL -> Material.BRAIN_CORAL_BLOCK;
            case BUBBLE_CORAL -> Material.BUBBLE_CORAL_BLOCK;
            case FIRE_CORAL -> Material.FIRE_CORAL_BLOCK;
            case HORN_CORAL -> Material.HORN_CORAL_BLOCK;
            default -> null;
        };
    }
}
