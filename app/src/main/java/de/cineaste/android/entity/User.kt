package de.cineaste.android.entity


//todo use data class
class User {

    var userName: String? = null

    constructor()

    constructor(userName: String) {
        this.userName = userName
    }
}
