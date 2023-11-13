package org.mbf.teams.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command("test")
public class TestCommand extends BaseCommand {

    @Default
    public void argExecute(CommandSender sender, Material material, int amount){
        Player player = (Player) sender;
        ItemStack itemStack = new ItemStack(material, amount);
        player.getInventory().addItem(itemStack);
        player.sendMessage("Added " + amount + " " + material.toString() + " to your inventory!");

    }
}
