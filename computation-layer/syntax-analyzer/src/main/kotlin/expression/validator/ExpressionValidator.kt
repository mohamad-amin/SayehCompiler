package expression.validator

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import org.statefulj.fsm.model.impl.StateImpl

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class ExpressionValidator {

    val fsm = ExpressionFSM.getFSM()

    fun validate(expression: List<Token>): ExpressionResult {

        val state = ExpressionState()

        expression.forEach { token ->
            val events = (fsm.getCurrentState(state) as StateImpl).transitions.keys
            when (token) {
                is Number ->
                    if (hasEvent(state, Number::javaClass.toString())) {
                        fsm.onEvent(state, Number::javaClass.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is Operator ->
                    if (hasEvent(state, Operator::javaClass.toString())) {
                        fsm.onEvent(state, Operator::javaClass.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is ParenthesisClose ->
                    if (hasEvent(state, ParenthesisClose::javaClass.toString())) {
                        fsm.onEvent(state, ParenthesisClose::javaClass.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is ParenthesisOpen ->
                    if (hasEvent(state, ParenthesisOpen::javaClass.toString())) {
                        fsm.onEvent(state, ParenthesisOpen::javaClass.toString())
                    } else {
                        return getFailure(events, token)
                    }
                else -> return getFailure(events, token)
            }
        }

        return Success("Ok", -1)

    }

    fun hasEvent(state: ExpressionState, event: String) = fsm.getCurrentState(state).getTransition(event) != null

    fun getFailure(events: MutableSet<String>, token: Token) =
            Failure(CompileError(ErrorType.Syntax, "Unexpected Token", "required $events", token))

}