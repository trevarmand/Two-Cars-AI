package twoCars.view.agent
import twoCars.model.learn.Move


interface MDPLearningAgent {
    /**
     * Step decision
     * Calls on the model to step, then reports its next best move
     */

    /**
     * Creates initial utility values before making TD learning passes
     */
    fun initUtils()

    /**
     * Performs value iteration, updates utilities accordingly
     */
    fun solve()

    /**
     * Returns which of the three moves (left, right, or stay) is best, given utilities
     *
     * @param laneNum lane number agent is currently in
     *
     * @return best move to make based on calculated utilities
     */
    fun getBestMove(laneNum :Int) : Move

}