import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import java.io.File;

public class VideoPlayer extends Application {

    @Override
    public void start(Stage stage) {
        File file = new File("C:\\Users\\iamni\\OneDrive\\Documents\\NetBeansProjects\\FungJai\\pictures\\nineza123.mp4");
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        StackPane root = new StackPane(view);
        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("Java Video Player");
        stage.setScene(scene);
        stage.show();

        player.play(); // เริ่มเล่น
    }

    public static void main(String[] args) {
        launch();
    }
}