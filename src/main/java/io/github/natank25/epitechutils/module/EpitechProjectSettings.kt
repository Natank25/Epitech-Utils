package io.github.natank25.epitechutils.module

import com.intellij.openapi.ui.ValidationInfo


data class EpitechProjectSettings(
    var binName: String = "",
    var useLibMy: Boolean = false,
    var libPath: String? = null,
) {
    fun validate() : ValidationInfo? {
        if (binName.isEmpty()) return ValidationInfo("Binary Name can't be blank.")
        if (!binName.all { it.isLetter() }) return ValidationInfo("Binary name can't have spaces or tabs")
        return null
    }
}
