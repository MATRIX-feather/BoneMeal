package xiamomc.bonemeal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class Bonemeal extends JavaPlugin
{
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
