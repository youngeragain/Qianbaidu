@file:OptIn(
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalHazeMaterialsApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)

package xcj.app.qianbaidu

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsIgnoringVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsIgnoringVisibility
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import xcj.app.qianbaidu.data.model.AIModel
import xcj.app.qianbaidu.data.model.IDProvider
import xcj.app.qianbaidu.data.model.QA
import xcj.app.qianbaidu.data.model.QAState
import xcj.app.qianbaidu.ui.theme.AppTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ConversationPage(onSettingClick: () -> Unit) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val viewModel: MainViewModel = viewModel<MainViewModel>(viewModelStoreOwner = activity)
    val currentQAState by viewModel.currentQAState.collectAsState()
    var currentHistoryQA by remember {
        mutableStateOf<QA?>(null)
    }
    val qaHistory = viewModel.qaHistory
    val availableAIModels = viewModel.availableAIModels
    var inputText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    val hazeState = rememberHazeState()

    var currentAiModel by remember {
        mutableStateOf<AIModel?>(null)
    }
    val inputAreaEnable by remember {
        derivedStateOf {
            currentQAState !is QAState.InResponse && availableAIModels.isNotEmpty()
        }
    }

    val okButtonEnable by remember {
        derivedStateOf {
            inputText.isNotBlank() && currentQAState !is QAState.InResponse && availableAIModels.isNotEmpty()
        }
    }

    val simpleDateFormat = remember {
        SimpleDateFormat.getDateTimeInstance()
    }

    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(currentQAState) {
        if (currentQAState is QAState.ResponseDone) {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
        }
    }

    LaunchedEffect(availableAIModels, availableAIModels.size) {
        if (availableAIModels.isNotEmpty() && currentAiModel == null) {
            currentAiModel = availableAIModels.first()
        }
    }
    LaunchedEffect(qaHistory, qaHistory.size, currentQAState) {
        if (qaHistory.isNotEmpty()) {
            currentHistoryQA = (currentQAState as? QAState.QAProvider)?.qa ?: qaHistory.last()
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentQAState, transitionSpec = {
                (fadeIn(
                    animationSpec = tween(
                        350, delayMillis = 90
                    )
                ) + scaleIn(
                    initialScale = 0.92f, animationSpec = tween(350, delayMillis = 90)
                )).togetherWith(fadeOut(animationSpec = tween(90)))
            }) { targetQAState ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            ) {
                when (targetQAState) {
                    is QAState.InResponse -> {
                        LoadingIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    is QAState.ResponseDone -> {
                        val done = targetQAState
                        Column(
                            modifier = Modifier
                                .hazeSource(hazeState)
                                .verticalScroll(scrollState)
                                .padding(top = 120.dp, bottom = 120.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SelectionContainer {
                                Text(
                                    text = done.qa.question,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Markdown(
                                content = done.qa.answer?.trimIndent() ?: "",
                                colors = markdownColor(),
                                typography = markdownTypography(),
                                imageTransformer = Coil3ImageTransformerImpl,
                                error = {
                                    Text(text = done.qa.answer?.trimIndent() ?: "")
                                })

                        }
                    }

                    QAState.Init -> {
                        // Optionally, show a placeholder or welcome message
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = stringResource(R.string.ask_something_to_get_started))
                        }
                    }

                    else -> Unit
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(
                    top = WindowInsets.statusBarsIgnoringVisibility.asPaddingValues()
                        .calculateTopPadding()
                )

        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                var showActions by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    onClick = {
                        showActions = !showActions
                    }) {
                    val rotateState by animateFloatAsState(
                        if (showActions) {
                            180f
                        } else {
                            0f
                        }
                    )
                    Icon(
                        modifier = Modifier.rotate(rotateState), painter = painterResource(
                            R.drawable.keyboard_arrow_right_24px
                        ),
                        contentDescription = stringResource(R.string.expand)
                    )
                }
                AnimatedVisibility(showActions) {
                    Row {
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onSettingClick
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.settings_24px),
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                }
                AnimatedVisibility (showActions && availableAIModels.isNotEmpty()) {
                    Row {
                        Spacer(modifier = Modifier.width(6.dp))
                        DropDownSelection(
                            current = currentAiModel,
                            dataList = availableAIModels,
                            onPreviewClick = {

                            },
                            preview = { aiModel ->
                                AIModelChipContent(aiModel)
                            },
                            details = { aiModel ->
                                SuggestionChip(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .hazeEffect(
                                            hazeState, HazeMaterials.thin()
                                        ), onClick = {
                                        currentAiModel = aiModel
                                    }, label = {
                                        AIModelChipContent(aiModel)
                                    }, shape = CircleShape
                                )
                            })
                    }
                }
                AnimatedVisibility (showActions && qaHistory.isNotEmpty()) {
                    Row {
                        Spacer(modifier = Modifier.width(6.dp))
                        DropDownSelection(
                            current = currentHistoryQA,
                            dataList = qaHistory,
                            onPreviewClick = {
                                val currentHistoryQA = currentHistoryQA
                                if (currentHistoryQA != null) {
                                    viewModel.showQA(currentHistoryQA)
                                }
                            },
                            preview = { qa ->
                                QAChipContent(qa, simpleDateFormat)
                            },
                            details = { qa ->
                                SuggestionChip(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .hazeEffect(
                                            hazeState, HazeMaterials.thin()
                                        ), onClick = {
                                        viewModel.showQA(qa)
                                    }, label = {
                                        QAChipContent(qa, simpleDateFormat)
                                    }, shape = CircleShape
                                )
                            })
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    start = 12.dp,
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp + WindowInsets.navigationBarsIgnoringVisibility.asPaddingValues()
                        .calculateBottomPadding()
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box() {
                    TextField(
                        enabled = inputAreaEnable,
                        shape = CircleShape,
                        placeholder = {
                            Text(text = stringResource(R.string.text_something))
                        },
                        colors = TextFieldDefaults.colors().copy(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        ),
                        value = inputText,
                        onValueChange = { inputText = it },
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(horizontal = 4.dp)
                    ) {
                        Button(
                            enabled = okButtonEnable,
                            onClick = {
                                val currentAIModel = currentAiModel
                                if (currentAIModel != null && inputText.isNotBlank()) {
                                    viewModel.processUserInput(currentAiModel!!, inputText)
                                }
                            },
                        ) {
                            Text(stringResource(R.string.ok))
                        }
                    }
                }
            }

        }

    }
}

@Composable
fun AIModelChipContent(aiModel: AIModel) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Text(text = stringResource(R.string.model), fontSize = 10.sp)
        Text(text = aiModel.name, fontSize = 12.sp)
    }
}

@Composable
fun QAChipContent(qa: QA, dateFormat: DateFormat) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Text(
            text = dateFormat.format(qa.timestamp), fontSize = 10.sp
        )
        Text(
            text = qa.question.replace("\n", "").replaceIndent(),
            maxLines = 1,
            fontSize = 12.sp,
            modifier = Modifier.basicMarquee(
                // 迭代次数，设置为 Int.MAX_VALUE 表示无限滚动
                iterations = 2,
                // 每次迭代之间的延迟时间（毫秒）
                // 初始状态的延迟时间（毫秒），即开始滚动前的等待时间
                initialDelayMillis = 1000,
                // 动画速度，可以调整这个值来改变滚动快慢
                velocity = 30.dp // 每秒滚动的 dp 值
            )
        )

    }
}

@Composable
fun <T : IDProvider> DropDownSelection(
    current: T?,
    dataList: List<T>,
    onPreviewClick: () -> Unit,
    preview: @Composable (T) -> Unit,
    details: @Composable (T) -> Unit
) {
    var showDropDown by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier.heightIn(max = 450.dp)
    ) {
        SplitButtonLayout(leadingButton = {
            SplitButtonDefaults.LeadingButton(
                modifier = Modifier, onClick = {
                    showDropDown = !showDropDown
                }, colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                val rotateState by animateFloatAsState(
                    if (showDropDown) {
                        180f
                    } else {
                        0f
                    }
                )
                Icon(
                    modifier = Modifier.rotate(rotateState),
                    painter = painterResource(R.drawable.keyboard_arrow_down_24px),
                    contentDescription = stringResource(R.string.expand)
                )
            }
        }, trailingButton = {
            SplitButtonDefaults.TrailingButton(
                modifier = Modifier,
                onClick = onPreviewClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                if (current != null) {
                    preview(current)
                }
            }
        })
        AnimatedVisibility(showDropDown) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(dataList, key = { it.id }) { data ->
                    details(data)
                }
            }
        }
    }
}


@Composable
fun markdownColor(): MarkdownColors {
    return DefaultMarkdownColors(
        text = MaterialTheme.colorScheme.onSurface,
        codeBackground = MaterialTheme.colorScheme.secondaryContainer,
        inlineCodeBackground = MaterialTheme.colorScheme.outlineVariant,
        dividerColor = MaterialTheme.colorScheme.outlineVariant,
        tableBackground = MaterialTheme.colorScheme.tertiaryContainer
    )
}

@Composable
fun markdownTypography(): MarkdownTypography {
    return DefaultMarkdownTypography(
        h1 = MaterialTheme.typography.headlineLargeEmphasized,
        h2 = MaterialTheme.typography.headlineLarge,
        h3 = MaterialTheme.typography.headlineMediumEmphasized,
        h4 = MaterialTheme.typography.headlineMedium,
        h5 = MaterialTheme.typography.headlineSmallEmphasized,
        h6 = MaterialTheme.typography.headlineSmall,
        text = LocalTextStyle.current,
        code = LocalTextStyle.current,
        inlineCode = LocalTextStyle.current,
        quote = LocalTextStyle.current,
        paragraph = LocalTextStyle.current,
        ordered = LocalTextStyle.current,
        bullet = LocalTextStyle.current,
        list = LocalTextStyle.current,
        textLink = TextLinkStyles(),
        table = LocalTextStyle.current
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun ConversationPagePreview() {
    AppTheme {
        // Previewing with a Drawer might be complex.
        // For simplicity, we can preview the content part directly,
        // or provide a simplified version.
        // For now, let's assume the preview doesn't need the full drawer interaction.
        // We might need a mock ViewModel if viewModel() causes issues in preview.
        // MarkdownPage() // This might fail if the viewModel() call isn't handled for preview
        Text("Preview of MarkdownPage - Drawer functionality may not be visible here.")
    }
}
