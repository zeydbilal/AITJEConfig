/**
 * Copyright (C) 2015 WernerLamprecht <werner.lamprecht@ymail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * This wizard is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.structurewizard;

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
 * Checks if the building with the same name exists, otherwise it creates a new one
 * 
 * @author Werner Lamprecht, Zeyd Bilal Calis
 */
public class AutomatedWizardStep1 extends WizardPane {

    private JEVisObject parentObject;
    private TextField newBuildingTxtf;
    // JEVis tree for structure updates
    private ObjectTree tree;
    // global Object variable
    private WizardSelectedObject wizardSelectedObject;

    /**
     * 
     * @param parentObject Has to be 'Monitored Object Directory' node
     * @param tree JEVis structure tree
     * @param wizardSelectedObject Selected Object in JECionfig
     */
    public AutomatedWizardStep1(JEVisObject parentObject, ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setParentObject(parentObject);
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));
    }

    /**
     * Sets the needed graphicl interface objects
     * 
     * @return JAVA FX GUI Objects
     */
    private HBox getInit() {
        
        HBox hbox = new HBox();
        Label newBuildingLbl = new Label("Building Name");
        
        newBuildingTxtf = new TextField();
        newBuildingTxtf.setPrefWidth(200);
        hbox.setSpacing(30);
        hbox.getChildren().addAll(newBuildingLbl, newBuildingTxtf);
        hbox.setPadding(new Insets(100, 10, 10, 20));

        return hbox;
    }

    @Override
    public void onEnteringPage(Wizard wizard) {
        
        //Hides the 'Back' button
        ObservableList<ButtonType> list = getButtonTypes();
        for (ButtonType type : list) {
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);
            }
        }
    }
    
    /**
     * Create the new buiilding or get ID if it exists
     * 
     * @param wizard 
     */
    @Override
    public void onExitingPage(Wizard wizard) {
        
        JEVisClass buildingClass = null;
        List<JEVisClass> listClasses = null;
        try {
            // Get the all allowed classes from selected parent
            listClasses = getParentObject().getAllowedChildrenClasses();
            // If 'Building' is allowed, assign to value
            for (JEVisClass element : listClasses) {
                if (element.getName().equals("Building")) {
                    buildingClass = element;
                }
            }
            
            JEVisObject buildingObject = null;

            List<JEVisObject> existingChildList = getParentObject().getChildren();
            // Check if the building name already exists, if not, create a new one
            for(JEVisObject jeO : existingChildList){
                Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,jeO.getName());
                if(jeO.getName().equals(newBuildingTxtf.getText())){
                    Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,jeO.getName()+ "in for!!!!!!!!!!!");
                   buildingObject = jeO; 
                   break;
                }
            }
            // If buildingObject still null, create a new one
            if(buildingObject == null){
                Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,"new created");
                buildingObject = getParentObject().buildObject(newBuildingTxtf.getText(), buildingClass);
                //buildingObject.commit();
            }
            // Set the buildingobject for the next step
            wizardSelectedObject.setCurrentSelectedBuildingObject(buildingObject);

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
