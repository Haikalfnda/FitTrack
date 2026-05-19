package com.aplikasis.fittrack.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aplikasis.fittrack.ui.theme.DarkText
import com.aplikasis.fittrack.ui.theme.MutedText
import com.aplikasis.fittrack.ui.theme.PrimaryBlue
import com.aplikasis.fittrack.ui.theme.ScreenBg

private val CardBg = Color.White

private enum class OptionType {
    LEVEL, GOAL, DURATION, DAYS
}

@Composable
fun PersonalizationScreen(
    onBackClick: () -> Unit = {},
    onCreateProgramClick: () -> Unit = {}
) {
    var level by remember { mutableStateOf("Pemula • Menengah • Lanjutan") }
    var goal by remember { mutableStateOf("Turun berat badan") }
    var duration by remember { mutableStateOf("20–30 menit") }
    var days by remember { mutableStateOf("3 hari") }

    var selectedOption by remember { mutableStateOf<OptionType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        PersonalizationTopBar(
            title = "FitTrack",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Personalisasi Program",
                color = DarkText,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Susun program latihan otomatis sesuai kondisi pengguna.",
                color = MutedText,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            OptionCard(
                title = "Level kemampuan",
                value = level,
                onClick = { selectedOption = OptionType.LEVEL }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCard(
                title = "Tujuan utama",
                value = goal,
                onClick = { selectedOption = OptionType.GOAL }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCard(
                title = "Durasi latihan",
                value = duration,
                onClick = { selectedOption = OptionType.DURATION }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OptionCard(
                title = "Hari latihan / minggu",
                value = days,
                onClick = { selectedOption = OptionType.DAYS }
            )

            Spacer(modifier = Modifier.height(18.dp))

            PreviewProgramCard(
                level = level,
                goal = goal,
                duration = duration,
                days = days
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onCreateProgramClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Buat program saya",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    selectedOption?.let { option ->
        ChoiceDialog(
            optionType = option,
            onDismiss = { selectedOption = null },
            onSelected = { selectedValue ->
                when (option) {
                    OptionType.LEVEL -> level = selectedValue
                    OptionType.GOAL -> goal = selectedValue
                    OptionType.DURATION -> duration = selectedValue
                    OptionType.DAYS -> days = selectedValue
                }
                selectedOption = null
            }
        )
    }
}

@Composable
private fun PersonalizationTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "‹",
                    color = PrimaryBlue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = title,
                color = DarkText,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        HorizontalDivider(
            thickness = 1.dp,
            color = Color(0xFFE6EAF0)
        )
    }
}

@Composable
private fun OptionCard(
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = MutedText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    color = DarkText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Text(
                text = "›",
                color = Color(0xFF8FA0B8),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreviewProgramCard(
    level: String,
    goal: String,
    duration: String,
    days: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Preview Program Personal",
                color = DarkText,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "AI menyusun program berdasarkan level, target, dan waktu yang tersedia.",
                color = MutedText,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProgramChip(
                    text = "$days / minggu",
                    background = Color(0xFFE5F0FF),
                    textColor = PrimaryBlue
                )

                ProgramChip(
                    text = goal,
                    background = Color(0xFFEAF8E6),
                    textColor = Color(0xFF27A844)
                )

                ProgramChip(
                    text = duration,
                    background = Color(0xFFFFF3C8),
                    textColor = Color(0xFFE6A100)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val programList = getProgramList(goal)

            programList.forEach { item ->
                Text(
                    text = item,
                    color = DarkText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Level: $level",
                color = MutedText,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun ProgramChip(
    text: String,
    background: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun ChoiceDialog(
    optionType: OptionType,
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit
) {
    val title: String
    val options: List<String>

    when (optionType) {
        OptionType.LEVEL -> {
            title = "Pilih level kemampuan"
            options = listOf(
                "Pemula",
                "Menengah",
                "Lanjutan",
                "Pemula • Menengah • Lanjutan"
            )
        }

        OptionType.GOAL -> {
            title = "Pilih tujuan utama"
            options = listOf(
                "Turun berat badan",
                "Menambah massa otot",
                "Menjaga kebugaran",
                "Meningkatkan stamina"
            )
        }

        OptionType.DURATION -> {
            title = "Pilih durasi latihan"
            options = listOf(
                "10–15 menit",
                "20–30 menit",
                "30–45 menit",
                "45–60 menit"
            )
        }

        OptionType.DAYS -> {
            title = "Pilih hari latihan per minggu"
            options = listOf(
                "2 hari",
                "3 hari",
                "4 hari",
                "5 hari",
                "6 hari"
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                color = DarkText
            )
        },
        text = {
            Column {
                options.forEach { option ->
                    Text(
                        text = option,
                        color = DarkText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelected(option)
                            }
                            .padding(vertical = 12.dp)
                    )

                    HorizontalDivider(
                        color = Color(0xFFE8EDF4)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Batal",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

private fun getProgramList(goal: String): List<String> {
    return when (goal) {
        "Menambah massa otot" -> listOf(
            "Senin • Upper body + strength",
            "Rabu • Lower body + core",
            "Jumat • Full body + recovery"
        )

        "Menjaga kebugaran" -> listOf(
            "Senin • Full body ringan",
            "Rabu • Cardio + mobilitas",
            "Jumat • Core + stretching"
        )

        "Meningkatkan stamina" -> listOf(
            "Senin • Cardio interval",
            "Rabu • Bodyweight circuit",
            "Jumat • Full body endurance"
        )

        else -> listOf(
            "Senin • Lower body + cardio ringan",
            "Rabu • Upper body + core",
            "Jumat • Full body + cooldown"
        )
    }
}

@Preview(
    name = "Personalization Screen",
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=360dp,height=800dp,dpi=440"
)
@Composable
fun PersonalizationScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = PrimaryBlue,
            background = ScreenBg
        )
    ) {
        PersonalizationScreen()
    }
}