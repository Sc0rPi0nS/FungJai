import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayerService extends Library{

    private boolean looping;
    private boolean shuffling;
    private int volume0to100 = 50;

    private Playlist currentQueue;
    private int currentIndex = -1;

    private boolean isPlaying = false;

    public PlayerService(Playlist playlist) {
        this.currentQueue = playlist;
        if (playlist != null && !playlist.getSongs().isEmpty()) {
            currentIndex = 0;
        }
    }

    public void playSong(Song song) {
        if (song == null) return;

        currentIndex = currentQueue.getSongs().indexOf(song);
        song.play();
        isPlaying = true;
    }

    public void togglePlayPause() {
        Song current = getCurrentSong();
        if (current == null) return;

        if (isPlaying) {
            current.pause();
            isPlaying = false;
        } else {
            current.play();
            isPlaying = true;
        }
    }

    public void next() {
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) return;

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
        if (songs.isEmpty()) return;

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

    public void setVolume(int volume) {
        if (volume < 0) volume = 0;
        if (volume > 100) volume = 100;

        this.volume0to100 = volume;
    }

    public Song getCurrentSong() {
        if (currentQueue == null) return null;

        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty() || currentIndex < 0 || currentIndex >= songs.size()) {
            return null;
        }

        return songs.get(currentIndex);
    }

    private void playCurrent() {
        Song current = getCurrentSong();
        if (current != null) {
            current.play();
            isPlaying = true;
        }
    }
}