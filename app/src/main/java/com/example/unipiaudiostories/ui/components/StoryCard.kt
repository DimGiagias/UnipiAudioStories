package com.example.unipiaudiostories.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.unipiaudiostories.data.model.Story
import com.example.unipiaudiostories.utils.transformGoogleDriveUrl
import com.example.unipiaudiostories.R

@Composable
fun StoryCard(
    story: Story,
    onClick: () -> Unit
) {
    BoxWithConstraints {
        val scaleFactor = maxWidth / 360.dp

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height((120 * scaleFactor).dp)
                .padding((1 * scaleFactor).dp)
                .background(color = Color.White, shape = RoundedCornerShape((8 * scaleFactor).dp))
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width((13 * scaleFactor).dp))
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(transformGoogleDriveUrl(story.imageUrl))
                    .crossfade(true)
                    .build(),
                contentDescription = "Story Image",
                modifier = Modifier
                    .size((90 * scaleFactor).dp)
                    .clip(RoundedCornerShape((8 * scaleFactor).dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.image_placeholder),
                error = painterResource(R.drawable.error_image_placeholder)
            )
            Spacer(modifier = Modifier.width((13 * scaleFactor).dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    fontSize = (20 * scaleFactor).sp,
                    maxLines = 2,
                    modifier = Modifier.padding(bottom = (10 * scaleFactor).dp),
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.by) + " " + story.author,
                    fontSize = (12 * scaleFactor).sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}