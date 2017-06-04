package reader

import entity.Word
import java.io.File

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class WordReader : Reader {

    private var sourceChanged = true
    private var words = arrayListOf<Word>()

    override var fileName: String = ""
        set(value) {
            sourceChanged = true
        }

    override fun extractWords(): List<Word> {
        if (sourceChanged) {
            words.clear()
            File(fileName).readLines().map { it.replace(Regex("\\s+"), " ") }.forEachIndexed { index, line ->
                line.split(" ").forEach { words.add(Word(it, index)) }
            }
            sourceChanged = false
        }
        return words
    }

}
