/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author CalisZ
 */
public class ManualWizardStep1 extends WizardPane {

    private JEVisObject parentObject;
    private TextField nameTextField;
    private ObjectTree tree;

    public ManualWizardStep1(JEVisObject parentObject) {
        setParentObject(parentObject);
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));
    }

    private HBox getInit() {
        VBox vBox = new VBox();

        HBox hbox = new HBox();
        Label namelbl = new Label();
        //Please give your building name : 
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
        commitObject();
    }

    public JEVisObject getParentObject() {
        return this.parentObject;
    }

    public void setParentObject(JEVisObject parentObject) {
        this.parentObject = parentObject;
    }

    public void commitObject() {

        JEVisClass buildingClass = null;
        List<JEVisClass> listClasses = null;
        try {
            listClasses = getParentObject().getAllowedChildrenClasses();

            for (JEVisClass element : listClasses) {
                if (element.getName().equals("Building")) {
                    buildingClass = element;
                }
            }
            JEVisObject newObject = getParentObject().buildObject(nameTextField.getText(), buildingClass);
            newObject.commit();
        } catch (JEVisException ex) {
            Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
