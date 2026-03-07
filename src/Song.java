import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.io.Serializable;

public class Song extends MediaItem implements Playable, Serializable {

    private String filePathMp3;
    private MediaPlayer mediaPlayer;

    public Song(String title, String artist, String filePathMp3, int durationSec) {
        super(title, artist);
        this.filePathMp3 = filePathMp3;
    }

    public MediaPlayer getMediaPlayer() {
        if (mediaPlayer == null) {
            Media media = new Media(new File(filePathMp3).toURI().toString());
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
        return 0;
    }
    
    public String getFilePathMp3(){
        return filePathMp3;
    }
}