package tripletail

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.crypto.CryptoKey
import org.scalajs.dom.raw.{IDBDatabase, IDBVersionChangeEvent}

object LicenseeStore {
  private val dbName = "db"
  private val dbVersion = 1

  private val cryptoStore = "crypto"
  private val cryptoStoreKey = "key"
  private var cryptoKeyCache: Option[CryptoKey] = None

  private val licenseeStore = "licensee"
  private val licenseeStoreKey = "key"
  private var licenseeCache: Option[Licensee] = None

  private val openDBRequest = window.indexedDB.open(dbName, dbVersion)

  openDBRequest.onupgradeneeded = (event: IDBVersionChangeEvent) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    db.createObjectStore(cryptoStore, s"{ keyPath: $cryptoStoreKey }")
    db.createObjectStore(licenseeStore, s"{ keyPath: $licenseeStoreKey }")
    // Create CryptoKey and put in store.
    console.log("openDBRequest.onupgradeneeded", event)
  }

  openDBRequest.onerror = (event: ErrorEvent) => console.error("openDBRequest.onerror", event)

  openDBRequest.onsuccess = (event: dom.Event) => {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    cacheCryptoKey(db)
    cacheLicensee(db)
    console.log("openDBRequest.onsuccess", event)
  }

  private def cacheCryptoKey(db: IDBDatabase): Unit = {
    val store = db.transaction(cryptoStore, "readonly").objectStore(cryptoStore)
    val dbRequest = store.get(cryptoStoreKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheCryptoKey", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      val cryptoKey = dbRequest.result.asInstanceOf[CryptoKey]
      cryptoKeyCache = Some(cryptoKey)
      console.log("cacheCryptoKey: db request => on success", event)
    }
  }

  private def cacheLicensee(db: IDBDatabase): Unit = {
    val db = openDBRequest.result.asInstanceOf[IDBDatabase]
    val store = db.transaction(licenseeStore, "readonly").objectStore(licenseeStore)
    val dbRequest = store.get(licenseeStoreKey)
    dbRequest.onerror = (event: ErrorEvent) => console.error("cacheLicensee", event)
    dbRequest.onsuccess = (event: dom.Event) => {
      val licensee = dbRequest.result.asInstanceOf[Licensee]
      licenseeCache = Some(licensee)
      console.log("cacheLicensee: db request => on success", event)
    }
  }
}