package lantum.skilltreeshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandInterface {
    boolean condition(String label, String[] args);
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}

class CommandCreateSkillTree implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("생성")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandDeleteSkillTree implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("삭제")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandOpenSkillTree implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("확인")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandSetSkillCost implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("구매가격설정")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandSetSkillRefundCost implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("판매가격설정")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandEnableBuying implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("구매허용")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandDisableBuying implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("구매금지")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandEnableRefund implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("판매허용")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}

class CommandDisableRefund implements CommandInterface {
    @Override
    public boolean condition(String label, String[] args) {
        if(args[1].compareTo("판매금지")==0 && args.length == 2) return true;
        else return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}