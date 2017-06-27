package expression.solver

import entity.*
import entity.Number
import expression.converter.base.TokenConverter
import expression.resolver.NumericPostfixGenerator
import lexicalanalyzer.tokenizer.WordTokenizer
import memory.MemoryManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class ExpressionCompiler(var tokens: List<Token>) {

    val memory = MemoryManager()
    val converter = TokenConverter()

    /**
     * @return index of the next token after expression
     */
    fun compile(startIndex: Int, expressionType: ValueType, endTokens: List<Token>): ExpressionCode {

        val expression = getNeededTokens(startIndex, endTokens)
        expression.forEachIndexed { index, token -> token.index = startIndex + index }

//        println("Expression: ${expression.joinToString(" ") { it.word }}") // Todo: Remove this line in final build

        return when (expressionType) {
            ValueType.INT -> interactIntExpr(expression, startIndex)
            ValueType.CHAR -> interactCharExpr(expression, startIndex)
            ValueType.BOOL -> interactBoolExpr(expression, startIndex)
            ValueType.UNKNOWN -> throw IllegalArgumentException("WTF :| Needed unknown value? token: ${expression[0]}")
        }

    }

    fun getNeededTokens(startIndex: Int, endTokens: List<Token>): List<Token> {
        val neededTokens = tokens.subList(startIndex, tokens.size)
        val op = endTokens.all { it.javaClass == ParenthesisClose().javaClass }
        return if (op) {
            var pars = 0
            neededTokens.takeWhile { token ->
                if (token is ParenthesisOpen) pars++
                else if (token is ParenthesisClose) pars--
                pars != -1
            }
        } else neededTokens.takeWhile { token -> endTokens.none { it.javaClass == token.javaClass } }
    }

    fun interactIntExpr(expression: List<Token>, startIndex: Int): ExpressionCode {
        val postfix = NumericPostfixGenerator(expression).getPostfixFromTokens()
//        println("Postfix: ${postfix.joinToString(" "){it.word}}")
        val result = solveIntExpression(postfix, 0)
        memory.freeRegisters(result.disposables)
        return ExpressionCode(result.code, startIndex + expression.size, result.address, listOf(result.address))
    }

    fun interactBoolExpr(expression: List<Token>, startIndex: Int) = when (expression.size) {
        1 -> {
            val address = memory.getNextEmptyRegister(expression[0].index)
            val code = "R$address <= ${converter.convert(expression[0])}"
            ExpressionCode(code, startIndex+1, address, listOf(address))
        }
        2 -> {
            val address = memory.getNextEmptyRegister(expression[0].index)
            val code = "R$address <= ${converter.convert(expression[0])}\nR$address <= NOT R$address"
            ExpressionCode(code, startIndex+1, address, listOf(address))
        }
        else -> {

            val endTokens = listOf(Equal(-1), NotEqual(-1), Bigger(-1), BiggerEqual(-1), Smaller(-1),
                    SmallerEqual(-1), AndOperator(), OrOperator())

            val localCompiler = ExpressionCompiler(expression)
            val first = localCompiler.compile(0, getValueType(expression[0]), endTokens)
            val operator = expression[first.nextIndex]
//            println("Operator: ${operator.word}")
            val second = localCompiler.compile(first.nextIndex+1, getValueType(expression[first.nextIndex+1]), listOf())

            val address = memory.getNextEmptyRegister(operator.index)
            val finalCode = "${first.code}\n${second.code}\n" +
                    "R$address <= R${first.address} ${operator.word} R${second.address}"

            ExpressionCode(finalCode, second.nextIndex, address, listOf(address))

        }
    }

    fun getValueType(token: Token) = when (token) {
        is TRUE -> ValueType.BOOL
        is FALSE -> ValueType.BOOL
        is CHAR -> ValueType.CHAR
        is Number -> ValueType.INT
        is Identifier -> token.type
        is ParenthesisOpen -> ValueType.INT
        else -> {
            println("WTF token: $token")
            ValueType.UNKNOWN
        }
    }

    fun interactCharExpr(expression: List<Token>, startIndex: Int): ExpressionCode {
        val address = memory.getNextEmptyRegister(expression[0].index)
        val code = "R$address <= ${converter.convert(expression[0])}"
        return ExpressionCode(code, startIndex+1, address, listOf(address))
    }

    fun solveIntExpression(postfix: List<Token>, startIndex: Int): ExpressionCode {
        val token = postfix[startIndex]
        return when (token) {
            is ArithmeticOperator -> {
                val first = solveIntExpression(postfix, startIndex+1)
                val second = solveIntExpression(postfix, first.nextIndex)
                val address = memory.getNextEmptyRegister(token.index)
                memory.freeRegisters(first.disposables.plus(second.disposables))
                val resultCode = "R$address <= R${first.address} ${token.word} R${second.address}"
                val finalCode = "${first.code}\n${second.code}\n$resultCode"
                ExpressionCode(finalCode, second.nextIndex, address, listOf(address))
            }
            else -> {
                val address = memory.getNextEmptyRegister(token.index)
                ExpressionCode("R$address <= ${converter.convert(token)}", startIndex+1, address, listOf(address))
            }
        }
    }

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val expression = "bool c = ( 1 * ( 2 ) == 4 * 5 / ( ( 2 ) ) )"
    val tokenizer = WordTokenizer()

    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

    val expressionCompiler = ExpressionCompiler(tokens)

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${expressionCompiler.compile(4, ValueType.BOOL, listOf(ParenthesisClose())).code}")

}