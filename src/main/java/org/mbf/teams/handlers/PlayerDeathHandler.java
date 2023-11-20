package org.mbf.teams.handlers;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;

public class PlayerDeathHandler implements Listener {
    private final Teams plugin;

    public PlayerDeathHandler(Teams plugin) {
        this.plugin = Teams.getPlugin();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws SQLException {
        System.out.println(plugin.getRoundState().isRoundActive());
        if(plugin.getRoundState().isRoundActive()) {
            e.setKeepInventory(true);
            e.getDrops().clear();
            TeamMember member = plugin.getTeamDatabase().getTeamMember(e.getEntity());
            member.spawnInRandomPos(e.getEntity());
            member.giveLoadout();
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) throws SQLException {
        TeamMember member = plugin.getTeamDatabase().getTeamMember(e.getPlayer());
        e.getPlayer().sendMessage("" + member.getRoundLives());
        if(member.getRoundLives() == 0) {
            e.getPlayer().setGameMode(GameMode.SPECTATOR);
        }
        e.setRespawnLocation(member.getRandomSpawnLocation(e.getPlayer()));
        member.spawnInRandomPos(e.getPlayer());
        member.setRoundLives(member.getRoundLives() - 1);

    }
}
