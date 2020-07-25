package tk.fatpackage.enforceoa.spigot;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class PlayerInventoryEquipment {

    @Getter
    private ItemStack[] inv;
    @Getter private ItemStack[] equip;
    @Getter private ItemStack[] extra;

    public PlayerInventoryEquipment (ItemStack[] inv, ItemStack[] equip, ItemStack[] extra) {
        this.inv = inv;
        this.equip = equip;
        this.extra = extra;
    }
}
