package xiamomc.bonemeal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.bonemeal.features.bonemeal.BonemealListener;
import xiamomc.bonemeal.features.deepslateFarm.DeepslateListener;
import xiamomc.bonemeal.features.shulker.ShulkerListener;

public final class XiaMoExperience extends JavaPlugin
{
    public static XiaMoExperience getInstance()
    {
        return instance;
    }

    private static XiaMoExperience instance;

    public XiaMoExperience()
    {
        instance = this;
    }

    @Override
    public void onEnable()
    {
        super.onEnable();

        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new BonemealListener(), this);
        pluginManager.registerEvents(new DeepslateListener(), this);
        pluginManager.registerEvents(shulkerListener = new ShulkerListener(), this);
    }

    private ShulkerListener shulkerListener;

    @Override
    public void onDisable()
    {
        super.onDisable();

        if (shulkerListener != null)
            shulkerListener.onDisable();
    }
}
