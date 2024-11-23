package io.github.natank25.epitechutils.module;

import J.L.a.V;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.DocumentUtil;
import com.jetbrains.cmake.psi.CMakeFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibMyDirectoryChooser extends FileChooserDescriptor {
	public LibMyDirectoryChooser() {
		super(false, true, false, false, false, false);
		setTitle("Choose Libmy Path");
	}
	
	@Override
	public void validateSelectedFiles(VirtualFile @NotNull [] files) throws Exception {
		super.validateSelectedFiles(files);
		VirtualFile directory = Arrays.stream(files).findFirst().orElseThrow();
		VirtualFile makefile = VfsUtil.refreshAndFindChild(directory, "Makefile");
		if (makefile == null || !makefile.exists() || makefile.isDirectory())
			throw new Exception("No makefile file in selected directory.");
		
		VirtualFile headerFile = directory.findChild("my.h");
		if (headerFile == null || !headerFile.exists() || headerFile.isDirectory())
			throw new Exception("No my.h file in selected directory.");
		
		String makefileContent = VfsUtil.loadText(makefile);
		containsRequiredRulesAndVariable(makefileContent);
	}
	
	public static ValidationInfo validateDirectory(String directory) {
		File dir = new File(directory);
		if (!dir.isDirectory())
			return new ValidationInfo("Selected file is not a directory");
		
		List<File> files = Arrays.asList(Objects.requireNonNull(dir.listFiles()));
		Optional<File> opt_makefile = files.stream().filter(file -> file.getName().equals("Makefile")).findFirst();
		if (opt_makefile.isEmpty())
			return new ValidationInfo("The selected directory does not contain a Makefile");
		
		Optional<File> opt_my_header = files.stream().filter(file -> file.getName().equals("my.h")).findFirst();
		if (opt_my_header.isEmpty())
			return new ValidationInfo("The selected directory does not contain a \"my.h\" file");
		
		File makefile = opt_makefile.get();
		try {
			containsRequiredRulesAndVariable(Files.readString(makefile.toPath()));
		} catch (Exception e) {
			return new ValidationInfo(e.getMessage());
		}
		return null;
	}
	
	private static void containsRequiredRulesAndVariable(@NotNull String makefileContent) throws Exception {
		String libmyVariable = findLibmyVariable(makefileContent);
		
		String contentWithReplacements = makefileContent.replaceAll("\\$\\(" + libmyVariable + "\\)", "libmy.a");
		
		List<String> requiredRules = List.of(
				"^libmy\\.a\\s*:", // Rule for libmy.a or its variable
				"^all:",
				"^clean:",
				"^fclean:",
				"^re:"
		);
		
		for (String rule : requiredRules) {
			Pattern pattern = Pattern.compile(rule, Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(contentWithReplacements);
			if (!matcher.find()) {
				throw new Exception("Rule \"" + pattern.pattern() + "\" not found in Makefile");
			}
		}
	}
	
	private static String findLibmyVariable(@NotNull String makefileContent) {
		Pattern variablePattern = Pattern.compile("^(\\w+)\\s*=\\s*libmy\\.a", Pattern.MULTILINE);
		Matcher variableMatcher = variablePattern.matcher(makefileContent);
		
		return variableMatcher.find() ? variableMatcher.group(1) : "libmy.a";
	}
}
