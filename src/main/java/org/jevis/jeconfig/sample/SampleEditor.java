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
package org.jevis.jeconfig.sample;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisSample;
import org.jevis.application.dialog.DialogHeader;
import org.jevis.commons.dataprocessing.Options;
import org.jevis.commons.dataprocessing.ProcessorObjectHandler;
import org.jevis.commons.dataprocessing.Task;
import org.jevis.commons.dataprocessing.TaskImp;
import org.jevis.commons.dataprocessing.processor.AggrigatorProcessor;
import org.jevis.commons.dataprocessing.processor.InputProcessor;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.datepicker.DatePicker;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleEditor {

    public static String ICON = "1415314386_Graph.png";
    private boolean _dataChanged = false;
    private SampleEditorExtension _visibleExtension = null;
    private DateTime _from = null;
    private DateTime _until = null;
    final List<SampleEditorExtension> extensions = new ArrayList<>();
    private JEVisAttribute _attribute;
    private Task _dataProcessor;
    private List<JEVisObject> _dataProcessors = new ArrayList<JEVisObject>();

    private enum AGGREGATION {

        None, Daily, Weekly, Monthly,
        Yearly
    }

    private AGGREGATION _mode = AGGREGATION.None;

    public static enum Response {

        YES, CANCEL
    };
    List<JEVisSample> samples = new ArrayList<>();

    private Response response = Response.CANCEL;

//    final Label passL = new Label("New Password:");
//    final Label confirmL = new Label("Comfirm Password:");
//    final PasswordField pass = new PasswordField();
//    final PasswordField comfirm = new PasswordField();
    final Button ok = new Button("OK");

    /**
     *
     * @param owner
     * @param attribute
     * @return
     */
    public Response show(Stage owner, final JEVisAttribute attribute) {
        final Stage stage = new Stage();

        _attribute = attribute;
        stage.setTitle("Sample Editor");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);

        VBox root = new VBox();
        root.setMaxWidth(2000);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setWidth(740);
        stage.setHeight(690);
        stage.setMaxWidth(2000);
        stage.initStyle(StageStyle.UTILITY);
        stage.setResizable(false);

        Screen screen = Screen.getPrimary();
        if (screen.getBounds().getHeight() < 740) {
            stage.setWidth(screen.getBounds().getHeight());
        }

        HBox buttonPanel = new HBox();

        ok.setDefaultButton(true);

//        Button export = new Button("Export");
        Button cancel = new Button("Close");
        cancel.setCancelButton(true);

        Region spacer = new Region();
        spacer.setMaxWidth(2000);

        Label startLabel = new Label("From:");
        DatePicker startdate = new DatePicker();

        startdate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
//        startdate.getCalendarView().todayButtonTextProperty().set("Today");
        startdate.getCalendarView().setShowWeeks(false);
        startdate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

        Label endLabel = new Label("Until:");
        DatePicker enddate = new DatePicker();

        enddate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        enddate.getCalendarView().todayButtonTextProperty().set("Today");
        enddate.getCalendarView().setShowWeeks(true);
        enddate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));

        SampleTabelExtension tabelExtension = new SampleTabelExtension(attribute);//Default plugin

//        final List<JEVisSample> samples = attribute.getAllSamples();
        if (attribute.hasSample()) {
            _from = attribute.getTimestampFromLastSample().minus(Duration.standardDays(1));
            _until = attribute.getTimestampFromLastSample();

            startdate = new DatePicker(Locale.getDefault(), _from.toDate());
            startdate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            startdate.setSelectedDate(_from.toDate());
            startdate.getCalendarView().setShowWeeks(false);
            startdate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));
            startdate.setMaxWidth(100d);

//            enddate.setSelectedDate(_until.toDate());
            enddate.selectedDateProperty().setValue(_until.toDate());
            enddate.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
            enddate.setSelectedDate(_until.toDate());
            enddate.getCalendarView().setShowWeeks(true);
            enddate.getStylesheets().add(JEConfig.getResource("DatePicker.css"));
            enddate.setMaxWidth(100d);

        }

        Node preclean = buildProcessorBox(attribute.getObject());

        Label timeRangeL = new Label("Time range");
        timeRangeL.setStyle("-fx-font-weight: bold");
        GridPane timeSpan = new GridPane();
        timeSpan.setHgap(5);
        timeSpan.setVgap(2);
        timeSpan.add(timeRangeL, 0, 0, 2, 1); // column=1 row=0
        timeSpan.add(startLabel, 0, 1, 1, 1); // column=1 row=0
        timeSpan.add(endLabel, 0, 2, 1, 1); // column=1 row=0

        timeSpan.add(startdate, 1, 1, 1, 1); // column=1 row=0
        timeSpan.add(enddate, 1, 2, 1, 1); // column=1 row=0

        buttonPanel.getChildren().addAll(timeSpan, preclean, spacer, ok, cancel);
//        buttonPanel.getChildren().addAll(startLabel, startdate, endLabel, enddate, preclean, spacer, ok, cancel);
        buttonPanel.setAlignment(Pos.BOTTOM_RIGHT);
        buttonPanel.setPadding(new Insets(10, 10, 10, 10));
        buttonPanel.setSpacing(15);//10
        buttonPanel.setMaxHeight(25);
        HBox.setHgrow(spacer, Priority.ALWAYS);
//        HBox.setHgrow(export, Priority.NEVER);
        HBox.setHgrow(ok, Priority.NEVER);
        HBox.setHgrow(cancel, Priority.NEVER);

        extensions.add(tabelExtension);
        extensions.add(new SampleGraphExtension(attribute));
        extensions.add(new AttributeStatesExtension(attribute));
        extensions.add(new SampleExportExtension(attribute));

        final List<Tab> tabs = new ArrayList<>();

//        boolean fistEx = true;
        for (SampleEditorExtension ex : extensions) {
//            _dataChanged
//            if (fistEx) {
//                System.out.println("is first");
//                ex.setSamples(attribute, samples);
//                ex.update();
//                fistEx = false;
//            }

            Tab tabEditor = new Tab();
            tabEditor.setText(ex.getTitel());
            tabEditor.setContent(ex.getView());
            tabs.add(tabEditor);

        }
        _visibleExtension = extensions.get(0);
        updateSamples(attribute, _from, _until, extensions);

        final TabPane tabPane = new TabPane();
//        tabPane.setMaxWidth(2000);
//        tabPane.setMaxHeight(2000);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(tabs);

//        tabPane.setPrefSize(200, 200);
//        tabPane.getSelectionModel().selectFirst();
        GridPane gp = new GridPane();
        gp.setStyle("-fx-background-color: white;");

        gp.setHgap(0);
        gp.setVgap(0);
        int y = 0;
        gp.add(tabPane, 0, y);

        Node header = DialogHeader.getDialogHeader(ICON, "Sample Editor");//new Separator(Orientation.HORIZONTAL),

        root.getChildren().addAll(header, gp, new Separator(Orientation.HORIZONTAL), buttonPanel);
        VBox.setVgrow(buttonPanel, Priority.NEVER);
        VBox.setVgrow(header, Priority.NEVER);

//        ok.setDisable(true);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("OK action to: " + _visibleExtension.getTitel());
                _visibleExtension.sendOKAction();
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

            @Override
            public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
//                System.out.println("tabPane.getSelectionModel(): " + t1.getText());

                for (SampleEditorExtension ex : extensions) {
                    if (ex.getTitel().equals(t1.getText())) {
                        ex.update();
                        _visibleExtension = ex;
                    }
                }
//                }
            }
        });

        startdate.selectedDateProperty().addListener(new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> ov, Date t, Date t1) {
                DateTime from = new DateTime(t1.getTime());
                _from = from;
//                _visibleExtension.setSamples(attribute, attribute.getSamples(_from, _until));
                updateSamples(attribute, _from, _until, extensions);
            }
        });

        enddate.selectedDateProperty().addListener(new ChangeListener<Date>() {

            @Override
            public void changed(ObservableValue<? extends Date> ov, Date t, Date t1) {
                DateTime until = new DateTime(t1.getTime());
                _until = until;
//                _visibleExtension.setSamples(attribute, attribute.getSamples(_from, _until));
                updateSamples(attribute, _from, _until, extensions);
            }
        });

        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                stage.close();
                response = Response.CANCEL;

            }
        });

        //TODO: replace Workaround.., without it the first tab will be emty
//        tabPane.getSelectionModel().selectLast();
//        tabPane.getSelectionModel().selectFirst();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                tabPane.getSelectionModel().selectLast();
                tabPane.getSelectionModel().selectFirst();
            }
        });

        stage.showAndWait();

        return response;
    }

    private Node buildProcessorBox(final JEVisObject parentObj) {
        List<String> proNames = new ArrayList<>();
        proNames.add("Raw Data");

        try {
            JEVisClass dpClass = parentObj.getDataSource().getJEVisClass("Data Processor");
            _dataProcessors = parentObj.getChildren(dpClass, true);
            for (JEVisObject configObject : _dataProcessors) {
                proNames.add(configObject.getName());
            }

        } catch (JEVisException ex) {
            Logger.getLogger(SampleTabelExtension.class.getName()).log(Level.SEVERE, null, ex);
        }

        ChoiceBox processorBox = new ChoiceBox();
        processorBox.setItems(FXCollections.observableArrayList(proNames));
        processorBox.getSelectionModel().selectFirst();
        processorBox.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Select GUI Tpye: " + newValue);
                //TODO:replace this quick and dirty workaround

                try {
                    JEVisClass dpClass = parentObj.getDataSource().getJEVisClass("Data Processor");

                    if (newValue.equals("None")) {
                        _dataProcessor = null;
                        update();
                    } else {

                        //TODO going by name is not the fine art, replace!
                        for (JEVisObject configObject : _dataProcessors) {
                            if (configObject.getName().equals(newValue)) {
                                _dataProcessor = ProcessorObjectHandler.getTask(configObject);

                                update();
                            }

                        }
                    }

                } catch (JEVisException ex) {
                    Logger.getLogger(SampleTabelExtension.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
//
        List<String> aggList = new ArrayList<>();
        aggList.add("None");
        aggList.add("Daily");
        aggList.add("Weekly");
        aggList.add("Monthly");
        aggList.add("Yearly");

        ChoiceBox aggrigate = new ChoiceBox();
        aggrigate.setItems(FXCollections.observableArrayList(aggList));
        aggrigate.getSelectionModel().selectFirst();
        aggrigate.valueProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("Select Aggrigation Tpye: " + newValue);
                //TODO:replace this quick and dirty workaround

                switch (newValue) {
                    case "None":
                        _mode = AGGREGATION.None;
                        break;
                    case "Daily":
                        _mode = AGGREGATION.Daily;
                        break;
                    case "Weekly":
                        _mode = AGGREGATION.Weekly;
                        break;
                    case "Monthly":
                        _mode = AGGREGATION.Monthly;
                        break;
                    case "Yearly":
                        _mode = AGGREGATION.Yearly;
                        break;
                }
                update();

            }
        });

        processorBox.setMinWidth(150);
        aggrigate.setMinWidth(150);
//        aggrigate.prefWidthProperty().bind(processorBox.prefWidthProperty());
//        aggrigate.prefHeightProperty()
//        Bindings.add(aggrigate.prefWidthProperty(), processorBox.prefWidthProperty());

        HBox hbox = new HBox(2);

        Label header = new Label("Data Processing");
        header.setStyle("-fx-font-weight: bold");
        Label settingL = new Label("Setting:");
        Label aggregation = new Label("Aggregation:");//("Aggrigation");

        Button config = new Button();
        config.setGraphic(JEConfig.getImage("Service Manager.png", 16, 16));

        hbox.getChildren().addAll(processorBox);//, config);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(2);
        grid.add(header, 0, 0, 2, 1); // column=1 row=0

        grid.add(settingL, 0, 1, 1, 1); // column=1 row=0
        grid.add(aggregation, 0, 2, 1, 1); // column=1 row=0

        grid.add(hbox, 1, 1, 1, 1); // column=1 row=0
        grid.add(aggrigate, 1, 2, 1, 1); // column=1 row=0

        return grid;
    }

    private void update() {
        updateSamples(_attribute, _from, _until, extensions);
    }

    /**
     *
     * @param att
     * @param from
     * @param until
     * @param extensions
     */
    private void updateSamples(final JEVisAttribute att, final DateTime from, final DateTime until, List<SampleEditorExtension> extensions) {
        System.out.println("update samples");
        try {
            samples.clear();

            _from = from;
            _until = until;

            if (_dataProcessor != null) {
                Options.setStartEnd(_dataProcessor, _from, _until, true, true);
                _dataProcessor.restResult();
            }

            Task aggrigate = null;
            if (_mode == AGGREGATION.None) {

            } else if (_mode == AGGREGATION.Daily) {
                aggrigate = new TaskImp();
                aggrigate.setJEVisDataSource(att.getDataSource());
                aggrigate.setID("Dynamic");
                aggrigate.setProcessor(new AggrigatorProcessor());
                aggrigate.addOption(Options.PERIOD, Period.days(1).toString());
            } else if (_mode == AGGREGATION.Monthly) {
                aggrigate = new TaskImp();
                aggrigate.setJEVisDataSource(att.getDataSource());
                aggrigate.setID("Dynamic");
                aggrigate.setProcessor(new AggrigatorProcessor());
                aggrigate.addOption(Options.PERIOD, Period.months(1).toString());
            } else if (_mode == AGGREGATION.Weekly) {
                aggrigate = new TaskImp();
                aggrigate.setJEVisDataSource(att.getDataSource());
                aggrigate.setID("Dynamic");
                aggrigate.setProcessor(new AggrigatorProcessor());
                aggrigate.addOption(Options.PERIOD, Period.weeks(1).toString());
            } else if (_mode == AGGREGATION.Yearly) {
                System.out.println("year.....  " + Period.years(1).toString());
                aggrigate = new TaskImp();
                aggrigate.setJEVisDataSource(att.getDataSource());
                aggrigate.setID("Dynamic");
                aggrigate.setProcessor(new AggrigatorProcessor());
                aggrigate.addOption(Options.PERIOD, Period.years(1).toString());
            }

            if (_dataProcessor == null) {
                if (aggrigate != null) {
                    Task input = new TaskImp();
                    input.setJEVisDataSource(att.getDataSource());
                    input.setID("Dynamic Input");
                    input.setProcessor(new InputProcessor());
                    input.getOptions().put(InputProcessor.ATTRIBUTE_ID, _attribute.getName());
                    input.getOptions().put(InputProcessor.OBJECT_ID, _attribute.getObject().getID() + "");
                    aggrigate.setSubTasks(Arrays.asList(input));
                    samples.addAll(aggrigate.getResult());
                } else {
                    samples.addAll(att.getSamples(from, until));
                }

            } else {
                if (aggrigate != null) {
                    aggrigate.setSubTasks(Arrays.asList(_dataProcessor));
                    samples.addAll(aggrigate.getResult());
                } else {
                    samples.addAll(_dataProcessor.getResult());
                }
            }

            for (SampleEditorExtension ex : extensions) {
                ex.setSamples(att, samples);
            }

            _dataChanged = true;
            _visibleExtension.update();
        } catch (JEVisException ex) {
            ex.printStackTrace();
        }
    }

}
