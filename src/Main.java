
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        stage.getIcons().add(
                new Image(getClass().getResource("/pictures/logo.png").toExternalForm())
        );
        HomeWindow home = new HomeWindow();
        home.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
