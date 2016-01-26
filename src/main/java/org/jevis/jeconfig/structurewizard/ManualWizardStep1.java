package org.jevis.jeconfig.structurewizard;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.structurewizard.WizardSelectedObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author Zeyd Bilal Calis
 */
public class ManualWizardStep1 extends WizardPane {

    private JEVisObject parentObject;
    private TextField nameTextField;
    private ObjectTree tree;
    private WizardSelectedObject wizardSelectedObject;

    public ManualWizardStep1(JEVisObject parentObject, ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setParentObject(parentObject);
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));
    }

    private HBox getInit() {
        VBox vBox = new VBox();

        HBox hbox = new HBox();
        Label namelbl = new Label();
        //Give your building name : 
        namelbl.setText("Building name : ");

        nameTextField = new TextField();
        nameTextField.setPrefWidth(200);
        hbox.setSpacing(30);
        hbox.getChildren().addAll(namelbl, nameTextField);
        hbox.setPadding(new Insets(200, 10, 10, 20));

        nameTextField.setPromptText("Building here");

        return hbox;
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        //Hide the back button.
        ObservableList<ButtonType> list = getButtonTypes();

        for (ButtonType type : list) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);
            }
        }
    }

    @Override
    public void onExitingPage(Wizard wizard) {
        //Erzeuge Building ,Data Source Directory und Data Directory Objekte.
        commitObject();
    }

    public void commitObject() {

        JEVisClass buildingClass = null;
        List<JEVisClass> listClasses = null;
        try {
            //Get the all children from selected Parent.
            listClasses = getParentObject().getAllowedChildrenClasses();
            //If the child name equals Building than get the type(JEVisClass) of this class.
            for (JEVisClass element : listClasses) {
                if (element.getName().equals("Building")) {
                    buildingClass = element;
                }
            }
            //Create Building object
            JEVisObject newObject = getParentObject().buildObject(nameTextField.getText(), buildingClass);
            newObject.commit();

            final TreeItem<JEVisObject> newTreeItem = tree.buildItem(newObject);
            TreeItem<JEVisObject> parentItem = tree.getObjectTreeItem(getParentObject());

            parentItem.getChildren().add(newTreeItem);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    tree.getSelectionModel().select(newTreeItem);
                }
            });

            //Speichere das neue erzeugte Building-Objekt als CurrentSelectedBuildingObject.
            //Das brauchen wir in dem letzten Schritt. --> -> tree.expandSelected(true); 
            wizardSelectedObject.setCurrentSelectedBuildingObject(newObject);

            ObservableList<JEVisClass> allowedChildrenClasses = FXCollections.observableArrayList(newTreeItem.getValue().getAllowedChildrenClasses());

            //Create Data Source Directory und Data Directory
            if (allowedChildrenClasses.size() > 0) {
                for (int i = 0; i < allowedChildrenClasses.size(); i++) {
                    JEVisObject newChildObject = null;
                    if (allowedChildrenClasses.get(i).getName().equals("Data Directory")) {
                        newChildObject = newTreeItem.getValue().buildObject(allowedChildrenClasses.get(i).getName(), allowedChildrenClasses.get(i));
                        wizardSelectedObject.setCurrentDataDirectory(newChildObject);
                    } else if (allowedChildrenClasses.get(i).getName().equals("Data Source Directory")) {
                        newChildObject = newTreeItem.getValue().buildObject(allowedChildrenClasses.get(i).getName(), allowedChildrenClasses.get(i));
                    }
                }
            }

            //Set the new parent.The new parent is Data Source Directory!
            //We need Data Source Directory for second step
            List<JEVisObject> listChildren = newTreeItem.getValue().getChildren();
            for (int i = 0; i < listChildren.size(); i++) {
                if (listChildren.get(i).getName().equals("Data Source Directory")) {
                    wizardSelectedObject.setCurrentSelectedObject(listChildren.get(i));
                }
            }

        } catch (JEVisException ex) {
            Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JEVisObject getParentObject() {
        return this.parentObject;
    }

    public void setParentObject(JEVisObject parentObject) {
        this.parentObject = parentObject;
    }
}
