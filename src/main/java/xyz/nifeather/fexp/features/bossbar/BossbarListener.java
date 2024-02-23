package xyz.nifeather.fexp.features.bossbar;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nifeather.fexp.FPluginObject;

public class BossbarListener extends FPluginObject implements Listener
{
    private final BossbarManager manager = new BossbarManager();

    @EventHandler
    public void onEntityAdd(EntityAddToWorldEvent e)
    {
        var entity = e.getEntity();
        if (!(entity instanceof Warden warden)) return;

        manager.register(warden);
    }

    @EventHandler
    public void onEntityRemove(EntityRemoveFromWorldEvent e)
    {
        if (!(e.getEntity() instanceof Warden warden)) return;

        manager.drop(warden);
    }
}
