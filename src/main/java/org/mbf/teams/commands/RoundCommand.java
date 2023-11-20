package org.mbf.teams.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.joml.Vector3d;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.Team;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Command("round")
public class RoundCommand extends BaseCommand {
    private final Teams plugin;

    public RoundCommand(Teams plugin) {
        this.plugin = plugin;
    }

    @SubCommand("start")
    public void startRoundCommand(Player sender) throws SQLException {
        plugin.getRoundState().setRoundActive(true);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            TeamMember member = plugin.getTeamDatabase().getTeamMember(player);
            member.spawnInRandomPos(player);
            member.giveLoadout();
            member.setRoundLives(plugin.getConfig().getInt("player-lives"));
            player.sendMessage("Round started!");

        }
    }

    @SubCommand("addteamspawn")
    @Requirement("playerInTeam")
    public void addTeamSpawnCommand(Player sender) throws SQLException {
        Vector3d spawn = new Vector3d(sender.getLocation().getX(), sender.getLocation().getY(), sender.getLocation().getZ());
        Team team = plugin.getTeamDatabase().getPlayerTeam(sender);
        HashSet<Vector3d> spawnPositions = team.getSpawnPositions();
        spawnPositions.add(spawn);
        team.setSpawnPositions(spawnPositions);
        plugin.getTeamDatabase().updateTeam(team);
        sender.sendMessage("Added spawn position for team " + team.getName());

    }

    @SubCommand("clearteamspawn")
    @Requirement("playerInTeam")
    public void clearTeamSpawnCommand(Player sender) {
        try {
            Team team = plugin.getTeamDatabase().getPlayerTeam(sender);
            team.setSpawnPositions(new HashSet<>());
            plugin.getTeamDatabase().updateTeam(team);
            sender.sendMessage("Cleared spawn positions for team " + team.getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
