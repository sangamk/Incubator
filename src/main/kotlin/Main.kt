import com.natpryce.konfig.ConfigurationProperties
import java.io.File
import config.incubator
import org.apache.commons.brut.io.FileUtils
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter


fun main(args: Array<String>) {
    println("Starting incubator")
    val config = ConfigurationProperties.fromFile(File("incubator.properties"))
    val basePathResult = config[incubator.output]
    val mode = config[incubator.mode]
    val ignoreAppsFile = config[incubator.ignoreApps]

    val ignoreApps = File(ignoreAppsFile).readLines().toSet()

    File(config[incubator.apk]).walk().forEach {
        if (it.extension == "apk" && !ignoreApps.contains(it.toString())) {
            println(it)

            if (!File(basePathResult).exists()) {
                File(basePathResult).mkdir()
            }

            try {
                Argus().run(it.toString(), basePathResult, mode.toString())
                if (File("./output").isDirectory) {
                    FileUtils.deleteDirectory(File("./output"))
                }

                val writer = PrintWriter(FileOutputStream(File("succes.csv"), true))
                writer.append(it.toString() + "\r\n")
                writer.close()
            } catch (e : IOException){
            } catch (e: Exception) {
                println(e.printStackTrace())
                println("Failed skipping.. $it")
                val errorFile = File("errors.txt")
                val writer = PrintWriter(FileOutputStream(errorFile, true ))
                writer.append("Failed skipping.. $it \r\n")
                e.printStackTrace(writer)
                writer.close()
            }

        }
    }
}