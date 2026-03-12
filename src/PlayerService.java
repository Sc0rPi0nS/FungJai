
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
        this.currentIndex = -1;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isShuffling() {
        return shuffling;
    }

    public int getLoopMode() {
        return loopMode;
    }

    public void playSong(Song song) {

        if (song == null) {
            return;
        }

        Song prev = getCurrentSong();
        if (prev != null && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        // ถ้าเพลงอยู่ใน playlist
        if (currentQueue != null) {
            int idx = currentQueue.getSongs().indexOf(song);

            if (idx >= 0) {
                currentIndex = idx;
            } else {
                // เพลงไม่ได้อยู่ใน playlist → ออกจาก playlist
                currentQueue = null;
                currentIndex = -1;
            }
        }

song.play();

MediaPlayer player = song.getMediaPlayer();

if (player != null) {
    player.setOnEndOfMedia(() -> {

        if (loopMode == 2) { // loop one
            player.seek(player.getStartTime());
            player.play();
            return;
        }

        Song nextSong = next();

        if (nextSong != null) {
            nextSong.play();
        }
    });
}

isPlaying = true;     
    }

    public void playPlaylist(Playlist playlist, int startIndex) {

        if (playlist == null || playlist.getSongs().isEmpty()) {
            return;
        }

        currentQueue = playlist;
        currentIndex = startIndex;

        Song song = playlist.getSongs().get(startIndex);

        playSong(song);
    }

    public void togglePlayPause() {
        Song currentSong = getCurrentSong();
        if (currentSong == null) {
            return;
        }
        MediaPlayer player = currentSong.getMediaPlayer();
        if (player == null) {
            return;
        }
        if (player.getStatus() == MediaPlayer.Status.PLAYING) {
            player.pause();
            isPlaying = false;
        } else {
            player.play();
            isPlaying = true;
        }
    }
public void playLibrary(List<Song> songs, Song start) {

    if (songs == null || songs.isEmpty()) return;
    clearQueue();
    Song prev = getCurrentSong();
    if (prev != null && prev.getMediaPlayer() != null) {
        prev.getMediaPlayer().stop();
    }

    Playlist queue = new Playlist("LibraryQueue","Default Playlist");
    queue.getSongs().addAll(songs);

    currentQueue = queue;
    currentIndex = songs.indexOf(start);

    if (currentIndex < 0) currentIndex = 0;

    Song s = queue.getSongs().get(currentIndex);
    s.play();

    isPlaying = true;
}
    // Returns next Song so HomeWindow can call setSongInfo()
    public Song next() {
        if (currentQueue == null) return null;
            List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) {
            return null;
        }

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }

        if (shuffling) {
            Random rand = new Random();
            int newIndex;

            do {
                newIndex = rand.nextInt(songs.size());
            } while (songs.size() > 1 && newIndex == currentIndex);

            currentIndex = newIndex;
        } else {
            currentIndex++;
            if (currentIndex >= songs.size()) {
                if (loopMode >= 1) {
                    currentIndex = 0;
                } else {
                    currentIndex = songs.size() - 1;
                    return null;
                }
            }
        }

        Song next = getCurrentSong();
        if (next != null) {
            next.play();
            isPlaying = true;
        }
        return next;
    }
public void clearQueue() {

    Song current = getCurrentSong();

    if (current != null && current.getMediaPlayer() != null) {
        current.getMediaPlayer().stop();
    }

    currentQueue = null;
    currentIndex = -1;
    isPlaying = false;
}
    // Returns previous Song so HomeWindow can call setSongInfo()
    public Song previous() {
        if (currentQueue == null) return null;
List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) {
            return null;
        }

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }

        currentIndex--;
        if (currentIndex < 0) {
            if (loopMode >= 1) {
                currentIndex = songs.size() - 1;
            } else {
                currentIndex = 0;
                return null;
            }
        }

        Song prev = getCurrentSong();
        if (prev != null) {
            prev.play();
            isPlaying = true;
        }
        return prev;
    }

    public void toggleLoop() {
        loopMode = (loopMode + 1) % 3;
        looping = loopMode > 0;
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

    public void setQueue(Playlist playlist) {
        this.currentQueue = playlist;
    }
}
