package io.github.natank25.epitechutils.module;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Disposer;
import com.intellij.platform.ProjectGeneratorPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EpitechProjectGeneratorPeer implements ProjectGeneratorPeer<EpitechProjectSettings> {
	private final EpitechNewProjectPanel epitechNewProjectPanel;
	private JComponent component;
	private Runnable checkValid;
	
	public EpitechProjectGeneratorPeer() {
		epitechNewProjectPanel = new EpitechNewProjectPanel(true);
	}
	
	@Override
	public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid) {
		epitechNewProjectPanel.checkValid = checkValid;
		checkValid.run();
		return getComponent();
	}
	
	@Override
	public @NotNull JComponent getComponent() {
		if (component == null) {
			return component = epitechNewProjectPanel.createPanel();
		}
		return component;
	}
	
	@Override
	public void buildUI(@NotNull SettingsStep settingsStep) {
	}
	
	@Override
	public @NotNull EpitechProjectSettings getSettings() {
		return epitechNewProjectPanel.getData();
	}
	
	@Override
	public @Nullable ValidationInfo validate() {
		ValidationInfo info;
		if (null != (info = epitechNewProjectPanel.validate()))
			return info;
		return null;
	}
	
	@Override
	public boolean isBackgroundJobRunning() {
		return false;
	}
	
}
