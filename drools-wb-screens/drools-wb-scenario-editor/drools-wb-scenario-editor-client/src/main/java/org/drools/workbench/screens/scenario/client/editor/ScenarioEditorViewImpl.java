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

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;
import org.drools.workbench.screens.scenario.client.resources.i18n.ScenarioEditorConstants;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;


@Templated
@Dependent
public class ScenarioEditorViewImpl
        extends KieEditorViewImpl
        implements ScenarioEditorView {

    private ScenarioEditorPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    @DataField("add-button")
    private Button addButton;

    @Inject
    @DataField("list-div")
    private DivElement listDiv;

    @Inject
    @DataField("unordered-list")
    private UListElement ul;

    @Inject
    @DataField("text-box")
    private TextBox textBox;

    @Inject
    @DataField("test-table")
    private Grid testTable;
    
    @Inject
    @DataField("text1")
    private LIElement le1;
    
    @Inject
    @DataField("text2")
    private LIElement le2;
    
    @Inject
    @DataField("text3")
    private LIElement le3;
    
    @Inject
    @DataField("text4")
    private LIElement le4;
    
    @Inject
    @DataField("text5")
    private LIElement le5;
    
    private Map<String, String> decoupledText = new HashMap<String, String>();


    public ScenarioEditorViewImpl() {
    }

    @EventHandler("add-button")
    public void addButtonCommand(ClickEvent event) {
        GWT.log("This should print to console!");
        
        // create li element
        Element li = Document.get().createElement("li");

        // create text node for li element
        Node node = Document.get().createTextNode(textBox.getText());

        // add text node to li element
        li.appendChild(node);

        // add li node to unordered list
        ul.appendChild(li);

        // add ul node to div
        listDiv.appendChild(ul);

    }
    
    public void decoupleText(LIElement le) {
        String listElementText = le.getInnerText();
        String newListElementText = "my" + listElementText;
    
        decoupledText.put(listElementText, newListElementText);
    }
    
    @EventHandler("text1")
    public void clickListItemCommand1(ClickEvent event) {
        decoupleText(le1);
        String newText = decoupledText.get(le1.getInnerText());
        textBox.setText(newText);
    }
    
    @EventHandler("text2")
    public void clickListItemCommand2(ClickEvent event) {
        decoupleText(le2);
        String newText = decoupledText.get(le2.getInnerText());
        textBox.setText(newText);
    }
    
    @EventHandler("text3")
    public void clickListItemCommand3(ClickEvent event) {
        decoupleText(le3);
        String newText = decoupledText.get(le3.getInnerText());
        textBox.setText(newText);
    }
    
    @EventHandler("text4")
    public void clickListItemCommand4(ClickEvent event) {
        decoupleText(le4);
        String newText = decoupledText.get(le4.getInnerText());
        textBox.setText(newText);
    }
    
    @EventHandler("text5")
    public void clickListItemCommand5(ClickEvent event) {
        decoupleText(le5);
        String newText = decoupledText.get(le5.getInnerText());
        textBox.setText(newText);
    }
    

    @Override
    public void init() {
        //addButton.innerHTML = translationService.getTranslation(ScenarioEditorConstants.ScenarioEditorViewImplExpressionBuilder);
        addButton.setText(translationService.getTranslation(ScenarioEditorConstants.ScenarioEditorViewImplExpressionBuilder));
        textBox.setText("Enter expression here.");
        
        GWT.log("This should print to console!");
    }

    @Override
    public void setContent() {
    }

}
