import java.util.*

fun main() {

    fun dijkstraCheapestPath(
        corruptedBlocks: Set<Position>,
        start: Position,
        end: Position,
    ): Int? {
        val toBeEvaluated = PriorityQueue<Pair<Position, Int>>(compareBy { (_, distance) -> distance })
        toBeEvaluated.add(Pair(start, 0))

        val visited = mutableSetOf<Position>()

        while (toBeEvaluated.isNotEmpty()) {
            val (position, distance) = toBeEvaluated.poll()

            if (position !in visited) {
                if (position == end) return distance

                visited.add(position)

                toBeEvaluated += position.neighbors()
                    .filter { (x, y) -> x >= 0 && y >= 0 && x <= end.x && y <= end.y }
                    .filter { it !in corruptedBlocks }
                    .map { it to distance + 1 }
            }
        }
        return null
    }

    fun part1(input: List<String>, end: Position, bytes: Int): Int {
        val start = Position(0, 0)

        val corruptedMemBlocks = input.map {
            val (x, y) = it.split(",")
            Position(x.toInt(), y.toInt())
        }
            .take(bytes)
            .toSet()

        return dijkstraCheapestPath(corruptedMemBlocks, start, end)!!
    }

    fun part2(input: List<String>, end: Position): Position {
        val parsedInput = input.map {
            val (x, y) = it.split(",")
            Position(x.toInt(), y.toInt())
        }
        val (_, lastPassingSize) = parsedInput.asSequence()
            .runningFold(emptySet<Position>()) { acc, pos -> acc + pos }
            .map { dijkstraCheapestPath(it, Position(0, 0), end) to it.size }
            .takeWhile { (distance, _) -> distance != null }
            .last()
        return parsedInput[lastPassingSize]
    }

    val testInput = readInput("Day18_test")
    check(part1(testInput, Position(6, 6), 12) == 22)
    check(part2(testInput, Position(6, 6)) == Position(6, 1))

    val input = readInput("Day18")
    part1(input, Position(70, 70), 1024).println()
    part2(input, Position(70, 70)).println()
}
