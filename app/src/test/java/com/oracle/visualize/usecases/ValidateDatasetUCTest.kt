package com.oracle.visualize.usecases

import com.oracle.visualize.domain.usecases.ValidateDatasetUseCase
import com.oracle.visualize.fixtures.DatasetFixtures
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
    fun csvFile_withValidSize_returnsSuccess() {
        // given
        val validSize = DatasetFixtures.VALID_SIZE

        // when
        val result = validateDataset(DatasetFixtures.VALID_CSV_NAME, validSize)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun xlsxFile_withValidSize_returnsSuccess() {
        // given
        val validSize = DatasetFixtures.VALID_SIZE

        // when
        val result = validateDataset(DatasetFixtures.VALID_XLSX_NAME, validSize)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun unsupportedExtension_returnsIllegalArgumentException() {
        // given
        val unsupportedFile = "data.pdf"

        // when
        val result = validateDataset(unsupportedFile, DatasetFixtures.VALID_SIZE)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun noExtension_returnsIllegalArgumentException() {
        // given
        val noExtensionFile = "datafile"

        // when
        val result = validateDataset(noExtensionFile, DatasetFixtures.VALID_SIZE)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun blankFileName_returnsIllegalArgumentException() {
        // given
        val blankFileName = ""

        // when
        val result = validateDataset(blankFileName, DatasetFixtures.VALID_SIZE)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun uppercaseExtension_isAccepted_returnsSuccess() {
        // given - extension is lowercased internally via lowercase(Locale.ROOT)
        val uppercaseExtension = "data.CSV"

        // when
        val result = validateDataset(uppercaseExtension, DatasetFixtures.VALID_SIZE)

        // then
        assertTrue(result.isSuccess)
    }

    //Size Validation

    @Test
    fun csvFile_atExactly100MB_returnsSuccess() {
        // given - boundary size: exactly at the limit
        val result = validateDataset(DatasetFixtures.VALID_CSV_NAME, DatasetFixtures.MAX_SIZE)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun xlsxFile_atExactly100MB_returnsSuccess() {
        // given - size: exactly at the limit
        val result = validateDataset(DatasetFixtures.VALID_XLSX_NAME, DatasetFixtures.MAX_SIZE)

        // then
        assertTrue(result.isSuccess)
    }

    @Test
    fun file_oneByteOver100MB_returnsIllegalArgumentException() {
        // given
        val oneByteOver = DatasetFixtures.MAX_SIZE + 1

        // when
        val result = validateDataset(DatasetFixtures.VALID_CSV_NAME, oneByteOver)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun file_100BytesOver100MB_returnsIllegalArgumentException() {
        // given
        val hundredBytesOver = DatasetFixtures.MAX_SIZE + 100

        // when
        val result = validateDataset(DatasetFixtures.VALID_CSV_NAME, hundredBytesOver)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun emptyFile_returnsIllegalArgumentException() {
        // given
        val emptyFile = 0L

        // when
        val result = validateDataset(DatasetFixtures.VALID_CSV_NAME, emptyFile)

        // then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

}