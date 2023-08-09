package top.goosople.poemtime

import com.google.gson.annotations.SerializedName

/**
 * A class to save the poems
 * @author Goosople
 */
class Poem : ArrayList<ArrayList<PoemItem>>() {
    var bookNames: Array<String> = arrayOf(
        "必修一",
        "必修一诵读",
        "必修二",
        "必修二诵读",
        "选择性必修一",
        "选择性必修一诵读",
        "选择性必修二",
        "选择性必修二诵读",
        "选择性必修三",
        "选择性必修三诵读",
        "课标"
    )
}

/**
 * The item in Poem
 * @author Goosople
 */
class PoemItem(
    @SerializedName("title") val title: String,
    @SerializedName("poet") val poet: String,
    @SerializedName("poem") val poem: List<String>
) {
    val detail get() = "《${title}》 $poet"
    val totalNum get() = poem.size
}