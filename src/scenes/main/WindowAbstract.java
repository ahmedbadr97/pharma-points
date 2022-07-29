package scenes.main;

import javafx.scene.control.Alert;
import scenes.fxml.FMXLResourcesLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;

public abstract class WindowAbstract<windowController> {
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private Parent parent;
    private windowController controller;

    public interface OnCloseAction {
        public void closeAction();
    }

    private final ArrayList<OnCloseAction> onCloseActions;

    //TODO add update screen method
    public WindowAbstract() {
        stage = new Stage();
        onCloseActions = new ArrayList<>();
    }

    public void load(String fxmlfileName, int width, int hight) {
        try {
            loader = FMXLResourcesLoader.FXML(fxmlfileName + ".fxml");
            parent = loader.load();
            scene = new Scene(parent, width, hight);
            //          stage.getIcons().add(ImageLoader.LoadImage("exeicon.ico"));
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                        executeOnCloseActions();
                    }
            );
            stage.setMaxWidth(width + 40);
            stage.setMaxHeight(hight + 40);
            controller = loader.getController();

        } catch (Exception e1) {
            new Alerts("error loading FXML Files in " + this.getClass().getName() + " " + e1.getMessage(), Alert.AlertType.ERROR);

        }
    }

    public void loadFxmlOnly(String fxmlfileName, int width, int hight) {
        try {
            loader = FMXLResourcesLoader.FXML(fxmlfileName + ".fxml");
            parent = loader.load();
            controller = loader.getController();
        } catch (Exception e1) {
            new Alerts("error loading FXML Files in " + this.getClass().getName() + " " + e1.getMessage(), Alert.AlertType.ERROR);

        }
    }

    public void load(Stage stage, String fxmlfileName, int width, int hight) {
        this.stage = stage;
        try {
            loader = FMXLResourcesLoader.FXML(fxmlfileName + ".fxml");
            parent = loader.load();
            scene = new Scene(parent, width, hight);
            stage.getIcons().add(new Image("file:src/Images/exeicon.ico"));
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                        executeOnCloseActions();

                    }
            );
            controller = loader.getController();
        } catch (Exception e1) {
            new Alerts("error loading FXML Files in " + this.getClass().getName() + " " + e1.getMessage(), Alert.AlertType.ERROR);


        }
    }

    public void showStage() {
        stage.show();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Parent getParent() {
        return parent;
    }

    public windowController getController() {
        return controller;
    }

    public void addOnCloseAction(OnCloseAction onCloseAction) {
        onCloseActions.add(onCloseAction);
    }

    private void executeOnCloseActions() {
        for (OnCloseAction action : onCloseActions)
            action.closeAction();
    }

    public void setController(windowController controller) {
        this.controller = controller;
    }

    public void setOnTop() {
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    public void closeStage() {
        executeOnCloseActions();

        this.stage.close();
    }

}
