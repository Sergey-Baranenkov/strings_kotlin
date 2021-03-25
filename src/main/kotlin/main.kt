import kotlinx.coroutines.*
import java.io.File
import java.util.concurrent.ForkJoinPool
import kotlin.random.Random
import kotlin.system.measureTimeMillis

typealias LetterCounterType = Map<Char, Int>

fun getLetterCounter (word: String): LetterCounterType {
    return word.groupingBy { it }.eachCount()
}

fun checkLetterCounterContainment (origin: LetterCounterType, toCheck: LetterCounterType): Boolean {
    return toCheck.entries.all{ origin.getOrDefault(it.key, 0) >= it.value }
}

suspend fun countScore (file: File, userWords: List<String>) {
    delay(1000)
    var scoreCount = 0
    file.useLines { lines: Sequence<String> -> lines.forEach { if (userWords.contains(it)) scoreCount += it.length }  }
    println("Число очков: $scoreCount")
}

fun main() {
    val path = "src/main/resources/words.txt"
    val file = File(path)
    var lineCount = 0

    file.forEachLine { lineCount++ }
    val rowNumber = Random.nextInt(0, lineCount - 1)
    val word = file.useLines { lines: Sequence<String> ->
        lines.elementAt(rowNumber)
    }.toLowerCase()

    val letters = getLetterCounter(word)
    println("Слово: $word")
    println("Возможные буква и их число: $letters")
    println("Введите новые слова через запятую")

    val userWords = readLine()?.
    ifBlank { null }?.
    split(',')?.
    distinct()

    if (userWords === null) {
        throw Exception("Введена пустая строка")
    }

    val isValidated = userWords.all{
        w -> checkLetterCounterContainment(letters, getLetterCounter(w))
    }

    if (!isValidated) {
        throw Exception("Все слова должны состоять исключительно из букв данного слова и не превышать их количество")
    }

    File("src/main/resources/output.txt").writeText(userWords.joinToString(separator = "\n"))

    // пример просто чтобы показать парралельность выполнения
    val time = measureTimeMillis {
        runBlocking {
            launch  {
                async { countScore(file, userWords) }
                async { countScore(file, userWords) }
            }
        }
    }
    println("Затраченное время: $time")
}
