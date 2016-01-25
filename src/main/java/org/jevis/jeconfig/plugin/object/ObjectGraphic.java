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
package org.jevis.jeconfig.plugin.object;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.api.JEVisRelationship;
import org.jevis.commons.CommonClasses;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectGraphic {

    private HBox _view = new HBox();
    private HBox _edior = new HBox();
    private ImageView icon = new ImageView();
    private Label nameLabel = new Label("*Missing*");
    private JEVisObject _obj;
    private ObjectContextMenu _menu;
    private ObjectTree _tree;
    private Tooltip _tip;

    public ObjectGraphic(JEVisObject obj, ObjectTree tree) {
//        System.out.println("    ObjectGraphic: " + obj.getID());
        _obj = obj;
        _tree = tree;
        update();
    }

    public void update() {
        _menu = new ObjectContextMenu(_obj, _tree);

        icon = getIcon(_obj);
        _view.setAlignment(Pos.CENTER_LEFT);
        _view.setSpacing(3);
        _view.setPadding(new Insets(0, 0, 0, 5));

        try {

            if (_obj.getJEVisClass() != null) {
                if (_obj.getJEVisClass().getName().equals(CommonClasses.LINK.NAME)) {

                    if (_obj.getLinkedObject() != null) {
                        icon = getIcon(_obj.getLinkedObject());
                    } else {
                        icon = JEConfig.getImage("1403724422_link_break.png", 20, 20);
                    }

                }
            } else {
                icon = JEConfig.getImage("1390343812_folder-open.png", 20, 20);
            }

        } catch (Exception ex) {
            Logger.getLogger(ObjectGraphic.class.getName()).log(Level.SEVERE, null, ex);
            icon = JEConfig.getImage("1403724422_link_break.png", 20, 20);
        }

        try {
            String classname = "";
            if (_obj.getJEVisClass() != null) {
                classname = _obj.getJEVisClass().getName();
            }

            _tip = new Tooltip(String.format("ID:       %s\nName: %s\nClass:  %s\n", _obj.getID().toString(), _obj.getName(), classname));
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectGraphic.class.getName()).log(Level.SEVERE, null, ex);
        }

        nameLabel.setText(_obj.getName());
        _view.getChildren().setAll(icon, nameLabel);
    }

    public Node getGraphic() {
//        nameLabel.setText(_obj.getName());
//        _view.getChildren().setAll(icon, nameLabel);

        return _view;
    }

    public ObjectContextMenu getContexMenu() {
        return _menu;
    }

    public Tooltip getToolTip() {
        return _tip;
    }

    public String getText() {
        return "";
    }

    private ImageView getIcon(JEVisObject item) {
        try {
            if (item != null && item.getJEVisClass() != null && item.getJEVisClass().getIcon() != null) {
//                System.out.println("return class icon");
                return ImageConverter.convertToImageView(item.getJEVisClass().getIcon(), 20, 20);//20
            } else {
                return JEConfig.getImage("1390343812_folder-open.png", 20, 20);
            }

        } catch (Exception ex) {
            System.out.println("Error while get icon for object: " + ex);
            return new ImageView();
        }

    }

}
