package xyz.nifeather.fexp.commands;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Command.SubCommandHandler;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.FeatherExperience;
import xyz.nifeather.fexp.commands.builder.CommandBuilder;
import xyz.nifeather.fexp.utilities.MessageUtils;

import java.util.Arrays;
import java.util.List;

public class MainPluginCommand extends SubCommandHandler<FeatherExperience>
{
    @Override
    public String getCommandName()
    {
        return "fexp";
    }

    private final List<String> aliases = List.of("fe");

    @Override
    public List<String> getAliases()
    {
        return aliases;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return new FormattableMessage(plugin, "_");
    }

    @Override
    public List<ISubCommand> getSubCommands()
    {
        return subCommands;
    }

    @Override
    public List<FormattableMessage> getNotes()
    {
        return List.of();
    }

    @Override
    protected String getPluginNamespace()
    {
        return FeatherExperience.namespace();
    }


    private List<ISubCommand> subCommands = new ObjectArrayList<>();

    public boolean register(ISubCommand cmd)
    {
        if (subCommands.stream().anyMatch(c -> c.getCommandName().equalsIgnoreCase(cmd.getCommandName())))
            return false;

        subCommands.add(cmd);
        return true;
    }

    public boolean registerRange(List<ISubCommand> cmdList)
    {
        var allSuccess = true;

        for (ISubCommand cmd : cmdList)
        {
            allSuccess = registerRange(cmd) && allSuccess;
        }

        return allSuccess;
    }

    public boolean registerRange(ISubCommand... cmdArray)
    {
        return registerRange(Arrays.stream(cmdArray).toList());
    }

    @Initializer
    private void load()
    {
        register(new OptionSubCommand());
    }
}
