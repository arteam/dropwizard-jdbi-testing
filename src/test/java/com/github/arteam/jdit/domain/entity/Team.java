package com.github.arteam.jdit.domain.entity;

import com.google.common.base.MoreObjects;
import java.util.Optional;

/**
 * Date: 2/7/15
 * Time: 4:58 PM
 *
 * @author Artem Prigoda
 */
public class Team {

    public final Optional<Long> id;
    public final String name;
    public final Division division;

    public Team(Optional<Long> id, String name, Division division) {
        this.id = id;
        this.name = name;
        this.division = division;
    }

    public Team(String name, Division division) {
        this.name = name;
        this.division = division;
        this.id = Optional.empty();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .add("division", division)
                .toString();
    }
}
