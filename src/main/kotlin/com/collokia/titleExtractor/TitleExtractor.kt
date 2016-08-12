package com.collokia.titleExtractor

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Serializable
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.util.regex.Pattern


public class TitleExtractor() : Serializable{

    private val TITLE_TAG = Pattern.compile("\\<title>(.*)\\</title>", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)

    /**
     * @param url the HTML page
     * *
     * @return title text (null if document isn't HTML or lacks a title tag)
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    fun getPageTitle(url: String): String? {
        val u = URL(url)
        val conn = u.openConnection()

        // ContentType is an inner class defined below
        val contentType = getContentTypeHeader(conn)
        if (!contentType?.contentType.equals("text/html")) {
            return null // don't continue if not HTML
        } else {
            // determine the charset, or use the default
            val charset = getCharset(contentType) ?: Charset.defaultCharset()

            // read the response body, using BufferedReader for performance
            val inputStream = conn.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream, charset))
            //var n = 0
            var totalRead = 0
            val buf = CharArray(1024)
            val content = StringBuilder()

            // read until EOF or first 8192 characters
            var n = 0
            while ((totalRead < 8192) && (n != -1)) {
                n = reader.read(buf, 0, buf.size)
                content.append(buf, 0, n)
                totalRead += n

            }
            reader.close()

            // extract the title
            val matcher = TITLE_TAG.matcher(content)
            if (matcher.find()) {
                /* replace any occurrences of whitespace (which may
                 * include line feeds and other uglies) as well
                 * as HTML brackets with a space */
                return matcher.group(1).replace("[\\s\\<>]+", " ").trim()
            } else
                return null
        }
    }

    /**
     * Loops through response headers until Content-Type is found.
     * @param conn
     * *
     * @return ContentType object representing the value of
     * * the Content-Type header
     */
    private fun getContentTypeHeader(conn: URLConnection): ContentType? {
        var i = 0
        var moreHeaders = true
        do {
            val headerName = conn.getHeaderFieldKey(i)
            val headerValue = conn.getHeaderField(i)
            if (headerName != null && headerName == "Content-Type")
                return ContentType(headerValue)

            i++
            moreHeaders = headerName != null || headerValue != null
        } while (moreHeaders)

        return null
    }

    private fun getCharset(contentType: ContentType?): Charset? {

        if (contentType != null && contentType.charsetName != null && Charset.isSupported(contentType.charsetName)) {
            return Charset.forName(contentType.charsetName)
        } else {
            return null
        }
    }

    /**
     * Class holds the content type and charset (if present)
     */
    private class ContentType public constructor(headerValue: String?) {

        public var contentType: String? = null
        public var charsetName: String? = null

        init {
            if (headerValue == null)
                throw IllegalArgumentException("ContentType must be constructed with a not-null headerValue")
            val n = headerValue.indexOf(";")
            if (n != -1) {
                contentType = headerValue.substring(0, n)
                val matcher = CHARSET_HEADER.matcher(headerValue)
                if (matcher.find())
                    charsetName = matcher.group(1)
            } else
                contentType = headerValue
        }

        companion object {
            private val CHARSET_HEADER = Pattern.compile("charset=([-_a-zA-Z0-9]+)", Pattern.CASE_INSENSITIVE or Pattern.DOTALL)
        }
    }


}

fun main(args: Array<String>) {

    val titleExtractor = TitleExtractor()
    println(titleExtractor.getPageTitle("https://docs.gradle.org/current/userguide/artifact_dependencies_tutorial.html"))
}
