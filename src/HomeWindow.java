
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
    ;
    private PlayerService playerService;
    private Song currentSong;

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
        mySong.setOnAction(e
                -> new MySongWindow(this, libraryService).show(stage)
        );
        playlist.setOnAction(e -> new PlaylistWindow().show(stage));
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

        Media media = new Media(videoUrl.toExternalForm());
        videoPlayer = new MediaPlayer(media);
        videoPlayer.setMute(true);
        videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        MediaView videoView = new MediaView(videoPlayer);
        videoView.setFitWidth(370);
        videoView.setFitHeight(200);

        Rectangle clip = new Rectangle(370, 200);
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        videoView.setClip(clip);

        videoPlayer.setOnReady(()
                -> videoView.setViewport(new Rectangle2D(200, 100, 1500, 900))
        );

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
        progress.setStyle("-fx-background-color:transparent;");

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

                player.seek(
                        javafx.util.Duration.seconds(newVal.doubleValue())
                );
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

            if (playerService.getCurrentSong() == null) {
                return;
            }

            playerService.togglePlayPause();

            if (playerService.isPlaying()) {

                play.setText("⏸");
                videoPlayer.play();

            } else {

                play.setText("▶");
                videoPlayer.pause();
            }
        });

        next.setOnAction(e -> {

            Song song = playerService.next();

            if (song != null) {
                setSongInfo(song);
            }
        });

        prev.setOnAction(e -> {

            Song song = playerService.previous();

            if (song != null) {
                setSongInfo(song);
            }
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

            if (playerService.isLooping()) {
                replay.setStyle("-fx-text-fill:green;");
            } else {
                replay.setStyle("");
            }
        });

        lyrics.setOnAction(e -> {

            Song current = playerService.getCurrentSong();

            if (current != null) {

                String url
                        = "https://www.musixmatch.com/lyrics/"
                        + current.getArtist()
                        + "/"
                        + current.getTitle();

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

        if (active) {
            b.setStyle(
                    "-fx-background-color:#4773a1;"
                    + "-fx-font-weight:bold;"
                    + "-fx-text-fill:black;"
                    + "-fx-cursor:hand;"
            );
        } else {
            b.setStyle(
                    "-fx-background-color:transparent;"
                    + "-fx-font-weight:bold;"
                    + "-fx-text-fill:#444;"
                    + "-fx-cursor:hand;"
            );
        }

        b.setOnMouseEntered(e -> {
            if (!active) {
                b.setStyle(
                        "-fx-background-color:#4773a1;"
                        + "-fx-font-weight:bold;"
                        + "-fx-text-fill:black;"
                        + "-fx-cursor:hand;"
                );
            }
        });

        b.setOnMouseExited(e -> {
            if (!active) {
                b.setStyle(
                        "-fx-background-color:transparent;"
                        + "-fx-font-weight:bold;"
                        + "-fx-text-fill:#444;"
                        + "-fx-cursor:hand;"
                );
            }
        });

        return b;
    }

    // ================= EQ =================
    private void setupSpectrum(MediaPlayer player) {

        player.setAudioSpectrumInterval(0.05);
        player.setAudioSpectrumNumBands(32);

        player.setAudioSpectrumListener((t, d, mag, ph) -> {

            for (int i = 0; i < mag.length; i++) {

                Rectangle bar = (Rectangle) eqBars.getChildren().get(i);

                double value = mag[i] + 60;
                double height = Math.max(5, value * 2);

                bar.setScaleY(height / 20.0);
            }
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

    // ================= SET SONG =================
    public void setSongInfo(Song song) {

        if (song == null) {
            return;
        }

        this.currentSong = song;

        this.song.setText(song.getTitle());
        this.artist.setText(song.getArtist());

        playerService.playSong(song);
        play.setText("⏸");

        MediaPlayer player = playerService.getMediaPlayer();

        if (player != null) {

            player.setVolume(volume.getValue());

            setupSpectrum(player);

            player.setOnReady(() -> {

                double total = player.getTotalDuration().toSeconds();

                progress.setMin(0);
                progress.setMax(total);
                progress.setValue(0);

            });
            player.currentTimeProperty().addListener((obs, oldT, newT) -> {

                double current = newT.toSeconds();

                if (!progress.isValueChanging()) {
                    progress.setValue(current);
                }

                updateProgressColor(); // ⭐ ทำให้สี progress วิ่งตามเพลง

                int min = (int) current / 60;
                int sec = (int) current % 60;

                time.setText(String.format("%02d:%02d", min, sec));
            });

            player.setOnEndOfMedia(() -> {

                Song nextSong = playerService.next();

                if (nextSong != null) {
                    setSongInfo(nextSong);
                    play.setText("⏸");
                }
            });
        }
    }
}
