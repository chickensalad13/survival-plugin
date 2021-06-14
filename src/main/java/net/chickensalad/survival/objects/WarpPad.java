package net.chickensalad.survival.objects;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.EulerAngle;

import java.util.UUID;

@Data
public final class WarpPad {

    public static final ItemStack HAND_ITEM = new ItemStack(Material.SMOOTH_STONE_SLAB);

    static {
        final ItemMeta HAND_ITEM_META = HAND_ITEM.getItemMeta();
        assert HAND_ITEM_META != null;
        HAND_ITEM_META.setDisplayName(ChatColor.WHITE + "Warp Pad");
        HAND_ITEM_META.addEnchant(Enchantment.DURABILITY, 10, true);
        HAND_ITEM_META.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        HAND_ITEM.setItemMeta(HAND_ITEM_META);
    }

    private UUID owner;
    private String name;

    private Visibility visibility = Visibility.PRIVATE;

    private int x, y, z;
    private String world;

    private transient boolean visible;

    private transient ArmorStand stand;

    WarpPad() {
    }

    public WarpPad(UUID owner, String name, Location location) {
        this.owner = owner;
        this.name = name;
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
        this.world = location.getWorld().getName();
    }

    public void spawn() {
        this.getLocation().getBlock().setType(Material.SMOOTH_STONE_SLAB);

        if (this.stand != null) {
            this.stand.remove();
        }

        stand = this.getLocation().getWorld().spawn(this.getLocation().add(0.5, 0.7, 0.5), ArmorStand.class);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(ChatColor.AQUA + this.name + " - Warp Pad");
        stand.setCustomNameVisible(true);
        stand.setHeadPose(new EulerAngle(Math.toRadians(180), 0, 0)); // If you can figure out how to center this, be my guest.
        stand.setHelmet(new ItemStack(Material.ENDER_EYE));
        stand.setChestplate(new ItemStack(Material.ENDER_EYE));

        this.visible = true;
    }

    public void despawn() {
        if (stand != null) {
            stand.remove();
            stand = null;
        }

        this.getLocation().getBlock().setType(Material.AIR);
        this.visible = false;
    }
    
   public enum Visibility {
        PRIVATE("Private"),
        PUBLIC("Public")
        ;

        private final String name;

        Visibility(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public boolean canSee(Player player) {
        return this.visibility == Visibility.PUBLIC || owner.equals(player.getUniqueId());
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
