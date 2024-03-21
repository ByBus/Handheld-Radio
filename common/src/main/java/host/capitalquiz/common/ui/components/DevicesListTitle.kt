package host.capitalquiz.common.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import host.capitalquiz.common.ui.SemiGray
import host.capitalquiz.common.ui.adventProFamily

@Composable
fun DevicesListTitle(title: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 26.sp,
            modifier = Modifier.padding(16.dp),
            fontFamily = adventProFamily
        )
        Divider(thickness = Dp.Hairline, color = SemiGray, modifier = Modifier.padding(2.dp))
    }
}