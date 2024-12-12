fun main() {

    fun replacementRule(number: String): Sequence<String> {
        return when {
            number == "0" -> sequenceOf("1")
            number.length % 2 == 0 -> {
                sequenceOf(
                    number.substring(0, number.length / 2),
                    number.substring(number.length / 2).trimStart { it == '0' }.ifEmpty { "0" }
                )
            }

            else -> sequenceOf((number.toLong() * 2024).toString())
        }
    }

    fun part1(input: String): Int {
        val stones = input.trimIndent().split(" ")
        return 0.until(25)
            .fold(stones) { acc, _ ->
                acc.flatMap { replacementRule(it) }
            }
            .size
    }

    fun part2(input: String): Long {
        val stones = input.trimIndent().split(" ").associateWith { 1L }
        return 0.until(75).fold(stones) { acc, _ ->
            buildMap<String, Long> {
                acc.forEach { (stone, n) ->
                    replacementRule(stone).forEach {
                        this[it] = this.getOrDefault(it, 0L) + n
                    }
                }
            }
        }
            .values.sum()
    }

    val testInput = readInputAsString("Day11_test")
    check(part1(testInput) == 55312)

    val input = readInputAsString("Day11")
    part1(input).println()
    part2(input).println()
}
