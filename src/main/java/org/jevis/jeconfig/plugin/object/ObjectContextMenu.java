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
package org.jevis.jeconfig.plugin.object;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.ImageView;
import org.jevis.api.JEVisClass;
import org.jevis.api.JEVisException;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.JEConfig;
import org.jevis.jeconfig.export.JsonExportDialog;
import org.jevis.jeconfig.tool.ImageConverter;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class ObjectContextMenu extends ContextMenu {

    private JEVisObject _obj;
    private ObjectTree _tree;

    public ObjectContextMenu(JEVisObject obj, ObjectTree tree) {
        super();

        _obj = obj;
        _tree = tree;

//        getItems().add(buildMenuNew());
        getItems().setAll(
                buildNew2(),
                new SeparatorMenuItem(),
                buildDelete(),
                buildRename(),
                buildCopy(),
                buildPaste(),
                buildExport()
        );

    }

    private MenuItem buildPaste() {
        //TODO: diable if not allowed
        MenuItem menu = new MenuItem("Paste", JEConfig.getImage("17_Paste_48x48.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                _tree.pasteCopyItem();
            }
        }
        );
        return menu;
    }

    private MenuItem buildCopy() {
        MenuItem menu = new MenuItem("Copy", JEConfig.getImage("16_Copy_48x48.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                _tree.copyCurrentItem();
            }
        }
        );
        return menu;
    }

    private MenuItem buildExport() {
        MenuItem menu = new MenuItem("Export", JEConfig.getImage("1401894975_Export.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                JsonExportDialog dia = new JsonExportDialog(JEConfig.getStage(), "Export", _obj);
            }
        }
        );
        return menu;
    }

    public List<MenuItem> buildMenuNewContent() {
        List<MenuItem> newContent = new ArrayList<>();
        try {
            for (JEVisClass jlass : _obj.getAllowedChildrenClasses()) {
                MenuItem classItem;

                classItem = new CheckMenuItem(jlass.getName(), getIcon(jlass));
                classItem.setOnAction(new EventHandler<ActionEvent>() {

                    @Override
                    public void handle(ActionEvent t) {
                        _tree.fireEventNew(_obj);
                    }
                }
                );
                newContent.add(classItem);
            }
        } catch (JEVisException ex) {
            Logger.getLogger(ObjectContextMenu.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newContent;
    }

    private Menu buildMenuNew() {
        Menu addMenu = new Menu("New", JEConfig.getImage("list-add.png", 20, 20));
        addMenu.getItems().addAll(buildMenuNewContent());

        return addMenu;

    }

    private ImageView getIcon(JEVisClass jclass) {
        try {
            return ImageConverter.convertToImageView(jclass.getIcon(), 20, 20);
        } catch (Exception ex) {
            return JEConfig.getImage("1393615831_unknown2.png", 20, 20);
        }
    }

    private MenuItem buildProperties() {
        MenuItem menu = new MenuItem("Expand");//shoud be edit but i use it for expand for the time
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
//                PopOver popup = new PopOver(new HBox());
//                popup.show(_item.getGraphic(), 200d, 200d, Duration.seconds(1));
                //TMP test

//                System.out.println("expand all");
//                _item.expandAll(true);
            }
        });
        return menu;
    }

    private MenuItem buildNew2() {
        MenuItem menu = new MenuItem("New", JEConfig.getImage("list-add.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

//                _tree.edit(_item);
                //workaround
                if (_tree instanceof ObjectTree) {
//                    ((ObjectTree) _tree).fireEventRename();
                    _tree.fireEventNew(_obj);

                }

            }
        });
        return menu;
    }

    private MenuItem buildRename() {
        MenuItem menu = new MenuItem("Rename", JEConfig.getImage("Rename.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

//                _tree.edit(_item);
                //workaround
                if (_tree instanceof ObjectTree) {
                    ((ObjectTree) _tree).fireEventRename();
                }

            }
        });
        return menu;
    }

    private MenuItem buildDelete() {
        MenuItem menu = new MenuItem("Delete", JEConfig.getImage("list-remove.png", 20, 20));
        menu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent t) {
                _tree.fireDelete(_obj);
            }
        });
        return menu;
    }
}
