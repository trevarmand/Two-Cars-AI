package twoCars.model

import java.lang.IllegalArgumentException

class Car {

    constructor(currentLane: Int, totalLanes: Int) {
        this.currentLane = currentLane
        this.totalLanes = totalLanes
        this.yPosn = 10
    }

    /**
     * What lane does the car currently occupy?
     */
    private var currentLane = 0
        get() = currentLane

    /**
     * The total number of lanes in the world.
     * Influences movement legality.
     */
    private val totalLanes: Int

    /**
     * The vertical position of the car.
     * For now, this won't change; in the future, that may be controllable by the agent.
     */
    private val yPosn: Int

    /**
     * Move the car left or right.
     */
    fun switchLane(direction : String) {
        if(direction == "left" && this.currentLane != 0){
            this.currentLane -= 1
        }
        else if(direction == "right" && this.currentLane != totalLanes - 1){
            this.currentLane += 1
        } else {
            throw IllegalArgumentException("switchLane direction must be 'left' or 'right'")
        }
    }
}