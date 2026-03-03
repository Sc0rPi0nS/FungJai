import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.UUID;

public class SongRow {

    private UUID id;
    private StringProperty title;
    private StringProperty artist;

    public SongRow(UUID id, String title, String artist) {
        this.id = id;
        this.title = new SimpleStringProperty(title);
        this.artist = new SimpleStringProperty(artist);
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
}