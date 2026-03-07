import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.UUID;

public class SongRow {

    private UUID id;
    private StringProperty title;
    private StringProperty artist;
    private String filePath;

    public SongRow(UUID id, String title, String artist, String filePath) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
        this.filePath = filePath;
    }

    public UUID getId() {
        return id;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty artistProperty() {
        return artist;
    }

    public String getFilePath() {
        return filePath;
    }
}