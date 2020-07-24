package io.github.battlepass.logger.influxdb;

import com.google.common.collect.Lists;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import io.github.battlepass.BattlePlugin;
import io.github.battlepass.logger.influxdb.listeners.QuestProgressionListener;
import me.hyfe.simplespigot.config.Config;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;

public class InfluxManager {
    private final BattlePlugin plugin;
    private final List<Point> pointsToSend = Lists.newArrayList();
    private InfluxDBClient fluxClient;
    private WriteApi writeApi;

    public InfluxManager(BattlePlugin plugin) {
        this.plugin = plugin;
        this.start();
    }

    private void start() {
        Config config = this.plugin.getConfig("influx-settings");
        String url = config.string("influx-options.url");
        char[] token = config.string("influx-options.token").toCharArray();;
        String organization = config.string("influx-options.organization");
        String bucket = config.string("influx-options.bucket");
        this.fluxClient = InfluxDBClientFactory.create(url, token, organization, bucket);
        this.writeApi = this.fluxClient.getWriteApi();

        this.plugin.registerListeners(
                new QuestProgressionListener(this)
        );
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            this.writeApi.writePoints(this.pointsToSend);
            Bukkit.getLogger().log(Level.INFO, "Sent " + this.pointsToSend.size() + " data points to InfluxDB.");
            this.pointsToSend.clear();
        }, 600, 600);
    }

    public void addPoint(Point point) {
        this.pointsToSend.add(point.time(System.currentTimeMillis(), WritePrecision.MS));
    }
}
