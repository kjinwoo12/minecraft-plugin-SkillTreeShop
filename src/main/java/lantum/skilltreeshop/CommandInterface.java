package lantum.skilltreeshop;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public interface CommandInterface {
    boolean condition(String label, String[] args);
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}

class CommandCreateSkillTree implements CommandInterface {
    JavaPlugin javaPlugin;

    CommandCreateSkillTree(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("생성")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }

        String skillTreeName = args[1];
        SkillTree skillTree;
        try {
            skillTree = new SkillTree(javaPlugin, skillTreeName);
        } catch (Exception e) {
            javaPlugin.getLogger().warning("Failed to create skillTree.");
            sender.sendMessage("스킬트리를 생성하는데 실패했습니다. 잠시 후 다시 시도해주세요.");
            e.printStackTrace();
            return true;
        }
        SkillTreeManager skillTreeManager = SkillTreeManager.getInstance();
        sender.sendMessage(skillTreeName + " 스킬트리가 생성되었습니다.");

        skillTreeManager.addSkillTree(skillTree);
        skillTreeManager.saveSkillTrees();
        return true;
    }
}

class CommandDeleteSkillTree implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("삭제")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        String skillTreeName = args[1];
        SkillTreeManager skillTreeManager = SkillTreeManager.getInstance();
        skillTreeManager.removeSkillTree(skillTreeName);
        skillTreeManager.saveSkillTrees();
        sender.sendMessage(skillTreeName + " 스킬트리가 삭제되었습니다.");
        return true;
    }
}

class CommandSettingSkillTree implements CommandInterface {
    private static final String PERMISSION_NODE_PREFIX = "skilltree.";

    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("설정")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String skillTreeName = args[1];
        SkillTree skillTree = SkillTreeManager.getInstance().getSkillTree(skillTreeName);
        if(skillTree == null) {
            sender.sendMessage(skillTreeName + "라는 스킬트리가 존재하지 않습니다.");
            return true;
        }
        if(!sender.hasPermission(PERMISSION_NODE_PREFIX+skillTreeName)) {
            sender.sendMessage(PERMISSION_NODE_PREFIX+skillTreeName+" 노드 권한이 없습니다.");
            return true;
        }

        Player player = sender.getServer().getPlayer(sender.getName());
        skillTree.openEditMode(player);
        return true;
    }
}

class CommandOpenSkillTree implements CommandInterface {
    private static final String PERMISSION_NODE_PREFIX = "skilltree.";

    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("확인")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String skillTreeName = args[1];
        SkillTree skillTree = SkillTreeManager.getInstance().getSkillTree(skillTreeName);
        if(skillTree == null) {
            sender.sendMessage(skillTreeName + "라는 스킬트리가 존재하지 않습니다.");
            return true;
        }
        if(!sender.hasPermission(PERMISSION_NODE_PREFIX+skillTreeName)) {
            sender.sendMessage(PERMISSION_NODE_PREFIX+skillTreeName+" 노드 권한이 없습니다.");
            return true;
        }

        Player player = sender.getServer().getPlayer(sender.getName());
        skillTree.openShopMode(player);
        return true;
    }
}

class CommandSetSkillCost implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("구매가격설정")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        String costString = args[1];
        Integer cost = Integer.parseInt(costString);
        if(cost == null) {
            sender.sendMessage(costString + "은 올바르지 않은 가격입니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setCostBuying(handItem, cost);
        sender.sendMessage(cost + "원으로 가격이 설정되었습니다.");
        return true;
    }
}

class CommandSetSkillRefundCost implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("판매가격설정")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        String costString = args[1];
        Integer cost = Integer.parseInt(costString);
        if(cost == null) {
            sender.sendMessage(costString + "은 올바르지 않은 가격입니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setCostRefund(handItem, cost);
        sender.sendMessage(cost + "원으로 가격이 설정되었습니다.");

        return true;
    }
}

class CommandEnableBuying implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("구매허용")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setEnableBuying(handItem, true);

        return true;
    }
}

class CommandDisableBuying implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("구매금지")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setEnableBuying(handItem, false);

        return true;
    }
}

class CommandEnableRefund implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("판매허용")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setEnableRefund(handItem, true);

        return true;
    }
}

class CommandDisableRefund implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[0].compareTo("판매금지")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage("해당 명령어는 OP 권한이 필요합니다.");
            return true;
        }
        Player player = sender.getServer().getPlayer(sender.getName());
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if(handItem == null || handItem.getType().equals(Material.AIR)) {
            sender.sendMessage("손에 아이템을 들고 명령어를 입력해주세요.");
            return true;
        }
        SkillManager.getInstance().setEnableRefund(handItem, false);

        return true;
    }
}