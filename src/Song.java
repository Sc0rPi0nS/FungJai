import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.Serializable;

public class Song extends MediaItem implements Playable, Serializable {

    private String filePathMp3;
    private int durationSec;
    private transient MediaPlayer mediaPlayer;

public Song(String title, String artist, String filePathMp3, int durationSec) {
    super(title, artist);
    this.filePathMp3 = filePathMp3;
    this.durationSec = durationSec;
}

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            Media media = new Media(new File(filePathMp3).toURI().toString());
            media.setOnError(() -> {
        System.out.println(media.getError());
    });
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
public int getDurationSrc() {
    return durationSec;
}
    
    public String getFilePathMp3(){
        return filePathMp3;
    }
}