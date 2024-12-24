package xyz.nifeather.fexp.commands;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.commands.brigadier.IConvertibleBrigadier;

import java.util.List;

public class FCommandHelper extends FPluginObject
{
    private final List<IConvertibleBrigadier> commands = List.of(
            new MainPluginCommand(),
            new MainTogglePvPCommand()
    );

    public void register(ReloadableRegistrarEvent<@NotNull Commands> event)
    {
        var registrar = event.registrar();

        for (var brigadierConvertable : commands)
            brigadierConvertable.register(registrar);
    }

    public List<IConvertibleBrigadier> children()
    {
        return new ObjectArrayList<>(commands);
    }

    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }
}
