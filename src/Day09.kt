fun main() {

    data class FileNode(val id: Int, val size: Int, val spaceAfter: Int)

    fun List<FileNode>.checksum(): Long {
        var checkSum = 0L
        var index = 0
        forEach { el ->
            repeat(el.size) {
                checkSum += index * el.id
                index++
            }
            index += el.spaceAfter
        }
        return checkSum
    }

    fun part1(input: String): Long {
        val files = input.toList().chunked(2)
            .mapIndexed { index, (size, spaceAfter) ->
                FileNode(index, size.digitToInt(), spaceAfter.digitToIntOrNull() ?: 0)
            }
        val defragmentedFiles = files.toMutableList().also {
            var index = 0
            while (index <= it.lastIndex - 1) {
                val fileNode = it[index]
                val lastFileNode = it.removeLast()
                if (fileNode.spaceAfter == 0) {
                    it.add(lastFileNode)
                } else if (fileNode.spaceAfter >= lastFileNode.size) {
                    it[index] = fileNode.copy(spaceAfter = 0)
                    it.add(index + 1, FileNode(lastFileNode.id, lastFileNode.size, fileNode.spaceAfter - lastFileNode.size))
                } else {
                    it[index] = fileNode.copy(spaceAfter = 0)
                    it.add(index + 1, FileNode(lastFileNode.id, fileNode.spaceAfter, 0))
                    it.add(
                        lastFileNode.copy(
                            size = lastFileNode.size - fileNode.spaceAfter,
                            spaceAfter = lastFileNode.spaceAfter + fileNode.spaceAfter
                        )
                    )
                }
                index += 1
            }
        }
            .toList()

        return defragmentedFiles.checksum()
    }

    fun part2(input: String): Long {
        val files = input.toList().chunked(2)
            .mapIndexed { index, (size, spaceAfter) ->
                FileNode(index, size.digitToInt(), spaceAfter.digitToIntOrNull() ?: 0)
            }
        val defragmentedFiles = files.toMutableList()
            .also { it ->
                var index = it.lastIndex
                while (index >= 0) {
                    val lastFile = it[index]
                    val freeSpaceIndex = it.indexOfFirst { it.spaceAfter >= lastFile.size }
                    if (freeSpaceIndex in 0 until index) {
                        it.removeAt(index)
                        val oldFile = it.removeAt(freeSpaceIndex)
                        it.addAll(
                            freeSpaceIndex, listOf(
                                oldFile.copy(spaceAfter = 0),
                                lastFile.copy(spaceAfter = oldFile.spaceAfter - lastFile.size),
                            )
                        )
                        val newLast = it[index]
                        it[index] = it[index].copy(
                            spaceAfter = newLast.spaceAfter + lastFile.size + lastFile.spaceAfter
                        )
                    } else {
                        index -= 1
                    }
                }
            }
            .toList()

        return defragmentedFiles.checksum()
    }

    val testInput = readInputAsString("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    val input = readInputAsString("Day09")
    part1(input).println()
    part2(input).println()
}
