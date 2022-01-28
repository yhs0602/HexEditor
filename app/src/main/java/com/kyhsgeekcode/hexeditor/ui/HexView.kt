package com.kyhsgeekcode.hexeditor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed


@ExperimentalFoundationApi
@Composable
fun HexEditor(
    rowLength: Int,
    byteRows: suspend (Int) -> ByteArray,
    onKeyEvent: (KeyEvent) -> Boolean,
) {
    val pager = remember {
        Pager(
            PagingConfig(
                pageSize = 100,
                enablePlaceholders = true,
                maxSize = 200
            ),
        ) {
            MyPagingSource(byteRows)
        }
    }
    val lazyPagingItems = pager.flow.collectAsLazyPagingItems()
    LazyColumn(
        Modifier
            .horizontalScroll(rememberScrollState())
            .onKeyEvent(onKeyEvent)
    ) {
        stickyHeader {
            HexViewHeader(rowLength = rowLength)
        }
        itemsIndexed(lazyPagingItems) { _, item ->
            if (item != null) {
                HexViewRow(item.toList())
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun HexView(bytes: ByteArray, rowLength: Int = 8) {
    val splitted = derivedStateOf { (bytes.toList().chunked(rowLength)) }

    LazyColumn(Modifier.horizontalScroll(rememberScrollState())) {
        stickyHeader {
            HexViewHeader(rowLength = rowLength)
        }
        items(splitted.value) { item ->
            HexViewRow(item, rowLength = rowLength)
        }
    }
}

@Composable
fun HexViewHeader(rowLength: Int = 8) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        for (v in 0 until rowLength) {
            Text(
                text = String.format("%02X", v),
                modifier = Modifier
                    .width(25.dp)
                    .fillMaxHeight()
                    .background(Color.White),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Blue
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(10.dp)
        )
        for (v in 0 until rowLength) {
            Text(
                text = String.format("%02X", v),
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxHeight()
                    .background(Color.White),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Green
            )
        }
    }

}

@Composable
private fun HexViewRow(item: List<Byte>, rowLength: Int = 8) {
    Row(
        Modifier.height(IntrinsicSize.Min)
    ) {
        for (v in item) {
            Text(
                text = String.format("%02X", v),
                modifier = Modifier
                    .width(25.dp)
                    .fillMaxHeight()
                    .background(Color.White),
                textAlign = TextAlign.Center
            )
        }
        for (i in 0 until rowLength - item.size) {
            Text(
                text = "",
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .width(25.dp)
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(10.dp)
        )
        for (v in item) {
            val c = v.toInt().toChar()
            Text(
                text = if (isPrintableChar(c)) c.toString() else ".",
                modifier = Modifier
                    .width(20.dp)
                    .fillMaxHeight()
                    .background(Color.White),
                textAlign = TextAlign.Center
            )
        }
        for (i in 0 until rowLength - item.size) {
            Text(
                text = "",
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxHeight()
                    .width(20.dp)
            )
        }
    }
}

fun isPrintableChar(c: Char): Boolean {
    val block = Character.UnicodeBlock.of(c)
    return !Character.isISOControl(c) && block != null && block !== Character.UnicodeBlock.SPECIALS
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun HexPreview() {
    HexView(
        bytes = byteArrayOf(
            0xFF.toByte(),
            0x12.toByte(),
            0x13.toByte(),
            0x11.toByte(),
            0x40.toByte(),
            0x33.toByte(),
            0x65.toByte(),
            0x55.toByte(),
            0x70.toByte(),
            0x59.toByte()
        )
    )
}