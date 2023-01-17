package com.amirhosseinhamidian.my.utils

import java.util.Calendar

object DayOfWeek {
    fun getDayNumber(day: Int ,firstDayOfWeek: Int) : Int {
        var dayNumber = 0

        if (firstDayOfWeek == Constants.FIRST_DAY_SATURDAY) {
            when(day) {
                Calendar.SATURDAY -> dayNumber =  1
                Calendar.SUNDAY -> dayNumber = 2
                Calendar.MONDAY -> dayNumber = 3
                Calendar.TUESDAY -> dayNumber = 4
                Calendar.WEDNESDAY -> dayNumber = 5
                Calendar.THURSDAY -> dayNumber = 6
                Calendar.FRIDAY -> dayNumber = 7
            }
        }

        if (firstDayOfWeek == Constants.FIRST_DAY_SUNDAY) {
            when(day) {
                Calendar.SUNDAY -> dayNumber =  1
                Calendar.MONDAY -> dayNumber = 2
                Calendar.TUESDAY -> dayNumber = 3
                Calendar.WEDNESDAY -> dayNumber = 4
                Calendar.THURSDAY -> dayNumber = 5
                Calendar.FRIDAY -> dayNumber = 6
                Calendar.SATURDAY -> dayNumber = 7
            }
        }

        if (firstDayOfWeek == Constants.FIRST_DAY_MONDAY) {
            when(day) {
                Calendar.MONDAY -> dayNumber =  1
                Calendar.TUESDAY -> dayNumber = 2
                Calendar.WEDNESDAY -> dayNumber = 3
                Calendar.THURSDAY -> dayNumber = 4
                Calendar.FRIDAY -> dayNumber = 5
                Calendar.SATURDAY -> dayNumber = 6
                Calendar.SUNDAY -> dayNumber = 7
            }
        }

        return dayNumber
    }
}