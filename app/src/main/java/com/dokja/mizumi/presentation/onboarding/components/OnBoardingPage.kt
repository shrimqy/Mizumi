package com.dokja.mizumi.presentation.onboarding.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.Dimens
import com.dokja.mizumi.presentation.Dimens.MediumPadding2
import com.dokja.mizumi.presentation.onboarding.Page
import com.dokja.mizumi.presentation.utils.padding

@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    page: Page
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp) // Adjust the minimum height as needed
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = MediumPadding2),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
        ) {
            if (page.image != null) {
                // Display image if available
                Image(
                    painter = painterResource(id = page.image),
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            Spacer(modifier = Modifier.height(MediumPadding2))

            // Display title
            page.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.display_small)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.MediumPadding1))

            // Display description or content
            if (page.description != null) {
                // Display description
                Text(
                    text = page.description,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.text_medium)
                )
            } else {
                Box(
                    modifier = Modifier
                        .padding(vertical = MaterialTheme.padding.small)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    page.content()
                }
            }
        }
    }
}
