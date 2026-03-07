import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.UUID;

public class SongRow {

    private UUID id;
    private StringProperty title;
    private StringProperty artist;
    private StringProperty time;
    private StringProperty dateAdded;
    private String filePath;

    public SongRow(UUID id, String title, String artist, String time, String dateAdded, String filePath) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.time = new SimpleStringProperty(time);
        this.dateAdded = new SimpleStringProperty(dateAdded);
        this.filePath = filePath;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title.get();
    }

    public String getArtist() {
        return artist.get();
    }

    public String getTime() {
        return time.get();
    }

    public String getDateAdded() {
        return dateAdded.get();
    }

    public String getFilePath() {
        return filePath;
    }
}