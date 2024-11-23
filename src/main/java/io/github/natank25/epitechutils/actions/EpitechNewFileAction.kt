package io.github.natank25.epitechutils.actions

import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import io.github.natank25.epitechutils.Icons.EpitechLogo
import io.github.natank25.epitechutils.files.EpitechTemplates
import org.jetbrains.annotations.NonNls
import java.util.Properties

class EpitechNewFileAction : CreateFileFromTemplateAction() {
    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("Epitech File")
        builder.addKind("C file", EpitechLogo.EpitechIcon_150x150, EpitechTemplates.C_FILE)
        builder.addKind("Header file", EpitechLogo.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE)
    }

    override fun getActionName(
        directory: PsiDirectory?,
        newName: @NonNls String,
        templateName: @NonNls String?
    ): @NlsContexts.Command String {
        return "Epitech File"
    }

    companion object {
        fun createProperties(project: Project, filename: String?): Properties {
            val properties = FileTemplateManager.getInstance(project).getDefaultProperties()
            properties.setProperty("FILE_NAME", filename)
            return properties
        }
    }
}
