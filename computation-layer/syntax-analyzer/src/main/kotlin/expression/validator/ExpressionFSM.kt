package expression.validator

import entity.ParenthesisClose
import entity.ParenthesisOpen
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

        val parenthesisOpenAction = Action<ExpressionState> { stateful, event, args ->
            println("Pushing into stack")
            stateful.parenthesisStack.push(true)
        }

        val parenthesisCloseAction = Action<ExpressionState> { stateful, event, args ->
            if (stateful.parenthesisStack.isEmpty()) {
                println("WTF EMPTY STACK @ExpressionFSM::parenthesisCloseAction")
            } else {
                println("popping into stack, old size: ${stateful.parenthesisStack.size}")
                stateful.parenthesisStack.pop()
                println("popped from stack, new size: ${stateful.parenthesisStack.size}")
            }
        }

        fun getFSM(): FSM<ExpressionState> {

            val stateS: State<ExpressionState> = StateImpl("S")
            val stateO: State<ExpressionState> = StateImpl("O")
            val stateN: State<ExpressionState> = StateImpl("N", true)

            stateS.addTransition(Number::class.java.toString(), stateN)
            stateS.addTransition(ParenthesisOpen::class.java.toString(), stateS, parenthesisOpenAction)

            stateN.addTransition(ParenthesisClose::class.java.toString(),
                    { state, event, args ->
                        if (state.parenthesisStack.isNotEmpty()) {
                            println("With close parenthesis action")
                            StateActionPairImpl<ExpressionState>(stateN, parenthesisCloseAction)
                        } else {
                            println("with null")
                            null
                        }
                    })
            stateN.addTransition(Operator::class.java.toString(), stateO)

            stateO.addTransition(Number::class.java.toString(), stateN)
            stateO.addTransition(ParenthesisOpen::class.java.toString(), stateS, parenthesisOpenAction)

            return FSM.FSMBuilder<ExpressionState>()
                    .addState(stateS, true)
                    .addState(stateN)
                    .addState(stateO)
                    .build()

        }

    }

}