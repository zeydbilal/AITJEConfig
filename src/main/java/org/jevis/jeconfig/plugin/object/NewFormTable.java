package org.jevis.jeconfig.plugin.object;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import javafx.util.Callback;
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

    private ObservableList<ComboBox> optionsList = FXCollections.observableArrayList();

    public NewFormTable() {

    }

    public void initGUI(final JEVisObject parent) throws Exception {
        ObservableList<JEVisClass> options = FXCollections.observableArrayList(parent.getAllowedChildrenClasses());
        
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
        
        for (int i = 0; i < 10; i++) {
            listAttribute.add("");
        }

        int rowCount = 1000;
        int columnCount = listAttribute.size();
        grid = new GridBase(rowCount, columnCount);

        for (int row = 0; row < grid.getRowCount(); ++row) {
            cells = FXCollections.observableArrayList();
            for (int column = 0; column < grid.getColumnCount(); ++column) {
                if (column == 1) {
                    SpreadsheetCell cellIndex = SpreadsheetCellType.STRING.createCell(row, column, 1, 1, null);
                    ComboBox<JEVisClass> comboBox = new ComboBox(options);
                    comboBox.setMinWidth(250);
                    comboBox.setCellFactory(cellFactory);
                    comboBox.setButtonCell(cellFactory.call(null));
                    
                    cellIndex.setGraphic(comboBox);
                    cellIndex.setEditable(false);
                    optionsList.add(comboBox);
                    cells.add(cellIndex);

                } else {
                    cells.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1, ""));
                }
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
        spv.getSelectionModel().setCellSelectionEnabled(true);
        spv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        BorderPane root = new BorderPane();
        root.setTop(new Button("Create Structure"));
        root.setCenter(spv);

        Scene scene = new Scene(root);

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY), new Runnable() {

            @Override
            public void run() {
                try {
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
                            SpreadsheetCell spc = rows.get(currentRow).get(currentColumn);
                            grid.setCellValue(currentRow, currentColumn, spc.getCellType().convertValue(word));
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

        stage.setTitle("New Form");
        stage.setScene(scene);
        stage.show();
    }
}
