/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.form;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author CalisZ
 */
public class FormDialog {

    GridPane gridPane = new GridPane();

    public void getFormDialog() {

           BorderPane root = new BorderPane();
                Scene scene = new Scene(root, 500, 500, Color.WHITE);
               
                GridPane gridPane = new GridPane();

                Label name = new Label("Name");
                Label unit = new Label("Unit");
                TextField nameField = new TextField();
                TextField unitField = new TextField();

                Button createStructure = new Button("Create Structure");

                GridPane.setHalignment(name, HPos.RIGHT);
                GridPane.setHalignment(unit, HPos.LEFT);
                GridPane.setHalignment(createStructure, HPos.RIGHT);

                gridPane.add(name, 0, 0);
                gridPane.add(unit, 1, 0);
                gridPane.add(nameField, 0, 1);
                gridPane.add(unitField, 1, 1);
                gridPane.add(createStructure, 1, 2);
                gridPane.setPadding(new Insets(50));
                root.setCenter(gridPane);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Form Dialog");
                stage.show();
    }

}
