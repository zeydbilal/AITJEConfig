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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.jevis.api.JEVisDataSource;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectPlugin;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class PluginManager {

    private List<Plugin> _plugins = new ArrayList<>();
    private JEVisDataSource _ds;
    private boolean _watermark = true;
    private Plugin _selectedPlugin = null;
    private Number _tabPos = 0;
    private Number _tabPosOld = 0;
    private TabPane tabPane;

    public PluginManager(JEVisDataSource _ds) {
        this._ds = _ds;
    }

    public void addPlugin(Plugin plugin) {
        _plugins.add(plugin);
    }

    public void removePlugin(Plugin plugin) {
        _plugins.remove(plugin);
    }

    public List<Plugin> getPlugins() {
        return _plugins;
    }

    public void addPluginsByUserSetting(JEVisObject user) {
        _plugins.add(new ObjectPlugin(_ds, "Resources"));
        _plugins.add(new org.jevis.jeconfig.plugin.classes.ClassPlugin(_ds, "Classes"));
        _plugins.add(new org.jevis.jeconfig.plugin.unit.UnitPlugin(_ds, "Units"));
    }

    public void setWatermark(boolean water) {
        _watermark = water;
    }

    public Node getView() {
        StackPane box = new StackPane();
        
        //@AITBilal - TabPane für Resources,Classes und Units
        //Alle Plugins werden hier aufgerufen!
        tabPane = new TabPane();
        tabPane.setSide(Side.LEFT);

        for (Plugin plugin : _plugins) {
            Tab pluginTab = new Tab(plugin.getName());
            pluginTab.setClosable(false);
            pluginTab.setTooltip(new Tooltip(plugin.getUUID()));
//            pluginTab.setContent(plugin.getView().getNode());
            pluginTab.setContent(plugin.getConntentNode());
            tabPane.getTabs().add(pluginTab);

            pluginTab.setOnClosed(new EventHandler<Event>() {
                @Override
                public void handle(Event t) {
                    Plugin plugin = _plugins.get(_tabPosOld.intValue());
                    _plugins.remove(plugin);
                }
            });

            pluginTab.setGraphic(plugin.getIcon());

        }

        tabPane.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                // do something...
                _tabPos = newValue;
                _tabPosOld = oldValue;
            }
        });

        if (_watermark) {
            VBox waterBox = new VBox();
            //TODO better load the watermark from an JEVis Object and not from css
            waterBox.setId("watermark");
            waterBox.setStyle(null);
            waterBox.setDisable(true);
            box.getChildren().addAll(tabPane, waterBox);
        } else {
            box.getChildren().addAll(tabPane);
        }

        return box;
    }

    Plugin getSelectedPlugin() {
        return _plugins.get(_tabPos.intValue());
    }
}
