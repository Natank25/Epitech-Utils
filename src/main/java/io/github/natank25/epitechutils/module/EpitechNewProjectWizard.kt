package io.github.natank25.epitechutils.module

import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GitNewProjectWizardData
import com.intellij.ide.wizard.GitNewProjectWizardData.Companion.gitData
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.language.LanguageGeneratorNewProjectWizard
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import io.github.natank25.epitechutils.Icons.EpitechLogo
import io.github.natank25.epitechutils.module.EpitechNewProjectWizard.EpitechNewProjectWizardStep
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class EpitechNewProjectWizard : LanguageGeneratorNewProjectWizard {
    override val name: String
        get() = "Epitech Project Wizard"

    override val ordinal: Int
        get() = 900

    override val icon: Icon
        get() = EpitechLogo.EpitechIcon_150x150

    override fun createStep(parent: NewProjectWizardStep): NewProjectWizardStep {
        return EpitechNewProjectWizardStep(parent)
    }

    private class EpitechNewProjectWizardStep(parentStep: NewProjectWizardStep) :
        AbstractNewProjectWizardStep(parentStep) {
        override fun setupUI(builder: Panel) {
            builder.row() {
                checkBox("EpitechNewProjectWizard")
            }
        }

        override fun setupProject(project: Project) {
            val builder = EpitechModuleBuilder()
            val gitData = gitData
            builder.forceGitignore = gitData != null && gitData.git
            builder.commit(project)
        }
    }
}
