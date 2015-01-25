package com.github.arteam.dropwizard.testing.jdbi;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.HandleCallback;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import org.skife.jdbi.v2.util.StringMapper;

import javax.management.monitor.StringMonitor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Date: 1/25/15
 * Time: 9:50 PM
 *
 * @author Artem Prigoda
 */
@RunWith(DBIRunner.class)
public class HandleTest {

    @DBIHandle
    Handle handle;

    private final String helloDBI = "Hello DBI!";

    @Test
    public void testHelloWorld() {
        System.out.println("Hello world!");
    }

    @Test
    public void testInsert() {
        System.out.println(helloDBI);
        int amount = handle.insert("insert into players(first_name, last_name, birth_date, weight, height)" +
                " values ('Vladimir','Tarasenko', '1991-08-05 00:00:00', 84, 99)");
        Assert.assertEquals(amount, 1);

        String initials = handle.createQuery("select first_name || ' ' || last_name from players")
                .map(StringMapper.FIRST)
                .first();
        System.out.println(initials);
        Assert.assertEquals(initials, "Vladimir Tarasenko");
    }


    @Test
    public void testGetInitials() {
        List<String> lastNames = handle.createQuery("select last_name from players")
                .map(StringMapper.FIRST)
                .list();
        System.out.println(lastNames);
        Assert.assertTrue(lastNames.isEmpty());
    }
}