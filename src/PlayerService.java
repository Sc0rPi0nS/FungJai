
import java.util.List;
import javafx.scene.media.MediaPlayer;
import java.util.Collections;

public class PlayerService {

    private boolean looping;
    private boolean shuffling;

    private Playlist currentQueue;
    private Song currentSong;
    private int currentIndex = -1;
    private List<Integer> shuffleOrder;
    private int shuffleIndex = 0;

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

    if (song == null || currentQueue == null) return;

    if (currentSong != null) {
        currentSong.stop();
    }

    currentIndex = currentQueue.getSongs().indexOf(song);

    if (currentIndex == -1) {
        currentQueue.addSong(song);
        currentIndex = currentQueue.getSongs().size() - 1;
    }

    // ⭐ sync shuffleIndex
    if (shuffling && shuffleOrder != null) {
        shuffleIndex = shuffleOrder.indexOf(currentIndex);
    }

    currentSong = song;

    song.getMediaPlayer().seek(javafx.util.Duration.ZERO);
    song.play();

    isPlaying = true;
}

    public void togglePlayPause() {

        if (currentSong == null) {
            return;
        }

        MediaPlayer player = currentSong.getMediaPlayer();

        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
            isPlaying = false;
        } else {
            player.play();
            isPlaying = true;
        }
    }

public void toggleShuffle() {

    shuffling = !shuffling;

    if (currentQueue == null) return;

    List<Song> songs = currentQueue.getSongs();

    if (shuffling) {

        shuffleOrder = new java.util.ArrayList<>();

        for (int i = 0; i < songs.size(); i++) {
            shuffleOrder.add(i);
        }

        Collections.shuffle(shuffleOrder, new java.util.Random());

        // ให้เพลงปัจจุบันอยู่ตำแหน่งแรก
        Song current = getCurrentSong();
        if (current != null) {

            int currentIdx = songs.indexOf(current);

            shuffleOrder.remove((Integer) currentIdx);
            shuffleOrder.add(0, currentIdx);

            shuffleIndex = 0;
        }

    } else {
        shuffleOrder = null;
    }
}

public Song next() {

    List<Song> songs = currentQueue.getSongs();
    if (songs.isEmpty()) return null;

    if (shuffling) {

        shuffleIndex++;

        if (shuffleIndex >= shuffleOrder.size()) {

            if (looping) {

                int lastPlayed = currentIndex;

                Collections.shuffle(shuffleOrder);

                if (shuffleOrder.get(0) == lastPlayed && shuffleOrder.size() > 1) {
                    Collections.swap(shuffleOrder, 0, 1);
                }

                shuffleIndex = 0;

            } else {
                return null;
            }
        }

        currentIndex = shuffleOrder.get(shuffleIndex);

    } else {

        currentIndex++;

        if (currentIndex >= songs.size()) {

            if (looping) {
                currentIndex = 0;
            } else {
                return null;
            }
        }
    }

    playCurrent();
    return getCurrentSong();
}

public Song previous() {

    List<Song> songs = currentQueue.getSongs();

    if (songs.isEmpty()) return null;

    if (shuffling) {

        shuffleIndex--;

        if (shuffleIndex < 0) {

            if (looping) {
                shuffleIndex = shuffleOrder.size() - 1;
            } else {
                shuffleIndex = 0;
                return getCurrentSong();
            }
        }

        currentIndex = shuffleOrder.get(shuffleIndex);

    } else {

        currentIndex--;

        if (currentIndex < 0) {

            if (looping) {
                currentIndex = songs.size() - 1;
            } else {
                currentIndex = 0;
                return getCurrentSong();
            }
        }
    }

    playCurrent();
    return getCurrentSong();
}

    public boolean isShuffling(){
        return shuffling;
    }
            public boolean isLooping(){
        return looping;
            }
    public void toggleLoop() {
        looping = !looping;
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

        // ⭐ หยุดเพลงเก่าก่อน
        if (currentSong != null) {
            currentSong.stop();
        }

        Song current = getCurrentSong();

        if (current != null) {

            currentSong = current;

            current.getMediaPlayer().seek(javafx.util.Duration.ZERO);

            current.play();

            isPlaying = true;
        }
    }
}
