package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.PurpleText
import com.aplikasis.fittrack.ui.theme.ScreenBg

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF6677F4),
                        Color(0xFF36A9D7),
                        Color(0xFF23C7C1)
                    )
                )
            )
    ) {
        CircleDecoration(
            size = 80,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 6.dp, y = 40.dp)
        )

        CircleDecoration(
            size = 120,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 14.dp, y = 60.dp)
        )

        CircleDecoration(
            size = 160,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 56.dp, y = (-24).dp)
        )

        CircleDecoration(
            size = 110,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-32).dp, y = (-4).dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 130.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color.White.copy(alpha = 0.14f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape)
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    DumbbellLogo()
                }
            }

            Text(
                text = "FitTrack",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Catat latihan, pantau progres, capai target.",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onStartClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 39.dp, vertical = 70.dp)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = PurpleText
            )
        ) {
            Text(
                text = "Mulai Sekarang",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CircleDecoration(
    size: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .background(Color.White.copy(alpha = 0.13f), CircleShape)
    )
}

@Composable
private fun DumbbellLogo() {
    Canvas(
        modifier = Modifier.size(30.dp)
    ) {
        val blue = Color(0xFF6D79FF)
        val teal = Color(0xFF24C6C2)

        val centerY = size.height / 2f
        val centerX = size.width / 2f

        drawLine(
            color = blue,
            start = Offset(centerX - 7.dp.toPx(), centerY),
            end = Offset(centerX + 7.dp.toPx(), centerY),
            strokeWidth = 4.dp.toPx()
        )

        drawCircle(
            color = blue,
            radius = 4.dp.toPx(),
            center = Offset(centerX - 7.dp.toPx(), centerY)
        )

        drawCircle(
            color = teal,
            radius = 4.dp.toPx(),
            center = Offset(centerX + 7.dp.toPx(), centerY)
        )

        drawRoundRect(
            color = blue,
            topLeft = Offset(centerX - 14.dp.toPx(), centerY - 5.dp.toPx()),
            size = Size(6.dp.toPx(), 10.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        drawRoundRect(
            color = teal,
            topLeft = Offset(centerX + 8.dp.toPx(), centerY - 5.dp.toPx()),
            size = Size(6.dp.toPx(), 10.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = 15.dp.toPx(),
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

@Preview(
    name = "Welcome Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=360dp,height=800dp,dpi=440"
)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PrimaryBlue,
            background = ScreenBg
        )
    ) {
        WelcomeScreen(
            onStartClick = {}
        )
    }
}