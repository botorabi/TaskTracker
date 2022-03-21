/*
 * Copyright (c) 2020-2022 by Botorabi. All rights reserved.
 * https://github.com/botorabi/TaskTracker
 *
 * License: MIT License (MIT), read the LICENSE text in
 *          main directory for more details.
 */
package net.vrfun.tasktracker.report.docgen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class ReportGeneratorFactoryTest {

    @BeforeEach
    public void setup() {
    }

    @Test
    public void buildGenerator() {
        assertThatThrownBy(() -> ReportGeneratorFactory.build(ReportFormat.Unknown)).isInstanceOf(IllegalArgumentException.class);
        assertThat(ReportGeneratorFactory.build(ReportFormat.PDF)).isInstanceOf(ReportGeneratorPDF.class);
        assertThat(ReportGeneratorFactory.build(ReportFormat.PlainText)).isInstanceOf(ReportGeneratorPlainText.class);
    }
}

