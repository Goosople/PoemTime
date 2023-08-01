package top.goosople.poemtime

import com.google.gson.annotations.SerializedName

class Poem : ArrayList<ArrayList<PoemSubListItem>>()

data class PoemSubListItem(
    @SerializedName("title") val title: String,
    @SerializedName("poet") val poet: String,
    @SerializedName("poem") val poem: List<String>
)