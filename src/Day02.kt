import kotlin.time.measureTime

fun main() {

    fun List<Int>.isSafe(): Boolean {
        val zipped = zipWithNext()
        return zipped.all { (a, b) -> (b - a) in 1..3 } || zipped.all { (a, b) -> (a - b) in 1..3 }
    }

    fun part1(input: List<String>): Int {
        return input.map { line ->
            line.split(" ").map { it.toInt() }
                .isSafe()
        }
            .count { it }
    }

    fun part2(input: List<String>): Int {
        return input.map { line ->
            val intsLine = line.split(" ").map { it.toInt() }
            intsLine.isSafe() || intsLine.indices.any {
                val y = intsLine.toMutableList()
                y.removeAt(it)
                y.isSafe()
            }
        }
            .count { it }
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
