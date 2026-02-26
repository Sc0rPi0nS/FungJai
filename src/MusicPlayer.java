import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.function.Consumer;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;
    private Runnable onFinish;
    private Consumer<float[]> spectrumListener;

    static {
        new JFXPanel(); // init JavaFX
    }

    public void load(File file) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        Media media = new Media(file.toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setAudioSpectrumInterval(0.05);
        mediaPlayer.setAudioSpectrumNumBands(64);

        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            if (spectrumListener != null) {
                spectrumListener.accept(magnitudes);
            }
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            if (onFinish != null) onFinish.run();
        });
    }

    public void play() {
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void setOnFinish(Runnable r) {
        this.onFinish = r;
    }

    public void setOnSpectrum(Consumer<float[]> c) {
        this.spectrumListener = c;
    }
}