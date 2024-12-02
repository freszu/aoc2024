import kotlin.math.abs

fun main() {

    fun parseInput(input: String): Pair<Sequence<Int>, Sequence<Int>> {
        val regex = Regex("""(\d*)\s{3}(\d*)""")

        val find = regex.findAll(input)
        val list1 = find.map { it.groupValues[1].toInt() }
        val list2 = find.map { it.groupValues[2].toInt() }
        return list1 to list2
    }

    fun part1(input: String): Int {
        val (list1, list2) = parseInput(input).run { first.sorted() to second.sorted() }
        return list1.mapIndexed { index, i ->
            val distance = abs(list2.elementAt(index) - i)
            distance
        }
            .sum()
    }

    fun part2(input: String): Int {
        val (list1, list2) = parseInput(input)

        return list1.map { i ->
            val similarity = list2.count { it == i } * i
            similarity
        }
            .sum()
    }

    val testInput = readInputAsString("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    val input = readInputAsString("Day01")
    part1(input).println()
    part2(input).println()
}
