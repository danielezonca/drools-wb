/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenario.backend.server.util;

import org.drools.workbench.screens.scenario.model.ScenarioModel;
import org.kie.soup.project.datamodel.commons.packages.PackageNameWriter;

/**
 * This class persists the rule model to DRL and back
 */
public class ScenarioPersistence {

    private static final ScenarioPersistence INSTANCE = new ScenarioPersistence();

    protected ScenarioPersistence() {
    }

    public static ScenarioPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(final ScenarioModel model) {
        final StringBuilder sb = new StringBuilder();

        PackageNameWriter.write(sb,
                                model);

        // TODO add serializable mechanism
        return sb.toString();
    }

    public ScenarioModel unmarshal(final String content) {
        // TODO add deserialization logic
        final ScenarioModel model = new ScenarioModel();
        return model;
    }
}
