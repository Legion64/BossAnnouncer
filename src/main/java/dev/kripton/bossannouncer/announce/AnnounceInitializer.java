package dev.kripton.bossannouncer.announce;

import com.google.inject.Inject;
import dev.kripton.bossannouncer.BossAnnouncer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Getter
@Setter
public class AnnounceInitializer {

    protected BossAnnouncer plugin;

    protected List<AnnounceBar> bars = new ArrayList<>();

    protected Set<String> listOfAnnounceName;

    @Inject
    public AnnounceInitializer(BossAnnouncer plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        listOfAnnounceName = Objects.requireNonNull(plugin.getConfig().getConfigurationSection("announces")).getKeys(false);
        mapAnnounce();
    }

    public void mapAnnounce() {
        getListOfAnnounceName().forEach((name) -> {
            ConfigurationSection announce = plugin.getConfig().getConfigurationSection("announces." + name);
            if (!Objects.isNull(announce)) {
                bars.add(new AnnounceBar(
                        announce.getString("title"),
                        getColor(announce.getString("bar.color")),
                        getStyle(announce.getString("bar.style")),
                        announce.getInt("duration")
                ));
            }
        });
    }

    private BarColor getColor(String color) {
        return BarColor.valueOf(color);
    }

    private BarStyle getStyle(String style) {
        return BarStyle.valueOf(style);
    }

}
