package io.github.natank25.epitechutils.module;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.lang.IdeLanguageCustomization;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.DirectoryProjectGeneratorBase;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.psi.PsiManager;
import com.jetbrains.cidr.cpp.cmake.model.CMakeGeneratorType;
import com.jetbrains.cidr.cpp.makefile.MakefileUtil;
import com.jetbrains.lang.makefile.MakefileRunConfigurationFactory;
import com.jetbrains.lang.makefile.MakefileRunConfigurationType;
import io.github.natank25.epitechutils.Icons;
import io.github.natank25.epitechutils.actions.EpitechNewFileAction;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Properties;

public class EpitechDirectoryProjectGenerator extends DirectoryProjectGeneratorBase<EpitechProjectSettings> implements CustomStepProjectGenerator<EpitechProjectSettings> {
	
	@Override
	public @NotNull ProjectGeneratorPeer<EpitechProjectSettings> createPeer() {
		return new EpitechProjectGeneratorPeer();
	}
	
	@Override
	public AbstractActionWithPanel createStep(DirectoryProjectGenerator<EpitechProjectSettings> projectGenerator, AbstractNewProjectStep.AbstractCallback<EpitechProjectSettings> callback) {
		return new EpitechProjectSettingsStep(projectGenerator);
	}
	
	@Override
	public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull EpitechProjectSettings settings, @NotNull Module module) {
		ApplicationManager.getApplication().runWriteAction(() ->
		{
			createIncludeDirectory(project, baseDir);
			createSourceDirectory(project, baseDir);
			createTestsDirectory(project, baseDir);
			EpitechTemplates.createMakefileFileFromTemplate(project, baseDir, settings.getBinName());
		});
		MakefileUtil.linkMakefileProject(project, baseDir, (mkBuildSystemDetector, project1) -> null);
		generateRunConfiguration(project, settings.getBinName());
	}
	
	private static void createTestsDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile tests;
		try {
			tests = baseDir.createChildDirectory(project, "tests");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createSourceDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile src;
		try {
			src = baseDir.createChildDirectory(project, "src");
			EpitechTemplates.createCFileFromTemplate(project, "main.c", src);
			EpitechTemplates.createCFileFromTemplate(project, project.getName() + ".c", src);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createIncludeDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile include;
		try {
			include = baseDir.createChildDirectory(project, "include");
			EpitechTemplates.createHeaderFileFromTemplate(project, project.getName() + ".h", include);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void generateRunConfiguration(Project project, String binary_name){
		RunManager runManager = RunManager.getInstance(project);
		MakefileRunConfigurationFactory configurationFactory = new MakefileRunConfigurationFactory(MakefileRunConfigurationType.getInstance());
		RunnerAndConfigurationSettings runBin = runManager.createConfiguration("Run ./" + binary_name, configurationFactory);
		runManager.addConfiguration(runBin);
		
	}
	
	@Override
	public @Nullable Icon getLogo() {
		return Icons.EpitechLogo.EpitechIcon_150x150;
	}
	
	@Override
	public @NotNull @NlsContexts.Label String getName() {
		return "Epitech project";
	}
	
	
}
