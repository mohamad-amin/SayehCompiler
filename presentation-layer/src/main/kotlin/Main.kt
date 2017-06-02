import entity.*
import entity.token.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/2/17.
 */

fun main(args: Array<String>) {

    val token: Token = Plus("")

    when (token) {
        is Unknown -> {
            println("Uknown")
        }
        is Operator -> when (token) {
            is ArithmeticOperator -> when (token) {
                is Plus -> println("plus")
            }
        }
    }

}