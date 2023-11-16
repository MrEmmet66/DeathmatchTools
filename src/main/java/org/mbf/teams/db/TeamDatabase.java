package org.mbf.teams.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.Team;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;

public class TeamDatabase {
    private final Dao<Team, Integer> teamDao;
    private final Dao<TeamMember, String> teamMemberDao;
    private final Teams plugin;

    public TeamDatabase(String path, Teams plugin) throws SQLException {
        ConnectionSource connectionSource = new JdbcConnectionSource("jdbc:sqlite:" + path);
        TableUtils.createTableIfNotExists(connectionSource, Team.class);
        TableUtils.createTableIfNotExists(connectionSource, TeamMember.class);
        teamDao = DaoManager.createDao(connectionSource, Team.class);
        teamMemberDao = DaoManager.createDao(connectionSource, TeamMember.class);
        this.plugin = plugin;



    }

    public void addTeamMember(TeamMember member) throws SQLException {
        teamMemberDao.create(member);
    }
    public void addTeam(Team team) throws SQLException {
        teamDao.create(team);
    }

    public TeamMember getTeamMember(Player player) throws SQLException {
        return teamMemberDao.queryForId(player.getUniqueId().toString());
    }

    public boolean isTeamMemberExists(Player player) throws SQLException {
        return teamMemberDao.idExists(player.getUniqueId().toString());
    }

    public void updateTeamMember(TeamMember member) throws SQLException {
        teamMemberDao.update(member);
    }

    public Team getTeam(int id) throws SQLException {
        return teamDao.queryForId(id);
    }

    public void updateTeam(Team team) throws SQLException {
        teamDao.update(team);
    }

    public Team getPlayerTeam(Player player) throws SQLException {
        TeamMember member = getTeamMember(player);
        return member.getTeam();
    }

    public void removeTeam(Team team) throws SQLException {
        sendTeamMessage(team, ChatColor.RED + "Your team has been disbanded!");
        teamDao.delete(team);
    }

    public void sendTeamMessage(Team team, String message){
        for(TeamMember member : team.getMembers()){
            Player player = plugin.getServer().getPlayer(member.getName());
            if(player != null){
                player.sendMessage(message);
            }
        }
    }

}
