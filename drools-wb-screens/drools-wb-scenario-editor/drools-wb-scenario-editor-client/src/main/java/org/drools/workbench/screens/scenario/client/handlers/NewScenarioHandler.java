/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenario.client.handlers;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenario.client.resources.ScenarioEditorResources;
import org.drools.workbench.screens.scenario.client.resources.i18n.ScenarioEditorConstants;
import org.drools.workbench.screens.scenario.client.type.ScenarioResourceType;
import org.drools.workbench.screens.scenario.model.ScenarioModel;
import org.drools.workbench.screens.scenario.service.ScenarioEditorService;
import org.guvnor.common.services.project.model.Package;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.widgets.client.handlers.DefaultNewResourceHandler;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * Handler for the creation of new DRL Text Rules
 */
@ApplicationScoped
public class NewScenarioHandler extends DefaultNewResourceHandler {

    @Inject
    private Caller<ScenarioEditorService> globalsService;

    @Inject
    private PlaceManager placeManager;

    @Inject
    private ScenarioResourceType resourceType;

    @Inject
    private BusyIndicatorView busyIndicatorView;

    @Inject
    private TranslationService translationService;

    @Override
    public String getDescription() {
        return translationService.getTranslation(ScenarioEditorConstants.NewScenarioHandlerNewScenarioDescription );
    }

    @Override
    public IsWidget getIcon() {
        return new Image(ScenarioEditorResources.INSTANCE.images().typeGlobalVariable() );
    }

    @Override
    public ResourceTypeDefinition getResourceType() {
        return resourceType;
    }

    @Override
    public void create( final Package pkg,
                        final String baseFileName,
                        final NewResourcePresenter presenter ) {
        final ScenarioModel model = new ScenarioModel();
        busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
        globalsService.call( getSuccessCallback( presenter ),
                             new HasBusyIndicatorDefaultErrorCallback( busyIndicatorView ) ).create( pkg.getPackageMainResourcesPath(),
                                                                                                     buildFileName( baseFileName,
                                                                                                                    resourceType ),
                                                                                                     model,
                                                                                                     "" );
    }

}
