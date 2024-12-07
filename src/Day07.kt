fun main() {
    data class Formula(val result: Long, val input: List<Long>)

    fun Formula.isPossible(operationOutcomes: (Long, Long) -> Set<Long>): Boolean = when {
        input[0] > result -> false
        input.size == 2 -> operationOutcomes(input[0], input[1]).any { it == result }
        else -> operationOutcomes(input[0], input[1])
            .map { Formula(result, listOf(it) + input.drop(2)) }
            .any { it.isPossible(operationOutcomes) }
    }

    fun parseLine(line: String): Formula {
        val split = line.split(' ')
        val result = split[0].dropLast(1).toLong()
        val numbers = split.drop(1).map(String::toLong)
        return Formula(result, numbers)
    }

    fun part1(input: List<String>): Long {
        return input.map(::parseLine)
            .filter {
                it.isPossible { a, b -> setOf(a + b, a * b) }
            }
            .sumOf { it.result }

    }

    fun part2(input: List<String>): Long {
        return input.map(::parseLine)
            .filter {
                it.isPossible { a, b -> setOf(a + b, a * b, "$a$b".toLong()) }
            }
            .sumOf { it.result }
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
