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
import xyz.nifeather.fexp.messages.strings.HelpStrings;
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
        subCommands.add(getToggle("bonemeal.coral_allow_dispenser", FConfigOptions.CORAL_ALLOW_DISPENSER));

        subCommands.add(getToggle("bonemeal.flower", FConfigOptions.FEAT_BONEMEAL_ON_FLOWER));
        subCommands.add(getToggle("bonemeal.flower_allow_dispenser", FConfigOptions.FLOWER_ALLOW_DISPENSER));

        subCommands.add(getToggle("bonemeal.sugarcane", FConfigOptions.FEAT_BONEMEAL_ON_SUGARCANE));
        subCommands.add(getToggle("bonemeal.sugarcane_allow_dispenser", FConfigOptions.SUGARCANE_ALLOW_DISPENSER));

        subCommands.add(getToggle("shulkerbox.open", FConfigOptions.FEAT_OPEN_SHULKERBOX));
        subCommands.add(getInteger("shulkerbox.open_delay", FConfigOptions.SHULKERBOX_OPEN_DELAY));

        subCommands.add(getToggle("deepslate.farm", FConfigOptions.FEAT_DEEPSLATE_FARM));

        subCommands.add(getToggle("bossbar.warden", FConfigOptions.WARDEN_BOSSBAR));
        subCommands.add(getToggle("bossbar.warden_show_anger", FConfigOptions.WARDEN_BOSSBAR_SHOW_ANGER));

        subCommands.add(getToggle("save_tridents.enabled", FConfigOptions.TRIDENT));

        subCommands.add(getToggle("mob_eggs.enabled", FConfigOptions.VILLAGER_EGG));
        subCommands.add(getList("mob_eggs.disabled_worlds", FConfigOptions.EGG_DISABLED_WORLDS, null));
        subCommands.add(getList("mob_eggs.disabled_mobs", FConfigOptions.EGG_DISABLED_MOBS, null));
        subCommands.add(getList("mob_eggs.mob_whitelist", FConfigOptions.EGG_WHITELIST, null));
    }

    private <T> ISubCommand getGeneric(String name, ConfigOption<T> option,
                                       @Nullable FormattableMessage displayName, Class<T> targetClass,
                                       Function<String, T> func, String typeName)
    {
        return getGeneric(name, option,
                displayName, targetClass, func, new FormattableMessage(plugin, typeName), (args) -> List.of());
    }

    private ISubCommand getList(String optionName, ConfigOption<?> option,
                                @Nullable FormattableMessage displayName)
    {
        var targetDisplay = displayName == null ? new FormattableMessage(plugin, optionName) : displayName;

        var bindableList = config.getBindableList(String.class, option);

        return CommandBuilder.builder().startNew()
                .name(optionName)
                .permission(CommonPermissions.optionCommand)
                .onFilter(args ->
                {
                    if (args.size() > 1) return List.of();

                    var input = args.isEmpty() ? "" : args.get(0);
                    var list = List.of("add", "remove", "list");
                    return list.stream().filter(op -> op.toLowerCase().startsWith(input.toLowerCase())).toList();
                })
                .executes((sender, args) ->
                {
                    if (args.isEmpty() || args.get(0).equalsIgnoreCase("list"))
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
                            // 先预先检查一遍是否存在
                            if (bindableList.contains(value))
                            {
                                sender.sendMessage(MessageUtils.prefixes(sender,
                                        CommandStrings.listAddSuccess()
                                                .withLocale(MessageUtils.getLocale(sender))
                                                .resolve("value", value)
                                                .resolve("option", optionName)));

                                return true;
                            }

                            // 若不存在，则尝试添加
                            bindableList.add(value);

                            sender.sendMessage(MessageUtils.prefixes(sender,
                                    CommandStrings.listAddSuccess()
                                            .withLocale(MessageUtils.getLocale(sender))
                                            .resolve("value", value)
                                            .resolve("option", optionName)));
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
                                       Function<String, T> func, FormattableMessage typeName,
                                       Function<List<String>, List<String>> filterFunc)
    {
        var targetDisplay = displayName == null ? new FormattableMessage(plugin, name) : displayName;

        //todo: Move this to getToggle()
        boolean isBoolean = targetClass.equals(Boolean.class);

        return CommandBuilder.builder().startNew()
                .name(name)
                .permission(CommonPermissions.optionCommand)
                .onFilter(filterFunc)
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
        return getGeneric(name, option, displayName, Double.class, Double::parseDouble, TypesString.typeDouble(), args ->
        {
            return List.of();
        });
    }

    private ISubCommand getInteger(String name, ConfigOption<Integer> option)
    {
        return getInteger(name, option, null);
    }

    private ISubCommand getInteger(String name, ConfigOption<Integer> option, @Nullable FormattableMessage displayName)
    {
        return getGeneric(name, option, displayName, Integer.class, Integer::parseInt, TypesString.typeInteger(), args ->
        {
            return List.of();
        });
    }

    private ISubCommand getToggle(String name, ConfigOption<Boolean> option)
    {
        return getToggle(name, option, null);
    }

    private ISubCommand getToggle(String name, ConfigOption<Boolean> option, @Nullable FormattableMessage displayName)
    {
        return getGeneric(name, option, displayName, Boolean.class, this::parseBoolean, TypesString.typeBoolean(), args ->
        {
            return args.size() == 1 ? List.of("true", "false") : List.of();
        });
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
        return HelpStrings.pluginOptionDescription();
    }
}
