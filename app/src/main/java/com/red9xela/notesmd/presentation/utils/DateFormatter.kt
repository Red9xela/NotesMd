package com.red9xela.notesmd.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.red9xela.notesmd.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


object DateFormatter {
    //с помощью вспомогательного класса преобразуем час в миллисекунды
    private val millisInHour = TimeUnit.HOURS.toMillis(1)
    private val millisInDay = TimeUnit.DAYS.toMillis(1)
    private val formatter = SimpleDateFormat.getDateInstance(DateFormat.SHORT)

    fun formatCurrentDate(): String {
        return formatter.format(System.currentTimeMillis())
    }

    @Composable
    fun formatDateToString(timestamp: Long): String {
        //получаем текущее время
        val now = System.currentTimeMillis()
        //получаем разницу между текущим временем и временем обновления заметки
        val diff = now - timestamp

        return when {
            //если с момента последнего обновления прошло меньше часа
            diff < millisInHour -> stringResource(R.string.just_now)
            // если прошло больше часа, но меньше суток
            diff < millisInDay -> {
                //считаем количество часов с момента редактирования
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                stringResource(R.string.hours_ago, hours)
            }
            //если же прошло больше суток, то отображаем дату в виде: число месяц год
            else -> {
                formatter.format(timestamp)
            }
        }
    }

}