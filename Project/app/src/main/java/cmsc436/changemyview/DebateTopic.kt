package cmsc436.changemyview

import java.util.*

data class DebateTopic(
    val debateID: String,
    val title: String,
    val questions: List<String>,
    val runtime: Int,                   // Runtime in seconds
    val startTime: Date?,
    val leftUIDs: List<String>?,
    val rightUIDs: List<String>?,
    val leftVotes: Int,
    val rightVotes: Int
) {

    companion object {
        const val DEBATE_ID = "debateID"
        const val TITLE = "title"
        const val QUESTIONS = "questions"
        const val RUNTIME = "runtime"
        const val START_TIME = "startTime"
        const val LEFT_UIDS = "leftUIDs"
        const val RIGHT_UIDS = "rightUIDs"
        const val LEFT_VOTES = "leftVotes"
        const val RIGHT_VOTES = "rightVotes"
    }
}