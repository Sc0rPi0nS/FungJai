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
    private Stage miniStage;

    private LibraryService libraryService;

    private Label song;
    private Label artist;
    private Label time;

    private HBox eqBars;

    private Slider progress;
    private Slider volume;

    private Button play;

    private About aboutWindow;

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

        // ℹ About button
        Button info = infoBtn();
        info.setOnAction(e -> {
            if (aboutWindow == null) {
                aboutWindow = new About();
            }
            aboutWindow.show(stage.getX(), stage.getY(), stage.getWidth());
        });

        HBox menuBar = new HBox(15, home, mySong, playlist, mix, info);
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
        stage.iconifiedProperty().addListener((obs, wasMin, isNow) -> {
    if (isNow) {
        showMiniPlayer(stage);
    } else {
        if (miniStage != null) miniStage.close();
    }
});
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

// ================= INFO BUTTON =================
private Button infoBtn() {
    Button b = new Button("ℹ");
    b.setPrefWidth(32);
    b.setPrefHeight(32);
    b.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #444;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: transparent;" +
        "-fx-background-radius: 50%;"
    );
    b.setOnMouseEntered(e -> b.setStyle(
        "-fx-background-color: #4773a1;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: white;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: transparent;" +
        "-fx-background-radius: 50%;"
    ));
    b.setOnMouseExited(e -> b.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-font-size: 16px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #444;" +
        "-fx-cursor: hand;" +
        "-fx-border-color: transparent;" +
        "-fx-background-radius: 50%;"
    ));
    return b;
}

private void showMiniPlayer(Stage mainStage) {
    if (miniStage != null && miniStage.isShowing()) return;

    miniStage = new Stage();
    miniStage.initStyle(StageStyle.UNDECORATED);

    // ================= LABELS =================
    Label miniTitle  = new Label(currentSong != null ? currentSong.getTitle()  : "No song");
    Label miniArtist = new Label(currentSong != null ? currentSong.getArtist() : "—");

    miniTitle.setStyle("-fx-font-size:12px; -fx-font-weight:bold; -fx-text-fill:#1a1a2e;");
    miniArtist.setStyle("-fx-font-size:10px; -fx-text-fill:#4773a1;");

    // ================= MARQUEE: TITLE =================
    StackPane titleClip = new StackPane(miniTitle);
    titleClip.setMaxWidth(200);
    titleClip.setPrefWidth(200);
    titleClip.setAlignment(Pos.CENTER_LEFT);
    titleClip.setClip(new Rectangle(160, 20));

    javafx.animation.TranslateTransition marquee = new javafx.animation.TranslateTransition();
    marquee.setNode(miniTitle);
    marquee.setCycleCount(javafx.animation.Animation.INDEFINITE);
    marquee.setAutoReverse(false);

    titleClip.layoutBoundsProperty().addListener((obs, o, n) -> {
        double textWidth = miniTitle.prefWidth(-1);
        if (textWidth > 160) {
            double dist = textWidth - 150;
            marquee.stop();
            miniTitle.setTranslateX(0);
            marquee.setFromX(0);
            marquee.setToX(-dist);
            marquee.setDuration(javafx.util.Duration.seconds(dist / 30.0));
            marquee.play();
        } else {
            marquee.stop();
            miniTitle.setTranslateX(0);
        }
    });

    // ================= MARQUEE: ARTIST =================
    StackPane artistClip = new StackPane(miniArtist);
    artistClip.setMaxWidth(200);
    artistClip.setPrefWidth(200);
    artistClip.setAlignment(Pos.CENTER_LEFT);
    artistClip.setClip(new Rectangle(160, 18));

    javafx.animation.TranslateTransition marqueeArtist = new javafx.animation.TranslateTransition();
    marqueeArtist.setNode(miniArtist);
    marqueeArtist.setCycleCount(javafx.animation.Animation.INDEFINITE);
    marqueeArtist.setAutoReverse(false);

    artistClip.layoutBoundsProperty().addListener((obs, o, n) -> {
        double textWidth = miniArtist.prefWidth(-1);
        if (textWidth > 160) {
            double dist = textWidth - 150;
            marqueeArtist.stop();
            miniArtist.setTranslateX(0);
            marqueeArtist.setFromX(0);
            marqueeArtist.setToX(-dist);
            marqueeArtist.setDuration(javafx.util.Duration.seconds(dist / 30.0));
            marqueeArtist.play();
        } else {
            marqueeArtist.stop();
            miniArtist.setTranslateX(0);
        }
    });

    // ================= PROGRESS BAR =================
    Slider miniProgress = new Slider();
    miniProgress.setMin(progress.getMin());
    miniProgress.setMax(progress.getMax() > 0 ? progress.getMax() : 100);
    miniProgress.setValue(progress.getValue());
    miniProgress.setPrefWidth(300);
    miniProgress.setPrefHeight(6);
    miniProgress.setStyle("-fx-control-inner-background:#cccccc;");

    // Sync main → mini + อัปเดตสี
    progress.valueProperty().addListener((obs, o, n) -> {
        miniProgress.setValue(n.doubleValue());
        updateMiniProgressColor(miniProgress); // ✅ เรียกทุกครั้ง
    });
    progress.maxProperty().addListener((obs, o, n) ->
        miniProgress.setMax(n.doubleValue())
    );

    // Seek จาก mini bar
    miniProgress.valueProperty().addListener((obs, o, n) -> {
        if (miniProgress.isValueChanging()) {
            MediaPlayer player = playerService.getMediaPlayer();
            if (player != null)
                player.seek(javafx.util.Duration.seconds(n.doubleValue()));
        }
    });

    // อัปเดตสีครั้งแรกทันทีที่ scene โหลดเสร็จ
    miniProgress.sceneProperty().addListener((obs, o, n) -> {
        if (n != null) updateMiniProgressColor(miniProgress);
    });

    // ================= BUTTONS =================
    String btnStyle =
        "-fx-background-color:transparent;" +
        "-fx-font-size:15px;" +
        "-fx-text-fill:#1a1a2e;" +
        "-fx-cursor:hand;" +
        "-fx-padding:2 6 2 6;";
    String btnHover =
        "-fx-background-color:#4773a1;" +
        "-fx-font-size:15px;" +
        "-fx-text-fill:white;" +
        "-fx-cursor:hand;" +
        "-fx-background-radius:4;" +
        "-fx-padding:2 6 2 6;";

    Button miniPrev    = new Button("⏮");
    Button miniPlayBtn = new Button(playerService.isPlaying() ? "⏸" : "▶");
    Button miniNext    = new Button("⏭");
    Button miniRestore = new Button("⬆");

    for (Button b : new Button[]{miniPrev, miniPlayBtn, miniNext, miniRestore}) {
        b.setStyle(btnStyle);
        b.setOnMouseEntered(e -> b.setStyle(btnHover));
        b.setOnMouseExited (e -> b.setStyle(btnStyle));
    }

    // Play / Pause
    miniPlayBtn.setOnAction(e -> {
        playerService.togglePlayPause();
        boolean playing = playerService.isPlaying();
        miniPlayBtn.setText(playing ? "⏸" : "▶");
        play.setText(playing ? "⏸" : "▶");
        if (videoPlayer != null) {
            if (playing) videoPlayer.play(); else videoPlayer.pause();
        }
    });

    // helper reset marquee เมื่อเปลี่ยนเพลง
    Runnable resetMarquee = () -> {
        marquee.stop();
        marqueeArtist.stop();
        miniTitle.setTranslateX(0);
        miniArtist.setTranslateX(0);
        // layoutBoundsProperty listener จะเริ่ม animation เองหลัง setText
    };

    // Next
    miniNext.setOnAction(e -> {
        Song s = playerService.next();
        if (s != null) {
            setSongInfo(s, currentPlaylist);
            resetMarquee.run();
            miniTitle.setText(s.getTitle());
            miniArtist.setText(s.getArtist());
            miniPlayBtn.setText("⏸");
        }
    });

    // Prev
    miniPrev.setOnAction(e -> {
        Song s = playerService.previous();
        if (s != null) {
            setSongInfo(s, currentPlaylist);
            resetMarquee.run();
            miniTitle.setText(s.getTitle());
            miniArtist.setText(s.getArtist());
            miniPlayBtn.setText("⏸");
        }
    });

    // Restore
    miniRestore.setOnAction(e -> mainStage.setIconified(false));

    // ================= LAYOUT =================
    HBox controls = new HBox(6, miniPrev, miniPlayBtn, miniNext, miniRestore);
    controls.setAlignment(Pos.CENTER_RIGHT);

    VBox songInfo = new VBox(2, titleClip, artistClip); // ✅ ใช้ clip แทน label ตรงๆ

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

HBox topRow = new HBox(10, songInfo, new Region(), controls);
topRow.setAlignment(Pos.CENTER_LEFT);
HBox.setHgrow(topRow.getChildren().get(1), Priority.ALWAYS);

    VBox layout = new VBox(8, topRow, miniProgress);
    layout.setPadding(new Insets(12, 14, 12, 14));
    layout.setPrefWidth(270);
    layout.setStyle(
        "-fx-background-color: #f0f4fa;" +
        "-fx-border-color: #4773a1;" +
        "-fx-border-width: 1.5;" +
        "-fx-border-radius: 10;" +
        "-fx-background-radius: 10;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 12, 0, 0, 4);"
    );

    // ================= POSITION & SHOW =================
    Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
    miniStage.setX(screen.getMaxX() - 290);
    miniStage.setY(screen.getMaxY() - 110);

    miniStage.setScene(new Scene(layout));
    miniStage.setAlwaysOnTop(true);
    miniStage.show();
}
    private void updateMiniProgressColor(Slider miniProgress) {
    if (miniProgress.getMax() == 0) return;

    double percent = (miniProgress.getValue() - miniProgress.getMin())
            / (miniProgress.getMax() - miniProgress.getMin()) * 100;

    javafx.application.Platform.runLater(() -> {
        Node track = miniProgress.lookup(".track");
        if (track != null) {
            track.setStyle(String.format(
                "-fx-background-color: linear-gradient(to right, #4773a1 %.2f%%, #cccccc %.2f%%);",
                percent, percent
            ));
        }
    });
}

}