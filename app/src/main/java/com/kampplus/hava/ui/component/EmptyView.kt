package com.kampplus.hava.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.R
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.no_search_results),
    icon: ImageVector = Icons.Outlined.SearchOff,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.ExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(R.string.no_results_icon),
            modifier = Modifier.size(Spacing.MinTouchTarget),
            tint = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(modifier = Modifier.height(Spacing.Large))
        Text(
            text = message,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyViewPreview() {
    KampplusHavaTheme {
        Surface {
            EmptyView()
        }
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EmptyViewDarkPreview() {
    KampplusHavaTheme(darkTheme = true) {
        Surface {
            EmptyView()
        }
    }
}
