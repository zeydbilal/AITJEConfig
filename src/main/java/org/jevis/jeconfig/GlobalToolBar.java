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
package org.jevis.jeconfig;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import org.jevis.jeconfig.plugin.object.ObjectPlugin;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class GlobalToolBar {

    private static final String STANDARD_BUTTON_STYLE = "-fx-background-color: transparent;-fx-background-insets: 0 0 0;";
    private static final String HOVERED_BUTTON_STYLE = "-fx-background-insets: 1 1 1;";
    private final PluginManager pm;

    public GlobalToolBar(PluginManager pm) {
        this.pm = pm;
    }

    public ToolBar ToolBarFactory() {
        ToolBar toolBar = new ToolBar();
        double iconSize = 20;
        ToggleButton newB = new ToggleButton("", JEConfig.getImage("list-add.png", iconSize, iconSize));
        changeBackgroundOnHoverUsingBinding(newB);
        addEventHandler(newB, Constants.Plugin.Command.NEW);

        ToggleButton save = new ToggleButton("", JEConfig.getImage("save.gif", iconSize, iconSize));
        changeBackgroundOnHoverUsingBinding(save);
        addEventHandler(save, Constants.Plugin.Command.SAVE);

        ToggleButton delete = new ToggleButton("", JEConfig.getImage("list-remove.png", iconSize, iconSize));
        changeBackgroundOnHoverUsingBinding(delete);
        addEventHandler(delete, Constants.Plugin.Command.DELTE);

        Separator sep1 = new Separator();

        ToggleButton reload = new ToggleButton("", JEConfig.getImage("1403018303_Refresh.png", iconSize, iconSize));
        changeBackgroundOnHoverUsingBinding(reload);
        addEventHandler(reload, Constants.Plugin.Command.RELOAD);

        //@AITBilal - A new Form button on the ToolBar
        ToggleButton form = new ToggleButton("", JEConfig.getImage("add_a_form.png", iconSize, iconSize));
        changeBackgroundOnHoverUsingBinding(form);
        addEventHandler(form,Constants.Plugin.Command.FORM);

        toolBar.getItems().addAll(save, newB, delete, sep1, form);

        //test
        return toolBar;
    }

    private void addEventHandler(ToggleButton button, final int command) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("send command  " + command);
                pm.getSelectedPlugin().handelRequest(command);

//                if (pm.getSelectedPlugin() instanceof ObjectPlugin) {
//                    ((ObjectPlugin) pm.getSelectedPlugin()).handelRequest(command);
//                }
            }
        });

    }

    private void addEventHandler(Button button, final int command) {
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                if (pm.getSelectedPlugin() instanceof ObjectPlugin) {
                    ((ObjectPlugin) pm.getSelectedPlugin()).handelRequest(command);
                }

            }
        });

    }

    private static void changeBackgroundOnHoverUsingBinding(Node node) {
        node.styleProperty().bind(
                Bindings
                .when(node.hoverProperty())
                .then(
                        new SimpleStringProperty(HOVERED_BUTTON_STYLE))
                .otherwise(
                        new SimpleStringProperty(STANDARD_BUTTON_STYLE)));
    }
}
