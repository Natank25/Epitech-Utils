package io.github.natank25.epitechutils.module

import com.intellij.openapi.ui.ValidationInfo

data class EpitechProjectSettings(
    var binName: String = "",
    var useLibMy: Boolean = false,
    var libPath: String? = null,
    var gitRepo: String = "",
) {
    fun validate() : ValidationInfo? {
        if (binName.isEmpty()) return ValidationInfo("Binary Name can't be blank.")
        if (!binName.matches(Regex("^[a-zA-Z0-9_.-]+\$"))) return ValidationInfo("Binary name can only contains a-z, A-Z, 0-9 _ . - characters.")
        return null
    }
}
