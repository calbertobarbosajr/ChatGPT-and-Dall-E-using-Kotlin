package com.calberto.chatgptkotlin

import com.stfalcon.chatkit.commons.models.IUser

class User ( var idm : String, var namem : String, var avatarm : String ) : IUser {
    override fun getId(): String {
        return idm
    }

    override fun getName(): String {
        return namem
    }

    override fun getAvatar(): String {
        return avatarm
    }
}