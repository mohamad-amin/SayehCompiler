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

sealed class Token(protected val value: String, val line: Int)

    sealed class Keyword(value: String, line: Int) : Token(value, line)

        class IF(line: Int): Keyword(TokenConstants.Keyword.IF, line)
        class ELSE(line: Int): Keyword(TokenConstants.Keyword.ELSE, line)
        class WHILE(line: Int): Keyword(TokenConstants.Keyword.WHILE, line)
        class INT(line: Int): Keyword(TokenConstants.Keyword.INT, line)
        class CHAR(line: Int): Keyword(TokenConstants.Keyword.CHAR, line)
        class BOOL(line: Int): Keyword(TokenConstants.Keyword.BOOL, line)
        class NULL(line: Int): Keyword(TokenConstants.Keyword.NULL, line)
        class TRUE(line: Int): Keyword(TokenConstants.Keyword.TRUE, line)
        class FALSE(line: Int): Keyword(TokenConstants.Keyword.FALSE, line)

    sealed class Operator(value: String, line: Int): Token(value, line)

        sealed class AssignmentOperator(value: String, line: Int): Operator(value, line)

            class Assign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.Assign, line)
            class PlusAssign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.PlusAssign, line)
            class MinusAssign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MinusAssign, line)
            class MultiplyAssign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.MultiplyAssign, line)
            class DivideAssign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.DivideAssign, line)
            class ModuloAssign(line: Int): AssignmentOperator(TokenConstants.Operator.AssignmentOperator.ModuloAssign, line)

        sealed class LogicalOperator(value: String, line: Int): Operator(value, line)

            class AndOperator(line: Int): LogicalOperator(TokenConstants.Operator.LogicalOperator.And, line)
            class OrOperator(line: Int): LogicalOperator(TokenConstants.Operator.LogicalOperator.Or, line)
            class NotOperator(line: Int): LogicalOperator(TokenConstants.Operator.LogicalOperator.Not, line)

        sealed class RelationalOperator(value: String, line: Int): Operator(value, line)

            class Equal(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.Equal, line)
            class NotEqual(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.NotEqual, line)
            class Bigger(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.Bigger, line)
            class Smaller(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.Smaller, line)
            class BiggerEqual(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.BiggerEqual, line)
            class SmallerEqual(line: Int): RelationalOperator(TokenConstants.Operator.RelationalOperator.SmallerEqual, line)

        sealed class ArithmeticOperator(value: String, line: Int): Operator(value, line)

            class Plus(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Plus, line)
            class Minus(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Minus, line)
            class Multiply(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Multiply, line)
            class Divide(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.Divide, line)
            class PlusPlus(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.PlusPlus, line)
            class MinusMinus(line: Int): ArithmeticOperator(TokenConstants.Operator.ArithmeticOperator.MinusMinus, line)

    sealed class Punctuation(value: String, line: Int): Token(value, line)

        class Comma(line: Int): Punctuation(TokenConstants.Punctuation.Comma, line)
        class ParenthesisOpen(line: Int): Punctuation(TokenConstants.Punctuation.ParenthesisOpen, line)
        class ParenthesisClose(line: Int): Punctuation(TokenConstants.Punctuation.ParenthesisClose, line)
        class BraceOpen(line: Int): Punctuation(TokenConstants.Punctuation.BraceOpen, line)
        class BraceClose(line: Int): Punctuation(TokenConstants.Punctuation.BraceClose, line)
        class Colon(line: Int): Punctuation(TokenConstants.Punctuation.Colon, line)
        class Semicolon(line: Int): Punctuation(TokenConstants.Punctuation.Semicolon, line)

    class Number(val name: String, var number: String, var registerAddress: Int, var memoryAddress: Int, line: Int):
            Token(if (number[0].isDigit()) "+" + number else number, line) {

        init {
            if (number[0].isDigit()) {
                number = "+" + number
            }
        }

    }
    class Identifier(val name: String, var registerAddress: Int, var memoryAddress: Int, line: Int): Token(name, line)
    class Character(val char: String, line: Int): Token(char, line)
    class Unknown(val text: String, line: Int): Token(text, line)