package com.dokja.mizumi.presentation.onboarding.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokja.mizumi.R
import com.dokja.mizumi.presentation.Dimens
import com.dokja.mizumi.presentation.Dimens.MediumPadding2
import com.dokja.mizumi.presentation.onboarding.Page
import com.dokja.mizumi.presentation.onboarding.pages
import com.dokja.mizumi.presentation.theme.MizumiTheme

@Composable
fun OnBoardingPage(
    modifier: Modifier = Modifier,
    page: Page
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(50.dp))

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
        } else {
            // Display spacer if image is not available
            Spacer(modifier = Modifier.height(198.dp))
        }

        Spacer(modifier = Modifier.height(MediumPadding2))

        // Display title
        Text(
            modifier = Modifier.padding(horizontal = MediumPadding2),
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.display_small)
        )

        Spacer(modifier = Modifier.height(Dimens.MediumPadding1))

        // Display description
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.MediumPadding1),
            textAlign = TextAlign.Center,
            color = colorResource(id = R.color.text_medium)
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun OnBoardingPagePreview() {
    MizumiTheme {
        OnBoardingPage(page = pages[2]
        )
    }
}
