package com.ezi.gallery.core.model

enum class ThemeMode(val title: String) {
    SYSTEM("System Default"),
    LIGHT("Light"),
    DARK("Dark"),
    AMOLED("AMOLED")
}

enum class SortOrder(val title: String) {
    DATE_DESC("Newest first"),
    DATE_ASC("Oldest first"),
    NAME_ASC("Name (A to Z)"),
    NAME_DESC("Name (Z to A)")
}

enum class GridDensity(val columns: Int, val label: String) {
    TWO(2, "2 Columns (Comfortable)"),
    THREE(3, "3 Columns (Default)"),
    FOUR(4, "4 Columns (Compact)"),
    FIVE(5, "5 Columns (Dense)")
}
