typealias Matrix<T> = List<List<T>>
typealias MutableMatrix<T> = MutableList<MutableList<T>>

fun <T> Matrix<T>.nicePrint() = joinToString("\n")
operator fun <T> Matrix<T>.get(position: Position): T = this[position.y][position.x]
operator fun <T> MutableMatrix<T>.set(position: Position, value: T) {
    this[position.y][position.x] = value
}

fun <T> Matrix<T>.getOrNull(x: Int, y: Int) = this.getOrNull(y)?.getOrNull(x)

data class Position(val x: Int, val y: Int) {
    operator fun plus(dir: Direction4) = when (dir) {
        Direction4.LEFT -> Position(x - 1, y)
        Direction4.UP -> Position(x, y - 1)
        Direction4.RIGHT -> Position(x + 1, y)
        Direction4.DOWN -> Position(x, y + 1)
    }

    operator fun plus(position: Position) = Position(x + position.x, y + position.y)
}

typealias XY = Position

inline fun <T, R> Matrix<T>.map2d(transform: (T) -> R) = this.map { it.map(transform) }

inline fun <T, R> Matrix<T>.map2dIndexed(transform: (x: Int, y: Int, T) -> R) = mapIndexed { y, rows ->
    rows.mapIndexed { x, t -> transform(x, y, t) }
}

inline fun <T> Matrix<T>.forEachIndexed2d(action: (x: Int, y: Int, T) -> Unit) {
    forEachIndexed { y, rows ->
        rows.forEachIndexed { x, t -> action(x, y, t) }
    }
}

operator fun <T> Matrix<T>.contains(position: Position): Boolean {
    val (x, y) = position
    return y in indices && x in this[y].indices
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
    return position.neighbors().filter { getOrNull(it.x, it.y) != null }
}

/**
 * Left, top, right, bottom
 *
 * Von Neumann neighborhood - use [Matrix.neighbors] instead if you want to account for out of bounds
 */
fun Position.neighbors(): Sequence<Position> {
    val (x, y) = this

    return sequenceOf(
        Position(x - 1, y), Position(x, y - 1), Position(x + 1, y), Position(x, y + 1)
    )
}

fun Position.neighborsWithDir(): Sequence<Pair<Position, Direction4>> {
    val (x, y) = this

    return sequenceOf(
        Position(x - 1, y) to Direction4.LEFT,
        Position(x, y - 1) to Direction4.UP,
        Position(x + 1, y) to Direction4.RIGHT,
        Position(x, y + 1) to Direction4.DOWN
    )
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

fun <T> Matrix<T>.indexes(): Sequence<Position> = sequence {
    for (y in indices) {
        val row = this@indexes[y]
        for (x in row.indices) {
            yield(Position(x, y))
        }
    }
}

fun <T> Matrix<T>.toMutableMatrix(): MutableMatrix<T> = map { it.toMutableList() }.toMutableList()

enum class Direction8 {
    LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, DOWN, LEFT_DOWN;

    /**
     * Rotate clockwise
     */
    fun next() = entries[(ordinal + 1) % entries.size]

    companion object {
        val diagonals = setOf(LEFT_UP, RIGHT_UP, RIGHT_DOWN, LEFT_DOWN)
    }
}

enum class Direction4 {
    LEFT, UP, RIGHT, DOWN;

    /**
     * Rotate clockwise
     */
    fun next() = entries[(ordinal + 1).mod(entries.size)]

    fun previous() = entries[(ordinal - 1).mod(entries.size)]

    companion object {
        fun toDirection8(direction4: Direction4): Direction8 = when (direction4) {
            LEFT -> Direction8.LEFT
            UP -> Direction8.UP
            RIGHT -> Direction8.RIGHT
            DOWN -> Direction8.DOWN
        }
    }
}

/**
 * Walk in a direction from a position - inclusive
 */
fun <T> Matrix<T>.walk(from: Position, direction: Direction8): Sequence<Pair<Position, T>> = sequence {
    val (dx, dy) = when (direction) {
        Direction8.LEFT -> -1 to 0
        Direction8.LEFT_UP -> -1 to -1
        Direction8.UP -> 0 to -1
        Direction8.RIGHT_UP -> 1 to -1
        Direction8.RIGHT -> 1 to 0
        Direction8.RIGHT_DOWN -> 1 to 1
        Direction8.DOWN -> 0 to 1
        Direction8.LEFT_DOWN -> -1 to 1
    }

    var (x, y) = from
    while (true) {
        val value = getOrNull(x, y) ?: break
        yield(Position(x, y) to value)
        x += dx
        y += dy
    }
}

/**
 * Walk in a direction from a position - inclusive
 */
fun <T> Matrix<T>.walk(from: Position, direction: Direction4): Sequence<Pair<Position, T>> {
    return walk(from, Direction4.toDirection8(direction))
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

