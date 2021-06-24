package view.agent

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import twoCars.model.TwoCarsModel
import twoCars.view.agent.PositionBasedQLearningAgent

internal class PositionBasedQLearningAgentTest {

    @Test
    fun developmentModel() {
        var model = TwoCarsModel("Circle 0 15, Circle 1 20, Circle 1 30, Square 1 40")
        var learner = PositionBasedQLearningAgent(model, 40.0)
        learner.qSolve(30000)
    }

    @Test
    fun init() {

    }


    @Test
    fun qSolve() {
        val model = TwoCarsModel("Circle 0 20, Circle 1 25, Circle 2 30, Square 2 35")

        var learner = PositionBasedQLearningAgent(model, 35.0)

//        var utilities = learner.makeUtilityMap(model)
//        for((y, lane) in model.getScrollers().withIndex()) {
//            println("Lane $y")
//            for(scroller in lane) {
//                print(scroller.type)
//                print(" at " + scroller.yPosn + " ")
//            }
//            println()
//        }
        learner.qSolve(30000)
    }
}