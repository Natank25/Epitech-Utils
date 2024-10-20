package io.github.natank25.epitechutils.module;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBCheckBox;

public class EpitechNewProjectPanel implements Disposable {
	private boolean handleGit;
	private JBCheckBox git = new JBCheckBox();
	
	public EpitechNewProjectPanel(boolean handleGit) {
		this.handleGit = handleGit;
	}
	
	@Override
	public void dispose() {
	
	}
	
	public EpitechProjectSettings getData() {
		return new EpitechProjectSettings(); //TODO: get from current panel
	}
}
