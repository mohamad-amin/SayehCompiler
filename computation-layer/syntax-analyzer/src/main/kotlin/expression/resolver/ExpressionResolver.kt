package expression.resolver

import entity.*
import entity.Number
import expression.ExpressionResult
import expression.Failure
import expression.Success
import expression.validator.ExpressionValidator
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
        val nextToken = neededTokens[expression.size + startIndex]

        println("Expression: $expression") // Todo: Remove this line in final build

        return when (expressionType) {
            ValueType.CHAR -> interactCharExpr(expression[0], startIndex, expressionType)
            ValueType.INT -> interactIntExpr(expression, nextToken, startIndex, expressionType)
            ValueType.BOOL -> interactBoolExpr(expression, nextToken, startIndex, expressionType)
            ValueType.UNKNOWN -> Failure(CompileError("WTF :| Needed unknown value? token: ${expression[0]}"))
        }

    }

    fun interactCharExpr(exp: Token, startIndex: Int, expType: ValueType) = when (exp) {
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

    fun interactBoolExpr(exp: List<Token>, nextToken: Token, startIndex: Int, expType: ValueType): ExpressionResult {

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
                                println("WTF Here @ExpressionResolver::interactBoolExpr")
                                token
                            }
                        }
                    else return Failure(
                            CompileError(ErrorType.Semantic, "Undefined variable", "required type: $expType", token))
                else return Failure(
                        CompileError(ErrorType.Semantic, "Types do not match", "required type: $expType", token))
            } else token
        }

        return when(tokens.size) {
            1 -> checkSingleBoolExpr(tokens[0], expType, startIndex+1)
            2 -> {
                if (tokens[0].word == TokenConstants.Operator.LogicalOperator.Not) {
                    if (checkSingleBoolExpr(tokens[1], expType, -1) is Success)
                        Success("true", startIndex+2)
                    else unexpectedTokenError(tokens[1], expType)
                } else Failure(CompileError("SYNTAX ERROR: Types do not match, expected a logical " +
                        "expression but found: ${tokens[0].word} ${tokens[1].word} at line ${tokens[0].line}"))
            }
            else -> {
                if (tokens[0] is Keyword) {
                    if (tokens.size != 3) {
                        Failure(CompileError("SYNTAX ERROR: Types do not match, expected a logical expression " +
                                "but found: ${tokens.joinToString(" ") { it.word }} at line ${tokens[0].line}"))
                    } else {
                        if (checkSingleBoolExpr(tokens[0], expType, -1) is Success) {
                            if ((tokens[1] is LogicalOperator || tokens[1] is RelationalOperator) &&
                                    tokens[1].word != TokenConstants.Operator.LogicalOperator.Not) {
                                if (checkSingleBoolExpr(tokens[2], expType, -1) is Success) {
                                    Success("true", startIndex+3)
                                } else unexpectedTokenError(tokens[2], expType)
                            } else Failure(CompileError(ErrorType.Syntax,
                                    "Unexpected Token", "required logical operator", tokens[1]))
                        } else unexpectedTokenError(tokens[0], expType)
                    }
                } else {

                    val endTokens = listOf<Token>(AndOperator(-1), OrOperator(-1),
                            Equal(-1), NotEqual(-1), Bigger(-1), BiggerEqual(-1), Smaller(-1), SmallerEqual(-1))

                    val firstExpressionResult = interact(startIndex, ValueType.INT, endTokens)
                    return when (firstExpressionResult) {
                        is Failure -> firstExpressionResult
                        is Success -> {
                            val newStart = firstExpressionResult.endToken - startIndex
                            if (endTokens.any { it.javaClass == tokens[newStart].javaClass }) {
                                val secondExpressionResult = interact(newStart+1, ValueType.INT, listOf(nextToken))
                                return when (secondExpressionResult) {
                                    is Failure -> secondExpressionResult
                                    is Success -> Success("true", secondExpressionResult.endToken)
                                }
                            } else Failure(CompileError(
                                    ErrorType.Syntax, "Unexpected Token", "required $endTokens", tokens[newStart]))
                        }
                    }

                }
            }
        }

    }

    fun unexpectedTokenError(token: Token, expType: ValueType) =
            Failure(CompileError(ErrorType.Syntax, "Unexpected Token", "required type: $expType", token))

    fun checkSingleBoolExpr(token: Token, expType: ValueType, endToken: Int) = when(token) {
        is TRUE -> Success(TokenConstants.Keyword.TRUE, endToken)
        is FALSE -> Success(TokenConstants.Keyword.FALSE, endToken)
        else -> Failure(CompileError(
                ErrorType.Semantic, "Types do not match", "required type: $expType", token))
    }

    fun interactIntExpr(exp: List<Token>, nextToken: Token, startIndex: Int, expType: ValueType): ExpressionResult {

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
            val validationResult = expressionValidator.validate(exp)
            when (validationResult) {
                is Success -> processIntExpression(expression, startIndex + exp.size, exp[0].line)
                is Failure -> validationResult
            }
        }

    }

    // Todo: Possible Improvements
    fun processIntExpression(expression: String, endToken: Int, line: Int): ExpressionResult {
        val engine = ScriptEngineManager().getEngineByName("js")
        val result = engine.eval(expression).toString()
        return when (result) {
            "Infinity" ->
                Failure(CompileError(ErrorType.Semantic, "Division by zero!", "zero wasn't allowed", Divide(line)))
            else -> Success(result, endToken)
        }
    }

}