package io.github.natank25.epitechutils.module;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.ui.Hint;
import com.intellij.ui.HintHint;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.fields.ExtendableTextField;

import javax.swing.*;
import java.awt.*;

public class EpitechNewProjectPanel  {
	private boolean handleGit;
	private final JBCheckBox useLibMy = new JBCheckBox("Use libmy");
	private final TextFieldWithBrowseButton libMyChooser = new TextFieldWithBrowseButton(new ExtendableTextField(), null, null);;
	public Runnable checkValid;
	
	public EpitechNewProjectPanel(boolean handleGit) {
		this.handleGit = handleGit;
		useLibMy.setSelected(true);
		libMyChooser.addBrowseFolderListener("Choose Libmy Folder", "Select the folder containing the whole mylib", null, new LibMyDirectoryChooser());
		useLibMy.addChangeListener(e -> libMyChooser.setEnabled(useLibMy.isSelected()));
		libMyChooser.setText(PathManager.getPluginsPath());
		
		useLibMy.addActionListener(e -> checkValid.run());
		libMyChooser.addActionListener(e -> checkValid.run());
	}
	
	public JComponent createPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(useLibMy, BorderLayout.WEST);
		//TODO panel to group both of them: panel.add(new JLabel("Libmy Directory : "), BorderLayout.AFTER_LAST_LINE);
		panel.add(libMyChooser, BorderLayout.AFTER_LAST_LINE);
		return panel;
	}
	
	public EpitechProjectSettings getData() {
		EpitechProjectSettings settings = new EpitechProjectSettings();
		settings.useLibMy = useLibMy.isSelected();
		settings.libPath = libMyChooser.getText();
		return settings; //TODO: get from current panel
	}
	
	public ValidationInfo validate() {
		ValidationInfo info;
		if (useLibMy.isSelected())
			if (null != (info = LibMyDirectoryChooser.validateDirectory(libMyChooser.getText())))
				return info;
		return null;
	}
}
