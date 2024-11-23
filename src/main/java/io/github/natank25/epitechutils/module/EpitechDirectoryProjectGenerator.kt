package io.github.natank25.epitechutils.module

import com.intellij.execution.RunManager
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator
import com.intellij.openapi.actionSystem.ex.ActionUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel
import com.intellij.platform.DirectoryProjectGenerator
import com.intellij.platform.DirectoryProjectGeneratorBase
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.util.concurrency.annotations.RequiresEdt
import com.jetbrains.cidr.cpp.execution.external.build.runners.CLionExternalProjectTaskRunner
import com.jetbrains.cidr.cpp.makefile.MakefileWorkspace
import com.jetbrains.cidr.cpp.makefile.actions.MakefileCleanAndRefreshProjectAction
import com.jetbrains.cidr.cpp.makefile.project.resolver.preconfigure.api.MkBuildSystemDetector
import com.jetbrains.lang.makefile.MakefileProjectSettings
import com.jetbrains.lang.makefile.MakefileRunConfigurationFactory
import com.jetbrains.lang.makefile.MakefileRunConfigurationType
import io.github.natank25.epitechutils.Icons.EpitechLogo
import io.github.natank25.epitechutils.files.EpitechTemplates
import java.io.IOException
import java.lang.RuntimeException
import javax.swing.Icon

class EpitechDirectoryProjectGenerator :
    DirectoryProjectGeneratorBase<EpitechProjectSettings>(),
    CustomStepProjectGenerator<EpitechProjectSettings> {

    override fun createPeer(): ProjectGeneratorPeer<EpitechProjectSettings> {
        return EpitechProjectGeneratorPeer()
    }

    override fun createStep(
        projectGenerator: DirectoryProjectGenerator<EpitechProjectSettings?>?,
        callback: AbstractCallback<EpitechProjectSettings>?
    ): AbstractActionWithPanel {
        return EpitechProjectSettingsStep(projectGenerator!!, callback)
    }

    override fun generateProject(
        project: Project,
        baseDir: VirtualFile,
        settings: EpitechProjectSettings,
        module: Module
    ) {
        ApplicationManager.getApplication().runWriteAction(Runnable {
            var include: VirtualFile?
            var src: VirtualFile?
            var tests: VirtualFile?

            try {
                include = baseDir.createChildDirectory(project, "include")
                EpitechTemplates.createHeaderFileFromTemplate(project, project.getName() + ".h", include)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            try {
                src = baseDir.createChildDirectory(project, "src")
                EpitechTemplates.createCFileFromTemplate(project, "main.c", src)
                EpitechTemplates.createCFileFromTemplate(project, project.getName() + ".c", src)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            try {
                tests = baseDir.createChildDirectory(project, "tests")
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            EpitechTemplates.createMakefileFileFromTemplate(project, baseDir, settings.binName)
        })
        generateRunConfiguration(project, settings.binName)
    }

    @JvmStatic
    @RequiresEdt
    internal fun linkMakefileProject(
        project: Project,
        projectDirectory: VirtualFile,
        afterBuildSystemDetected: (MkBuildSystemDetector, Project) -> Unit
    ) {
        require(projectDirectory.isDirectory) {
            "$projectDirectory is not a directory"
        }

        val settings = MakefileProjectSettings.default().apply {
            externalProjectPath = projectDirectory.path
        }

        val detector = MkBuildSystemDetector.EXTENSIONS
            .firstOrNull { it.isApplicableTo(MkVirtualFile(projectDirectory)) }

        detector?.let {
            INSTANCE.fill(it, settings)
            afterBuildSystemDetected(it, project)
        }

        val workspace = MakefileWorkspace.getInstance(project)
        CLionExternalProjectTaskRunner.linkExternalProject(project, ID, settings, workspace)
    }

    private fun generateRunConfiguration(project: Project, binary_name: String?) {
        val runManager = RunManager.getInstance(project)
        val configurationFactory = MakefileRunConfigurationFactory(MakefileRunConfigurationType.instance)
        val runBin = runManager.createConfiguration("Run ./" + binary_name, configurationFactory)
        runManager.addConfiguration(runBin)
    }

    override fun getLogo(): Icon? {
        return EpitechLogo.EpitechIcon_150x150
    }

    override fun getName(): @NlsContexts.Label String {
        return "Epitech project"
    }
}
