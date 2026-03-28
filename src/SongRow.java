import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.UUID;

public class SongRow {

    private UUID id;
    private StringProperty title;
    private StringProperty artist;
    private String filePath;
    private StringProperty time;
    private StringProperty date;

    // Constructor หลัก — ใช้ทุกกรณี
    // ถ้า durationSeconds == 0 จะแสดง "..." รอ MySongWindow อัปเดตให้ผ่าน timeProperty()
    public SongRow(UUID id, String title, String artist, String filePath, long durationSeconds, String dateAdded) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = filePath;
        this.time = new SimpleStringProperty(durationSeconds > 0 ? formatDuration(durationSeconds) : "...");
        this.date = new SimpleStringProperty(dateAdded != null ? dateAdded : java.time.LocalDate.now().toString());
    }

    // Constructor สั้น — fallback
    public SongRow(UUID id, String title, String artist, String filePath) {
        this(id, title, artist, filePath, 0, java.time.LocalDate.now().toString());
    }

    // ===== Helpers =====

    private static String formatDuration(long seconds) {
        if (seconds <= 0) return "0:00";
        long m = seconds / 60;
        long s = seconds % 60;
        return m + ":" + String.format("%02d", s);
    }

    // ===== Properties =====

    public StringProperty timeProperty() { return time; }
    public StringProperty dateProperty() { return date; }
    public UUID getId() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty artistProperty() { return artist; }
    public String getFilePath() { return filePath; }

    public void setTitle(String title) { this.title.set(title); }
    public void setArtist(String artist) { this.artist.set(artist); }
}