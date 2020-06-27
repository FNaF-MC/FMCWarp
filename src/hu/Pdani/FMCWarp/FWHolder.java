package hu.Pdani.FMCWarp;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class FWHolder implements InventoryHolder {
    private Inventory inventory = null;

    public FWHolder(){
    }

    public FWHolder setInventory(Inventory inventory){
        this.inventory = inventory;
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
