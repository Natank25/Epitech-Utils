package io.github.natank25.epitechutils.files;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import io.github.natank25.epitechutils.actions.EpitechNewFileAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Properties;

public class EpitechTemplates {
	public static final String C_FILE = "Epitech C File.c";
	public static final String HEADER_FILE = "Epitech Header File.h";
	public static final String MAKEFILE = "Makefile";
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, PsiDirectory dir, Templates template, @Nullable Properties properties){
		Properties defaultProperties = EpitechNewFileAction.createProperties(project, fileName);
		if (properties != null)
			defaultProperties.putAll(properties);
		try {
			return FileTemplateUtil.createFromTemplate(
					FileTemplateManager.getInstance(project).getTemplate(template.toString()),
					fileName,
					properties,
					dir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, PsiDirectory dir, Templates template){
		return createFromTemplate(project, fileName,dir, template, null);
	}
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, VirtualFile dir, Templates template){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), template);
	}
	
	public static @NotNull PsiElement createCFileFromTemplate(Project project, String fileName, VirtualFile dir){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), Templates.C_FILE);
	}
	
	public static @NotNull PsiElement createHeaderFileFromTemplate(Project project, String fileName, VirtualFile dir){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), Templates.HEADER_FILE);
	}
	
	public static @NotNull PsiElement createMakefileFileFromTemplate(Project project, VirtualFile dir, String binary_name){
		Properties makefileProperties = new Properties();
		makefileProperties.setProperty("BIN_NAME", binary_name);
		return createFromTemplate(project, "Makefile", PsiManager.getInstance(project).findDirectory(dir), Templates.MAKEFILE, makefileProperties);
	}
	
	public enum Templates{
		C_FILE(EpitechTemplates.C_FILE),
		HEADER_FILE(EpitechTemplates.HEADER_FILE),
		MAKEFILE(EpitechTemplates.MAKEFILE);
		
		private final String filename;
		
		Templates(final String name){
			this.filename = name;
		}
		
		
		@Override
		public String toString() {
			return this.filename;
		}
	}
}
