/**
 * which quadrant in [area] position is in
 */
fun Position.quadrant(area: XY) = when {
    x < area.x / 2 && y < area.y / 2 -> 0
    x > area.x / 2 && y < area.y / 2 -> 1
    x < area.x / 2 && y > area.y / 2 -> 2
    x > area.x / 2 && y > area.y / 2 -> 3
    else -> null
}

fun main() {

    data class Robot(val position: Position, val velocity: XY) {
        fun move(areaSize: XY, times: Int = 1): Robot = this.copy(
            position = Position(
                (position.x + velocity.x * times).mod(areaSize.x),
                (position.y + velocity.y * times).mod(areaSize.y)
            )
        )
    }

    fun parse(input: List<String>): List<Robot> {
        val regex = Regex("""p=(-?\d*),(-?\d*) v=(-?\d*),(-?\d*)""")
        return input.map {
            val (x, y, dx, dy) = regex.find(it)!!.destructured
            Robot(Position(x.toInt(), y.toInt()), XY(dx.toInt(), dy.toInt()))
        }
    }

    fun List<Robot>.printRobots(area: XY) {
        val grid = List(area.y) { y ->
            List(area.x) { x ->
                this.find { it.position == Position(x, y) }?.let { '■' } ?: ' '
            }
        }
        grid.forEach { println(it.joinToString("")) }
    }

    fun part1(input: List<String>, area: XY): Int {
        val robots = parse(input)

        return robots.map { it.move(area, 100) }
            .map { it.position.quadrant(area) }
            .groupingBy { it }
            .eachCount()
            .filterKeys { it != null }
            .values
            .reduce(Int::times)
    }

    fun part2(input: List<String>): Int {
        val area = XY(101, 103)
        val initialRobotsState = parse(input)

        return generateSequence(0) { it + 1 }
            .map { times -> initialRobotsState.map { it.move(area, times) } }
            .indexOfFirst { robots -> robots.map { it.position }.toSet().size == robots.size }
            .also { index ->
                initialRobotsState.map { it.move(area, index) }.printRobots(area)
            }
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput, area = XY(11, 7)) == 12)

    val input = readInput("Day14")
    part1(input, area = XY(101, 103)).println()
    part2(input).println()
}
