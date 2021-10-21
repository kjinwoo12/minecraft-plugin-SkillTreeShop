package lantum.skilltreeshop;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class SkillTree implements Listener {
    public enum OpenMode {
        EDIT,
        SHOP,
        NONE
    }

    private static final String SKILLTREE_ROOT = "plugins/SkillTreeShop";
    private static final String SKILLTREE_EXTENSION = ".skilltree";
    private static final String INVENTORY_CONTENTS_KEY = "inventory";
    private static final String ITEMSTACK_KEY = ".itemStack";
    private static final String COST_BUYING_KEY = ".cost_buying";
    private static final String COST_REFUND_KEY = ".cost_refund";
    private static final String ENABLE_BUYING_KEY = ".enable_buying";
    private static final String ENABLE_REFUND_KEY = ".enable_refund";
    private Inventory skillInventory;
    private String name;
    private Logger logger;
    HashMap<String, OpenMode> playersOpenModes;

    public SkillTree(Logger logger, String name) throws Exception {
        this.logger = logger;
        this.name = name;
        playersOpenModes = new HashMap<>();
        skillInventory = load();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        //If player doesn't open the skilltree or is EDIT mode for, then return.
        Player player = (Player) event.getWhoClicked();
        String playerName = player.getName();
        OpenMode openMode = playersOpenModes.get(playerName);
        if(!playersOpenModes.containsKey(playerName)||openMode == OpenMode.NONE||openMode == OpenMode.EDIT) {
            return;
        }

        //If clicked inventory is not skilltree or player's inventory, then return
        Inventory clickedInventory = event.getClickedInventory();
        Inventory playerInventory = player.getInventory();
        if(!clickedInventory.equals(skillInventory)&&!clickedInventory.equals(playerInventory)) {
            return;
        }

        //Do click action
        switch (openMode) {
            case SHOP:
                onShopClicked(event);
                break;
            default:
                logger.warning("OpenMode is incorrect value.");
        }
    }

    private void onShopClicked(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        Inventory playerInventory = player.getInventory();
        ItemStack clickedItem = clickedInventory.getItem(event.getSlot());
        SkillManager skillManager = SkillManager.getInstance();

        //Cancel event
        if(clickedInventory.equals(skillInventory)) {
            event.setCancelled(true);
        }

        //Do click action
        if(event.isLeftClick() && clickedInventory.equals(skillInventory)) {
            int skillTier = skillManager.getTier(skillInventory, clickedItem);

            //Checkout player already has the item
            if(playerInventory.contains(clickedItem)) {
                player.sendMessage("이미 해당 스킬을 소유하고 있습니다.");
                return;
            }

            //Checkout player doesn't have same tier skill
            for(int i=skillTier*9, end=i+9; i<end; i++) {
                ItemStack slotItem = skillInventory.getItem(i);
                if(slotItem == null) continue;
                if(playerInventory.contains(slotItem)) {
                    player.sendMessage("이미 해당 스킬과 같은 티어의 스킬을 소유하고 있습니다.");
                    return;
                }
            }

            if(skillTier == 0) {
                buySkill(player, clickedItem);
            } else {
                //Checkout player has row tier skill
                for(int i=(skillTier-1)*9, end=i+9; i<end; i++) {
                    ItemStack slotItem = skillInventory.getItem(i);
                    if(slotItem == null) continue;
                    if(playerInventory.contains(slotItem)) {
                        buySkill(player, clickedItem);
                        return;
                    }
                }
                player.sendMessage("해당 스킬의 하위 스킬이 없어 구매할 수 없습니다.");
            }
        } else if(event.isRightClick() && clickedInventory.equals(playerInventory)) {
            event.setCancelled(true);
            refundSkill(player, clickedItem);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if(!event.getInventory().equals(skillInventory)) return;
        playersOpenModes.put(event.getPlayer().getName(), OpenMode.NONE);
    }

    void open(Player player, OpenMode openMode) {
        String playerName = player.getName();
        playersOpenModes.put(playerName, openMode);
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
        ItemStack[] contents = skillInventory.getContents();
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
