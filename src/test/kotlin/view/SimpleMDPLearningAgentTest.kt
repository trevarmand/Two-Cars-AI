package view

import org.junit.Test
import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.view.agent.SimpleMDPLearningAgent

class SimpleMDPLearningAgentTest {
    var simpleModel : TwoCarsModelInterface
    var learner : SimpleMDPLearningAgent

    constructor() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.learner = SimpleMDPLearningAgent(simpleModel)
    }

    private fun resetModel() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.learner = SimpleMDPLearningAgent(simpleModel)
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

        //max score
        assert(simpleModel.getScore() == 7)
        // should be no more scrollers
        for (lane in simpleModel.getScrollers()) {
            assert(lane.isEmpty())
        }
    }
}