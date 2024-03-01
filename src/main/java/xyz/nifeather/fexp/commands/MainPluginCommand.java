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
import xyz.nifeather.fexp.messages.strings.CommandStrings;
import xyz.nifeather.fexp.messages.strings.HelpStrings;
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
        return HelpStrings.mmorphDescription();
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

    private final List<ISubCommand> subCommands = new ObjectArrayList<>();

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
            allSuccess = register(cmd) && allSuccess;
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
        registerRange(new OptionSubCommand(), new ReloadSubCommand(), new HelpSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        var result = super.onCommand(sender, command, label, args);

        if (!result)
            sender.sendMessage(MessageUtils.prefixes(sender, CommandStrings.commandNotFoundString()));

        return true;
    }
}
