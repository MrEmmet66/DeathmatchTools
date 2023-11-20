package org.mbf.teams.db.models;

import org.bukkit.entity.Player;

import java.io.Serializable;

public class Loadout implements Serializable {
    private String name;
    private String items;
    private String armor;

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    public String getArmor() {
        return armor;
    }

    public void setArmor(String armor) {
        this.armor = armor;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public Loadout(String itemData, String armorData, String name) {
        this.items = itemData;
        this.armor = armorData;
        this.name = name;
    }

    public Loadout() {

    }

}
