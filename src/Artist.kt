

class Artist (name: String, link: String, genre: String, id: Int) {
     var fullName: String = name
    get() {
        return field
    }
    set(newValue) {
        field = newValue
    }
    var artstationLink: String = link
        get() {
            return field
        }
        set(newValue) {
            field = newValue
        }
     var workGenre: String = genre
        get() {
            return field
        }
        set(newValue) {
            field = newValue
        }
     var id: Int = id
        get() {
            return field
        }
        set(newValue) {
            field = newValue
        }


}