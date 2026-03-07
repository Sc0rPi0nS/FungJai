import javafx.animation.Animation;
import javafx.beans.InvalidationListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.net.URL;

public class HomeWindow {

    private MediaPlayer videoPlayer;
    private final PlayerService playerService = PlaylistWindow.getPlayerService();

    private Label song;
    private Label artist;
    private Label time;
    private Label totalTime;
    private Button play;
    private Slider progress;
    private Slider volume;
    private HBox eqBars;

    private boolean userDraggingProgress = false;
    private MediaPlayer boundPlayer;
    private InvalidationListener currentTimeListener;

    public void show(Stage stage) {
        PlaylistWindow.ensureDefaults();

        BorderPane root = new BorderPane();
        root.setPrefSize(500, 450);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Button home = menuBtn("HOME");
        Button mySong = menuBtn("MYSONG");
        Button playlist = menuBtn("MYPLAYLIST");
        playlist.setOnAction(e -> new PlaylistWindow().show(stage));
        Button mix = menuBtn("MIXFORYOU");

        HBox menuBar = new HBox(15, home, mySong, playlist, mix);
        menuBar.setPrefWidth(420);
        menuBar.setMaxWidth(420);
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPadding(new Insets(4));
        menuBar.setStyle(
            "-fx-background-color: #eeeeee;" +
            "-fx-border-color: #cccccc;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 0" +
            "-fx-background-radius: 0"
        );

        VBox top = new VBox(menuBar);
        top.setAlignment(Pos.CENTER);
        root.setTop(top);

        URL videoUrl = getClass().getResource("/pictures/nineza123.mp4");
        if (videoUrl == null) {
            throw new RuntimeException("ไม่พบไฟล์ nineza123.mp4");
        }

        Media media = new Media(videoUrl.toExternalForm());
        videoPlayer = new MediaPlayer(media);
        videoPlayer.setMute(true);
        videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        videoPlayer.play();

        MediaView videoView = new MediaView(videoPlayer);
        videoView.setFitWidth(370);
        videoView.setFitHeight(200);
        videoView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(370, 200);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        videoView.setClip(clip);

        videoPlayer.setOnReady(() -> {
            videoView.setViewport(new Rectangle2D(200, 100, 1500, 900));
            videoPlayer.play();
        });

        song = new Label("Song Title");
        artist = new Label("Artist");
        song.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        artist.setStyle("-fx-text-fill: gray;");

        HBox songLine = new HBox(6, song, new Label("–"), artist);
        songLine.setAlignment(Pos.CENTER);

        eqBars = new HBox(2);
        eqBars.setAlignment(Pos.CENTER);
        for (int i = 0; i < 26; i++) {
            Rectangle bar = new Rectangle(8, 15);
            bar.setFill(javafx.scene.paint.Color.GREY);
            eqBars.getChildren().add(bar);
        }

        VBox center = new VBox(10, videoView, songLine, eqBars);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10));
        root.setCenter(center);

        Button lyrics = new Button("Lyrics");
        Button prev = new Button("⏮");
        play = new Button("▶");
        Button next = new Button("⏭");
        Button shuffle = new Button("🔀");
        Button replay = new Button("🔁");

        prev.setOnAction(e -> {
            playerService.previous();
            bindToCurrentPlayer();
            updatePlayerUI();
        });

        play.setOnAction(e -> {
            playerService.togglePlayPause();
            updatePlayButton();
        });

        next.setOnAction(e -> {
            playerService.next();
            bindToCurrentPlayer();
            updatePlayerUI();
        });

        shuffle.setOnAction(e -> playerService.toggleShuffle());
        replay.setOnAction(e -> playerService.toggleLoop());

        volume = new Slider(0, 1, 0.7);
        volume.setPrefWidth(80);
        videoPlayer.volumeProperty().bind(volume.valueProperty());
        volume.valueProperty().addListener((obs, oldV, newV) -> {
            playerService.setVolume((int) Math.round(newV.doubleValue() * 100));
        });

        HBox leftControls = new HBox(3, lyrics, shuffle, prev);
        leftControls.setAlignment(Pos.CENTER_RIGHT);
        leftControls.setPrefWidth(200);
        leftControls.setPadding(new Insets(0, 0, 0, 0));

        StackPane centerControls = new StackPane(play);
        centerControls.setAlignment(Pos.CENTER);
        centerControls.setPrefWidth(40);
        centerControls.setMinWidth(40);
        centerControls.setMaxWidth(40);

        HBox rightControls = new HBox(3, next, replay, new Label("🔊"), volume);
        rightControls.setAlignment(Pos.CENTER_LEFT);
        rightControls.setPrefWidth(200);
        rightControls.setPadding(new Insets(0, 0, 0, 0));

        BorderPane controlBar = new BorderPane();
        controlBar.setLeft(leftControls);
        controlBar.setCenter(centerControls);
        controlBar.setRight(rightControls);
        controlBar.setPadding(new Insets(3));

        progress = new Slider();
        progress.setPrefWidth(300);
        progress.setMin(0);
        progress.setMax(100);

        time = new Label("00:00");
        totalTime = new Label("00:00");

        progress.setOnMousePressed(e -> userDraggingProgress = true);
        progress.setOnMouseReleased(e -> {
            MediaPlayer current = playerService.getMediaPlayer();
            if (current != null && current.getTotalDuration() != null && !current.getTotalDuration().isUnknown()) {
                current.seek(javafx.util.Duration.seconds(progress.getValue()));
            }
            userDraggingProgress = false;
        });

        HBox progressBar = new HBox(10, time, progress, totalTime);
        progressBar.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(8, controlBar, progressBar);
        bottom.setPadding(new Insets(10));
        root.setBottom(bottom);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("FungJai");
        stage.setResizable(false);
        stage.setMinWidth(500);
        stage.setMinHeight(450);
        stage.setMaxWidth(500);
        stage.setMaxHeight(450);

        playerService.setOnSongChanged(() -> {
            bindToCurrentPlayer();
            updatePlayerUI();
        });

        updatePlayerUI();
        stage.show();
    }

    private void bindToCurrentPlayer() {
        MediaPlayer current = playerService.getMediaPlayer();
        if (boundPlayer == current) {
            return;
        }

        if (boundPlayer != null && currentTimeListener != null) {
            boundPlayer.currentTimeProperty().removeListener(currentTimeListener);
        }

        boundPlayer = current;
        if (boundPlayer == null) {
            clearSpectrum();
            return;
        }

        boundPlayer.setAudioSpectrumInterval(0.05);
        boundPlayer.setAudioSpectrumNumBands(26);
        boundPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            for (int i = 0; i < eqBars.getChildren().size() && i < magnitudes.length; i++) {
                Rectangle bar = (Rectangle) eqBars.getChildren().get(i);
                double value = magnitudes[i] + 60;
                if (i > 10 && i < 20) {
                    value *= 2;
                }
                double height = Math.max(5, value * 2);
                bar.setHeight(15);
                bar.setScaleY(height / 20.0);
            }
        });

        boundPlayer.setOnReady(() -> {
            progress.setMax(boundPlayer.getTotalDuration().toSeconds());
            totalTime.setText(formatDuration(boundPlayer.getTotalDuration()));
            updatePlayButton();
        });

        currentTimeListener = obs -> {
            if (!userDraggingProgress) {
                progress.setValue(boundPlayer.getCurrentTime().toSeconds());
            }
            time.setText(formatDuration(boundPlayer.getCurrentTime()));
            if (boundPlayer.getTotalDuration() != null && !boundPlayer.getTotalDuration().isUnknown()) {
                totalTime.setText(formatDuration(boundPlayer.getTotalDuration()));
            }
            updatePlayButton();
        };

        boundPlayer.currentTimeProperty().addListener(currentTimeListener);
        updatePlayButton();
    }

    private void updatePlayerUI() {
        Song currentSong = playerService.getCurrentSong();
        if (currentSong == null) {
            song.setText("Song Title");
            artist.setText("Artist");
            time.setText("00:00");
            totalTime.setText("00:00");
            progress.setValue(0);
            updatePlayButton();
            clearSpectrum();
            return;
        }

        song.setText(currentSong.getTitle());
        String artistName = currentSong.getArtist();
        artist.setText(artistName == null || artistName.isBlank() ? "Unknown Artist" : artistName);
        updatePlayButton();
    }

    private void updatePlayButton() {
        play.setText(playerService.isPlaying() ? "⏸" : "▶");
    }

    private void clearSpectrum() {
        for (int i = 0; i < eqBars.getChildren().size(); i++) {
            Rectangle bar = (Rectangle) eqBars.getChildren().get(i);
            bar.setScaleY(1.0);
        }
    }

    private String formatDuration(javafx.util.Duration duration) {
        if (duration == null || duration.isUnknown()) {
            return "00:00";
        }
        int totalSeconds = (int) Math.floor(duration.toSeconds());
        int mins = Math.max(0, totalSeconds) / 60;
        int secs = Math.max(0, totalSeconds) % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private Button menuBtn(String text) {
        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setPrefHeight(32);
        b.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #444444;" +
            "-fx-cursor: hand;"
        );
        b.setOnMouseEntered(e ->
            b.setStyle(
                "-fx-background-color: rgba(0,0,0,0.08);" +
                "-fx-border-color: transparent;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: black;"
            )
        );
        b.setOnMouseExited(e ->
            b.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-border-color: transparent;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #444444;"
            )
        );
        return b;
    }
}