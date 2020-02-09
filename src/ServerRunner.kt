import ServerRunner.artList
import ServerRunner.exec
import java.net.ServerSocket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


object ServerRunner {
    val limit: Int = 4
    internal var artList = ArrayList<Artist>()
    internal var exec: ExecutorService = Executors.newFixedThreadPool(limit)
}
    fun main() {
        try {
            val server: ServerSocket = ServerSocket(PORT)
            println("Server just started")
            artList.add(Artist(
                "Mariia Solianyk",
                "https://www.artstation.com/miragemari",
                "Character Artist",
                artList.size+1))
            while (!server.isClosed) {
                val client = server.accept()
                val clientHandler = ClientHandler(client,artList)
                exec.execute(clientHandler)
            }
            exec.shutdown()
        }
        catch (e:Exception) {
            e.printStackTrace()
        }
    }

