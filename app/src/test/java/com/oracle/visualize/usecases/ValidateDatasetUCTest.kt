package com.oracle.visualize.usecases

import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import junit.framework.TestCase.assertTrue
import org.junit.Test

/**
 * Unit tests for [ValidateDatasetUseCase].
 *
 *   Has no repository dependency.
 */

class ValidateDatasetUCTest {
    private val validateDataset = ValidateDatasetUseCase()

    //Extension validation

    @Test
    fun `csv file with valid size returns success`() {
        // given
        val fileName = "data.csv"
        val validSize = 1 * 1024 * 1024L // 1 MB

        // when
        val result = validateDataset(fileName, validSize)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `xlsx file with valid size returns success`() {
        // given
        val fileName = "data.xlsx"
        val validSize = 1 * 1024 * 1024L // 1 MB

        // when
        val result = validateDataset(fileName, validSize)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `unsupported extension returns IllegalArgumentException`() {
        // given
        val fileName = "data.pdf"
        val validSize = 1 * 1024 * 1024L // 1 MB

        // when
        val result = validateDataset(fileName, validSize)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `no extension returns IllegalArgumentException`() {
        // given
        val fileName = "datafile"
        val validSize = 1 * 1024 * 1024L

        // when
        val result = validateDataset(fileName, validSize)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `uppercase extension is accepted`() {
        // given - extension is lowercased internally via lowercase(Locale.ROOT)
        val fileName = "data.CSV"
        val validSize = 1 * 1024 * 1024L

        // when
        val result = validateDataset(fileName, validSize)

        // then
        assertTrue(result.isSuccess)
    }

    //Size Validation

    @Test
    fun `csv file at exactly 100 MB returns success`() {
        // given
        val fileName = "data.csv"
        val exactLimit = 100 * 1024 * 1024L // exactly 100 MB

        // when
        val result = validateDataset(fileName, exactLimit)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `xlsx file at exactly 100 MB returns success`() {
        // given
        val fileName = "data.xlsx"
        val exactLimit = 100 * 1024 * 1024L // exactly 100 MB

        // when
        val result = validateDataset(fileName, exactLimit)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `file one byte over 100 MB returns IllegalArgumentException`() {
        // given
        val fileName = "data.csv"
        val oneByteOver = (100 * 1024 * 1024L) + 1

        // when
        val result = validateDataset(fileName, oneByteOver)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `file 100 bytes over 100 MB returns IllegalArgumentException`() {
        // given
        val fileName = "data.csv"
        val oneByteOver = (100 * 1024 * 1024L) + 100

        // when
        val result = validateDataset(fileName, oneByteOver)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `empty file returns failure`() {
        // given
        val fileName = "data.csv"
        val emptyFile = 0L

        // when
        val result = validateDataset(fileName, emptyFile)

        // then
        assertTrue(result.isFailure)
    }

}