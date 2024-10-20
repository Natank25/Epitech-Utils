package io.github.natank25.epitechutils.module;/*
 * Copyright 2023-2024 FalsePattern
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.ide.wizard.GitNewProjectWizardData;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.project.Project;
import com.intellij.ui.dsl.builder.AlignX;
import com.intellij.ui.dsl.builder.Panel;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import javax.swing.JLabel;


public class EpitechNewProjectWizard implements LanguageGeneratorNewProjectWizard {
    @NotNull
    @Override
    public String getName() {
        return "Epitech Project Wizard";
    }

    @Override
    public int getOrdinal() {
        return 900;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return Icons.EpitechIcon;
    }

    @NotNull
    @Override
    public NewProjectWizardStep createStep(@NotNull NewProjectWizardStep parent) {
        return new EpitechNewProjectWizardStep(parent);
    }

    private static class EpitechNewProjectWizardStep extends AbstractNewProjectWizardStep {

        public EpitechNewProjectWizardStep(@NotNull NewProjectWizardStep parentStep) {
            super(parentStep);
        }

        @Override
        public void setupUI(@NotNull Panel builder) {
            builder.row((JLabel) null, (row) -> {
                row.checkBox("CHECKBOX");
                return null;
            });
        }

        @Override
        public void setupProject(@NotNull Project project) {
            EpitechModuleBuilder builder = new EpitechModuleBuilder();
            var gitData = GitNewProjectWizardData.Companion.getGitData(this);
            builder.forceGitignore = gitData != null && gitData.getGit();
            builder.commit(project);
        }
    }
}
