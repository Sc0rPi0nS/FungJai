
import java.util.UUID;
import java.time.LocalDateTime;
import java.io.Serializable;
//used as a base class
public abstract class MediaItem implements Serializable{
    //Attribute
    private UUID id;
    private String title;
    private String artist;
    private LocalDateTime dateAdded;
    private static final long serialVersionUID = 1L;//version for Serializable
    //Con
    public MediaItem(String title, String artist) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.artist = artist;
        this.dateAdded = LocalDateTime.now();
    }
    
    //normal set and get
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