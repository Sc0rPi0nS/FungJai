import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.*;
import javafx.stage.Stage;

public class TestVideo extends Application {

    @Override
    public void start(Stage stage) {

        Media media = new Media(
            "file:///C:/Users/iamni/OneDrive/Documents/NetBeansProjects/FungJai/pictures/nineza.mp4"
        );

        MediaPlayer player = new MediaPlayer(media);
        MediaView view = new MediaView(player);

        view.setFitWidth(450);
        view.setFitHeight(450);

        player.setOnReady(() -> {
            System.out.println("VIDEO READY");
            player.play();
        });

        player.setOnError(() -> {
            System.out.println("ERROR: " + player.getError());
        });

        stage.setScene(new Scene(new StackPane(view), 450, 450));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}