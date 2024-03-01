package xyz.nifeather.fexp.messages.strings;

import xiamomc.pluginbase.Messages.FormattableMessage;

import java.text.Format;

public class HelpStrings extends AbstractMorphStrings
{
    //help
    public static FormattableMessage avaliableCommandHeaderString()
    {
        return getFormattable(getKey("avaliable_cmd_header"),
                "当前可用的指令（单击补全/查看）");
    }

    public static FormattableMessage commandNamePatternString()
    {
        return getFormattable(getKey("cmdname_pattern"),
                "/<basename>... -- <description>");
    }

    public static FormattableMessage clickToCompleteString()
    {
        return getFormattable(getKey("click_to_complete"),
                "点击补全");
    }

    public static FormattableMessage clickToViewString()
    {
        return getFormattable(getKey("click_to_view"),
                "点击查看");
    }

    public static FormattableMessage commandSectionHeaderString()
    {
        return getFormattable(getKey("section_header"),
                "指令 /<basename> 的用法：");
    }

    public static FormattableMessage commandEntryString()
    {
        return getFormattable(getKey("cmd_entry"),
            "/<basename>：<description>");
    }

    public static FormattableMessage specialNoteString()
    {
        return getFormattable(getKey("special_note"),
                "特别标注：");
    }

    public static FormattableMessage sectionNotFoundString()
    {
        return getFormattable(getKey("section_not_found"),
                "<color:red> 未找到此章节");
    }

    public static FormattableMessage mmorphDescription()
    {
        return getFormattable(getKey("description_mmorph"),
                "插件指令");
    }

    public static FormattableMessage otherCommandDescription()
    {
        return getFormattable(getKey("description_other_commands"),
                "其他指令");
    }

    public static FormattableMessage reloadDescription()
    {
        return getFormattable(getKey("description_reload"),
                "重载插件配置");
    }

    public static FormattableMessage pluginOptionDescription()
    {
        return getFormattable(getKey("description_plugin_option"),
                "查询、设置插件选项");
    }

    public static FormattableMessage helpDescription()
    {
        return getFormattable(getKey("description_help"),
                "显示帮助信息");
    }

    private static String getKey(String key)
    {
        return "help." + key;
    }
}
