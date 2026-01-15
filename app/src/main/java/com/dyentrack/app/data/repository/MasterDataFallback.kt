package com.dyentrack.app.data.repository

import com.dyentrack.app.domain.model.ReturnIdAndName
import com.dyentrack.app.domain.model.exercise.EquipmentInfo
import com.dyentrack.app.domain.model.exercise.Exercise
import com.dyentrack.app.domain.model.exercise.MuscleInfos

object MasterDataFallback {

    val muscleGroups: List<ReturnIdAndName> = listOf(
        ReturnIdAndName(1, "Bras", "Arms"),
        ReturnIdAndName(2, "Épaules", "Shoulder"),
        ReturnIdAndName(3, "Pecs", "Chest"),
        ReturnIdAndName(4, "Dos", "Back"),
        ReturnIdAndName(5, "Jambes", "Leg"),
        ReturnIdAndName(6, "Abdos", "Core")
    )
    val muscles: List<ReturnIdAndName> = listOf(
        ReturnIdAndName(1, "Triceps", "Triceps", 1),
        ReturnIdAndName(2, "Biceps", "Biceps", 1),
        ReturnIdAndName(3, "Brachialis", "Brachialis", 1),
        ReturnIdAndName(4, "Avant Bras", "Forearm", 1),
        ReturnIdAndName(5, "Épaule", "Shoulder", 2),
        ReturnIdAndName(6, "Infra-épineux", "Infraspinatus", 2),
        ReturnIdAndName(7, "Pectoraux", "Pectorals", 3),
        ReturnIdAndName(8, "Trapèzes", "Trapezius", 4),
        ReturnIdAndName(9, "Dorsaux", "Lats", 4),
        ReturnIdAndName(10, "Lombaires", "Lower Back", 4),
        ReturnIdAndName(11, "Grand Rond", "Teres Major", 4),
        ReturnIdAndName(12, "Quadriceps", "Quadriceps", 5),
        ReturnIdAndName(13, "Ischio-jambiers", "Hamstrings", 5),
        ReturnIdAndName(14, "Mollets", "Calves", 5),
        ReturnIdAndName(15, "Fessiers", "Glutes", 5),
        ReturnIdAndName(16, "Abdominaux", "Abs", 6),
        ReturnIdAndName(17, "Cou", "Neck", 6),
        ReturnIdAndName(19, "Oblique", "Oblique", 6),
        ReturnIdAndName(20, "Adducteur", "Adductor", 5)
    )

    val lateralities: List<ReturnIdAndName> = listOf(
        ReturnIdAndName(1, "Bilatéral", "Bilateral"),
        ReturnIdAndName(2, "Unilatéral", "Unilateral"),
        ReturnIdAndName(3, "Alterné", "Alternated")
    )

    val equipments: List<ReturnIdAndName> = listOf(
        ReturnIdAndName(1, "Haltère", "Dumbbell"),
        ReturnIdAndName(2, "Barre", "Bar"),
        ReturnIdAndName(3, "Poulie", "Pulley"),
        ReturnIdAndName(4, "Double poulie", "Double pulley"),
        ReturnIdAndName(5, "Machine", "Machine"),
        ReturnIdAndName(6, "Smith machine", "Smith Machine"),
        ReturnIdAndName(7, "Poids du corps", "Bodyweight"),
        ReturnIdAndName(8, "Élastique", "Elastic Band")
    )

    val officialExercises: List<Exercise> = listOf(

        Exercise(
            idExercise = 1,
            nameFR = "Développé Couché",
            nameEN = "Bench Press",
            muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(5), MuscleInfos(7)
            ),
            mainFocusGroup = 3,
            mainMuscle = 7,
            defaultEquipment = 2,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6)
            )
        ),

        Exercise(
            idExercise = 2, nameFR = "Écarté Couché", nameEN = "Pecs Fly", muscleInfos = listOf(
                MuscleInfos(6), MuscleInfos(7)
            ), mainFocusGroup = 3, mainMuscle = 7, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(3), EquipmentInfo(4), EquipmentInfo(5)
            )
        ),

        Exercise(
            idExercise = 3, nameFR = "Tractions", nameEN = "Pull-ups", muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(8), MuscleInfos(9)
            ), mainFocusGroup = 4, mainMuscle = 9, defaultEquipment = 7, equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(5), EquipmentInfo(7), EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 4,
            nameFR = "Rowing (Lats)",
            nameEN = "Rowing (Lats)",
            muscleInfos = listOf(
                MuscleInfos(2), MuscleInfos(9), MuscleInfos(10)
            ),
            mainFocusGroup = 4,
            mainMuscle = 9,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 5,
            nameFR = "Squat",
            nameEN = "Squat",
            muscleInfos = listOf(
                MuscleInfos(12), MuscleInfos(14), MuscleInfos(15)
            ),
            mainFocusGroup = 5,
            mainMuscle = 12,
            defaultEquipment = 2,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(5),
                EquipmentInfo(6),
                EquipmentInfo(7)
            ),
        ),
        Exercise(
            idExercise = 6, nameFR = "Soulevé de Terre", nameEN = "Deadlift", muscleInfos = listOf(
                MuscleInfos(10), MuscleInfos(12), MuscleInfos(13), MuscleInfos(15)
            ), mainFocusGroup = 4, mainMuscle = 10, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5)
            )
        ),

        Exercise(
            idExercise = 7, nameFR = "Curl Biceps", nameEN = "Biceps Curl", muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(2)
            ), mainFocusGroup = 1, mainMuscle = 2, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 8,
            nameFR = "Extension Triceps haute",
            nameEN = "Overhead Triceps Extension",
            muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(2)
            ),
            mainFocusGroup = 1,
            mainMuscle = 1,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 9, nameFR = "Crunch", nameEN = "Crunch", muscleInfos = listOf(
                MuscleInfos(16)
            ), mainFocusGroup = 6, mainMuscle = 16, defaultEquipment = 7, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(7)
            )
        ),

        Exercise(
            idExercise = 10,
            nameFR = "Relevé de jambes",
            nameEN = "Leg Raises",
            muscleInfos = listOf(
                MuscleInfos(16)
            ),
            mainFocusGroup = 6,
            mainMuscle = 16,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(5), EquipmentInfo(7), EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 11,
            nameFR = "Flexion latérale",
            nameEN = "Side Bend",
            muscleInfos = listOf(
                MuscleInfos(19)
            ),
            mainFocusGroup = 6,
            mainMuscle = 19,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6),
                EquipmentInfo(7)
            )
        ),

        Exercise(
            idExercise = 12,
            nameFR = "Soulevé de terre Roumain",
            nameEN = "Romanian Deadlift",
            muscleInfos = listOf(
                MuscleInfos(10), MuscleInfos(13), MuscleInfos(15)
            ),
            mainFocusGroup = 5,
            mainMuscle = 15,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6)
            )
        ),

        Exercise(
            idExercise = 13,
            nameFR = "Reverse Nordic curl",
            nameEN = "Reverse Nordic Curl",
            muscleInfos = listOf(
                MuscleInfos(12)
            ),
            mainFocusGroup = 5,
            mainMuscle = 12,
            defaultEquipment = 7,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(7),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 14, nameFR = "Leg curl", nameEN = "Leg Curl", muscleInfos = listOf(
                MuscleInfos(13), MuscleInfos(14)
            ), mainFocusGroup = 5, mainMuscle = 13, defaultEquipment = 5, equipmentInfo = listOf(
                EquipmentInfo(3), EquipmentInfo(4), EquipmentInfo(5)
            )
        ),

        Exercise(
            idExercise = 15,
            nameFR = "Presse à mollet",
            nameEN = "Calf Press",
            muscleInfos = listOf(
                MuscleInfos(14)
            ),
            mainFocusGroup = 5,
            mainMuscle = 14,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(5),
                EquipmentInfo(6),
                EquipmentInfo(7)
            )
        ),

        Exercise(
            idExercise = 16,
            nameFR = "Presse à adducteur",
            nameEN = "Adductor Press",
            muscleInfos = listOf(
                MuscleInfos(20)
            ),
            mainFocusGroup = 5,
            mainMuscle = 20,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(3), EquipmentInfo(4), EquipmentInfo(5)
            )
        ),

        Exercise(
            idExercise = 17,
            nameFR = "Développé militaire",
            nameEN = "Military Press",
            muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(5), MuscleInfos(7)
            ),
            mainFocusGroup = 2,
            mainMuscle = 5,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6)
            )
        ),

        Exercise(
            idExercise = 18, nameFR = "Dips", nameEN = "Dips", muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(7)
            ), mainFocusGroup = 3, mainMuscle = 7, defaultEquipment = 7, equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(5), EquipmentInfo(7), EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 19,
            nameFR = "Extension triceps basse",
            nameEN = "Triceps Pushdown",
            muscleInfos = listOf(
                MuscleInfos(1)
            ),
            mainFocusGroup = 1,
            mainMuscle = 1,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 20,
            nameFR = "Élévation latérale",
            nameEN = "Lateral Raise",
            muscleInfos = listOf(
                MuscleInfos(5)
            ),
            mainFocusGroup = 2,
            mainMuscle = 5,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1), EquipmentInfo(3), EquipmentInfo(4), EquipmentInfo(5)
            )
        ),
        Exercise(
            idExercise = 21,
            nameFR = "Développé incliné",
            nameEN = "Incline Bench Press",
            muscleInfos = listOf(
                MuscleInfos(1), MuscleInfos(5), MuscleInfos(7)
            ),
            mainFocusGroup = 3,
            mainMuscle = 7,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6)
            )
        ),

        Exercise(
            idExercise = 22,
            nameFR = "L-Fly",
            nameEN = "External Rotation (L-Fly)",
            muscleInfos = listOf(
                MuscleInfos(6)
            ),
            mainFocusGroup = 2,
            mainMuscle = 6,
            defaultEquipment = 3,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 23,
            nameFR = "Ecarté couché decline",
            nameEN = "Decline Pecs Fly",
            muscleInfos = listOf(
                MuscleInfos(7)
            ),
            mainFocusGroup = 3,
            mainMuscle = 7,
            defaultEquipment = 3,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 24,
            nameFR = "Tirage Horizontal",
            nameEN = "Seated Cable Row",
            muscleInfos = listOf(
                MuscleInfos(9)
            ),
            mainFocusGroup = 4,
            mainMuscle = 9,
            defaultEquipment = 3,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 25,
            nameFR = "Tirage Vertical prise sérré",
            nameEN = "Close-Grip Lat Pulldown",
            muscleInfos = listOf(
                MuscleInfos(2), MuscleInfos(9)
            ),
            mainFocusGroup = 4,
            mainMuscle = 9,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 26,
            nameFR = "Tirage Vertical prise large",
            nameEN = "Wide-grip Lat Pulldown",
            muscleInfos = listOf(
                MuscleInfos(8), MuscleInfos(9), MuscleInfos(11)
            ),
            mainFocusGroup = 4,
            mainMuscle = 8,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 27,
            nameFR = "Rowing (Trapèze)",
            nameEN = "Rowing (Traps)",
            muscleInfos = listOf(
                MuscleInfos(8), MuscleInfos(11)
            ),
            mainFocusGroup = 4,
            mainMuscle = 8,
            defaultEquipment = 2,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(6),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 28, nameFR = "Curl marteau", nameEN = "Hammer Curl", muscleInfos = listOf(
                MuscleInfos(2), MuscleInfos(3)
            ), mainFocusGroup = 1, mainMuscle = 3, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 30, nameFR = "IO Raise", nameEN = "IO Raise", muscleInfos = listOf(
                MuscleInfos(5), MuscleInfos(8)
            ), mainFocusGroup = 4, mainMuscle = 8, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),
        Exercise(
            idExercise = 31, nameFR = "Oiseau", nameEN = "Rear Delt Fly", muscleInfos = listOf(
                MuscleInfos(5)
            ), mainFocusGroup = 2, mainMuscle = 5, defaultEquipment = 3, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 32, nameFR = "Neck Curl", nameEN = "Neck Curl", muscleInfos = listOf(
                MuscleInfos(17)
            ), mainFocusGroup = 6, mainMuscle = 17, defaultEquipment = 7, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(7),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 33, nameFR = "Curl Décliné", nameEN = "Decline Curl", muscleInfos = listOf(
                MuscleInfos(2)
            ), mainFocusGroup = 1, mainMuscle = 2, defaultEquipment = 1, equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 34,
            nameFR = "Flexion Poignet",
            nameEN = "Wrist Curl",
            muscleInfos = listOf(
                MuscleInfos(4)
            ),
            mainFocusGroup = 1,
            mainMuscle = 4,
            defaultEquipment = 1,
            equipmentInfo = listOf(
                EquipmentInfo(1),
                EquipmentInfo(2),
                EquipmentInfo(3),
                EquipmentInfo(4),
                EquipmentInfo(5),
                EquipmentInfo(8)
            )
        ),

        Exercise(
            idExercise = 35,
            nameFR = "Extension Quadriceps",
            nameEN = "Leg Extension",
            muscleInfos = listOf(
                MuscleInfos(12)
            ),
            mainFocusGroup = 5,
            mainMuscle = 12,
            defaultEquipment = 5,
            equipmentInfo = listOf(
                EquipmentInfo(3), EquipmentInfo(4), EquipmentInfo(5), EquipmentInfo(8)
            )
        ),


        )
}