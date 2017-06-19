package reader

import com.commentremover.app.CommentProcessor
import com.commentremover.app.CommentRemover
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
            field = value
        }

    override fun extractWords(): List<Word> {
        if (sourceChanged) {

            val tempFileName = File(fileName).name + "\$\$Temp.java"
            val tempFile = File(tempFileName)
            File(fileName).createNewFile()
            tempFile.writeText(File(fileName).readText())

            val commentRemover = CommentRemover.CommentRemoverBuilder()
                    .startExternalPath(tempFileName)
                    .removeJava(true)
                    .removeTodos(true)
                    .removeMultiLines(true)
                    .removeSingleLines(true)
                    .build()
            CommentProcessor(commentRemover).start()

            tempFile.readLines().map { it.replace("\\s+", "") }.forEachIndexed { index, line ->
                line.split(" ").filter(String::isNotBlank).forEach { words.add(Word(it, index)) }
            }

            // Todo: Delete the temp file in the final build
//            tempFile.delete()
            sourceChanged = false

        }
        return words
    }
}
