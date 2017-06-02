package entity

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

object TokenConstants {

    fun contains(text: String) = Keyword.contains(text) || Operator.contains(text) || Punctuation.contains(text)

    object Keyword {
        fun contains(text: String) =
                arrayOf(IF, ELSE, WHILE, INT, CHAR, BOOL, NULL, TRUE, FALSE).contains(text)
        val IF = "if"
        val ELSE = "else"
        val WHILE = "while"
        val INT = "int"
        val CHAR = "char"
        val BOOL = "bool"
        val NULL = "null"
        val TRUE = "true"
        val FALSE = "false"
    }

    object Operator {

        fun contains(text: String) =
            AssignmentOperator.contains(text) || LogicalOperator.contains(text) ||
            RelationalOperator.contains(text) || ArithmeticOperator.contains(text)

        object AssignmentOperator {
            fun contains(text: String) =
                    arrayOf(Assign, PlusAssign, MinusAssign, MultiplyAssign, DivideAssign, ModuloAssign).contains(text)
            val Assign = "="
            val PlusAssign = "+="
            val MinusAssign = "-="
            val MultiplyAssign = "*="
            val DivideAssign = "/="
            val ModuloAssign = "%="
        }

        object LogicalOperator {
            fun contains(text: String) = arrayOf(And, Or, Not).contains(text)
            val And = "&&"
            val Or = "||"
            val Not = "!"
        }

        object RelationalOperator {
            fun contains(text: String) =
                    arrayOf(Equal, NotEqual, Bigger, Smaller, BiggerEqual, SmallerEqual).contains(text)
            val Equal = "=="
            val NotEqual = "!="
            val Bigger = ">"
            val Smaller = "<"
            val BiggerEqual = ">="
            val SmallerEqual = "<="
        }

        object ArithmeticOperator {
            fun contains(text: String) =
                    arrayOf(Plus, Minus, Multiply, Divide, PlusPlus, MinusMinus).contains(text)
            val Plus = "+"
            val Minus = "-"
            val Multiply = "*"
            val Divide = "/"
            val PlusPlus = "++"
            val MinusMinus = "--"
        }

    }

    object Punctuation {
        fun contains(text: String) = arrayOf(
                Comma, ParenthesisOpen, ParenthesisClose, BraceOpen, BraceClose, Colon, Semicolon).contains(text)
        val Comma = ","
        val ParenthesisOpen = "("
        val ParenthesisClose = ")"
        val BraceOpen = "{"
        val BraceClose = "}"
        val Colon = ":"
        val Semicolon = ";"
    }

}

sealed class Token(protected val value: String)
    sealed class Keyword(value: String) : Token(value)
        class IF(): Keyword(TokenConstants.Keyword.IF)
        class ELSE(): Keyword(TokenConstants.Keyword.ELSE)
        class WHILE(): Keyword(TokenConstants.Keyword.WHILE)
        class INT(): Keyword(TokenConstants.Keyword.INT)
        class CHAR(): Keyword(TokenConstants.Keyword.CHAR)
        class BOOL(): Keyword(TokenConstants.Keyword.BOOL)
        class NULL(): Keyword(TokenConstants.Keyword.NULL)
        class TRUE(): Keyword(TokenConstants.Keyword.TRUE)
        class FALSE(): Keyword(TokenConstants.Keyword.FALSE)
    sealed class Operator(value: String): Token(value)
        sealed class AssignmentOperator(value: String): Operator(value)
            class Assign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.Assign)
            class PlusAssign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.PlusAssign)
            class MinusAssign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MinusAssign)
            class MultiplyAssign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MultiplyAssign)
            class DivideAssign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.DivideAssign)
            class ModuloAssign(): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.ModuloAssign)
        sealed class LogicalOperator(value: String): Operator(value)
            class AndOperator(): LogicalOperator(TokenConstants.Operator.LogicalOperator.And)
            class OrOperator(): LogicalOperator(TokenConstants.Operator.LogicalOperator.Or)
            class NotOperator(): LogicalOperator(TokenConstants.Operator.LogicalOperator.Not)
        sealed class RelationalOperator(value: String): Operator(value)
            class Equal(): RelationalOperator(TokenConstants.Operator.RelationalOperator.Equal)
            class NotEqual(): RelationalOperator(TokenConstants.Operator.RelationalOperator.NotEqual)
            class Bigger(): RelationalOperator(TokenConstants.Operator.RelationalOperator.Bigger)
            class Smaller(): RelationalOperator(TokenConstants.Operator.RelationalOperator.Smaller)
            class BiggerEqual(): RelationalOperator(TokenConstants.Operator.RelationalOperator.BiggerEqual)
            class SmallerEqual(): RelationalOperator(TokenConstants.Operator.RelationalOperator.SmallerEqual)
        sealed class ArithmeticOperator(value: String): Operator(value)
            class Plus(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Plus)
            class Minus(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Minus)
            class Multiply(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Multiply)
            class Divide(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Divide)
            class PlusPlus(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.PlusPlus)
            class MinusMinus(): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.MinusMinus)
    sealed class Punctuation(value: String): Token(value)
        class Comma(): Punctuation(TokenConstants.Punctuation.Comma)
        class ParenthesisOpen(): Punctuation(TokenConstants.Punctuation.ParenthesisOpen)
        class ParenthesisClose(): Punctuation(TokenConstants.Punctuation.ParenthesisClose)
        class BraceOpen(): Punctuation(TokenConstants.Punctuation.BraceOpen)
        class BraceClose(): Punctuation(TokenConstants.Punctuation.BraceClose)
        class Colon(): Punctuation(TokenConstants.Punctuation.Colon)
        class Semicolon(): Punctuation(TokenConstants.Punctuation.Semicolon)
    class Identifier(value: String): Token(value)
    class Number(var number: String): Token(if (number.first().isDigit()) "+" + number else number) {
        init {
            if (number.first().isDigit()) {
                number = "+" + number
            }
        }
    }
    class Character(val char: String): Token(char)
    class Unknown(val text: String): Token(text)