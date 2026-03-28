import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;

public class PlayerService {
    
    //Attribute
    private boolean looping;
    private boolean shuffling;
    private int loopMode = 0; // 0=off, 1=loop playlist, 2=loop one song

    private Playlist currentQueue;
    private int currentIndex = -1;

    private boolean isPlaying = false;

    private Consumer<Song> onSongChanged;

    public void setOnSongChanged(Consumer<Song> callback) {
        this.onSongChanged = callback;
    }

    //Constructor
    public PlayerService(Playlist playlist) {
        this.currentQueue = playlist;
        this.currentIndex = -1;
    }

    //getter
    public boolean isPlaying() { return isPlaying; }
    public boolean isShuffling() { return shuffling; }
    public int getLoopMode() { return loopMode; }

    //play song
    public void playSong(Song song) {
        if (song == null) return;

        Song prev = getCurrentSong();
        if (prev != null && prev != song && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        if (currentQueue != null) {
            int idx = currentQueue.getSongs().indexOf(song);
            if (idx >= 0) {
                currentIndex = idx;
            } else {
                currentQueue = null;
                currentIndex = -1;
            }
        }

        MediaPlayer player = song.getMediaPlayer();

        //When Loop=2
        player.setCycleCount(loopMode == 2 ? MediaPlayer.INDEFINITE : 1);
        player.setOnEndOfMedia(() -> {
            Song nextSong = nextInternal();
            if (nextSong != null) {
                Platform.runLater(() -> {
                    if (onSongChanged != null) onSongChanged.accept(nextSong);
                });
            } else {
                isPlaying = false;
            }
        });

        song.play();
        isPlaying = true;
    }

    //what playlist play
    public void playPlaylist(Playlist playlist, int startIndex) {
        if (playlist == null || playlist.getSongs().isEmpty()) return;
        currentQueue = playlist;
        currentIndex = startIndex;
        playSong(playlist.getSongs().get(startIndex));
    }

    //what Library play
    public void playLibrary(List<Song> songs, Song start) {
        if (songs == null || songs.isEmpty()) return;

        Song prev = getCurrentSong();
        if (prev != null && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        Playlist queue = new Playlist("LibraryQueue", "Default Playlist");
        queue.getSongs().addAll(songs);
        currentQueue = queue;
        currentIndex = songs.indexOf(start);
        if (currentIndex < 0) currentIndex = 0;

        playSong(queue.getSongs().get(currentIndex));
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

    //next
    public Song next() { return nextInternal(); }

    //nextSong
    private Song nextInternal() {
        if (currentQueue == null) return null;
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) return null;

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }

        // loopMode 2
        if (loopMode == 2) {
            if (current != null) playSong(current);
            return current;
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
                    isPlaying = false;
                    return null;
                }
            }
        }

        Song nextSong = getCurrentSong();
        if (nextSong != null) playSong(nextSong);
        return nextSong;
    }

    //prev
    public Song previous() {
        if (currentQueue == null) return null;
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty()) return null;

        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }
            if (loopMode == 2) {
        if (current != null) playSong(current);
        return current;
    }

        currentIndex--;
        if (currentIndex < 0) {
            if (loopMode >= 1) {
                currentIndex = songs.size() - 1;
            } else {
                currentIndex = 0;
                isPlaying = false;
                return null;
            }
        }

        Song prev = getCurrentSong();
        if (prev != null) playSong(prev);
        return prev;
    }

    //clear
    public void clearQueue() {
        Song current = getCurrentSong();
        if (current != null && current.getMediaPlayer() != null) {
            current.getMediaPlayer().stop();
        }
        currentQueue = null;
        currentIndex = -1;
        isPlaying = false;
    }

    //looptoggle
    public void toggleLoop() {
        loopMode = (loopMode + 1) % 3;
        looping = loopMode > 0;

        MediaPlayer player = getMediaPlayer();
        if (player != null) {
            if (loopMode == 2) {
                player.setCycleCount(MediaPlayer.INDEFINITE);
            } else {
                player.setCycleCount(1);
            }
        }
    }

    public void toggleShuffle() { shuffling = !shuffling; }

    //getterSong
    public Song getCurrentSong() {
        if (currentQueue == null) return null;
        List<Song> songs = currentQueue.getSongs();
        if (songs.isEmpty() || currentIndex < 0 || currentIndex >= songs.size()) return null;
        return songs.get(currentIndex);
    }

    //getMediaPlayer
    public MediaPlayer getMediaPlayer() {
        Song current = getCurrentSong();
        return current == null ? null : current.getMediaPlayer();
    }

    //setQueue
    public void setQueue(Playlist playlist) {
        this.currentQueue = playlist;
    }
}