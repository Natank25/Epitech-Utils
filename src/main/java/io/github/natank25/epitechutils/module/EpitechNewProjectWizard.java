package io.github.natank25.epitechutils.module;

import com.intellij.ide.wizard.AbstractNewProjectWizardStep;
import com.intellij.ide.wizard.GitNewProjectWizardData;
import com.intellij.ide.wizard.NewProjectWizardStep;
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard;
import com.intellij.openapi.project.Project;
import com.intellij.ui.dsl.builder.Panel;
import io.github.natank25.epitechutils.icons.EpitechUtilsIcons;
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
        return EpitechUtilsIcons.EpitechIcon_150x150;
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
                row.checkBox("EpitechNewProjectWizard:64");
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
