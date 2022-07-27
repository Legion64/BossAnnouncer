package dev.kripton.bossannouncer.modules;

import com.google.inject.AbstractModule;
import dev.kripton.bossannouncer.tasks.BossBarTask;

public abstract class PluginModule extends AbstractModule {

    protected abstract void otherConfigure();

    @Override
    protected void configure() {
        otherConfigure();

        // Listeners
        // bind(PlayerListener.class).asEagerSingleton();

        // Tasks
        bind(BossBarTask.class).asEagerSingleton();
    }

}
