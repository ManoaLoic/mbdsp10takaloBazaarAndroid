package com.tpt.takalobazaar.screens.home

import android.app.Activity
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import com.tpt.takalobazaar.R
import com.tpt.takalobazaar.components.CustomCaptureActivity
import com.tpt.takalobazaar.components.CustomInputField
import com.tpt.takalobazaar.components.DrawableButton
import com.tpt.takalobazaar.components.ObjectCard
import com.tpt.takalobazaar.models.Advertisement
import com.tpt.takalobazaar.sealed.UiState
import com.tpt.takalobazaar.ui.theme.Dimension
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    LaunchedEffect(key1 = Unit) {
        homeViewModel.loadObjects()
    }

    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val searchQuery by remember { homeViewModel.searchQuery }

    val advertisementsUiState by remember { homeViewModel.homeAdvertisementsUiState }
    val advertisements = listOf(
        Advertisement(
            title = "Échangez vos objets",
            subtitle = "Découvrez des Objets",
            image = R.drawable.banner3
        ),
        Advertisement(
            title = "Trouvez de nouveaux trésors",
            subtitle = "Commencez votre Recherche",
            image = R.drawable.banner
        ),
        Advertisement(
            title = "Transformez vos objets en nouvelles opportunités",
            subtitle = "Explorez Maintenant",
            image = R.drawable.banner2
        )
    )

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

    val context = LocalContext.current

    val qrScanLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
        if (intentResult != null && intentResult.contents != null) {
            val scannedData = intentResult.contents
            scannedData.toIntOrNull()?.let { objectId ->
                navController.navigate("ficheobjet/$objectId")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_no_background),
                            contentDescription = null,
                            modifier = Modifier
                                .size(150.dp)
                                .clip(MaterialTheme.shapes.medium)
                        )
                        IconButton(onClick = {
                            val integrator = IntentIntegrator(context as Activity)
                            integrator.setOrientationLocked(false)
                            integrator.setBeepEnabled(false)
                            integrator.captureActivity = CustomCaptureActivity::class.java
                            qrScanLauncher.launch(integrator.createScanIntent())
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_qr_scan),
                                contentDescription = "Scan QR",
                                tint = MaterialTheme.colors.onBackground
                            )
                        }
                    }
                },
                backgroundColor = MaterialTheme.colors.background,
                contentColor = MaterialTheme.colors.onBackground
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
                    },
                    placeholder = "Qu'est ce que vous recherchez?"
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
                                    onAdvertiseClicked = {},
                                    navController = navController,
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
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.66f)
                                .clip(MaterialTheme.shapes.large)
                                .background(MaterialTheme.colors.primary)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Les 20 objets les plus récents",
                                style = MaterialTheme.typography.subtitle1.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
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
                                ) {
                                    ObjectCard(obj, true, navController)
                                }
                            }

                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    item {
                        if (!isLoading && objects.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { navController.navigate("objectsearch?query=") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium),
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                            ) {
                                Text(
                                    text = "Voir plus",
                                    color = Color.White,
                                    style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.Bold)
                                )
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
    placeholder: String
) {
    CustomInputField(
        modifier = Modifier
            .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
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
    navController: NavHostController,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimension.pagePadding.div(2)),
    ) {
        /** Horizontal pager section */
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(Color(0xFFC4C4C4)),
            count = advertisements.size,
            state = pagerState,
            itemSpacing = Dimension.pagePadding.times(2),
        ) {
            val advertisement = advertisements[this.currentPage]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFFC4C4C4))
                    .padding(Dimension.pagePadding)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = { onAdvertiseClicked(advertisement) }
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = advertisement.title,
                            style = MaterialTheme.typography.h6.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                            onClick = { navController.navigate("objectsearch?query=") },
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary)
                        ) {
                            Text(
                                text = advertisement.subtitle,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                    AsyncImage(
                        model = advertisement.image,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Fit
                    )
                }
            }
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