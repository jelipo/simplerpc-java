/**
 * @author Springmarker
 * @date 2018/10/15 21:34
 */
class Main {

}

fun main(args: Array<String>) {
    val forName = Class.forName("java.util.concurrent.ConcurrentHashMap")
    println(forName.canonicalName)
    println(forName.name)
}