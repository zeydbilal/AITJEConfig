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
 *
 * JEConfig is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.csv;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import static javafx.scene.input.DataFormat.URL;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;
import javax.measure.unit.UnitFormat;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisUnit;
import org.jevis.application.dialog.SelectTargetDialog;
import org.jevis.application.object.tree.UserSelection;
import org.jevis.application.resource.ResourceLoader;
import org.jevis.application.unit.UnitChooserDialog;
import org.jevis.jeconfig.JEConfig;
import org.joda.time.DateTime;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class CSVColumnHeader {

    private final VBox root = new VBox(5);

    Label typeL = new Label("Meaning:");
    Label formateL = new Label("Formate:");
    private JEVisAttribute _target = null;
    private ComboBox<String> meaning;
    private HashMap<Integer, CSVLine> _lines = new HashMap<Integer, CSVLine>();
    private HashMap<Integer, SimpleObjectProperty<Node>> _valuePropertys = new HashMap<Integer, SimpleObjectProperty<Node>>();
    private HashMap<Integer, CSVCellGraphic> _valueGraphic = new HashMap<Integer, CSVCellGraphic>();

    private TimeZone _selectedTimeZone = TimeZone.getDefault();
    DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();

    private static double FIELD_WIDTH = 210;
    private static double ROW_HIGHT = 25;

    final Button unitButton = new Button("Choose Unit..");
    private CSVTable _table;
    private String _currentFormate;

    private String _groupingSeparator;
    private char _decimalSeparator;

    private SimpleDateFormat _dateFormater = new SimpleDateFormat();

    public static enum Meaning {

        Ignore, Date, DateTime, Time, Value, Text, Index
    };

    public static enum DateTimeMode {

        Date, DateTime, Time
    };
    private Meaning currentMeaning = Meaning.Ignore;
    private int coloumNr = -1;

    public CSVColumnHeader(CSVTable table, int column) {
        coloumNr = column;
        _table = table;

        root.setPrefHeight(110);

        buildMeaningButton();
        buildIgnoreGraphic();
    }

    public int getColumn() {
        return coloumNr;
    }

    public JEVisAttribute getTarget() {
        return _target;
    }

    private String getCurrentFormate() {
        return _currentFormate;
    }

    private String getGroupingSeparator() {
        return _groupingSeparator;
    }

    private char getDecimalSeparator() {
        return _decimalSeparator;
    }

    public SimpleObjectProperty getValueProperty(CSVLine line) {
        int lineNumber = line.getRowNumber();
        if (_valuePropertys.containsKey(lineNumber)) {
            return _valuePropertys.get(lineNumber);
        } else {
            _lines.put(lineNumber, line);

            CSVCellGraphic graphic = new CSVCellGraphic(line.getColumn(coloumNr));
            _valueGraphic.put(lineNumber, graphic);
            graphic.setText(getFormatedValue(line.getColumn(coloumNr)));
            graphic.setValid(valueIsValid(line.getColumn(coloumNr)));
            graphic.setToolTipText(line.getColumn(coloumNr));

            if (getMeaning() == Meaning.Ignore) {
                graphic.setIgnore();
                graphic.getGraphic().setDisable(true);
            }

            _valuePropertys.put(lineNumber, new SimpleObjectProperty<>(graphic.getGraphic()));
            return _valuePropertys.get(lineNumber);
        }
    }

    public String getFormatedValue(String value) {
//        System.out.println("get formatedt value: " + value);
        try {

            switch (currentMeaning) {
                case Date:
                    Date date = getDateFormater().parse(value);
                    return getDateFormater().format(date);
                case DateTime:
                    Date datetime = getDateFormater().parse(value);
                    return getDateFormater().format(datetime);
                case Time:
                    Date time = getDateFormater().parse(value);
                    return getDateFormater().format(time);
                case Value:
                    //hmm lokks some kinde if strage i bet there is a better ways
                    DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,###,###,###,##0.00###################################");
//                    String unit = "";
//
                    if (getTarget() != null && getTarget().getInputUnit() != null) {
                        JEVisUnit unit = getTarget().getInputUnit();
                        System.out.println("Value with unit: " + df.format(getValueAsDouble(value)) + unit.getLabel());
                        return df.format(getValueAsDouble(value)) + unit.getLabel();
                    }
//                    System.out.println("unit.formate: " + unit);

//                    return df.format(getValueAsDouble(value)) + unit;
                    return getValueAsDouble(value) + "";
                case Index:
                    break;
                case Ignore:
                    return value;
//                    System.out.println("To Ignore");
            }
        } catch (Exception pe) {
            return value;
        }
        return value;
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public String getTextValue(String value) throws ParseException {
        //TODO: mybee
        if (getMeaning() == Meaning.Value) {
            return value;
        } else {
            throw new ParseException(value, coloumNr);
        }
    }

    public double getValueAsDouble(String value) throws ParseException {
//        DecimalFormat df = new DecimalFormat("#.#", symbols);
//        System.out.println("org value: " + value);
//        System.out.println("Seperator in use: " + symbols.getDecimalSeparator());
//        String tmpValue = value;
//
//        if (getDecimalSeparator() == ',') {
//            tmpValue = tmpValue.replace('.', ' ');//removeall grouping chars
//        } else {
//            tmpValue = tmpValue.replace(',', ' ');//removeall grouping chars
//        }
//        tmpValue = tmpValue.replaceAll(" ", "");
//        tmpValue = tmpValue.trim();//some locales use the spaceas grouping
//        System.out.println("Value after fix: " + tmpValue);
//
//        Number number = df.parse(tmpValue);

        String tmpValue = value;
        if (getDecimalSeparator() == ',') {
            tmpValue = tmpValue.replace('.', ' ');//removeall grouping chars
            tmpValue = tmpValue.replaceAll(",", ".");
        } else {
            tmpValue = tmpValue.replace(',', ' ');//removeall grouping chars
        }
        tmpValue = tmpValue.replaceAll(" ", "");

        Double number = Double.valueOf(tmpValue);

        return number;
    }

    /**
     *
     * @param value
     * @return
     * @throws ParseException
     */
    public DateTime getValueAsDate(String value) throws ParseException {
        System.out.println("getValueAsDate: " + value);
        if (getMeaning() == Meaning.Date || getMeaning() == Meaning.DateTime || getMeaning() == Meaning.Time) {
            Date datetime = getDateFormater().parse(value);
            datetime.getTime();
            return new DateTime(datetime);

//            DateTimeZone.setDefault(DateTimeZone.forTimeZone(getTimeZone()));
//            return DateTimeFormat.forPattern(getCurrentFormate()).parseDateTime(value);
        } else {
            throw new ParseException(value, coloumNr);

        }
    }

    public SimpleDateFormat getDateFormater() {
        return _dateFormater;
    }

    /**
     * TODO replace checks with the later uses functions like getValueAsDate
     *
     * @param value
     * @return
     */
    public boolean valueIsValid(String value) {
        try {

            switch (currentMeaning) {
                case Date:
                    Date date = getDateFormater().parse(value);
                    date.getTime();
                    return true;
                case DateTime:
                    Date datetime = getDateFormater().parse(value);
                    datetime.getTime();
                    return true;
                case Time:
                    Date time = getDateFormater().parse(value);
                    time.getTime();
                    return true;
                case Value:

                    getValueAsDouble(value);
                    return true;

//                    symbols.setDecimalSeparator(getDecimalSeparator());
//                    DecimalFormat df = new DecimalFormat("#,#", symbols);
//                    String tmpValue = value;
//
//                    if (getDecimalSeparator() == ',') {
//                        tmpValue = tmpValue.replace('.', ' ');//removeall grouping chars
//                    } else {
//                        tmpValue = tmpValue.replace(',', ' ');//removeall grouping chars
//                    }
//                    tmpValue = tmpValue.trim();//some locales use the spaceas grouping
//
//                    Number number = df.parse(tmpValue);
//                    Double dValue = number.doubleValue();
//
//                    System.out.println("Value is valid: " + dValue);
//                    return true;
                case Text:
                    //TODO maybe check for .... if the attriute is from type string
                    return true;
                case Index:
                    return true;
                case Ignore:
                    return true;
            }
        } catch (Exception pe) {
            return false;
        }
        return false;
    }

    public void formteAllRows() {
//        _table.setScrollBottom();
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                _table.setScrollBottom();
//            }
//        });

        Iterator it = _valuePropertys.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
//            System.out.println(pairs.getKey() + " = " + pairs.getValue());

            SimpleObjectProperty prop = (SimpleObjectProperty) pairs.getValue();
            CSVCellGraphic graphic = _valueGraphic.get((Integer) pairs.getKey());
            CSVLine csvLIne = _lines.get((Integer) pairs.getKey());

            graphic.setText(getFormatedValue(csvLIne.getColumn(coloumNr)));
            graphic.setValid(valueIsValid(csvLIne.getColumn(coloumNr)));
            graphic.setToolTipText("Original: '" + csvLIne.getColumn(coloumNr) + "'");

            if (getMeaning() == Meaning.Ignore) {
                graphic.setIgnore();
                graphic.getGraphic().setDisable(true);
            } else {
                graphic.getGraphic().setDisable(false);
            }

            prop.setValue(graphic.getGraphic());

        }

//        _table.setLastScrollPosition();
//        Platform.runLater(new Runnable() {
//            @Override
//            public void run() {
//                _table.setLastScrollPosition();
//            }
//        });
    }

    public TimeZone getTimeZone() {
        return _selectedTimeZone;
    }

    public Meaning getMeaning() {
        return currentMeaning;
    }

    private void setMeaning(Meaning meaning) {
        currentMeaning = meaning;

        switch (meaning) {
            case Date:
                buildDateTime(Meaning.Date);
                break;
            case DateTime:
                buildDateTime(Meaning.DateTime);
                break;
            case Time:
                buildDateTime(Meaning.Time);
                break;
            case Value:
                buildValueGraphic();
                break;
            case Text:
                buildTextGraphic();
                break;
            case Index:
                break;
            case Ignore:
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        buildIgnoreGraphic();
                    }
                });

        }

        formteAllRows();
    }

    private void buildMeaningButton() {
        ObservableList<String> options = FXCollections.observableArrayList();

        for (Meaning meaningEnum : Meaning.values()) {
            options.add(meaningEnum.name());
        }

        meaning = new ComboBox<String>(options);
        meaning.getSelectionModel().selectFirst();

        meaning.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {

                if (t1 != null) {
                    setMeaning(Meaning.valueOf(t1));
                }

            }
        });

        meaning.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
    }

    private void buildTextGraphic() {
        root.setPadding(new Insets(8, 8, 8, 8));

        Label targetL = new Label("Target:");
        Button targetB = buildTargetButton();

        Region spacer = new Region();

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        root.getChildren().setAll(gp);

        //x , y
        gp.add(typeL, 0, 0);
        gp.add(meaning, 1, 0);

        gp.add(spacer, 1, 1);

        gp.add(targetL, 0, 2);
        gp.add(targetB, 1, 2);

        spacer.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        meaning.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        targetB.setPrefSize(FIELD_WIDTH, ROW_HIGHT);

    }

    private void buildValueGraphic() {
        root.setPadding(new Insets(8, 8, 8, 8));

        ToggleGroup deciSepGroup = new ToggleGroup();
        Label deciSeperator = new Label("Decimal Separator:");
        final RadioButton comma = new RadioButton("Comma");
        comma.setId("commaRadio");
        final RadioButton dot = new RadioButton("Dot");
        dot.setId("dotRadio");
        Label targetL = new Label("Target:");
        Label unitLabel = new Label("Unit:");
        final Button unitButton = new Button("Choose Unit..");
//        unitButton.setDisable(true);
        unitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {

                try {
                    if (_target != null) {

                        UnitChooserDialog dia = new UnitChooserDialog();
                        dia.show(JEConfig.getStage(), _target);
                    } else {
                        //TODO reimplement unit
//                        Unit kwh = SI.KILO(SI.WATT.times(NonSI.HOUR));
//                        UnitChooserDialog dia = new UnitChooserDialog();
//                        dia.showSelector(JEConfig.getStage(), kwh, "");
                    }

                } catch (JEVisException ex) {
                    Logger.getLogger(CSVColumnHeader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        dot.setToggleGroup(deciSepGroup);
        comma.setToggleGroup(deciSepGroup);

        deciSepGroup.selectToggle(dot);
        _decimalSeparator = '.';
        symbols.setDecimalSeparator(_decimalSeparator);

        deciSepGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> ov, Toggle t, Toggle t1) {
//                System.out.println("Seperator changed: " + t1);
                if (t1.equals(comma)) {
//                    System.out.println("sep is now ,");
                    _decimalSeparator = ',';

                } else if (t1.equals(dot)) {
//                    System.out.println("sep is now .");
                    _decimalSeparator = '.';
                }
                symbols.setDecimalSeparator(_decimalSeparator);
                formteAllRows();

            }
        });

        HBox spebox = new HBox(10);
        spebox.setAlignment(Pos.CENTER_LEFT);

        spebox.getChildren().setAll(deciSeperator, dot, comma);

        Button targetB = buildTargetButton();

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        root.getChildren().setAll(gp);

        //x , y
        gp.add(typeL, 0, 0);
        gp.add(meaning, 1, 0);

//        gp.add(deciSeperator, 0, 1);
        gp.add(spebox, 0, 1, 2, 1);

        gp.add(targetL, 0, 2);
        gp.add(targetB, 1, 2);

        gp.add(unitLabel, 0, 3);
        gp.add(unitButton, 1, 3);

        GridPane.setHgrow(spebox, Priority.ALWAYS);
        GridPane.setHgrow(targetB, Priority.ALWAYS);
        GridPane.setHgrow(meaning, Priority.ALWAYS);

        //preite ,hoehe
        meaning.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        targetB.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        spebox.setPrefHeight(ROW_HIGHT);
        unitButton.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
    }

    private void buildDateTime(Meaning mode) {
        root.setPadding(new Insets(8, 8, 8, 8));

        final ComboBox<String> timeZone;
        ComboBox<String> timeLocale;
        final TextField formate = new TextField();
        Label timeZoneL = new Label("TimeZone:");
        Label targetL = new Label("Target:");
        Label vaueLocaleL = new Label("Locale:");

        formate.setPromptText("Formate");

        ObservableList<String> timeZoneOpt = FXCollections.observableArrayList();
        String[] allTimeZones = TimeZone.getAvailableIDs();

        timeZoneOpt = FXCollections.observableArrayList(allTimeZones);
        timeZone = new ComboBox<String>(timeZoneOpt);
//        timeZone.getSelectionModel().select("UTC");
        timeZone.getSelectionModel().select(TimeZone.getDefault().getID());
        timeZone.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                System.out.println("new Timezone: ");

                _selectedTimeZone = TimeZone.getTimeZone(timeZone.getSelectionModel().getSelectedItem());
            }
        });

        switch (mode) {
            case DateTime:
                formate.setText("yyyy-MM-dd HH:mm:ss");
                _dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                break;
            case Date:
                _dateFormater = new SimpleDateFormat("yyyy-MM-dd");
                formate.setText("yyyy-MM-dd");
                break;
            case Time:
                _dateFormater = new SimpleDateFormat("HH:mm:ss");
                formate.setText("HH:mm:ss");
                break;
        }

        formate.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                _currentFormate = formate.getText();
                _dateFormater = new SimpleDateFormat(_currentFormate);
                formteAllRows();
            }
        });

        HBox boxFormate = new HBox(5);
        ImageView help = JEConfig.getImage("1404161580_help_blue.png", 22, 22);
        boxFormate.getChildren().setAll(formate, help);

        help.setStyle("-fx-background-color: \n"
                + "        rgba(0,0,0,0.08);\n"
                + "    -fx-background-insets: 0 0 -1 0,0,1;\n"
                //                + "    -fx-background-radius: 5,5,4;\n"
                //                + "    -fx-padding: 3 30 3 30;\n"
                + "    -fx-text-fill: #242d35;\n"
                + "    -fx-font-size: 14px;");

        help.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                showHelp();
            }
        });

        //Damn workaround for fu***** layouts
        typeL.setPrefWidth(100);
        meaning.setPrefWidth(FIELD_WIDTH);
        timeZone.setPrefWidth(FIELD_WIDTH);
        formate.setPrefWidth(FIELD_WIDTH);

        boxFormate.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        meaning.setPrefSize(FIELD_WIDTH, ROW_HIGHT);
        timeZone.setPrefSize(FIELD_WIDTH, ROW_HIGHT);

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        root.getChildren().setAll(gp);

        //x , y
        gp.add(typeL, 0, 0);
        gp.add(meaning, 1, 0);

        gp.add(formateL, 0, 1);
        gp.add(boxFormate, 1, 1);

        gp.add(timeZoneL, 0, 2);
        gp.add(timeZone, 1, 2);
    }

    private void buildIgnoreGraphic() {
//        root.getChildren().removeAll();
        root.setPadding(new Insets(8, 8, 8, 8));

        GridPane gp = new GridPane();
        gp.setHgap(5);
        gp.setVgap(5);
        root.getChildren().setAll(gp);

        //x , y
        gp.add(typeL, 0, 0);
        gp.add(meaning, 1, 0);

    }

    private Button buildTargetButton() {
        final Button button = new Button("Select Import Target..");//, JEConfig.getImage("1404843819_node-tree.png", 15, 15));
        button.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                SelectTargetDialog dia = new SelectTargetDialog();
                if (dia.show(JEConfig.getStage(), _table.getDataSource()) == SelectTargetDialog.Response.OK) {
                    System.out.println("OK");
                    for (UserSelection selection : dia.getUserSelection()) {
                        button.setText(selection.getSelectedAttribute().getObject().getName() + "." + selection.getSelectedAttribute().getName());
                        _target = selection.getSelectedAttribute();
                        try {
                            System.out.println("Unit: " + _target.getDisplayUnit());
                            unitButton.setText(UnitFormat.getInstance().format(_target.getDisplayUnit()));
                        } catch (JEVisException ex) {
                            Logger.getLogger(CSVColumnHeader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        formteAllRows();
//                        try {
//                            Unit kwh = SI.KILO(SI.WATT.times(NonSI.HOUR));
//                            _target.setUnit(kwh);
//                            _target.getObject().commit();
//
//                        } catch (JEVisException ex) {
//                            Logger.getLogger(CSVColumnHeader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                    }
                }
            }
        });

        return button;
    }

    public Node getGraphic() {
        return root;
    }

    public void showHelp() {
        final Stage stage = new Stage();

        stage.setTitle("Help: Formate");
        stage.initModality(Modality.NONE);
        stage.initOwner(JEConfig.getStage());

//        BorderPane root = new BorderPane();
        VBox root = new VBox();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(750);
        stage.setHeight(620);
        stage.initStyle(StageStyle.UTILITY);

        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: linear-gradient(#e2e2e2,#eeeeee);");
        header.setPadding(new Insets(10, 10, 10, 10));

        Label topTitle = new Label("Help: Formate");
        topTitle.setTextFill(Color.web("#0076a3"));
        topTitle.setFont(Font.font("Cambria", 25));

        ImageView imageView = ResourceLoader.getImage("1404161580_help_blue.png", 65, 65);

        stage.getIcons().add(imageView.getImage());

        VBox vboxLeft = new VBox();
        VBox vboxRight = new VBox();
        vboxLeft.getChildren().add(topTitle);
        vboxLeft.setAlignment(Pos.CENTER_LEFT);
        vboxRight.setAlignment(Pos.CENTER_LEFT);
        vboxRight.getChildren().add(imageView);

        header.setLeft(vboxLeft);

        header.setRight(vboxRight);

        HBox webBox = new HBox();
        webBox.setPadding(new Insets(10));
        WebView helpView = new WebView();
//        helpView.getEngine().loadContent(getFormateHelpText());
//        URL urlHello = getClass().getResource("/html/help_dateformate.html");
        helpView.getEngine().load(getClass().getResource("/html/help_dateformate.html").toExternalForm());

        webBox.getChildren().setAll(helpView);

//        TextArea helpText = new TextArea();
//        helpText.setText(ICON_QUESTION);
        HBox buttonbox = new HBox();
        buttonbox.setAlignment(Pos.BOTTOM_RIGHT);

        Button close = new Button("Close");
        close.setDefaultButton(true);
        close.setCancelButton(true);
        close.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                stage.hide();
            }
        });
        buttonbox.getChildren().setAll(close);
        buttonbox.setPadding(new Insets(10));

        root.getChildren().setAll(header, webBox, buttonbox);

        stage.show();
    }

    private String getFormateHelpText() {
        return "<html lang=\"en\"><head>\n"
                + "<meta http-equiv=\"content-type\" content=\"text/html; charset=windows-1252\">\n"
                + "<title>MaskFormatter (Java Platform SE 7 )</title>\n"
                + "</head>\n"
                + "<body>\n"
                + "\n"
                + "<br>\n"
                + "<p>\n"
                + "<pre>Formate Mask</span>\n"
                + "MaskFormatter is used to format and edit strings. The behavior\n"
                + " of a <code>MaskFormatter</code> is controlled by way of a String mask\n"
                + " that specifies the valid characters that can be contained at a particular\n"
                + " location in the <code>Document</code> model. The following characters can\n"
                + " be specified:\n"
                + "\n"
                + " <table summary=\"Valid characters and their descriptions\" border=\"1\">\n"
                + " <tbody><tr>\n"
                + "    <th>Character&nbsp;</th>\n"
                + "    <th><p align=\"left\">Description</p></th>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>#</td>\n"
                + "    <td>Any valid number, uses <code>Character.isDigit</code>.</td>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>'</td>\n"
                + "    <td>Escape character, used to escape any of the\n"
                + "       special formatting characters.</td>\n"
                + " </tr>\n"
                + " <tr>\n"
                + "    <td>U</td><td>Any character (<code>Character.isLetter</code>). All\n"
                + "        lowercase letters are mapped to upper case.</td>\n"
                + " </tr>\n"
                + " <tr><td>L</td><td>Any character (<code>Character.isLetter</code>). All\n"
                + "        upper case letters are mapped to lower case.</td>\n"
                + " </tr>\n"
                + " <tr><td>A</td><td>Any character or number (<code>Character.isLetter</code>\n"
                + "       or <code>Character.isDigit</code>)</td>\n"
                + " </tr>\n"
                + " <tr><td>?</td><td>Any character\n"
                + "        (<code>Character.isLetter</code>).</td>\n"
                + " </tr>\n"
                + " <tr><td>*</td><td>Anything.</td></tr>\n"
                + " <tr><td>H</td><td>Any hex character (0-9, a-f or A-F).</td></tr>\n"
                + " </tbody></table>\n"
                + "</p>\n"
                + " \n"
                + "</body></html>";
    }

}
