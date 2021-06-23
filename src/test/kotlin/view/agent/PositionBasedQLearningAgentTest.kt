package view.agent

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import twoCars.view.agent.PositionBasedQLearningAgent

internal class PositionBasedQLearningAgentTest {

    @Test
    fun init() {
        var learner = PositionBasedQLearningAgent()
    }


    @Test
    fun qSolve() {
        var learner = PositionBasedQLearningAgent()
        learner.qSolve(100)
    }
}