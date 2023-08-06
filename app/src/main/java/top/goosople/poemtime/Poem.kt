package top.goosople.poemtime

import com.google.gson.annotations.SerializedName

/**
 * A class to save the poems
 * @author Goosople
 */
class Poem : ArrayList<ArrayList<PoemItem>>()

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