
import javafx.application.*;
import javafx.geometry.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.media.*;
import java.util.*;
import javafx.util.Duration;
import java.io.*;
import javafx.scene.text.*;
import javafx.scene.shape.*;
import javafx.animation.*;


public class Main extends Application {
    private MediaPlayer mediaPlayer;
    private MediaPlayer videoPlayer;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(450, 450);
        
        stage.getIcons().add(new Image("file:C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJaiZ/pictures/icon1.png"));

        Media videoMedia = new Media("file:///C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJaiZ/pictures/nineza12.mp4");
        videoPlayer = new MediaPlayer(videoMedia);
        videoPlayer.setMute(true);
        videoPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        
        MediaView videoView = new MediaView(videoPlayer);
        videoView.setFitWidth(450);
        videoView.setFitHeight(450);
        videoView.setPreserveRatio(true); // สำคัญ

        // ซูมเข้า
        videoView.setScaleX(1.3); // ปรับเลขได้ เช่น 1.2, 1.5
        videoView.setScaleY(1.3);

        // จัดให้อยู่กึ่งกลาง
        videoView.setLayoutX(225);
        videoView.setLayoutY(225);
        videoView.setTranslateX(-225);
        videoView.setTranslateY(-225);
        
        // music player ====
        Media media = new Media("file:///C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJaiZ/song/song1.mp3");
        mediaPlayer = new MediaPlayer(media);


        // ===== Overlay =====
        Pane overlay = new Pane();
        overlay.setPrefSize(450, 450);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.65);");
        
        // ===== Song Title (Scrolling) =====
        Text songTitle = new Text("TATTOO COLOUR - ขอให้เราทั้งคู่โชคดี");
        songTitle.setStyle("-fx-fill: white; -fx-font-size: 25px; -fx-font-weight: bold; -fx-font-family: 'THSarabunNew';");
        songTitle.setLayoutX(0);
        songTitle.setLayoutY(40);
        
        // test pushing //ssss
        Pane titlePane = new Pane(songTitle);
        titlePane.setPrefWidth(450);
        titlePane.setPrefHeight(70);
        titlePane.setLayoutX(0);
        titlePane.setLayoutY(200);

        Rectangle clip = new Rectangle(450, 70);
        titlePane.setClip(clip);

        TranslateTransition scroll = new TranslateTransition(Duration.seconds(20), songTitle);
        scroll.setFromX(450);      // เริ่มจากขวาสุด
        scroll.setToX(-450);       // เลื่อนไปซ้าย (ปรับเลขได้)
        scroll.setCycleCount(Animation.INDEFINITE);
        scroll.setInterpolator(Interpolator.LINEAR);

        // ===== Progress Bar =====
        Slider progress = new Slider();
        progress.setPrefWidth(350);
        progress.setLayoutX(50);
        progress.setLayoutY(400);

        Label timeLabel = new Label("00:00 / 00:00");
        timeLabel.setStyle("-fx-text-fill: white;");
        timeLabel.setLayoutX(190);
        timeLabel.setLayoutY(380);
        mediaPlayer.setOnReady(() -> {
            Duration total = mediaPlayer.getTotalDuration();
            progress.setMax(total.toSeconds());
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {

        if (!progress.isValueChanging()) {
            progress.setValue(newTime.toSeconds());
        }

        Duration total = mediaPlayer.getTotalDuration();
        if (total == null || total.isUnknown()) return;

        int currentMin = (int) newTime.toMinutes();
        int currentSec = (int) newTime.toSeconds() % 60;

        int totalMin = (int) total.toMinutes();
        int totalSec = (int) total.toSeconds() % 60;

        timeLabel.setText(
        String.format("%02d:%02d / %02d:%02d",
                currentMin, currentSec,
                totalMin, totalSec
            )
        );
    });
    
                Button playBtn = new Button("▶");
        playBtn.setPrefSize(70, 70);
        playBtn.setLayoutX(190);
        playBtn.setLayoutY(300);
                playBtn.setStyle(
            "-fx-background-color: #1DB954;" +
            "-fx-background-radius: 50%;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 24px;"
        );
                
 playBtn.setOnAction(e -> {

    MediaPlayer.Status status = mediaPlayer.getStatus();

    if (status == MediaPlayer.Status.PLAYING) {

        mediaPlayer.pause();
        playBtn.setText("▶");
        scroll.pause();
        videoPlayer.pause();

    } else {

        mediaPlayer.play();
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        playBtn.setText("||");
        scroll.play();
        videoPlayer.play();
        
    }
});

progress.valueChangingProperty().addListener((obs, wasChanging, changing) -> {
    if (!changing) {
        mediaPlayer.seek(Duration.seconds(progress.getValue()));
    }
});
        

        overlay.getChildren().addAll(titlePane,progress, timeLabel, playBtn);

        // ===== Top Menu =====
        HBox menuBar = new HBox(25);
        menuBar.setPrefHeight(60);
        menuBar.setAlignment(Pos.CENTER);
        menuBar.setPadding(new Insets(10));
        menuBar.setLayoutY(0);
        menuBar.setLayoutX(0);
        menuBar.setStyle(
            "-fx-background-color: rgba(20,20,20,0.2);"
        ); 

        Button btnHome = createMenuButton("Home");
        Button btnSong = createMenuButton("Song");
        Button btnPlaylist = createMenuButton("Playlist");
        Button btnProfile = createMenuButton("Profile");
        Button btnPremium = createMenuButton("Premium");

        menuBar.setPrefWidth(450);
        menuBar.getChildren().addAll(   
            btnHome, btnSong, btnPlaylist, btnProfile, btnPremium
        );

        overlay.getChildren().add(menuBar);
        root.getChildren().addAll(videoView, overlay);

        Scene scene = new Scene(root, 450, 450);
        scene.getStylesheets().add("file:/C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJai/src/style.css");
        stage.setTitle("FungJai");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
        
    }

    // ===== สไตล์ปุ่มเมนู =====
        private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;"
        );

        btn.setOnMouseEntered(e ->
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: #1DB954;" +   // เขียวแบบ Spotify
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
            )
        );

        btn.setOnMouseExited(e ->
            btn.setStyle(
                "-fx-background-color: transparent;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;"
            )
        );

        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}