package io.github.natank25.epitechutils.module;

import com.intellij.facet.ui.ValidationResult;
import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.CustomStepProjectGenerator;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.impl.welcomeScreen.AbstractActionWithPanel;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.platform.ProjectGeneratorPeer;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EpitechDirectoryProjectGenerator implements DirectoryProjectGenerator<EpitechProjectSettings>, CustomStepProjectGenerator<EpitechProjectSettings> {
	
	@Override
	public AbstractActionWithPanel createStep(DirectoryProjectGenerator<EpitechProjectSettings> projectGenerator, AbstractNewProjectStep.AbstractCallback<EpitechProjectSettings> callback) {
		return new EpitechProjectSettingsStep(projectGenerator);
	}
	
	@Override
	public @NotNull @NlsContexts.Label String getName() {
		return "Epitech Directory Project";
	}
	
	@Override
	public @NotNull ProjectGeneratorPeer<EpitechProjectSettings> createPeer() {
		return new EpitechProjectGeneratorPeer();
	}
	
	@Override
	public @Nullable Icon getLogo() {
		return Icons.EpitechIcon;
	}
	
	@Override
	public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir, @NotNull EpitechProjectSettings settings, @NotNull Module module) {
	
	}
	
	
	@Override
	public @NotNull ValidationResult validate(@NotNull String baseDirPath) {
		return ValidationResult.OK;
	}
	
	
	
}
