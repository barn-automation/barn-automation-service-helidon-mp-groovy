package codes.recursive.barn.automation.model

class BarnEvent {
    def id
    String type
    String data
    Date capturedAt

    BarnEvent() {

    }

    BarnEvent(String type, String data, Date capturedAt) {
        this.type = type
        this.data = data
        this.capturedAt = capturedAt
    }

    BarnEvent(def id, String type, String data, Date capturedAt) {
        this.id = id
        this.type = type
        this.data = data
        this.capturedAt = capturedAt
    }
}

