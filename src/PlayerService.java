import java.util.List;
import java.util.Random;
import javafx.scene.media.MediaPlayer;

public class PlayerService {

    private boolean looping;
    private boolean shuffling;
    private int loopMode = 0; // 0=off, 1=loop all, 2=loop one

    private Playlist currentQueue;
    private int currentIndex = -1;

    private boolean isPlaying = false;

    public PlayerService(Playlist playlist) {
        this.currentQueue = playlist;
        if (playlist != null && !playlist.getSongs().isEmpty()) {
            currentIndex = 0;
        }
    }

    public boolean isPlaying()    { return isPlaying; }
    public boolean isShuffling()  { return shuffling; }
    public int getLoopMode()      { return loopMode; }

    public void playSong(Song song) {
        if (song == null || currentQueue == null) return;

        Song prev = getCurrentSong();
        if (prev != null && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        currentIndex = currentQueue.getSongs().indexOf(song);
        if (currentIndex == -1) {
            currentQueue.addSong(song);
            currentIndex = currentQueue.getSongs().size() - 1;
        }

        song.play();
        isPlaying = true;
    }

    public void togglePlayPause() {
        Song currentSong = getCurrentSong();
        if (currentSong == null) return;
        MediaPlayer player = currentSong.getMediaPlayer();
        if (player == null) return;
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
            isPlaying = false;
        } else {
            player.play();
            isPlaying = true;
        }
    }

    // Returns next Song so HomeWindow can call setSongInfo()
    public Song next() {
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) return null;

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }

        if (shuffling) {
            currentIndex = new Random().nextInt(songs.size());
        } else {
            currentIndex++;
            if (currentIndex >= songs.size()) {
                if (loopMode >= 1) { currentIndex = 0; }
                else { currentIndex = songs.size() - 1; return null; }
            }
        }

        Song next = getCurrentSong();
        if (next != null) { next.play(); isPlaying = true; }
        return next;
    }

    // Returns previous Song so HomeWindow can call setSongInfo()
    public Song previous() {
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) return null;

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }

        currentIndex--;
        if (currentIndex < 0) {
            if (loopMode >= 1) { currentIndex = songs.size() - 1; }
            else { currentIndex = 0; return null; }
        }

        Song prev = getCurrentSong();
        if (prev != null) { prev.play(); isPlaying = true; }
        return prev;
    }

    public void toggleLoop()    { loopMode = (loopMode + 1) % 3; looping = loopMode > 0; }
    public void toggleShuffle() { shuffling = !shuffling; }

    public Song getCurrentSong() {
        if (currentQueue == null) return null;
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty() || currentIndex < 0 || currentIndex >= songs.size()) return null;
        return songs.get(currentIndex);
    }

    public MediaPlayer getMediaPlayer() {
        Song current = getCurrentSong();
        if (current == null) return null;
        return current.getMediaPlayer();
    }
}