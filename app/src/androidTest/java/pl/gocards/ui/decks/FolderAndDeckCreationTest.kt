package pl.gocards.ui.decks

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import pl.gocards.db.deck.AppDeckDbUtil
import pl.gocards.db.room.DeckDatabase
import pl.gocards.room.entity.deck.Card
import pl.gocards.room.util.TimeUtil
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.LinkedList

/**
 * Creates sample folders and decks for preparing screenshots used in promotional materials
 * such as Google Play Store listings, documentation, and marketing assets.
 *
 * @author Grzegorz Ziemski
 */
@RunWith(AndroidJUnit4::class)
class SampleFolderAndDeckCreationTest {

    @Test
    fun createSampleFolders() = runBlocking {
        val folders = listOf(
            "Spanish Vocabulary & Grammar",
            "Human Anatomy & Biology",
            "Programming & Coding",
        )
        val root = getDbRootFolder()

        folders.forEach { folderName ->
            val folderPath = Paths.get(root.toString(), folderName)
            Files.createDirectories(folderPath)
        }
    }

    @Test
    fun createSampleDecks() = runBlocking {
        val createdAt = TimeUtil.getNowEpochSec()

        // Create decks with sample cards
        createDeckWithCards(
            "English Idioms & Phrases",
            listOf(
                "Break the ice" to "To initiate conversation in a social setting",
                "Piece of cake" to "Something very easy to do",
                "Hit the nail on the head" to "To describe exactly what is causing a situation or problem"
            ),
            createdAt
        )

        createDeckWithCards(
            "Anatomy: Human Bones & Muscles",
            listOf(
                "Femur" to "The longest and strongest bone in the human body, located in the thigh",
                "Biceps" to "A muscle in the upper arm that flexes the elbow",
                "Scapula" to "The shoulder blade",
                "Humerus" to "The long bone in the upper arm extending from shoulder to elbow",
                "Quadriceps" to "A group of four muscles on the front of the thigh"
            ),
            createdAt
        )

        createDeckWithCards(
            "Python Programming Basics",
            listOf(
                "What is a list in Python?" to "A mutable, ordered collection of elements enclosed in square brackets []",
                "What is a dictionary in Python?" to "A mutable, unordered collection of key-value pairs enclosed in curly braces {}",
                "What does 'def' keyword do?" to "Defines a function in Python",
                "What is a tuple?" to "An immutable, ordered collection of elements enclosed in parentheses ()"
            ),
            createdAt
        )

        createDeckWithCards(
            "SQL & Databases",
            listOf(
                "SELECT statement" to "Retrieves data from a database table",
                "JOIN clause" to "Combines rows from two or more tables based on a related column",
                "PRIMARY KEY" to "A unique identifier for each record in a table",
                "WHERE clause" to "Filters records based on specified conditions",
                "GROUP BY clause" to "Groups rows that have the same values in specified columns",
                "FOREIGN KEY" to "A field that links to the primary key of another table"
            ),
            createdAt
        )

        createDeckWithCards(
            "Java Core Concepts",
            listOf(
                "What is encapsulation?" to "Bundling data and methods that operate on that data within a single unit (class)",
                "What is inheritance?" to "A mechanism where a new class derives properties and behaviors from an existing class",
                "What is polymorphism?" to "The ability of objects to take on multiple forms",
                "What is an interface?" to "A contract that defines methods a class must implement",
                "What is the JVM?" to "Java Virtual Machine - executes Java bytecode and provides platform independence"
            ),
            createdAt
        )

        createDeckWithCards(
            "Object-Oriented Programming",
            listOf(
                "What is a class?" to "A blueprint for creating objects that defines attributes and methods",
                "What is an object?" to "An instance of a class",
                "What is abstraction?" to "Hiding complex implementation details and showing only essential features",
                "What is a constructor?" to "A special method called when creating an object to initialize its state"
            ),
            createdAt
        )

        createDeckWithCards(
            "Algorithms & Data Structures",
            listOf(
                "Binary Search complexity" to "O(log n) - logarithmic time complexity",
                "Stack data structure" to "LIFO (Last In First Out) data structure",
                "Queue data structure" to "FIFO (First In First Out) data structure",
                "Linked List" to "A linear data structure where elements are stored in nodes with pointers to the next node",
                "Hash Table" to "A data structure that maps keys to values for efficient lookup",
                "Quick Sort complexity" to "Average: O(n log n), Worst: O(n²)",
                "Tree traversal types" to "In-order, Pre-order, Post-order, and Level-order"
            ),
            createdAt
        )

        createDeckWithCards(
            "Algebra & Geometry Formulas",
            listOf(
                "Pythagorean theorem" to "a² + b² = c²",
                "Area of a circle" to "A = πr²",
                "Quadratic formula" to "x = (-b ± √(b² - 4ac)) / 2a",
                "Volume of a sphere" to "V = (4/3)πr³",
                "Slope formula" to "m = (y₂ - y₁) / (x₂ - x₁)"
            ),
            createdAt
        )

        /*
        createDeckWithCards(
            "World Capitals",
            listOf(
                "France" to "Paris",
                "Japan" to "Tokyo",
                "Brazil" to "Brasília"
            ),
            createdAt
        )*/

        createDeckWithCards(
            "Job Interview Questions",
            listOf(
                "Tell me about yourself" to "A brief professional summary highlighting relevant experience and skills",
                "What are your strengths?" to "Focus on skills relevant to the job with specific examples",
                "Where do you see yourself in 5 years?" to "Show ambition aligned with the company's growth",
                "What is your greatest weakness?" to "Mention a real weakness and steps taken to improve it",
                "Why do you want to work here?" to "Show knowledge of the company and alignment with values",
                "Describe a challenge you overcame" to "Use STAR method: Situation, Task, Action, Result"
            ),
            createdAt
        )
    }

    private suspend fun createDeckWithCards(
        deckName: String,
        cards: List<Pair<String, String>>,
        createdAt: Long
    ) {
        val deckDb = createDatabase(deckName)
        val cardEntities = LinkedList<Card>()

        cards.forEachIndexed { index, (term, definition) ->
            val card = Card()
            card.ordinal = index + 1
            Card.setTerm(card, term)
            Card.setDefinition(card, definition)
            card.createdAt = createdAt
            card.updatedAt = createdAt
            Card.setHtmlFlags(card)
            cardEntities.add(card)
        }

        deckDb.cardKtxDao().insertAll(cardEntities)

        // Close the database
        if (deckDb.isOpen) {
            deckDb.close()
        }
    }

    private fun createDatabase(deckName: String): DeckDatabase {
        val root = getDbRootFolder()
        val deckPath = "$root/$deckName.db"
        val deckDbUtil = getAppDeckDbUtil()
        deckDbUtil.deleteDatabase(deckPath)
        return deckDbUtil.createDatabase(getApplication(), deckPath)
    }

    private fun getDbRootFolder(): Path {
        return this.getAppDeckDbUtil().getDbRootFolderPath(getApplication())
    }

    private fun getAppDeckDbUtil(): AppDeckDbUtil {
        return AppDeckDbUtil.getInstance(getApplication())
    }

    private fun getApplication(): Application {
        return ApplicationProvider.getApplicationContext()
    }
}