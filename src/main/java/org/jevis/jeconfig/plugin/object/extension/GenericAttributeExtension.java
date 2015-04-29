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
package org.jevis.jeconfig.plugin.object.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import org.jevis.api.JEVisAttribute;
import org.jevis.api.JEVisConstants;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.application.dialog.ExceptionDialog;
import org.jevis.application.type.GUIConstants;
import org.jevis.jeconfig.Constants;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.plugin.object.ObjectEditor;
import org.jevis.jeconfig.plugin.object.ObjectEditorExtension;
import static org.jevis.jeconfig.JEConfig.PROGRAMM_INFO;
import org.jevis.jeconfig.plugin.object.attribute.AttributeEditor;
import org.jevis.jeconfig.plugin.object.attribute.BooleanValueEditor;
import org.jevis.jeconfig.plugin.object.attribute.FileValueEditor;
import org.jevis.jeconfig.plugin.object.attribute.NumberWithUnit;
import org.jevis.jeconfig.plugin.object.attribute.PasswordEditor;
import org.jevis.jeconfig.plugin.object.attribute.StringMultyLine;
import org.jevis.jeconfig.plugin.object.attribute.StringValueEditor;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
//@AITBilal - Hier werden die Attribute erstellen.
public class GenericAttributeExtension implements ObjectEditorExtension {

    private static final String TITEL = "Attributes";
    private final BorderPane _view = new BorderPane();
    private JEVisObject _obj;
    private boolean _needSave = false;
    private List<AttributeEditor> _attributesEditor;
    private final BooleanProperty _changed = new SimpleBooleanProperty(false);

    public GenericAttributeExtension(JEVisObject obj) {
        _obj = obj;
        _attributesEditor = new ArrayList<>();
        _view.setStyle("-fx-background-color: " + Constants.Color.LIGHT_GREY2);
    }

    @Override
    public boolean isForObject(JEVisObject obj) {

        //its for all in the memoment
        //TODO: handel the case that we have an spezial representation an dont whant the generic
        return true;
    }

    @Override
    public BooleanProperty getValueChangedProperty() {
        return _changed;
    }

    @Override
    public Node getView() {
        return _view;
    }

    @Override
    public void setVisible() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buildGui(_obj);
            }
        });
    }

    @Override
    public String getTitel() {
        return TITEL;
    }

    @Override
    public boolean needSave() {
        return _changed.getValue();
//        return true;//TODO: implement
    }

    @Override
    public boolean save() {
        for (AttributeEditor editor : _attributesEditor) {
            try {
                editor.commit();
            } catch (JEVisException ex) {
                Logger.getLogger(GenericAttributeExtension.class.getName()).log(Level.SEVERE, null, ex);
                ExceptionDialog dia = new ExceptionDialog();
                dia.show(JEConfig.getStage(), "Error", "Could not commit to Server", ex, PROGRAMM_INFO);
            }
        }

        _changed.setValue(false);
        _needSave = false;

        //TODO: save
        return true;
    }
    
    private void buildGui(JEVisObject obj) {

        _needSave = false;

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(5, 0, 20, 20));
        gridPane.setHgap(7);
        gridPane.setVgap(7);

        try {
            int coloum = 0;

            if (obj.getAttributes().isEmpty()) {
                Label emtyLabel = new Label("This object has no attributes.");
                gridPane.add(emtyLabel, 0, coloum);
            }

            for (JEVisAttribute att : obj.getAttributes()) {
                AttributeEditor editor = null;

                switch (att.getPrimitiveType()) {
                    case JEVisConstants.PrimitiveType.STRING:

                        try {
                            if (att.getType().getGUIDisplayType().equalsIgnoreCase(GUIConstants.BASIC_TEXT.getId())) {
                                editor = new StringValueEditor(att);
                            }
                            if (att.getType().getGUIDisplayType().equalsIgnoreCase(GUIConstants.BASIC_TEXT_MULTI.getId())) {
                                editor = new StringMultyLine(att);
                            }
                        } catch (Exception e) {
                            editor = new StringValueEditor(att);
                        }

                        break;
                    case JEVisConstants.PrimitiveType.BOOLEAN:
                        editor = new BooleanValueEditor(att);
                        break;
                    case JEVisConstants.PrimitiveType.FILE:
                        if (att.getType().getGUIDisplayType().equals(GUIConstants.NUMBER_WITH_UNIT.getId())) {
                            editor = new FileValueEditor(att);
                        } else {
                            editor = new StringValueEditor(att);
                        }

                        break;
                    case JEVisConstants.PrimitiveType.DOUBLE:
                        editor = new NumberWithUnit(att);
                        break;
                    case JEVisConstants.PrimitiveType.PASSWORD_PBKDF2:
                        editor = new PasswordEditor(att);
                        break;
                    default:
                        editor = new StringValueEditor(att);
                        break;

                }

                _attributesEditor.add(editor);
                editor.getValueChangedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                        System.out.println("GenericAttExtension.value.changed:" + t1);
                        if (t1) {
                            _changed.setValue(t1);
                        }
                    }
                });

                Label name = new Label("*Missing_Name*");

                name.setId("attributelabel");

                GridPane.setHalignment(name, HPos.LEFT);
                gridPane.add(name, 0, coloum);
                gridPane.add(editor.getEditor(), 1, coloum);

                name.setText(att.getName() + ":");

//                if (att.hasSample()) {
//                    value.setText(att.getLatestSample().getValueAsString());
//                }
                coloum++;
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

        AnchorPane.setTopAnchor(gridPane, 0.0);
        AnchorPane.setRightAnchor(gridPane, 0.0);
        AnchorPane.setLeftAnchor(gridPane, 0.0);
        AnchorPane.setBottomAnchor(gridPane, 0.0);

        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color: transparent");
        scroll.setMaxSize(10000, 10000);
        scroll.setContent(gridPane);
//        _view.getChildren().setAll(scroll);
        _view.setCenter(scroll);

    }
}
