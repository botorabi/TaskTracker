/*
 * Copyright (c) 2020 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.task;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class TagTest {

    @Before
    public void setup() {
    }

    @Test
    public void defaultConstruction() {
        Tag tag = new Tag();

        assertThat(tag.getId()).isNull();
        assertThat(tag.getName()).isNull();
    }

    @Test
    public void constructionWithName() {
        final String name = "TestTag";

        Tag tag = new Tag(name);

        assertThat(tag.getId()).isNull();
        assertThat(tag.getName()).isEqualTo(name);
    }

    @Test
    public void getterSetter() {
        Tag tag = new Tag();

        final long id = 42;
        final String name = "TestTag";

        tag.setId(id);
        tag.setName(name);

        assertThat(tag.getId()).isEqualTo(id);
        assertThat(tag.getName()).isEqualTo(name);
    }
}
