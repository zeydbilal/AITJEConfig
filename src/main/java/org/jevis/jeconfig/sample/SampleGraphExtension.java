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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisSample;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class SampleGraphExtension implements SampleEditorExtension {

    private final static String TITEL = "Graph";
    private final BorderPane _view = new BorderPane();
    private JEVisAttribute _att;
    private List<JEVisSample> _samples;
    private boolean _dataChanged = true;

    public SampleGraphExtension(JEVisAttribute att) {
        _att = att;
    }

    private void buildGui(JEVisAttribute obj, List<JEVisSample> samples) {

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: transparent");
        scroll.setMaxSize(10000, 10000);
        scroll.setContent(buildChart(samples));
//        _view.getChildren().setAll(scroll);
        _view.setCenter(scroll);
//        _view.setCenter(buildChart(samples));
    }

    @Override
    public boolean isForAttribute(JEVisAttribute obj) {
        return true;
    }

    @Override
    public Node getView() {
        return _view;
    }

    @Override
    public String getTitel() {
        return TITEL;
    }

    @Override
    public void setSamples(final JEVisAttribute att, final List<JEVisSample> samples) {
        _samples = samples;
        _att = att;
        _dataChanged = true;
    }

    @Override
    public void update() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (_dataChanged) {
                    buildGui(_att, _samples);
                    _dataChanged = false;
                }
            }
        });
    }

    private XYChart buildChart(final List<JEVisSample> samples) {

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(true);

        xAxis.setTickUnit(1);
        xAxis.setTickMarkVisible(true);
        xAxis.setTickLength(1);
//        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
//
//            @Override
//            public String toString(Number t) {
//
//                DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd    HH:mm:ss");
//                try {
////                    Number index = (t.doubleValue() - 1.5);
//                    Number index = t.doubleValue();
////                    if (index.intValue() < 0) {
////                        index = 0;
////                    }
////                    if (index.intValue() > 100) {
////                        System.out.println(" is bigger 100");
////                    }
//                    System.out.println("convert Major value: " + t.toString() + "=" + index);
//                    return fmtDate.print(samples.get(index.intValue()).getTimestamp());
////                return fmtDate.print(new DateTime(t.longValue()));
//                } catch (Exception ex) {
//                    System.out.println("error");
//                }
//                return t.toString();
//            }
//
//            @Override
//            public Number fromString(String string) {
//                System.out.println("from string: " + string);
//                return 200;
//            }
//        });

        final DateTimeFormatter fmtDate = DateTimeFormat.forPattern("yyyy-MM-dd    HH:mm:ss");
        xAxis.setMinorTickCount(1);
        xAxis.setMinorTickLength(1);
        xAxis.setMinorTickVisible(true);
        xAxis.setTickLabelRotation(75d);
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number t) {

                try {
//                    System.out.println("number: " + t);
                    //TODO: replace this DIRTY workaround. For this i will come in the DevHell
                    //NOTE: the axis is % based, java 1.8 has an dateAxe use this if we migrate to it
                    if (!samples.isEmpty()) {
                        Double round = samples.size() - 1 / 100.0 * t.doubleValue();
                        int index = round.intValue() - 1;
                        return fmtDate.print(samples.get(index).getTimestamp());
                    }
                    return "";

//                    return fmtDate.print(samples.get(t.intValue()).getTimestamp());
                } catch (Exception ex) {
                    System.out.println("error: " + ex);
                    return "";
                }

            }

            @Override
            public Number fromString(String string) {
                return -1;
            }
        });

//        xAxis.setLabel("Month");
        final LineChart<Number, Number> lineChart = new LineChart(xAxis, yAxis);
//        final BarChart<String, Number> lineChart = new BarChart(xAxis, yAxis);

        String titel = String.format("");

        lineChart.setTitle(titel);
//        lineChart.setAnimated(true);
        lineChart.setLegendVisible(false);
        lineChart.setCache(true);

        XYChart.Series series1 = new XYChart.Series();

//        DateTimeFormatter fmttime = DateTimeFormat.forPattern("E HH:mm:ss");
//        DateTimeFormatter fmttime2 = DateTimeFormat.forPattern("E HH:mm:ss");
        int pos = 0;

        for (JEVisSample sample : samples) {
            try {
////                String datelabel = "";
////                if (pos == 0 || samples.size() == pos || pos % 10 == 0) {
////                    datelabel = fmtDate.print(sample.getTimestamp());
////                }

//                String datelabel = fmtDate.print(sample.getTimestamp());
                series1.getData().add(new XYChart.Data((Number) pos, sample.getValueAsDouble()));

//                System.out.println("pos1: " + pos + " sample=" + sample);
                if (yAxis.getLowerBound() > sample.getValueAsDouble()) {
                    yAxis.setLowerBound(sample.getValueAsDouble() * 0.9d);
                }

                pos++;
//                series1.getData().add(new XYChart.Data(pos + "", sample.getValueAsDouble()));
            } catch (Exception ex) {
                Logger.getLogger(SampleEditor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        yAxis.setLowerBound(10d);

//        int size = 22 * samples.size();
//        int size = 15 * samples.size();
//        lineChart.setPrefWidth(size);
        lineChart.setPrefWidth(720);
        lineChart.getData().addAll(series1);

        return lineChart;
    }

    @Override
    public boolean sendOKAction() {
        return false;
    }

}
