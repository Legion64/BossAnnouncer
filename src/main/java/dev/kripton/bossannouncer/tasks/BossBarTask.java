package dev.kripton.bossannouncer.tasks;

import com.google.inject.Inject;
import dev.kripton.bossannouncer.BossAnnouncer;
import dev.kripton.bossannouncer.announce.AnnounceBar;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class BossBarTask extends BukkitRunnable implements IBossBar {

    /**
     * Mapping boss bars
     */
    public static Map<Player, BossBar> bossBarMap = new HashMap<>();

    protected List<AnnounceBar> bars = new ArrayList<>();

    protected long previousTime = System.currentTimeMillis();

    protected float remainTime;

    /**
     * Current row index
     */
    protected int current = 0;

    protected BossAnnouncer plugin;

    protected List<String> lines;

    @Inject
    public BossBarTask(BossAnnouncer plugin) {
        this.plugin = plugin;
        this.lines = plugin.getConfig().getStringList("lines");

        for (String line : this.lines) {
            bars.add(new AnnounceBar(
                    ChatColor.translateAlternateColorCodes('&', line), BarColor.BLUE, BarStyle.SOLID, 5));
        }

        this.remainTime = bars.get(0).getRemainTime();
    }

    @Override
    public void run() {
        float deltaTime = (System.currentTimeMillis() - previousTime) / 1000F;
        previousTime = System.currentTimeMillis();

        remainTime -= deltaTime;
        if (remainTime < 0) {
            if (current == bars.size() - 1) current = 0;
            else current++;
            remainTime = bars.get(current).getRemainTime();
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (!bossBarMap.containsKey(p)) {
                bossBarMap.put(p, currentBossBar(bars.get(current), p));
            }
            if (bossBarMap.containsKey(p)){
                AnnounceBar currentBar = bars.get(current);
                BossBar bossBar = bossBarMap.get(p);
                bossBar.setTitle(currentBar.getTitle());
                bossBar.setColor(currentBar.getColor());
                bossBar.setStyle(currentBar.getStyle());
            }
        }
    }

    private BossBar currentBossBar(AnnounceBar bar, Player p) {
        BossBar bossBar = plugin.getServer().createBossBar(
                bar.getTitle(),
                bar.getColor(),
                bar.getStyle()
        );

        bossBar.addPlayer(p);
        return bossBar;
    }

    @Override
    public void startTask() {
        this.runTaskTimer(plugin, 5L, 5L);
    }

    @Override
    public void stopTask(){
        for(Map.Entry<Player, BossBar> entry : bossBarMap.entrySet()){
            entry.getValue().removeAll();
        }
        bossBarMap.clear();
        this.cancel();
    }
}
