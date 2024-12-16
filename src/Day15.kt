private sealed class MapObject {

    data class Wall(val position: Position) : MapObject()

    data class Box(val position: Position) : MapObject() {
        fun lanternfishGPS() = position.y * 100 + position.x
    }

    data class FancyBox(val left: Position, val right: Position) : MapObject() {
        fun lanternfishGPS() = left.y * 100 + left.x
        fun overlaps(other: FancyBox) =
            left == other.left || right == other.right || left == other.right || right == other.left
    }

    data class Robot(val position: Position) : MapObject() {
        fun move(direction: Direction4): Robot = this.copy(position = position + direction)
    }
}

fun main() {

    data class MapState(
        val robot: MapObject.Robot,
        val boxes: Set<MapObject.Box>,
        val fancyBoxes: Set<MapObject.FancyBox>,
        val walls: Set<Position>
    ) {
        fun describe(): String = (0..walls.maxOf { it.y }).joinToString("\n") { y ->
            (0..walls.maxOf { it.x }).joinToString("") { x ->
                val point = Position(x, y)
                when {
                    walls.contains(point) -> "#"
                    point == robot.position -> "@"
                    boxes.any { it.position == point } -> "O"
                    fancyBoxes.any { it.left == point || it.right == point } -> "?"
                    else -> "."
                }
            }
        }
    }

    fun parse(input: String): Pair<MapState, List<Direction4>> {
        val (map, moves) = input.split("\n\n")
        val mapStateMap = map.lines().map { it.toCharArray().toList() }
            .map2dIndexed { x, y, c ->
                when (c) {
                    '#' -> MapObject.Wall(Position(x, y))
                    'O' -> MapObject.Box(Position(x, y))
                    '[' -> MapObject.FancyBox(Position(x, y), Position(x + 1, y))
                    '@' -> MapObject.Robot(Position(x, y))
                    else -> null
                }
            }
            .flatten()
            .filterNotNull()
            .groupBy { it::class }

        val mapState = MapState(
            robot = mapStateMap.getValue(MapObject.Robot::class).first() as MapObject.Robot,
            boxes = (mapStateMap.getOrDefault(MapObject.Box::class, emptyList()) as List<MapObject.Box>).toSet(),
            fancyBoxes = (mapStateMap.getOrDefault(MapObject.FancyBox::class, emptyList()) as List<MapObject.FancyBox>)
                .toSet(),
            walls = (mapStateMap.getValue(MapObject.Wall::class) as List<MapObject.Wall>).map { it.position }.toSet()
        )

        val instructionSet = moves.toList().mapNotNull {
            when (it) {
                '^' -> Direction4.UP
                '>' -> Direction4.RIGHT
                'v' -> Direction4.DOWN
                '<' -> Direction4.LEFT
                else -> null
            }
        }
        return mapState to instructionSet
    }

    fun part1(input: String): Int {
        val (mapState, instructionSet) = parse(input)

        fun MapState.moveBoxes(box: MapObject.Box, direction: Direction4): MapState? {
            val boxesToMove = generateSequence(box) { it.copy(position = it.position + direction) }
                .takeWhile { boxes.contains(it) }
                .toSet()

            val boxesMoved = boxesToMove.map { it.copy(position = it.position + direction) }
            return if (boxesMoved.any { walls.contains(it.position) }) {
                null
            } else {
                copy(boxes = boxes - boxesToMove + boxesMoved)
            }
        }

        val finalState = instructionSet.fold(mapState) { state, direction ->
            //state.describe().alsoPrintln()
            val nextRobot = state.robot.move(direction)
            if (state.walls.contains(nextRobot.position)) {
                state
            } else if (state.boxes.contains(MapObject.Box(nextRobot.position))) {
                state.moveBoxes(
                    box = MapObject.Box(nextRobot.position),
                    direction = direction
                )?.copy(robot = nextRobot)
                    ?: state
            } else {
                state.copy(robot = nextRobot)
            }
        }
        return finalState.boxes.sumOf { it.lanternfishGPS() }
    }

    fun doubleMapSize(input: String): String {
        return input.map {
            when (it) {
                '#' -> "##"
                'O' -> "[]"
                '.' -> ".."
                '@' -> "@."
                else -> it
            }
        }.joinToString("")
    }

    fun part2(input: String): Int {
        val (mapState, instructionSet) = parse(doubleMapSize(input))

        tailrec fun MapState.moveFancyBoxes(boxesToMove: Set<MapObject.FancyBox>, direction: Direction4): MapState? {
            val movedBoxes = boxesToMove.map { it.copy(left = it.left + direction, right = it.right + direction) }
            if (movedBoxes.any { box -> walls.contains(box.left) || walls.contains(box.right) }) return null
            val otherBoxes = fancyBoxes - boxesToMove

            val newDiscoveredBoxesToMove = otherBoxes.filter { box ->
                movedBoxes.any { movedBox -> movedBox.overlaps(box) }
            }
            if (newDiscoveredBoxesToMove.isNotEmpty()) {
                return moveFancyBoxes(
                    boxesToMove = boxesToMove + newDiscoveredBoxesToMove,
                    direction = direction
                )
            }
            return copy(
                fancyBoxes = fancyBoxes - boxesToMove + movedBoxes
            )
        }

        val finalState = instructionSet.fold(mapState) { state, direction ->
            val nextRobot = state.robot.move(direction)

            if (state.walls.contains(nextRobot.position)) return@fold state
            val fancyBox = state.fancyBoxes.find { it.left == nextRobot.position || it.right == nextRobot.position }
            if (fancyBox != null) {
                state.moveFancyBoxes(
                    boxesToMove = setOf(fancyBox),
                    direction = direction
                )?.copy(robot = nextRobot)
                    ?: state
            } else {
                state.copy(robot = nextRobot)
            }
        }
        finalState.describe().alsoPrintln()
        return finalState.fancyBoxes.sumOf { it.lanternfishGPS() }
    }

    val testInput = readInputAsString("Day15_test")
    val testInput2 = readInputAsString("Day15_test2")
    check(part1(testInput) == 2028)
    check(part2(testInput2) == 618)

    val input = readInputAsString("Day15")
    part1(input).println()
    part2(input).println()
}
