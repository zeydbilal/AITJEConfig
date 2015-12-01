/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEApplication.
 *
 * JEApplication is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation in version 3.
 *
 * JEApplication is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEApplication. If not, see <http://www.gnu.org/licenses/>.
 *
 * JEApplication is part of the OpenJEVis project, further project information
 * are published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.plugin.object;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.commons.CommonClasses;
import org.jevis.jeconfig.tool.NumberSpinner;

/**
 *
 * @author fs
 */
public class CopyObjectDialog {

    public static String ICON = "1403555565_stock_folder-move.png";

    private JEVisClass createClass;
    private String infoText = "";
    private TextField nameField = new TextField();
    private int createCount = 1;
    final Button ok = new Button("OK");

    final RadioButton move = new RadioButton("Move");
    final RadioButton link = new RadioButton("Link");
    final RadioButton copy = new RadioButton("Copy");
    final RadioButton clone = new RadioButton("Clone");

    public static enum Response {

        MOVE, LINK, CANCEL, CLONE, COPY
    };

    private Response response = Response.CANCEL;

    public JEVisClass getCreateClass() {
        return createClass;
    }

    public String getCreateName() {
        return nameField.getText();
    }

    public int getCreateCount() {
        if (createCount > 0 && createCount < 100) {
            return createCount;
        } else {
            return 1;
        }
    }

    /**
     *
     * @param owner
     * @param object
     * @param newParent
     * @param fixClass
     * @param objName
     * @return
     */
    public Response show(Stage owner, final JEVisObject object, final JEVisObject newParent) {
        final Stage stage = new Stage();

        final BooleanProperty isOK = new SimpleBooleanProperty(false);

        stage.setTitle("Move/Link Object");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

//        BorderPane root = new BorderPane();
        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(450);
        stage.setHeight(350);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        scene.setCursor(Cursor.DEFAULT);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: linear-gradient(#e2e2e2,#eeeeee);");
        header.setPadding(new Insets(10, 10, 10, 10));

        Label topTitle = new Label("Choose Action");
        topTitle.setTextFill(Color.web("#0076a3"));
        topTitle.setFont(Font.font("Cambria", 25));

        ImageView imageView = ResourceLoader.getImage(ICON, 64, 64);

        stage.getIcons().add(imageView.getImage());

        VBox vboxLeft = new VBox();
        VBox vboxRight = new VBox();
        vboxLeft.getChildren().add(topTitle);
        vboxLeft.setAlignment(Pos.CENTER_LEFT);
        vboxRight.setAlignment(Pos.CENTER_LEFT);
        vboxRight.getChildren().add(imageView);

        header.setLeft(vboxLeft);

        header.setRight(vboxRight);

        HBox buttonPanel = new HBox();

        ok.setDefaultButton(true);
        ok.setDisable(true);

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        buttonPanel.getChildren().addAll(ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);
        buttonPanel.setMaxHeight(25);

        GridPane gp = new GridPane();
        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(5);

        final ToggleGroup group = new ToggleGroup();

        move.setMaxWidth(Double.MAX_VALUE);
        move.setMinWidth(120);
        link.setMaxWidth(Double.MAX_VALUE);
        copy.setMaxWidth(Double.MAX_VALUE);
        clone.setMaxWidth(Double.MAX_VALUE);

        link.setToggleGroup(group);
        move.setToggleGroup(group);
        copy.setToggleGroup(group);
        clone.setToggleGroup(group);

        nameField.setPromptText("Name of the new object(s)");

        final Label nameLabel = new Label("Name:");
        final Label countLabel = new Label("Count:");

        final NumberSpinner count = new NumberSpinner(BigDecimal.valueOf(1), BigDecimal.valueOf(1));
        final Label info = new Label("Test");
        info.wrapTextProperty().setValue(true);
//        info.setPrefRowCount(4);
//        info.setDisable(true);
        info.setMinWidth(1d);
//        info.setMaxWidth(200);
//        info.setPrefWidth(200);

        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {

                if (t1 != null) {
//                    System.out.println("new toggel: " + t1);
                    if (t1.equals(move)) {
                        infoText = String.format("Move '%s' into '%s'", object.getName(), newParent.getName());
                        ok.setDisable(false);
                        nameField.setDisable(true);
                        nameLabel.setDisable(true);
                        countLabel.setDisable(true);
                        count.setDisable(true);
                    } else if (t1.equals(link)) {
                        infoText = String.format("Create an new link of '%s' into '%s'", object.getName(), newParent.getName());
                        nameField.setDisable(false);
                        count.setDisable(true);
                        nameLabel.setDisable(false);
                        countLabel.setDisable(true);
                        checkName();

                    } else if (t1.equals(copy)) {
                        infoText = String.format("Copy '%s' into '%s' without data", object.getName(), newParent.getName());

//                        infoText = String.format("<html>Copy <font color=\"#DB6A6A\">'%s'</font> into <font color=\"#DB6A6A\">'%s'</font> without data</html>", object.getName(), newParent.getName());
                        nameField.setDisable(false);
                        count.setDisable(false);
                        nameLabel.setDisable(false);
                        countLabel.setDisable(false);
                        nameField.setText(object.getName());
                        checkName();
                    } else if (t1.equals(clone)) {
                        infoText = String.format("Clone '%s' into '%s' with all data", object.getName(), newParent.getName());
                        nameField.setDisable(false);
                        count.setDisable(false);
                        nameLabel.setDisable(false);
                        countLabel.setDisable(false);
                        nameField.setText(object.getName());
                        checkName();
                    }

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            info.setText(infoText);
                        }
                    });
                }

            }
        });

        try {
            System.out.println("-> Object: " + object.getJEVisClass());
            System.out.println("newParent: " + newParent.getJEVisClass());
            System.out.println("Is allowed under target: " + object.isAllowedUnder(newParent));
            System.out.println("");

            if (newParent.getJEVisClass().getName().equals("View Directory") || newParent.getJEVisClass().getName().equals(CommonClasses.LINK.NAME)) {
                link.setDisable(false);
            } else {
                link.setDisable(true);
            }

            if (object.isAllowedUnder(newParent)) {
                move.setDisable(false);
                copy.setDisable(false);
                clone.setDisable(false);
            } else {
                move.setDisable(true);
                copy.setDisable(true);
                clone.setDisable(true);
            }

            if (!link.isDisable()) {
                group.selectToggle(link);
                nameField.setText(object.getName());
                ok.setDisable(false);
            } else if (!move.isDisable()) {
                group.selectToggle(move);
            }

        } catch (JEVisException ex) {
            Logger.getLogger(CopyObjectDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        HBox nameBox = new HBox(5);
        nameBox.getChildren().setAll(nameLabel, nameField);
        nameBox.setAlignment(Pos.CENTER_LEFT);
        HBox countBox = new HBox(5);
        countBox.getChildren().setAll(countLabel, count);
        countBox.setAlignment(Pos.CENTER_LEFT);

        Separator s1 = new Separator(Orientation.HORIZONTAL);
        GridPane.setMargin(s1, new Insets(5, 0, 10, 0));

        //check allowed
        int x = 0;

        gp.add(link, 0, x);
        gp.add(move, 0, ++x);

        gp.add(copy, 0, ++x);
        gp.add(clone, 0, ++x);

        gp.add(new Separator(Orientation.VERTICAL), 1, 0, 1, 4);
        gp.add(s1, 0, ++x, 3, 1);
        gp.add(nameBox, 0, ++x, 3, 1);
        gp.add(countBox, 0, ++x, 3, 1);

        gp.add(info, 2, 0, 1, 4);

        GridPane.setHgrow(info, Priority.ALWAYS);
        GridPane.setVgrow(info, Priority.ALWAYS);
        GridPane.setHalignment(info, HPos.LEFT);
        GridPane.setValignment(info, VPos.TOP);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setMinHeight(10);

        root.getChildren().addAll(header, new Separator(Orientation.HORIZONTAL), gp, buttonPanel);
        VBox.setVgrow(gp, Priority.ALWAYS);
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(header, Priority.NEVER);

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();

                if (group.getSelectedToggle().equals(move)) {
                    response = Response.MOVE;
                } else if (group.getSelectedToggle().equals(link)) {
                    response = Response.LINK;
                } else if (group.getSelectedToggle().equals(copy)) {
                    response = Response.COPY;
                } else if (group.getSelectedToggle().equals(clone)) {
                    response = Response.CLONE;
                }

            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

        nameField.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if (nameField.getText() != null && !nameField.getText().equals("")) {
                    ok.setDisable(false);
                }
            }
        });

        stage.showAndWait();

        return response;
    }

    private void checkName() {
        if (nameField.getText() != null && !nameField.getText().isEmpty()) {
            ok.setDisable(false);
        } else {
            ok.setDisable(true);
        }
    }
}
