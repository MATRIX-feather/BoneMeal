package xyz.nifeather.fexp.features.pvp.storage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import xyz.nifeather.fexp.utilities.FJsonStorage;

import java.util.List;
import java.util.UUID;

public class PVPStorage extends FJsonStorage<DataRoot>
{
    @Override
    protected @NotNull String getFileName()
    {
        return "pvp_players.json";
    }

    @Override
    protected @NotNull DataRoot createDefault()
    {
        return new DataRoot();
    }

    @Override
    protected @NotNull String getDisplayName()
    {
        return "PVP Storage";
    }

    public List<UUID> getDisabledPlayers()
    {
        return new ObjectArrayList<>(storingObject.disabledPlayers);
    }

    public void addPlayer(UUID uuid)
    {
        storingObject.disabledPlayers.add(uuid);
    }

    public void removePlayer(UUID uuid)
    {
        storingObject.disabledPlayers.removeIf(u -> u.equals(uuid));
    }

    public void setPlayers(List<UUID> players)
    {
        storingObject.disabledPlayers.clear();
        storingObject.disabledPlayers.addAll(players);
    }
}
