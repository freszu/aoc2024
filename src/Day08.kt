fun main() {

    fun parse(input: List<String>): Matrix<Char> {
        return input.map { it.toCharArray().toList() }
    }

    fun findAntennaLocations(antennaMap: Matrix<Char>): Map<Char, List<Position>> {
        return antennaMap.fold2d(emptyMap()) { acc, x, y, c ->
            if (c == '.') return@fold2d acc

            acc + (c to (acc[c] ?: emptyList()) + Position(x, y))
        }
    }

    fun Map<Char, List<Position>>.findAntinodes(
        antennaMapWidth: Int,
        antennaMapHeight: Int,
        drop: Int = 0,
        take: Int = Int.MAX_VALUE
    ): Set<Position> {
        return this.toList().fold(emptySet()) { acc, (_, positions) ->
            val antinodes = mutableSetOf<Position>()
            positions.indices.forEach { i ->
                val p1 = positions[i]
                (i + 1..positions.lastIndex).forEach { j ->
                    val p2 = positions[j]
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y

                    fun Sequence<Position>.limitSequence() = this.drop(drop).take(take).takeWhile {
                        it.x in 0 until antennaMapWidth && it.y in 0 until antennaMapHeight
                    }

                    val p1antinodes = generateSequence(p1) { Position(it.x - dx, it.y - dy) }
                        .limitSequence()
                    val p2antinodes = generateSequence(p2) { Position(it.x + dx, it.y + dy) }
                        .limitSequence()
                    antinodes.addAll(p1antinodes + p2antinodes)
                }
            }
            acc + antinodes
        }
    }

    fun part1(input: List<String>): Int {
        val antennaMap: Matrix<Char> = parse(input)
        val antennaMapWidth = antennaMap.first().size
        val antennaMapHeight = antennaMap.size
        val antennaLocations = findAntennaLocations(antennaMap)

        return antennaLocations.findAntinodes(antennaMapWidth, antennaMapHeight, drop = 1, take = 1).count()
    }

    fun part2(input: List<String>): Int {
        val antennaMap: Matrix<Char> = input.map { it.toCharArray().toList() }
        val antennaMapWidth = antennaMap.first().size
        val antennaMapHeight = antennaMap.size

        val antennaLocations = findAntennaLocations(antennaMap)

        return antennaLocations.findAntinodes(antennaMapWidth, antennaMapHeight).count()
    }

    val testInput = readInput("Day08_test")
    val testInput2 = readInput("Day08_test2")
    check(part1(testInput) == 14)
    check(part2(testInput2) == 9)
    check(part2(testInput) == 34)

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}
