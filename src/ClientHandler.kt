import ServerRunner.artList
import com.fooxer.httpserver.RomeToArabic
import java.io.*
import java.lang.Exception
import java.net.Socket
import java.nio.file.Files

const val PORT = 8080
const val HTTP_OK = "200 OK"
const val HTTP_ERR = "500 Internal Server Error"

class ClientHandler(s: Socket, artists: ArrayList<Artist>) : Thread() {

    private var clientSocket: Socket = s
    private var martList = artists
    private var inStream: InputStream
    private var outStream: OutputStream
    private var br: BufferedReader
    private var bw: BufferedWriter

    init {
        inStream = clientSocket.getInputStream()
        outStream = clientSocket.getOutputStream()
        br = BufferedReader(InputStreamReader(inStream))
        bw = BufferedWriter(OutputStreamWriter(outStream))
    }

    override fun run() {
        //println("Client connected: ${clientSocket.inetAddress.hostAddress}")
        println("Client connected: ${this.id}")
        try {

                readInput()

        }
        catch(e:Exception) {
            e.printStackTrace()
        }
        System.err.println("Client ${this.id} disconnected")
    }
    private fun readInput() { // читаем входной поток
        var input = br.readLine()
        println(input)
        if (input.startsWith("GET")) {
            val pagePath = input.split(" ")[1]
            println(pagePath)
            handleGET(pagePath)
        } else if (input.startsWith("POST")) {
            val postpath = input.split(" ")[1]
            var data = readPost()
            println(postpath)
            handlePOST(postpath,data)

        }

    }

   private fun readPost() : ArrayList<String> { // обрабатываем post-запросы - вытаскиваем из них переданные данные
       var data: ArrayList<String> = ArrayList()
       var input = br.readLine()
        while(!input.endsWith("--")) {
            println(input)
            input = br.readLine()
            if (input == null || input.endsWith("--"))
                break
            if (input.startsWith("Content-Disposition")) {
                do {
                    data.add(input)
                    input = br.readLine()
                } while (!input.endsWith("--"))

                println("DATA: $data")
                return data
            }
        }
        return data
    }
     private fun writeResponse (body: ByteArray, code: String, type: String) { // пишем ответ

        val response = "HTTP/1.1 $code\r\n" +
                "Server: Server/2019-01-01\r\n" +
                "Content-Type: $type\r\n" +
                "Content-Length: ${body.size}\r\n" +
                "Connection: close\r\n\r\n"
        bw.write(response)
        bw.flush()
        outStream.write(body)
        outStream.flush()
        println (response)


    }


    @Synchronized fun handleGET(getpath: String) { // обрабатываем get-запросы - в зависимости от требуемого адреса отдаем определенную страницу
        lateinit var path: String
        lateinit var type: String
        when (getpath) {
            "/" -> { path = "resources/index.html"; type = "text/html"}
            "/rome_form.html" -> { path = "resources/rome_form.html"; type = "text/html"}
            "/table_form.html" -> { path = "resources/table_form.html"; type = "text/html"}
            "/style.css" -> { path = "resources/style.css"; type = "text/css"}
            "/romeToArabicScript.js" -> { path = "resources/romeToArabicScript.js"; type = "text/javascript"}
            "/tableScript.js" -> { path = "resources/tableScript.js"; type = "text/javascript"}
            "/modal" -> {path =""; type="text/html"}
            else -> { path = "resources/index.html"; type = "text/html"}
        }
        println("Path: $path, type: $type")
        writeResponse(makePageFromFile(path),HTTP_OK, type)
    }

    private fun handlePOST (postpath: String, data: ArrayList<String>) { // обрабатываем post-запросы - в зависимости от адреса вносим разные изменения

        lateinit var type: String

        when(postpath) {
            "/rome_form/calculate" -> {
                type = "plain"
                val rome = data[2] // в пост-запросе строка с данными - третья в поле Контент-диспозишн
                val arabic = try {
                    val converter = RomeToArabic(rome)
                    converter.getResult()
                } catch (e: Exception) {
                    "\"INPUT ERROR NOT A ROMAN SYMBOL\""
                }
                val byteArabic = arabic.toString().toByteArray()
                writeResponse(byteArabic,HTTP_OK,type)
            }
            "/table_form/getTable" -> {
                type = "plain"
                var listResponse: String = String()
                for (i in 0 until artList.size) {
                    listResponse +=("${artList[i].fullName};${artList[i].artstationLink};${artList[i].workGenre}\n")
                }
                println(listResponse)
                writeResponse(listResponse.toByteArray(),HTTP_OK,type)
            }
            "/table_form/addNote" -> {
                type = "plain"
                try {
                    var sNote = data[2].split(";")
                    artList.add(Artist(sNote[0], sNote[1], sNote[2], artList.size))
                    writeResponse("add ok".toByteArray(), HTTP_OK, type)
                } catch (e:Exception) {
                    e.printStackTrace()
                    writeResponse("server error".toByteArray(), HTTP_ERR, type)
                }
                //println(data)
            }
            "/table_form/changeNote" -> {
                type = "plain"
                var sNote = data[2].split(";")
                try{
                val id = sNote[0].toInt() - 1
                val _name = sNote[1]
                val _link = sNote[2]
                val _genre = sNote[3]
                artList[id].fullName = _name
                artList[id].artstationLink = _link
                artList[id].workGenre = _genre
                writeResponse("change ok".toByteArray(), HTTP_OK, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                    writeResponse("server error".toByteArray(),HTTP_ERR,type)
                }

            }
            "/table_form/deleteNote" -> {
                type = "plain"
                var sNote = data[2].split(";")
                val id = sNote[0].toInt()-1
                try {
                    artList.removeAt(id)
                    writeResponse("delete ok".toByteArray(), HTTP_OK,type)
                } catch (e:Exception) {
                    e.printStackTrace()
                    writeResponse("server error".toByteArray(),HTTP_ERR,type)

                }
            }
        }
    }
    @Synchronized private fun makePageFromFile(path: String) : ByteArray { // открываем файл и считываем из него сразу поток байтов
        val file: File = File(path)
        return try {
            println("File is open")
           /* val lines = Files.readAllLines(file.toPath())
            for (line in lines)
                println(line)*/
            Files.readAllBytes(file.toPath())
        } catch(e : Exception) {
            e.printStackTrace()
            "".toByteArray()
        }
    }
}
