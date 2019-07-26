package sk.hudak.prco.task

import java.util.*

data class TaskContext @JvmOverloads constructor(
        val status: TaskStatus,
        val lastChanged: Date = Date())
