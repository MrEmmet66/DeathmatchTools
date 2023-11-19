package org.mbf.teams.db.models;

import java.io.Serializable;

public class Loadout implements Serializable {
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

    public Loadout() {

    }

}
