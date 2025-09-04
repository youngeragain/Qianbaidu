package xcj.app.qianbaidu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

@Composable
fun SettingsPage(
    onBack: () -> Unit,
    onClearHistory: () -> Unit,
    onClearCaches: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 12.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = stringResource(R.string.settings),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Card(
                    onClick = onClearHistory,
                    modifier = Modifier
                        .widthIn(min = TextFieldDefaults.MinWidth)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        text = stringResource(R.string.clear_history),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Card(
                    onClick = onClearCaches,
                    modifier = Modifier
                        .widthIn(min = TextFieldDefaults.MinWidth)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        text = stringResource(R.string.clear_caches),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


        }
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FilledTonalIconButton(
                onClick = onBack
            ) {
                Icon(
                    painter = painterResource(R.drawable.keyboard_arrow_left_24px),
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    }
}