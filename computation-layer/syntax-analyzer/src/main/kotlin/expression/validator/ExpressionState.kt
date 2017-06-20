package expression.validator

import entity.Token
import org.statefulj.persistence.annotations.State
import java.util.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class ExpressionState {

    @State
    var state: String? = ""

    val parenthesisStack = Stack<Boolean>()

}