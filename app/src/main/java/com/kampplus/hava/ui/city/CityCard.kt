package com.kampplus.hava.ui.city

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.kampplus.hava.R
import com.kampplus.hava.model.City
import com.kampplus.hava.model.plakaCode
import com.kampplus.hava.ui.theme.KampplusHavaTheme
import com.kampplus.hava.ui.theme.Spacing
import com.kampplus.hava.ui.theme.tabularNumerals
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.miuixShape

@Composable
fun CityCard(
    city: City,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        showIndication = true,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.Large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Large),
        ) {
            PlateBadge(plate = city.plakaCode)

            // Şehir Adı ve Ülke/Bölge
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.ExtraSmall),
            ) {
                Text(
                    text = city.name,
                    style = MiuixTheme.textStyles.main,
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurface,
                )

                val details = listOfNotNull(city.admin1, city.country).filter { it.isNotBlank() }
                if (details.isNotEmpty()) {
                    Text(
                        text = details.distinct().joinToString(", "),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                }
            }

            if (onFavoriteClick != null) {
                IconButton(
                    onClick = onFavoriteClick,
                    minHeight = Spacing.MinTouchTarget,
                    minWidth = Spacing.MinTouchTarget,
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = stringResource(
                            if (isFavorite) R.string.remove_favorite else R.string.add_favorite,
                        ),
                        tint = if (isFavorite) {
                            MiuixTheme.colorScheme.primary
                        } else {
                            MiuixTheme.colorScheme.onSurfaceVariantActions
                        },
                    )
                }
            }

            Icon(
                imageVector = MiuixIcons.Basic.ArrowRight,
                contentDescription = stringResource(R.string.open_weather),
                tint = MiuixTheme.colorScheme.onSurfaceVariantActions,
            )
        }
    }
}

/**
 * Plaka rozeti: nötr kapsayıcı üzerinde sabit genişlikli rakamlar.
 */
@Composable
fun PlateBadge(
    plate: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = miuixShape(Spacing.Medium),
        color = MiuixTheme.colorScheme.secondaryContainer,
        contentColor = MiuixTheme.colorScheme.onSurfaceSecondary,
        modifier = modifier.size(Spacing.MinTouchTarget),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = plate,
                style = MiuixTheme.textStyles.headline2.tabularNumerals(),
                fontWeight = FontWeight.SemiBold,
                color = MiuixTheme.colorScheme.onSurfaceSecondary,
            )
        }
    }
}

private val previewCity = City(
    id = 1,
    name = "Ankara",
    latitude = 39.91987,
    longitude = 32.85427,
    country = "Türkiye",
    admin1 = "Ankara",
    plaka = "06",
)

@Preview(showBackground = true)
@Composable
fun CityCardPreview() {
    KampplusHavaTheme {
        Surface {
            CityCard(
                city = previewCity,
                onClick = {},
                modifier = Modifier.padding(Spacing.Medium),
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark", uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CityCardDarkPreview() {
    KampplusHavaTheme(darkTheme = true) {
        Surface {
            CityCard(
                city = previewCity,
                onClick = {},
                modifier = Modifier.padding(Spacing.Medium),
            )
        }
    }
}
