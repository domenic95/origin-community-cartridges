/*
 * RHQ Management Platform
 * Copyright (C) 2005-2010 Red Hat, Inc.
 * All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.rhq.enterprise.gui.coregui.client.bundle.revert;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.VLayout;

import org.rhq.core.domain.bundle.BundleDeployment;
import org.rhq.enterprise.gui.coregui.client.CoreGUI;
import org.rhq.enterprise.gui.coregui.client.components.wizard.AbstractWizardStep;
import org.rhq.enterprise.gui.coregui.client.gwt.BundleGWTServiceAsync;
import org.rhq.enterprise.gui.coregui.client.gwt.GWTServiceLookup;
import org.rhq.enterprise.gui.coregui.client.util.message.Message;
import org.rhq.enterprise.gui.coregui.client.util.message.Message.Severity;
import org.rhq.enterprise.gui.coregui.client.util.selenium.Locatable;
import org.rhq.enterprise.gui.coregui.client.util.selenium.LocatableVLayout;

/**
 * @author Jay Shaughnessy
 *
 */
public class RevertStep extends AbstractWizardStep {

    private VLayout canvas;
    private final BundleRevertWizard wizard;

    private final BundleGWTServiceAsync bundleServer = GWTServiceLookup.getBundleService();

    public RevertStep(BundleRevertWizard wizard) {
        this.wizard = wizard;
    }

    public String getName() {
        return MSG.view_bundle_revertWizard_revertStep_name();
    }

    public Canvas getCanvas(Locatable parent) {
        if (canvas == null) {
            if (parent != null) {
                canvas = new LocatableVLayout(parent.extendLocatorId("BundleRevertRevert"));
            } else {
                canvas = new LocatableVLayout("BundleRevertRevert");
            }
            canvas.setWidth100();
            canvas.setHeight100();
            canvas.setAlign(Alignment.CENTER);

            final Img deployingImage = new Img("/images/status-bar.gif");
            deployingImage.setLayoutAlign(Alignment.CENTER);
            deployingImage.setWidth(50);
            deployingImage.setHeight(15);

            final Label deployingMessage = new Label(MSG.view_bundle_revertWizard_revertStep_reverting());
            deployingMessage.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

            canvas.addMember(deployingImage);
            canvas.addMember(deployingMessage);

            bundleServer.scheduleRevertBundleDeployment(this.wizard.getDestination().getId(), this.wizard
                .getDeploymentDescription(), this.wizard.isCleanDeployment(), //
                new AsyncCallback<BundleDeployment>() {
                    public void onSuccess(BundleDeployment result) {
                        deployingImage.setSrc("/images/status_complete.gif");
                        deployingMessage.setText(MSG.view_bundle_revertWizard_revertStep_scheduled());
                        CoreGUI.getMessageCenter().notify(
                            new Message(MSG.view_bundle_revertWizard_revertStep_scheduledDetails(result.getName(),
                                result.getDestination().getGroup().getName()), Severity.Info));
                        CoreGUI.refresh();
                        wizard.setDeployment(result);
                    }

                    public void onFailure(Throwable caught) {
                        deployingImage.setSrc("/images/status_error.gif");
                        deployingMessage.setText(MSG.view_bundle_revertWizard_revertStep_scheduledFailure());
                        CoreGUI.getErrorHandler().handleError(
                            MSG.view_bundle_revertWizard_revertStep_scheduledFailure(), caught);
                    }
                });
        }

        return canvas;
    }

    public boolean nextPage() {
        return true;
    }
}
