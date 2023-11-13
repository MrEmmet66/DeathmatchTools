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

    @SubCommand("test")
    public void testCommand(CommandSender sender, Material material, int amount) throws SQLException {
        Player player = (Player) sender;
        ItemStack items = new ItemStack(material, amount);
        HashMap<String, Integer> itemStackList = plugin.getTeamDatabase().getTeamMember(player).getLoadoutItems();
        itemStackList.put(material.toString(), amount);
        TeamMember member = plugin.getTeamDatabase().getTeamMember(player);
        member.setLoadoutItems(itemStackList);
        plugin.getTeamDatabase().getTeamMember(player).setLoadoutItems(itemStackList);
        plugin.getTeamDatabase().updateTeamMember(member);
        player.sendMessage("Added " + amount + " " + material.toString() + " to your loadout!");
    }

    @SubCommand("give")
    public void giveCommand(CommandSender sender) throws SQLException {
        Player player = (Player) sender;
        HashMap<String, Integer> itemStackList = plugin.getTeamDatabase().getTeamMember(player).getLoadoutItems();
        player.sendMessage(String.valueOf(itemStackList.size()));
        for(String item : itemStackList.keySet()){
            player.sendMessage(item + " " + itemStackList.get(item).toString());
            player.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), itemStackList.get(item)));
            player.sendMessage("Gived you " + itemStackList.get(item) + " " + item);
        }
    }
    @SubCommand("chat")
    @Requirements({
            @Requirement("isPlayer"),
            @Requirement("playerInTeam")
    })
    public void teamChatCommand(CommandSender sender, String message) {
        Player player = (Player) sender;
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
    public void teamMembersCommand(CommandSender sender) {
        Player player = (Player) sender;
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
    @SubCommand("create")
    @Requirement("isPlayer")
    public void createTeamCommand(CommandSender sender, String name, String tag, ChatColor color) {
        Player player = (Player) sender;
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
    public void acceptTeamInviteCommand(CommandSender sender) {
        Player player = (Player) sender;
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
    public void kickPlayerFromTeamCommand(CommandSender sender, Player targetPlayer) {
        Player player = (Player) sender;
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
