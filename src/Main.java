package src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        // =========================
        // Background
        // =========================

        Image backgroundImage =
                new Image(getClass().getResource("/src/background.jpg").toExternalForm());

        ImageView background = new ImageView(backgroundImage);

        background.setPreserveRatio(false);


        // =========================
        // Button Menu
        // =========================

        VBox leftMenu = createButtonMenu("Left");
        VBox rightMenu = createButtonMenu("Right");

        leftMenu.setPadding(new Insets(20));
        rightMenu.setPadding(new Insets(20));


        // =========================
        // Main Layout
        // =========================

        BorderPane content = new BorderPane();

        content.setLeft(leftMenu);
        content.setRight(rightMenu);


        // =========================
        // Root
        // =========================

        StackPane root = new StackPane();

        root.getChildren().addAll(
                background,
                content
        );

        // =========================
        // Scene
        // =========================

        Scene scene = new Scene(root, 1000, 600);


        // =========================
        // Background Resize
        // =========================

        background.fitWidthProperty()
                .bind(scene.widthProperty());

        background.fitHeightProperty()
                .bind(scene.heightProperty());


        // =========================
        // Stage
        // =========================

        stage.setTitle("My JavaFX App");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

    private VBox createButtonMenu(String side) {
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);

        for (int i = 1; i <= 5; i++) {
            Button button = new Button(side + " " + i);
            button.setPrefSize(120, 50);
            menu.getChildren().add(button);
        }

        return menu;
    }
}
