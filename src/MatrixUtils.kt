typealias Matrix<T> = List<List<T>>
typealias MutableMatrix<T> = MutableList<MutableList<T>>

fun <T> Matrix<T>.nicePrint() = joinToString("\n")
operator fun <T> Matrix<T>.get(position: Position): T = this[position.y][position.x]
fun <T> Matrix<T>.getOrNull(x: Int, y: Int) = this.getOrNull(y)?.getOrNull(x)

data class Position(val x: Int, val y: Int)
typealias XY = Position

inline fun <T, R> Matrix<T>.map2d(transform: (T) -> R) = this.map { it.map(transform) }

inline fun <T, R> Matrix<T>.map2dIndexed(transform: (x: Int, y: Int, T) -> R) = mapIndexed { y, rows ->
    rows.mapIndexed { x, t -> transform(x, y, t) }
}

fun <T, R> Matrix<T>.fold2d(initial: R, operation: (acc: R, x: Int, y: Int, T) -> R): R {
    var accumulator = initial
    for (y in indices) {
        val row = this[y]
        for (x in row.indices) {
            accumulator = operation(accumulator, x, y, row[x])
        }
    }
    return accumulator
}

/**
 * Left, top, right, bottom
 *
 * Von Neumann neighborhood
 */
fun <T> Matrix<T>.neighbors(position: Position): Sequence<Position> {
    val (x, y) = position

    return sequenceOf(
        Position(x - 1, y), Position(x, y + 1), Position(x + 1, y), Position(x, y - 1)
    )
        .filter { getOrNull(it.x, it.y) != null }
}

inline fun <T> Matrix<T>.findFirst(predicate: (T) -> Boolean): Pair<Position, T>? {
    for (y in indices) {
        val row = this[y]
        for (x in row.indices) {
            val value = row[x]
            if (predicate(value)) {
                return Position(x, y) to value
            }
        }
    }
    return null
}

fun <T> Matrix<T>.toMutableMatrix(): MutableMatrix<T> = map { it.toMutableList() }.toMutableList()

enum class Direction {
    LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN;

    companion object {
        val diagonals = setOf(LEFT_UP, RIGHT_UP, RIGHT_DOWN, LEFT_DOWN)
    }
}

/**
 * Walk in a direction from a position - inclusive
 */
fun <T> Matrix<T>.walk(from: Position, direction: Direction): Sequence<Pair<Position, T>> = sequence {
    val (dx, dy) = when (direction) {
        Direction.LEFT -> -1 to 0
        Direction.LEFT_UP -> -1 to -1
        Direction.UP -> 0 to -1
        Direction.RIGHT_UP -> 1 to -1
        Direction.RIGHT -> 1 to 0
        Direction.RIGHT_DOWN -> 1 to 1
        Direction.DOWN -> 0 to 1
        Direction.LEFT_DOWN -> -1 to 1
    }

    var (x, y) = from
    while (true) {
        val value = getOrNull(x, y) ?: break
        yield(Position(x, y) to value)
        x += dx
        y += dy
    }
}

fun <T> Matrix<T>.sequence(): Sequence<Pair<Position, T>> {
    val matrix = this
    return sequence {
        matrix.forEachIndexed { y, ts ->
            ts.forEachIndexed { x, t ->
                yield(Position(x, y) to t)
            }
        }
    }
}

