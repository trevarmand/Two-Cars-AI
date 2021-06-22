package twoCars

import twoCars.model.TwoCarsModel
import twoCars.view.agent.SimpleQLearningAgent
import twoCars.model.learn.Move

fun main(args : Array<String>) {
    // Describes a basic world: Shape lane yPosn, Shape lane yPosn....
    val simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")

    // learner: simple for now
    // should we be passing in copy of model? Can't see us mutating it at all here
    // TODO: allow user to specify learner via input parameter
    val learner = SimpleQLearningAgent(simpleModel)

    // Test colliding with a circle
    // The score should keep incrementing; the circle still needs to be removed from the world after collection.
    for (i in 0..100) {
        // we should probably be stepping before making a decision right?
        simpleModel.step()
        learner.solve()
        var move = learner.getBestMove(simpleModel.getCarInfo().currentLane)
        println(simpleModel.getCarInfo().currentLane)
        simpleModel.switchLane(move)

        if (simpleModel.isGameOver()) {
            print("\n\nCOLLISION! ")
            print(i)
            print(" SCORE: ")
            print(simpleModel.getScore())
            return
        }
    }

    print("\n\nNO COLLISION DETECTED! ")
    print(" SCORE: ")
    print(simpleModel.getScore())
}