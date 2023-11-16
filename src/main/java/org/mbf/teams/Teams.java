package org.mbf.teams;

import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.message.MessageKey;
import dev.triumphteam.cmd.core.requirement.RequirementKey;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.mbf.teams.commands.LoadoutCommand;
import org.mbf.teams.commands.TeamCommand;
import org.mbf.teams.db.TeamDatabase;
import org.mbf.teams.handlers.LoadoutInventoryHandler;
import org.mbf.teams.handlers.PlayerJoinHandler;

import java.sql.SQLException;

public final class Teams extends JavaPlugin {
    private TeamDatabase teamDatabase;

    @Override
    public void onEnable() {
        BukkitCommandManager<CommandSender> manager = BukkitCommandManager.create(this);
        registerRequirements(manager);
        manager.registerCommand(new TeamCommand(this));
        manager.registerCommand(new LoadoutCommand(this));
        try{
            if(!getDataFolder().exists()){
                getDataFolder().mkdirs();
            }
            teamDatabase = new TeamDatabase(getDataFolder().getAbsolutePath() + "/teams.db", this);

        } catch (SQLException e){
            e.printStackTrace();
            getLogger().warning("Could not connect to database, disabling plugin");
            Bukkit.getPluginManager().disablePlugin(this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerJoinHandler(this), this);
        Bukkit.getPluginManager().registerEvents(new LoadoutInventoryHandler(this), this);

    }

    private void registerRequirements(BukkitCommandManager<CommandSender> manager) {
        manager.registerRequirement(RequirementKey.of("isPlayer"), (sender) -> sender instanceof Player);
        manager.registerRequirement(RequirementKey.of("playerInTeam"), (sender) -> {
            try {
                return teamDatabase.getTeamMember((Player) sender).getTeam() != null;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    public TeamDatabase getTeamDatabase() {
        return teamDatabase;
    }

    public static Teams getPlugin() {
        return JavaPlugin.getPlugin(Teams.class);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
