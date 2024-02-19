package xyz.nifeather.fexp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.features.bonemeal.BonemealListener;
import xyz.nifeather.fexp.features.deepslateFarm.DeepslateListener;
import xyz.nifeather.fexp.features.shulker.ShulkerListener;

public final class FeatherExperience extends XiaMoJavaPlugin
{
    public static FeatherExperience getInstance()
    {
        return instance;
    }

    private static FeatherExperience instance;

    @Override
    public String getNameSpace()
    {
        return "fexp";
    }

    public FeatherExperience()
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
