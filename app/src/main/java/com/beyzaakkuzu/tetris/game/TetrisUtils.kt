package com.beyzaakkuzu.tetris.game

import androidx.compose.ui.gesture.Direction
import androidx.compose.ui.graphics.Color

fun calculateScore(linesDestroyed: Int) = when (linesDestroyed) {
    1 -> 100
    2 -> 300
    3 -> 700
    4 -> 1500
    else -> 0
}

val shapeVariants = listOf(
    listOf(Pair(0, -1), Pair(0, 0), Pair(-1, 0), Pair(-1, 1)) to Color(0xFF115EB4),
    listOf(Pair(0, -1), Pair(0, 0), Pair(1, 0), Pair(1, 1)) to Color(0xFF643077),
    listOf(Pair(0, -1), Pair(0, 0), Pair(0, 1), Pair(0, 2)) to Color(0xFFAA0259),
    listOf(Pair(0, 1), Pair(0, 0), Pair(0, -1), Pair(1, 0)) to Color(0xFF478053),
    listOf(Pair(0, 0), Pair(-1, 0), Pair(0, -1), Pair(-1, -1)) to Color(0xFFB8A60C),
    listOf(Pair(-1, -1), Pair(0, -1), Pair(0, 0), Pair(0, 1)) to Color(0xFF128E9E),
    listOf(Pair(1, -1), Pair(0, -1), Pair(0, 0), Pair(0, 1)) to Color(0xFFC0B6B3)
)

operator fun Pair<Int, Int>.plus(pair: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first + pair.first, second + pair.second)

operator fun Pair<Int, Int>.div(pair: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first / pair.first, second / pair.second)

operator fun Pair<Int, Int>.times(pair: Pair<Int, Int>): Pair<Int, Int> =
    Pair(first * pair.first, second * pair.second)

fun <T> MutableList<List<T>>.update(i: Int, j: Int, item: T): MutableList<List<T>> = apply {
    this[j] = this[j].toMutableList().apply {
        this[i] = item
    }
}

val <T> List<List<T>>.multiSize: Pair<Int, Int> get() = (firstOrNull()?.size ?: 0) to size

fun TetrisBlock.createProjection(blocks: Board): TetrisBlock {
    var projection = this
    while (projection.move(0 to 1).isValid(blocks)) {
        projection = projection.move(0 to 1)
    }
    return projection
}

fun Board.modifyBlocks(block: TetrisBlock): Pair<Board, Int> {
    val size = multiSize
    var newBoard = this.toMutableList()
    var destroyedRows = 0
    block.coordinates.forEach {
        newBoard = newBoard.update(it.first, it.second, block.color)
    }
    if (newBoard.removeAll { row -> row.all { it != Color.Unspecified } }) {
        destroyedRows = size.second - newBoard.size
        newBoard.addAll(0, (0 until destroyedRows).map { (0 until size.first).map { Color.Unspecified } })
    }
    return newBoard to destroyedRows
}

fun TetrisBlock.isValid(blocks: Board): Boolean {
    val size = blocks.multiSize
    return coordinates.none {
        it.first < 0 || it.first > size.first - 1 || it.second > size.second - 1 ||
                blocks[it.second][it.first] != Color.Unspecified
    }
}

fun Direction.toOffset() = when (this) {
    Direction.LEFT -> -1 to 0
    Direction.UP -> 0 to -1
    Direction.RIGHT -> 1 to 0
    Direction.DOWN -> 0 to 1
}