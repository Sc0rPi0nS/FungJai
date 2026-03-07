
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

    private MediaPlayer videoPlayer;
    private PlayerService playerService;
    private Song currentSong;

    private LibraryService libraryService;
    private Label song;
    private Label artist;
    private HBox eqBars;

    public void show(Stage stage) {

        libraryService = new LibraryService();

        playerService = new PlayerService(
                libraryService.getLibrary().getAllSongs()
        );

        BorderPane root = new BorderPane();
        root.setPrefSize(500, 450);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // ================= TOP =================
        Button home = menuBtn("HOME");

        Button mySong = menuBtn("MYSONG");
mySong.setOnAction(e -> 
    new MySongWindow(this, libraryService).show(stage)
);

        Button playlist = menuBtn("MYPLAYLIST");
        playlist.setOnAction(e -> new PlaylistWindow().show(stage));

        Button mix = menuBtn("MIXFORYOU");

        HBox menuBar = new HBox(15, home, mySong, playlist, mix);
        menuBar.setPrefWidth(420);
        menuBar.setMaxWidth(420);
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPadding(new Insets(4));
        menuBar.setStyle(
                "-fx-background-color: #eeeeee;"
                + "-fx-border-color: #cccccc;"
                + "-fx-border-width: 1;"
        );

        VBox top = new VBox(menuBar);
        top.setAlignment(Pos.CENTER);
        root.setTop(top);

        // ================= CENTER =================
        URL videoUrl = getClass().getResource("/pictures/nineza123.mp4");

        if (videoUrl == null) {
            throw new RuntimeException("ไม่พบไฟล์ nineza123.mp4");
        }

        Media media = new Media(videoUrl.toExternalForm());
        videoPlayer = new MediaPlayer(media);
        videoPlayer.setMute(true);
        videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        videoPlayer.pause();

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
        });

        song = new Label("Song Title");
        artist = new Label("Artist");

        song.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        artist.setStyle("-fx-text-fill: gray;");

        HBox songLine = new HBox(6, song, new Label("–"), artist);
        songLine.setAlignment(Pos.CENTER);

        // ===== EQ Bars =====
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

        Button play = new Button("▶");

        play.setOnAction(e -> {

            playerService.togglePlayPause();

            if (playerService.isPlaying()) {
                play.setText("⏸");
                videoPlayer.play();
            } else {
                play.setText("▶");
                videoPlayer.pause();
            }
        });

        Button next = new Button("⏭");
        Button shuffle = new Button("🔀");
        Button replay = new Button("🔁");

        Slider volume = new Slider(0, 1, 0.7);
        volume.setPrefWidth(80);

        videoPlayer.volumeProperty().bind(volume.valueProperty());

        // ===== Left =====
        HBox leftControls = new HBox(3, lyrics, shuffle, prev);
        leftControls.setAlignment(Pos.CENTER_RIGHT);
        leftControls.setPrefWidth(200);

        // ===== Center =====
        StackPane centerControls = new StackPane(play);
        centerControls.setAlignment(Pos.CENTER);
        centerControls.setPrefWidth(40);

        // ===== Right =====
        HBox rightControls = new HBox(3, next, replay, new Label("🔊"), volume);
        rightControls.setAlignment(Pos.CENTER_LEFT);
        rightControls.setPrefWidth(200);

        BorderPane controlBar = new BorderPane();
        controlBar.setLeft(leftControls);
        controlBar.setCenter(centerControls);
        controlBar.setRight(rightControls);
        controlBar.setPadding(new Insets(3));

        Slider progress = new Slider();
        progress.setPrefWidth(350);

        Label time = new Label("00:00");

        HBox progressBar = new HBox(10, time, progress);
        progressBar.setAlignment(Pos.CENTER);

        VBox bottom = new VBox(8, controlBar, progressBar);
        bottom.setPadding(new Insets(10));

        root.setBottom(bottom);

        // ================= SCENE =================
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("FungJai");
        stage.setResizable(false);
        stage.setMinWidth(500);
        stage.setMinHeight(450);
        stage.setMaxWidth(500);
        stage.setMaxHeight(450);
        stage.show();
    }

    // ================= MENU BUTTON =================
    private Button menuBtn(String text) {

        Button b = new Button(text);

        b.setPrefWidth(100);
        b.setPrefHeight(32);

        b.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-border-color: transparent;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #444444;"
                + "-fx-cursor: hand;"
        );

        b.setOnMouseEntered(e
                -> b.setStyle(
                        "-fx-background-color: rgba(0,0,0,0.08);"
                        + "-fx-border-color: transparent;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: black;"
                )
        );

        b.setOnMouseExited(e
                -> b.setStyle(
                        "-fx-background-color: transparent;"
                        + "-fx-border-color: transparent;"
                        + "-fx-font-weight: bold;"
                        + "-fx-text-fill: #444444;"
                )
        );

        return b;
    }

    // ================= EQ =================
    private void setupSpectrum(MediaPlayer player) {

        player.setAudioSpectrumInterval(0.05);
        player.setAudioSpectrumNumBands(32);

        player.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {

            for (int i = 0; i < magnitudes.length; i++) {

                Rectangle bar = (Rectangle) eqBars.getChildren().get(i);

                double value = magnitudes[i] + 60;

                double height = Math.max(5, value * 2);

                bar.setScaleY(height / 20.0);
            }
        });
    }

    // ================= SET SONG =================
    public void setSongInfo(Song song) {

        if (song == null) {
            return;
        }

        this.currentSong = song;

        this.song.setText(song.getTitle());
        this.artist.setText(song.getArtist());

        playerService.playSong(song);

        MediaPlayer player = playerService.getMediaPlayer();

        if (player != null) {
            setupSpectrum(player);
        }
    }
}
