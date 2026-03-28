import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.Serializable;

public class Song extends MediaItem implements Playable, Serializable {

    private String filePathMp3;
    private long durationSec;

    private transient MediaPlayer mediaPlayer;

    private static final long serialVersionUID = 1L;

    public Song(String title, String artist, String filePathMp3, int durationSec) {
        super(title, artist); // dateAdded ถูก set ใน MediaItem แล้ว
        this.filePathMp3 = filePathMp3;
        this.durationSec = durationSec;
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            Media media = new Media(new File(filePathMp3).toURI().toString());
            media.setOnError(() -> System.out.println(media.getError()));
            mediaPlayer = new MediaPlayer(media);
        }
        return mediaPlayer;
    }

    @Override
    public void play() {
        getMediaPlayer().play();
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    @Override
    public long getDurationSrc() {
        return durationSec;
    }

    // alias ให้ SongRow เรียกได้สะดวก
    public long getDuration() {
        return durationSec;
    }

    public void setDuration(long durationSec) {
        this.durationSec = durationSec;
    }

    // คืนวันที่เป็น String "yyyy-MM-dd" จาก LocalDateTime ของ MediaItem
    public String getDateAddedString() {
        return getDateAdded() != null
                ? getDateAdded().toLocalDate().toString()
                : java.time.LocalDate.now().toString();
    }

    public String getFilePathMp3() {
        return filePathMp3;
    }

    public void dispose() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
}