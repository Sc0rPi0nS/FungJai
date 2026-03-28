
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.io.Serializable;

public class Playlist implements Serializable {
    //gun error waylaa load file
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String description;
    private List<Song> songs;

    public Playlist(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.songs = new ArrayList<>(); //creat list for keep the songs
    }
    
    //add songs to the playlist kub
    public void addSong(Song song) {
        songs.add(song);
    }
    
    //delete by using the ID of the songs
    public boolean removeSong(UUID songId) {
        Objects.requireNonNull(songId, "songId must not be null");
        //if songs'ID = songs'ID that want to delete then return true if delete success
        return songs.removeIf(s -> s.getId().equals(songId));
    }

    public void moveSong(int from, int to) {
        //check that index is still in the list
        if (from >= 0 && from < songs.size()
                && to >= 0 && to < songs.size()) {
            Song temp = songs.remove(from);//keep the data then delete
            songs.add(to, temp);//put in back in the index we wantt
        }
    }
    
    //get infooo
    public List<Song> getSongs() {
        return songs;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    //setters for edit na jaa
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }
}
