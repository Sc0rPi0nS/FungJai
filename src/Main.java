import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Try to find the logo safely
        java.net.URL logoUrl = getClass().getResource("/pictures/logo.png");

        // 2. Only load it if it actually exists!
        if (logoUrl != null) {
            stage.getIcons().add(new Image(logoUrl.toExternalForm()));
        } else {
            System.out.println("⚠️ WARNING: Could not find /pictures/logo.png!");
        }

        // 3. Start the Home Window
        HomeWindow home = new HomeWindow();
        home.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
