package io.github.battlepass.logger.influxdb.listeners;

import com.influxdb.client.write.Point;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.api.events.user.UserQuestProgressionEvent;
import io.github.battlepass.logger.influxdb.InfluxManager;
import me.hyfe.simplespigot.uuid.FastUuid;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestProgressionListener implements Listener {
    private final BattlePlugin plugin;
    private final InfluxManager influxManager;

    public QuestProgressionListener(BattlePlugin plugin, InfluxManager influxManager) {
        this.plugin = plugin;
        this.influxManager = influxManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestProgression(UserQuestProgressionEvent event) {
        this.plugin.runAsync(() -> {
            this.influxManager.addPoint(Point
                    .measurement("quest-progression")
                    .addField("progress", event.getProgression())
                    .addTag("quest-type", event.getQuest().getType())
                    .addTag("quest-id", event.getQuest().getId())
                    .addTag("quest-category", event.getQuest().getCategoryId())
                    .addTag("player-uuid", FastUuid.toString(event.getUser().getUuid())));
        });
    }
}
