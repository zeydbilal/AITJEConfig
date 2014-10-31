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
package org.jevis.jeconfig.plugin.unit;

import java.util.Locale;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javax.measure.unit.Unit;
import org.jevis.application.unit.UnitChooser;
import org.jevis.commons.unit.UnitManager;

/**
 *
 * @author Florian Simon <florian.simon@envidatec.com>
 */
public class UnitEditor {

    VBox _view = new VBox();

    public void setUnit(final UnitObject unit) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                GridPane gridPane = new GridPane();
                gridPane.setPadding(new Insets(10));
                gridPane.setHgap(7);
                gridPane.setVgap(7);

                Label nameL = new Label("Name: ");
                Label symbolL = new Label("Symbol: ");
                Label orderLabel = new Label("Dimension: ");

                TextField nameT = new TextField(UnitManager.getInstance().getUnitName(unit.getUnit(), Locale.ENGLISH));
                TextField symboleT = new TextField(unit.getUnit().toString());
                TextField orderT = new TextField(unit.getUnit().getDimension().toString());

                UnitChooser uc = new UnitChooser(Unit.ONE);

                UnitChooser uc2 = new UnitChooser(Unit.ONE, 0);

                gridPane.add(nameL, 0, 0);
                gridPane.add(symbolL, 0, 1);
                gridPane.add(orderLabel, 0, 2);
                gridPane.add(nameT, 1, 0);
                gridPane.add(symboleT, 1, 1);
                gridPane.add(orderT, 1, 2);

                //gridPane.add(uc.getGraphic(), 0, 3);
                //gridPane.add(uc2.getGraphic(), 0, 4);
                _view.getChildren().setAll(gridPane);

            }
        });

    }

    public Node getView() {
        return _view;
    }

}
