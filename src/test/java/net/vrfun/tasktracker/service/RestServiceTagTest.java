/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.service;

import net.vrfun.tasktracker.task.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;



public class RestServiceTagTest {

    @Mock
    private Tags tags;

    private RestServiceTag restServiceTag;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        restServiceTag = new RestServiceTag(tags);
    }

    @Test
    public void getOrCreate() {
        Tag tag = new Tag("MyTag");
        tag.setId(42L);

        when(tags.getOrCreate(anyString())).thenReturn(tag);

        ResponseEntity<Long> response = restServiceTag.getOrCreate(anyString());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(42L);
    }

    @Test
    public void deleteExistingTag() {
        try {
            ResponseEntity<Void> response = restServiceTag.delete(anyString());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void deleteNonExistingTag() {
        doThrow(new IllegalArgumentException()).when(tags).delete(anyString());

        try {
            ResponseEntity<Void> response = restServiceTag.delete(anyString());
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void getAllTags() {
        List<Tag> allTags = new ArrayList<>();
        allTags.add(new Tag("MyTag1"));
        allTags.add(new Tag("MyTag2"));

        doReturn(allTags).when(tags).getAll();

        ResponseEntity<List<Tag>> response = restServiceTag.getAllTags();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(allTags.size());
    }
}
