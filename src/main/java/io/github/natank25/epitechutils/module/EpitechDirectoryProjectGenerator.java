package io.github.natank25.epitechutils.module;

import com.intellij.docker.DockerCloudType;
import com.intellij.docker.DockerDeploymentConfiguration;
import com.intellij.docker.DockerRunConfigurationCreator;
import com.intellij.docker.agent.DockerAgentDeploymentConfig;
import com.intellij.docker.agent.settings.DockerVolumeBindingImpl;
import com.intellij.docker.deploymentSource.DockerImageDeploymentSourceType;
import com.intellij.execution.DefaultExecutionTarget;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.impl.RunConfigurationBeforeRunProvider;
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.openapi.GitRepositoryInitializer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.DirectoryProjectGeneratorBase;
import com.intellij.platform.ProjectGeneratorPeer;
import com.intellij.remoteServer.ServerType;
import com.intellij.remoteServer.configuration.RemoteServer;
import com.intellij.remoteServer.configuration.RemoteServersManager;
import com.intellij.remoteServer.configuration.deployment.DeploymentSource;
import com.intellij.sh.run.ShConfigurationType;
import com.intellij.sh.run.ShRunConfiguration;
import com.jetbrains.cidr.cpp.makefile.MakefileUtil;
import com.jetbrains.cidr.project.CidrRootConfiguration;
import git4idea.commands.Git;
import git4idea.repo.GitRepositoryManager;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import io.github.natank25.epitechutils.icons.EpitechUtilsIcons;
import io.github.natank25.epitechutils.project.EpitechUtilsConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class EpitechDirectoryProjectGenerator extends DirectoryProjectGeneratorBase<EpitechProjectSettings> implements CustomStepProjectGenerator<EpitechProjectSettings> {
	
	private static final Logger log = LoggerFactory.getLogger(EpitechDirectoryProjectGenerator.class);
	
	private static @NotNull EpitechUtilsConfiguration getEpitechUtilsConfiguration(@NotNull Project project, @NotNull EpitechProjectSettings settings) {
		EpitechUtilsConfiguration configuration = EpitechUtilsConfiguration.getInstance(project);
		configuration.BINARY_NAME = settings.getBinName();
		configuration.PROJECT_NAME = settings.getProjectName();
		return configuration;
	}
	
	private static @NotNull RunnerAndConfigurationSettings createDockerRunConfiguration(@NotNull Project project) {
		DeploymentSource adaptedSourceType = DockerImageDeploymentSourceType.getInstance().getSingletonSource();
		
		DockerDeploymentConfiguration deploymentConfig = getDockerDeploymentConfiguration(project);
		
		RemoteServer<?> remoteServer = RemoteServersManager.getInstance().createServer(DockerCloudType.getInstance());
		
		DockerRunConfigurationCreator dockerRunConfigurationCreator = new DockerRunConfigurationCreator(project);
		RunnerAndConfigurationSettings configurationSettings = dockerRunConfigurationCreator.createConfiguration(adaptedSourceType, deploymentConfig, remoteServer);
		configurationSettings.setName("Generate Coding Style Report");
		return configurationSettings;
	}
	
	private static @NotNull DockerDeploymentConfiguration getDockerDeploymentConfiguration(@NotNull Project project) {
		List<DockerVolumeBindingImpl> volumeBindings = new ArrayList<>();
		DockerVolumeBindingImpl volumeBinding1 = new DockerVolumeBindingImpl("/mnt/delivery", project.getBasePath(), false);
		DockerVolumeBindingImpl volumeBinding2 = new DockerVolumeBindingImpl("/mnt/reports", project.getBasePath(), false);
		volumeBindings.add(volumeBinding1);
		volumeBindings.add(volumeBinding2);
		
		DockerDeploymentConfiguration deploymentConfig = new DockerDeploymentConfiguration();
		deploymentConfig.setTheOnlyImageTag("ghcr.io/epitech/coding-style-checker:latest");
		deploymentConfig.setPullImage(DockerAgentDeploymentConfig.PullImage.ALWAYS);
		deploymentConfig.setCommand("\"/mnt/delivery\" \"/mnt/reports\"");
		deploymentConfig.setVolumeBindings(volumeBindings);
		deploymentConfig.setRunCliOptions("--rm --security-opt \"label:disable\" -i");
		return deploymentConfig;
	}
	
	private static void initialize_vcs(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull EpitechProjectSettings settings) {
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
			CidrRootConfiguration.getInstance(project).addSourceRoot(src);
			EpitechTemplates.createCFileFromTemplate(project, "main.c", src);
			EpitechTemplates.createCFileFromTemplate(project, EpitechUtilsConfiguration.getInstance(project).BINARY_NAME + ".c", src);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static void createIncludeDirectory(@NotNull Project project, @NotNull VirtualFile baseDir, String binary_name) {
		VirtualFile include;
		try {
			include = baseDir.createChildDirectory(project, "include");
			CidrRootConfiguration.getInstance(project).addSourceRoot(include);
			EpitechTemplates.createHeaderFileFromTemplate(project, binary_name + ".h", include);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
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
		EpitechUtilsConfiguration configuration = getEpitechUtilsConfiguration(project, settings);
		
		createFilesAndFolders(project, baseDir, configuration);
		initializeMakefile(project, baseDir, configuration);
		initialize_vcs(project, baseDir, settings);
		createCodingStyleRunConfiguration(project);
	}
	
	@Override
	public @Nullable Icon getLogo() {
		return EpitechUtilsIcons.EpitechIcon_150x150;
	}
	
	@Override
	public @NotNull @NlsContexts.Label String getName() {
		return "Epitech project";
	}
	
	private void createLibDirectory(@NotNull Project project, @NotNull VirtualFile baseDir) {
		VirtualFile lib;
		try {
			lib = baseDir.createChildDirectory(project, "lib");
			CidrRootConfiguration.getInstance(project).addLibraryRoot(lib);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static void createCodingStyleRunConfiguration(@NotNull Project project) {
		RunManager runManager = RunManager.getInstance(project);
		RunnerAndConfigurationSettings dockerRunConfiguration = createDockerRunConfiguration(project);
		//RunnerAndConfigurationSettings shConfigurationSettings = runManager.createConfiguration(shRunConfiguration, ShConfigurationType.getInstance());
		//runManager.addConfiguration(shConfigurationSettings);
		runManager.addConfiguration(dockerRunConfiguration);
		
		//RunConfigurationBeforeRunProvider.RunConfigurableBeforeRunTask task = Objects.requireNonNull(RunConfigurationBeforeRunProvider.getProvider(project, RunConfigurationBeforeRunProvider.ID)).createTask(shRunConfiguration);
		//if (task == null) return;
		//task.setSettingsWithTarget(shConfigurationSettings, DefaultExecutionTarget.INSTANCE);
		//runManager.getConfigurationsList(dockerRunConfiguration.getType()).forEach(runConfiguration -> runConfiguration.setBeforeRunTasks(new ArrayList<>(Collections.singletonList(task))));
	}
	
	private void createFilesAndFolders(@NotNull Project project, @NotNull VirtualFile baseDir, EpitechUtilsConfiguration configuration) {
		ApplicationManager.getApplication().runWriteAction(() ->
		{
			createIncludeDirectory(project, baseDir, configuration.BINARY_NAME);
			createSourceDirectory(project, baseDir);
			createTestsDirectory(project, baseDir);
			createLibDirectory(project, baseDir);
			EpitechTemplates.createMakefileFileFromTemplate(project, baseDir, configuration.BINARY_NAME);
			EpitechTemplates.createGitignoreFileFromTemplate(project, baseDir, configuration.BINARY_NAME);
		});
	}
	
	private void initializeMakefile(@NotNull Project project, @NotNull VirtualFile baseDir, EpitechUtilsConfiguration configuration) {
		MakefileUtil.linkMakefileProject(project, baseDir, (mkBuildSystemDetector, project1) -> null);
		// RunConfigWatcher.waitForRunConfigurations(project, () -> setExecutableInRunConfiguration(project, configuration.BINARY_NAME));
	}
	
	private void setExecutableInRunConfiguration(Project project, String binary_name) {
//		RunManager runManager = RunManager.getInstance(project);
//		runManager.getConfigurationsList(new CLionNativeAppRunConfigurationType()).forEach(runConfiguration -> {
//			System.out.println("1" + (runConfiguration instanceof CLionNativeAppRunConfiguration));
//			System.out.println(runConfiguration.getType());
//			MakefileRunConfiguration runConfiguration1 = (MakefileRunConfiguration) runConfiguration;
//			System.out.println("2");
//			runConfiguration1.setFilename("FILENAME");
//			System.out.println("3");
//			runConfiguration1.setArguments("ARGS");
//			System.out.println("4");
//			runConfiguration1.setWorkingDirectory("WORKING DIR");
//			System.out.println("5");
//		});
	}
	
	public static class RunConfigWatcher {
		public static void waitForRunConfigurations(Project project, Runnable on_run_config_ready) {
			ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			AtomicInteger count = new AtomicInteger();
			executor.scheduleWithFixedDelay(() -> {
				if (count.get() >= 60 || areRunConfigurationsReady(project)) {
					executor.shutdown();
					on_run_config_ready.run();
				}
				count.addAndGet(1);
			}, 0, 1, TimeUnit.SECONDS);
		}
		
		private static boolean areRunConfigurationsReady(Project project) {
			RunManager runManager = RunManager.getInstance(project);
			for (RunConfiguration configuration : runManager.getAllConfigurationsList()) {
				if (configuration.getName().contains("all")) {
					return true;
				}
			}
			return false;
		}
	}
}
