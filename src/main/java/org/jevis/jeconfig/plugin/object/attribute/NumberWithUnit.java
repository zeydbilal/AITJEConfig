/**
 * Copyright (C) 2009 - 2014 Envidatec GmbH <info@envidatec.com>
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
package org.jevis.jeconfig.plugin.object.attribute;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
//import javafx.scene.control.Dialogs;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.sample.SampleEditor;
import org.joda.time.DateTime;

/**
 * This editor can edit and render values from the type number.
 *
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class NumberWithUnit implements AttributeEditor {

    HBox box = new HBox();
    public JEVisAttribute _attribute;
    private TextField _field;
    private Node cell;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private final BooleanProperty _changed = new SimpleBooleanProperty(false);
    String preOKValue;

    public NumberWithUnit(JEVisAttribute att) {
        _attribute = att;
    }

    @Override
    public boolean hasChanged() {
//        System.out.println(_attribute.getName() + " changed: " + _hasChanged);
        return _changed.getValue();
    }

    @Override
    public BooleanProperty getValueChangedProperty() {
        return _changed;
    }

//    @Override
//    public void setAttribute(JEVisAttribute att) {
//        _attribute = att;
//    }
    @Override
    public void commit() throws JEVisException {
        if (hasChanged() && _newSample != null) {

            //TODO: check if type is ok, maybe better at imput time
            _newSample.commit();
        }
    }

    @Override
    public Node getEditor() {
        try {
            buildTextFild();
        } catch (Exception ex) {

        }

        return box;
//        return _field;
    }

    private void buildTextFild() throws JEVisException {
        if (_field == null) {
            _field = new TextField();
//            _field = new TextField() {
//                @Override
//                public void replaceText(int start, int end, String text) {
//                    System.out.println("replaceText: s: " + start + " e:" + end + " text:" + text);
//
//                    if (text.equals(",")) {
//                        text = ".";
//                    }
//
//                    if (text.matches("[0-9]") && isVaildNumber(start, end, text)) {
//                        System.out.println("is valid: " + text);
//                        super.replaceText(start, end, text);
////                    } else if (text.equals(",") && isVaildNumber(start, end, text)) {
////                        System.out.println("is valid: " + text);
////                        super.replaceText(start, end, ".");
//                    } else if (text.equals(".") && isVaildNumber(start, end, text)) {
//                        System.out.println("is valid: " + text);
//                        super.replaceText(start, end, ".");
//                    }
////                    super.replaceText(start, end, "");
//                }
//
//                private boolean isVaildNumber(int start, int end, String text) {
//                    try {
//
//                        String beginning = "";
//                        String endstring = "";
//                        if (start > 0) {
//                            beginning = _field.getText().substring(0, start);
//                        }
//                        if (end < _field.getText().length()) {
//                            endstring = _field.getText().substring(end, _field.getText().length());
//                        }
//                        String substring = _field.getText().substring(start, end);
//
//                        double number = Double.valueOf(beginning + text + endstring);
//                        return true;
//                    } catch (NumberFormatException nex) {
//                        System.out.println("is not an number: " + nex);
//                        return false;
//                    }
//                }
//
//                private boolean isVaildNumber() {
//                    try {
//
//                        double number = Double.valueOf(_field.getText());
//                        return true;
//                    } catch (NumberFormatException nex) {
//                        System.out.println("nits not an number: " + nex);
//                        return false;
//                    }
//                }
//
//                @Override
//                public void replaceSelection(String text) {
//                    System.out.println("replace text");
//                    if (text.matches("[0-9]*") && isVaildNumber()) {
//                        super.replaceSelection(text);
//                    } else if (text.equals(",") && isVaildNumber()) {
//                        super.replaceSelection(".");
//                    } else if (text.equals(".") && isVaildNumber()) {
//                        super.replaceSelection(".");
//                    }
////                    super.replaceSelection("");
//                }
//            };

            _field.setPrefWidth(500);//TODO: remove this workaround

            _lastSample = _attribute.getLatestSample();

            if (_lastSample != null) {
//                System.out.println("Original Value: " + _attribute.getLatestSample().getValueAsDouble() + " " + _attribute.getInputUnit());
//                System.out.println("Display  Value: " + _attribute.getLatestSample().getValueAsDouble(_attribute.getDisplayUnit()) + " " + _attribute.getDisplayUnit());
                Double value = _attribute.getLatestSample().getValueAsDouble(_attribute.getDisplayUnit());
                _field.setText(value + "");

                preOKValue = _attribute.getLatestSample().getValueAsDouble(_attribute.getDisplayUnit()) + "";
            } else {
                _field.setText("");
                preOKValue = "";
            }

            //After the gui changed
            _field.setOnKeyReleased(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent t) {
                    try {
                        Double test = Double.valueOf(_field.getText());
                        preOKValue = test + "";
                    } catch (Exception ex) {
                        _field.setText(preOKValue + "");
//                        _field.setStyle("-fx-text-box-border: red ;\n" + "  -fx-focus-color: red ;");
                    }

                    try {
                        if (_lastSample == null) {
//                            System.out.println("new Value");
//                            _lastSample = _attribute.buildSample(new DateTime(), _field.getText());
                            _newSample = _attribute.buildSample(new DateTime(), _field.getText());
                            _changed.setValue(true);
                        } else if (!_lastSample.getValueAsString().equals(_field.getText())) {
//                            System.out.println("set Chnaged.numberwithunit");
                            _changed.setValue(true);
                            _newSample = _attribute.buildSample(new DateTime(), _field.getText());
//                            System.out.println("value changed");
                        } else if (_lastSample.getValueAsString().equals(_field.getText())) {
                            _changed.setValue(false);
                        }
                    } catch (JEVisException ex) {
                        Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            _field.setPrefWidth(500);
            _field.setId("attributelabel");
            _field.setAlignment(Pos.CENTER_RIGHT);

            if (_attribute.getType().getDescription() != null && !_attribute.getType().getDescription().isEmpty()) {
                Tooltip tooltip = new Tooltip();
                try {
                    tooltip.setText(_attribute.getType().getDescription());
                    tooltip.setGraphic(JEConfig.getImage("1393862576_info_blue.png", 30, 30));
                    _field.setTooltip(tooltip);
                } catch (JEVisException ex) {
                    Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
//            UnitChooser uc = new UnitChooser(_attribute.getType().getUnit(), 1);
            TextField tf = new TextField("kWh");
            tf.setDisable(true);
            tf.setPrefWidth(50);

//            System.out.println("formtetd unit: " + UnitManager.getInstance().formate(_attribute.getUnit()));
//            final Button unitb = new Button(UnitManager.getInstance().formate(_attribute.getType().getUnit()));
            final Button unitb = new Button(_attribute.getDisplayUnit().toString());

            double height = 28;

            unitb.setPrefWidth(60);
            unitb.setPrefHeight(height);
            unitb.setStyle("-fx-background-radius: 0 10 10 0; -fx-base: rgba(75, 106, 139, 0.89);");
            unitb.setAlignment(Pos.BOTTOM_LEFT);

            _field.setStyle("-fx-background-radius: 3 0 0 3;");
            _field.heightProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    System.out.println("file.height: " + newValue);
                }
            });

            unitb.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {

//                    final Point2D nodeCoord = unitb.localToScene(0.0, 0.0);
                    AttributeSettingsDialog asd = new AttributeSettingsDialog();

                    try {
                        if (asd.show(JEConfig.getStage(), _attribute) == AttributeSettingsDialog.Response.YES) {
//                            unitb.setText(asd.getPrefix() + UnitManager.getInstance().formate(suc.getUnit()));
                            asd.saveInDataSource();
                            unitb.setText(_attribute.getDisplayUnit().toString());
                            Double value = _attribute.getLatestSample().getValueAsDouble(_attribute.getDisplayUnit());
                            _field.setText(value + "");

                        }
                    } catch (JEVisException ex) {
                        Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            HBox.setHgrow(_field, Priority.ALWAYS);

            Button chartView = new Button();

            try {
                chartView = new Button();
                chartView.setGraphic(JEConfig.getImage("1394566386_Graph.png", height, height));
                chartView.setStyle("-fx-padding: 0 2 0 2;-fx-background-insets: 0;-fx-background-radius: 0;-fx-background-color: transparent;");

                chartView.setMaxHeight(_field.getHeight());
                chartView.setMaxWidth(height);
                chartView.setPrefHeight(height);

                box.getChildren().add(chartView);
                HBox.setHgrow(chartView, Priority.NEVER);
                box.setAlignment(Pos.CENTER_LEFT);

                chartView.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent t) {
                        SampleEditor se = new SampleEditor();
                        se.show(JEConfig.getStage(), _attribute);

                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(NumberWithUnit.class.getName()).log(Level.SEVERE, null, ex);
            }
            box.getChildren().setAll(chartView, _field, unitb);

        }
    }
}
