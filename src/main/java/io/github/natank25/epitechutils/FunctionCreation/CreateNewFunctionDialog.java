package io.github.natank25.epitechutils.FunctionCreation;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CreateNewFunctionDialog extends DialogWrapper {
	
	@NotNull
	Function function;
	
	public CreateNewFunctionDialog(@NotNull Project project) {
		super(project);
		function = new Function(project.getName());
	}
	
	@Override
	protected @Nullable JComponent createCenterPanel() {
		return null;
	}
	
	public @NotNull Function getFunction(){
		return this.function;
	}
	
	@Override
	protected @Nullable JComponent createTitlePane() {
		return new JLabel("Create a function");
	}
	
	
}
