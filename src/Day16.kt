import java.util.*

private enum class MazeObject {
    Wall, Empty, Start, End;
}

fun main() {
    fun maze(input: List<String>): Matrix<MazeObject> {
        return input.map {
            it.toCharArray().map { c ->
                when (c) {
                    '#' -> MazeObject.Wall
                    '.' -> MazeObject.Empty
                    'S' -> MazeObject.Start
                    'E' -> MazeObject.End
                    else -> throw IllegalArgumentException("Unknown maze object: $c")
                }
            }
        }
    }

    fun Matrix<MazeObject>.dijkstraCheapestPath(
        start: Position,
        startDirection4: Direction4
    ): Int? {
        val toBeEvaluated = PriorityQueue<Triple<Position, Direction4, Int>>(compareBy { (_, _, distance) -> distance })
        toBeEvaluated.add(Triple(start, startDirection4, 0))

        val visited = mutableSetOf<Position>()

        while (toBeEvaluated.isNotEmpty()) {
            val (position, direction, distance) = toBeEvaluated.poll()

            if (position !in visited) {
                if (this[position] == MazeObject.End) return distance

                visited.add(position)

                toBeEvaluated += sequenceOf(
                    Triple(position + direction, direction, distance + 1),
                    Triple(position + direction.next(), direction.next(), distance + 1001),
                    Triple(position + direction.previous(), direction.previous(), distance + 1001),
                )
                    .filter { (position, _) -> get(position) != MazeObject.Wall }
                    .filter { (position, _) -> position !in visited }

            }
        }
        return null
    }

    fun part1(input: List<String>): Int {
        val maze: Matrix<MazeObject> = maze(input)
        val startPos = maze.findFirst { it == MazeObject.Start }!!.first

        return maze.dijkstraCheapestPath(
            start = startPos,
            startDirection4 = Direction4.RIGHT
        )
            ?: -1
    }

    data class Path(val position: Position, val direction: Direction4, val distance: Int, val path: List<Position>)

    fun part2(input: List<String>): Int {
        val maze: Matrix<MazeObject> = maze(input)
        val startPos = maze.findFirst { it == MazeObject.Start }!!.first

        val minScore = maze.dijkstraCheapestPath(startPos, Direction4.RIGHT) ?: return -1

        val toBeEvaluated = PriorityQueue<Path>(compareBy { it.distance })
        toBeEvaluated.add(Path(startPos, Direction4.RIGHT, 0, listOf(startPos)))
        val checkpointScores = mutableMapOf<Pair<Position, Direction4>, Int>()
        val bestPathsPoints = mutableSetOf<Position>()
        while (toBeEvaluated.isNotEmpty()) {
            val state = toBeEvaluated.poll()

            val checkpointDistance = checkpointScores.getOrPut(state.position to state.direction) {
                state.distance
            }
            if (checkpointDistance < state.distance) continue

            if (state.distance > minScore) continue
            if (maze[state.position] == MazeObject.End) {
                bestPathsPoints += state.path
                continue
            }
            toBeEvaluated += sequenceOf(
                Path(
                    position = state.position + state.direction,
                    direction = state.direction,
                    distance = state.distance + 1,
                    path = state.path + (state.position + state.direction),
                ),
                Path(
                    position = state.position + state.direction.next(),
                    direction = state.direction.next(),
                    distance = state.distance + 1001,
                    path = state.path + (state.position + state.direction.next()),
                ),
                Path(
                    position = state.position + state.direction.previous(),
                    direction = state.direction.previous(),
                    distance = state.distance + 1001,
                    path = state.path + (state.position + state.direction.previous()),
                ),
            )
                .filter { (position, _) -> maze[position] != MazeObject.Wall }
        }

        return bestPathsPoints.size
    }

    val testInput = readInput("Day16_test")
    check(part1(testInput) == 7036)
    check(part2(testInput) == 45)

    val input = readInput("Day16")
    part1(input).println()
    part2(input).println()
}
