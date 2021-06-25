package view

import org.junit.Test
import twoCars.model.TwoCarsModel
import twoCars.model.TwoCarsModelInterface
import twoCars.model.learn.Move
import twoCars.view.agent.SimpleMDPLearningAgent
import kotlin.test.assertFalse

class SimpleMDPLearningAgentTest {
    var simpleModel : TwoCarsModelInterface
    var simpleReversedModel : TwoCarsModelInterface
    var reachableModel : TwoCarsModelInterface
    var decisionModel : TwoCarsModelInterface
    var simpleSquareModel : TwoCarsModelInterface
    var learner : SimpleMDPLearningAgent
    var reversedLearner : SimpleMDPLearningAgent
    var squareLearner : SimpleMDPLearningAgent
    var reachableLearner : SimpleMDPLearningAgent
    var decisionLearner : SimpleMDPLearningAgent

    constructor() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.simpleReversedModel = TwoCarsModel("Star 0 50, Star 0 100, Square 1 25, Circle 1 80, Circle 2 50, Square 2 100")
        this.simpleSquareModel = TwoCarsModel("Square 0 25, Square 0 70, Square 1 50, Square 2 35")
        // reachableModel: missed circle when Circle 1 70 was Circle 2 70
        this.reachableModel = TwoCarsModel("Circle 0 20, Star 0 80, Star 1 50, Circle 1 70, Circle 2 30")
        this.decisionModel = TwoCarsModel("Star 0 30, Square 1 25, Circle 2 30, Star 2 31")
        this.learner = SimpleMDPLearningAgent(simpleModel)
        this.reversedLearner = SimpleMDPLearningAgent(simpleReversedModel)
        this.squareLearner = SimpleMDPLearningAgent(simpleSquareModel)
        this.reachableLearner = SimpleMDPLearningAgent(reachableModel)
        this.decisionLearner = SimpleMDPLearningAgent(decisionModel)
    }

    private fun resetModel() {
        this.simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")
        this.simpleReversedModel = TwoCarsModel("Star 0 50, Star 0 100, Square 1 25, Circle 1 80, Circle 2 50, Square 2 100")
        this.simpleSquareModel = TwoCarsModel("Square 0 25, Square 0 70, Square 1 50, Square 2 35")
        this.reachableModel = TwoCarsModel("Circle 0 20, Star 0 80, Star 1 50, Circle 1 70, Circle 2 30")
        this.decisionModel = TwoCarsModel("Star 0 30, Square 1 25, Circle 2 30, Star 2 31")
        this.learner = SimpleMDPLearningAgent(simpleModel)
        this.reversedLearner = SimpleMDPLearningAgent(simpleReversedModel)
        this.squareLearner = SimpleMDPLearningAgent(simpleSquareModel)
        this.reachableLearner = SimpleMDPLearningAgent(reachableModel)
        this.decisionLearner = SimpleMDPLearningAgent(decisionModel)
    }

    @Test
    fun simpleLearnerRun() {
        resetModel()
        for (i in 0..100) {
            print("ITERATION ")
            println(i)
            simpleModel.step()
            learner.solve()
            var move = learner.getBestMove(simpleModel.getCarInfo().currentLane)
            print(" CURRENT LANE: ")
            println(simpleModel.getCarInfo().currentLane)
            simpleModel.switchLane(move)
            print("  MOVING: ")
            println(move)
        }

        // game should not have "ended"
        assertFalse { simpleModel.isGameOver() }
        //max score
        assert(simpleModel.getScore() == 7)
        // should be no more scrollers
        for (lane in simpleModel.getScrollers()) {
            assert(lane.isEmpty())
        }

        println()
    }

    @Test
    // same model as above but flipped: should still work the same
    fun simpleReversedLearnerRun() {
        resetModel()
        for (i in 0..100) {
            print("ITERATION ")
            println(i)
            simpleReversedModel.step()
            reversedLearner.solve()
            var move = reversedLearner.getBestMove(simpleReversedModel.getCarInfo().currentLane)
            print(" CURRENT LANE: ")
            println(simpleReversedModel.getCarInfo().currentLane)
            simpleReversedModel.switchLane(move)
            print("  MOVING: ")
            println(move)
        }

        // game should not have "ended"
        assertFalse { simpleReversedModel.isGameOver() }
        //max score
        assert(simpleReversedModel.getScore() == 7)
        // should be no more scrollers
        for (lane in simpleReversedModel.getScrollers()) {
            assert(lane.isEmpty())
        }

        println()
    }

    @Test
    // agent shouldn't accumulate score, goal should just be to avoid the squares
    fun avoidSquares() {
        resetModel()
        for (i in 0..100) {
            print("ITERATION ")
            println(i)
            simpleSquareModel.step()
            squareLearner.solve()
            var move = squareLearner.getBestMove(simpleSquareModel.getCarInfo().currentLane)
            print(" CURRENT LANE: ")
            println(simpleSquareModel.getCarInfo().currentLane)
            simpleSquareModel.switchLane(move)
            print("  MOVING: ")
            println(move)
        }

        //game should not have "ended"
        assertFalse { simpleSquareModel.isGameOver() }

        // no score
        assert(simpleSquareModel.getScore() == 0)
        // should be no more scrollers
        for (lane in simpleSquareModel.getScrollers()) {
            assert(lane.isEmpty())
        }

        println()
    }

    @Test
    // no squares, should be able to get all rewards
    fun reachableRewards() {
        resetModel()
        for (i in 0..100) {
            print("ITERATION ")
            println(i)
            reachableModel.step()
            reachableLearner.solve()
            var move = reachableLearner.getBestMove(reachableModel.getCarInfo().currentLane)
            print(" CURRENT LANE: ")
            println(reachableModel.getCarInfo().currentLane)
            reachableModel.switchLane(move)
            print("  MOVING: ")
            println(move)
        }

        //game should not have "ended"
        assertFalse { reachableModel.isGameOver() }

        // score from collecting all objects
        assert(reachableModel.getScore() == 9)
        // should be no more scrollers
        for (lane in reachableModel.getScrollers()) {
            assert(lane.isEmpty())
        }
    }

    @Test
    // needs to decide between two similar lanes
    fun decision() {
        resetModel()
        for (i in 0..100) {
            print("ITERATION ")
            println(i)
            decisionModel.step()
            decisionLearner.solve()
            var move = decisionLearner.getBestMove(decisionModel.getCarInfo().currentLane)
            print(" CURRENT LANE: ")
            println(decisionModel.getCarInfo().currentLane)
            decisionModel.switchLane(move)
            print("  MOVING: ")
            println(move)
        }

        //game should not have "ended"
        assertFalse { decisionModel.isGameOver() }

        // no score
        assert(decisionModel.getScore() == 4)
        // should be no more scrollers
        for (lane in decisionModel.getScrollers()) {
            assert(lane.isEmpty())
        }
    }
}