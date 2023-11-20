package org.mbf.teams;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector3d;
import org.mbf.teams.db.models.Team;

import java.sql.SQLException;
import java.util.Random;

public class RoundState {
    private final Teams plugin;

    public RoundState(Teams plugin) {
        this.plugin = plugin;
    }
    private boolean isRoundActive = false;

    public boolean isRoundActive() {
        return isRoundActive;
    }

    public void setRoundActive(boolean state) {
        isRoundActive = state;
    }

}
