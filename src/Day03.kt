fun main() {
    fun part1(input: String): Int {
        return Regex("""mul\((\d*),(\d*)\)""").findAll(input)
            .map { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
            .sum()
    }

    fun part2(input: String): Int {
        return Regex("""do\(\)|don't\(\)|mul\((\d*),(\d*)\)""").findAll(input)
            .fold(true to 0) { (enabled, acc), match ->
                val command = match.groupValues[0]
                when {
                    command.startsWith("don't") -> false to acc
                    command.startsWith("do") -> true to acc
                    command.startsWith("mul") -> {
                        val mulResult = if (enabled) match.groupValues[1].toInt() * match.groupValues[2].toInt() else 0
                        enabled to acc + mulResult
                    }

                    else -> error("Unknown command: $command")
                }
            }
            .second
    }

    val testInput = readInputAsString("Day03_test")
    check(part1(testInput) == 161)
    val testInput2 = readInputAsString("Day03_test2")
    check(part2(testInput2) == 48)

    val input = readInputAsString("Day03")
    part1(input).println()
    part2(input).println()
}
