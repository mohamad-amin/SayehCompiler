package expression.resolver

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import expression.validator.ExpressionValidator
import lexicalanalyzer.tokenizer.WordTokenizer
import javax.script.ScriptEngineManager

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 * Todo: Remember to update tokens every time
 * Todo: Set values or names to expressions?
 */
class BaseExpressionResolver(var tokens: List<Token>) {

    val expressionValidator = ExpressionValidator()

    fun interact(startIndex: Int, expressionType: ValueType, endTokens: List<Token>): ExpressionResult {

        val neededTokens = tokens.subList(startIndex, tokens.size)
        val expression = neededTokens.takeWhile { token -> endTokens.none { it.javaClass == token.javaClass } }
        val nextToken = neededTokens[expression.size]

        println("Expression: $expression") // Todo: Remove this line in final build

        return when (expressionType) {
            ValueType.CHAR -> interactCharExpr(expression[0], startIndex, expressionType)
            ValueType.INT -> interactIntExpr(expression, nextToken, startIndex, expressionType)
            ValueType.BOOL -> interactBoolExpr(expression, nextToken, startIndex, expressionType)
            ValueType.UNKNOWN -> Failure(CompileError("WTF :| Needed unknown value? token: ${expression[0]}"))
        }

    }

    private fun interactBoolExpr(exp: List<Token>, nextToken: Token, startIndex: Int, expType: ValueType): ExpressionResult {

        exp.map { token ->
            if (token is Identifier) {
                if (token.type == expType)
                    if (token.value.isNotEmpty())
                        when (expType) {
                            ValueType.BOOL ->
                                if (token.value == TokenConstants.Keyword.TRUE) TRUE(token.line) else FALSE(token.line)
                            ValueType.CHAR -> Character(token.name, token.value, token.line)
                            ValueType.INT -> Number(token.name, token.value, token.line)
                            else -> {
                                println("WTF @ExpressionResolver::interactBoolExpr")
                                token
                            }
                        }
                    else return Failure(
                            CompileError(ErrorType.Semantic, "Undefined variable", "required type: $expType", token))
                else return Failure(
                        CompileError(ErrorType.Semantic, "Types do not match", "required type: $expType", token))
            } else token
        }

        return when(exp.size) {
            1 -> checkSingleBoolExpr(exp[0], expType, startIndex+1)
            2 -> {
                if (exp[0].word == TokenConstants.Operator.LogicalOperator.Not) {
                    if (checkSingleBoolExpr(exp[1], expType, exp[0].line) is Success)
                        Success("true", startIndex+2)
                    else unexpectedTokenError(exp[1], expType)
                } else Failure(CompileError("SYNTAX ERROR: Types do not match, expected a logical " +
                        "expression but found: ${exp[0].word} ${exp[1].word} at line ${exp[0].line}"))
            }
            else -> {
                if (exp[0] is Keyword) {
                    if (exp.size != 3) {
                        Failure(CompileError("SYNTAX ERROR: Types do not match, expected a logical expression " +
                                "but found: ${exp.joinToString(" ") { it.word }} at line ${exp[0].line}"))
                    } else {
                        if (checkSingleBoolExpr(exp[0], expType, exp[0].line) is Success) {
                            if ((exp[1] is LogicalOperator || exp[1] is RelationalOperator) &&
                                    exp[1].word != TokenConstants.Operator.LogicalOperator.Not) {
                                if (checkSingleBoolExpr(exp[2], expType, exp[2].line) is Success) {
                                    Success("true", startIndex+3)
                                } else unexpectedTokenError(exp[2], expType)
                            } else Failure(CompileError(ErrorType.Syntax,
                                    "Unexpected Token", "required logical operator", exp[1]))
                        } else unexpectedTokenError(exp[0], expType)
                    }
                } else {

                    val endTokens = listOf<Token>(AndOperator(-1), OrOperator(-1),
                            Equal(-1), NotEqual(-1), Bigger(-1), BiggerEqual(-1), Smaller(-1), SmallerEqual(-1))

                    val firstExpressionResult = interact(startIndex, ValueType.INT, endTokens)
                    return when (firstExpressionResult) {
                        is Failure -> firstExpressionResult
                        is Success -> {
                            val newStart = firstExpressionResult.endToken - startIndex
                            if (endTokens.any { it.javaClass == exp[newStart].javaClass }) {
                                val secondResult = interact(newStart+1+startIndex, ValueType.INT, listOf(nextToken))
                                return when (secondResult) {
                                    is Failure -> secondResult
                                    is Success -> Success("true", secondResult.endToken)
                                }
                            } else Failure(CompileError(
                                    ErrorType.Syntax, "Unexpected Token", "required $endTokens", exp[newStart]))
                        }
                    }

                }
            }
        }

    }

    private fun interactCharExpr(exp: Token, startIndex: Int, expType: ValueType) = when (exp) {
        is Identifier -> {
            if (exp.type == expType)
                if (exp.value.isNotEmpty())
                    Success(exp.value, startIndex+1)
                else Failure(CompileError(ErrorType.Semantic, "Undefined variable", "required type: $expType", exp))
            else Failure(CompileError(ErrorType.Semantic, "Types do not match", "required type: $expType", exp))
        }
        is Character -> Success(exp.char, startIndex+1)
        else -> Failure(CompileError(ErrorType.Syntax, "Unexpected token", "required a character expression", exp))
    }

    private fun unexpectedTokenError(token: Token, expType: ValueType) =
            Failure(CompileError(ErrorType.Syntax, "Unexpected Token", "required type: $expType", token))

    private fun checkSingleBoolExpr(token: Token, expType: ValueType, endToken: Int) = when(token) {
        is TRUE -> Success(TokenConstants.Keyword.TRUE, endToken)
        is FALSE -> Success(TokenConstants.Keyword.FALSE, endToken)
        else -> Failure(CompileError(
                ErrorType.Semantic, "Types do not match", "required type: $expType", token))
    }

    private fun interactIntExpr(exp: List<Token>, nextToken: Token, startIndex: Int, expType: ValueType): ExpressionResult {

        var faultyExpression = false
        var failureResult: Failure = Failure(CompileError(ErrorType.Unknown, "", "", nextToken))

        val expression = exp.joinToString(" ") { token ->
            if (faultyExpression) token.word else when(token) {
                is Identifier -> {
                    if (token.type == expType)
                        if (token.value.isNotEmpty()) token.value
                        else {
                            faultyExpression = true
                            failureResult = Failure(CompileError(
                                    ErrorType.Semantic, "Undefined variable", "required type: $expType", token))
                            token.word
                        }
                    else {
                        faultyExpression = true
                        failureResult = Failure(CompileError(
                                ErrorType.Semantic, "Types do not match", "required type: $expType", token))
                        token.word
                    }
                }
                is Number -> token.number
                is Operator -> token.word
                is ParenthesisOpen -> token.word
                is ParenthesisClose -> token.word
                else -> {
                    faultyExpression = true
                    failureResult = Failure(CompileError(ErrorType.Syntax,
                            "Unexpected token", "required a number expression", token))
                    token.word
                }
            }
        }

        return if (faultyExpression) failureResult
        else {
            val validationResult = expressionValidator.validate(exp, nextToken)
            when (validationResult) {
                is Success -> processIntExpression(expression, startIndex + exp.size, exp[0].line)
                is Failure -> validationResult
            }
        }

    }

    // Todo: Possible Improvements
    private fun processIntExpression(expression: String, endToken: Int, line: Int): ExpressionResult {
        val engine = ScriptEngineManager().getEngineByName("js")
        val result = engine.eval(expression).toString()
        return when (result) {
            "Infinity" ->
                Failure(CompileError("SEMANTIC ERROR: Division by zero! zero wasn't allowed as divisor at line $line"))
            else -> Success(result, endToken)
        }
    }

}

// Todo: Remove this function in the final build
fun main(args: Array<String>) {

    val expression = "bool c = 1 * 2 == 4 * ( 5 ) / ( 10 - 10 ) ;"
    val tokenizer = WordTokenizer()

    tokenizer.words = expression.split(" ").map { Word(it, 1) }
    val tokens = tokenizer.extractTokens()

    val expressionResolver = BaseExpressionResolver(tokens)

//    tokens.forEach { println("${it.word} -> ${it.javaClass}") }
    println("The result:\n${expressionResolver.interact(3, ValueType.BOOL, listOf(Semicolon(1)))}")

}