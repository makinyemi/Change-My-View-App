package cmsc436.changemyview

import java.util.*

data class DebateTopic(
    val debateID: String = "",
    val title: String = "",
    val questions: List<String> = emptyList(),
    val runtime: Int = 0,
    val startTime: Date? = null,
) {

    companion object {
        const val DEBATE_ID = "debateID"
        const val TITLE = "title"
        const val QUESTIONS = "questions"
        const val RUNTIME = "runtime"
        const val START_TIME = "startTime"
    }
}