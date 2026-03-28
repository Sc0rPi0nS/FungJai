
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

    // เก็บ reference listener เพื่อ remove ก่อน bind ใหม่ ป้องกัน listener สะสม
    private javafx.beans.value.ChangeListener<javafx.util.Duration> currentTimeListener;
    private MediaPlayer boundPlayer; // player ที่ผูก listener อยู่ตอนนี้

    public void show(Stage stage) {

        libraryService = new LibraryService();

        playerService = new PlayerService(
                libraryService.getLibrary().getAllSongs()
        );

        // เมื่อเพลงเปลี่ยนอัตโนมัติ (จบ → ขึ้นถัดไป) ให้ HomeWindow อัปเดต UI
        playerService.setOnSongChanged(newSong -> setSongInfo(newSong, currentPlaylist));

        BorderPane root = new BorderPane();
        root.setPrefSize(600, 450);
        root.getStyleClass().add("root");

        // ================= TOP =================
        Button home = menuBtn("HOME", true);
        Button mySong = menuBtn("MYSONG", false);
        Button playlist = menuBtn("MYPLAYLIST", false);
        Button mix = menuBtn("MIXFORYOU", false);
        Button themeBtn = new Button("🌙 Dark");//Button for Dark mode
        themeBtn.setStyle("-fx-background-color: #cccccc; -fx-font-weight: bold;");

        mySong.setOnAction(e
                -> new MySongWindow(this, libraryService).show(stage)
        );

        // ⭐ pass libraryService so PlaylistWindow uses real Playlist objects
        playlist.setOnAction(e -> new PlaylistWindow(this, libraryService).show(stage));
        mix.setOnAction(e -> new MixForYouWindow(this, libraryService).show(stage));

        // ℹ About button
        Button info = infoBtn();
        info.setOnAction(e -> {
            if (aboutWindow == null) {
                aboutWindow = new About();
            }
            aboutWindow.show(stage.getX(), stage.getY(), stage.getWidth());
        });

        HBox menuBar = new HBox(15, home, mySong, playlist, mix, themeBtn, info);
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPadding(new Insets(4));

        menuBar.getStyleClass().add("menu-bar");

        root.setTop(menuBar);

        // ================= CENTER =================
        URL videoUrl = getClass().getResource("/pictures/tape.mp4");
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
            videoPlayer.setOnReady(()
                    -> videoView.setViewport(new Rectangle2D(200, 100, 1500, 900))
            );
        }

        song = new Label("Song Title");
        artist = new Label("Artist");

        //CSS
        song.getStyleClass().add("song-title");
        artist.getStyleClass().add("artist-name");

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

        String ctrlBtnStyle
                = "-fx-background-color:linear-gradient(to bottom,#f0f0f0,#d8d8d8,#c8c8c8);"
                + "-fx-background-radius:6px;"
                + "-fx-border-color:#e4e4e4 #a8a8a8 #a8a8a8 #e4e4e4;"
                + "-fx-border-radius:6px;-fx-border-width:1;"
                + "-fx-text-fill:#333333;-fx-font-size:13px;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.2),3,0,0,2);";

        String ctrlBtnActiveStyle
                = "-fx-background-color:linear-gradient(to bottom,#6090d0,#4070b0,#2a5090);"
                + "-fx-background-radius:6px;"
                + "-fx-border-color:#80b0e0 #1a4080 #1a4080 #80b0e0;"
                + "-fx-border-radius:6px;-fx-border-width:1;"
                + "-fx-text-fill:white;-fx-font-size:13px;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,80,180,0.4),5,0.3,0,0);";

        lyrics.setStyle(ctrlBtnStyle + "-fx-font-size:11px;-fx-padding:5 14 5 14;");
        shuffle.setStyle(ctrlBtnStyle);
        shuffle.setPrefSize(36, 36);
        prev.setStyle(ctrlBtnStyle);
        prev.setPrefSize(36, 36);
        next.setStyle(ctrlBtnStyle);
        next.setPrefSize(36, 36);
        replay.setStyle(ctrlBtnStyle);
        replay.setPrefSize(36, 36);

        play.setStyle(
                "-fx-background-color:linear-gradient(to bottom,#f0f0f0,#d8d8d8,#c4c4c4);"
                + "-fx-background-radius:50%;-fx-border-radius:50%;"
                + "-fx-border-color:#e4e4e4 #a0a0a0 #a0a0a0 #e4e4e4;"
                + "-fx-border-width:1.5;-fx-text-fill:#222222;-fx-font-size:20px;-fx-cursor:hand;"
                + "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.25),6,0,0,3);"
        );
        play.setPrefSize(56, 56);

        volume = new Slider(0, 1, 0.7);
        volume.setPrefWidth(80);

        volume.valueProperty().addListener((obs, oldVal, newVal) -> {
            MediaPlayer player = playerService.getMediaPlayer();
            if (player != null) {
                player.setVolume(newVal.doubleValue());
            }
        });
        Label volIcon = new Label("🔊");

        HBox volumeBox = new HBox(4, volIcon, volume);
        volumeBox.setAlignment(Pos.CENTER);

// ⭐ รวมทุกปุ่มไว้ด้วยกัน
        HBox controls = new HBox(6, lyrics, shuffle, prev, play, next, replay, volumeBox);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(0, 0, 0, 20));

        BorderPane controlBar = new BorderPane();
        controlBar.setCenter(controls);

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

            if (playerService.getCurrentSong() == null) {
                return;
            }

            playerService.togglePlayPause();

            if (playerService.isPlaying()) {
                play.setText("⏸");
                if (videoPlayer != null) {
                    videoPlayer.play();
                }
            } else {
                play.setText("▶");
                if (videoPlayer != null) {
                    videoPlayer.pause();
                }
            }
        });

        next.setOnAction(e -> {
            Song s = playerService.next();
            if (s != null) {
                setSongInfo(s, currentPlaylist);
            }
        });

        prev.setOnAction(e -> {
            Song s = playerService.previous();
            if (s != null) {
                setSongInfo(s, currentPlaylist);
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

        // 1. Load the light theme by default
        try {
            scene.getStylesheets().add(getClass().getResource("/css/light-theme.css").toExternalForm());
        } catch (Exception ex) {
            System.out.println("Could not find light-theme.css! Make sure it is in the right folder.");
        }

        // 2. Make the button swap the themes when clicked
        themeBtn.setOnAction(e -> {
            scene.getStylesheets().clear(); // Remove the current theme

            if (themeBtn.getText().equals("🌙 Dark")) {
                // Switch to Dark
                scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
                themeBtn.setText("☀️Light");
            } else {
                // Switch to Light
                scene.getStylesheets().add(getClass().getResource("/css/light-theme.css").toExternalForm());
                themeBtn.setText("🌙 Dark");
            }
        });
        stage.setScene(scene);
        stage.setTitle("FungJai");
        stage.setResizable(false);
        stage.show();
        stage.iconifiedProperty().addListener((obs, wasMin, isNow) -> {
            if (isNow) {
                showMiniPlayer(stage);
            } else {
                if (miniStage != null) {
                    miniStage.close();
                }
            }
        });
    }

    // ================= MENU BUTTON =================
    private Button menuBtn(String text, boolean active) {
        Button b = new Button(text);
        b.getStyleClass().add("menu-btn");

        b.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-font-size: 14px;");

        if (active) {
            b.setStyle(b.getStyle() + "-fx-text-fill: #4773a1;"); // Keep blue active color
        }
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

        // ── ลบ listener เก่าออกก่อนเสมอ ป้องกัน listener สะสมหลาย song ──
        if (boundPlayer != null && currentTimeListener != null) {
            boundPlayer.currentTimeProperty().removeListener(currentTimeListener);
        }

        boundPlayer = player;

        currentTimeListener = (obs, oldTime, newTime) -> {
            // อัปเดต progress เฉพาะตอนที่ user ไม่ได้ลาก slider
            if (!progress.isValueChanging()) {
                double seconds = newTime.toSeconds();
                progress.setValue(seconds);

                int min = (int) seconds / 60;
                int sec = (int) seconds % 60;
                time.setText(String.format("%02d:%02d", min, sec));

                updateProgressColor();
            }
        };

        player.currentTimeProperty().addListener(currentTimeListener);

        // setMax — ถ้า player ready แล้วก็ set ทันที ถ้ายังไม่ ready รอ setOnReady
        Runnable applyMax = () -> {
            double total = player.getTotalDuration() != null
                    ? player.getTotalDuration().toSeconds() : 0;
            if (total > 0) {
                progress.setMax(total);
            }
        };

        MediaPlayer.Status status = player.getStatus();
        if (status == MediaPlayer.Status.READY
                || status == MediaPlayer.Status.PLAYING
                || status == MediaPlayer.Status.PAUSED
                || status == MediaPlayer.Status.STALLED) {
            applyMax.run();
        } else {
            player.setOnReady(applyMax);
        }
    }

    // ================= SET SONG =================
    public void setSongInfo(Song song, Playlist playlist) {

        if (song == null) {
            videoPlayer.stop();
            return;
        }

        this.currentSong = song;
        this.currentPlaylist = playlist;

        this.song.setText(song.getTitle());
        this.artist.setText(song.getArtist());

        if (playlist != null) {
            playerService.setQueue(playlist);
        }

        playerService.playSong(song);

        MediaPlayer player = song.getMediaPlayer();

        // รีเซ็ต progress ก่อนเสมอ เพื่อป้องกัน bar ค้างจากเพลงก่อนหน้า
        progress.setValue(0);
        progress.setMax(100); // placeholder จนกว่า bindPlayer จะ setMax จริง

        setupSpectrum(player);
        bindPlayer(player);

        play.setText("⏸");
        if (videoPlayer != null) {
            videoPlayer.stop();
            videoPlayer.play();
        }
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
                "-fx-background-color: transparent;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #444;"
                + "-fx-cursor: hand;"
                + "-fx-border-color: transparent;"
                + "-fx-background-radius: 50%;"
        );
        b.setOnMouseEntered(e -> b.setStyle(
                "-fx-background-color: #4773a1;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: white;"
                + "-fx-cursor: hand;"
                + "-fx-border-color: transparent;"
                + "-fx-background-radius: 50%;"
        ));
        b.setOnMouseExited(e -> b.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #444;"
                + "-fx-cursor: hand;"
                + "-fx-border-color: transparent;"
                + "-fx-background-radius: 50%;"
        ));
        return b;
    }

    private void showMiniPlayer(Stage mainStage) {
        if (miniStage != null && miniStage.isShowing()) {
            return;
        }

        miniStage = new Stage();
        miniStage.initStyle(StageStyle.UNDECORATED);

        // ================= LABEL =================
        Label miniTitle = new Label(currentSong != null ? currentSong.getTitle() : "No song");
        Label miniArtist = new Label(currentSong != null ? currentSong.getArtist() : "—");

        miniTitle.setStyle("-fx-font-size:13px; -fx-font-weight:bold; -fx-text-fill:#1a1a2e;");
        miniArtist.setStyle("-fx-font-size:11px; -fx-text-fill:#4773a1;");

        miniTitle.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
        miniArtist.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);

        // ================= MARQUEE =================
        StackPane titleClip = new StackPane(miniTitle);
        titleClip.setMinSize(160, 22);
        titleClip.setMaxSize(160, 22);
        titleClip.setAlignment(Pos.CENTER_LEFT);
        titleClip.setClip(new Rectangle(160, 22));

        StackPane artistClip = new StackPane(miniArtist);
        artistClip.setMinSize(160, 18);
        artistClip.setMaxSize(160, 18);
        artistClip.setAlignment(Pos.CENTER_LEFT);
        artistClip.setClip(new Rectangle(160, 18));

        javafx.animation.TranslateTransition marquee = new javafx.animation.TranslateTransition();
        marquee.setNode(miniTitle);

        Runnable runMarquee = () -> {
            double textW = miniTitle.prefWidth(-1) + 30;
            double duration = (160 + textW) / 30.0;

            marquee.stop();
            miniTitle.setTranslateX(160);

            marquee.setFromX(160);
            marquee.setToX(-textW);
            marquee.setDuration(javafx.util.Duration.seconds(duration));

            marquee.play();
        };

        marquee.setOnFinished(e -> runMarquee.run());

        miniTitle.widthProperty().addListener((obs, o, n) -> {
            if (n.doubleValue() > 0) {
                runMarquee.run();
            }
        });

        // ================= PROGRESS =================
        Slider miniProgress = new Slider();
        miniProgress.setMin(progress.getMin());
        miniProgress.setMax(progress.getMax() > 0 ? progress.getMax() : 100);
        miniProgress.setValue(progress.getValue());
        miniProgress.setPrefWidth(260);
        miniProgress.setPrefHeight(5);

        miniProgress.setStyle(
                "-fx-background-color: transparent;"
                + "-fx-control-inner-background: #d0d7e2;"
        );

        progress.valueProperty().addListener((obs, o, n) -> {
            miniProgress.setValue(n.doubleValue());
            updateMiniProgressColor(miniProgress);
        });

        progress.maxProperty().addListener((obs, o, n)
                -> miniProgress.setMax(n.doubleValue())
        );

        miniProgress.valueProperty().addListener((obs, o, n) -> {
            if (miniProgress.isValueChanging()) {
                MediaPlayer player = playerService.getMediaPlayer();
                if (player != null) {
                    player.seek(javafx.util.Duration.seconds(n.doubleValue()));
                }
            }
        });

        miniProgress.sceneProperty().addListener((obs, o, n) -> {
            if (n != null) {
                updateMiniProgressColor(miniProgress);
            }
        });

        // ================= BUTTON STYLE =================
        String btnStyle
                = "-fx-background-color: transparent;"
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: #1a1a2e;"
                + "-fx-cursor: hand;"
                + "-fx-background-radius: 20;"
                + "-fx-padding: 6;";

        String btnHover
                = "-fx-background-color: #4773a1;"
                + "-fx-font-size: 14px;"
                + "-fx-text-fill: white;"
                + "-fx-cursor: hand;"
                + "-fx-background-radius: 20;"
                + "-fx-padding: 6;";

        Button miniPrev = new Button("⏮");
        Button miniPlayBtn = new Button(playerService.isPlaying() ? "⏸" : "▶");
        Button miniNext = new Button("⏭");
        Button miniRestore = new Button("⛶");

        for (Button b : new Button[]{miniPrev, miniNext, miniRestore}) {
            b.setStyle(btnStyle);
            b.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

            b.setOnMouseEntered(e -> b.setStyle(btnHover));
            b.setOnMouseExited(e -> b.setStyle(btnStyle));
        }

        // ⭐ PLAY BUTTON (เด่น)
        miniPlayBtn.setStyle(
                "-fx-background-color: #4773a1;"
                + "-fx-text-fill: white;"
                + "-fx-font-size: 15px;"
                + "-fx-background-radius: 20;"
                + "-fx-padding: 6 10 6 10;"
        );

        miniPlayBtn.setOnMouseEntered(e
                -> miniPlayBtn.setStyle(
                        "-fx-background-color: #355d87;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 15px;"
                        + "-fx-background-radius: 20;"
                        + "-fx-padding: 6 10 6 10;"
                ));

        miniPlayBtn.setOnMouseExited(e
                -> miniPlayBtn.setStyle(
                        "-fx-background-color: #4773a1;"
                        + "-fx-text-fill: white;"
                        + "-fx-font-size: 15px;"
                        + "-fx-background-radius: 20;"
                        + "-fx-padding: 6 10 6 10;"
                ));

        // ================= BUTTON ACTION =================
        miniPlayBtn.setOnAction(e -> {
            playerService.togglePlayPause();
            boolean playing = playerService.isPlaying();

            miniPlayBtn.setText(playing ? "⏸" : "▶");
            play.setText(playing ? "⏸" : "▶");

            if (videoPlayer != null) {
                if (playing) {
                    videoPlayer.play();
                } else {
                    videoPlayer.pause();
                }
            }
        });

        Runnable resetMarquee = () -> {
            marquee.stop();
            javafx.application.Platform.runLater(runMarquee);
        };

        miniNext.setOnAction(e -> {
            Song s = playerService.next();
            if (s != null) {
                setSongInfo(s, currentPlaylist);
                miniTitle.setText(s.getTitle());
                miniArtist.setText(s.getArtist());
                resetMarquee.run();
            }
        });

        miniPrev.setOnAction(e -> {
            Song s = playerService.previous();
            if (s != null) {
                setSongInfo(s, currentPlaylist);
                miniTitle.setText(s.getTitle());
                miniArtist.setText(s.getArtist());
                resetMarquee.run();
            }
        });

        miniRestore.setOnAction(e -> mainStage.setIconified(false));

        // ================= LAYOUT =================
        HBox controls = new HBox(6, miniPrev, miniPlayBtn, miniNext, miniRestore);
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.setMinWidth(Region.USE_PREF_SIZE);

        VBox songInfo = new VBox(2, titleClip, artistClip);
        songInfo.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox topRow = new HBox(12, songInfo, spacer, controls);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox layout = new VBox(10, topRow, miniProgress);
        layout.setPadding(new Insets(12, 16, 12, 16));
        layout.setPrefWidth(300);
        layout.setStyle(
                "-fx-background-color: #f5f5f5;"
                + "-fx-background-insets: 0;"
                + "-fx-border-color: #4773a1;"
                + "-fx-border-width: 2;"
                + "-fx-border-radius: 12;"
                + "-fx-background-radius: 12;"
        );

        // ================= POSITION =================
        Rectangle2D screen = javafx.stage.Screen.getPrimary().getVisualBounds();
        miniStage.setX(screen.getMaxX() - 350);
        miniStage.setY(screen.getMaxY() - 130);

        Scene scene = new Scene(layout);
        scene.setFill(null);

        miniStage.setScene(scene);
        miniStage.initStyle(StageStyle.TRANSPARENT);
        miniStage.setAlwaysOnTop(true);
        miniStage.show();
    }

    private void updateMiniProgressColor(Slider miniProgress) {
        if (miniProgress.getMax() == 0) {
            return;
        }

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
