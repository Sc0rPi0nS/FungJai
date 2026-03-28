import java.io.*;
import java.util.*;

public class Library implements Serializable {
    //Attribue
    private static final long serialVersionUID = 1L;
    private Playlist allSongs;

    private final List<Song> mySongs;
    private final List<Playlist> playlists;
    private transient List<Playlist> mixForYou;

    //Constructor
    public Library() {
        this.mySongs   = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.mixForYou = new ArrayList<>();
        this.allSongs  = new Playlist("All Songs", "Default playlist with all songs");
    }

    //readFileObject
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        mixForYou = new ArrayList<>(); // เริ่มใหม่เสมอ ไม่โหลดจาก disk
    }

    //getterAllSong
    public Playlist getAllSongs() { return allSongs; }
    
    
    public List<Song> getMySongs() {
        return Collections.unmodifiableList(mySongs);
    }

    public List<Playlist> getPlaylists() {
        return Collections.unmodifiableList(playlists);
    }

    public List<Playlist> getMixForYou() {
        return Collections.unmodifiableList(mixForYou);
    }

    public void addSong(Song song) {
        Objects.requireNonNull(song, "song must not be null");
        mySongs.add(song);
        allSongs.addSong(song);
    }

    public boolean removeSong(UUID songId) {
        Objects.requireNonNull(songId, "songId must not be null");
        boolean removed = mySongs.removeIf(s -> s.getId().equals(songId));
        if (!removed) return false;
        allSongs.removeSong(songId);
        for (Playlist p : playlists) p.removeSong(songId);
        for (Playlist m : mixForYou) m.removeSong(songId);
        return true;
    }

    public Playlist createPlaylist(String name, String description) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Playlist name must not be blank");
        Playlist p = new Playlist(name.trim(), description);
        playlists.add(p);
        return p;
    }

    public boolean deletePlaylist(UUID playlistId) {
        Objects.requireNonNull(playlistId, "playlistId must not be null");
        return playlists.removeIf(p -> p.getId().equals(playlistId));
    }

    public Optional<Playlist> findPlaylist(UUID playlistId) {
        Objects.requireNonNull(playlistId, "playlistId must not be null");
        return playlists.stream().filter(p -> p.getId().equals(playlistId)).findFirst();
    }

    public void addMix(Playlist mix) {
        Objects.requireNonNull(mix, "mix must not be null");
        mixForYou.add(mix);
    }

    public void clearMixes() { mixForYou.clear(); }

    public Optional<Playlist> findMix(UUID mixId) {
        Objects.requireNonNull(mixId, "mixId must not be null");
        return mixForYou.stream().filter(m -> m.getId().equals(mixId)).findFirst();
    }

    //getterSong
    public Song getSong(UUID id) {
        for (Song song : mySongs) {
            if (song.getId().equals(id)) return song;
        }
        return null;
    }
}
