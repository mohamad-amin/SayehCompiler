package fsm

import org.statefulj.fsm.model.Action
import org.statefulj.fsm.model.State
import org.statefulj.fsm.model.Transition
import org.statefulj.fsm.model.impl.DeterministicTransitionImpl
import java.util.HashMap

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/20/17.
 */
class CustomStateImpl<T> : State<T> {

    private var name: String? = null
    private var transitions: MutableMap<String, Transition<T>> = HashMap()
    internal var isEndState = false
    internal var isBlocking = false

    constructor() {}

    constructor(name: String?) {
        if (name == null || name.trim { it <= ' ' } == "") {
            throw RuntimeException("Name must be a non-empty value")
        }
        this.name = name
    }

    constructor(name: String, isEndState: Boolean) : this(name) {
        this.isEndState = isEndState
    }

    constructor(name: String, isEndState: Boolean, isBlocking: Boolean) : this(name, isEndState) {
        this.isBlocking = isBlocking
    }

    constructor(name: String, transitions: MutableMap<String, Transition<T>>, isEndState: Boolean) : this(name, isEndState) {
        this.transitions = transitions
    }

    constructor(name: String, transitions: MutableMap<String, Transition<T>>, isEndState: Boolean, isBlocking: Boolean) : this(name, transitions, isEndState) {
        this.isBlocking = isBlocking
    }

    override fun getName(): String? {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    override fun getTransition(event: String): Transition<T>? {
        return transitions[event]
    }

    fun getTransitions(): Map<String, Transition<T>> {
        return transitions
    }

    fun setTransitions(transitions: MutableMap<String, Transition<T>>) {
        this.transitions = transitions
    }

    override fun isEndState(): Boolean {
        return isEndState
    }

    fun setEndState(isEndState: Boolean) {
        this.isEndState = isEndState
    }

    override fun addTransition(event: String, next: State<T>) {
        this.transitions.put(event, DeterministicTransitionImpl(next, null))
    }

    override fun addTransition(event: String, next: State<T>, action: Action<T>) {
        this.transitions.put(event, DeterministicTransitionImpl(next, action))
    }

    override fun addTransition(event: String, transition: Transition<T>) {
        this.transitions.put(event, transition)
    }

    override fun removeTransition(event: String) {
        this.transitions.remove(event)
    }

    override fun setBlocking(isBlocking: Boolean) {
        this.isBlocking = isBlocking
    }

    override fun isBlocking(): Boolean {
        return this.isBlocking
    }

    override fun toString(): String {
        return "State[name=" + this.name + ", isEndState=" + this.isEndState + ", isBlocking=" + this.isBlocking + "]"
    }
}