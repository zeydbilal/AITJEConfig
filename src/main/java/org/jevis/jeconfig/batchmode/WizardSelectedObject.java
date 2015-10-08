/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import org.jevis.api.JEVisObject;

/**
 *
 * @author Zeyd Bilal Calis
 */
//This is a help class for selected objects.
//Die ausgewaehlte Objekte werden in dieser Klasse abgespeichert bzw. gelagert.
public class WizardSelectedObject {

    // Selected objects from tree
    private JEVisObject currentSelectedObject;
    private JEVisObject currentSelectedBuildingObject;
    private JEVisObject currentDataDirectory;
    private JEVisObject currentDataPointDirectory;

    public WizardSelectedObject() {
    }

    public JEVisObject getCurrentSelectedObject() {
        return currentSelectedObject;
    }

    public void setCurrentSelectedObject(JEVisObject currentSelectedObject) {
        this.currentSelectedObject = currentSelectedObject;
    }

    public JEVisObject getCurrentSelectedBuildingObject() {
        return currentSelectedBuildingObject;
    }

    public void setCurrentSelectedBuildingObject(JEVisObject currentSelectedBuildingObject) {
        this.currentSelectedBuildingObject = currentSelectedBuildingObject;
    }

    public JEVisObject getCurrentDataDirectory() {
        return currentDataDirectory;
    }

    public void setCurrentDataDirectory(JEVisObject currentDataDirectory) {
        this.currentDataDirectory = currentDataDirectory;
    }

    public JEVisObject getCurrentDataPointDirectory() {
        return currentDataPointDirectory;
    }

    public void setCurrentDataPointDirectory(JEVisObject currentDataPointDirectory) {
        this.currentDataPointDirectory = currentDataPointDirectory;
    }
}
