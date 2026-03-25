import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.media.*;
import javafx.scene.shape.*;
import javafx.geometry.Rectangle2D;
import java.net.URL;

public class HomeWindow {

    private transient MediaPlayer videoPlayer;
    private PlayerService playerService;
    private Song currentSong;
    private Playlist currentPlaylist;

    private LibraryService libraryService;

    private Label song;
    private Label artist;
    private Label time;

    private HBox eqBars;

    private Slider progress;
    private Slider volume;

    private Button play;

    public void show(Stage stage) {

        libraryService = new LibraryService();

        playerService = new PlayerService(
                libraryService.getLibrary().getAllSongs()
        );
        

        BorderPane root = new BorderPane();
        root.setPrefSize(500, 450);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // ================= TOP =================
        Button home = menuBtn("HOME", true);
        Button mySong = menuBtn("MYSONG", false);
        Button playlist = menuBtn("MYPLAYLIST", false);
        Button mix = menuBtn("MIXFORYOU", false);

        mySong.setOnAction(e ->
                new MySongWindow(this, libraryService).show(stage)
        );

        // ⭐ pass libraryService so PlaylistWindow uses real Playlist objects
        playlist.setOnAction(e -> new PlaylistWindow(this, libraryService).show(stage));
        mix.setOnAction(e-> new MixForYouWindow(this,libraryService).show(stage));

        HBox menuBar = new HBox(15, home, mySong, playlist, mix);
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPadding(new Insets(4));
        menuBar.setStyle(
                "-fx-background-color: #eeeeee;"
                + "-fx-border-color: #cccccc;"
        );

        root.setTop(menuBar);

        // ================= CENTER =================
        URL videoUrl = getClass().getResource("/pictures/nineza123.mp4");

        // ⭐ Only load video if file actually exists — no crash if missing
        MediaView videoView = new MediaView();
        videoView.setFitWidth(370);
        videoView.setFitHeight(200);
        videoView.setPreserveRatio(false);

        Rectangle clip = new Rectangle(370, 200);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        videoView.setClip(clip);

        if (videoUrl != null) {
            Media media = new Media(videoUrl.toExternalForm());
            videoPlayer = new MediaPlayer(media);
            videoPlayer.setMute(true);
            videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            videoView.setMediaPlayer(videoPlayer);
            videoPlayer.setOnReady(() ->
                    videoView.setViewport(new Rectangle2D(200, 100, 1500, 900))
            );
        }

        song = new Label("Song Title");
        artist = new Label("Artist");

        song.setStyle("-fx-font-size:14px;-fx-font-weight:bold;");
        artist.setStyle("-fx-text-fill:gray;");

        HBox songLine = new HBox(6, song, new Label("–"), artist);
        songLine.setAlignment(Pos.CENTER);

        eqBars = new HBox(2);
        eqBars.setAlignment(Pos.CENTER);

        for (int i = 0; i < 32; i++) {
            Rectangle bar = new Rectangle(8, 15);
            bar.setFill(javafx.scene.paint.Color.GREY);
            eqBars.getChildren().add(bar);
        }

        VBox center = new VBox(10, videoView, songLine, eqBars);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(10));

        root.setCenter(center);

        // ================= BOTTOM =================
        Button lyrics = new Button("Lyrics");
        Button prev = new Button("⏮");
        play = new Button("▶");

        Button next = new Button("⏭");
        Button shuffle = new Button("🔀");
        Button replay = new Button("🔁");

        volume = new Slider(0, 1, 0.7);
        volume.setPrefWidth(80);

        volume.valueProperty().addListener((obs, oldVal, newVal) -> {
            MediaPlayer player = playerService.getMediaPlayer();
            if (player != null) {
                player.setVolume(newVal.doubleValue());
            }
        });

        HBox leftControls = new HBox(3, lyrics, shuffle, prev);
        leftControls.setAlignment(Pos.CENTER_RIGHT);
        leftControls.setPrefWidth(200);

        StackPane centerControls = new StackPane(play);
        centerControls.setAlignment(Pos.CENTER);

        HBox rightControls = new HBox(3, next, replay, new Label("🔊"), volume);
        rightControls.setAlignment(Pos.CENTER_LEFT);
        rightControls.setPrefWidth(200);

        BorderPane controlBar = new BorderPane();
        controlBar.setLeft(leftControls);
        controlBar.setCenter(centerControls);
        controlBar.setRight(rightControls);

        progress = new Slider();
        progress.setMin(0);
        progress.setPrefWidth(350);
        progress.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-control-inner-background: #cccccc;"
        );
        progress.setPrefHeight(4);

        StackPane progressStack = new StackPane(progress);
        progressStack.setPrefWidth(350);
        progressStack.setMaxWidth(350);

        progress.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateProgressColor();
            MediaPlayer player = playerService.getMediaPlayer();
            if (player != null && progress.isValueChanging()) {
                player.seek(javafx.util.Duration.seconds(newVal.doubleValue()));
            }
        });

        time = new Label("00:00");

        HBox progressBar = new HBox(10, time, progressStack);
        progressBar.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(8, controlBar, progressBar);
        bottom.setPadding(new Insets(10));

        root.setBottom(bottom);

        // ================= BUTTON EVENTS =================
        play.setOnAction(e -> {

            if (playerService.getCurrentSong() == null) return;

            playerService.togglePlayPause();

            if (playerService.isPlaying()) {
                play.setText("⏸");
                if (videoPlayer != null) videoPlayer.play();
            } else {
                play.setText("▶");
                if (videoPlayer != null) videoPlayer.pause();
            }
        });

        next.setOnAction(e -> {
            Song s = playerService.next();
            if (s != null) setSongInfo(s,currentPlaylist);
        });

        prev.setOnAction(e -> {
            Song s = playerService.previous();
            if (s != null) setSongInfo(s,currentPlaylist);
        });

        shuffle.setOnAction(e -> {
            playerService.toggleShuffle();
            if (playerService.isShuffling()) {
                shuffle.setStyle("-fx-text-fill:green;");
            } else {
                shuffle.setStyle("");
            }
        });

        replay.setOnAction(e -> {
            playerService.toggleLoop();
            int mode = playerService.getLoopMode();
            if (mode == 0) {
                replay.setText("🔁");
                replay.setStyle("");
            } else if (mode == 1) {
                replay.setText("🔁");
                replay.setStyle("-fx-text-fill:green;");
            } else if (mode == 2) {
                replay.setText("🔂");
                replay.setStyle("-fx-text-fill:green;");
            }
        });

        lyrics.setOnAction(e -> {
            Song current = playerService.getCurrentSong();
            if (current != null) {
                String url = "https://www.musixmatch.com/lyrics/"
                        + current.getArtist() + "/" + current.getTitle();
                try {
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // ================= SCENE =================
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("FungJai");
        stage.setResizable(false);
        stage.show();
    }

    // ================= MENU BUTTON =================
    private Button menuBtn(String text, boolean active) {

        Button b = new Button(text);
        b.setPrefWidth(100);
        b.setPrefHeight(32);

        String activeStyle = "-fx-background-color:#4773a1;-fx-font-weight:bold;-fx-text-fill:black;-fx-cursor:hand;";
        String normalStyle = "-fx-background-color:transparent;-fx-font-weight:bold;-fx-text-fill:#444;-fx-cursor:hand;";

        b.setStyle(active ? activeStyle : normalStyle);
        b.setOnMouseEntered(e -> { if (!active) b.setStyle(activeStyle); });
        b.setOnMouseExited(e  -> { if (!active) b.setStyle(normalStyle); });

        return b;
    }

    // ================= EQ =================
 private void setupSpectrum(MediaPlayer player) {

    player.setAudioSpectrumInterval(0.05);
    player.setAudioSpectrumNumBands(32);

    player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {

        javafx.application.Platform.runLater(() -> {

            for (int i = 0; i < magnitudes.length && i < eqBars.getChildren().size(); i++) {

                Rectangle bar = (Rectangle) eqBars.getChildren().get(i);

                double value = magnitudes[i] + 60; // normalize

                if (i > 12 && i < 25) {
                    value *= 2;
                }

                double height = Math.max(5, value * 2);

                bar.setScaleY(height / 20.0);
            }

        });

    });
}
    private void updateProgressColor() {

        double percent = (progress.getValue() - progress.getMin())
                / (progress.getMax() - progress.getMin()) * 100;

        Node track = progress.lookup(".track");

        if (track != null) {
            track.setStyle(String.format(
                    "-fx-background-color: linear-gradient(to right, #4773a1 %.2f%%, #cccccc %.2f%%);",
                    percent, percent
            ));
        }
    }
    
    private void bindPlayer(MediaPlayer player) {

    player.currentTimeProperty().addListener((obs, oldTime, newTime) -> {

        double seconds = newTime.toSeconds();

        progress.setValue(seconds);

        int min = (int) seconds / 60;
        int sec = (int) seconds % 60;

        time.setText(String.format("%02d:%02d", min, sec));

        updateProgressColor();
    });

    player.setOnReady(() -> {
        progress.setMax(player.getTotalDuration().toSeconds());
    });
}

    // ================= SET SONG =================
public void setSongInfo(Song song, Playlist playlist) {
    
    if (song == null) return;

    this.currentSong = song;
    this.currentPlaylist = playlist;

    this.song.setText(song.getTitle());
    this.artist.setText(song.getArtist());

    if (playlist != null) {
        playerService.setQueue(playlist);
    }
    
    playerService.playSong(song);

    MediaPlayer player = song.getMediaPlayer(); // ⭐ ใช้ตัวนี้

    setupSpectrum(player);
    bindPlayer(player);

    play.setText("⏸");
}

public PlayerService getPlayerService() {
    return playerService;
}
}