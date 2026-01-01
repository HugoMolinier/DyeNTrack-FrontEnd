package com.dyentrack.app.domain.usecase

import com.dyentrack.app.domain.model.DayDataRequest
import com.dyentrack.app.domain.model.SetData
import com.dyentrack.app.domain.model.SetSide
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLastExercisePerfBetweenDateUseCase @Inject constructor(){

    operator fun invoke(
        dayDataFlow: List<DayDataRequest>?,
        idExercise: Long,
        dateRecent: String,
        dateBefore: String
    ): Map<String, List<SetData>> {

        if (dayDataFlow == null) return emptyMap()

        val result = mutableMapOf<String, MutableList<SetData>>()

        generateSequence(LocalDate.parse(dateRecent)) { it.minusDays(1) }
            .takeWhile { !it.isBefore(LocalDate.parse(dateBefore)) }
            .forEach { day ->
                val dayKey = day.format(DateTimeFormatter.ISO_DATE)

                val dayData = dayDataFlow.find { it.dayData == dayKey } ?: return@forEach

                val exercise = dayData.seanceTrack
                    ?.plannedExercises
                    ?.firstOrNull { it.exerciseId == idExercise }

                val setsVO = exercise?.sets.orEmpty()
                if (setsVO.isNotEmpty()) {
                    val setsData = mutableListOf<SetData>()

                    if (exercise?.lateraliteId == 2L) {
                        // Unilatéral : grouper par setOrder pour lier gauche/droite
                        val setsByOrder = setsVO.groupBy { it.setOrder }

                        setsByOrder.forEach { (_, group) ->
                            val left = group.firstOrNull { it.side == SetSide.LEFT }
                            val right = group.firstOrNull { it.side == SetSide.RIGHT }

                            left?.let { l ->
                                val linkedRightSet = right?.let { r ->
                                    SetData(
                                        type = r.typeOfSet,
                                        charge = r.charge,
                                        reps = r.repsNumber,
                                        rir = r.rir,
                                        side = r.side
                                    )
                                }

                                setsData.add(
                                    SetData(
                                        type = l.typeOfSet,
                                        charge = l.charge,
                                        reps = l.repsNumber,
                                        rir = l.rir,
                                        side = l.side,
                                        linkedRightSet = linkedRightSet
                                    )
                                )
                            }
                        }

                    } else {
                        // Bilatéral ou autre : ajouter directement
                        setsVO.forEach { b ->
                            setsData.add(
                                SetData(
                                    type = b.typeOfSet,
                                    charge = b.charge,
                                    reps = b.repsNumber,
                                    rir = b.rir,
                                    side = b.side
                                )
                            )
                        }
                    }

                    result.getOrPut(dayKey) { mutableListOf() }.addAll(setsData)
                }
            }

        return result
    }
}
