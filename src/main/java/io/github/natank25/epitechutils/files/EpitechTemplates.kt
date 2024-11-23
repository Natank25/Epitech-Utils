package io.github.natank25.epitechutils.files

import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import io.github.natank25.epitechutils.actions.EpitechNewFileAction
import java.lang.Exception
import java.lang.RuntimeException
import java.util.Properties

object EpitechTemplates {
    const val C_FILE: String = "Epitech C File.c"
    const val HEADER_FILE: String = "Epitech Header File.h"
    const val MAKEFILE: String = "Makefile"

    @JvmOverloads
    fun createFromTemplate(
        project: Project,
        fileName: String?,
        dir: PsiDirectory,
        template: Templates,
        properties: Properties? = null
    ): PsiElement {
        val defaultProperties: Properties = EpitechNewFileAction.Companion.createProperties(project, fileName)
        if (properties != null) defaultProperties.putAll(properties)
        try {
            return FileTemplateUtil.createFromTemplate(
                FileTemplateManager.getInstance(project).getTemplate(template.toString()),
                fileName,
                properties,
                dir
            )
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun createFromTemplate(project: Project, fileName: String?, dir: VirtualFile, template: Templates): PsiElement {
        return this.createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), template)
    }

    fun createCFileFromTemplate(project: Project, fileName: String?, dir: VirtualFile): PsiElement {
        return createFromTemplate(
            project,
            fileName,
            PsiManager.getInstance(project).findDirectory(dir),
            Templates.C_FILE,
            null
        )
    }

    fun createHeaderFileFromTemplate(project: Project, fileName: String?, dir: VirtualFile): PsiElement {
        return createFromTemplate(
            project,
            fileName,
            PsiManager.getInstance(project).findDirectory(dir),
            Templates.HEADER_FILE
        )
    }

    fun createMakefileFileFromTemplate(project: Project, dir: VirtualFile, binary_name: String?): PsiElement {
        val makefileProperties = Properties()
        makefileProperties.setProperty("BIN_NAME", binary_name)
        return createFromTemplate(
            project,
            "Makefile",
            PsiManager.getInstance(project).findDirectory(dir)!!,
            Templates.MAKEFILE,
            makefileProperties
        )
    }

    enum class Templates(name: String) {
        C_FILE(EpitechTemplates.C_FILE),
        HEADER_FILE(EpitechTemplates.HEADER_FILE),
        MAKEFILE(EpitechTemplates.MAKEFILE);

        private val filename: String?

        init {
            this.filename = name
        }


        override fun toString(): String {
            return this.filename!!
        }
    }
}
