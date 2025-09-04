package xcj.app.qianbaidu

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.random.Random

@Composable
fun InputBox() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Enter text") }
    )
}

@Preview(showBackground = true)
@Composable
fun InputBoxPreview() {
    InputBox()
}

@Composable
fun GlowingLightInputBox(
    modifier: Modifier = Modifier,
    initialGlowColor: Color = Color.Yellow, // Renamed from glowColor
    glowRadius: Dp = 10.dp,
    animationDurationMillis: Int = 1500,
    minGlowFactor: Float = 0.7f,
    maxGlowFactor: Float = 1.3f,
    minGlowAlpha: Float = 0.4f,
    maxGlowAlpha: Float = 1.0f,
    randomizeColor: Boolean = false,
    colorChangeIntervalMillis: Int = 3000,
    colorTransitionDurationMillis: Int = 1500,
    contentAlignment: Alignment = Alignment.Center,
    content: (@Composable () -> Unit)? = null
) {
    // Animation for glow size pulsation
    val infiniteTransition = rememberInfiniteTransition(label = "glowSizeTransition")
    val animatedGlowFactor by infiniteTransition.animateFloat(
        initialValue = minGlowFactor,
        targetValue = maxGlowFactor,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animationDurationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowFactorAnimation"
    )

    // Animation for glow alpha pulsation based on glow size
    val normalizedFactor = if ((maxGlowFactor - minGlowFactor) == 0f) {
        0.5f
    } else {
        ((animatedGlowFactor - minGlowFactor) / (maxGlowFactor - minGlowFactor)).coerceIn(0f, 1f)
    }
    val animatedAlpha =
        (minGlowAlpha + (maxGlowAlpha - minGlowAlpha) * normalizedFactor).coerceIn(0f, 1f)

    // State for the target color (for random color animation)
    var targetColor by remember { mutableStateOf(initialGlowColor) }

    // LaunchedEffect to change targetColor randomly if randomizeColor is true
    if (randomizeColor) {
        LaunchedEffect(key1 = randomizeColor, key2 = colorChangeIntervalMillis) {
            while (isActive) {
                delay(colorChangeIntervalMillis.toLong())
                targetColor = Color(
                    red = Random.nextInt(255),
                    green = Random.nextInt(255),
                    blue = Random.nextInt(255),
                    alpha = 255 // Opaque base color, alpha will be applied later
                )
            }
        }
    } else {
        // If not randomizing, ensure targetColor is the initialGlowColor
        // This handles the case where randomizeColor might be toggled at runtime
        LaunchedEffect(initialGlowColor) {
            targetColor = initialGlowColor
        }
    }


    // Animate the base color towards the targetColor
    val animatedBaseColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = if (randomizeColor) colorTransitionDurationMillis else 0), // No animation if not randomizing
        label = "glowColorAnimation"
    )

    // Combine animated base color with animated alpha
    val finalAnimatedGlowColor = animatedBaseColor.copy(alpha = animatedAlpha)
    val animatedActualGlowRadius = glowRadius * 1


    Box(
        modifier = modifier
            .shadow(
                shape = CircleShape,
                elevation = animatedActualGlowRadius,
                spotColor = finalAnimatedGlowColor,
                ambientColor = finalAnimatedGlowColor,
                clip = false
            ).padding(glowRadius),
        contentAlignment = contentAlignment
    ) {
        content?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
fun GlowingBoxPreview() {
    Box(modifier = Modifier.padding(32.dp)) {
        GlowingLightInputBox(
            initialGlowColor = Color.Magenta,
            glowRadius = 3.dp, // Increased radius for better preview
            modifier = Modifier.wrapContentSize().clip(CircleShape),
            randomizeColor = true, // Enable random color for preview
            minGlowAlpha = 0.3f,
            maxGlowAlpha = 0.8f
        ) {
            var text by remember {
                mutableStateOf("")
            }
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                shape = CircleShape,
                placeholder = {
                    Text(text="text something")
                },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}
