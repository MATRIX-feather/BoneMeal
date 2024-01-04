package xiamomc.bonemeal.features.shulker;

import org.bukkit.Material;

public class MaterialUtils
{
    public static boolean isShulkerBox(Material material)
    {
        return material.toString().endsWith("SHULKER_BOX");
    }
}
