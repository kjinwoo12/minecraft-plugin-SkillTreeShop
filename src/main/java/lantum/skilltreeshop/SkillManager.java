package lantum.skilltreeshop;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillManager {
    JavaPlugin javaPlugin;
    NamespacedKey buyingCostKey;
    NamespacedKey enableBuyingKey;
    NamespacedKey refundCostKey;
    NamespacedKey enableRefundKey;

    private static class Singleton{
        private static final SkillManager instance = new SkillManager();
    }

    public static SkillManager getInstance() {
        return Singleton.instance;
    }

    void initialize(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        buyingCostKey = new NamespacedKey(javaPlugin, "cost_buying");
        enableBuyingKey = new NamespacedKey(javaPlugin, "enable_buying");
        refundCostKey = new NamespacedKey(javaPlugin, "cost_refund");
        enableRefundKey = new NamespacedKey(javaPlugin, "enable_refund");
    }

    int getCostBuying(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if(dataContainer.has(buyingCostKey, PersistentDataType.INTEGER))
            return dataContainer.get(buyingCostKey, PersistentDataType.INTEGER);
        else
            return 0;
    }

    void setCostBuying(ItemStack itemStack, int cost) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(buyingCostKey, PersistentDataType.INTEGER, cost);
        itemStack.setItemMeta(itemMeta);
        setEnableBuying(itemStack, true);
    }

    int getCostRefund(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if(dataContainer.has(refundCostKey, PersistentDataType.INTEGER))
            return dataContainer.get(refundCostKey, PersistentDataType.INTEGER);
        else
            return 0;
    }

    void setCostRefund(ItemStack itemStack, int cost) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(refundCostKey, PersistentDataType.INTEGER, cost);
        itemStack.setItemMeta(itemMeta);
        setEnableRefund(itemStack, true);
    }

    boolean getEnableBuying(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if(dataContainer.has(enableBuyingKey, PersistentDataType.STRING))
            return Boolean.valueOf(dataContainer.get(enableBuyingKey, PersistentDataType.STRING));
        else
            return false;
    }

    void setEnableBuying(ItemStack itemStack, boolean enable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(enableBuyingKey, PersistentDataType.STRING, String.valueOf(enable));
        itemStack.setItemMeta(itemMeta);
    }

    boolean getEnableRefund(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        if(dataContainer.has(enableRefundKey, PersistentDataType.STRING))
            return Boolean.valueOf(dataContainer.get(enableRefundKey, PersistentDataType.STRING));
        else
            return false;
    }

    void setEnableRefund(ItemStack itemStack, boolean enable) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.getPersistentDataContainer().set(enableRefundKey, PersistentDataType.STRING, String.valueOf(enable));
        itemStack.setItemMeta(itemMeta);
    }

    //The number '9' is width of line. Tier for itemStack is decided by what line itemStack in inventory is.
    //If the item is in the 6th slot of first line, then the item's tier is 6/9 = 0.
    //If the item is in the 3th slot of third line, then the item's tier is 21/9 = 2.
    int getTier(Inventory inventory, ItemStack itemStack) {
        if(!inventory.contains(itemStack)) return -1;
        return inventory.first(itemStack)/9;
    }
}
