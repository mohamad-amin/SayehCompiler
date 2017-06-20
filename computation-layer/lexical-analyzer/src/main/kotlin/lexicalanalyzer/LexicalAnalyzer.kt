package lexicalanalyzer

import entity.Token
import lexicalanalyzer.reader.WordReader
import lexicalanalyzer.tokenizer.WordTokenizer

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/5/17.
 */
class LexicalAnalyzer {

    private val wordReader = WordReader()
    private val wordTokenizer = WordTokenizer()

    operator fun invoke(fileName: String) : List<Token> {
        wordReader.fileName = fileName
        wordTokenizer.words = wordReader.extractWords()
        return wordTokenizer.extractTokens()
    }

}