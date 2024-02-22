package xyz.nifeather.fexp.commands;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Configuration.ConfigOption;
import xiamomc.pluginbase.Exceptions.NullDependencyException;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xyz.nifeather.fexp.CommonPermissions;
import xyz.nifeather.fexp.FPluginObject;
import xyz.nifeather.fexp.commands.builder.CommandBuilder;
import xyz.nifeather.fexp.config.FConfigManager;
import xyz.nifeather.fexp.config.FConfigOptions;
import xyz.nifeather.fexp.messages.strings.CommandStrings;
import xyz.nifeather.fexp.messages.strings.TypesString;
import xyz.nifeather.fexp.utilities.BindableUtils;
import xyz.nifeather.fexp.utilities.MessageUtils;

import java.util.List;
import java.util.function.Function;

public class OptionSubCommand extends FPluginObject implements ISubCommand
{
    @Override
    public @NotNull String getCommandName()
    {
        return "option";
    }

    @Resolved(shouldSolveImmediately = true)
    private FConfigManager config;

    public OptionSubCommand()
    {
        subCommands.add(getToggle("bonemeal.coral", FConfigOptions.FEAT_BONEMEAL_ON_CORAL));
        subCommands.add(getToggle("bonemeal.flower", FConfigOptions.FEAT_BONEMEAL_ON_FLOWER));
        subCommands.add(getToggle("bonemeal.sugarcane", FConfigOptions.FEAT_BONEMEAL_ON_SUGARCANE));

        subCommands.add(getToggle("shulkerbox.open", FConfigOptions.FEAT_OPEN_SHULKERBOX));

        subCommands.add(getToggle("deepslate.farm", FConfigOptions.FEAT_DEEPSLATE_FARM));
    }

    private <T> ISubCommand getGeneric(String name, ConfigOption<T> option,
                                       @Nullable FormattableMessage displayName, Class<T> targetClass,
                                       Function<String, T> func, String typeName)
    {
        return getGeneric(name, option,
                displayName, targetClass, func, new FormattableMessage(plugin, typeName));
    }

    private ISubCommand getList(String optionName, ConfigOption<?> option,
                                @Nullable FormattableMessage displayName)
    {
        var targetDisplay = displayName == null ? new FormattableMessage(plugin, optionName) : displayName;

        var bindableList = config.getBindableList(String.class, option);

        return CommandBuilder.builder().startNew()
                .name(optionName)
                .permission(CommonPermissions.optionCommand)
                .executes((sender, args) ->
                {
                    if (args.isEmpty())
                    {
                        var displayValue = BindableUtils.bindableListToString(bindableList);
                        sender.sendMessage(MessageUtils.prefixes(sender,
                                CommandStrings.optionValueString()
                                        .withLocale(MessageUtils.getLocale(sender))
                                        .resolve("what", targetDisplay, null)
                                        .resolve("value", displayValue)));

                        return true;
                    }

                    if (args.size() < 2)
                    {
                        sender.sendMessage(MessageUtils.prefixes(sender,
                                CommandStrings.listNoEnoughArguments()
                                        .withLocale(MessageUtils.getLocale(sender))));

                        return true;
                    }

                    var operation = args.get(0);
                    if (operation.equalsIgnoreCase("add"))
                    {
                        var value = args.get(1);
                        try
                        {
                            bindableList.add(value);

                            //workaround: List的add方法传入非null时永远返回true
                            if (bindableList.contains(value))
                            {
                                sender.sendMessage(MessageUtils.prefixes(sender,
                                        CommandStrings.listAddSuccess()
                                                .withLocale(MessageUtils.getLocale(sender))
                                                .resolve("value", value)
                                                .resolve("option", optionName)));
                            }
                            else
                            {
                                sender.sendMessage(MessageUtils.prefixes(sender,
                                        CommandStrings.listAddFailUnknown()
                                                .withLocale(MessageUtils.getLocale(sender))
                                                .resolve("value", value)
                                                .resolve("option", optionName)));
                            }
                        }
                        catch (Throwable t)
                        {
                            sender.sendMessage(MessageUtils.prefixes(sender,
                                    CommandStrings.listAddFailUnknown()
                                            .withLocale(MessageUtils.getLocale(sender))
                                            .resolve("value", value)
                                            .resolve("option", optionName)));

                            logger.error("Error adding option to bindable list: " + t.getMessage());
                        }

                        return true;
                    }
                    else if (operation.equalsIgnoreCase("remove"))
                    {
                        var value = args.get(1);
                        var listChanged = bindableList.remove(value);

                        if (listChanged)
                        {
                            sender.sendMessage(MessageUtils.prefixes(sender,
                                    CommandStrings.listRemoveSuccess()
                                            .withLocale(MessageUtils.getLocale(sender))
                                            .resolve("value", value)
                                            .resolve("option", optionName)));
                        }
                        else
                        {
                            sender.sendMessage(MessageUtils.prefixes(sender,
                                    CommandStrings.listRemoveFailUnknown()
                                            .withLocale(MessageUtils.getLocale(sender))
                                            .resolve("value", value)
                                            .resolve("option", optionName)));
                        }

                        return true;
                    }
                    else
                    {
                        sender.sendMessage(MessageUtils.prefixes(sender,
                                CommandStrings.unknownOperation()
                                        .withLocale(MessageUtils.getLocale(sender))
                                        .resolve("operation", operation)));

                        return true;
                    }
                })
                .buildAll().get(0);
    }

    private <T> ISubCommand getGeneric(String name, ConfigOption<T> option,
                                       @Nullable FormattableMessage displayName, Class<T> targetClass,
                                       Function<String, T> func, FormattableMessage typeName)
    {
        var targetDisplay = displayName == null ? new FormattableMessage(plugin, name) : displayName;

        return CommandBuilder.builder().startNew()
                .name(name)
                .permission(CommonPermissions.optionCommand)
                .executes((sender, args) ->
                {
                    if (args.isEmpty())
                    {
                        sender.sendMessage(MessageUtils.prefixes(sender,
                                CommandStrings.optionValueString()
                                        .withLocale(MessageUtils.getLocale(sender))
                                        .resolve("what", targetDisplay, null)
                                        .resolve("value", config.get(targetClass, option) + "")));

                        return true;
                    }

                    T value = null;

                    try
                    {
                        value = func.apply(args.get(0));

                        if (value == null)
                            throw new NullDependencyException("");
                    }
                    catch (Throwable ignored)
                    {
                        sender.sendMessage(MessageUtils.prefixes(sender,
                                CommandStrings.argumentTypeErrorString()
                                        .withLocale(MessageUtils.getLocale(sender))
                                        .resolve("type", typeName)));

                        return true;
                    }

                    config.set(option, value);

                    sender.sendMessage(MessageUtils.prefixes(sender,
                            CommandStrings.optionSetString()
                                    .withLocale(MessageUtils.getLocale(sender))
                                    .resolve("what", targetDisplay, null)
                                    .resolve("value", value + "")));
                    return true;
                })
                .buildAll().get(0);
    }

    private ISubCommand getDouble(String name, ConfigOption<Double> option, @Nullable FormattableMessage displayName)
    {
        return getGeneric(name, option, displayName, Double.class, Double::parseDouble, TypesString.typeDouble());
    }

    private ISubCommand getInteger(String name, ConfigOption<Integer> option)
    {
        return getInteger(name, option, null);
    }

    private ISubCommand getInteger(String name, ConfigOption<Integer> option, @Nullable FormattableMessage displayName)
    {
        return getGeneric(name, option, displayName, Integer.class, Integer::parseInt, TypesString.typeInteger());
    }

    private ISubCommand getToggle(String name, ConfigOption<Boolean> option)
    {
        return getToggle(name, option, null);
    }

    private ISubCommand getToggle(String name, ConfigOption<Boolean> option, @Nullable FormattableMessage displayName)
    {
        return getGeneric(name, option, displayName, Boolean.class, this::parseBoolean, "true/false");
    }

    private boolean parseBoolean(String input)
    {
        return "true".equalsIgnoreCase(input)
                || "t".equalsIgnoreCase(input)
                || "on".equalsIgnoreCase(input)
                || "1".equalsIgnoreCase(input)
                || "enable".equalsIgnoreCase(input)
                || "enabled".equalsIgnoreCase(input);
    }

    private final List<ISubCommand> subCommands = new ObjectArrayList<>();

    @Override
    public String getPermissionRequirement()
    {
        return CommonPermissions.optionCommand;
    }

    @Override
    public List<ISubCommand> getSubCommands()
    {
        return subCommands;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return new FormattableMessage(plugin, "OptionSubCommand");
    }
}
