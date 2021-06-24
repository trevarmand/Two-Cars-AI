package view

import org.junit.Test
import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.view.agent.SimpleMDPLearningAgent
import kotlin.test.assertFalse

class SimpleMDPLearningAgentTest {
    var simpleModel : TwoCarsModelInterface
    var simpleSquareModel : TwoCarsModelInterface
    var learner : SimpleMDPLearningAgent
    var squareLearner : SimpleMDPLearningAgent

    constructor() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.simpleSquareModel = TwoCarsModel("Square 0 25, Square 0 70, Square 1 50, Square 2 35")
        this.learner = SimpleMDPLearningAgent(simpleModel)
        this.squareLearner = SimpleMDPLearningAgent(simpleSquareModel)
    }

    private fun resetModel() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.simpleSquareModel = TwoCarsModel("Square 0 25, Square 0 70, Square 1 50, Square 2 35")
        this.learner = SimpleMDPLearningAgent(simpleModel)
        this.squareLearner = SimpleMDPLearningAgent(simpleSquareModel)
    }

    @Test
    fun simpleLearnerRun() {
        resetModel()
        for (i in 0..100) {
            simpleModel.step()
            learner.solve()
            var move = learner.getBestMove(simpleModel.getCarInfo().currentLane)
            println(simpleModel.getCarInfo().currentLane)
            simpleModel.switchLane(move)
        }

        // game should not have "ended"
        assertFalse { simpleSquareModel.isGameOver() }
        //max score
        assert(simpleModel.getScore() == 7)
        // should be no more scrollers
        for (lane in simpleModel.getScrollers()) {
            assert(lane.isEmpty())
        }
    }

    @Test
    // agent shouldn't accumulate score, goal should just be to avoid the squares
    fun avoidSquares() {
        resetModel()
        for (i in 0..100) {
            if (i != 35) {
                simpleSquareModel.step()
                squareLearner.solve()
                var move = squareLearner.getBestMove(simpleSquareModel.getCarInfo().currentLane)
                println(simpleSquareModel.getCarInfo().currentLane)
                simpleSquareModel.switchLane(move)
            } else {
                simpleSquareModel.step()
                squareLearner.solve()
                var move = squareLearner.getBestMove(simpleSquareModel.getCarInfo().currentLane)
                println(simpleSquareModel.getCarInfo().currentLane)
                simpleSquareModel.switchLane(move)
            }
            /*
            if (simpleSquareModel.isGameOver()) {
                println("Bug")
            }

             */
        }

        //game should not have "ended"
        assertFalse { simpleSquareModel.isGameOver() }

        // no score
        assert(simpleSquareModel.getScore() == 0)
        // should be no more scrollers
        for (lane in simpleSquareModel.getScrollers()) {
            assert(lane.isEmpty())
        }
    }
}