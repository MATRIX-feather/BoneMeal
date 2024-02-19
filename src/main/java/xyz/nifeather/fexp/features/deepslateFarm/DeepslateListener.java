package xyz.nifeather.fexp.features.deepslateFarm;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;
import org.slf4j.LoggerFactory;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Bindables.Bindable;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.config.ConfigOption;
import xyz.nifeather.fexp.config.FConfigManager;

public class DeepslateListener extends FPluginObject implements Listener
{
    /*

        public BonemealListener()
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
        config.bind(enabled, ConfigOption.FEAT_DEEPSLATE_FARM);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e)
    {
        if (!enabled.get()) return;

        var pos = e.getBlock().getLocation();
        if (pos.y() >= 0) return;

        var newState = e.getNewState();
        if (newState.getBlock().getType() != Material.LAVA) return;

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
