package lantum.skilltreeshop;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

public class SkillTreeManager {

    private static final String SKILLTREE_INDEX_FILENAME = "plugins/SkillTreeShop/SkillTreeIndex.metadata";
    HashMap<String, SkillTree> skillTrees;
    Logger logger;
    JavaPlugin javaPlugin;

    private static class Singleton {
        private static final SkillTreeManager instance = new SkillTreeManager();
    }

    private SkillTreeManager() {
        skillTrees = new HashMap<>();
    }

    public static SkillTreeManager getInstance() {
        return Singleton.instance;
    }

    void initialize(JavaPlugin javaPlugin) {
        this.javaPlugin = javaPlugin;
        this.logger = javaPlugin.getLogger();

        loadSkillTrees();
    }

    void saveSkillTrees() {
        File file = new File(SKILLTREE_INDEX_FILENAME);
        try {
            if(!file.exists()) file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            Iterator<String> iter = skillTrees.keySet().iterator();
            while(iter.hasNext()) {
                SkillTree skillTree = skillTrees.get(iter.next());
                skillTree.save();
                bufferedWriter.write(skillTree.getName());
                if(iter.hasNext()) bufferedWriter.newLine();
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            logger.warning("Can't save file : `"+SKILLTREE_INDEX_FILENAME+"`");
        }
    }

    void loadSkillTrees() {
        skillTrees.clear();
        File file = new File(SKILLTREE_INDEX_FILENAME);
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.warning("Failed to create file! : `"+ SKILLTREE_INDEX_FILENAME +"`");
            }
            return;
        }

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while((line = bufferedReader.readLine()) != null ) {
                addSkillTree(new SkillTree(javaPlugin.getLogger(), line));
            }
            bufferedReader.close();
            logger.warning("[SkillTreeShop] Success load skillTrees : '"+ SKILLTREE_INDEX_FILENAME +"'");
        } catch (Exception e) {
            logger.warning("[SkillTreeShop] Can't read file! : '"+ SKILLTREE_INDEX_FILENAME +"'");
            e.printStackTrace();
            return;
        }
    }

    public SkillTree getSkillTree(String name) {
        return skillTrees.get(name);
    }

    public void addSkillTree(SkillTree skillTree) {
        skillTrees.put(skillTree.getName(), skillTree);
        javaPlugin.getServer().getPluginManager().registerEvents(skillTree, javaPlugin);
    }

    public void removeSkillTree(String name) {
        if(!skillTrees.containsKey(name)) return;
        skillTrees.get(name).delete();
        skillTrees.remove(name);
    }
}
