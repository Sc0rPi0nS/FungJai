import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.media.*;
import javafx.util.Duration;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.animation.*;
import java.net.URL;
import javafx.scene.web.*;
public class HomeWindow {

    private MediaPlayer mediaPlayer;
    private MediaPlayer videoPlayer;

public void show(Stage stage) {

    BorderPane root = new BorderPane();
    root.setPrefSize(500, 450);
    root.setStyle("-fx-background-color: #f5f5f5;");

    // ================= TOP =================
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

    // ================= CENTER =================
    // ===== VIDEO (แทน ImageView) =====
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

    /* ===== 2. ครอปไม่ให้ล้น ===== */
    Rectangle clip = new Rectangle(370, 200);
    clip.setArcWidth(10);
    clip.setArcHeight(10);
    videoView.setClip(clip);

    /* ===== 3. รอ media พร้อมก่อนค่อยซูม ===== */
    videoPlayer.setOnReady(() -> {

        videoView.setViewport(
            new Rectangle2D(200, 100, 1500, 900)
        );

        videoPlayer.play();
    });

    Label song = new Label("Song Title");
    Label artist = new Label("Artist");

    song.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
    artist.setStyle("-fx-text-fill: gray;");

    // รวมให้อยู่บรรทัดเดียว
    HBox songLine = new HBox(6, song, new Label("–"), artist);
    songLine.setAlignment(Pos.CENTER);

    URL audioUrl = getClass().getResource("/song/song1.mp3");
    if (audioUrl == null) {
        throw new RuntimeException("ไม่พบไฟล์ testsong.mp3");
    }
    Media audioMedia = new Media(audioUrl.toExternalForm());
    mediaPlayer = new MediaPlayer(audioMedia);
    // สร้าง EQ bar 32 แท่ง
    HBox eqBars = new HBox(2);
    eqBars.setAlignment(Pos.CENTER);
    for (int i = 0; i < 26; i++) {
        Rectangle bar = new Rectangle(8, 15);
        bar.setFill(javafx.scene.paint.Color.GREY);
        eqBars.getChildren().add(bar);
}

    // ตั้งค่า spectrum listener
    mediaPlayer.setAudioSpectrumInterval(0.05);
    mediaPlayer.setAudioSpectrumNumBands(32);

    mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
        for (int i = 0; i < magnitudes.length; i++) {
            Rectangle bar = (Rectangle) eqBars.getChildren().get(i);

            double value = magnitudes[i] + 60; // normalize (-60 ถึง 0 → 0 ถึง 60)

            // ถ้าเป็นแท่งตรงกลาง (เช่น index 12–20) ให้ขยายการเคลื่อนไหว
            if (i > 12 && i < 25) {
                value *= 2; // ขยาย amplitude
            }

            double height = Math.max(5, value * 2);
            bar.setHeight(15); // fix ความสูงเริ่มต้น
            bar.setScaleY(height / 20.0); // ขยาย/หดตามเสียง
        }
    });



    VBox center = new VBox(10, videoView, songLine, eqBars);
    center.setAlignment(Pos.CENTER);
    center.setPadding(new Insets(10));
    root.setCenter(center);

    // ================= BOTTOM =================
    Button lyrics = new Button("Lyrics");
    Button prev = new Button("⏮");
    Button play = new Button("▶");

    play.setOnAction(e -> {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    });
    mediaPlayer.setOnReady(() -> {
    mediaPlayer.play();
});
    Button next = new Button("⏭");
    Button shuffle = new Button("🔀");
    Button replay = new Button("🔁");

    Slider volume = new Slider(0, 1, 0.7);
    volume.setPrefWidth(80);
    videoPlayer.volumeProperty().bind(volume.valueProperty());

    // ===== ซ้าย =====
    HBox leftControls = new HBox(3, lyrics, shuffle, prev);
    leftControls.setAlignment(Pos.CENTER_RIGHT);
    leftControls.setPrefWidth(200);
    leftControls.setPadding(new Insets(0, 0, 0, 0)); // 👈 ดันเข้ากลาง

    // ===== กลาง (▶ ตรงกลางจริง) =====
    StackPane centerControls = new StackPane(play);
    centerControls.setAlignment(Pos.CENTER);
    centerControls.setPrefWidth(40);
    centerControls.setMinWidth(40);
    centerControls.setMaxWidth(40);

    // ===== ขวา =====
    HBox rightControls = new HBox(3, next, replay, new Label("🔊"), volume);
    rightControls.setAlignment(Pos.CENTER_LEFT);
    rightControls.setPrefWidth(200);
    rightControls.setPadding(new Insets(0, 0, 0, 0)); // 👈 ดันเข้ากลาง

    // ===== control bar =====
    BorderPane controlBar = new BorderPane();
    controlBar.setLeft(leftControls);
    controlBar.setCenter(centerControls);
    controlBar.setRight(rightControls);
    controlBar.setPadding(new Insets(3));

    // (debug เอาออกได้ทีหลัง)
    // controlBar.setStyle("-fx-border-color: red;");
    // centerControls.setStyle("-fx-border-color: blue;");

    // ===== progress bar =====
    Slider progress = new Slider();
    progress.setPrefWidth(350);

    Label time = new Label("00:00");

    HBox progressBar = new HBox(10, time, progress);
    progressBar.setAlignment(Pos.CENTER);

    // ===== bottom =====
    VBox bottom = new VBox(8, controlBar, progressBar);
    bottom.setPadding(new Insets(10));
    root.setBottom(bottom);

    
    // ================= SCENE =================
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.setTitle("FungJai");
    stage.setResizable(false);
    stage.setResizable(false);
    stage.setMinWidth(500);
    stage.setMinHeight(450);
    stage.setMaxWidth(500);
    stage.setMaxHeight(450);
    stage.show();
}

private Button menuBtn(String text) {
    Button b = new Button(text);

    b.setPrefWidth(100);
    b.setPrefHeight(32);

    // default
    b.setStyle(
        "-fx-background-color: transparent;" +
        "-fx-border-color: transparent;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: #444444;" +
        "-fx-cursor: hand;"
    );

    // hover
    b.setOnMouseEntered(e ->
        b.setStyle(
            "-fx-background-color: rgba(0,0,0,0.08);" +
            "-fx-border-color: transparent;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: black;"
        )
    );

    // exit
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