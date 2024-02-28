package kr.sjh.presentation.ui.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kr.sjh.presentation.R
import kr.sjh.presentation.navigation.LeafScreen

@Composable
fun BoardScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    boardViewModel: BoardViewModel = hiltViewModel()
) {

    val posts by boardViewModel.posts.collectAsState()

    LazyColumn(modifier = modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        itemsIndexed(posts) { index, item ->
            Post(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        navController.navigate(LeafScreen.BoardDetail.route)
                    }, title = item.key
            )
            if (index < posts.size - 1)
                Divider(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    thickness = 1.dp,
                    color = Color.LightGray
                )
        }
    }
}

@Composable
fun Post(modifier: Modifier = Modifier, title: String) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .width(150.dp)
                .height(150.dp)
                .clip(RoundedCornerShape(20.dp)),
            painter = painterResource(id = R.drawable.test_image),
            contentDescription = ""
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp)
        ) {
            Text(text = "레스토랑 ㅇㅇㅇ점 갔다왔어요. 음식사진 및 후기 공유드립니다.")
            Text(text = "")
            Text(text = "/")
        }
    }
}


