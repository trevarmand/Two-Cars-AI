package view

import org.junit.jupiter.api.Test

import twoCars.model.TwoCarsModel
import twoCars.view.agent.PositionBasedQLearningAgent

/**
 * These tests show the basic functionality we were able to achieve in the time provided.
 * The Q agent can successfully collect circles, but isn't able to avoid squares.
 * We believe this is due to somewhere that I am choosing a max value, but we don't have sufficient time to fix it before the deadline.
 */
internal class PositionBasedQLearningAgentTest {

    @Test
    fun developmentModel() {
        var model = TwoCarsModel("Circle 0 15, Circle 1 20, Circle 1 30, Circle 0 35, Square 1 40")
        var learner = PositionBasedQLearningAgent(model, 40.0)
        learner.qSolve(200)
    }

    @Test
    fun circleAndStar() {
        var model = TwoCarsModel("Circle 0 15, Star 1 20, Circle 1 30, Star 1 30, Circle 0 35")
        var learner = PositionBasedQLearningAgent(model, 40.0)
        learner.qSolve(200)
    }

    @Test
    fun verySimpleModel() {
        val model = TwoCarsModel("Circle 0 25, Circle 1 30")

        var learner = PositionBasedQLearningAgent(model, 30.0)
        learner.qSolve(100)
    }
}