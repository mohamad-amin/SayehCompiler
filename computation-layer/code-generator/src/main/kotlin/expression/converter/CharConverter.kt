package expression.converter

import expression.converter.base.Converter

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class CharConverter: Converter<Char> {

    override fun convert(t: Char) = IntConverter().convert(t.toInt())

}