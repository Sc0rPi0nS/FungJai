
import java.io.*;
import java.util.UUID;

public class LibraryService {

    private static final String LIBRARY_FILE = "library.dat";
    private static final String PLAYLIST_FILE = "playlist.dat";

    private Library library;

    public LibraryService() {
        library = loadLibrary();
    }

    public Library getLibrary() {
        return library;
    }

    // ── Songs ─────────────────────────────────────────────────
    public void addSong(Song song) {
        library.addSong(song);
        saveLibrary();
    }

    public void deleteSong(UUID id) {
        library.removeSong(id);
        saveLibrary();
    }

// Call this to update the song and trigger the forced save
    public void updateSong(UUID id, String newTitle, String newArtist) {
        Song song = getSongById(id);
        if (song != null) {
            song.setTitle(newTitle);
            song.setArtist(newArtist);
            forceSave();
        }
    }

    // A bulletproof save method that reports errors to the console
    public void forceSave() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE))) {
            out.writeObject(library);
            System.out.println("✅ Library successfully saved to disk!");
        } catch (Exception e) {
            System.out.println("❌ ERROR SAVING LIBRARY:");
            e.printStackTrace();
        }
    }

    // ── Playlists ─────────────────────────────────────────────
    public java.util.List<Playlist> getPlaylists() {
        return library.getPlaylists();
    }

    public Playlist createPlaylist(String name, String description) {
        Playlist p = library.createPlaylist(name, description);
        saveLibrary();
        return p;
    }

    public void deletePlaylist(UUID id) {
        library.deletePlaylist(id);
        saveLibrary();
    }

    public Song getSongById(UUID id) {
        return library.getMySongs()
                .stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Add a song (by id) to a playlist (by id), then persist.
     */
    public boolean addSongToPlaylist(UUID playlistId, UUID songId) {
        java.util.Optional<Playlist> op = library.findPlaylist(playlistId);
        if (op.isEmpty()) {
            return false;
        }

        Playlist playlist = op.get();

        // avoid duplicates
        boolean already = playlist.getSongs().stream()
                .anyMatch(s -> s.getId().equals(songId));
        if (already) {
            return false;
        }

        java.util.Optional<Song> os = library.getMySongs().stream()
                .filter(s -> s.getId().equals(songId))
                .findFirst();
        if (os.isEmpty()) {
            return false;
        }

        playlist.addSong(os.get());
        saveLibrary();
        return true;
    }

    /**
     * Remove a song from a playlist, then persist.
     */
    public void removeSongFromPlaylist(UUID playlistId, UUID songId) {
        library.findPlaylist(playlistId).ifPresent(p -> {
            p.removeSong(songId);
            saveLibrary();
        });
    }

    /**
     * Update playlist name / description, then persist.
     */
    public void updatePlaylist(UUID playlistId, String name, String description) {
        library.findPlaylist(playlistId).ifPresent(p -> {
            p.setName(name);
            p.setDescription(description);
            saveLibrary();
        });
    }

    // ── Persistence ───────────────────────────────────────────
    private void saveLibrary() {
        try (ObjectOutputStream out
                = new ObjectOutputStream(new FileOutputStream(LIBRARY_FILE))) {
            out.writeObject(library);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library loadLibrary() {
        try (ObjectInputStream in
                = new ObjectInputStream(new FileInputStream(LIBRARY_FILE))) {
            return (Library) in.readObject();
        } catch (Exception e) {
            return new Library();
        }
    }
}
