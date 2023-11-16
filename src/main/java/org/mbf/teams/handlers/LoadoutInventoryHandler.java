package org.mbf.teams.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;
import java.util.HashMap;

public class LoadoutInventoryHandler implements Listener {
    private final Teams plugin;
    public LoadoutInventoryHandler(Teams plugin) {
        this.plugin = Teams.getPlugin();

    }

    @EventHandler
    public void TestInvHandler(InventoryCloseEvent event) throws SQLException {
        Inventory inventory = event.getInventory();
        if(!event.getView().getTitle().equals("Loadout"))
            return;
        ItemStack[] items = inventory.getContents();
        TeamMember member = plugin.getTeamDatabase().getTeamMember((Player) event.getPlayer());
        HashMap<String, Integer> newLoadout = new HashMap<>();
        for(ItemStack item : items){
            if(item == null)
                continue;
            System.out.println(item.getType().toString());
            newLoadout.put(item.getType().toString(), item.getAmount());
        }
        member.setLoadoutItems(newLoadout);
        plugin.getTeamDatabase().getTeamMember((Player)event.getPlayer()).setLoadoutItems(newLoadout);
        plugin.getTeamDatabase().updateTeamMember(member);
        event.getPlayer().sendMessage("Updated your loadout!");
    }
}
