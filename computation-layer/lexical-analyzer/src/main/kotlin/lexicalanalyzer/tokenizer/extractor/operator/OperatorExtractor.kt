package lexicalanalyzer.tokenizer.extractor.operator

import entity.Operator
import lexicalanalyzer.tokenizer.extractor.TokenExtractor

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
interface OperatorExtractor<T : Operator>: TokenExtractor<T>