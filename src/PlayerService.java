
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.media.MediaPlayer;

public class PlayerService {

    private boolean looping;
    private boolean shuffling;
    private int loopMode = 0; // 0=off, 1=loop all, 2=loop one

    private Playlist currentQueue;
    private int currentIndex = -1;

    private boolean isPlaying = false;

    /**
     * Callback ที่ HomeWindow ลงทะเบียนไว้ จะถูกเรียก (บน FX thread)
     * ทุกครั้งที่เพลงเปลี่ยนอัตโนมัติ
     */
    private Consumer<Song> onSongChanged;

    public void setOnSongChanged(Consumer<Song> callback) {
        this.onSongChanged = callback;
    }

    // ── Constructor ─────────────────────────────────────────
    public PlayerService(Playlist playlist) {
        this.currentQueue = playlist;
        this.currentIndex = -1;
    }

    // ── Getters ──────────────────────────────────────────────
    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isShuffling() {
        return shuffling;
    }

    public int getLoopMode() {
        return loopMode;
    }

    // ── Core playback ─────────────────────────────────────────
    /**
     * เล่นเพลงที่ระบุ และผูก setOnEndOfMedia ให้อัตโนมัติ ตั้ง listener ก่อน
     * play เพื่อไม่พลาดกรณีเพลงสั้นมาก
     */
    public void playSong(Song song) {
        if (song == null) {
            return;
        }

        // หยุดเพลงก่อนหน้า (ถ้าไม่ใช่เพลงเดิม)
        Song prev = getCurrentSong();
        if (prev != null && prev != song && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        // อัปเดต index ใน queue
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

        // ผูก setOnEndOfMedia ก่อน play เสมอ
        player.setOnEndOfMedia(() -> {
            if (loopMode == 2) {
                // Loop one — เริ่มใหม่ + แจ้ง HomeWindow rebind progress
                Platform.runLater(() -> {
                    player.seek(player.getStartTime());
                    player.play();
                    if (onSongChanged != null) {
                        onSongChanged.accept(song);
                    }
                });
                return;
            }

            // ไปเพลงถัดไป
            Song nextSong = nextInternal();
            if (nextSong != null) {
                // แจ้ง HomeWindow บน FX thread เพื่ออัปเดต UI
                Platform.runLater(() -> {
                    if (onSongChanged != null) {
                        onSongChanged.accept(nextSong);
                    }
                });
            } else {
                isPlaying = false;
            }
        });

        song.play();
        isPlaying = true;
    }

    public void playPlaylist(Playlist playlist, int startIndex) {
        if (playlist == null || playlist.getSongs().isEmpty()) {
            return;
        }
        currentQueue = playlist;
        currentIndex = startIndex;
        playSong(playlist.getSongs().get(startIndex));
    }

    public void playLibrary(List<Song> songs, Song start) {
        if (songs == null || songs.isEmpty()) {
            return;
        }

        Song prev = getCurrentSong();
        if (prev != null && prev.getMediaPlayer() != null) {
            prev.getMediaPlayer().stop();
        }

        Playlist queue = new Playlist("LibraryQueue", "Default Playlist");
        queue.getSongs().addAll(songs);
        currentQueue = queue;
        currentIndex = songs.indexOf(start);
        if (currentIndex < 0) {
            currentIndex = 0;
        }

        playSong(queue.getSongs().get(currentIndex));
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

    // ── Navigation ────────────────────────────────────────────
    /**
     * เรียกจากปุ่ม ⏭ ใน HomeWindow
     */
    public Song next() {
        return nextInternal();
    }

    /**
     * ใช้ภายใน — เปลี่ยนเพลงถัดไปและ play ทันที คืนค่า Song ที่เล่นอยู่
     */
    private Song nextInternal() {
        if (currentQueue == null) {
            return null;
        }
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
                    isPlaying = false;
                    return null;
                }
            }
        }

        Song nextSong = getCurrentSong();
        if (nextSong != null) {
            playSong(nextSong); // playSong จะผูก setOnEndOfMedia ใหม่
        }
        return nextSong;
    }

    public Song previous() {
        if (currentQueue == null) {
            return null;
        }
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
                isPlaying = false;
                return null;
            }
        }

        Song prev = getCurrentSong();
        if (prev != null) {
            playSong(prev);
        }
        return prev;
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

    // ── Toggle ────────────────────────────────────────────────
    public void toggleLoop() {
        loopMode = (loopMode + 1) % 3;
        looping = loopMode > 0;
    }

    public void toggleShuffle() {
        shuffling = !shuffling;
    }

    // ── Query ────────────────────────────────────────────────
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
        return current == null ? null : current.getMediaPlayer();
    }

    public void setQueue(Playlist playlist) {
        this.currentQueue = playlist;
    }
}
