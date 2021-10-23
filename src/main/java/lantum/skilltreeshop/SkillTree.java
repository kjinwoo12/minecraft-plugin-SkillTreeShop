package lantum.skilltreeshop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class SkillTree implements Listener {
    private static final String SKILLTREE_ROOT = "plugins/SkillTreeShop";
    private static final String SKILLTREE_EXTENSION = ".skilltree";
    private static final String INVENTORY_CONTENTS_KEY = "inventory";
    private static final String ITEMSTACK_KEY = ".itemStack";
    private static final String COST_BUYING_KEY = ".cost_buying";
    private static final String COST_REFUND_KEY = ".cost_refund";
    private static final String ENABLE_BUYING_KEY = ".enable_buying";
    private static final String ENABLE_REFUND_KEY = ".enable_refund";
    private Inventory skillInventorySample;
    private HashMap<Player, Inventory> skillInventoryMap;
    private String name;
    private JavaPlugin javaPlugin;
    private Logger logger;

    public SkillTree(JavaPlugin javaPlugin, String name) throws Exception {
        this.javaPlugin = javaPlugin;
        this.logger = javaPlugin.getLogger();
        this.name = name;
        skillInventoryMap = new HashMap<>();
        skillInventorySample = load();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        if(clickedInventory == null) return;
        Inventory playerInventory = player.getInventory();

        if(skillInventoryMap.containsKey(player)&&clickedInventory.equals(skillInventoryMap.get(player))) {
            onShopClicked(event);
            return;
        } else if(clickedInventory.equals(playerInventory)) {
            onPlayerInventoryClicked(event);
            return;
        }
    }

    private void onShopClicked(InventoryClickEvent event) {
        event.setCancelled(true);

        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = clickedInventory.getItem(event.getSlot());
        ItemStack sampleItem = skillInventorySample.getItem(event.getSlot());
        if(clickedItem == null || sampleItem == null) return;
        Player player = (Player) event.getWhoClicked();
        Inventory playerInventory = player.getInventory();
        Inventory playerSkillInventory = skillInventoryMap.get(player);
        SkillManager skillManager = SkillManager.getInstance();
        int skillTier = skillManager.getTier(playerSkillInventory, clickedItem);

        if(playerInventory.contains(sampleItem)) {
            player.sendMessage("이미 해당 스킬을 소유하고 있습니다.");
            return;
        }

        for(int i=skillTier*9, end=i+9; i<end; i++) {
            ItemStack slotItem = skillInventorySample.getItem(i);
            if(slotItem == null) continue;
            if(playerInventory.contains(slotItem)) {
                player.sendMessage("이미 해당 스킬과 같은 티어의 스킬을 소유하고 있습니다.");
                return;
            }
        }

        if(skillTier == 0) {
            buySkill(player, sampleItem);
            GlowEnchantment.doEnchant(javaPlugin, clickedItem);
        } else {
            //Checkout player has row tier skill
            for(int i=(skillTier-1)*9, end=i+9; i<end; i++) {
                ItemStack slotItem = skillInventorySample.getItem(i);
                if(slotItem == null) continue;
                if(playerInventory.contains(slotItem)) {
                    buySkill(player, sampleItem);
                    GlowEnchantment.doEnchant(javaPlugin, clickedItem);
                    return;
                }
            }
            player.sendMessage("해당 스킬의 하위 스킬이 없어 구매할 수 없습니다.");
        }
    }

    private void onPlayerInventoryClicked(InventoryClickEvent event) {
        if(!event.isRightClick()) return;
        event.setCancelled(true);
        ItemStack clickedItem = event.getClickedInventory().getItem(event.getSlot());
        if(clickedItem == null) return;
        Player player = (Player) event.getWhoClicked();

        if(!skillInventorySample.contains(clickedItem)) return;
        int itemSlot = 0;
        for(int i=0, size=skillInventorySample.getMaxStackSize(); i<size; i++) {
            ItemStack itemStack = skillInventorySample.getItem(i);
            if(itemStack == null) continue;
            if(itemStack.equals(clickedItem)) {
                itemSlot = i;
                break;
            }
        }

        ItemStack refundedItemOnShop = skillInventoryMap.get(player).getItem(itemSlot);
        ItemMeta itemMeta = refundedItemOnShop.getItemMeta();
        for (Enchantment enchantment : itemMeta.getEnchants().keySet()) {
            itemMeta.removeEnchant(enchantment);
        }
        refundedItemOnShop.setItemMeta(itemMeta);
        refundSkill(player, clickedItem);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory closedInventory = event.getInventory();
        if(closedInventory.equals(skillInventorySample)) {
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        } else if(skillInventoryMap.containsKey(player)&&closedInventory.equals(skillInventoryMap.get(player))) {
            skillInventoryMap.remove(player);
            return;
        }

    }

    void openEditMode(Player player) {
        player.openInventory(skillInventorySample);
    }

    void openShopMode(Player player) {
        Inventory skillInventory = Bukkit.createInventory(null, 54, name);
        Inventory playerInventory = player.getInventory();
        skillInventory.setContents(skillInventorySample.getContents());
        for(ItemStack itemStack : skillInventory) {
            if(playerInventory.contains(itemStack))
                GlowEnchantment.doEnchant(javaPlugin, itemStack);
        }
        skillInventoryMap.put(player, skillInventory);
        player.openInventory(skillInventory);
    }

    void buySkill(Player player, ItemStack itemStack) {
        SkillManager skillManager = SkillManager.getInstance();
        if(itemStack == null) return;
        if(!skillManager.getEnableBuying(itemStack)) {
            player.sendMessage("구매가 불가능한 스킬입니다.");
            return;
        }
        MoneyManager moneyManager = MoneyManager.getInstance();
        int playerMoney = moneyManager.getMoney(player);
        int itemCost = SkillManager.getInstance().getCostBuying(itemStack);
        if(playerMoney<itemCost) {
            player.sendMessage("스킬 구매에 필요한 돈이 부족합니다.");
            return;
        }
        Inventory playerInventory = player.getInventory();
        if(playerInventory.firstEmpty()==-1) {
            player.sendMessage("인벤토리 공간이 충분하지 않습니다.");
            return;
        }
        moneyManager.setMoney(player, playerMoney - itemCost);
        playerInventory.addItem(itemStack);
    }

    void refundSkill(Player player, ItemStack itemStack) {
        SkillManager skillManager = SkillManager.getInstance();
        if(!skillManager.getEnableRefund(itemStack)) {
            player.sendMessage("판매가 불가능한 스킬입니다.");
            return;
        }
        MoneyManager moneyManager = MoneyManager.getInstance();
        int playerMoney = moneyManager.getMoney(player);
        int itemCost = SkillManager.getInstance().getCostRefund(itemStack);
        moneyManager.setMoney(player, playerMoney + itemCost);
        itemStack.setAmount(itemStack.getAmount()-1);
    }

    void save() throws IOException {
        File file = new File(SKILLTREE_ROOT, name+SKILLTREE_EXTENSION);
        FileConfiguration fileConfiguration = new YamlConfiguration();
        ItemStack[] contents = skillInventorySample.getContents();
        SkillManager skillManager = SkillManager.getInstance();
        for(int i=0; i<contents.length; i++) {
            if(contents[i] == null) continue;
            String itemKey = INVENTORY_CONTENTS_KEY+'.'+i;
            int costBuying = skillManager.getCostBuying(contents[i]);
            int costRefund = skillManager.getCostRefund(contents[i]);
            boolean enableBuying = skillManager.getEnableBuying(contents[i]);
            boolean enableRefund = skillManager.getEnableRefund(contents[i]);
            fileConfiguration.set(itemKey+ITEMSTACK_KEY, contents[i]);
            fileConfiguration.set(itemKey+COST_BUYING_KEY, costBuying);
            fileConfiguration.set(itemKey+COST_REFUND_KEY, costRefund);
            fileConfiguration.set(itemKey+ENABLE_BUYING_KEY, enableBuying);
            fileConfiguration.set(itemKey+ENABLE_REFUND_KEY, enableRefund);
        }
        fileConfiguration.save(file);
    }

    void delete() {
        File file = new File(SKILLTREE_ROOT, name+SKILLTREE_EXTENSION);
        if(file.exists()) file.delete();
    }

    Inventory load() throws IOException, InvalidConfigurationException {
        Inventory inventory = Bukkit.createInventory(null, 54, name);
        File file = new File(SKILLTREE_ROOT, name+SKILLTREE_EXTENSION);
        if(!file.exists()) return inventory;
        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.load(file);
        int size = inventory.getSize();
        SkillManager skillManager = SkillManager.getInstance();
        for(int i=0; i<size; i++) {
            String itemKey = INVENTORY_CONTENTS_KEY+'.'+i;
            ItemStack itemStack = fileConfiguration.getItemStack(itemKey+ITEMSTACK_KEY);
            if(itemStack == null) continue;
            skillManager.setCostBuying(itemStack, fileConfiguration.getInt(itemKey+COST_BUYING_KEY, 0));
            skillManager.setCostRefund(itemStack, fileConfiguration.getInt(itemKey+COST_REFUND_KEY, 0));
            skillManager.setEnableBuying(itemStack, fileConfiguration.getBoolean(itemKey+ENABLE_BUYING_KEY, false));
            skillManager.setEnableRefund(itemStack, fileConfiguration.getBoolean(itemKey+ENABLE_REFUND_KEY, false));
            inventory.setItem(i, itemStack);
        }
        return inventory;
    }

    public String getName() {
        return name;
    }
}
