package twoCars.view.agent
import twoCars.model.scroller.*

/**
 * Helper functions pertaining to Q-Learning
 */
class MDPLearningUtil {
    companion object {
        /**
         * Returns values for each type of scroller, or 0 if object isn't recognized
         *
         * @param type type of scroller
         *
         * @ returns double value for the scroller
         */
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