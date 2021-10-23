package lantum.skilltreeshop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Main extends JavaPlugin {

    ArrayList<CommandInterface> commands;

    @Override
    public void onEnable() {

        //Add commands
        commands = new ArrayList<CommandInterface>();
        commands.add(new CommandCreateSkillTree(this));
        commands.add(new CommandDeleteSkillTree());
        commands.add(new CommandOpenSkillTree());
        commands.add(new CommandSettingSkillTree());
        commands.add(new CommandSetSkillCost());
        commands.add(new CommandSetSkillRefundCost());
        commands.add(new CommandDisableBuying());
        commands.add(new CommandEnableBuying());
        commands.add(new CommandDisableRefund());
        commands.add(new CommandEnableRefund());

        //Init Singletons
        SkillManager.getInstance().initialize(this);
        MoneyManager.getInstance().initialize(this);
        SkillTreeManager.getInstance().initialize(this);

        //Regist glow enchant
        GlowEnchantment.Regist(this);
    }

    @Override
    public void onDisable() {
        SkillTreeManager.getInstance().saveSkillTrees();
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
