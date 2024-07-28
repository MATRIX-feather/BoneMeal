package xyz.nifeather.fexp.commands;

import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xyz.nifeather.fexp.FeatherExperience;

import java.util.List;

public class FCommandHelper extends CommandHelper<FeatherExperience>
{
    private final List<IPluginCommand> commands = List.of(
            new MainPluginCommand(),
            new MainTogglePvPCommand()
    );

    @Override
    public List<IPluginCommand> getCommands()
    {
        return commands;
    }

    @Override
    protected XiaMoJavaPlugin getPlugin()
    {
        return FeatherExperience.getInstance();
    }

    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }
}
