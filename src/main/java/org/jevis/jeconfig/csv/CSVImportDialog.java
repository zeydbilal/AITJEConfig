/**
 * Copyright (C) 2014-2015 Envidatec GmbH <info@envidatec.com>
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
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.csv;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisDataSource;
import org.jevis.application.dialog.InfoDialog;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.commons.parsing.DataCollectorParser;
import org.jevis.commons.parsing.csvParsing.CSVParsing;
import org.jevis.commons.parsing.inputHandler.FileInputHandler;
import org.jevis.commons.parsing.inputHandler.InputHandler;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.NumberSpinner;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class CSVImportDialog {

    public static String ICON = "1403727005_gnome-mime-application-vnd.lotus-1-2-3.png";

    private String _encloser = "";
    private String _seperator = "";

    private double LEFT_PADDING = 30;

    final Button ok = new Button("Import");
    final Button automatic = new Button("Detect");//, JEConfig.getImage("1403018303_Refresh.png", 15, 15));
    final Button fileButton = new Button("Choose..");
    final Button saveFormat = new Button("Save Format");
    Button reload = new Button("Preview");//, JEConfig.getImage("1403018303_Refresh.png", 20, 20));
    final NumberSpinner count = new NumberSpinner(BigDecimal.valueOf(1), BigDecimal.valueOf(1));

    RadioButton tab = new RadioButton("Tab (/t)");
    RadioButton semicolon = new RadioButton("Semicolon (;)");
    RadioButton comma = new RadioButton("Comma (,)");
    RadioButton space = new RadioButton("Space ( )");
    RadioButton otherLineSep = new RadioButton("Other:");
    final ToggleGroup sepGroup = new ToggleGroup();

    RadioButton apostrop = new RadioButton("Apostrophe ('')");
    RadioButton ditto = new RadioButton("Ditto mark (\"\")");
    RadioButton enc2 = new RadioButton("``");
    RadioButton none = new RadioButton("none");
    RadioButton otherTextSep = new RadioButton("Other:");

    final ToggleGroup textDiGroup = new ToggleGroup();
    ObservableList<String> formatOptions;

    final VBox tableRootPane = new VBox(10);

    TextField otherColumnF = new TextField();
    TextField otherTextF = new TextField();
    public DataCollectorParser _fileParser;
    private File _csvFile;
    private JEVisDataSource _ds;
    private CSVTable tree;

    public static enum Format {

        Default, ARA01, Custom
    };

    public static enum Response {

        OK, CANCEL
    };

    private Response response = Response.CANCEL;

    public Response show(Stage owner, JEVisDataSource ds) {
        final Stage stage = new Stage();
        _ds = ds;

        stage.setTitle("CSV Import");
        stage.initModality(Modality.NONE);
        stage.initOwner(owner);

//        BorderPane root = new BorderPane();
        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);
        scene.setCursor(Cursor.DEFAULT);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: linear-gradient(#e2e2e2,#eeeeee);");
        header.setPadding(new Insets(10, 10, 10, 10));

        Label topTitle = new Label("CSV Import");
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

        HBox buttonPanel = new HBox(8);

        ok.setDefaultButton(true);
//        ok.setDisable(true);

        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);

        buttonPanel.getChildren().setAll(saveFormat, ok, cancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);
        buttonPanel.setPadding(new Insets(5));

        GridPane gp = new GridPane();
//        gp.setPadding(new Insets(10));
//        gp.setHgap(10);
//        gp.setVgap(5);

        //check allowed
        int x = 0;

        Node filePane = buildFileOptions();
        Node seperatorPane = buildSeperatorPane();
        Node tablePane = buildTabelPane();

        gp.add(filePane, 0, 0);
        gp.add(seperatorPane, 0, 1);
        gp.add(tablePane, 0, 2);

        GridPane.setVgrow(filePane, Priority.NEVER);
        GridPane.setVgrow(seperatorPane, Priority.NEVER);
        GridPane.setVgrow(tablePane, Priority.ALWAYS);

        VBox content = new VBox(10);

        content.getChildren().setAll(
                buildTitle("File Options"), filePane,
                buildTitle("Separator Options"), seperatorPane,
                buildTitle("Field Options"), tablePane);

        Separator sep = new Separator(Orientation.HORIZONTAL);
        sep.setMinHeight(10);
        Region spacer = new Region();

        root.getChildren().addAll(header, new Separator(Orientation.HORIZONTAL), content, spacer, buttonPanel);
        VBox.setVgrow(gp, Priority.ALWAYS);
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        VBox.setVgrow(header, Priority.NEVER);

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;
            }
        });

        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("size: h:" + stage.getHeight() + " w:" + stage.getWidth());
                if (tree.doImport()) {
                    InfoDialog dia = new InfoDialog();
                    dia.show(stage, "Success", "Import was successful", "Import was successful.");
                }
//                stage.hide();
            }
        });

        stage.showAndWait();

        return response;
    }

    private void updateTree() {
//        System.out.println("UpdateTree");
        final CSVParser parser = parseCSV();
        tree = new CSVTable(_ds, parser);
        tableRootPane.getChildren().setAll(tree);
        tableRootPane.heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                if (tree != null) {
                    tree.setPrefHeight(t1.doubleValue());
                }
            }
        });
        VBox.setVgrow(tree, Priority.ALWAYS);
//        tree.setVisible(false);
//        tree.setScrollBottom();
//        tree.setVisible(true);
//
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                tree.setVisible(false);
//                tree.setScrollBottom();
//                tree.setVisible(true);
//                tree.setVisible(false);
//                tree.setScrollTop();
//                tree.setVisible(true);
//            }
//        });
    }

    private Node buildTabelPane() {

        tableRootPane.setPadding(new Insets(10, 10, 5, LEFT_PADDING));

        TableView placeholderTree = new TableView();
        TableColumn firstNameCol = new TableColumn("First Column");
        TableColumn lastNameCol = new TableColumn("Secound Column");
        firstNameCol.prefWidthProperty().bind(placeholderTree.widthProperty().multiply(0.5));
        lastNameCol.prefWidthProperty().bind(placeholderTree.widthProperty().multiply(0.5));
        placeholderTree.getColumns().addAll(firstNameCol, lastNameCol);

//        placeholderTree.setItems(FXCollections.observableArrayList());
//        reload.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent t) {
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        reloadTree();
//                    }
//                });
//
//            }
//        });
        tableRootPane.getChildren().setAll(placeholderTree);

        return tableRootPane;
    }

    private void setMeaning() {

    }

    private CSVParser parseCSV() {
//        System.out.println("_encloser: " + getEncloser());
//        System.out.println("_seperator: " + getSeperator());
        CSVParser parser = new CSVParser(_csvFile, getEncloser(), getSeperator(), getStartLine());
//        CSVParser parser = new CSVParser(_csvFile, _encloser, _seperator, getStartLine());
        return parser;
    }

    private Node buildSeperatorPane() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(5, 10, 5, LEFT_PADDING));

        gp.setHgap(10);
        gp.setVgap(5);

//        gp.setStyle("-fx-background-color: #86D64D;");
        Label sepL = new Label("Column separated by:");
        Label sepTextL = new Label("Text seperated by:");

        tab.setToggleGroup(sepGroup);
        semicolon.setToggleGroup(sepGroup);
        comma.setToggleGroup(sepGroup);
        space.setToggleGroup(sepGroup);
        otherLineSep.setToggleGroup(sepGroup);
//        sepGroup.selectToggle(semicolon);

        otherColumnF.setMinHeight(22);

        none.setToggleGroup(textDiGroup);
        ditto.setToggleGroup(textDiGroup);
        apostrop.setToggleGroup(textDiGroup);
        enc2.setToggleGroup(textDiGroup);
        otherTextSep.setToggleGroup(textDiGroup);
//        textDiGroup.selectToggle(none);

        setEncloser("");
        setSeperator(";");

        textDiGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                updateEnclosed();
            }
        });

        sepGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
                updateSeperator();
            }
        });

        otherTextF.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
//                sepGroup.selectToggle(otherLineSep);
                setEncloser(otherColumnF.getText());
//                _seperator = ;
//                reloadTree();
            }
        });

        otherColumnF.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
//                sepGroup.selectToggle(otherLineSep);
//                _seperator = otherColumnF.getText();
                if (!otherColumnF.getText().isEmpty()) {
                    setSeperator(otherColumnF.getText());
                }
//                reloadTree();
            }
        });

        ObservableList<String> options = FXCollections.observableArrayList();
        options = FXCollections.observableArrayList("Semicolon (;)", "Tabulator (TAB) ", "Comma (,)", "Space ( )", "OtherBox");
        final ComboBox<String> chaset = new ComboBox<String>(options);
        chaset.getSelectionModel().selectFirst();

        HBox otherBox = new HBox(5);
        otherBox.setAlignment(Pos.CENTER_LEFT);
        otherBox.getChildren().setAll(otherLineSep, otherColumnF);

        HBox otherTextBox = new HBox(5);
        otherTextBox.setAlignment(Pos.CENTER_LEFT);
        otherTextBox.getChildren().setAll(otherTextSep, otherTextF);

        Node title = buildTitle("Seperator options");

        HBox root = new HBox();

        VBox columnB = new VBox(5);
        VBox textB = new VBox(5);

        root.setPadding(new Insets(5, 10, 5, LEFT_PADDING));
//        FlowPane cSep = new FlowPane(Orientation.VERTICAL, 10, 5);
//        FlowPane tSep = new FlowPane(Orientation.VERTICAL, 10, 5);
        VBox cSep = new VBox(5);
        VBox tSep = new VBox(5);
        cSep.setPadding(new Insets(0, 0, 0, 20));
        tSep.setPadding(new Insets(0, 0, 0, 20));

        cSep.getChildren().setAll(semicolon, tab, comma, space, otherBox);
        tSep.getChildren().setAll(none, apostrop, ditto, enc2, otherTextBox);

        columnB.getChildren().setAll(sepL, cSep);
        textB.getChildren().setAll(sepTextL, tSep);

        Region spacer = new Region();
//        spacer.setStyle("-fx-background-color: red;");

        root.getChildren().setAll(columnB, textB, spacer);
        root.setAlignment(Pos.TOP_LEFT);
//        root.setPadding(new Insets(0, 0, 0, 10));
        HBox.setHgrow(columnB, Priority.NEVER);
        HBox.setHgrow(textB, Priority.NEVER);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        return root;

    }

    private Node buildTitle(String name) {
        HBox titelBox = new HBox(2);
        titelBox.setPadding(new Insets(8));
        Separator titelSep = new Separator(Orientation.HORIZONTAL);
        titelSep.setMaxWidth(Double.MAX_VALUE);
        Label title = new Label(name);
//        titelBox.getChildren().setAll(titelSep);
        titelBox.getChildren().setAll(title, titelSep);
        HBox.setHgrow(titelSep, Priority.NEVER);
        HBox.setHgrow(titelSep, Priority.ALWAYS);
        titelBox.setAlignment(Pos.CENTER_LEFT);
        titelBox.setPrefWidth(1024);

        return titelBox;
    }

    private void parseFile() {
    }

    private int getStartLine() {
        return count.getNumber().intValue();
    }

    private String getEncloser() {
        return _encloser;

    }

    private void updateEnclosed() {
        RadioButton selecedt = (RadioButton) textDiGroup.getSelectedToggle();
        if (selecedt.equals(none)) {
            _encloser = "";
        } else if (selecedt.equals(ditto)) {
            _encloser = "\"";
        } else if (selecedt.equals(apostrop)) {
            _encloser = "'";
        } else if (selecedt.equals(enc2)) {
            _encloser = "`";
        } else if (selecedt.equals(ditto)) {
            _encloser = "\"";
        } else if (selecedt.equals(otherTextSep)) {
            _encloser = otherTextF.getText();
        }
        updateTree();

    }

    private void setEncloser(String endclosed) {
        _encloser = endclosed;
        RadioButton toSelect = none;

        switch (endclosed) {
            case "\"":
                toSelect = ditto;
                break;
            case "'":
                toSelect = apostrop;
                break;
            case "`":
                toSelect = enc2;
                break;
            case "":
                toSelect = none;
                break;
            default:
                toSelect = otherTextSep;
                otherTextSep.setText(endclosed);
                break;
        }

        if (textDiGroup.getSelectedToggle() != toSelect) {
            textDiGroup.selectToggle(toSelect);
        }
    }

    private String getSeperator() {
        return _seperator;
    }

    private void updateSeperator() {
        if (sepGroup.getSelectedToggle() != null) {
            RadioButton selecedt = (RadioButton) sepGroup.getSelectedToggle();
            if (selecedt.equals(semicolon)) {
                _seperator = ";";
            } else if (selecedt.equals(comma)) {
                _seperator = ",";
            } else if (selecedt.equals(space)) {
                _seperator = " ";
            } else if (selecedt.equals(tab)) {
                _seperator = "\t";
            } else if (selecedt.equals(otherLineSep)) {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        otherColumnF.requestFocus();
                    }
                });
                if (!otherColumnF.getText().isEmpty()) {
                    _seperator = otherColumnF.getText();
                } else {
                    return;
                }

            }
            System.out.println("reloadtree");
            updateTree();
        }

    }

    private void setSeperator(String sep) {
        _seperator = sep;
        RadioButton toSelect = semicolon;

        switch (sep) {
            case ";":
                toSelect = semicolon;
                break;
            case ",":
                toSelect = comma;
                break;
            case " ":
                toSelect = space;
                break;
            case "\t":
                toSelect = tab;
                break;
            default:
                toSelect = otherLineSep;
                otherColumnF.setText(sep);
                break;
        }

        if (sepGroup.getSelectedToggle() != toSelect) {
            sepGroup.selectToggle(toSelect);
        }

    }

    private Node buildFileOptions() {
        GridPane gp = new GridPane();
        gp.setPadding(new Insets(0, 10, 10, LEFT_PADDING));
//        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(5);

//        gp.setStyle("-fx-background-color: #EBED50;");
        Label fileL = new Label("File:");
        Label formatL = new Label("Format:");
        Label charSetL = new Label("Character set:");
        Label fromRow = new Label("From row:");
        final Label fileNameL = new Label();

        ObservableList<String> options = FXCollections.observableArrayList();
        options = FXCollections.observableArrayList("UTF-8");
        final ComboBox<String> chaset = new ComboBox<String>(options);
        chaset.getSelectionModel().selectFirst();

        formatOptions = FXCollections.observableArrayList();
        for (Format format : Format.values()) {
            formatOptions.add(format.name());
        }

//        formatOptions = FXCollections.observableArrayList("MS Office, ARA01, Custom");
        final ComboBox<String> formats = new ComboBox<String>(formatOptions);
        formats.getSelectionModel().selectFirst();

        Node title = buildTitle("File Options");

        fileButton.setPrefWidth(100);
        chaset.setPrefWidth(100);
        formats.setPrefWidth(100);
        chaset.setMaxWidth(1000);
        formats.setMaxWidth(1000);

        count.setMinHeight(22);
        count.numberProperty().addListener(new ChangeListener<BigDecimal>() {

            @Override
            public void changed(ObservableValue<? extends BigDecimal> ov, BigDecimal t, BigDecimal t1) {
                System.out.println("Spinner changed");
                updateTree();
            }
        });

        automatic.setDisable(true);
        automatic.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                System.out.println("autdidect");
                if (_csvFile != null) {
                    try {
                        CSVAnalyser analys = new CSVAnalyser(_csvFile);
                        System.out.println("Enclosed: " + analys.getEnclosed());
                        System.out.println("Seperator: " + analys.getSeperator());

                        setEncloser(analys.getEnclosed());
                        setSeperator(analys.getSeperator());
                        formats.getSelectionModel().select(Format.Custom.name());
                        updateTree();

                    } catch (Exception ex) {
                        System.out.println("Error while anylysing csv: " + ex);
                    }
                }

            }
        });

        fileButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                FileChooser fileChooser = new FileChooser();
                if (JEConfig.getLastPath() != null) {
//                    System.out.println("Last Path: " + JEConfig.getLastPath().getParentFile());
                    File file = JEConfig.getLastPath();
                    if (file.exists() && file.canRead()) {
                        fileChooser.setInitialDirectory(file);
                    }
                }

                FileChooser.ExtensionFilter csvFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
                FileChooser.ExtensionFilter allFilter = new FileChooser.ExtensionFilter("All files ", "*");
                fileChooser.getExtensionFilters().addAll(csvFilter, allFilter);

                final File file = fileChooser.showOpenDialog(JEConfig.getStage());
                if (file != null) {
                    JEConfig.setLastPath(file);
//                    System.out.println("file: " + file);
                    try {
                        //                    fileButton.setText(file.getName());
                        fileNameL.setText(file.getCanonicalPath());// + System.getProperty("file.separator") + file.getName());
                    } catch (IOException ex) {
                        Logger.getLogger(CSVImportDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    openFile(file);
                    automatic.setDisable(false);
                    updateTree();

//                    System.out.println("size.dfs: " + otherColumnF.getHeight());
                }
            }
        });

        int x = 0;

//        gp.add(title, 0, x, 3, 1);
        gp.add(fileL, 0, ++x);
        gp.add(fileButton, 1, x);
        gp.add(fileNameL, 2, x);

        gp.add(formatL, 0, ++x);
        gp.add(formats, 1, x);
        gp.add(automatic, 2, x);

        gp.add(charSetL, 0, ++x);
        gp.add(chaset, 1, x);

        gp.add(fromRow, 0, ++x);
        gp.add(count, 1, x);

        GridPane.setHgrow(title, Priority.ALWAYS);

        return gp;

    }

    private void openFile(File file) {
        _csvFile = file;
        InputHandler inputHandler = new FileInputHandler(file);
        inputHandler.convertInput();

        _fileParser = new CSVParsing();
    }

    public class CSVTabel extends TreeView<CSVCell> {

    }

}
