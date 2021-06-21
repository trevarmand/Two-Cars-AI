package twoCars.view.agent
import twoCars.model.learn.MOVE


interface QLearningAgent {
    /**
     * Step decision
     * Calls on the model to step, then reports its next best move
     */

    /**
     * Creates initial utility values before making TD learning passes
     */
    fun initUtils()

    /**
     * Performs value iteration, updates utlities accordingly
     */
    fun solve()

    /**
     * Returns which of the three moves (left, right, or stay) is best, given utilities
     */
    fun getBestMove() : Move

}