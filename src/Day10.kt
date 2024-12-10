fun main() {

    fun Matrix<Int>.walkToEnd(from: Position, value: Int): Sequence<Position> {
        return if (value == 9) {
            sequenceOf(from)
        } else {
            neighbors(from)
                .filter { pos -> this[pos] == value + 1 }
                .flatMap {
                    walkToEnd(it, value + 1)
                }
        }
    }

    fun part1(input: List<String>): Int {
        val map: Matrix<Int> = input.map { it.map(Char::digitToInt) }

        val startingPoints = map.sequence().filter { (_, int) -> int == 0 }

        return startingPoints.sumOf { (pos, value) ->
            map.walkToEnd(pos, value).toSet().size
        }
    }

    fun part2(input: List<String>): Int {
        val map: Matrix<Int> = input.map { it.map(Char::digitToInt) }

        val startingPoints = map.sequence().filter { (_, int) -> int == 0 }

        return startingPoints.sumOf { (pos, value) ->
            map.walkToEnd(pos, value).count()
        }
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 36)
    check(part2(testInput) == 81)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
