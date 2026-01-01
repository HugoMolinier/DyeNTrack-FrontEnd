package com.dyentrack.app.domain.usecase.muscleRate

import com.dyentrack.app.domain.model.MuscleRate
import com.dyentrack.app.domain.model.PlannedExerciseVO
import com.dyentrack.app.domain.model.RateSetSomme
import javax.inject.Inject
import kotlin.math.ceil

class ComputePlannedExerciseMuscleRateUseCase @Inject constructor() {

    /**
     * Calcule le MuscleRate d'un plannedExercise
     * @param plannedExercise l'exercice planifié
     */
    fun execute(
        plannedExercise: PlannedExerciseVO,

        ): MuscleRate {
        val sets = plannedExercise.sets.orEmpty()
        var sommePond = 0.0
        var numberSet = 0
        var numberSetFail = 0
        var numberRep = 0.0

        if (plannedExercise.lateraliteId == 2L) {
            // ----- UNILATERAL -----
            val grouped = sets.groupBy { it.setOrder }
            grouped.forEach { (_, groupedSets) ->
                val avgReps = groupedSets.map { it.repsNumber }.average()
                val charge = if (groupedSets.first().charge == 0.0) 1.0 else groupedSets.first().charge
                sommePond += avgReps * charge
                numberSet += 1
                if (groupedSets.first().rir == 0) numberSetFail += 1
                numberRep += avgReps
            }
            numberSet = ceil(numberSet / 2.0).toInt()
        } else {
            sets.forEach {
                val charge = if (it.charge == 0.0) 1.0 else it.charge
                sommePond += it.repsNumber * charge
                numberSet += 1
                if (it.rir == 0) numberSetFail += 1
                numberRep += it.repsNumber
            }
        }



        return MuscleRate(
            numberExercise = 1,
            numberSet = numberSet,
            rateSetSomme = RateSetSomme(
                numberSetFail = numberSetFail,
                sommePond = sommePond,
                numberRep = numberRep
            )
        )
    }
}