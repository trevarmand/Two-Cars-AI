package twoCars.view.agent
import twoCars.model.learn.Move
import twoCars.model.scroller.*

/**
 * Helper functions pertaining to Q-Learning
 */
class QLearningUtil {
    companion object {
        // TODO: reconsider the weights we want to assign to these
        fun getScrollerVal(type :ScrollerType) : Int{
            if (type == ScrollerType.SQUARE) {
                return -10
            } else if (type == ScrollerType.CIRCLE) {
                return 1
            } else if (type == ScrollerType.STAR) {
                return 2
            } else {
                return 0
            }
        }

        // TODO: move this function back to learner so we don't need to pass in utils?
        fun bestUtil(laneNum :Int, laneUtils :Map<Int, Double>): Int{
            var leftUtil = 0.0
            var rightUtil = 0.0
            var stayUtil = laneUtils[laneNum]?.toDouble()
            //edge case: can't move left
            if (laneNum == 0) {
                leftUtil = stayUtil
            } else {
                leftUtil = laneUtils[laneNum - 1]
            }

            // edge case: can't move right
            if (laneNum == laneUtils.size - 1) {
                rightUtil = stayUtil
            } else {
                rightUtil = laneUtils[laneNum + 1]
            }

            // check for maximum
            return max(leftUtil, rightUtil, stayUtil)
        }
    }
}