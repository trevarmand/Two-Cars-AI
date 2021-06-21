package twoCars.view.agent

/**
 * Helper functions pertaining to Q-Learning
 */
class QLearningUtil {
    companion object {
        // TODO: reconsider the weights we want to assign to these
        fun getScrollerVal(type) {
            if (type == SQUARE) {
                return -10
            } else if (type == CIRCLE) {
                return 1
            } else if (type == STAR) {
                return 2
            } else {
                return 0
            }
        }

        fun bestUtil(laneNum, laneUtils): Int{
            var leftUtil = 0
            var rightUtil = 0
            var stayUtil = laneUtils[laneNum]
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
            var maxUtil = max(leftUtil, rightUtil, stayUtil)
            //var maxMove = Move.LEFT

            // there's probably a cleaner and more efficient way to do this
            // this is tying highest utility value to corresponding move
            /*
            if (maxUtil == leftUtil) {
                maxMove = Move.LEFT
            } else if (maxUtil == rightUtil) {
                maxMove = Move.RIGHT
            } else {
                maxMove = Move.STAY
            }
             */

            return maxUtil
            //return new Decision(maxMove, maxUtil)
        }
    }
}