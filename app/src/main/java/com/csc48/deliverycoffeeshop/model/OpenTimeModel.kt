package com.csc48.deliverycoffeeshop.model

import com.csc48.deliverycoffeeshop.utils.CLOSE_TIME
import com.csc48.deliverycoffeeshop.utils.OPEN_TIME

data class OpenTimeModel(
    var open: String = OPEN_TIME,
    var close: String = CLOSE_TIME
)