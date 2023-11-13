package org.mbf.teams.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;

public class PlayerJoinHandler implements Listener {
    private final Teams plugin;
    public PlayerJoinHandler(Teams plugin) {
        this.plugin = plugin;

    }

    @EventHandler
    public void onPlayerJoinServer(PlayerJoinEvent event) throws SQLException {
        if(!plugin.getTeamDatabase().isTeamMemberExists(event.getPlayer())){
            plugin.getLogger().info("Creating team member for " + event.getPlayer().getName());
            TeamMember member = new TeamMember();
            member.setName(event.getPlayer().getName());
            member.setId(event.getPlayer().getUniqueId().toString());
            plugin.getTeamDatabase().addTeamMember(member);
        }
    }

}
