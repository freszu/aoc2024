fun main() {

    fun Set<Position>.continuousGroups(): List<Set<Position>> = buildList {
        val toCheck = this@continuousGroups.toMutableSet()
        while (toCheck.isNotEmpty()) {
            add(
                buildSet {
                    val queue = mutableListOf(toCheck.first())
                    toCheck.remove(queue.first())
                    while (queue.isNotEmpty()) {
                        val element = queue.removeFirst()
                        add(element)
                        element.neighbors().filter { it in toCheck }.forEach {
                            queue.add(it)
                            toCheck.remove(it)
                        }
                    }
                }
            )
        }
    }

    fun Set<Position>.perimeterSize(): Int {
        return this.sumOf { position -> position.neighbors().count { it !in this } }
    }

    fun Set<Position>.perimeter(): Set<Pair<Position, Direction4>> {
        return flatMap { it.neighborsWithDir().filterNot { (pos, _) -> pos in this } }.toSet()
    }

    fun Set<Pair<Position, Direction4>>.discounted(): Set<Pair<Position, Direction4>> =
        filterNot { (pos, dir) -> (pos + dir.next() to dir) in this }.toSet()

    fun part1(input: List<String>): Int {

        val matrix: Matrix<Char> = input.map { it.toCharArray().toList() }
        val perCharGroups: List<Set<Position>> = matrix.sequence().groupBy { (_, value) -> value }
            .values.map {
                it.map { (pos, _) -> pos }.toSet()
            }

        return perCharGroups.flatMap { it.continuousGroups() }
            .sumOf { it.perimeterSize() * it.size }
    }

    fun part2(input: List<String>): Int {
        val matrix: Matrix<Char> = input.map { it.toCharArray().toList() }
        val perCharGroups = matrix.indexes().groupBy { matrix[it] }.values.map { it.toSet() }

        return perCharGroups.flatMap { it.continuousGroups() }
            .sumOf { it.perimeter().discounted().size * it.size }
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 1930)
    check(part2(testInput) == 1206)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
