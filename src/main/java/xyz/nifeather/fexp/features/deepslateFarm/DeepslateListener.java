package xyz.nifeather.fexp.features.deepslateFarm;

import com.destroystokyo.paper.event.entity.EntityPathfindEvent;
import com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.event.entity.SlimeWanderEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.world.GenericGameEvent;
import org.bukkit.plugin.RegisteredListener;
import org.slf4j.LoggerFactory;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.config.FConfigManager;

public class DeepslateListener extends FPluginObject implements Listener
{

    /*
public DeepslateListener()
{
    RegisteredListener registeredListener = new RegisteredListener(this, (listener, event) -> onEvent(event), EventPriority.NORMAL, FeatherExperience.getPlugin(FeatherExperience.class), false);
    for (HandlerList handler : HandlerList.getHandlerLists())
        handler.register(registeredListener);
}

private void onEvent(Event event)
{
    if (event instanceof ServerTickEndEvent
            || event instanceof EntityMoveEvent
            || event instanceof GenericGameEvent
            || event instanceof HopperInventorySearchEvent
            || event instanceof SlimeWanderEvent
            || event instanceof PreCreatureSpawnEvent
            || event instanceof PlayerNaturallySpawnCreaturesEvent
            || event instanceof ServerTickStartEvent
            || event instanceof EntityPathfindEvent)
    {
        return;
    }

    if (!(event instanceof BlockEvent))
    {
        return;
    }

    if (event instanceof BlockPhysicsEvent)
    {
        return;
    }
    var logger = LoggerFactory.getLogger("FeatherExperience");
    logger.info("Event! " + event);
}

*/
    private final Bindable<Boolean> enabled = new Bindable<>(false);

    @Initializer
    private void load(FConfigManager config)
    {
        config.bind(enabled, FConfigOptions.FEAT_DEEPSLATE_FARM);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e)
    {
        if (!enabled.get()) return;

        var pos = e.getBlock().getLocation();
        if (pos.y() >= 0) return;

        var newState = e.getNewState();
        var newStateType = newState.getBlock().getType();
        if (newStateType != Material.LAVA && newStateType != Material.WATER) return;

        var blockType = e.getBlock().getType();
        if (blockType != Material.LAVA && blockType != Material.WATER) return;

        if (newState.getType() == Material.COBBLESTONE)
        {
            newState.setType(Material.COBBLED_DEEPSLATE);
        }
        else if (newState.getType() == Material.STONE)
        {
            newState.setType(Material.DEEPSLATE);
        }
    }

}
