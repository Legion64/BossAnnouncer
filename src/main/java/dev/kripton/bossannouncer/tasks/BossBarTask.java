package dev.kripton.bossannouncer.tasks;

import com.google.inject.Inject;
import dev.kripton.bossannouncer.BossAnnouncer;
import dev.kripton.bossannouncer.announce.AnnounceBar;
import dev.kripton.bossannouncer.announce.AnnounceInitializer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


public class BossBarTask extends BukkitRunnable implements IBossBar {

    protected BossBar currentBossBar;

    protected AnnounceBar currentBar;

    protected List<Player> boundedPlayers = new ArrayList<>();

    protected BossAnnouncer plugin;

    protected AnnounceInitializer initializer;

    protected long previousTime = System.currentTimeMillis();

    protected float remainTime;

    protected int barIndex = 0;

    @Inject
    public BossBarTask(BossAnnouncer plugin, AnnounceInitializer initializer) {
        this.plugin = plugin;
        this.initializer = initializer;

        initializer.initialize();
        currentBar = initializer.getBars().get(barIndex);
        currentBossBar = createBossBar(currentBar);
        remainTime = currentBar.getRemainTime();

        // TODO: BossBar için gerekli olan AnnounceBar çekildi. İlk barın otomatik olarak tanımlanması yapılacak.
        // TODO: Her AnnounceBar ın config yml üzerinde belirlenen duration süresine göre bar seçimleri yapılacak.
        // TODO: BossBar title için PlaceholderAPI desteği getirilecek.
        // TODO: Oyuncunun oyundan çıkması, pluginin yeniden başlatılması gibi durumlarda bossbarlar bütün oyunculardan kaldırılacak.
    }

    @Override
    public void run() {
        Collection<? extends Player> playerList = plugin.getServer().getOnlinePlayers();

        long time = System.currentTimeMillis();
        float deltaTime = (time - previousTime) / 1_000F;
        previousTime = time;

        remainTime -= deltaTime;
        if (remainTime < 0) {
            if (barIndex == initializer.getBars().size()) {
                barIndex = 0;
            }

            currentBossBar.removeAll();
            currentBar = initializer.getBars().get(barIndex++);
            remainTime = currentBar.getRemainTime();
            currentBossBar = createBossBar(currentBar);
        }

        for (Player p : playerList) {
            currentBossBar.setTitle(
                    ChatColor.translateAlternateColorCodes('&',
                            PlaceholderAPI.setPlaceholders(p, currentBossBar.getTitle()))
            );
            if (!currentBossBar.getPlayers().contains(p)) {
                currentBossBar.addPlayer(p);
            }
        }
    }

    @Override
    public void startTask() {
        this.runTaskTimer(plugin, 0, 1L);
    }

    @Override
    public void stopTask() {
        currentBossBar.removeAll();
        this.cancel();
    }

    private AnnounceBar getCurrentBar(int index) {
        return initializer.getBars().get(index);
    }

    private BossBar createBossBar(AnnounceBar bar) {
        return plugin.getServer().createBossBar(
                bar.getTitle(),
                bar.getBarColor(),
                bar.getBarStyle()
        );
    }
}
