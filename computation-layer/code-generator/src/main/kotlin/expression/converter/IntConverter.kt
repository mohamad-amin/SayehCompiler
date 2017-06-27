package expression.converter

import expression.converter.base.Converter

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class IntConverter: Converter<Int> {

    override fun convert(t: Int): String {
        val binary = Integer.toBinaryString(t)
        return if (binary.length > 16) binary.slice(binary.length-16 .. binary.length-1)
        else generateSequence { '0' }.take(16 - binary.length).joinToString("").plus(binary)
    }


}