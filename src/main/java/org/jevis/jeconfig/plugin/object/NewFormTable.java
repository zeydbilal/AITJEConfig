package org.jevis.jeconfig.plugin.object;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Pair;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Bilal
 */
//TODO
public class NewFormTable {

    private final ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
    private ObservableList<SpreadsheetCell> cells;
    private ObservableList<String> listAttribute = FXCollections.observableArrayList();
    private SpreadsheetView spv;
    private GridBase grid;
    private Stage stage = new Stage();
    private JEVisClass createClass;
    private LinkedList<String> listObjectNames = new LinkedList<>();
    private int rowCount;
    private int columnCount;
    private CreateNewTable createNewTable;
    private CreateNewDataTable createNewDataTable;
    private ObservableList<String> columnHeaderNames = FXCollections.observableArrayList();
    private ObservableList<String> columnHeaderNamesDataTable = FXCollections.observableArrayList();
    private ObservableList<Pair<String, ArrayList<String>>> pairList = FXCollections.observableArrayList();

    class CreateNewTable {

        public CreateNewTable() {
            try {
                rowCount = 1000;
                columnCount = createClass.getTypes().size() + 1;
            } catch (JEVisException ex) {
                Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
            }

            grid = new GridBase(rowCount, columnCount);

            for (int row = 0; row < grid.getRowCount(); ++row) {
                cells = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }

                rows.add(cells);
            }
            grid.setRows(rows);
            spv = new SpreadsheetView();
            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            columnHeaderNames.add("Object Name");
            try {
                //Get and set Typenames :)
                for (int i = 0; i < createClass.getTypes().size(); i++) {
                    columnHeaderNames.add(createClass.getTypes().get(i).getName());
                }

            } catch (JEVisException ex) {
                Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
            }
            spv.getGrid().getColumnHeaders().addAll(columnHeaderNames);
        }
    }

    public static enum Type {

        NEW, RENAME
    };

    public static enum Response {

        NO, YES, CANCEL
    };

    private Response response = Response.CANCEL;

    public NewFormTable() {
    }

    public Response show(Stage owner, final JEVisClass jclass, final JEVisObject parent, boolean fixClass, Type type, String objName) {
        ObservableList<JEVisClass> options = FXCollections.observableArrayList();
        try {
            if (type == Type.NEW) {
                options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
            }
        } catch (JEVisException ex) {
            Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
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

        ComboBox<JEVisClass> classComboBox = new ComboBox<JEVisClass>(options);
        classComboBox.setCellFactory(cellFactory);
        classComboBox.setButtonCell(cellFactory.call(null));
        classComboBox.setMinWidth(250);
        classComboBox.getSelectionModel().selectFirst();
        createClass = classComboBox.getSelectionModel().getSelectedItem();

        try {
            if (createClass.getName().equals("Data")) {
                createNewDataTable = new CreateNewDataTable();
            } else {
                createNewTable = new CreateNewTable();
            }
        } catch (JEVisException ex) {
            Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
        }

        BorderPane root = new BorderPane();

        Button create = new Button("Create Structure");

        Button cancel = new Button("Cancel");
        HBox hbox = new HBox();
        hbox.getChildren().addAll(classComboBox, create, cancel);
        root.setTop(hbox);
        //remove it
        root.setCenter(spv);
        Scene scene = new Scene(root);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), new Runnable() {

            @Override
            public void run() {
                try {
                    //FIXME
                    Clipboard clipboard = Clipboard.getSystemClipboard();

                    if (clipboard.hasString()) {

                        String[] words = clipboard.getString().split("\n");

                        ObservableList<TablePosition> focusedCell = spv.getSelectionModel().getSelectedCells();

                        int currentRow = 0;
                        int currentColumn = 0;

                        for (final TablePosition<?, ?> p : focusedCell) {
                            currentRow = p.getRow();
                            currentColumn = p.getColumn();
                        }

                        for (String word : words) {
                            String[] parseWord = word.split("\t");
                            int col = currentColumn;
                            for (int i = 0; i < parseWord.length; i++) {
                                SpreadsheetCell spc = rows.get(currentRow).get(col);
                                grid.setCellValue(currentRow, col, spc.getCellType().convertValue(parseWord[i]));
                                col++;
                            }
                            currentRow++;
                        }

                    } else {
                        spv.pasteClipboard();
                    }
                } catch (NullPointerException e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        create.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                for (int i = 0; i < grid.getRowCount(); i++) {
                    SpreadsheetCell spcObjectName = rows.get(i).get(0);
                    if (!spcObjectName.getText().equals("")) {
                        
                        ArrayList<String> attributs = new ArrayList<>();
                        for (int j = 1; j < grid.getColumnCount(); j++) {
                            SpreadsheetCell spcAttribut = rows.get(i).get(j);
                            attributs.add(spcAttribut.getText());
                        }
                        pairList.add(new Pair(spcObjectName.getText(), attributs));
                    }
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

        classComboBox.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    rows.clear();
                    columnHeaderNames.clear();
                    columnHeaderNamesDataTable.clear();
                    createClass = classComboBox.getSelectionModel().getSelectedItem();

                    if (createClass.getName().equals("Data")) {
                        createNewDataTable = new CreateNewDataTable();
                        root.setCenter(spv);
                    } else {
                        createNewTable = new CreateNewTable();
                        root.setCenter(spv);
                    }
                } catch (JEVisException ex) {
                    Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });

        stage.setTitle("New Form");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(true);
        stage.showAndWait();

        return response;
    }

    public LinkedList<String> getlistObjectNames() {
        return listObjectNames;
    }

    public ObservableList<Pair<String, ArrayList<String>>> getPairList() {
        return pairList;
    }

    public JEVisClass getCreateClass() {
        return createClass;
    }

    public ObservableList<String> getColumnHeaderNames() {
        return columnHeaderNames;
    }

    class CreateNewDataTable {

        public CreateNewDataTable() {

            rowCount = 1000;
            columnCount = 7;

            grid = new GridBase(rowCount, columnCount);

            for (int row = 0; row < grid.getRowCount(); ++row) {
                cells = FXCollections.observableArrayList();
                for (int column = 0; column < grid.getColumnCount(); ++column) {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }

                rows.add(cells);
            }
            grid.setRows(rows);
            spv = new SpreadsheetView();
            spv.setGrid(grid);

            ObservableList<SpreadsheetColumn> colList = spv.getColumns();

            for (SpreadsheetColumn colListElement : colList) {
                colListElement.setPrefWidth(150);
            }

            spv.setEditable(true);
            spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            String[] colNames = {"Object Name", "Display Symbol", "Display Prefix", "Display Sample Rate", "Input Symbol", "Input Prefix", "Input Sample Rate"};
            columnHeaderNamesDataTable.addAll(colNames);

            spv.getGrid().getColumnHeaders().addAll(columnHeaderNamesDataTable);
        }
    }
}
