package lexicalanalyzer.tokenizer.extractor.operator

import entity.*
import entity.LogicalOperator
import entity.TokenConstants.Operator.LogicalOperator as L

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class LogicalOperatorExtractor : OperatorExtractor<LogicalOperator> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Operator.LogicalOperator.And -> AndOperator(from.line)
        TokenConstants.Operator.LogicalOperator.Or -> OrOperator(from.line)
        TokenConstants.Operator.LogicalOperator.Not -> NotOperator(from.line)
        else -> throw IllegalStateException(
                "Required token: LogicalOperator but it was: ${from.text} at line: ${from.line}")
    }

}