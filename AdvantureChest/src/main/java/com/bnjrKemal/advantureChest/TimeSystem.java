package com.bnjrKemal.advantureChest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.bnjrKemal.advantureChest.AdvantureChest.getInstance;
import static com.bnjrKemal.advantureChest.Manage.manage;

public class TimeSystem {

    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TimeSystem(){
        startScheduler();
    }

    private void startScheduler() {
        File chestFolder = new File(getInstance().getDataFolder(), "chests");

        if (!chestFolder.exists()) {
            boolean created = chestFolder.mkdirs();
            if (!created) {
                getInstance().getLogger().severe("Chests klasörü oluşturulamadı!");
                Bukkit.shutdown();
                return;
            }
            getInstance().saveResource("chests/example.yml", true);
        }

        for (File file : Objects.requireNonNull(chestFolder.listFiles())) {
            try {
                processChestFile(file);
            } catch (Exception e) {
                getInstance().getLogger().severe("Dosya işlenirken hata oluştu: " + file.getName() + " Hata: " + e.getMessage());
            }
        }
    }

    private void processChestFile(File chestFile) {
        FileConfiguration chestConfig = YamlConfiguration.loadConfiguration(chestFile);
        List<String> scheduledTimes = chestConfig.getStringList("scheduled-times");

        if (scheduledTimes.isEmpty()) return;

        LocalDateTime nextTriggerTime = getNextTriggerTime(scheduledTimes);

        long delayMillis = Duration.between(LocalDateTime.now(), nextTriggerTime).toMillis();

        if (delayMillis < 0) return;

        scheduler.schedule(() -> triggerEvent(chestFile), delayMillis, TimeUnit.MILLISECONDS);

        getInstance().getLogger().info("Bir sonraki tetikleme zamanı: " + nextTriggerTime.format(timeFormatter) + " Dosya: " + chestFile.getName());
    }

    private void triggerEvent(File file) {
        FileConfiguration chestConfig = YamlConfiguration.loadConfiguration(file);

        manage(chestConfig);

        try {
            processChestFile(file);
        } catch (Exception e) {
            getInstance().getLogger().severe("Tetkikleme zamanı ayarlanırken hata oluştu: " + e.getMessage());
        }
    }

    public static LocalDateTime getNextTriggerTime(List<String> scheduledTimes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextTrigger = null;

        for (String timeStr : scheduledTimes) {
            LocalTime scheduledTime = LocalTime.parse(timeStr, timeFormatter);
            LocalDateTime todayScheduled = LocalDateTime.of(LocalDate.now(), scheduledTime);

            if (todayScheduled.isAfter(now)) {
                if (nextTrigger == null || todayScheduled.isBefore(nextTrigger)) {
                    nextTrigger = todayScheduled;
                }
            }
        }

        if (nextTrigger == null) {
            LocalTime firstScheduledTime = LocalTime.parse(scheduledTimes.getFirst(), timeFormatter);
            nextTrigger = LocalDateTime.of(LocalDate.now().plusDays(1), firstScheduledTime);
        }

        return nextTrigger;
    }


}
