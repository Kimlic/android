package com.kimlic.profile

import android.arch.lifecycle.LiveData
import com.kimlic.db.KimlicDB
import com.kimlic.db.dao.*
import com.kimlic.db.entity.*

class ProfileRepository private constructor() {

    private object Holder {
        val INSTANCE = ProfileRepository()
    }

    // Companion

    companion object {
        val instance: ProfileRepository by lazy { Holder.INSTANCE }
    }

    // Variables

    private var db: KimlicDB

    private var userDao: UserDao
    private var contactDao: ContactDao
    private var documentDao: DocumentDao
    private var addressDao: AddressDao
    private var photoDao: PhotoDao


    init {
        db = KimlicDB.getInstance()!!

        userDao = db.userDao()
        contactDao = db.contactDao()
        documentDao = db.documentDao()
        addressDao = db.addressDao()
        photoDao = db.photoDao()
    }

    // Public

    // User

    fun insertUser(user: User) = userDao.insert(user)

    fun getUser(accountAddress: String) = userDao.select(accountAddress)

    fun deleteUser(accountAddress: String) = userDao.delete(accountAddress)

    fun userLive(accountAddress: String): LiveData<User> = userDao.selectLive(accountAddress)

    fun addUserPhoto(accountAddress: String, fileName: String) {
        val user = db.userDao().select(accountAddress = accountAddress)
        user.portraitFile = fileName
        db.userDao().update(user = user)
    }

    fun addUserPhotoPreview(accountAddress: String, fileName: String) {
        val user = userDao.select(accountAddress = accountAddress)
        user.portraitPreviewFile = fileName
        userDao.update(user)
    }

    fun addUserName(accountAddress: String, firstName: String, lastName: String) {
        val user = db.userDao().select(accountAddress = accountAddress)
        user.firstName = firstName
        user.lastName = lastName
        db.userDao().update(user)

    }

    // Address

    fun addUserAddress(accountAddress: String, address: Address): Long {
        val user = db.userDao().select(accountAddress)
        val userId = user.id
        address.userId = userId
        return db.addressDao().insert(address = address)
    }

    fun addressUpdate(address: Address) = addressDao.update(address)

    fun addressLive(accountAddress: String) = addressDao.selectLive(accountAddress = accountAddress)

    fun address(accountAddress: String) = addressDao.select(accountAddress = accountAddress)

    fun addressDelete(addressId: Long) = addressDao.delete(addressId)

    // Contacts

    fun userContactsLive(accountAddress: String): LiveData<List<Contact>> = contactDao.selectLive(accountAddress = accountAddress)

    fun contactAdd(accountAddress: String, contact: Contact) {
        val user = db.userDao().select(accountAddress)
        val userId = user.id
        contact.userId = userId
        db.contactDao().insert(contact)

    }

    fun contactDelete(accountAddress: String, contactType: String) = contactDao.delete(accountAddress, contactType)

    // Document

    fun documentAdd(accountAddress: String, document: Document): Long {
        val user = db.userDao().select(accountAddress = accountAddress)
        val userId = user.id
        document.userId = userId
        return db.documentDao().insert(document)
    }

    fun documentsLive(accountAddress: String) = documentDao.selectLive(accountAddress = accountAddress)

    fun documentDelete(documentId: Long) = documentDao.delete(id = documentId)

    // Photo

    fun userDocumentPhotos(accountAddress: String, documentType: String) = photoDao.selectUserPhotosByDocument(accountAddress = accountAddress, documentType = documentType)

    fun userAddressPhoto(accountAddress: String) = photoDao.selectUserAddresPhoto(accountAddress = accountAddress)

    fun addDocumentPhoto(vararg photos: Photo) = photoDao.insert(photos = photos.asList())
}