package lexicalanalyzer.tokenizer.extractor

import base.Mapper
import entity.Token
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
interface TokenExtractor<T : Token> : Mapper<Word, T>