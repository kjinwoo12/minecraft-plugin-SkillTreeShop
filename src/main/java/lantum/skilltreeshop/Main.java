package lantum.skilltreeshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

public final class Main extends JavaPlugin {
    ArrayList<CommandInterface> commands;

    @Override
    public void onEnable() {
        commands = new ArrayList<CommandInterface>();
        commands.add(new CommandCreateSkillTree());
        commands.add(new CommandDeleteSkillTree());
        commands.add(new CommandOpenSkillTree());
        commands.add(new CommandSetSkillCost());
        commands.add(new CommandSetSkillRefundCost());
        commands.add(new CommandDisableBuying());
        commands.add(new CommandEnableBuying());
        commands.add(new CommandDisableRefund());
        commands.add(new CommandEnableRefund());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(label.compareTo("스킬트리") != 0) return false;

        for(CommandInterface commandInterface : commands) {
            if(commandInterface.condition(label, args)) {
                return commandInterface.onCommand(sender, command, label, args);
            }
        }

        return false;
    }
}
