package expression.validator

import entity.BraceClose
import entity.BraceOpen
import entity.Number
import entity.Operator
import org.statefulj.fsm.FSM
import org.statefulj.fsm.model.Action
import org.statefulj.fsm.model.State
import org.statefulj.fsm.model.Transition
import org.statefulj.fsm.model.impl.StateActionPairImpl
import org.statefulj.fsm.model.impl.StateImpl

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class ExpressionFSM {

    companion object {

        val braceOpenAction = Action<ExpressionState> { stateful, event, args ->
            stateful.parenthesisStack.push(true)
        }

        val braceCloseAction = Action<ExpressionState> { stateful, event, args ->
            stateful.parenthesisStack.pop()
        }

        fun getFSM(): FSM<ExpressionState> {

            val stateS = StateImpl<ExpressionState>("S")
            val stateO = StateImpl<ExpressionState>("O")
            val stateN = StateImpl<ExpressionState>("N", true)

            stateS.addTransition(BraceOpen::javaClass.toString(), stateS, braceOpenAction)
            stateS.addTransition(Number::javaClass.toString(), stateN)

            stateN.addTransition(BraceClose::javaClass.toString(),
                    { state, event, args ->
                        if (state.parenthesisStack.isNotEmpty()) {
                            StateActionPairImpl<ExpressionState>(stateN, braceCloseAction)
                        } else null
                    })
            stateN.addTransition(Operator::javaClass.toString(), stateO)

            stateO.addTransition(Number::javaClass.toString(), stateN)
            stateO.addTransition(BraceOpen::javaClass.toString(), stateS, braceOpenAction)

            return FSM.FSMBuilder<ExpressionState>()
                    .addState(stateS, true)
                    .addState(stateN)
                    .addState(stateO)
                    .build()

        }

    }

}