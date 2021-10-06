package com.mats.giveawayapp.models


class User2
{
    internal var uid: String = ""
    private var username: String = ""
    private var email: String = ""
    private var firstname: String = ""
    private var lastname: String = ""
    private var profileImage: String = ""
    private var mobile: Long = 0
    private var gender: String = ""
    private var status: String = ""
    private var search: String = ""
    private var facebook: String = ""
    private var instagram: String = ""
    private var website: String = ""
    private var profileCompleted: Int = 0


    constructor()


    constructor(
        uid: String,
        username: String,
        email: String = "",
        firstname: String = "",
        lastname: String = "",
        profileImage: String,
        mobile: Long = 0,
        gender: String = "",
        status: String,
        search: String,
        facebook: String,
        instagram: String,
        website: String,
        profileCompleted: Int = 0
    ) {
        this.uid = uid
        this.username = username
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.profileImage = profileImage
        this.mobile = mobile
        this.gender = gender
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
        this.website = website
        this.profileCompleted = profileCompleted
    }

    fun getUID(): String{
        return uid
    }

    fun setUID(uid: String){
        this.uid = uid
    }

    fun getUsername(): String{
        return username
    }

    fun setUsername(username: String){
        this.username = username
    }

    fun getEmail(): String{
        return email
    }

    fun setEmail(email: String){
        this.email = email
    }

    fun getFirstname(): String{
        return firstname
    }

    fun setFirstname(firstname: String) {
        this.firstname = firstname
    }

    fun getLastname(): String{
        return lastname
    }

    fun setLastname(lastname: String) {
        this.lastname = lastname
    }

    fun getProfileImage(): String{
        return profileImage
    }

    fun setProfileImage(profileImage: String){
        this.profileImage = profileImage
    }

    fun getMobile(): Long{
        return mobile
    }

    fun setMobile(mobile: Long) {
        this.mobile = mobile
    }

    fun getGender(): String{
        return gender
    }

    fun setGender(gender: String){
        this.gender = gender
    }

    fun getStatus(): String{
        return status
    }

    fun setStatus(status: String){
        this.status = status
    }

    fun getSearch(): String{
        return search
    }

    fun setSearch(search: String){
        this.search = search
    }

    fun getFacebook(): String{
        return facebook
    }

    fun setFacebook(facebook: String){
        this.facebook = facebook
    }

    fun getInstagram(): String{
        return instagram
    }

    fun setInstagram(instagram: String){
        this.instagram = instagram
    }

    fun getWebsite(): String{
        return website
    }

    fun setWebsite(website: String){
        this.website = website
    }

    fun getProfileCompleted(): Int{
        return profileCompleted
    }

    fun setProfileCompleted(profileCompleted: Int) {
        this.profileCompleted = profileCompleted
    }
}