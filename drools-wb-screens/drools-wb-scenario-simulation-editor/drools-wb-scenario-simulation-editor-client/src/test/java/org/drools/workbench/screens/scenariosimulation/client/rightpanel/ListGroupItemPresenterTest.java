/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.DivElement;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ListGroupItemPresenterTest extends AbstractRightPanelTest {

    private ListGroupItemPresenter listGroupItemPresenter;

    @Mock
    private ListGroupItemView listGroupItemViewMock;

    @Mock
    private DivElement divElementMock;

    @Mock
    private FieldItemPresenter fieldItemPresenterMock;

    @Mock
    private Map<String, ListGroupItemView> listGroupItemViewMapMock;

    @Mock
    private List<ListGroupItemView> listGroupItemViewValuesMock;

    @Before
    public void setup() {
        super.setup();
        when(viewsProviderMock.getListGroupItemView()).thenReturn(listGroupItemViewMock);
        when(listGroupItemViewMock.getListGroupItem()).thenReturn(divElementMock);
        when(listGroupItemViewMock.getListGroupExpansion()).thenReturn(divElementMock);
        when(listGroupItemViewMapMock.values()).thenReturn(listGroupItemViewValuesMock);
        this.listGroupItemPresenter = spy(new ListGroupItemPresenter() {
            {
                listGroupItemViewMap = listGroupItemViewMapMock;
                fieldItemPresenter = fieldItemPresenterMock;
                viewsProvider = viewsProviderMock;
            }
        });
    }

    @Test
    public void getDivElementByFactModel() {
        DivElement retrieved = listGroupItemPresenter.getDivElement(FACT_NAME, FACT_MODEL_TREE);
        assertNotNull(retrieved);
        assertEquals(divElementMock, retrieved);
        verify(listGroupItemPresenter, times(1)).commonGetListGroupItemView(eq(""), eq(FACT_NAME), eq(false));
        verify(listGroupItemPresenter, times(1)).populateListGroupItemView(eq(listGroupItemViewMock), eq(""), eq(FACT_NAME), eq(FACT_MODEL_TREE));
    }

    @Test
    public void getDivElementByStrings() {
        DivElement retrieved = listGroupItemPresenter.getDivElement(FULL_PACKAGE, VALUE, VALUE_CLASS_NAME);
        assertNotNull(retrieved);
        assertEquals(divElementMock, retrieved);
        verify(listGroupItemPresenter, times(1)).commonGetListGroupItemView(eq(FULL_PACKAGE), eq(VALUE), eq(true));
        verify(listGroupItemPresenter, times(1)).populateListGroupItemView(eq(listGroupItemViewMock), eq(VALUE), eq(VALUE_CLASS_NAME));
    }

    @Test
    public void onToggleRowExpansionDisabled() {
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        verify(listGroupItemViewValuesMock, never()).contains(eq(listGroupItemViewMock));
        verify(listGroupItemViewMock, never()).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(listGroupItemViewValuesMock, never()).contains(eq(listGroupItemViewMock));
        verify(listGroupItemViewMock, never()).expandRow();
    }

    @Test
    public void onToggleRowExpansionWithoutFactName() {
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        //verify(listGroupItemViewMock, times(1)).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        //verify(listGroupItemViewMock, times(1)).expandRow();
    }

    @Test
    public void onToggleRowExpansionWithFactName() {
        listGroupItemPresenter.enable(FACT_NAME);
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, true);
        verify(listGroupItemViewMock, times(1)).closeRow();
        reset(listGroupItemViewMapMock);
        when(listGroupItemViewValuesMock.contains(listGroupItemViewMock)).thenReturn(true);
        reset(listGroupItemViewMock);
        listGroupItemPresenter.onToggleRowExpansion(listGroupItemViewMock, false);
        verify(listGroupItemViewMock, times(1)).expandRow();
    }

    @Test
    public void populateListGroupItemView() {
        listGroupItemPresenter.populateListGroupItemView(listGroupItemViewMock, "", FACT_NAME, FACT_MODEL_TREE);
        verify(listGroupItemViewMock, times(1)).setFactName(eq(FACT_NAME));
        Map<String, String> simpleProperties = FACT_MODEL_TREE.getSimpleProperties();
        for (String key : simpleProperties.keySet()) {
            String value = simpleProperties.get(key);
            verify(fieldItemPresenterMock, times(1)).getLIElement(eq(FACT_NAME), eq(FACT_NAME), eq(key), eq(value));
        }
        verify(listGroupItemViewMock, times(simpleProperties.size())).addFactField(anyObject());
        reset(listGroupItemViewMock);
        Map<String, String> expandableProperties = FACT_MODEL_TREE.getExpandableProperties();
        for (String key : expandableProperties.keySet()) {
            String value = expandableProperties.get(key);
            verify(listGroupItemPresenter, times(1)).getDivElement(eq(""), eq(key), eq(value));
        }
        verify(listGroupItemViewMock, times(expandableProperties.size())).addExpandableFactField(anyObject());
    }
}