package cmsc436.changemyview.model

class TopicItem {

    var title: String? = null
    var id: String? = null
    var participation: String? = null

    constructor(title: String?, id: String?, participation: String?){
        this.title = title
        this.id = id
        this.participation = participation
    }
}