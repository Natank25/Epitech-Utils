package io.github.natank25.epitechutils.codingStyleWindow

import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.TopGap
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ThrowableRunnable
import kotlinx.coroutines.withTimeoutOrNull
import java.awt.Dimension
import java.io.IOException
import java.nio.file.Path
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class CodingStyleToolWindowFactory : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val toolWindowContent = CodingStyleToolWindowContent(project)
        val content = ContentFactory.getInstance().createContent(toolWindowContent.contentPanel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private class CodingStyleToolWindowContent(private val project: Project) {
        var ErrorList: List<CodingStyleError> = ArrayList()
        val tabbedPane: JBTabbedPane = JBTabbedPane()
        val errorWindowList: List<ErrorListWindow> = listOf(
            ErrorListWindow(ErrorListType.FATAL),
            ErrorListWindow(ErrorListType.MAJOR),
            ErrorListWindow(ErrorListType.MINOR),
            ErrorListWindow(ErrorListType.INFO),
            ErrorListWindow(ErrorListType.HIDDEN)
        )
        val contentPanel: JPanel = panel {
            row {
                button("Generate New Coding Style Report") {
                    generateReport()
                }
            }.topGap(TopGap.MEDIUM)

            row {
                cell(tabbedPane)
            }
        }

        init {
            tabbedPane.minimumSize = Dimension(400, 300)
            tabbedPane.preferredSize = Dimension(800, 600)
            for (window in errorWindowList) {
                tabbedPane.add(window.severity.string, window)
            }
            tabbedPane.isVisible = false
            project.messageBus.connect().subscribe(
                VirtualFileManager.VFS_CHANGES,
                object : BulkFileListener {
                    override fun after(events: List<VFileEvent>) {
                        events.filter { it.file?.name.equals(CODING_STYLE_REPORTS_LOG) }
                            .forEach { event -> updateReportView(event) }
                    }
                }
            )

        }

        fun generateReport() {
            val dockerRunConfig =
                RunManager.getInstance(project).findConfigurationByName("Generate Coding Style Report")
            if (dockerRunConfig == null) return
            try {
                WriteAction.run<IOException?>(ThrowableRunnable {
                    if (this.codingStyleReport != null) this.codingStyleReport?.delete(this)
                })
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            ProgramRunnerUtil.executeConfiguration(dockerRunConfig, DefaultRunExecutor.getRunExecutorInstance())
        }

        fun updateReportView(event: VFileEvent) {
            val file = event.file!!
            tabbedPane.isVisible = file.exists()
            if (!file.exists())
                return
            updateErrorList(file)

            for ((index, window) in errorWindowList.withIndex()) {
                window.updateList(ErrorList)
                tabbedPane.setTitleAt(index, window.getTitle())
            }
        }

        private fun updateErrorListWindow() {

        }

        private fun updateErrorList(file: VirtualFile) {
            var reportResult: String
            try {
                reportResult = String(file.contentsToByteArray())
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
            ErrorList = generateErrorList(reportResult)
        }

        private fun generateErrorList(string: String): List<CodingStyleError> {
            val list: ArrayList<CodingStyleError> = ArrayList()

            string.split("\n").forEach { errorLine ->
                if (!errorLine.isBlank()) list.add(CodingStyleError.createErrorFromLine(errorLine))
            }
            return list
        }

        val codingStyleReport: VirtualFile?
            get() {
                VirtualFileManager.getInstance().asyncRefresh()
                return ReadAction.compute<VirtualFile?, RuntimeException?>(
                    ThrowableComputable {
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                        VfsUtil.findFile(Path.of(project.basePath, CODING_STYLE_REPORTS_LOG), false)
                    })
            }

        companion object {
            const val CODING_STYLE_REPORTS_LOG: String = "coding-style-reports.log"
        }
    }


    private class ErrorListWindow(val severity: ErrorListType) : JTextArea() {
        var errorList : List<CodingStyleError> = emptyList()
        fun getTitle() : (String) {return severity.string + " (" + errorList.size + ")"}
        val scrollBar : JBScrollPane = JBScrollPane(this, JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JBScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        fun updateList(fullErrorList: List<CodingStyleError>){
            errorList = fullErrorList.filter { error ->
                if (hiddenErrorCode.contains(error.errorCode)){
                    this.severity == ErrorListType.HIDDEN
                }else
                    this.severity.string == error.severity.string
            }
            this.text = ""
            for (error in errorList) {
                this.text += error.fullLine + (if (error.comment != null) (": " + error.comment) else "") + "\n\n"
            }
        }

        init {
            this.isEditable = false
            this.isFocusable = false
            this.lineWrap = true
        }

        companion object {
            val hiddenErrorCode: List<String> = listOf("C-O1")
        }
    }

    private enum class ErrorListType(val string: String) {
        FATAL("FATAL"),
        MAJOR("MAJOR"),
        MINOR("MINOR"),
        INFO("INFO"),
        HIDDEN("HIDDEN")
    }
}
