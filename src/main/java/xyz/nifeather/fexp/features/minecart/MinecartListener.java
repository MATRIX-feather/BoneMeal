package xyz.nifeather.fexp.features.minecart;

import org.bukkit.Material;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import xyz.nifeather.fexp.FPluginObject;

public class MinecartListener extends FPluginObject implements Listener
{
    private final MinecartConfigHandler configHandler = new MinecartConfigHandler();

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e)
    {
        if (!configHandler.enabled()) return;

        var vehicle = e.getVehicle();
        if (!(vehicle instanceof Minecart minecart)) return;

        var railBlock = vehicle.getLocation().getBlock();
        var railType = railBlock.getType();

        if (railType != Material.RAIL
                && railType != Material.POWERED_RAIL
                && railType != Material.DETECTOR_RAIL
                && railType != Material.ACTIVATOR_RAIL)
        {
            return;
        }

        var blockUnder = railBlock.getRelative(0, -1, 0);
        var speed = configHandler.getDefaultMaxSpeed() * configHandler.getSpeedMultiplier(blockUnder.getType());

        minecart.setMaxSpeed(speed);
    }
}
