
package twoCars

import twoCars.model.TwoCarsModel


fun main(args : Array<String>) {
    // Describes a basic world: Shape lane yPosn, Shape lane yPosn....
    val simpleModel = TwoCarsModel("Circle 0 50, Square 0 100, Square 1 25, Circle 1 80, Star 2 50, Star 2 100")

    println(simpleModel.getCarInfo().currentLane)
    simpleModel.switchLane("left")
    println(simpleModel.getCarInfo().currentLane)

    // Test colliding with a circle
    // The score should keep incrementing; the circle still needs to be removed from the world after collection.
    for (i in 0..100) {
        simpleModel.step()
//        println(simpleModel.getScrollers()[0][0].yPosn)
        if (simpleModel.getCarInfo().yPosn == simpleModel.getScrollers()[0][0].yPosn) {
            print("\n\nCOLLISION! ")
            print(i)
            print(" SCORE: ")
            print(simpleModel.getScore())
        }
    }
}