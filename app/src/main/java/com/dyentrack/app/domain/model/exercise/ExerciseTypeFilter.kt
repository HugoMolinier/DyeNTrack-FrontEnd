package com.dyentrack.app.domain.model.exercise

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dyentrack.app.R

enum class ExerciseTypeFilter(@StringRes val stringRes: Int) {
    ALL(R.string.filter_all),
    OFFICIAL(R.string.filter_official),
    FAVORITE(R.string.filter_favorite),
    USER_CREATED(R.string.filter_user_created);


    fun toDisplayString(context: Context): String {
        return context.getString(stringRes)
    }
}