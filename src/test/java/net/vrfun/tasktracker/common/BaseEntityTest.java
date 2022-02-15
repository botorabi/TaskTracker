/*
 * Copyright (c) 2020-2021 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.common;




import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class BaseEntityTest {

    public class MyEntity extends BaseEntity {

        Long id;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }

    private MyEntity myEntity;

    @BeforeEach
    public void setup() {
        myEntity = new MyEntity();
    }

    @Test
    public void setGetId() {
        myEntity.setId(42L);

        assertThat(myEntity.getId()).isEqualTo(42L);
    }

    @Test
    public void hash() {
        assertThat(myEntity.hashCode()).isEqualTo(0);

        myEntity.setId(42L);

        assertThat(myEntity.hashCode()).isNotEqualTo(0);
    }

    @Test
    public void equality() {
        MyEntity myEntity2 = new MyEntity();
        myEntity2.setId(42L);
        myEntity.setId(42L);

        assertThat(myEntity).isEqualTo(myEntity2);
    }

    @Test
    public void nonEqualityWithNull() {
        assertThat(myEntity.equals(null)).isFalse();
    }

    @Test
    public void nonEqualityOnNoID() {
        assertThat(myEntity.equals(new MyEntity())).isFalse();
    }

    @Test
    public void nonEqualityOnDifferentTypes() {
        myEntity.setId(42L);
        assertThat(myEntity.equals(new Object())).isFalse();
    }

    @Test
    public void nonEqualityOnDifferentIDs() {
        MyEntity myEntity2 = new MyEntity();
        myEntity2.setId(43L);
        myEntity.setId(42L);

        assertThat(myEntity).isNotEqualTo(myEntity2);

        MyEntity myEntity3 = new MyEntity();

        assertThat(myEntity).isNotEqualTo(myEntity3);
    }

    @Test
    public void entityToString() {
        myEntity.setId(42L);

        assertThat(myEntity.toString()).isNotEmpty();
    }
}
