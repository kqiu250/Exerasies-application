package src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane content;

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
        // Button Menus
        // =========================
        VBox leftMenu = createButtonMenu("Left");
        VBox rightMenu = createButtonMenu("Right");

        leftMenu.setPadding(new Insets(20));
        rightMenu.setPadding(new Insets(20));

        // =========================
        // Main Layout
        // =========================
        content = new BorderPane();
        content.setLeft(leftMenu);
        content.setRight(rightMenu);
        // center stays empty until user opens the calculator

        StackPane root = new StackPane();
        root.getChildren().addAll(background, content);

        Scene scene = new Scene(root, 800, 600);

        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        stage.setTitle("My JavaFX App");
        stage.setScene(scene);
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

            if (side.equals("Right") && i == 5) {
                button.setText("Calories");
                button.setOnAction(e -> showCaloriePanel());
            }

            menu.getChildren().add(button);
        }

        return menu;
    }

    private void showCaloriePanel() {
        // --- Title ---
        Label title = new Label("Calorie Calculator");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // --- Inputs ---
        Label timeLabel = new Label("Exercise time (minutes):");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g. 30");
        timeField.setMaxWidth(180);

        Label rateLabel = new Label("Burn rate (cal/hour):");
        TextField rateField = new TextField();
        rateField.setPromptText("e.g. 500");
        rateField.setMaxWidth(180);

        // --- Result ---
        Label resultLabel = new Label("Calories burnt: --");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // --- Buttons ---
        Button calcBtn = new Button("Calculate");
        calcBtn.setPrefSize(120, 40);
        calcBtn.setOnAction(e -> {
            try {
                int minutes = Integer.parseInt(timeField.getText().trim());
                int rate = Integer.parseInt(rateField.getText().trim());

                Calorie_Calculator calc = new Calorie_Calculator(minutes * 60, rate);
                resultLabel.setText("Calories burnt: " + calc.calcCaloriesBurnt());
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numbers!");
            }
        });

        Button closeBtn = new Button("Close");
        closeBtn.setPrefSize(120, 40);
        closeBtn.setOnAction(e -> content.setCenter(null));

        // --- Layout ---
        VBox panel = new VBox(10,
                title,
                timeLabel, timeField,
                rateLabel, rateField,
                calcBtn,
                resultLabel,
                closeBtn);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(25));
        panel.setMaxSize(320, 420);
        panel.setStyle(
                "-fx-background-color: rgba(255,255,255,0.9);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #888;" +
                        "-fx-border-radius: 10;"
        );

        // Slide it into the center slot
        content.setCenter(panel);
    }
}