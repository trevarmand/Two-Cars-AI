package twoCars.view.agent
import twoCars.model.scroller.*

/**
 * Helper functions pertaining to Q-Learning
 */
class MDPLearningUtil {
    companion object {
        // would need to reconsider these weights if we decided to make circles/stars mandatory
        fun getScrollerVal(type :ScrollerType) : Double{
            if (type == ScrollerType.SQUARE) {
                return -10.0
            } else if (type == ScrollerType.CIRCLE) {
                return 1.0
            } else if (type == ScrollerType.STAR) {
                return 3.0
            } else {
                return 0.0
            }
        }
    }
}