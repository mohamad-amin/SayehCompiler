package expression.converter

import expression.converter.base.Converter

/**
 * Created by mohamadamin (torpedo.mohammadi@gmail.com) on 6/27/17.
 */
class BoolConverter: Converter<Boolean> {

    override fun convert(t: Boolean) = IntConverter().convert(if (t) 1 else 0)

}