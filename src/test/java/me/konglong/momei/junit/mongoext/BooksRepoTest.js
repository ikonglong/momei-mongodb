
// Prepare test data for test method 'findBooks'
prepare4_findBooks = function() {
    for (var i = 1; i <= 100; i++) {
        db.books.insert({ _id: ObjectId(), name: ("Thinking in Java "+i) });
    }
}

// Clean up test data for test method 'findBooks'
cleanup4_findBooks = function() {
    db.books.remove({ name: { $regex: /Thinking in Java/ } });
}