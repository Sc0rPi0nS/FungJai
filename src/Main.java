import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        //Try with Logo
        java.net.URL logoUrl = getClass().getResource("/pictures/logo.png");

        //if-not-found
        if (logoUrl != null) {
            stage.getIcons().add(new Image(logoUrl.toExternalForm()));
        } else {
            System.out.println("⚠️ WARNING: Could not find /pictures/logo.png!");
        }

        //Start
        HomeWindow home = new HomeWindow();
        home.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
