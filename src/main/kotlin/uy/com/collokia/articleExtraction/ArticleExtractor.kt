package uy.com.collokia.articleExtraction

import de.l3s.boilerpipe.BoilerpipeProcessingException
import de.l3s.boilerpipe.extractors.CanolaExtractor
import de.l3s.boilerpipe.extractors.DefaultExtractor
import de.l3s.boilerpipe.sax.BoilerpipeSAXInput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.Serializable
import java.net.MalformedURLException
import java.net.URL
import java.net.UnknownHostException


fun extractContentBoiler(url: URL, logger: Logger): String {


    val RET = try {

        val input = InputSource(url.openStream())

        val doc = BoilerpipeSAXInput(input).textDocument

        // perform the extraction/classification process on "doc"
        //de.l3s.boilerpipe.extractors.CanolaExtractor.INSTANCE.process(doc)
        DefaultExtractor.INSTANCE.process(doc)


        val content = CanolaExtractor.INSTANCE.getText(doc)

        content
    } catch (e: BoilerpipeProcessingException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    } catch (e: SAXException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    } catch (e: MalformedURLException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    } catch (e: IOException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    } catch (e: FileNotFoundException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    } catch (e: UnknownHostException) {
        logger.error("problem with url:\t$url\n ${e.stackTrace.joinToString("\n")}")
        println("problem with url:\t$url")
        "Exception"
    }
    RET

    return RET
}


class ArticleExtractor() : Serializable {

    companion object {
        val LOG = LoggerFactory.getLogger(ArticleExtractor::class.java)
    }


}

fun main(args: Array<String>) {

    val ae = ArticleExtractor()
    println(extractContentBoiler(URL("https://dzone.com/articles/harvard-develop-o-lab-to-encourage-experimentation"), ArticleExtractor.LOG))
}

