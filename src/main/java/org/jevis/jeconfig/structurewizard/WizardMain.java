/**
 * Copyright (C) 2015 WernerLamprecht <werner.lamprecht@ymail.com>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation in version 3.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * This wizard is part of the OpenJEVis project, further project information are
 * published at <http://www.OpenJEVis.org/>.
 */
package org.jevis.jeconfig.structurewizard;

import java.util.Optional;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author Werner Lamprecht, Zeyd Bilal Calis
 */
public class WizardMain extends Wizard {

    private JEVisObject parentObject;
    private ObjectTree tree;
    private WizardStartPane wizardStartPane;
    //The selected Parents.
    private WizardSelectedObject wizardSelectedObject = new WizardSelectedObject();

    //Manual Steps
    private MWS1 manualStep1;
    private MWS2 manualStep2;
    private MWS3 manualStep3;
    private MWS4 manualStep4;
    private MWS5 manualStep5;
    private MWS6 manualStep6;

    private ManualWizardStepLast manualStepLast; //Tabelle

    //Automated Steps
    private AutomatedWizardStep1 automatedWizardStep1;
    private AutomatedWizardStep2 automatedWizardStep2;

    public WizardMain(JEVisObject parentObject, ObjectTree tree) {
        setParentObject(parentObject);
        this.tree = tree;

        wizardStartPane = new WizardStartPane();

        manualStep1 = new MWS1(parentObject, tree, wizardSelectedObject);
        manualStep2 = new MWS2(tree, wizardSelectedObject);
        manualStep3 = new MWS3(tree, wizardSelectedObject);
        manualStep4 = new MWS4(tree, wizardSelectedObject);
        manualStep5 = new MWS5(tree, wizardSelectedObject);
        manualStep6 = new MWS6(tree, wizardSelectedObject);

        manualStepLast = new ManualWizardStepLast(tree, wizardSelectedObject);

        automatedWizardStep1 = new AutomatedWizardStep1(parentObject, tree, wizardSelectedObject);
        automatedWizardStep2 = new AutomatedWizardStep2(tree, wizardSelectedObject);

        setTitle("JEVIS Wizard");
        initWizard();
    }

    private void initWizard() {

        Wizard.Flow flow = new Wizard.Flow() {

            @Override
            public Optional<WizardPane> advance(WizardPane currentPage) {
                return Optional.of(getNext(currentPage));
            }

            @Override
            public boolean canAdvance(WizardPane currentPage) {
                return currentPage != manualStepLast && currentPage != automatedWizardStep2;
            }

            private WizardPane getNext(WizardPane currentPage) {
                if (currentPage == null) {
                    return wizardStartPane;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Manual")) {
                    // On the page ManualWizardStep1
                    return manualStep1;
                } else if (currentPage.equals(manualStep1)) {
                    // On the page ManualWizardStep2
                    return manualStep2;
                } else if (currentPage.equals(manualStep2)) {
                    // On the page ManualWizardStep3
                    return manualStep3;
                } else if (currentPage.equals(manualStep3)) {
                    // On the page ManualWizardStep4
                    return manualStep4;
                } else if (currentPage.equals(manualStep4)) {
                    // On the page ManualWizardStep5
                    return manualStep5;
                } else if (currentPage.equals(manualStep5)) {
                    // On the page ManualWizardStep6
                    return manualStep6;
                } else if (currentPage.equals(manualStep6)) {
                    return manualStepLast;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Automated Wiotech Structure Creation")) {
                    return automatedWizardStep1;
                } else if (currentPage.equals(automatedWizardStep1)) {
                    return automatedWizardStep2;
                } else {
                    return null;
                }
            }
        };
        setFlow(flow);
    }

    public JEVisObject getParentObject() {
        return this.parentObject;
    }

    public void setParentObject(JEVisObject parentObject) {
        this.parentObject = parentObject;
    }
}
