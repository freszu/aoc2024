fun main() {
    fun part1(input: List<String>): Int {
        val antennaMap: Matrix<Char> = input.map { it.toCharArray().toList() }
        val antennaMapWidth = antennaMap.first().size
        val antennaMapHeight = antennaMap.size

        val antennaLocations = antennaMap.fold2d(emptyMap<Char, List<Position>>()) { acc, x, y, c ->
            if (c == '.') return@fold2d acc

            acc + (c to (acc[c] ?: emptyList()) + Position(x, y))
        }

        fun isAntinodeInBounds(p: Position) = p.x in 0 until antennaMapWidth && p.y in 0 until antennaMapHeight
        return antennaLocations.map { (c, positions) ->
            val antinodes = mutableSetOf<Position>()
            positions.indices.forEach { i ->
                val p1 = positions[i]
                (i + 1..positions.lastIndex).forEach { j ->
                    val p2 = positions[j]
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val antinodeA = Position(p2.x + dx, p2.y + dy)
                    val antinodeB = Position(p1.x - dx, p1.y - dy)
                    if (isAntinodeInBounds(antinodeA)) {
                        antinodes.add(antinodeA)
                    }
                    if (isAntinodeInBounds(antinodeB)) {
                        antinodes.add(antinodeB)
                    }
                }
            }
            antinodes
        }
            .flatten()
            .toSet()
            .count()
    }

    fun part2(input: List<String>): Int {
        val antennaMap: Matrix<Char> = input.map { it.toCharArray().toList() }
        val antennaMapWidth = antennaMap.first().size
        val antennaMapHeight = antennaMap.size

        val antennaLocations = antennaMap.fold2d(emptyMap<Char, List<Position>>()) { acc, x, y, char ->
            if (char == '.') return@fold2d acc

            acc + (char to (acc[char] ?: emptyList()) + Position(x, y))
        }

        return antennaLocations.map { (c, positions) ->
            val antinodes = mutableSetOf<Position>()
            positions.indices.forEach { i ->
                val p1 = positions[i]
                (i + 1..positions.lastIndex).forEach { j ->
                    val p2 = positions[j]
                    val dx = p2.x - p1.x
                    val dy = p2.y - p1.y
                    val p1antinodes = generateSequence(Position(p1.x, p1.y)) {
                        Position(it.x - dx, it.y - dy)
                    }
                        .takeWhile {
                            it.x in 0 until antennaMapWidth && it.y in 0 until antennaMapHeight
                        }
                    val p2antinodes = generateSequence(Position(p2.x, p2.y)) {
                        Position(it.x + dx, it.y + dy)
                    }.takeWhile {
                        it.x in 0 until antennaMapWidth && it.y in 0 until antennaMapHeight
                    }
                    antinodes.addAll(p1antinodes)
                    antinodes.addAll(p2antinodes)
                }
            }
            antinodes
        }
            .flatten()
            .toSet()
            .count()
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
