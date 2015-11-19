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


import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.jeconfig.structurewizard.WizardSelectedObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;
import org.jevis.structurecreator.WiotechStructureCreator;

/**
 * This class reads the mysql database credentials and starts the structure creator
 * 
 */
public class AutomatedWizardStep2 extends WizardPane {

  
    private TextField serverNameTextField;
    private TextField databaseNameTextField;
    private TextArea errorLbl;
    private Label doneLbl;
    private ProgressBar creationStatus;
    private final ObjectTree tree;
    private final WizardSelectedObject wizardSelectedObject;
    private TextField localManagerIPTxtf;
    private TextField databaseUserTxtf;
    private TextField databasePwdTxtf;
    private Task task;
    private Thread thread;
    
    /**
     * 
     * @param tree JEVis structure tree
     * @param wizardSelectedObject Selected Object in JECionfig
     */
    public AutomatedWizardStep2(ObjectTree tree, WizardSelectedObject wizardSelectedObject) {
        
        this.wizardSelectedObject = wizardSelectedObject;
        this.tree = tree;
        setMinSize(500, 500);
        setGraphic(null);
    }
    
    /**
     * GUI init and hide 'Back' button
     * @param wizard 
     */
    @Override
    public void onEnteringPage(Wizard wizard) {
        setContent(getInit());
        
        
        ObservableList<ButtonType> list = getButtonTypes();
        for (ButtonType type : list) {
            //Hides the 'Back' button
            if (type.getButtonData().equals(ButtonBar.ButtonData.BACK_PREVIOUS)) {
                Node prev = lookupButton(type);
                prev.visibleProperty().setValue(Boolean.FALSE);
            }// On 'Cancel' button pressed
            else if (type.getButtonData().equals(ButtonBar.ButtonData.CANCEL_CLOSE)){
                Button prev = (Button)lookupButton(type);
                prev.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                          thread.interrupt();
                          
                    }
                });
            }
        }
    }
        
    /**
     * Cancel the current task on page exiting
     * @param wizard 
     */
    @Override
    public void onExitingPage(Wizard wizard) {
        thread.interrupt();
    }

    /**
     * initialize the GUI elements
     * @return 
     */
    private BorderPane getInit() {
        
        BorderPane root = new BorderPane();
        GridPane gridpane = new GridPane();
        creationStatus = new ProgressBar();
        
        Label localManagerIPLbl = new Label("Local Manager IP:");
        localManagerIPTxtf = new TextField();
        localManagerIPTxtf.setPrefWidth(200);
        
        Label databaseUserLbl = new Label("DB User");
        databaseUserTxtf = new TextField();
        databaseUserTxtf.setPrefWidth(200);
        
        Label databasePwdLbl = new Label("DB Password");
        databasePwdTxtf = new TextField();
        databasePwdTxtf.setPrefWidth(200);

        Button button = new Button("Start Structure Creation");

        gridpane.addRow(0, localManagerIPLbl, localManagerIPTxtf);
        gridpane.addRow(1, databaseUserLbl, databaseUserTxtf);
        gridpane.addRow(2, databasePwdLbl, databasePwdTxtf);
        gridpane.addRow(3, button);
        gridpane.setHgap(10);//horizontal gap in pixels 
        gridpane.setVgap(10);//vertical gap in pixels
        gridpane.setPadding(new Insets(50, 10, 10, 10));////margins around the whole grid
        
        doneLbl = new Label("Structure Created");
        doneLbl.setVisible(false);
        VBox labelVBox = new VBox(doneLbl);
        gridpane.addRow(4, labelVBox);
        gridpane.add(creationStatus, 1, 3);
        creationStatus.setVisible(false);
        //errorLbl = new Label();
        errorLbl = new TextArea();
        errorLbl.setVisible(false);

        // event listener for the start button
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                
                creationStatus.setVisible(true);
                execStructurCreation();
                thread = new Thread (task);
                thread.start();
                 
            }
        });
        root.setBottom(errorLbl);
        root.setTop(gridpane);
        return root;
    }
    
    /**
     * creates the structure
     */
    private void execStructurCreation(){
        task = new Task() {

                    @Override
                    protected Object call() throws Exception {
                        try {  
                                WiotechStructureCreator wsc = new WiotechStructureCreator(localManagerIPTxtf.getText(), 3306, "db_lm_cbv2", databaseUserTxtf.getText(), databasePwdTxtf.getText());
                                wsc.createStructure(tree, wizardSelectedObject.getCurrentSelectedBuildingObject());
                                wizardSelectedObject.setCurrentSelectedBuildingObject(wizardSelectedObject.getCurrentSelectedBuildingObject().getChildren().get(0));
                                Platform.runLater(() ->creationStatus.setVisible(false));
                                Platform.runLater(() ->doneLbl.setVisible(true));
                        } catch (Exception ex) {
                                Platform.runLater(() ->errorLbl.setVisible(true));  
                                Platform.runLater(() ->errorLbl.setText(ex.getMessage()));
                                Platform.runLater(() ->creationStatus.setVisible(false));
                                ex.printStackTrace();
                         }
                            
                        return null;
                    }
                };
    }
}
