package io.github.natank25.epitechutils.module;

import com.intellij.execution.RunManager;
import com.intellij.ide.projectView.actions.MarkRootsManager;
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.openapi.GitRepositoryInitializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileUtil;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.DirectoryProjectGeneratorBase;
import com.intellij.platform.ProjectGeneratorPeer;
import com.jetbrains.cidr.cpp.makefile.MakefileUtil;
import com.jetbrains.cidr.cpp.makefile.settings.*;
import git4idea.commands.Git;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import io.github.natank25.epitechutils.icons.EpitechUtilsIcons;
import io.github.natank25.epitechutils.project.EpitechUtilsConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.UnknownSourceRootType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class EpitechDirectoryProjectGenerator extends DirectoryProjectGeneratorBase<EpitechProjectSettings> implements CustomStepProjectGenerator<EpitechProjectSettings> {
	
	private static final Logger log = LoggerFactory.getLogger(EpitechDirectoryProjectGenerator.class);
	
	@Override
	public @NotNull ProjectGeneratorPeer<EpitechProjectSettings> createPeer() {
		return new EpitechProjectGeneratorPeer();
	}
	
	@Override
	public AbstractActionWithPanel createStep(DirectoryProjectGenerator<EpitechProjectSettings> projectGenerator, AbstractNewProjectStep.AbstractCallback<EpitechProjectSettings> callback) {
		return new EpitechProjectSettingsStep(projectGenerator);
	}
	
	@Override
	public @Nullable Icon getLogo() {
		return EpitechUtilsIcons.EpitechIcon_150x150;
	}
	
	@Override
	public @NotNull @NlsContexts.Label String getName() {
		return "Epitech project";
	}
	
	private static @NotNull EpitechUtilsConfiguration getEpitechUtilsConfiguration(@NotNull Project project, @NotNull EpitechProjectSettings settings) {
		EpitechUtilsConfiguration configuration = EpitechUtilsConfiguration.getInstance(project);
		configuration.BINARY_NAME = settings.getBinName();
		return configuration;
	}
	
	@Override
	public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull EpitechProjectSettings settings, @NotNull Module module) {
		EpitechUtilsConfiguration configuration = getEpitechUtilsConfiguration(project, settings);
		
		ApplicationManager.getApplication().runWriteAction(() ->
		{
			createIncludeDirectory(project, baseDir);
			createSourceDirectory(project, baseDir);
			createTestsDirectory(project, baseDir);
			createLibDirectory(project, baseDir);
			EpitechTemplates.createMakefileFileFromTemplate(project, baseDir, configuration.BINARY_NAME);
			EpitechTemplates.createGitignoreFileFromTemplate(project, baseDir, configuration.BINARY_NAME);
		});
		MakefileUtil.linkMakefileProject(project, baseDir, (mkBuildSystemDetector, project1) -> null);
		AbstractVcs vcs = ProjectLevelVcsManager.getInstance(project).findVcsByName("Git");
		if (vcs != null) vcs.enableIntegration();
		ApplicationManager.getApplication().executeOnPooledThread(() -> {
			Objects.requireNonNull(GitRepositoryInitializer.getInstance()).initRepository(project, baseDir, true);
			if (!settings.getGitRepo().isBlank())
				Git.getInstance().addRemote(Objects.requireNonNull(GitRepositoryManager.getInstance(project).getRepositoryForRoot(baseDir)), "origin", settings.getGitRepo());
		});
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
			EpitechTemplates.createCFileFromTemplate(project, EpitechUtilsConfiguration.getInstance(project).BINARY_NAME + ".c", src);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createIncludeDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile include;
		try {
			include = baseDir.createChildDirectory(project, "include");
			MarkRootsManager.modifyRoots(Objects.requireNonNull(ProjectFileIndex.getInstance(project).getModuleForFile(include)), new VirtualFile[]{include}, EpitechDirectoryProjectGenerator::modifyRoots);
			EpitechTemplates.createHeaderFileFromTemplate(project, project.getName() + ".h", include);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void createLibDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile lib;
		try {
			lib = baseDir.createChildDirectory(project, "lib");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	static void modifyRoots(@NotNull VirtualFile vFile, @NotNull ContentEntry entry) {
		entry.addSourceFolder(vFile, JavaSourceRootType.SOURCE);
	}
	
	private void setExecutableInRunConfiguration(Project project, String binary_name) {
		RunManager runManager = RunManager.getInstance(project);
	}
}
