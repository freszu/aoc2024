import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.runBlocking
import kotlin.time.measureTime

fun main() {
    val directions = listOf(Direction8.UP, Direction8.RIGHT, Direction8.DOWN, Direction8.LEFT)

    fun Matrix<Char>.patrol(
        startPosition: Position,
        direction: Direction8
    ): Sequence<Pair<Position, Direction8>> = sequence {
        val map = this@patrol
        var pos = startPosition
        var dir = direction
        var iterator = map.walk(pos, dir).iterator()

        while (iterator.hasNext()) {
            val (nextPos, nextChar) = iterator.next()
            when (nextChar) {
                '.', '^' -> yield(nextPos to dir)
                '#', 'O' -> {
                    dir = directions[(directions.indexOf(dir) + 1) % directions.size]
                    iterator = map.walk(pos, dir).iterator().also {
                        it.next() // skip the current position
                    }
                }

                else -> error("Unexpected char $nextChar")
            }
            pos = nextPos
        }
    }

    fun part1(input: List<String>): Int {
        val map: Matrix<Char> = input.map { it.toCharArray().toList() }
        val (startPos, _) = requireNotNull(map.findFirst { it == '^' })

        return map.patrol(startPos, Direction8.UP)
            .map { (pos, _) -> pos }
            .toSet()
            .count()
    }

    fun part2(input: List<String>): Int {
        val map: Matrix<Char> = input.map { it.toCharArray().toList() }
        val (startPos, _) = requireNotNull(map.findFirst { it == '^' })

        return map.patrol(startPos, Direction8.UP)
            .map { (pos, _) -> pos }
            .drop(1)
            .toSet()
            .map { (x, y) ->
                val mapCopy = map.toMutableMatrix()
                mapCopy[y][x] = 'O'
                val isNotLooped = mapCopy.patrol(startPos, Direction8.UP)
                    .all(mutableSetOf<Any>()::add)
                isNotLooped
            }
            .count { !it }
    }

    // just a twist with parallel processing using coroutines
    @OptIn(ExperimentalCoroutinesApi::class)
    fun part2v2(input: List<String>): Int {
        val map: Matrix<Char> = input.map { it.toCharArray().toList() }
        val (startPos, _) = requireNotNull(map.findFirst { it == '^' })


        return runBlocking(Dispatchers.Default) {
            map.patrol(startPos, Direction8.UP)
                .map { (pos, _) -> pos }
                .drop(1)
                .toSet()
                .asFlow()
                .flatMapMerge { (x, y) ->
                    suspend {
                        val mapCopy = map.toMutableMatrix()
                        mapCopy[y][x] = 'O'
                        val isNotLooped = mapCopy.patrol(startPos, Direction8.UP)
                            .all(mutableSetOf<Any>()::add)
                        isNotLooped
                    }
                        .asFlow()
                }
                .count { !it }
        }

    }

    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
    check(part2(testInput) == 6)

    val input = readInput("Day06")
    part1(input).println()
    measureTime {
        part2(input).println()
    }.alsoPrintln()
    measureTime {
        part2v2(input).println()
    }.alsoPrintln()
}
