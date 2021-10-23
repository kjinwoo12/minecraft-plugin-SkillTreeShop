package lantum.skilltreeshop;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class GlowEnchantment extends Enchantment {
    public static void Regist(JavaPlugin javaPlugin) {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NamespacedKey key = new NamespacedKey(javaPlugin, javaPlugin.getDescription().getName());

            GlowEnchantment glowEnchantment = new GlowEnchantment(key);
            Enchantment.registerEnchantment(glowEnchantment);
        }
        catch (IllegalArgumentException e){
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void doEnchant(JavaPlugin javaPlugin, ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        NamespacedKey glowEnchantKey = new NamespacedKey(javaPlugin, javaPlugin.getDescription().getName());
        GlowEnchantment glowEnchantment = new GlowEnchantment(glowEnchantKey);
        itemMeta.addEnchant(glowEnchantment, 1, true);
        itemStack.setItemMeta(itemMeta);
    }

    public GlowEnchantment(NamespacedKey id) {
        super(id);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getMaxLevel() {
        return 0;
    }

    @Override
    public int getStartLevel() {
        return 0;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return false;
    }
}
