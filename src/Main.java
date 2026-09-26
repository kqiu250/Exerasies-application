package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        Button button = new Button("Click Me!");
        Button quit = new Button("Quit");

        var ref = new Object() {
            int timesPressed = 1;
        };

        button.setOnAction(e -> {
            if (ref.timesPressed <= 1) {
                button.setText("Hello JavaFX!");
            }
            else {
                button.setText("Hello JavaFX! x" + ref.timesPressed);
            }
            ref.timesPressed++;
        });

        quit.setOnAction(e -> {
            stage.close();  // or Platform.exit();
        });

        // Put both buttons in the same layout
        VBox root = new VBox(10);  // 10 pixels spacing between buttons
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(button, quit);

        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("My JavaFX App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}