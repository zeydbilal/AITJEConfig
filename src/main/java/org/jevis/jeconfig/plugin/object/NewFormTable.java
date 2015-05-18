package org.jevis.jeconfig.plugin.object;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TablePosition;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetColumn;
import org.controlsfx.control.spreadsheet.SpreadsheetView;


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
    private DataFormat fmt = new DataFormat("Text");
    private Stage stage = new Stage();

    public NewFormTable(){
        try {
            initGUI();
        } catch (Exception ex) {
            Logger.getLogger(NewFormTable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initGUI() throws Exception {
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
                    cellIndex.setGraphic(new ComboBox());
                    cellIndex.setEditable(false);

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
        
        stage.setTitle("JavaFX and Maven");
        stage.setScene(scene);
        stage.show();
    }
}
