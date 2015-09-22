/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jevis.jeconfig.batchmode;

import java.util.Optional;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;
import org.jevis.api.JEVisObject;
import org.jevis.jeconfig.plugin.object.ObjectTree;

/**
 *
 * @author Bilal
 */

public class WizardMain extends Wizard {

    private JEVisObject parentObject;
    private ObjectTree tree;
    private WizardStartPane wizardStartPane;
    private ManualWizardStep1 manualStep1;
    private ManualWizardStep2 manualStep2;
    private ManualWizardStep3 manualStep3;
    private ManualWizardStep4 manualStep4;
    private WizardSelectedObject wizardSelectedObject = new WizardSelectedObject();

    private AutomatedWizardStep1 automatedWizardStep1 = new AutomatedWizardStep1();

    public WizardMain(JEVisObject parentObject, ObjectTree tree) {
        setParentObject(parentObject);
        this.tree = tree;

        wizardStartPane = new WizardStartPane();
        manualStep1 = new ManualWizardStep1(parentObject, tree, wizardSelectedObject);
        manualStep2 = new ManualWizardStep2(tree, wizardSelectedObject);
        manualStep3 = new ManualWizardStep3(tree, wizardSelectedObject);
        manualStep4 = new ManualWizardStep4(tree, wizardSelectedObject);

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
                //FIXME for Template Based
                return currentPage != manualStep4 && currentPage != automatedWizardStep1;
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
                    return manualStep3;
                } else if (currentPage.equals(manualStep3)) {
                    return manualStep4;
                } else if (currentPage.equals(wizardStartPane) && wizardStartPane.getControl().equals("Automated")) {
                    return automatedWizardStep1;
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
