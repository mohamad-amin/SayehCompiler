package expression.converter.base

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
interface Converter<T> {

    /**
     * Converts input type to binary value
     */
    fun convert(t: T): String

}