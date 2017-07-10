package fsm

import entity.*
import expression.solver.ExpressionCode
import org.statefulj.persistence.annotations.State
import java.util.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */

class GeneratorStateful(val tokens: List<Token>) {

    @State
    var state: String? = ""

    var code = ""

    var currentIndex = 0
    var nextIndex = 1
    var identifierIndex = -1
    var operatorType: AssignmentOperator = ModuloAssign()

    var expressionResult: ExpressionCode = ExpressionCode("", -1)
        set(value) {
            nextIndex = value.nextIndex
            field = value
        }

    var unionExpressionType: ArithmeticOperator = Plus()
    var flowType: Keyword = INT()

    val scopeStack: Stack<GScope> = Stack()

}

sealed class GScope
    class WhileG(val startIndex: Int, var escapeIndex: Int = -1): GScope()
    class IfG(var afterIndex: Int = -1): GScope()
    class ElseG(val afterIndex: Int): GScope()