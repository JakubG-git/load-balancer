package application
import  java.util.logging.Logger
fun main() {
    Logger.getLogger("org.hibernate").level = java.util.logging.Level.OFF
    App().run()
}