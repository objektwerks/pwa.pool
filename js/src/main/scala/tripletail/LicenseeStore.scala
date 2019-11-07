package tripletail

import java.nio.charset.StandardCharsets

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.crypto.GlobalCrypto.crypto.subtle._
import org.scalajs.dom.crypto.{BufferSource, CryptoKey, KeyUsage}
import org.scalajs.dom.raw.{IDBDatabase, IDBVersionChangeEvent}
import tripletail.Serializers._
import upickle.default._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.util.{Failure, Success}

@js.native
trait LicenseeRecord extends js.Object {
  val key: Int
  val cryptoKey: CryptoKey
  val encryptedLicensee: BufferSource
}

object LicenseeRecord {
  def apply(key: Int, cryptoKey: CryptoKey, encryptedLicensee: BufferSource): LicenseeRecord =
    js.Dynamic.literal(key = key, cryptoKey = cryptoKey, encryptedLicensee = encryptedLicensee).asInstanceOf[LicenseeRecord]
}

class LicenseeStore {
  private val dbName = "db"
  private val dbVersion = 1

  private val cryptoKeyAlgo = "RSA-OAEP"

  private val licenseeStore = "licensee"
  private val licenseeStoreKeyPath = "{ keyPath: 'key' }"
  private val licenseeKey = 1
  private var licenseeCache: Option[Licensee] = None

  private val openDBRequest = window.indexedDB.open(dbName, dbVersion)

  openDBRequest.onupgradeneeded = (event: IDBVersionChangeEvent) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    db.createObjectStore(licenseeStore, licenseeStoreKeyPath)
    console.log("openDBRequest.onupgradeneeded", event)
  }

  openDBRequest.onerror = (event: ErrorEvent) => console.error("openDBRequest.onerror", event)

  openDBRequest.onsuccess = (event: dom.Event) => console.log("openDBRequest.onsuccess", event)

  private def generateCryptoKey(): Future[Any] = {
    val extractable = false
    val keyUsages = js.Array(KeyUsage.encrypt, KeyUsage.decrypt)
    generateKey(cryptoKeyAlgo, extractable, keyUsages).toFuture
  }

  private def encryptLicensee(licensee: String, cryptoKey: CryptoKey): Future[Any] = {
    val array = new Uint8Array(js.Array(licensee.getBytes(StandardCharsets.UTF_8).toIterable))
    val buffer = array.buffer.asInstanceOf[BufferSource]
    encrypt(cryptoKeyAlgo, cryptoKey, buffer).toFuture
  }

  private def decryptLicensee(licensee: BufferSource, cryptoKey: CryptoKey): Future[Any] = {
    decrypt(cryptoKeyAlgo, cryptoKey, licensee).toFuture
  }

  private def cacheLicensee(): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readonly").objectStore(licenseeStore)
    val dbRequest = store.get(licenseeKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheLicensee.onerror", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      if (!js.isUndefined(dbRequest.result)) {
        val licenseeRecord = dbRequest.result.asInstanceOf[LicenseeRecord]
        decryptLicensee(licenseeRecord.encryptedLicensee, licenseeRecord.cryptoKey) onComplete {
          case Success(opaqueLicensee) =>
            val decryptedLicensee = opaqueLicensee.asInstanceOf[String]
            licenseeCache = Some( read[Licensee](decryptedLicensee) )
            console.log(s"cacheLicensee.onsuccess : $licenseeRecord", event)
          case Failure(error) => console.error("cacheLicensee.onerror", error.getMessage)
        }
      } else {
        licenseeCache = None
        console.log("cacheLicensee: no Licensee in db", event)
      }
    }
  }

  private def copyLicenseeCache: Option[Licensee] = {
    if (licenseeCache.nonEmpty) Some(licenseeCache.get) else None
  }

  def getLicensee: Future[Option[Licensee]] = {
    if (licenseeCache.isEmpty) cacheLicensee()
    Future.successful(copyLicenseeCache)
  }

  def putLicensee(licensee: Licensee): Future[Unit] = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readwrite").objectStore(licenseeStore)
    for {
      opaqueCryptoKey   <- generateCryptoKey()
      cryptoKey         = opaqueCryptoKey.asInstanceOf[CryptoKey]
      opaqueLicensee    <- encryptLicensee(write[Licensee](licensee), cryptoKey)
      encryptedLicensee = opaqueLicensee.asInstanceOf[BufferSource]
      licenseeRecord    = LicenseeRecord(licenseeKey, cryptoKey, encryptedLicensee)
      dbRequest         = store.put(licenseeRecord, licenseeKey)
    } yield {
      dbRequest.onerror = (event: ErrorEvent) => console.error("putLicensee.onerror", event)
      dbRequest.onsuccess = (event: dom.Event) => {
        licenseeCache = Some(licensee)
        console.log(s"putLicensee.onsuccess : $licenseeRecord", event)
      }
    }
  }
}