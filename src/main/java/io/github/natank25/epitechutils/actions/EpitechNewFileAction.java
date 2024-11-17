package io.github.natank25.epitechutils.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import io.github.natank25.epitechutils.Icons;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

public class EpitechNewFileAction extends CreateFileFromTemplateAction {
	@Override
	protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
		
		builder.setTitle("Epitech File");
		builder.addKind("C File", Icons.EpitechLogo.EpitechIcon_150x150, EpitechTemplates.C_FILE);
		builder.addKind("Header File", Icons.EpitechLogo.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE);
	}
	
	@Override
	protected @NlsContexts.Command String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
		return "Epitech File";
	}
	
	public static Properties createProperties(Project project, String filename){
		Properties properties = FileTemplateManager.getInstance(project).getDefaultProperties();
		properties.setProperty("FILE_NAME", filename);
		return properties;
	}
	
}
