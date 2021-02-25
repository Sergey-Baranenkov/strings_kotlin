import java.io.File
import java.lang.Exception
import kotlin.random.Random

fun validate (word: String, allowedLetters: List<String>) = word.chunked(1).sorted() == allowedLetters

fun main() {
    val path = "src/main/resources/words.txt"
    val file = File(path)
    var lineCount = 0
    file.forEachLine { lineCount++ }
    val rowNumber = Random.nextInt(0, lineCount - 1)
    val word = file.useLines { lines: Sequence<String> ->
        lines.elementAt(rowNumber)
    }

    val letters = word.chunked(1).sorted()
    println("Слово: $word")
    println("Введите новые слова через запятую")
    val userWords = readLine()?.split(',')
    if (userWords === null ) {
        throw Exception("Введена пустая строка")
    }

    val validated = userWords.all{
        v -> validate(v, letters)
    }

    if (!validated) {
        throw Exception("Все слова должны состоять исключительно из букв данного слова")
    }
    File("src/main/resources/output.txt").writeText(userWords.joinToString(separator = "\n"))
}
