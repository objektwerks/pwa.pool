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
  val keys: CryptoKey = js.native
  val encrypted: BufferSource = js.native
}

object LicenseeRecord {
  def apply(key: Int, keys: CryptoKey, encrypted: BufferSource): LicenseeRecord =
    js.Dynamic.literal(key = key, keys = keys, encrypted = encrypted).asInstanceOf[LicenseeRecord]
}

object LicenseeStore {
  private val dbName = "db"
  private val dbVersion = 1

  private val cryptoKeyAlgoId = "RSA-OAEP"

  private val licenseeStore = "licensee"
  private val licenseeStoreKeyPath = "{ keyPath: 'key' }"
  private val licenseeStoreKey = 1
  private var licenseeCache: Option[Licensee] = None

  private val openDBRequest = window.indexedDB.open(dbName, dbVersion)

  openDBRequest.onupgradeneeded = (event: IDBVersionChangeEvent) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    db.createObjectStore(licenseeStore, licenseeStoreKeyPath)
    console.log("openDBRequest.onupgradeneeded", event)
  }

  openDBRequest.onerror = (event: ErrorEvent) => console.error("openDBRequest.onerror", event)

  openDBRequest.onsuccess = (event: dom.Event) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    cacheLicensee(db)
    console.log("openDBRequest.onsuccess", event)
  }

  private def generateCryptoKey(): CryptoKey = {
    val extractable = false
    val keyUsages = js.Array(KeyUsage.encrypt, KeyUsage.decrypt)
    generateKey(cryptoKeyAlgoId, extractable, keyUsages).valueOf().asInstanceOf[CryptoKey]
  }

  private def encryptLicensee(licensee: String, cryptoKey: CryptoKey): BufferSource = {
    val blob = new Blob(js.Array(licensee), BlobPropertyBag("application/json"))
    encrypt(cryptoKeyAlgoId, cryptoKey, blob.valueOf()).valueOf().asInstanceOf[BufferSource]
  }

  private def decryptLicensee(licensee: BufferSource, cryptoKey: CryptoKey): String = {
    decrypt(cryptoKeyAlgoId, cryptoKey, licensee).valueOf().asInstanceOf[String]
  }

  private def cacheLicensee(db: IDBDatabase): Unit = {
    val store = db.transaction(licenseeStore, "readonly").objectStore(licenseeStore)
    val dbRequest = store.get(licenseeStoreKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheLicensee.onerror", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      if (!js.isUndefined(dbRequest.result)) {
        val licenseeRecord = dbRequest.result.asInstanceOf[LicenseeRecord]
        val key = licenseeRecord.key
        val keys = licenseeRecord.keys
        val encrypted = licenseeRecord.encrypted
        val decrypted = decryptLicensee(encrypted, keys)
        licenseeCache = Some(read(decrypted))
        console.log(s"cacheLicensee.onsuccess : key = $key  keys = $keys  encrypted = $encrypted", event)
      } else console.log("cacheLicensee: undefined result", event)
    }
  }

  def getLicensee: Option[Licensee] = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    if (licenseeCache.isEmpty) cacheLicensee(db)
    licenseeCache
  }

  def putLicensee(licensee: Licensee): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readwrite").objectStore(licenseeStore)
    val cryptoKey = generateCryptoKey()
    val encryptedLicensee = encryptLicensee(write(licensee), cryptoKey)
    val data = LicenseeRecord(key = licenseeStoreKey, keys = cryptoKey, encrypted = encryptedLicensee)
    store.put(data, licenseeStoreKey)
  }
}