package src;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {

    private BorderPane content;
    private VBox leftMenu;
    private VBox rightMenu;

    // Maps a region name (e.g., "Arms") to its full-size transparent PNG overlay
    private final Map<String, ImageView> overlays = new HashMap<>();

    // The dark layer that dims the background
    private Rectangle dimLayer;

    // Region -> specific muscles
    private static final Map<String, String[]> REGION_MUSCLES = Map.of(
            "Arms",      new String[]{ "Biceps", "Triceps", "Forearms" },
            "Legs",      new String[]{ "Quadriceps", "Hamstrings", "Glutes", "Calves" },
            "Torso",     new String[]{ "Chest", "Abs", "Lower Back" },
            "Back",      new String[]{ "Lats", "Traps", "Lower Back" },
            "Shoulders", new String[]{ "Front Delts", "Rear Delts", "Traps" }
    );

    @Override
    public void start(Stage stage) {

        // 1. Load Background
        Image bgImg = loadImage("background.jpg");
        ImageView background = new ImageView(bgImg);
        background.setPreserveRatio(false);

        // 2. Create the Dimming Layer
        dimLayer = new Rectangle();
        dimLayer.setFill(Color.rgb(0, 0, 0, 0.6)); // Black with 60% opacity
        dimLayer.setVisible(false); // Hidden by default
        dimLayer.setMouseTransparent(true);

        // 3. Load Overlays (The transparent images that are perfectly aligned)
        buildOverlays();

        // 4. Setup Layout
        StackPane root = new StackPane();
        root.getChildren().add(background);
        root.getChildren().add(dimLayer); // Dim layer sits above background, below muscles

        // Add all transparent overlays on top of the dim layer
        for (ImageView iv : overlays.values()) {
            root.getChildren().add(iv);
        }

        leftMenu  = new VBox(10);
        rightMenu = new VBox(10);
        leftMenu.setAlignment(Pos.CENTER);
        rightMenu.setAlignment(Pos.CENTER);
        leftMenu.setPadding(new Insets(20));
        rightMenu.setPadding(new Insets(20));

        content = new BorderPane();
        content.setLeft(leftMenu);
        content.setRight(rightMenu);
        root.getChildren().add(content);

        // 5. Setup Scene
        Scene scene = new Scene(root, 900, 700);

        // Bind everything to fill the window and scale perfectly together
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        dimLayer.widthProperty().bind(scene.widthProperty());
        dimLayer.heightProperty().bind(scene.heightProperty());

        for (ImageView iv : overlays.values()) {
            iv.fitWidthProperty().bind(scene.widthProperty());
            iv.fitHeightProperty().bind(scene.heightProperty());
        }

        stage.setTitle("Muscle Explorer");
        stage.setScene(scene);
        stage.show();

        showGeneralMenu();
    }

    public static void main(String[] args) {
        launch();
    }

    // =========================================================
    // OVERLAY LOADING
    // =========================================================
    private void buildOverlays() {
        loadOverlay("Arms", "arms.png");
        loadOverlay("Legs", "legs.png");
        loadOverlay("Torso", "torso.png");
        loadOverlay("Back", "back.png");
    }

    private void loadOverlay(String regionKey, String fileName) {
        Image img = loadImage(fileName);
        if (img == null) return;

        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(false);
        iv.setMouseTransparent(true);
        iv.setVisible(false); // Hidden by default

        // Make it look like a highlight.
        // We use a dark drop shadow so the red muscle "pops" out from the dimmed background.
        iv.setOpacity(0.95);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.BLACK); // Black glow creates a nice cutout effect
        glow.setRadius(25);
        glow.setSpread(0.4);
        iv.setEffect(glow);

        overlays.put(regionKey, iv);
    }

    private Image loadImage(String name) {
        var url = getClass().getResource("/src/" + name);
        if (url == null) {
            System.err.println("!! Could not find /src/" + name);
            return null;
        }
        Image img = new Image(url.toExternalForm());
        if (img.isError()) {
            System.err.println("!! Error loading " + name + ": " + img.getException());
            return null;
        }
        return img;
    }

    // =========================================================
    // MENUS
    // =========================================================
    private void showGeneralMenu() {
        leftMenu.getChildren().clear();
        rightMenu.getChildren().clear();
        content.setCenter(null);
        hideAllOverlays();

        String[] general = { "Arms", "Legs", "Torso", "Back", "Shoulders", "Calories" };

        for (int i = 0; i < general.length; i++) {
            String name = general[i];
            Button b = new Button(name);
            b.setPrefSize(130, 55);

            if (name.equals("Calories")) {
                b.setOnAction(e -> showCaloriePanel());
            } else {
                b.setOnMouseEntered(e -> showOverlayFor(name));
                b.setOnMouseExited(e  -> hideAllOverlays());
                final String area = name;
                b.setOnAction(e -> showMuscleMenu(area));
            }

            if (i % 2 == 0) leftMenu.getChildren().add(b);
            else            rightMenu.getChildren().add(b);
        }
    }

    private void showMuscleMenu(String area) {
        leftMenu.getChildren().clear();
        rightMenu.getChildren().clear();
        content.setCenter(null);
        hideAllOverlays();

        String[] muscles = REGION_MUSCLES.getOrDefault(area, new String[0]);

        Button back = new Button("← Back");
        back.setPrefSize(130, 55);
        back.setOnAction(e -> showGeneralMenu());
        leftMenu.getChildren().add(back);

        for (int i = 0; i < muscles.length; i++) {
            String name = muscles[i];
            Button b = new Button(name);
            b.setPrefSize(130, 55);

            b.setOnMouseEntered(e -> showOverlayFor(area));
            b.setOnMouseExited(e  -> hideAllOverlays());

            if (i % 2 == 0) leftMenu.getChildren().add(b);
            else            rightMenu.getChildren().add(b);
        }
    }

    // =========================================================
    // HIGHLIGHT CONTROL
    // =========================================================
    private void showOverlayFor(String region) {
        hideAllOverlays();
        ImageView iv = overlays.get(region);
        if (iv != null) {
            iv.setVisible(true);
            dimLayer.setVisible(true); // Turn on the dark background dimmer
            System.out.println("Showing overlay: " + region);
        }
    }

    private void hideAllOverlays() {
        for (ImageView iv : overlays.values()) {
            iv.setVisible(false);
        }
        dimLayer.setVisible(false); // Turn off the dimmer
    }

    // =========================================================
    // CALORIE PANEL
    // =========================================================
    private void showCaloriePanel() {
        hideAllOverlays();

        Label title = new Label("Calorie Calculator");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label timeLabel = new Label("Exercise time (minutes):");
        TextField timeField = new TextField();
        timeField.setPromptText("e.g. 30");
        timeField.setMaxWidth(180);

        Label rateLabel = new Label("Burn rate (cal/hour):");
        TextField rateField = new TextField();
        rateField.setPromptText("e.g. 500");
        rateField.setMaxWidth(180);

        Label resultLabel = new Label("Calories burnt: --");
        resultLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button calcBtn = new Button("Calculate");
        calcBtn.setPrefSize(120, 40);
        calcBtn.setOnAction(e -> {
            try {
                int minutes = Integer.parseInt(timeField.getText().trim());
                int rate    = Integer.parseInt(rateField.getText().trim());
                Calorie_Calculator calc = new Calorie_Calculator(minutes * 60, rate);
                resultLabel.setText("Calories burnt: " + calc.calcCaloriesBurnt());
            } catch (NumberFormatException ex) {
                resultLabel.setText("Please enter valid numbers!");
            }
        });

        Button closeBtn = new Button("Close");
        closeBtn.setPrefSize(120, 40);
        closeBtn.setOnAction(e -> showGeneralMenu());

        VBox panel = new VBox(10, title,
                timeLabel, timeField,
                rateLabel, rateField,
                calcBtn, resultLabel,
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
        content.setCenter(panel);
    }
}