package com.github.arteam.jdit;

import com.github.arteam.jdit.annotations.DataSet;
import com.github.arteam.jdit.annotations.TestedSqlObject;
import com.github.arteam.jdit.domain.PlayerSqlObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DBIRunner.class)
public class TestDataSet {

    @TestedSqlObject
    PlayerSqlObject playerDao;

    @Test
    @DataSet("playerDao/getInitials.sql")
    public void testGetInitials() {
        assertThat(playerDao.getLastNames()).containsOnly("Tarasenko");
    }
}