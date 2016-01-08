/*
 * comments-section-1 for testing
 */

// Prepare test data for test method 'findBooks'
prepare4_findBooks = function() {

    /*
      comments-section-2 for testing
    */

    for (var i = 1; i <= 100; i++) {

        /* comments-section-3 for testing */

        db.books.insert({ _id: ObjectId(), name: ("Thinking in Java "+i) });
    }
};

/*
 * comments-section-4 for testing
 */

// Clean up test data for test method 'findBooks'
cleanup4_findBooks = function() {

    // Remove the docs which's names start with 'Thinking in Java'

    db.books.remove({
        /* comments-section-5 for testing */
        name: {

            /*
             comments-section-6 for testing
             */

            $regex: /Thinking in Java/
        }
    });

    /*
     * comments-section-7 for testing
     */
};