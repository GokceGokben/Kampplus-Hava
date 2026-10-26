package com.kampplus.hava.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.R
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isNetwork: Boolean = false,
    fallbackMessage: String = stringResource(R.string.error_loading_weather),
) {
    // Sistem istisna mesajları genelde İngilizce ve teknik; bağlantı hatasında yerelleştirilmiş metin gösterilir.
    val displayMessage = when {
        isNetwork -> stringResource(R.string.error_no_connection)
        message.isBlank() -> fallbackMessage
        else -> message
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Spacing.ExtraLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.ErrorOutline,
            contentDescription = stringResource(R.string.error_icon),
            modifier = Modifier.size(Spacing.MinTouchTarget),
            tint = MiuixTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(Spacing.Large))
        Text(
            text = displayMessage,
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Spacing.ExtraLarge))
        Button(
            onClick = onRetry,
            modifier = Modifier.heightIn(min = Spacing.MinTouchTarget),
            colors = ButtonDefaults.buttonColorsPrimary(),
        ) {
            // Kalın 17sp "büyük metin" sayılır; beyaz/primary kontrastı (≈3.8:1) AA için yeterli
            Text(
                text = stringResource(R.string.retry),
                style = MiuixTheme.textStyles.button,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorViewPreview() {
    KampplusHavaTheme {
        Surface {
            ErrorView(
                message = "İnternet bağlantısı kontrol edilemedi.",
                onRetry = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ErrorViewDarkPreview() {
    KampplusHavaTheme(darkTheme = true) {
        Surface {
            ErrorView(
                message = "İnternet bağlantısı kontrol edilemedi.",
                onRetry = {},
            )
        }
    }
}
