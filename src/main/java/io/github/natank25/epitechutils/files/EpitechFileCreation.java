package io.github.natank25.epitechutils.files;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


public class EpitechFileCreation extends CreateFileFromTemplateAction implements DumbAware {
	
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
	
}
