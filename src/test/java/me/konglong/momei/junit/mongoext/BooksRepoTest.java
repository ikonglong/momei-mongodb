/*
 * Copyright (C) 2014, BeautySight Inc. All rights reserved.
 */

package me.konglong.momei.junit.mongoext;

import com.mongodb.diagnostics.logging.Logger;
import com.mongodb.diagnostics.logging.Loggers;
import me.konglong.momei.TestData;
import org.junit.Test;

/**
 * @author chenlong
 * @since 1.0
 */
@TestData
public class BooksRepoTest extends MongoAwareBaseTest {

    private static final Logger logger = Loggers.getLogger("BooksRepoTest");

    @Test
    @TestData
    public void findBooks() {
        logger.info("Test find logic!");
    }

}
