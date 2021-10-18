package lantum.skilltreeshop;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyManager {
    private static final String KEY_MONEY = "money";
    private JavaPlugin javaPlugin;

    private static class Singleton {
        private static final MoneyManager instance = new MoneyManager();
    }

    private MoneyManager() {
    }

    public static MoneyManager getInstance() {
        return Singleton.instance;
    }

    public void initialize(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
    }

    int getMoney(Player player) {
        return player.getMetadata(KEY_MONEY).get(0).asInt();
    }

    void setMoney(Player player, int money) {
        Server server = javaPlugin.getServer();
        server.dispatchCommand(server.getConsoleSender(), "돈 설정 "+player.getName()+" "+money);
    }
}
