/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.plugin.object;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
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
import javafx.util.Callback;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.sql.RelationsManagment;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author CalisZ
 */
public class NewFormDialog {

    private LinkedList<String> listCreateName = new LinkedList<>();
    private LinkedList<JEVisClass> listCreateClass = new LinkedList<>();

    private LinkedList<Label> listLabelName = new LinkedList<>();
    private LinkedList<TextField> listTextFieldName = new LinkedList<>();
    private LinkedList<Label> listLabelClass = new LinkedList<>();
    private LinkedList<ComboBox<JEVisClass>> listComboBox = new LinkedList<>();

    public static String ICON = "add_a_form.png";

    public static enum Type {

        NEW, RENAME
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    public LinkedList<String> getlistCreateName() {
        return listCreateName;
    }

    public LinkedList<JEVisClass> getlistCreateClasse() {
        return listCreateClass;
    }

    /**
     *
     * @param owner
     * @param jclass
     * @param parent
     * @param fixClass
     * @param type
     * @param objName
     * @return
     */
    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
        listLabelName.add(new Label("Name:"));
        listLabelName.add(new Label("Name:"));

        listTextFieldName.add(new TextField());
        listTextFieldName.add(new TextField());

        listLabelClass.add(new Label("Class:"));
        listLabelClass.add(new Label("Class:"));

        final Stage stage = new Stage();

        stage.setTitle("New Form");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(700);
        stage.setHeight(700);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: linear-gradient(#e2e2e2,#eeeeee);");
        header.setPadding(new Insets(10, 10, 10, 10));

        Label topTitle = new Label("New Form");
        topTitle.setTextFill(Color.web("#0076a3"));
        topTitle.setFont(Font.font("Cambria", 25));

        ImageView imageView = ResourceLoader.getImage(ICON, 50, 50);

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

        final Button ok = new Button("Create Structure");
        ok.setDefaultButton(true);
        ok.setDisable(true);

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        buttonPanel.getChildren().addAll(ok, cancel);
        buttonPanel.setAlignment(Pos.CENTER_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(10);
        buttonPanel.setMaxHeight(25);

        final TextField fName = new TextField();
        fName.setPromptText("Name of the Object");

        if (objName != null) {
            fName.setText(objName);
        }

        Label lClass = new Label("Class:");

        ObservableList<JEVisClass> options = FXCollections.observableArrayList();

        if (type == Type.NEW) {
            try {

                options = FXCollections.observableArrayList(
                        parent.getAllowedChildrenClasses()
                );

            } catch (JEVisException ex) {
                Logger.getLogger(NewFormDialog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (type == Type.RENAME) {
            options.add(jclass);
        }

        Callback<ListView<JEVisClass>, ListCell<JEVisClass>> cellFactory = new Callback<ListView<JEVisClass>, ListCell<JEVisClass>>() {
            @Override
            public ListCell<JEVisClass> call(ListView<JEVisClass> param) {
                final ListCell<JEVisClass> cell = new ListCell<JEVisClass>() {
                    {
                        super.setPrefWidth(260);
                    }

                    @Override
                    public void updateItem(JEVisClass item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            HBox box = new HBox(5);
                            box.setAlignment(Pos.CENTER_LEFT);
                            try {
                                ImageView icon = ImageConverter.convertToImageView(item.getIcon(), 15, 15);
                                Label cName = new Label(item.getName());
                                cName.setTextFill(Color.BLACK);
                                box.getChildren().setAll(icon, cName);

                            } catch (JEVisException ex) {
                                Logger.getLogger(NewFormDialog.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            setGraphic(box);

                        }
                    }
                };
                return cell;
            }
        };

        final ComboBox<JEVisClass> comboBox = new ComboBox<JEVisClass>(options);
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        final ComboBox<JEVisClass> comboBox2 = new ComboBox<JEVisClass>(options);
        comboBox2.setCellFactory(cellFactory);
        comboBox2.setButtonCell(cellFactory.call(null));

        listComboBox.add(comboBox);
        listComboBox.add(comboBox2);

        if (jclass != null) {
            comboBox.getSelectionModel().select(jclass);
        }

        comboBox.setMinWidth(250);
        comboBox.setMaxWidth(Integer.MAX_VALUE);//workaround

        if (fixClass) {
            comboBox.setDisable(true);
        }

        HBox hbox = new HBox();
        VBox vBoxlistLabelName = new VBox();
        VBox vBoxlistTextFieldName = new VBox();
        VBox vBoxlistLabelClass = new VBox();
        VBox vBoxlistComboBox = new VBox();

        vBoxlistLabelName.getChildren().addAll(listLabelName);
        vBoxlistTextFieldName.getChildren().addAll(listTextFieldName);
        vBoxlistLabelClass.getChildren().addAll(listLabelClass);
        vBoxlistComboBox.getChildren().addAll(listComboBox);

        hbox.getChildren().addAll(vBoxlistLabelName, vBoxlistTextFieldName, vBoxlistLabelClass, vBoxlistComboBox);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setMinHeight(10);

        root.getChildren().addAll(header, new Separator(Orientation.HORIZONTAL), hbox, buttonPanel);
        VBox.setVgrow(hbox, Priority.ALWAYS);//
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(header, Priority.NEVER);

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();

                for (int i = 0; i < listTextFieldName.size(); i++) {
                    listCreateName.add(listTextFieldName.get(i).getText());
                    listCreateClass.add(listComboBox.get(i).getSelectionModel().getSelectedItem());
                }

                response = Response.YES;
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

        fName.setOnKeyReleased(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent t) {
                if (!fName.getText().equals("")) {
                    ok.setDisable(false);
                }
            }
        });

        fName.setDisable(true);
        comboBox.setDisable(true);
        ok.setDisable(true);

        try {
            if (RelationsManagment.canWrite(parent.getDataSource().getCurrentUser(), parent)) {
                fName.setDisable(false);
                comboBox.setDisable(false);
                ok.setDisable(false);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(NewFormDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (type == Type.NEW) {
            stage.setTitle("New Form");
            topTitle.setText("New Form");
            comboBox.getSelectionModel().selectFirst();
        } else if (type == Type.RENAME) {
            stage.setTitle("Rename Form");
            topTitle.setText("Rename Form");
            comboBox.getSelectionModel().select(jclass);
        }

        stage.showAndWait();
        return response;
    }
}
