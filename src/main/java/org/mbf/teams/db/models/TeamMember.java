package org.mbf.teams.db.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.joml.Vector3d;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

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
    private HashSet<Loadout> loadouts = new HashSet<>();

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private Loadout loadout;

    private int roundLives;

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

    public int getRoundLives() {
        return roundLives;
    }

    public HashSet<Loadout> getLoadouts() {
        return loadouts;
    }

    public Loadout getLoadout(String name) {
        Loadout _loadout = loadouts.stream().filter(loadout -> loadout.getName().equals(name)).findFirst().orElse(null);
        return _loadout;
    }

    public void setLoadouts(HashSet<Loadout> loadouts) {
        this.loadouts = loadouts;
    }

    public void setRoundLives(int roundLives) {
        this.roundLives = roundLives;
    }

    public Loadout getLoadoutItems() {
        return loadout;
    }

    public void setLoadoutItems(Loadout loadoutItems) {
        this.loadout = loadoutItems;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public void spawnInRandomPos(Player player) throws SQLException {
        Team team = getTeam();
        Vector3d[] vectors = team.getSpawnPositions().toArray(new Vector3d[0]);
        Random rand = new Random();
        int randIndex = rand.nextInt(vectors.length);
        Vector3d vector3d = vectors[randIndex];
        Location location = new Location(player.getWorld(), vector3d.x, vector3d.y, vector3d.z);
        player.teleport(location);
    }

    public Location getRandomSpawnLocation(Player player) {
        Vector3d[] vectors = team.getSpawnPositions().toArray(new Vector3d[0]);
        Random rand = new Random();
        int randIndex = rand.nextInt(vectors.length);
        Vector3d vector3d = vectors[randIndex];
        Location location = new Location(player.getWorld(), vector3d.x, vector3d.y, vector3d.z);
        return location;
    }

    public void giveLoadout() {
        Player player = Bukkit.getPlayer(name);
        player.getInventory().setContents(itemStacksFromBase64(loadout.getItems()));
        player.getInventory().setArmorContents(itemStacksFromBase64(loadout.getArmor()));
    }

    private String itemStackToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for(ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ItemStack[] itemStacksFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for(int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
