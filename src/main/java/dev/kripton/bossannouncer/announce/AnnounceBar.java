package dev.kripton.bossannouncer.announce;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class AnnounceBar {
    private final String title;
    private final BarColor barColor;
    private final BarStyle barStyle;
    private final long remainTime;
}
