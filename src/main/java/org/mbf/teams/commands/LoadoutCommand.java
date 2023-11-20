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
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.Loadout;
import org.mbf.teams.db.models.TeamMember;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@Command("loadout")
public class LoadoutCommand extends BaseCommand {
    private final Teams plugin;

    public LoadoutCommand(Teams plugin) {
        this.plugin = plugin;
    }

    @SubCommand("view")
    @Requirement("isPlayer")
    public void viewLoadoutCommand(Player sender) throws SQLException {
        Inventory inventory = Bukkit.createInventory(null, 54, "Loadout");
        Loadout loadout = plugin.getTeamDatabase().getTeamMember(sender).getLoadoutItems();
        for(ItemStack item : itemStacksFromBase64(loadout.getItems())){
            inventory.addItem(item);
        }
        sender.openInventory(inventory);

    }

    @SubCommand("clear")
    @Requirement("isPlayer")
    public void clearLoadoutCommand(Player sender) throws SQLException {
        TeamMember member = plugin.getTeamDatabase().getTeamMember(sender);
        member.setLoadoutItems(new Loadout());
        try {
            plugin.getTeamDatabase().updateTeamMember(member);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sender.sendMessage("Cleared your loadout!");
    }

    @SubCommand("save")
    @Requirement("isPlayer")
    public void addLoadoutItemCommand(Player sender) throws SQLException {
        Loadout loadout = new Loadout();
        ItemStack[] items = sender.getInventory().getContents();
        ItemStack[] armor = sender.getInventory().getArmorContents();
        String encodedItems = itemStackToBase64(items);
        String encodedArmor = itemStackToBase64(armor);
        loadout.setItems(encodedItems);
        loadout.setArmor(encodedArmor);
        TeamMember member = plugin.getTeamDatabase().getTeamMember(sender);
        member.setLoadoutItems(loadout);
        try {
            plugin.getTeamDatabase().updateTeamMember(member);
            sender.sendMessage("Saved your loadout!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @SubCommand("give")
    public void giveCommand(Player player) throws SQLException {

        TeamMember member = plugin.getTeamDatabase().getTeamMember(player);
        member.giveLoadout();
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
