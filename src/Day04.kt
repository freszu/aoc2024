fun main() {
    fun part1(matrix: Matrix<Char>): Int {
        val keyword = "XMAS"
        return matrix.map2dIndexed { x, y, char ->
            if (char != keyword.first()) return@map2dIndexed 0

            Direction.entries.map {
                val walkLength = matrix.walk(Position(x, y), it)
                    .takeWhileIndexed { i, (_, char) -> keyword.getOrNull(i) == char }
                    .count()
                (walkLength == keyword.length)
            }
                .count { it }
        }
            .flatten()
            .sum()
    }

    fun part2(matrix: Matrix<Char>): Int {
        val keyword = "MAS"
        val masAPositions = matrix.map2dIndexed { x, y, char ->
            if (char != keyword.first()) return@map2dIndexed emptyList()

            Direction.diagonals.mapNotNull {
                val walk = matrix.walk(Position(x, y), it)
                    .takeWhileIndexed { i, (_, char) -> char == keyword.getOrNull(i) }

                val walkLength = walk.count()
                if (walk.count() == keyword.length) {
                    val (middlePosition, _) = walk.elementAt(walkLength / 2)
                    middlePosition
                } else {
                    null
                }
            }
        }
            .flatten()
            .flatten()

        return masAPositions.size - masAPositions.toSet().size
    }

    val testInput = readInput("Day04_test")
    val testMatrix: Matrix<Char> = testInput.map { it.toCharArray().toList() }

    check(part1(testMatrix) == 18)
    check(part2(testMatrix) == 9)

    val input = readInput("Day04")
    val inputMatrix: Matrix<Char> = input.map { it.toCharArray().toList() }
    part1(inputMatrix).println()
    part2(inputMatrix).println()
}

fun <T> Sequence<T>.takeWhileIndexed(predicate: (Int, T) -> Boolean): Sequence<T> = sequence {
    for ((index, item) in this@takeWhileIndexed.withIndex()) {
        if (!predicate(index, item)) break
        yield(item)
    }
}
