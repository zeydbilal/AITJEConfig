/**
 * Copyright (C) 2014 Envidatec GmbH <info@envidatec.com>
 *
 * This file is part of JEConfig.
 *
 * JEConfig is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * JEConfig is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * JEConfig. If not, see <http://www.gnu.org/licenses/>.
 */
package org.jevis.jeconfig.plugin.classes;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.jevis.jeapi.JEVisClass;
import org.jevis.jeapi.JEVisConstants;
import org.jevis.jeapi.JEVisException;
import org.jevis.jeapi.JEVisType;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.ObjectPlugin;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ClassEditor {

    private Desktop desktop = Desktop.getDesktop();
    private JEVisClass _class;
    Button fIcon;
    private boolean _typeOpen = false;
    private boolean _relationOpen = false;
    private TitledPane t2;
    private List<JEVisType> _toDelete;

    public ClassEditor() {
    }

    public Node buildEditor(JEVisClass jclass) {
        _class = jclass;
        _toDelete = new ArrayList<>();

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        Label lName = new Label("Name:");
        Label lDescription = new Label("Description:");
        Label lIsUnique = new Label("Unique:");
        Label lIcon = new Label("Icon:");
        Label lRel = new Label("Relaionships:");
        Label lInherit = new Label("Inheritance");
        Label lTypes = new Label("Types:");


        TextField fName = new TextField();
        fName.prefWidthProperty().set(250d);
        TextArea fDescript = new TextArea();
        fIcon = new Button("", getIcon(jclass));
        CheckBox fUnique = new CheckBox();
        fUnique.setSelected(false);

        ClassRelationshipTable table = new ClassRelationshipTable();
        GridPane tTable = table.buildTree(jclass);

        Button fInherit = new Button();

        gridPane.add(lName, 0, 0);
        gridPane.add(fName, 1, 0);
        gridPane.add(lInherit, 0, 1);
        gridPane.add(fInherit, 1, 1);
        gridPane.add(lIcon, 0, 2);
        gridPane.add(fIcon, 1, 2);
        gridPane.add(lIsUnique, 0, 3);
        gridPane.add(fUnique, 1, 3);
        gridPane.add(lDescription, 0, 4);
        gridPane.add(fDescript, 1, 4, 2, 1);


        GridPane.setHalignment(lInherit, HPos.RIGHT);
        GridPane.setHalignment(lIcon, HPos.RIGHT);
        GridPane.setValignment(lIcon, VPos.TOP);
        GridPane.setHalignment(lName, HPos.RIGHT);
        GridPane.setHalignment(lIsUnique, HPos.RIGHT);
        GridPane.setHalignment(lDescription, HPos.RIGHT);
        GridPane.setValignment(lDescription, VPos.TOP);
        GridPane.setHalignment(lRel, HPos.RIGHT);
        GridPane.setValignment(lRel, VPos.TOP);
        GridPane.setHgrow(tTable, Priority.ALWAYS);
        GridPane.setHalignment(lTypes, HPos.RIGHT);
        GridPane.setValignment(lTypes, VPos.TOP);

        try {
            if (jclass != null) {
                fName.setText(jclass.getName());
                if (jclass.getInheritance() != null) {
                    fInherit.setText(jclass.getInheritance().getName());
                } else {
                    fInherit.setText("");
                }

                fDescript.setText(jclass.getDescription());
                fUnique.setSelected(jclass.isUnique());
            }

        } catch (JEVisException ex) {
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }


        fIcon.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                FileChooser fileChooser = new FileChooser();
                if (JEConfig.getLastFile() != null) {
                    fileChooser.setInitialDirectory(JEConfig.getLastFile().getParentFile());
                }

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
                FileChooser.ExtensionFilter gifFilter = new FileChooser.ExtensionFilter("GIF files (*.gif)", "*.gif");
                FileChooser.ExtensionFilter jpgFilter = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.jpg");
                fileChooser.getExtensionFilters().addAll(extFilter, gifFilter, jpgFilter);
                File file = fileChooser.showOpenDialog(JEConfig.getStage());
                if (file != null) {
                    openFile(file);
                    JEConfig.setLastFile(file);
                }
            }
        });

        final TitledPane t1 = new TitledPane("General", gridPane);
        t2 = new TitledPane("Types", buildTypeNode());

        TitledPane t3 = new TitledPane("Relationships", tTable);
        t1.setAnimated(false);
        t1.setExpanded(true);
        t2.setExpanded(_typeOpen);
        t3.setExpanded(_relationOpen);

        t1.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                t1.setAnimated(true);
            }
        });

        t2.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                _typeOpen = newValue;
            }
        });
        t3.expandedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                _relationOpen = newValue;
            }
        });

        VBox root = new VBox();
        root.getChildren().addAll(t1, t2, t3);

        return root;
    }

    private ChoiceBox buildPrimitiveTypeBox(JEVisType type) {
        ChoiceBox primType = new ChoiceBox();
        primType.setItems(ClassHelper.getAllPrimitiveTypes());
        primType.getSelectionModel().select(ClassHelper.getNameforPrimitiveType(type));
        primType.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                System.out.println("new Type: " + t1);
//                type.setPrimitiveType(i);
            }
        });

        return primType;
    }

    private Node buildTypeNode() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 5, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);


        Label headerName = new Label("Name");
        Label headerPType = new Label("Primitive Type");
        Label headerGType = new Label("GUI Type");
        Label headerColtrol = new Label("Controls");

        Separator headerSep = new Separator();
        gridPane.add(headerName, 0, 0);
        gridPane.add(headerPType, 1, 0);
        gridPane.add(headerGType, 2, 0);
        gridPane.add(headerColtrol, 3, 0);
        gridPane.add(headerSep, 0, 1, 6, 1);

        int row = 2;
        try {
            Collections.sort(_class.getTypes());
            for (final JEVisType type : _class.getTypes()) {
                type.getPrimitiveType();
                TextField lName = new TextField(type.getName());
//                lName.setEditable(false);

                ChoiceBox guiType = new ChoiceBox();
                guiType.setItems(FXCollections.observableArrayList(
                        "String,", "IP-Address", "Number", "File Selector", "Check Box", "PASSWORD Field"));



                Button up = new Button();
                if (_class.getTypes().indexOf(type) == 0) {
                    up.disableProperty().set(true);
                }

                up.setGraphic(JEConfig.getImage("1395085229_arrow_return_right_up.png", 20, 20));
                up.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        try {
                            int pos = _class.getTypes().indexOf(type);
                            int lastPos = type.getGUIPosition();
                            System.out.println("position in list: " + pos);
                            if (pos > 0) {
                                System.out.println("old GUI Pos: " + type.getGUIPosition());
                                JEVisType prevType = _class.getTypes().get(pos - 1);
                                type.setGUIPosition(prevType.getGUIPosition());
                                prevType.setGUIPosition(lastPos);
                                System.out.println("new GUI Pos: " + type.getGUIPosition());
                            }
                            t2.setContent(buildTypeNode());

                        } catch (JEVisException ex) {
                            Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });


                Button down = new Button();
                if (_class.getTypes().indexOf(type) == _class.getTypes().size() - 1) {
                    down.disableProperty().set(true);
                }
                down.setGraphic(JEConfig.getImage("1395085233_arrow_return_right_down.png", 20, 20));
                down.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        try {
                            int pos = _class.getTypes().indexOf(type);
                            int lastPos = type.getGUIPosition();
                            System.out.println("position in list: " + pos);
                            if (pos < _class.getTypes().size() - 1) {
//                                System.out.println("old GUI Pos: " + type.getGUIPosition());

                                JEVisType afterType = _class.getTypes().get(pos + 1);
                                type.setGUIPosition(afterType.getGUIPosition());
                                afterType.setGUIPosition(lastPos);

//                                System.out.println("new GUI Pos: " + type.getGUIPosition());
                            }
                            t2.setContent(buildTypeNode());

                        } catch (JEVisException ex) {
                            Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });


                Button remove = new Button();
                remove.setGraphic(JEConfig.getImage("list-remove.png", 20, 20));
                remove.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        try {
                            _class.getTypes().remove(type);//TODo: this is no so save..
                            _toDelete.add(type);
                            type.delete();//TODO remove this and use the global "Save action"
                            t2.setContent(buildTypeNode());

                        } catch (JEVisException ex) {
                            Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });

                //                                              x, y
                gridPane.add(lName, 0, row);
                gridPane.add(buildPrimitiveTypeBox(type), 1, row);
                gridPane.add(guiType, 2, row);
                gridPane.add(remove, 3, row);
                gridPane.add(up, 4, row);
                gridPane.add(down, 5, row);


                row++;


            }
        } catch (JEVisException ex) {
            Logger.getLogger(ClassEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

        Separator newSep = new Separator();
        gridPane.add(newSep, 0, row++, 6, 1);

        final TextField lName = new TextField("New Attribute");
        final ChoiceBox pTypeBox = buildPrimitiveTypeBox(null);

        Button newB = new Button();
        newB.setGraphic(JEConfig.getImage("list-add.png", 20, 20));
        newB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                try {
                    JEVisType newType = _class.buildType(lName.getText());
                    JEVisType lastType = _class.getTypes().get(_class.getTypes().size() - 1);
                    newType.setPrimitiveType(ClassHelper.getIDforPrimitiveType(pTypeBox.getSelectionModel().getSelectedItem().toString()));
                    newType.setGUIPosition(lastType.getGUIPosition() + 1);
                    System.out.println("new pos for new Type: " + newType.getGUIPosition());

                    t2.setContent(buildTypeNode());

                } catch (Exception ex) {
                    Logger.getLogger(ClassEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });



        ChoiceBox guiType = new ChoiceBox();
        guiType.setItems(FXCollections.observableArrayList(
                "Text", "IP-Address", "Number", "File Selector", "Check Box", "PASSWORD Field"));

        gridPane.add(lName, 0, row);
        gridPane.add(pTypeBox, 1, row);
        gridPane.add(guiType, 2, row);
        gridPane.add(newB, 3, row);

        return gridPane;
    }

    private void openFile(File file) {
        try {
//            Image image = new Image(file.toURI().toString());
//            ImageView iv = new ImageView(image);
//            _class.setIcon(new ImageIcon(convertToAwtImage(image).getScaledInstance(60, 60, java.awt.Image.SCALE_SMOOTH)));
            _class.setIcon(file);
//            File newIcon = desktop.open(file);
            _class.commit();
//            fIcon.setGraphic(getImageView(_class));



        } catch (Exception ex) {
            Logger.getLogger(ClassEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public void comitAll() {
        try {
            _class.commit();
            for (JEVisType type : _toDelete) {
                type.delete();

            }


        } catch (JEVisException ex) {
            Logger.getLogger(ClassEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    public void rollback() {
        try {
            for (JEVisType type : _class.getTypes()) {
                type.rollBack();
            }
            _class.rollBack();

        } catch (JEVisException ex) {
            Logger.getLogger(ClassEditor.class
                    .getName()).log(Level.SEVERE, null, ex);
            Dialogs.showErrorDialog(JEConfig.getStage(), ex.getMessage(), "Error", "Error", ex);
        }
    }

    private ImageView getIcon(JEVisClass jclass) {
        try {
            return ImageConverter.convertToImageView(jclass.getIcon(), 30, 30);
        } catch (Exception ex) {
            System.out.println("Error while geeting class icon: " + ex);
            ex.printStackTrace();
            return JEConfig.getImage("1393615831_unknown2.png", 30, 30);
        }


    }
}
