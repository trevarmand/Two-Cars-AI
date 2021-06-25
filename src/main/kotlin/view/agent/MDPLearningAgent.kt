package twoCars.view.agent
import twoCars.model.learn.Move

/**
 * Different types of MDP learning Agents
 */
interface MDPLearningAgent {
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