package xyz.nifeather.fexp.features.bonemeal;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;

public interface IBonemealHandler
{
    /**
     * Unused.
     * @return The list of blocks that this handler supports
     */
    public default List<Material> supportedBlocks()
    {
        return List.of();
    }

    /**
     * @return The identifier of this handler
     */
    public String getIdentifier();

    /**
     * Called when bone meal is used on the given block
     * @param block The block that been interacted with bone meal
     * @return True if we should consume the bone meal, otherwise False
     */
    public boolean onBonemeal(Block block);
}
