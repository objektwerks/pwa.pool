package poolmate

import com.github.blemale.scaffeine.{ Cache, Scaffeine }
import com.typesafe.scalalogging.LazyLogging

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

import scala.concurrent.duration._
import scala.io.{Codec, Source}
import scala.util.{Try, Using}

object Resources:
  def cache(minSize: Int, maxSize: Int, expireAfter: FiniteDuration): Cache[String, Array[Byte]] =
    Scaffeine()
      .initialCapacity(minSize)
      .maximumSize(maxSize)
      .expireAfterWrite(expireAfter)
      .build[String, Array[Byte]]()

trait Resources(val basePath: String,
                val indexHtml: String,
                val cache: Cache[String, Array[Byte]]) extends LazyLogging:
  private val utf8 = Codec.UTF8.name
  private val contentType = "Content-Type"
  private val cssHeader = contentType -> "text/css"
  private val icoHeader = contentType -> "image/x-icon"
  private val pngHeader = contentType -> "image/png"
  private val jsHeader = contentType -> "text/javascript"
  private val jsmapHeader = contentType -> "application/json"
  private val textHeader = contentType -> "text/plain"
  protected val htmlHeader = contentType -> "text/html; charset=UTF-8"

  def toContentType(resource: String): String = resource.split('.').last

  def toPath(resource: String): String = s"$basePath$resource"

  def toHeader(resource: String): (String, String) =
    logger.debug(s"*** to header: ${toContentType(resource)}")
    toContentType(resource) match
      case "css"  => cssHeader
      case "ico"  => icoHeader
      case "png"  => pngHeader
      case "js"   => jsHeader
      case "map"  => jsmapHeader
      case "html" => htmlHeader
      case _      => textHeader
  
  def isImage(resource: String): Boolean =
    toContentType(resource) match
      case "ico" | "png"  => true
      case _              => false

  def loadImage(resource: String): Array[Byte] =
    cache.getIfPresent(resource) match
      case Some(bytes) =>
        logger.debug(s"*** resources cache get: $resource")
        bytes
      case None =>
        val path = toPath(resource)
        logger.debug(s"*** load image: $path")
        val url = getClass.getResource(path)
        val image = ImageIO.read(url)
        val baos = new ByteArrayOutputStream()
        val contentType = toContentType(resource)
        ImageIO.write(image, contentType, baos)
        val bytes = baos.toByteArray
        cache.put(resource, bytes)
        logger.debug(s"*** resources cache put: $resource")
        bytes

  def loadResource(resource: String): Array[Byte] =
    cache.getIfPresent(resource) match
      case Some(bytes) =>
        logger.debug(s"*** resources cache get: $resource")
        bytes
      case None =>
        val path = toPath(resource)
        logger.debug(s"*** load resource: $path")
        val bytes = Using( Source.fromInputStream(getClass.getResourceAsStream(path), utf8) ) {
          source => source.mkString.getBytes
        }.getOrElse(Array.empty[Byte])
        cache.put(resource, bytes)
        logger.debug(s"*** resources cache put: $resource")
        bytes