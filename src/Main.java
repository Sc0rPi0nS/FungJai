import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
     HomeWindow home = new HomeWindow();
        home.show(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
