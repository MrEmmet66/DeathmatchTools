package org.mbf.teams.commands;

import com.nametagedit.plugin.NametagEdit;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.Requirements;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.mbf.teams.Keys;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.Team;
import org.mbf.teams.db.models.TeamMember;
import dev.triumphteam.cmd.core.annotation.Command;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Command("team")
public class TeamCommand extends BaseCommand {
    private final Teams plugin;

    public TeamCommand(Teams plugin) {
        this.plugin = plugin;

    }
    @SubCommand("chat")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void teamChatCommand(Player player, String message) {
        try {
            Team team = plugin.getTeamDatabase().getPlayerTeam(player);
            plugin.getTeamDatabase().sendTeamMessage(team,  team.getColor() + "[" + team.getTag() + "] " + ChatColor.GREEN + player.getName() + ": " + ChatColor.WHITE + String.join(" ", message).substring(5));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @SubCommand("members")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void teamMembersCommand(Player player) {
        try {
            Team team = plugin.getTeamDatabase().getPlayerTeam(player);
            player.sendMessage("Team members:");
            for(TeamMember member : team.getMembers()){
                if(member.isLeader())
                    player.sendMessage(ChatColor.GOLD + member.getName());
                else
                    player.sendMessage(member.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SubCommand("leave")
    @Requirements({
        @Requirement("isPlayer"),
        @Requirement("playerInTeam")
    })
    public void leaveTeamComamnd(Player sender) {
        try {
            TeamMember member = plugin.getTeamDatabase().getTeamMember(sender);
            Team team = member.getTeam();
            if (member.isLeader()) {
                sender.sendMessage("You cannot leave the team as you are the leader! Use /team disband to disband the team!");
                return;
            }
            member.setTeam(null);
            plugin.getTeamDatabase().updateTeamMember(member);
            sender.sendMessage("You have left the team!");
            plugin.getTeamDatabase().sendTeamMessage(team, ChatColor.RED + sender.getName() + " has left the team!");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SubCommand("disband")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void disbandTeamCommand(Player player) {
        try {
            Team team = plugin.getTeamDatabase().getPlayerTeam(player);
            if (!team.getLeader().getName().equals(player.getName())) {
                player.sendMessage("You are not the team leader!");
                return;
            }
            plugin.getTeamDatabase().removeTeam(team);
            player.sendMessage("Team disbanded!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @SubCommand("create")
    @Requirement("isPlayer")
    public void createTeamCommand(Player player, String name, String tag, ChatColor color) {
        TeamMember owner;
        Team team = new Team();
        try {
            owner = plugin.getTeamDatabase().getTeamMember(player);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        };
        owner.setTeam(team);
        team.setLeader(owner);
        team.setName(name);
        team.setColor(color);
        team.setTag(tag);
        try {
            plugin.getTeamDatabase().addTeam(team);
            plugin.getTeamDatabase().updateTeamMember(owner);
            player.sendMessage("Team created!");
            NametagEdit.getApi().setPrefix(player, "[" + tag + "] " + player.getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @SubCommand("invite")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void teamInviteCommand(CommandSender sender, Player targetPlayer) {
        Player player = (Player) sender;
        try {
            TeamMember member = plugin.getTeamDatabase().getTeamMember(player);
            TeamMember targetMember = plugin.getTeamDatabase().getTeamMember(targetPlayer);
            if (targetMember.getTeam() != null) {
                player.sendMessage("Player is already in a team!");
            }
            invitePlayer(targetPlayer, member.getTeam().getId());
            player.sendMessage("Player invited!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @SubCommand("accept")
    @Requirement("isPlayer")
    public void acceptTeamInviteCommand(Player player) {
        if (player.getPersistentDataContainer().has(Keys.INVITE_KEY, PersistentDataType.INTEGER)) {
            PersistentDataContainer container = player.getPersistentDataContainer();
            int teamId = container.get(Keys.INVITE_KEY, PersistentDataType.INTEGER);
            try {
                Team team = plugin.getTeamDatabase().getTeam(teamId);
                TeamMember member = plugin.getTeamDatabase().getTeamMember(player);
                member.setTeam(team);
                plugin.getTeamDatabase().updateTeamMember(member);
                player.sendMessage("You have joined team " + team.getName());
                plugin.getTeamDatabase().sendTeamMessage(team, ChatColor.GREEN + player.getName() + " has joined the team!");
                player.getPersistentDataContainer().remove(Keys.INVITE_KEY);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } else {
            player.sendMessage("You have no pending invites!");
        }
    }
    @SubCommand("kick")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void kickPlayerFromTeamCommand(Player player, Player targetPlayer) {
        try {
            Team team = plugin.getTeamDatabase().getPlayerTeam(player);
            TeamMember targetMember = plugin.getTeamDatabase().getTeamMember(targetPlayer);
            if (targetMember.getTeam() == null) {
                player.sendMessage("Player is not in a team!");
            }
            if (!Objects.equals(targetMember.getTeam().getName(), plugin.getTeamDatabase().getPlayerTeam(player).getName())) {
                player.sendMessage("Player is not in your team!");
            }
            if (Objects.equals(targetMember.getTeam().getLeader().getName(), targetMember.getName()) || targetMember.getName().equals(player.getName())) {
                player.sendMessage("You cannot kick the team leader!");
            }
            targetMember.setTeam(null);
            plugin.getTeamDatabase().updateTeam(team);
            plugin.getTeamDatabase().updateTeamMember(targetMember);
            targetPlayer.sendMessage("You have been kicked from the team!");
            player.sendMessage("Player kicked!");
            plugin.getTeamDatabase().sendTeamMessage(team, ChatColor.RED + targetMember.getName() + " has been kicked from the team!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void invitePlayer(Player player, int teamId) {
        player.sendMessage("You have been invited to join a team! Type /team accept to join!");
        player.getPersistentDataContainer().set(Keys.INVITE_KEY, PersistentDataType.INTEGER, teamId);


    }
}
