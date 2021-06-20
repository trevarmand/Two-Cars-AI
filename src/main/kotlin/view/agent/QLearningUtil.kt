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
    }
}