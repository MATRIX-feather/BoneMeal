package xiamomc.bonemeal;

import org.bukkit.Material;

public class MaterialTypes
{
    public static boolean isCoralPlantable(Material material)
    {
        return material == Material.SAND
                || material == Material.RED_SAND
                || material == Material.GRASS_BLOCK
                || material == Material.DIRT
                || material == Material.GRAVEL;
    }

    public static boolean isCoral(Material material)
    {
        return material == Material.TUBE_CORAL
                || material == Material.BRAIN_CORAL
                || material == Material.BUBBLE_CORAL
                || material == Material.FIRE_CORAL
                || material == Material.HORN_CORAL;
    }

    public static boolean isSmallFlower(Material material)
    {
        return material == Material.DANDELION
                || material == Material.POPPY
                || material == Material.BLUE_ORCHID
                || material == Material.ALLIUM
                || material == Material.AZURE_BLUET
                || material == Material.RED_TULIP
                || material == Material.ORANGE_TULIP
                || material == Material.WHITE_TULIP
                || material == Material.PINK_TULIP
                || material == Material.OXEYE_DAISY
                || material == Material.CORNFLOWER
                || material == Material.LILY_OF_THE_VALLEY;
    }
}
