package cmsc436.changemyview.model

class UserEntity {
    var id: String? = null
    var email: String? = null
    var userName: String? = null


    constructor(id: String, email: String, userName: String){
        this.id = id
        this.email = email
        this.userName = userName
    }


}