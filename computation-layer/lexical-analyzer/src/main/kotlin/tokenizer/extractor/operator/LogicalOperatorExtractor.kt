package tokenizer.extractor.operator

import entity.*
import entity.LogicalOperator
import entity.TokenConstants.Operator.LogicalOperator as L

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class LogicalOperatorExtractor : OperatorExtractor<LogicalOperator> {

    override fun interact(from: Word) = when(from.text) {
        L.And -> AndOperator(from.line)
        L.Or -> OrOperator(from.line)
        L.Not -> NotOperator(from.line)
        else -> throw IllegalStateException(
                "Required token: LogicalOperator but it was: ${from.text} at line: ${from.line}")
    }

}