package twoCars.view.agent
import twoCars.model.learn.Move
import twoCars.model.scroller.*

/**
 * Helper functions pertaining to Q-Learning
 */
class MDPLearningUtil {
    companion object {
        // TODO: reconsider the weights we want to assign to these
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

        // TODO: move this function back to learner so we don't need to pass in utils?
        fun bestUtil(laneNum :Int, laneUtils :Map<Int, Double>): Double {
            //var leftUtil = laneUtils[laneNum - 1] ?: 0.0
            //var rightUtil = laneUtils[laneNum + 1] ?: 0.0
            //var stayUtil = laneUtils[laneNum] ?: 0.0
            // if utility doesn't exist at this point, should definitely not select it
            var leftUtil = laneUtils[laneNum - 1] ?: -Double.MAX_VALUE
            var rightUtil = laneUtils[laneNum + 1] ?: -Double.MAX_VALUE
            var stayUtil = laneUtils[laneNum] ?: -Double.MAX_VALUE

            // check for maximum
            return maxOf(leftUtil, rightUtil, stayUtil)
        }
    }
}