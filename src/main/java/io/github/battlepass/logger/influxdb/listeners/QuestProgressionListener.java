package io.github.battlepass.logger.influxdb.listeners;

import com.influxdb.client.write.Point;
import io.github.battlepass.api.events.user.UserQuestProgressionEvent;
import io.github.battlepass.logger.influxdb.InfluxManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestProgressionListener implements Listener {
    private final InfluxManager influxManager;

    public QuestProgressionListener(InfluxManager influxManager) {
        this.influxManager = influxManager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestProgression(UserQuestProgressionEvent event) {
        this.influxManager.addPoint(Point
                .measurement("quest-progression")
                .addField("progress", event.getProgression())
                .addTag("quest-type", event.getQuest().getType())
                .addTag("quest-category", event.getQuest().getCategoryId()));
    }
}
