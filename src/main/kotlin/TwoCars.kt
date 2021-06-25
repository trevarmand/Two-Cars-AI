package twoCars

import twoCars.model.TwoCarsModel
import twoCars.view.agent.SimpleMDPLearningAgent

// entry point of program
// includes a key test for each learner with print output
fun main(args : Array<String>) {
    mdpExample()
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