package tripletail

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.crypto.GlobalCrypto.crypto.subtle._
import org.scalajs.dom.crypto.{BufferSource, CryptoKey, KeyUsage}
import org.scalajs.dom.raw.{IDBDatabase, IDBVersionChangeEvent}
import tripletail.Serializers._
import upickle.default._

import scala.scalajs.js

trait LicenseeRecord extends js.Object {
  val key: Int = js.native
  val cryptoKey: CryptoKey = js.native
  val encryptedLicensee: BufferSource = js.native
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

  private def generateCryptoKey(): CryptoKey = {
    val extractable = false
    val keyUsages = js.Array(KeyUsage.encrypt, KeyUsage.decrypt)
    generateKey(cryptoKeyAlgo, extractable, keyUsages).valueOf().asInstanceOf[CryptoKey]
  }

  private def encryptLicensee(licensee: String, cryptoKey: CryptoKey): BufferSource = {
    val blob = new Blob(js.Array(licensee), BlobPropertyBag("application/json"))
    encrypt(cryptoKeyAlgo, cryptoKey, blob.valueOf()).valueOf().asInstanceOf[BufferSource]
  }

  private def decryptLicensee(licensee: BufferSource, cryptoKey: CryptoKey): String = {
    decrypt(cryptoKeyAlgo, cryptoKey, licensee).valueOf().asInstanceOf[String]
  }

  private def cacheLicensee(): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readonly").objectStore(licenseeStore)
    val dbRequest = store.get(licenseeKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheLicensee.onerror", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      if (!js.isUndefined(dbRequest.result)) {
        val licenseeRecord = dbRequest.result.asInstanceOf[LicenseeRecord]
        val key = licenseeRecord.key
        val cryptoKey = licenseeRecord.cryptoKey
        val encryptedLicensee = licenseeRecord.encryptedLicensee
        val decryptedLicensee = decryptLicensee(encryptedLicensee, cryptoKey)
        licenseeCache = Some(read(decryptedLicensee))
        console.log(s"cacheLicensee.onsuccess : key = $key  keys = $cryptoKey  encrypted = $encryptedLicensee", event)
      } else console.log("cacheLicensee: no Licensee in db", event)
    }
  }

  def getLicensee: Option[Licensee] = {
    if (licenseeCache.isEmpty) cacheLicensee()
    licenseeCache
  }

  def putLicensee(licensee: Licensee): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readwrite").objectStore(licenseeStore)
    val cryptoKey = generateCryptoKey()
    val encryptedLicensee = encryptLicensee(write(licensee), cryptoKey)
    val licenseeRecord = LicenseeRecord(key = licenseeKey, cryptoKey = cryptoKey, encryptedLicensee = encryptedLicensee)
    store.put(licenseeRecord, licenseeKey)
  }
}