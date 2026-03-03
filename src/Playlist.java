
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class Playlist {

    private UUID id;
    private String name;
    private String description;
    private List<Song> songs;

    public Playlist(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.songs = new ArrayList<>();
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public void moveSong(int from, int to) {
        if (from >= 0 && from < songs.size() //gun error 
                && to >= 0 && to < songs.size()) {

            Song temp = songs.remove(from);
            songs.add(to, temp);
        }
    }

    public List<Song> getSongs() {
        return songs;
    }
}