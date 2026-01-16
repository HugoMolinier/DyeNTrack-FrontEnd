package com.dyentrack.app.domain.model.enums

enum class TypeOfAction {
    UPDATEHEALTH,

    UPDATESEANCE,

    UPDATEBOTH,


    FORCESEANCEUPDATE // pour le cas Empty Seance vs existingSeance
}