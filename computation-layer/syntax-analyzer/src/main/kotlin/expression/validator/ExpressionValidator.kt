package expression.validator

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import lexicalanalyzer.tokenizer.WordTokenizer
import org.statefulj.fsm.model.impl.StateImpl

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 * Todo: Remove extra prints in the final build
 */
class ExpressionValidator {

    val fsm = ExpressionFSM.getFSM()

    fun validate(expression: List<Token>, nextToken: Token): ExpressionResult {

        val state = ExpressionState()

        expression.forEach { token ->
            val events = (fsm.getCurrentState(state) as StateImpl).transitions.keys
            printStateEvent(fsm.getCurrentState(state) as StateImpl, token.javaClass.toString())
            when (token) {
                is Number ->
                    if (hasEvent(state, Number::class.java.toString())) {
                        fsm.onEvent(state, Number::class.java.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is Operator ->
                    if (hasEvent(state, Operator::class.java.toString())) {
                        fsm.onEvent(state, Operator::class.java.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is ParenthesisClose ->
                    if (hasEvent(state, ParenthesisClose::class.java.toString())) {
                        fsm.onEvent(state, ParenthesisClose::class.java.toString())
                    } else {
                        return getFailure(events, token)
                    }
                is ParenthesisOpen ->
                    if (hasEvent(state, ParenthesisOpen::class.java.toString())) {
                        fsm.onEvent(state, ParenthesisOpen::class.java.toString())
                    } else {
                        return getFailure(events, token)
                    }
                else -> {
                    return getFailure(events, token)
                }
            }
        }

        return if (fsm.getCurrentState(state).isEndState)
            if (state.parenthesisStack.isEmpty()) Success("Ok", expression.size)
            else Failure(CompileError("SYNTAX ERROR, extra open parenthesis was found, required an " +
                    "extra close parenthesis but found ${nextToken.word} at line ${nextToken.line}"))
        else getFailure((fsm.getCurrentState(state) as StateImpl).transitions.keys, nextToken)

    }

    fun printStateEvent(state: StateImpl<ExpressionState>, event: String) {
//        print("state: ${state.name} and event: $event and possible events: ${state.transitions.keys.count()} -> ")
//        state.transitions.keys.forEach { print("$it | ")}
//        println()
    }

    fun hasEvent(state: ExpressionState, event: String) = fsm.getCurrentState(state).getTransition(event) != null

    fun getFailure(events: MutableSet<String>, token: Token) =
            Failure(CompileError(ErrorType.Syntax, "Unexpected Token", "required $events", token))

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val expressionValidator = ExpressionValidator()
    val tokenizer = WordTokenizer()

    val expression = "1 + ( ( 2 + 2 ) )"
    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${expressionValidator.validate(tokens, Semicolon(1))}")

}