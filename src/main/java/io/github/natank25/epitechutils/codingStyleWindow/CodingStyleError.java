package io.github.natank25.epitechutils.codingStyleWindow;

import com.esotericsoftware.kryo.kryo5.util.Null;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class CodingStyleError {
	public static final Map<String, String> CommentList = Map.ofEntries(
			Map.entry("C-A3", "file not ending with a line break (\\n)"),
			Map.entry("C-C1", "conditional block with more than 3 branches, or at a nesting level of 3 or more"),
			Map.entry("C-C2", "abusive ternary operator usage"),
			Map.entry("C-C3", "use of \"goto\" keyword"),
			Map.entry("C-F2", "function name not following the snake_case convention"),
			Map.entry("C-F3", "line of more than 80 columns"),
			Map.entry("C-F4", "line part of a function with more than 20 lines"),
			Map.entry("C-F5", "function with more than 4 parameters"),
			Map.entry("C-F6", "function or function pointer with empty parameter list"),
			Map.entry("C-F7", "structure parameter received by copy"),
			Map.entry("C-F8", "comment inside function"),
			Map.entry("C-F9", "nested function defined"),
			Map.entry("C-G1", "file not starting with correctly formatted Epitech standard header"),
			Map.entry("C-G2", "zero, two, or more empty lines separating implementations of functions"),
			Map.entry("C-G3", "bad indentation of preprocessor directive"),
			Map.entry("C-G4", "global variable used"),
			Map.entry("C-G5", "\"include\" directive used to include file other than a header"),
			Map.entry("C-G6", "bad line ending"),
			Map.entry("C-G7", "trailing space"),
			Map.entry("C-G8", "leading or trailing empty line"),
			Map.entry("C-G10", "use of inline assembly"),
			Map.entry("C-H1", "bad separation between source file and header file"),
			Map.entry("C-H2", "header file not protected against double inclusion"),
			Map.entry("C-H3", "abusive macro usage"),
			Map.entry("C-L1", "multiple statements on the same line"),
			Map.entry("C-L2", "bad indentation at the start of a line"),
			Map.entry("C-L3", "misplaced or missing space(s)"),
			Map.entry("C-L4", "misplaced curly bracket"),
			Map.entry("C-L5", "variable not declared at the beginning of the function or several declarations with the same statement"),
			Map.entry("C-L6", "missing blank line after variable declarations or unnecessary blank line"),
			Map.entry("C-O1", "compiled, temporary or unnecessary file"),
			Map.entry("C-O3", "more than 10 functions or more than 5 non-static functions in the file"),
			Map.entry("C-O4", "file name not following the snake_case convention"),
			Map.entry("C-V1", "identifier incorrectly named"),
			Map.entry("C-V3", "misplaced pointer symbol"),
			Map.entry("C-Z1", "null character used in file"));
	
	String errorCode;
	ErrorSeverity severity;
	@Nullable String comment;
	VirtualFile file;
	int line;
	String fullLine;
	
	public static CodingStyleError createErrorFromLine(@NotNull String line) {
		if (line.isBlank())
			return null;
		CodingStyleError error = new CodingStyleError();
		error.fullLine = line;
		error.errorCode = line.replaceAll(".*(FATAL|MAJOR|MINOR|INFO):", "");
		error.severity = ErrorSeverity.fromString(line.replaceAll("(?!FATAL|MAJOR|MINOR|INFO):C-.\\d", "").replaceAll(".*:\\d*: ", ""));
		error.file = VirtualFileManager.getInstance().findFileByUrl(line.replaceFirst(":\\d*: (FATAL|MAJOR|MINOR|INFO):C-[A-Z]\\d*", ""));
		error.line = Integer.parseInt(line.replaceAll("(?!\\d+:).", ""));
		error.comment = getComment(error.errorCode);
		return error;
	}
	
	private static @Nullable String getComment(String errorCode) {
		return CommentList.get(errorCode);
	}
	
	public enum ErrorSeverity {
		FATAL("FATAL"),
		MAJOR("MAJOR"),
		MINOR("MINOR"),
		INFO("INFO");
		
		public final String string;
		
		ErrorSeverity(String name) {
			this.string = name;
		}
		
		public static ErrorSeverity fromString(String string) {
			for (ErrorSeverity value : ErrorSeverity.values()) {
				if (value.string.equals(string))
					return value;
			}
			return null;
		}
	}
}
