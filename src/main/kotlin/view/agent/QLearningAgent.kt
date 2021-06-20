package twoCars.view.agent

interface QLearningAgent {
    /**
     * Step decision
     * Calls on the model to step, then reports its next best move
     */

    /**
     * Creates initial utility values before making TD learning passes
     */
    fun initUtils()

}