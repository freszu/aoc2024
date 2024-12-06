fun main() {

    data class Parse(
        val smallerRule: Map<Int, Set<Int>>,
        val biggerRule: Map<Int, Set<Int>>,
        val printOrders: List<List<Int>>
    )

    fun parseInput(input: String): Parse {
        val (rulesRaw, printOrdersRaw) = input.split("\n\n")
        val ruleRegex = Regex("""(\d*)\|(\d*)""")
        val rules = rulesRaw.lines().map {
            val (_, page, pageAfter) = ruleRegex.matchEntire(it)!!.groupValues
            page.toInt() to pageAfter.toInt()
        }

        val smallerRule = rules.groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .mapValues { it.value.toSet() }
        val biggerRule = rules.groupBy(keySelector = { it.second }, valueTransform = { it.first })
            .mapValues { it.value.toSet() }
        val printOrders = printOrdersRaw.lines().dropLast(1).map { it.split(',').map(String::toInt) }

        return Parse(smallerRule, biggerRule, printOrders)
    }

    fun isOrderOk(order: List<Int>, rules: Map<Int, Set<Int>>): Boolean {
        return order.mapIndexed { index, i ->
            val rulesAfter = rules.getOrDefault(i, emptySet())
            val after = order.subList(index + 1, order.size).toSet()
            (after - rulesAfter).isEmpty()
        }
            .all { it }
    }

    fun part1(input: String): Int {
        val parse = parseInput(input)

        return parse.printOrders
            .filter { isOrderOk(it, parse.smallerRule) }
            .sumOf { it[it.size / 2] }
    }

    fun part2(input: String): Int {
        val parse = parseInput(input)

        val wrongOrders = parse.printOrders.filterNot { isOrderOk(it, parse.smallerRule) }

        return wrongOrders.map {
            it.sortedWith { o1, o2 ->
                val isSmaller = parse.smallerRule.getOrDefault(o1, emptySet()).contains(o2)
                val isBigger = parse.biggerRule.getOrDefault(o1, emptySet()).contains(o2)
                when {
                    isSmaller -> -1
                    isBigger -> 1
                    else -> 0
                }
            }
        }
            .sumOf { it[it.size / 2] }
    }

    val testInput = readInputAsString("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    val input = readInputAsString("Day05")
    part1(input).println()
    part2(input).println()
}
