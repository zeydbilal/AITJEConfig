package org.jevis.jeconfig.batchmode;

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
import org.jevis.jeconfig.plugin.object.ObjectTree;
import org.jevis.structurecreator.WiotechStructureCreator;


/**
 *
 * @author Zeyd Bilal Calis
 */
 //In dieser Klasse wird ein Building erzeugt.
public class AutomatedWizardStep1 extends WizardPane {

    private JEVisObject parentObject;
    private TextField newBuildingTxtf;
    private ObjectTree tree;
    private WizardSelectedObject wizardSelectedObject;

    public AutomatedWizardStep1(JEVisObject parentObject, ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setParentObject(parentObject);
        setMinSize(500, 500);
        setContent(getInit());
        setGraphic(JEConfig.getImage("create_wizard.png", 100, 100));
    }

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
        //Erzeuge Building ,Data Source Directory und Data Directory Objekte
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
            
            
            JEVisObject buildingObject = null;
            
            // Check if the building name already exists, if not, create a new one
            List<JEVisObject> existingChildList = getParentObject().getChildren();
            
            for(JEVisObject jeO : existingChildList){
                Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,jeO.getName());
                if(jeO.getName().equals(newBuildingTxtf.getText())){
                    Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,jeO.getName()+ "in for!!!!!!!!!!!");
                   buildingObject = jeO; 
                   break;
                }
            }
            
            if(buildingObject == null){
                Logger.getLogger(ManualWizardStep1.class.getName()).log(Level.SEVERE,"new created");
                buildingObject = getParentObject().buildObject(newBuildingTxtf.getText(), buildingClass);
                buildingObject.commit();
            }
            
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
