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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisSample;
import org.joda.time.DateTime;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class BooleanValueEditor implements AttributeEditor {

    public JEVisAttribute _attribute;
    private CheckBox _field;
    private JEVisSample _newSample;
    private JEVisSample _lastSample;
    private boolean _hasChanged = false;
    private final BooleanProperty _changed = new SimpleBooleanProperty(false);

    public BooleanValueEditor(JEVisAttribute att) {
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

    @Override
    public void commit() throws JEVisException {
        if (hasChanged() && _newSample != null) {

            //TODO: check if tpye is ok, maybe better at imput time
            _newSample.commit();
        }
    }

    @Override
    public Node getEditor() {
        try {
            buildTextFild();
        } catch (Exception ex) {

        }
        return _field;

    }

    private void buildTextFild() throws JEVisException {
        if (_field == null) {
            _field = new CheckBox();
            _field.setPrefWidth(500);//TODO: hmm workaround remove

            if (_attribute.hasSample() && _attribute.getLatestSample() != null) {
                _field.setSelected(_attribute.getLatestSample().getValueAsBoolean());//TODO: get default Value
                _lastSample = _attribute.getLatestSample();
            } else {
                _field.setSelected(false);//TODO: get default Value
            }

            _field.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    try {
                        _newSample = _attribute.buildSample(new DateTime(), _field.isSelected());
                        _hasChanged = true;
                        _changed.setValue(true);
                    } catch (Exception ex) {
                        Logger.getLogger(BooleanValueEditor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            _field.setPrefWidth(500);
            _field.setId("attributelabel");

        }
    }
}
