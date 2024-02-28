package xyz.nifeather.fexp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import xiamomc.pluginbase.PluginObject;

public class FPluginObject extends PluginObject<FeatherExperience>
{
    protected void scheduleOn(Entity entity, Runnable r)
    {
        entity.getScheduler().execute(plugin, r, null, 1);
    }

    protected void scheduleOnRegion(Entity entity, Runnable r)
    {
        Bukkit.getRegionScheduler().execute(
                FeatherExperience.getInstance(),
                entity.getLocation(),
                r);
    }

    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }
}
