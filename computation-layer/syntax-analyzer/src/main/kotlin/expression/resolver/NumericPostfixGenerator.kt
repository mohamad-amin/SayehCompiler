package expression.resolver

import entity.*
import entity.Number
import lexicalanalyzer.tokenizer.WordTokenizer
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 * Todo: ++
 */
class NumericPostfixGenerator(val expression: List<Token>) {

    fun <T> stackOf(vararg elements: T) =
        if (elements.isEmpty()) Stack<T>() else {
            val stack = Stack<T>()
            elements.forEach { stack push it }
            stack
        }

    infix fun <T> ArrayList<T>.add(t: T) = add(t)
    infix fun <T> Stack<T>.push(t: T) = push(t)

    fun getPostfixFromTokens(): List<Token> {

        val opStack = stackOf<Token>()
        val output = arrayListOf<Token>()

        expression.forEach { when (it) {
            is Number -> output add it
            is Identifier -> output add it
            is ParenthesisOpen -> opStack push it
            is ParenthesisClose -> {
                while (opStack.peek() !is ParenthesisOpen) output add opStack.pop()
                opStack.pop()
            }
            is ArithmeticOperator -> {
                while (opStack.isNotEmpty() && higherPrecedenceOnTop(opStack.peek(), it)) output add opStack.pop()
                opStack push it
            }
            else -> println("WTF @${className()}::getInfixFromTokens()")
        }}

        while (opStack.isNotEmpty()) output add opStack.pop()
        return output.reversed()
    }

    fun higherPrecedenceOnTop(top: Token, operator: ArithmeticOperator) =
            top is ArithmeticOperator && top.precedence >= operator.precedence

}

fun main(args: Array<String>) {

    val expression = "1 * ( 2 + 4 )"
    val tokenizer = WordTokenizer()

    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

    val postfixGenerator = NumericPostfixGenerator(tokens)

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${postfixGenerator.getPostfixFromTokens().joinToString(" ") { it.word }}")


}