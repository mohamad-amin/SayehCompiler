package lexicalanalyzer.tokenizer.extractor.operator

import entity.Operator
import entity.TokenConstants
import entity.TokenConstants.Operator as O
import entity.Word

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class OperatorExtractorImp : OperatorExtractor<Operator> {

    val assignmentOperatorExtractor = AssignmentOperatorExtractor()
    val logicalOperatorExtractor = LogicalOperatorExtractor()
    val relationalOperatorExtractor = RelationalOperatorExtractor()
    val arithmeticOperatorExtractor = ArithmeticOperatorExtractor()

    override fun interact(from: Word) = when {
        TokenConstants.Operator.AssignmentOperator.contains(from.text) -> assignmentOperatorExtractor.interact(from)
        TokenConstants.Operator.LogicalOperator.contains(from.text) -> logicalOperatorExtractor.interact(from)
        TokenConstants.Operator.RelationalOperator.contains(from.text) -> relationalOperatorExtractor.interact(from)
        TokenConstants.Operator.ArithmeticOperator.contains(from.text) -> arithmeticOperatorExtractor.interact(from)
        else -> throw IllegalStateException(
                "Required token: Operator but it was: ${from.text} at line: ${from.line}")
    }

}