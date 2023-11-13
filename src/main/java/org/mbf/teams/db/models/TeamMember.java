package org.mbf.teams.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class TeamMember {
    @DatabaseField(id = true)
    private String uuid;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "team_id", foreignAutoCreate = true)
    private Team team;
    @DatabaseField(canBeNull = false, defaultValue = "0")
    private int kills;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashMap<String, Integer> loadoutItems = new HashMap<>();

    public TeamMember(){

    }

    public String getUuid() {
        return uuid;
    }

    public void setId(String id) {
        this.uuid = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(org.mbf.teams.db.models.Team team) {
        this.team = team;
    }

    public boolean isLeader(){
        return team.getLeader().equals(this);
    }

    public int getKills() {
        return kills;
    }

    public HashMap<String, Integer> getLoadoutItems() {
        return loadoutItems;
    }

    public void setLoadoutItems(HashMap<String, Integer> loadoutItems) {
        this.loadoutItems = loadoutItems;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
