
import java.util.UUID;
import java.time.LocalDateTime;
public abstract class MediaItem {
    private UUID id;
    private String title;
    private String artist;
    private LocalDateTime dateAdded;
    
    public MediaItem(String title, String artist) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.artist = artist;
        this.dateAdded = LocalDateTime.now();
    }
    
    public UUID getId(){
        return id;
    }
    
    public String getTitle(){
        return title;
    }
    
    public void setTitle (String title){
        this.title = title;
    }
    
    public String getArtist(){
        return artist;
    }
    
    public void setArtist(String arstist){
        this.artist = arstist;
    }
    
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }
}