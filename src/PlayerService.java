
import java.util.List;
import java.util.Random;
import javafx.scene.media.MediaPlayer;

public class PlayerService {

    private boolean looping;
    private boolean shuffling;

    private Playlist currentQueue;
    private int currentIndex = -1;

    private boolean isPlaying = false;

    public PlayerService(Playlist playlist) {
        this.currentQueue = playlist;

        if (playlist != null && !playlist.getSongs().isEmpty()) {
            currentIndex = 0;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void playSong(Song song) {
        System.out.println(song.getFilePathMp3());
        if (song == null || currentQueue == null) {
            return;
        }

        currentIndex = currentQueue.getSongs().indexOf(song);

        if (currentIndex == -1) {
            currentQueue.addSong(song);
            currentIndex = currentQueue.getSongs().size() - 1;
        }

        song.play();      // ⭐ เพิ่มบรรทัดนี้
        isPlaying = true;
    }

    public void togglePlayPause() {

        if (currentSong == null) {
            return;
        }

        MediaPlayer player = currentSong.getMediaPlayer();

        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
        } else {
            player.play();
        }
    }

    public void next() {

        List<Song> songs = currentQueue.getSongs();

        if (songs.isEmpty()) {
            return;
        }

        if (shuffling) {

            Random rand = new Random();
            currentIndex = rand.nextInt(songs.size());

        } else {

            currentIndex++;

            if (currentIndex >= songs.size()) {

                if (looping) {
                    currentIndex = 0;
                } else {
                    currentIndex = songs.size() - 1;
                    return;
                }
            }
        }

        playCurrent();
    }

    public void previous() {

        List<Song> songs = currentQueue.getSongs();

        if (songs.isEmpty()) {
            return;
        }

        currentIndex--;

        if (currentIndex < 0) {

            if (looping) {
                currentIndex = songs.size() - 1;
            } else {
                currentIndex = 0;
                return;
            }
        }

        playCurrent();
    }

    public void toggleLoop() {
        looping = !looping;
    }

    public void toggleShuffle() {
        shuffling = !shuffling;
    }

    public Song getCurrentSong() {

        if (currentQueue == null) {
            return null;
        }

        List<Song> songs = currentQueue.getSongs();

        if (songs.isEmpty() || currentIndex < 0 || currentIndex >= songs.size()) {
            return null;
        }

        return songs.get(currentIndex);
    }

    public MediaPlayer getMediaPlayer() {

        Song current = getCurrentSong();

        if (current == null) {
            return null;
        }

        return current.getMediaPlayer();
    }

    private void playCurrent() {

        Song current = getCurrentSong();

        if (current != null) {
            current.play();
            isPlaying = true;
        }
    }
}
