package com.mustfaibra.roffu.screens.home

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.mustfaibra.roffu.R
import com.mustfaibra.roffu.components.CustomInputField
import com.mustfaibra.roffu.components.DrawableButton
import com.mustfaibra.roffu.components.ObjectCard
import com.mustfaibra.roffu.models.Advertisement
import com.mustfaibra.roffu.sealed.UiState
import com.mustfaibra.roffu.ui.theme.Dimension
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    cartOffset: IntOffset,
    cartProductsIds: List<Int>,
    bookmarkProductsIds: List<Int>,
    onProductClicked: (productId: Int) -> Unit,
    onCartStateChanged: (productId: Int) -> Unit,
    onBookmarkStateChanged: (productId: Int) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        homeViewModel.getHomeAdvertisements()
        homeViewModel.loadObjects()
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val searchQuery by remember { homeViewModel.searchQuery }

    val advertisementsUiState by remember { homeViewModel.homeAdvertisementsUiState }
    val advertisements = homeViewModel.advertisements

    val objects by homeViewModel.objects.collectAsState()
    val isLoading by homeViewModel.isLoading.collectAsState()

    /** Now we configure the pager to auto scroll each 2 seconds, using Handler */
    val mainHandler = Handler(Looper.getMainLooper())
    val autoPagerScrollCallback = remember {
        object : Runnable {
            override fun run() {
                /** Handle where to scroll */
                val currentPage = pagerState.currentPage
                val pagesCount = pagerState.pageCount
                Timber.d("Current pager page is $currentPage and count is $pagesCount")
                when {
                    currentPage < (pagesCount - 1) -> {
                        /** go to next page */
                        scope.launch {
                            pagerState.animateScrollToPage(currentPage.inc())
                        }
                    }
                    else -> {
                        /** Start from beginning */
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                }
                mainHandler.postDelayed(this, 2000)
            }
        }
    }

    /** Staring our handler only once when the app is launched */
    LaunchedEffect(key1 = Unit) {
        mainHandler.post(autoPagerScrollCallback)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold, color = Color.Black)) },
                backgroundColor = Color.White,
                contentColor = Color.Black
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                /** Search field section */
                SearchField(
                    value = searchQuery,
                    onValueChange = {
                        homeViewModel.updateSearchInputValue(it)
                    },
                    onFocusChange = {},
                    onImeActionClicked = {
                        navController.navigate("objectsearch?query=${searchQuery}")
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 16.dp)
                ) {
                    item {
                        when (advertisementsUiState) {
                            is UiState.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            is UiState.Success -> {
                                AdvertisementsPager(
                                    pagerState = pagerState,
                                    advertisements = advertisements,
                                    onAdvertiseClicked = {}
                                )
                            }
                            is UiState.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Error loading advertisements")
                                }
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        if (isLoading && objects.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }

                    items(objects.chunked(2)) { rowItems ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEachIndexed { index, obj ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(0.75f)
                                ) {
                                    ObjectCard(obj, false, navController)
                                }
                            }

                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onImeActionClicked: KeyboardActionScope.() -> Unit,
) {
    CustomInputField(
        modifier = Modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = "Qu'est ce que vous recherchez?",
        textStyle = MaterialTheme.typography.caption.copy(
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        ),
        padding = PaddingValues(
            horizontal = Dimension.pagePadding,
            vertical = Dimension.pagePadding.times(0.7f),
        ),
        backgroundColor = MaterialTheme.colors.surface,
        textColor = MaterialTheme.colors.onBackground,
        imeAction = ImeAction.Search,
        shape = MaterialTheme.shapes.large,
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .padding(end = Dimension.pagePadding.div(2))
                    .size(Dimension.mdIcon.times(0.7f)),
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                tint = MaterialTheme.colors.onBackground.copy(alpha = 0.4f),
            )
        },
        onFocusChange = onFocusChange,
        onKeyboardActionClicked = onImeActionClicked,
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AdvertisementsPager(
    pagerState: PagerState,
    advertisements: List<Advertisement>,
    onAdvertiseClicked: (advertisement: Advertisement) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimension.pagePadding.div(2)),
    ) {
        /** Horizontal pager section */
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth(),
            count = advertisements.size,
            state = pagerState,
            itemSpacing = Dimension.pagePadding.times(2),
        ) {
            val advertisement = advertisements[this.currentPage]
            AsyncImage(
                model = advertisement.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = { onAdvertiseClicked(advertisement) }
                    ),
                contentScale = ContentScale.Crop,
            )
        }
        /** Horizontal pager indicators */
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimension.pagePadding.times(2)),
            horizontalArrangement = Arrangement.spacedBy(Dimension.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(pagerState.pageCount) { index ->
                Box(
                    modifier = Modifier
                        .width(
                            if (pagerState.currentPage == index) Dimension.sm.times(3)
                            else Dimension.sm
                        )
                        .height(Dimension.sm)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) MaterialTheme.colors.primary
                            else MaterialTheme.colors.primary.copy(alpha = 0.4f)
                        )
                )
            }
        }
    }
}
