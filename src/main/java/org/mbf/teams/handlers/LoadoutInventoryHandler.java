package org.mbf.teams.handlers;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.mbf.teams.Teams;
import org.mbf.teams.db.models.Loadout;
import org.mbf.teams.db.models.TeamMember;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class LoadoutInventoryHandler implements Listener {
    private final Teams plugin;
    public LoadoutInventoryHandler(Teams plugin) {
        this.plugin = Teams.getPlugin();

    }
}
