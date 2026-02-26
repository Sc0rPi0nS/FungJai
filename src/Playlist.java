import java.util.ArrayList;

public class Playlist {

    private ArrayList<Song> songs = new ArrayList<>();
    private int index = 0;

    public void addSong(Song song) {
        songs.add(song);
    }

    public Song getCurrentSong() {
        if (songs.isEmpty()) return null;
        return songs.get(index);
    }

    public Song next() {
        if (songs.isEmpty()) return null;
        index = (index + 1) % songs.size();
        return songs.get(index);
    }

    public boolean isEmpty() {
        return songs.isEmpty();
    }
}