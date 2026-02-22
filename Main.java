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


public class Main extends Application {
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(450, 450);

        // ===== Background =====
        Image bg = new Image(
            "file:C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJai/pictures/bg1.png"
        );
        ImageView bgView = new ImageView(bg);
        bgView.setScaleX(0.8);
        bgView.setScaleY(0.8);
        bgView.setLayoutX(-550);
        bgView.setLayoutY(-230);
        
        // music player ====
        Media media = new Media("file:///C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJai/song/song.mp3");
        mediaPlayer = new MediaPlayer(media);


        // ===== Overlay =====
        Pane overlay = new Pane();
        overlay.setPrefSize(450, 450);
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.6);");
        
        // ===== Progress Bar =====
        Slider progress = new Slider();
        progress.setPrefWidth(350);
        progress.setLayoutX(50);
        progress.setLayoutY(430);

        Label timeLabel = new Label("00:00 / 00:00");
        timeLabel.setStyle("-fx-text-fill: white;");
        timeLabel.setLayoutX(170);
        timeLabel.setLayoutY(380);
mediaPlayer.setOnReady(() -> {
    Duration total = mediaPlayer.getTotalDuration();
    progress.setMax(total.toSeconds());
    mediaPlayer.play();
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

progress.valueChangingProperty().addListener((obs, wasChanging, changing) -> {
    if (!changing) {
        mediaPlayer.seek(Duration.seconds(progress.getValue()));
    }
});
        

        overlay.getChildren().addAll(progress, timeLabel);

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
        root.getChildren().addAll(bgView, overlay);

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
