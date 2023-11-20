package org.mbf.teams.db.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.bukkit.ChatColor;
import org.joml.Vector3d;

import java.util.HashSet;
import java.util.Set;

@DatabaseTable(tableName = "teams")
public class Team {
    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(canBeNull = false, unique = true)
    private String name;

    @DatabaseField(canBeNull = false)
    private ChatColor color;

    @DatabaseField(canBeNull = false, unique = true)
    private String tag;
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true, columnName = "leader_id")
    private TeamMember leader;
    @ForeignCollectionField
    private ForeignCollection<TeamMember> members;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private HashSet<Vector3d> spawnPositions = new HashSet<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TeamMember getLeader() {
        return leader;
    }

    public void setLeader(TeamMember leader) {
        this.leader = leader;
    }

    public ForeignCollection<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(ForeignCollection<TeamMember> members) {
        this.members = members;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public HashSet<Vector3d> getSpawnPositions() {
        return spawnPositions;
    }

    public void setSpawnPositions(HashSet<Vector3d> spawnPositions) {
        this.spawnPositions = spawnPositions;
    }

    public Team() {
    }


}
