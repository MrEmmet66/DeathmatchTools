package org.mbf.teams.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Requirement;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;

@Command("loadout")
public class LoadoutCommand extends BaseCommand {
    private final Teams plugin;

    public LoadoutCommand(Teams plugin) {
        this.plugin = plugin;
    }

    @SubCommand("add")
    @Requirement("isPlayer")
    public void addLoadoutItemCommand(Player player, Material material, int amount) throws SQLException {
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
    public void giveCommand(Player player) throws SQLException {
        HashMap<String, Integer> itemStackList = plugin.getTeamDatabase().getTeamMember(player).getLoadoutItems();
        player.sendMessage(String.valueOf(itemStackList.size()));
        for(String item : itemStackList.keySet()){
            player.sendMessage(item + " " + itemStackList.get(item).toString());
            player.getInventory().addItem(new ItemStack(Objects.requireNonNull(Material.getMaterial(item)), itemStackList.get(item)));
            player.sendMessage("Gived you " + itemStackList.get(item) + " " + item);
        }
    }

}
