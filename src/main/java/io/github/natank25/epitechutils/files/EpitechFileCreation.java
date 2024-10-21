package io.github.natank25.epitechutils.files;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.actions.CreateTemplateInPackageAction;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.actions.CreateFromTemplateAction;
import com.intellij.ide.fileTemplates.actions.CreateFromTemplateActionBase;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.cidr.lang.daemon.clang.clangd.language.ClangPsiFile;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import javax.swing.*;
import java.util.Set;


public class EpitechFileCreation extends CreateFileFromTemplateAction implements DumbAware {
	
	@Override
	protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
		builder.setTitle("Epitech File");
		builder.addKind("C File", Icons.EpitechLogo.EpitechIcon_32x32, EpitechTemplates.C_FILE);
		builder.addKind("Header File", Icons.EpitechLogo.EpitechIcon_32x32, EpitechTemplates.HEADER_FILE);
	}
	
	@Override
	protected @NlsContexts.Command String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
		return "Epitech File";
	}
	
}
