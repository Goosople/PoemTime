package top.goosople.poemtime

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

/**
 * A class to get the state of a verse
 * @author Goosople
 */
@Entity(primaryKeys = ["bookNum", "poemNum", "verseNum"])
data class Verse(
    val bookNum: Int, val poemNum: Int, val verseNum: Int, val isFinished: Boolean
)

/**
 * A DAO class to interact with the database
 * @author Goosople
 */
@Dao
interface VerseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verse: Verse)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(verse: Verse): Int

    @Delete
    fun delete(verse: Verse)

    @Query("select * from verse where bookNum=:bookNum and poemNum=:poemNum and verseNum=:verseNum")
    fun find(bookNum: Int, poemNum: Int, verseNum: Int): Array<Verse>
}

/**
 * A database class
 * @author Goosople
 */
@Database(entities = [Verse::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun verseDao(): VerseDao
}