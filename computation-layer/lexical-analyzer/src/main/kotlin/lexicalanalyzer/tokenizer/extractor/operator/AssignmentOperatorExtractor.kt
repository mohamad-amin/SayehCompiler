package lexicalanalyzer.tokenizer.extractor.operator

import entity.*
import entity.TokenConstants.Operator.AssignmentOperator as A

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/4/17.
 */
class AssignmentOperatorExtractor: OperatorExtractor<AssignmentOperator> {

    override fun interact(from: Word) = when(from.text) {
        TokenConstants.Operator.AssignmentOperator.Assign -> Assign(from.line)
        TokenConstants.Operator.AssignmentOperator.PlusAssign -> PlusAssign(from.line)
        TokenConstants.Operator.AssignmentOperator.MinusAssign -> MinusAssign(from.line)
        TokenConstants.Operator.AssignmentOperator.MultiplyAssign -> MultiplyAssign(from.line)
        TokenConstants.Operator.AssignmentOperator.DivideAssign -> DivideAssign(from.line)
        TokenConstants.Operator.AssignmentOperator.ModuloAssign -> ModuloAssign(from.line)
        else -> throw IllegalStateException(
                "Required token: AssignmentOperator but it was: ${from.text} at line: ${from.line}")
    }

}