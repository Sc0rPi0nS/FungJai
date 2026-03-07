import java.io.File;
import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlayerService {

    private boolean looping   = false;
    private boolean shuffling = false;
    private int     volume    = 100;

    private Playlist    currentQueue;
    private int         currentIndex = -1;
    private Song        currentSong;
    private MediaPlayer mediaPlayer;

    // UI callbacks
    private Runnable onSongChanged;

    public void setOnSongChanged(Runnable r) { onSongChanged = r; }

    // ── Play ──────────────────────────────────────────────────

    public void playPlaylist(Playlist playlist) {
        if (playlist == null || playlist.getSongs().isEmpty()) return;
        currentQueue = playlist;
        currentIndex = 0;
        playSong(currentQueue.getSongs().get(currentIndex));
    }

    public void playSong(Song song) {
        if (song == null) return;
        try {
            if (mediaPlayer != null) mediaPlayer.stop();
            currentSong = song;

            File file = new File(song.getFilePathMp4());
            if (!file.exists()) {
                System.out.println("ไม่พบไฟล์: " + song.getFilePathMp4());
                return;
            }
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(volume / 100.0);
            mediaPlayer.setOnEndOfMedia(() -> {
                if (looping) {
                    mediaPlayer.seek(javafx.util.Duration.ZERO);
                    mediaPlayer.play();
                } else {
                    next();
                }
            });
            mediaPlayer.play();
            if (onSongChanged != null)
                Platform.runLater(onSongChanged);
            System.out.println("Now playing: " + song);
        } catch (Exception e) {
            System.out.println("Cannot play: " + song.getTitle());
            e.printStackTrace();
        }
    }

    public void playSongInPlaylist(Playlist playlist, int index) {
        if (playlist == null || index < 0 || index >= playlist.getSongs().size()) return;
        currentQueue = playlist;
        currentIndex = index;
        playSong(currentQueue.getSongs().get(currentIndex));
    }

    // ── Controls ──────────────────────────────────────────────

    public void togglePlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) mediaPlayer.pause();
        else mediaPlayer.play();
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    public void next() {
        if (currentQueue == null || currentQueue.getSongs().isEmpty()) return;
        currentIndex = shuffling
            ? (int)(Math.random() * currentQueue.getSongs().size())
            : (currentIndex + 1) % currentQueue.getSongs().size();
        playSong(currentQueue.getSongs().get(currentIndex));
    }

    public void previous() {
        if (currentQueue == null || currentQueue.getSongs().isEmpty()) return;
        currentIndex = shuffling
            ? (int)(Math.random() * currentQueue.getSongs().size())
            : (currentIndex - 1 + currentQueue.getSongs().size()) % currentQueue.getSongs().size();
        playSong(currentQueue.getSongs().get(currentIndex));
    }

    public void stop() { if (mediaPlayer != null) mediaPlayer.stop(); }

    public void toggleLoop()    { looping   = !looping; }
    public void toggleShuffle() { shuffling = !shuffling; }
    public boolean isLooping()  { return looping; }
    public boolean isShuffling(){ return shuffling; }

    public void setVolume(int v) {
        volume = v;
        if (mediaPlayer != null) mediaPlayer.setVolume(v / 100.0);
    }

    public MediaPlayer getMediaPlayer() { return mediaPlayer; }
    public Song     getCurrentSong()    { return currentSong; }
    public Playlist getCurrentQueue()   { return currentQueue; }
    public int      getCurrentIndex()   { return currentIndex; }
}