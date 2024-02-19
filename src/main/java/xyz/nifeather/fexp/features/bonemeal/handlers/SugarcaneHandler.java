package xyz.nifeather.fexp.features.bonemeal.handlers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.MaterialTypes;
import xyz.nifeather.fexp.config.ConfigOption;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.features.bonemeal.IBonemealHandler;

public class SugarcaneHandler extends FPluginObject implements IBonemealHandler
{
    /**
     * @return The identifier of this handler
     */
    @Override
    public String getIdentifier()
    {
        return "sugarcane";
    }

    /**
     * Called when bone meal is used on the given block
     *
     * @param block The block that been interacted with bone meal
     * @return True if we should consume the bone meal, otherwise False
     */
    @Override
    public boolean onBonemeal(Block block)
    {
        if (!enableSugarcane.get() || block.getType() != Material.SUGAR_CANE) return false;

        var world = block.getWorld();

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

        if (caneHeight >= 3) return false;

        // Set block
        upperBlock = upperBlock == null ? world.getBlockAt(searchLoc) : upperBlock;

        if (upperBlock.getType().isAir())
        {
            upperBlock.setType(Material.SUGAR_CANE);
            return true;
        }

        return false;
    }

    private final Bindable<Boolean> enableSugarcane = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enableSugarcane, ConfigOption.FEAT_BONEMEAL_ON_SUGARCANE);
    }
}
