package twoCars

import twoCars.model.TwoCarsModel
import twoCars.view.agent.PositionBasedQLearningAgent
import twoCars.view.agent.SimpleMDPLearningAgent

// entry point of program
// includes a key test for each learner with print output
fun main(args : Array<String>) {
    mdpExample()
    qLearnExamples()
}

fun mdpExample() {
    // Describes a basic world: Shape lane yPosn, Shape lane yPosn....
    val simpleModel = TwoCarsModel("Star 0 50, Star 0 100, Square 1 25, Circle 1 80, Circle 2 50, Square 2 100")
    println("Star 0 50, Star 0 100, Square 1 25, Circle 1 80, Circle 2 50, Square 2 100")

    // learner
    val learner = SimpleMDPLearningAgent(simpleModel)

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

    println()
    print("SCORE: ")
    println(simpleModel.getScore())
    println()
}


fun qLearnExamples() {
    // Unforunately, this agent isn't smart enough to avoid squares.
    var model = TwoCarsModel("Circle 0 15, Circle 1 20, Star 0 30, Circle 0 35, Square 1 40")
    var learner = PositionBasedQLearningAgent(model, 40.0)
    learner.qSolve(200)

}