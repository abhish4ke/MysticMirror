package com.abhiiscoding.mysticmirror.homeScreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abhiiscoding.mysticmirror.HomeScreenViewModel
import com.abhiiscoding.mysticmirror.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

//"In a tarot reading game, i chose 3rd, 9th, and 60th card out of 72. And my question was when will i get my job? give me a prediction that someone will say."

@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel) {
    val response by homeScreenViewModel.response.collectAsState()
    var questionText by remember { mutableStateOf("") }
    var textBoxVisibility by remember { mutableStateOf(true) }
    var cardsVisibility by remember { mutableStateOf(false) }
    var showResponseText by remember { mutableStateOf(false) }

    LaunchedEffect(response) {
        response.let{
            Log.d("Response in HomeScreen", response.toString())
            if (response != null) {
                Log.d("Response in HomeScreen", response.toString())
                textBoxVisibility = false
                cardsVisibility = false
                showResponseText = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(30.dp))
        Row(modifier = Modifier.weight(1f)) {
            AnimatedLetterText(fullText = if(!showResponseText)"Welcome to Mystic Mirror, Enter your question in the magic box and be ready for the fortune!" else response.toString())
        }
        if (cardsVisibility) {
            SelectCard(questionText = questionText)
            Spacer(Modifier.height(20.dp))
        }
        if (textBoxVisibility) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Ask a question") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        Image(
                            imageVector = Icons.AutoMirrored.TwoTone.Send,
                            contentDescription = "Send",
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    if (questionText.isNotEmpty() && questionText.trim()
                                            .endsWith("?")
                                    ) {
                                        textBoxVisibility = false
                                        cardsVisibility = true
                                    }
                                }
                        )
                    },
                    shape = RoundedCornerShape(30.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun SelectCard(questionText: String = "") {
    // Flag to control entrance animation.
    var showLazyRow by remember { mutableStateOf(false) }
    val tarotCards = remember { List(72) { "${it + 1}" } }
    val selectedCards = remember { mutableStateListOf<String>() }
    var buttonVisibility by remember { mutableStateOf(false) }
    val homeScreenViewModel = HomeScreenViewModel()
    // To track animated cards (for the selection effect)
    val animatedCards =
        remember { mutableStateListOf<Pair<String, Offset>>() }
    val cardPositions =
        remember { mutableStateMapOf<String, Offset>() }
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxWidth()) {
        // If the deck animation hasn’t finished, overlay it.
        if (!showLazyRow) {
            DeckAnimation(onAnimationFinished = { showLazyRow = true })
        }
        // Once the deck animation is done, show the LazyRow with all cards.
        if (showLazyRow) {
            Box(
                modifier = Modifier
                    .height(350.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 50.dp),
                        horizontalArrangement = Arrangement.spacedBy((-60).dp)
                    ) {
                        itemsIndexed(tarotCards) { index, card ->
                            // Compute scale based on the card’s distance from the center.
                            val scale by remember {
                                derivedStateOf {
                                    val layoutInfo = listState.layoutInfo
                                    var scale = 1f
                                    if (layoutInfo.visibleItemsInfo.isNotEmpty()) {
                                        val itemInfo =
                                            layoutInfo.visibleItemsInfo.find { it.index == index }
                                        if (itemInfo != null) {
                                            val viewportCenter = layoutInfo.viewportStartOffset +
                                                    (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f
                                            val itemCenter = itemInfo.offset + itemInfo.size / 2f
                                            val distanceFromCenter =
                                                abs(viewportCenter - itemCenter)
                                            val normalizedDistance =
                                                distanceFromCenter / (layoutInfo.viewportEndOffset / 2f)
                                            scale = 1f - 0.3f * normalizedDistance.coerceIn(0f, 1f)
                                        }
                                    }
                                    scale
                                }
                            }
                            val animatedScale by animateFloatAsState(
                                targetValue = scale,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                            TarotCardItem(
                                cardName = card,
                                isSelected = selectedCards.contains(card),
                                onClick = {
                                    if (!selectedCards.contains(card) && selectedCards.size < 3) {
                                        val pos = cardPositions[card]
                                            ?: Offset.Zero
                                        animatedCards.add(card to pos)
                                        selectedCards.add(card)
                                    } else if (selectedCards.contains(card)) {
                                        selectedCards.remove(card)
                                        animatedCards.removeAll { it.first == card }
                                        cardPositions.remove(card)
                                        buttonVisibility = false
                                    }
                                    if (selectedCards.size == 3) {
                                        buttonVisibility = true
                                    }
                                },
                                modifier = Modifier
                                    .graphicsLayer(
                                        scaleX = animatedScale,
                                        scaleY = animatedScale
                                    )
                                    .onGloballyPositioned { coordinates ->
                                        cardPositions[card] = coordinates.positionInRoot()
                                    }
                                    .alpha(if (selectedCards.contains(card)) 0.7f else 1f)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = buttonVisibility,
                        enter = fadeIn(animationSpec = tween(600)) + scaleIn(initialScale = 0.8f),
                        exit = fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 0.8f)
                    ) {
                        Button(onClick = {
                            val response = homeScreenViewModel.sendQuestion(selectedCards, questionText)
                            Log.d("Response in homeScreen", response.toString())

//                            val userInput =
//                                "In a tarot reading game, I chose ${selectedCards.joinToString(", ")} card out of 72. And my question was '$questionText'. give me in response only prediction that a reader will say."
//
//                            GeminiApiService.generateResponse(userInput) { response ->
//                                val responseGenerated = response ?: "Failed to get response"
//                                Log.d("Response", responseGenerated)
//                            }
                        }) {
                            Text(
                                text = "Tell my fortune",
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Cursive
                            )
                        }
                    }
                }
            }
            animatedCards.forEach { (card, startOffset) ->
                AnimatedSelectedCard(
                    cardName = card,
                    startOffset = startOffset
                )
            }
        }
    }
}

@Composable
fun AnimatedSelectedCard(
    cardName: String,
    startOffset: Offset,
    assignedImage: Int? = null,
    onAssignImage: (Int) -> Unit = {}
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val cardWidthPx = with(density) { 350.dp.toPx() }
    val cardHeightPx = with(density) { 300.dp.toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val targetX = (screenWidthPx - cardWidthPx) / 2f
    val targetY = -cardHeightPx

    //list of predefined images.
    val backgroundImages = listOf(
        R.drawable.img,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_5,
        R.drawable.img_6,
        R.drawable.img_7,
        R.drawable.img_8,
        R.drawable.img_9,
        R.drawable.img_10,
        R.drawable.img_11
    )

    val randomBackground by remember(cardName) {
        mutableStateOf(assignedImage ?: backgroundImages.random().also { onAssignImage(it) })
    }

    val offsetX = remember { Animatable(startOffset.x) }
    val offsetY = remember { Animatable(startOffset.y) }
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(cardName) {
        coroutineScope {
            launch {
                offsetX.animateTo(
                    targetX,
                    animationSpec = tween(durationMillis = 1000)
                )
            }
            launch {
                offsetY.animateTo(
                    targetY,
                    animationSpec = tween(durationMillis = 1000)
                )
            }
            launch {
                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(durationMillis = 1000)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Position the card using the animated offsets.
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) },
        contentAlignment = Alignment.Center
    ) {
        TarotCardItem(
            cardName = "",
            isSelected = true,
            onClick = {},
            modifier = Modifier.graphicsLayer(
                rotationY = rotation.value
            ),
            bgImage = randomBackground
        )
    }
}


@Composable
fun AnimatedLetterText(fullText: String) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = fullText) {
        for (char in fullText) {
            displayedText += char
            delay(50L)
        }
    }
    Text(
        text = displayedText,
        fontSize = 30.sp,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
    )
}

@Composable
fun TarotCardItem(
    cardName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    bgImage: Int = R.drawable.tarot_card_bg
) {
    Card(
        modifier = modifier
            .width(200.dp)
            .height(300.dp)
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(40.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = bgImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            // If cardName is blank (used for deck animation) you might choose to hide the text.
            if (cardName.isNotEmpty()) {
                Text(
                    text = cardName,
                    fontSize = 16.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = Color.White,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Cursive,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun DeckAnimation(onAnimationFinished: () -> Unit) {
    var shuffleStarted by remember { mutableStateOf(false) }
    var animationFinished by remember { mutableStateOf(false) }
    // Animate the overall alpha to fade out the deck.
    val alpha by animateFloatAsState(
        targetValue = if (animationFinished) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    LaunchedEffect(Unit) {
        delay(500)  // Initial pause before shuffling
        shuffleStarted = true
        delay(1000) // Duration of the shuffling animation
        animationFinished = true
        delay(300)  // Wait for the fade-out to complete
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        ShufflingCards(shuffleStarted = shuffleStarted)
    }
}

@Composable
fun ShufflingCards(shuffleStarted: Boolean) {
    // Create 20 cards
    val cardCount = 20
    val cards = List(cardCount) { it }
    // Define the radius of the circle (adjust as needed)
    val radius = 80.dp

    // Stack cards in a box so they overlap, then animate them to positions along a circle
    Box(modifier = Modifier.size(220.dp), contentAlignment = Alignment.Center) {
        cards.forEachIndexed { index, _ ->
            // Compute the angle (in radians) for this card.
            // We divide the full circle (360°) evenly among the cards.
            val angleDegrees = (index * 360f / cardCount)
            val angleRadians = angleDegrees * PI.toFloat() / 180f

            // When the shuffle starts, animate each card to its position on the circle.
            val targetOffsetX = if (shuffleStarted) radius * cos(angleRadians) else 0.dp
            val targetOffsetY = if (shuffleStarted) radius * sin(angleRadians) else 0.dp

            // Optionally, rotate each card so it faces outward.
            // Here we add 90° so the top of the card points outward.
            val targetRotation = if (shuffleStarted) angleDegrees + 90f else 0f

            val offsetX by animateDpAsState(
                targetValue = targetOffsetX,
                animationSpec = tween(durationMillis = 1000)
            )
            val offsetY by animateDpAsState(
                targetValue = targetOffsetY,
                animationSpec = tween(durationMillis = 1000)
            )
            val rotation by animateFloatAsState(
                targetValue = targetRotation,
                animationSpec = tween(durationMillis = 1000)
            )

            Box(
                modifier = Modifier
                    .offset(offsetX, offsetY)
                    .graphicsLayer(rotationZ = rotation),
                contentAlignment = Alignment.Center
            ) {
                // Use the TarotCardItem with an empty cardName (or a placeholder if desired)
                TarotCardItem(
                    cardName = "",
                    isSelected = false,
                    onClick = {},
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                )
            }
        }
    }
}

