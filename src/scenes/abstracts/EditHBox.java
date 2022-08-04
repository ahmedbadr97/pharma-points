package scenes.abstracts;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import scenes.images.ImageLoader;


public class EditHBox {
 private HBox buttonsHBox;
 private final Button editButton,saveButton,cancelButton;

    public EditHBox() {
        buttonsHBox=new HBox();
        editButton=initializeButton();
        saveButton=initializeButton();
        cancelButton=initializeButton();
        buttonsHBox.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        buttonsHBox.getChildren().addAll(cancelButton,editButton,saveButton);
        setMutability(false);

    }

    public HBox getButtonsHBox() {
        return buttonsHBox;
    }

    public Button initializeButton()
    {
       Button button=new Button();
        button.setGraphicTextGap(0);
        button.setPrefWidth(0);
        button.setPrefHeight(0);
        return button;
    }

    private void setMutability(boolean mutable)
 {
     if(mutable)
     {
         buttonDisappear(editButton);
         saveButton.setVisible(true);
         ImageLoader.icoButton(saveButton,"saveButton.png",10);
         cancelButton.setVisible(true);
         ImageLoader.icoButton(cancelButton,"cancel.png",10);

     }
     else {
         buttonDisappear(saveButton);
         editButton.setVisible(true);
         buttonDisappear(cancelButton);
         ImageLoader.icoButton(editButton,"editButton.png",10);
     }

 }
    public void buttonDisappear(Button button)
    {
        button.setVisible(false);
        button.setGraphic(null);
        button.setPrefWidth(0);
    }
    public void setEditAction(EventHandler<ActionEvent> event)
    {
        editButton.setOnAction((actionEvent)->{
            event.handle(actionEvent);
            setMutability(true);
        });
    }
    public void setCancelAction(EventHandler<ActionEvent> event)
    {
        cancelButton.setOnAction((actionEvent)->{
            event.handle(actionEvent);
            setMutability(false);
        });
    }
    public void setSaveAction(EventHandler<ActionEvent> event)
    {
        saveButton.setOnAction((actionEvent)->{
            event.handle(actionEvent);
            setMutability(false);
        });
    }
}
