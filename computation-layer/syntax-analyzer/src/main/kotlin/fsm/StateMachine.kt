package fsm

import entity.Keyword
import entity.Token
import entity.ValueType
import org.statefulj.persistence.annotations.State
import java.util.*

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */

class StateMachine(val tokens: MutableList<Token> = arrayListOf<Token>()) {

    @State
    var state: String? = ""

    var identifierType = ValueType.UNKNOWN
    var currentIndex = 0
    var nextIndex = 1
    var identifierIndex = -1
    var variableDeclarationMode = true
    var elsePossible = false
    var valuedIdentifier = false
    var operatorType = "?"

    val scopes: Stack<Keyword> = Stack()

}