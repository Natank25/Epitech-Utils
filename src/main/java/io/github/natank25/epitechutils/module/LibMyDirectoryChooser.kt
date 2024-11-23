package io.github.natank25.epitechutils.module

import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.io.File
import java.lang.Exception
import java.nio.file.Files
import java.util.Arrays
import java.util.Objects
import java.util.Optional
import java.util.regex.Pattern

class LibMyDirectoryChooser : FileChooserDescriptor(false, true, false, false, false, false) {
    init {
        setTitle("Choose Libmy Path")
    }

    @Throws(Exception::class)
    override fun validateSelectedFiles(files: Array<VirtualFile?>) {
        super.validateSelectedFiles(files)
        val directory = Arrays.stream<VirtualFile>(files).findFirst().orElseThrow()
        val makefile = VfsUtil.refreshAndFindChild(directory, "Makefile")
        if (makefile == null || !makefile.exists() || makefile.isDirectory()) throw Exception("No makefile file in selected directory.")

        val headerFile = directory.findChild("my.h")
        if (headerFile == null || !headerFile.exists() || headerFile.isDirectory()) throw Exception("No my.h file in selected directory.")

        val makefileContent = VfsUtil.loadText(makefile)
        LibMyDirectoryChooser.Companion.containsRequiredRulesAndVariable(makefileContent)
    }

    companion object {
        fun validateDirectory(directory: String): ValidationInfo? {
            val dir = File(directory)
            if (!dir.isDirectory()) return ValidationInfo("Selected file is not a directory")

            val files = Arrays.asList<File?>(*Objects.requireNonNull<Array<File?>?>(dir.listFiles()))
            val opt_makefile: Optional<File> =
                files.stream().filter { file: File -> file.getName() == "Makefile" }.findFirst()
            if (opt_makefile.isEmpty()) return ValidationInfo("The selected directory does not contain a Makefile")

            val opt_my_header = files.stream().filter { file: File? -> file!!.getName() == "my.h" }.findFirst()
            if (opt_my_header.isEmpty()) return ValidationInfo("The selected directory does not contain a \"my.h\" file")

            val makefile = opt_makefile.get()
            try {
                LibMyDirectoryChooser.Companion.containsRequiredRulesAndVariable(Files.readString(makefile.toPath()))
            } catch (e: Exception) {
                return ValidationInfo(e.message!!)
            }
            return null
        }

        @Throws(Exception::class)
        private fun containsRequiredRulesAndVariable(makefileContent: String) {
            val libmyVariable = LibMyDirectoryChooser.Companion.findLibmyVariable(makefileContent)

            val contentWithReplacements =
                makefileContent.replace(("\\$\\(" + libmyVariable + "\\)").toRegex(), "libmy.a")

            val requiredRules: MutableList<String> = mutableListOf<String>(
                "^libmy\\.a\\s*:",  // Rule for libmy.a or its variable
                "^all:",
                "^clean:",
                "^fclean:",
                "^re:"
            )

            for (rule in requiredRules) {
                val pattern = Pattern.compile(rule, Pattern.MULTILINE)
                val matcher = pattern.matcher(contentWithReplacements)
                if (!matcher.find()) {
                    throw Exception("Rule \"" + pattern.pattern() + "\" not found in Makefile")
                }
            }
        }

        private fun findLibmyVariable(makefileContent: String): String? {
            val variablePattern = Pattern.compile("^(\\w+)\\s*=\\s*libmy\\.a", Pattern.MULTILINE)
            val variableMatcher = variablePattern.matcher(makefileContent)

            return if (variableMatcher.find()) variableMatcher.group(1) else "libmy.a"
        }
    }
}
