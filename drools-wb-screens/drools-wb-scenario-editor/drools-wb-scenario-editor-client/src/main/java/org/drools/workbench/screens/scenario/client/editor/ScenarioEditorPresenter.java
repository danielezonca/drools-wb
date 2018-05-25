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

package org.drools.workbench.screens.scenario.client.editor;

import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.drools.workbench.screens.scenario.client.type.ScenarioResourceType;
import org.drools.workbench.screens.scenario.model.ScenarioEditorContent;
import org.drools.workbench.screens.scenario.model.ScenarioModel;
import org.drools.workbench.screens.scenario.service.ScenarioEditorService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.kie.workbench.common.widgets.client.popups.validation.ValidationPopup;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.metadata.client.KieEditor;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.service.support.SupportsSaveAndRename;
import org.uberfire.ext.widgets.common.client.callbacks.CommandErrorCallback;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchEditor(identifier = "org.kie.guvnor.scenario", supportedTypes = {ScenarioResourceType.class}, priority = 0)
public class ScenarioEditorPresenter
        extends KieEditor<ScenarioModel> {

    @Inject
    protected Caller<ScenarioEditorService> globalsEditorService;

    @Inject
    protected Caller<ValidationService> validationService;

    @Inject
    protected ValidationPopup validationPopup;

    private ScenarioEditorView view;

    @Inject
    private ScenarioResourceType type;

    private ScenarioModel model;

    public ScenarioEditorPresenter() {
    }

    @Inject
    public ScenarioEditorPresenter(final ScenarioEditorView baseView) {
        super(baseView);
        this.view = baseView;
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.init(path,
                   place,
                   type);
    }

    @Override
    protected void makeMenuBar() {
        if (canUpdateProject()) {
            fileMenuBuilder
                    .addSave(versionRecordManager.newSaveMenuItem(this::saveAction))
                    .addCopy(versionRecordManager.getCurrentPath(),
                             assetUpdateValidator)
                    .addRename(getSaveAndRename())
                    .addDelete(this::onDelete);
        }

        fileMenuBuilder
                .addValidate(getValidateCommand())
                .addNewTopLevelMenu(versionRecordManager.buildMenu())
                .addNewTopLevelMenu(alertsButtonMenuItemBuilder.build());
    }

    @Override
    protected void loadContent() {
        view.showLoading();
        view.init();
        globalsEditorService.call(getModelSuccessCallback(),
                                  getNoSuchFileExceptionErrorCallback()).loadContent(versionRecordManager.getCurrentPath());
    }

    @Override
    protected Supplier<ScenarioModel> getContentSupplier() {
        return () -> model;
    }

    @Override
    protected Caller<? extends SupportsSaveAndRename<ScenarioModel, Metadata>> getSaveAndRenameServiceCaller() {
        return globalsEditorService;
    }

    protected RemoteCallback<ScenarioEditorContent> getModelSuccessCallback() {
        return content -> {
            //Path is set to null when the Editor is closed (which can happen before async calls complete).
            if (versionRecordManager.getCurrentPath() == null) {
                return;
            }

            model = content.getModel();

            resetEditorPages(content.getOverview());
            addSourcePage();

            final List<String> fullyQualifiedClassNames = content.getFullyQualifiedClassNames();

            createOriginalHash(model);
            view.hideBusyIndicator();
        };
    }

    @Override
    protected void onValidate(final Command finished) {
        globalsEditorService.call(
                validationPopup.getValidationCallback(finished),
                new CommandErrorCallback(finished)).validate(versionRecordManager.getCurrentPath(),
                                                             model);
    }

    @Override
    protected void save() {
        validationService.call((validationMessages) -> {
            if (((List<ValidationMessage>) validationMessages).isEmpty()) {
                showSavePopup();
            } else {
                validationPopup.showSaveValidationMessages(() -> showSavePopup(),
                                                           () -> {
                                                           },
                                                           (List<ValidationMessage>) validationMessages);
            }
        }).validateForSave(versionRecordManager.getCurrentPath(),
                           model);

        concurrentUpdateSessionInfo = null;
    }

    private void showSavePopup() {
        savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                new ParameterizedCommand<String>() {
                                    @Override
                                    public void execute(final String commitMessage) {
                                        baseView.showSaving();
                                        globalsEditorService.call(getSaveSuccessCallback(model.hashCode()),
                                                                  new HasBusyIndicatorDefaultErrorCallback(view)).save(versionRecordManager.getCurrentPath(),
                                                                                                                       model,
                                                                                                                       metadata,
                                                                                                                       commitMessage);
                                        concurrentUpdateSessionInfo = null;
                                    }
                                });
    }

    protected void onDelete() {
        validationService.call((validationMessages) -> {
            if (((List<ValidationMessage>) validationMessages).isEmpty()) {
                showDeletePopup(getVersionRecordManager().getCurrentPath());
            } else {
                validationPopup.showDeleteValidationMessages(() -> showDeletePopup(versionRecordManager.getCurrentPath()),
                                                             () -> {
                                                             },
                                                             (List<ValidationMessage>) validationMessages);
            }
        }).validateForDelete(versionRecordManager.getCurrentPath());
    }

    private void showDeletePopup(final Path path) {
        deletePopUpPresenter.show(assetUpdateValidator,
                                  comment -> {
                                      view.showBusyIndicator(CommonConstants.INSTANCE.Deleting());
                                      globalsEditorService.call(getDeleteSuccessCallback(),
                                                                new HasBusyIndicatorDefaultErrorCallback(view)).delete(path,
                                                                                                                       "delete");
                                  });
    }

    private RemoteCallback<Path> getDeleteSuccessCallback() {
        return response -> {
            view.hideBusyIndicator();
            notification.fire(new NotificationEvent(org.uberfire.ext.editor.commons.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully()));
        };
    }

    @Override
    public void onSourceTabSelected() {
        globalsEditorService.call(new RemoteCallback<String>() {
            @Override
            public void callback(String source) {
                updateSource(source);
            }
        }).toSource(versionRecordManager.getCurrentPath(),
                    model);
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return super.getWidget();
    }

    @OnClose
    public void onClose() {
        this.versionRecordManager.clear();
    }

    @OnMayClose
    public boolean mayClose() {
        return super.mayClose(model);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }
}
