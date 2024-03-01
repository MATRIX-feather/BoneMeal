package xyz.nifeather.fexp.messages.strings;

import xiamomc.pluginbase.Messages.FormattableMessage;

public class CommandStrings extends AbstractMorphStrings
{
    public static FormattableMessage noPermissionMessage()
    {
        return getFormattable(getKey("no_permission"),
                "<color:red>禁止接触");
    }

    //reload
    public static FormattableMessage reloadCompleteMessage()
    {
        return getFormattable(getKey("reload_complete"),
                "重载完成！");
    }

    //options
    public static FormattableMessage optionSetString()
    {
        return getFormattable(getKey("option_set"),
                "已将选项<what>设置为<value>");
    }

    public static FormattableMessage optionValueString()
    {
        return getFormattable(getKey("option_get"),
                "<what>已设置为<value>");
    }

    //region Illegal arguments

    public static FormattableMessage illegalArgumentString()
    {
        return getFormattable(getKey("illegal_argument"),
                "无效的参数: <detail>");
    }

    public static FormattableMessage argumentTypeErrorString()
    {
        return getFormattable(getKey("illegal_argument.type_error"),
                "参数类型应为<type>");
    }

    //endregion Illegal arguments

    //region Lists

    public static FormattableMessage listNoEnoughArguments()
    {
        return getFormattable(getKey("not_enough_arguments"),
                "参数不足");
    }

    public static FormattableMessage listAddSuccess()
    {
        return getFormattable(getKey("list_add_success"),
                "成功添加<value>到<option>");
    }

    public static FormattableMessage listAddFailUnknown()
    {
        return getFormattable(getKey("list_add_fail_unknown"),
                "未能添加<value>到<option>，可能是类型不对");
    }

    public static FormattableMessage listRemoveSuccess()
    {
        return getFormattable(getKey("list_remove_success"),
                "成功从<option>移除<value>");
    }

    public static FormattableMessage listRemoveFailUnknown()
    {
        return getFormattable(getKey("list_remove_fail_unknown"),
                "未能移除<value>，可能是其不在列表中");
    }

    public static FormattableMessage unknownOperation()
    {
        return getFormattable(getKey("unknown_operation"),
                "未知操作：<operation>");
    }

    public static FormattableMessage commandNotFoundString()
    {
        return getFormattable(getKey("command_not_found"),
                "<color:red>未找到此指令");
    }

    //endregion Lists

    private static String getKey(String key)
    {
        return "commands." + key;
    }
}
