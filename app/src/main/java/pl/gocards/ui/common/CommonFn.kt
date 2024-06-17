package pl.gocards.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

/**
 * @author Grzegorz Ziemski
 */

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }