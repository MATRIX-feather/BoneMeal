package xiamomc.bonemeal;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Bonemeal extends JavaPlugin
{
    public static final List<Material> VALID_FLOWERS = List.of
            (
                    Material.DANDELION,
                    Material.POPPY,
                    Material.BLUE_ORCHID,
                    Material.ALLIUM,
                    Material.AZURE_BLUET,
                    Material.RED_TULIP,
                    Material.ORANGE_TULIP,
                    Material.WHITE_TULIP,
                    Material.PINK_TULIP,
                    Material.OXEYE_DAISY,
                    Material.CORNFLOWER,
                    Material.LILY_OF_THE_VALLEY
            );

    @Override
    public void onEnable()
    {
        super.onEnable();

        Bukkit.getPluginManager().registerEvents(new BonemealListener(), this);
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }
}
